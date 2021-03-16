// Copyright 2020 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package kogitobuild

import (
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	buildv1 "github.com/openshift/api/build/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"strconv"
	"strings"
)

const (
	// LabelKeyBuildType identifies the instance build type
	LabelKeyBuildType = "buildType"

	kindImageStreamTag = "ImageStreamTag"
	tagLatest          = "latest"

	destinationDir   = "."
	runnerSourcePath = operator.KogitoHomeDir + "/bin"

	nativeBuildEnvVarKey        = "NATIVE"
	builderLimitCPUEnvVarKey    = "LIMIT_CPU"
	builderLimitMemoryEnvVarKey = "LIMIT_MEMORY"
	mavenMirrorURLEnvVar        = "MAVEN_MIRROR_URL"
	mavenGroupIDEnvVar          = "PROJECT_GROUP_ID"
	mavenArtifactIDEnvVar       = "PROJECT_ARTIFACT_ID"
	mavenArtifactVersionEnvVar  = "PROJECT_VERSION"
	mavenDownloadOutputEnvVar   = "MAVEN_DOWNLOAD_OUTPUT"
	binaryBuildEnvVar           = "BINARY_BUILD"
)

// DecoratorHandler ...
type DecoratorHandler interface {
	decoratorForRemoteSourceBuilder() decorator
	decoratorForLocalSourceBuilder() decorator
	decoratorForSourceBuilder() decorator
	decoratorForBinaryRuntimeBuilder() decorator
	decoratorForSourceRuntimeBuilder() decorator
	decoratorForRuntimeBuilder() decorator
}

type decoratorHandler struct {
	*operator.Context
}

// NewDecoratorHandler ...
func NewDecoratorHandler(context *operator.Context) DecoratorHandler {
	return &decoratorHandler{
		context,
	}
}

type decorator func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig)

// decoratorForRemoteSourceBuilder decorates the builder BuildConfig with the attributes to support Remote Source build type
func (b *decoratorHandler) decoratorForRemoteSourceBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		bc.Spec.Source.Type = buildv1.BuildSourceGit
		// Remove the trailing slash, as it will be removed by OpenShift
		bc.Spec.Source.ContextDir = strings.TrimSuffix(build.GetSpec().GetGitSource().GetContextDir(), "/")
		bc.Spec.Source.Git = &buildv1.GitBuildSource{
			URI: build.GetSpec().GetGitSource().GetURI(),
			Ref: build.GetSpec().GetGitSource().GetReference(),
		}
		for _, hook := range build.GetSpec().GetWebHooks() {
			var triggerPolicy buildv1.BuildTriggerPolicy
			trigger := &buildv1.WebHookTrigger{SecretReference: &buildv1.SecretLocalReference{Name: hook.GetSecret()}}
			if hook.GetType() == api.GitHubWebHook {
				triggerPolicy = buildv1.BuildTriggerPolicy{GitHubWebHook: trigger, Type: buildv1.GitHubWebHookBuildTriggerType}
			} else {
				trigger.AllowEnv = true
				triggerPolicy = buildv1.BuildTriggerPolicy{GenericWebHook: trigger, Type: buildv1.GenericWebHookBuildTriggerType}
			}
			bc.Spec.Triggers = append(bc.Spec.Triggers, triggerPolicy)
		}

	}
}

// decoratorForLocalSourceBuilder decorates the original BuildConfig to support Local Source build type
func (b *decoratorHandler) decoratorForLocalSourceBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		var envs []corev1.EnvVar
		if len(build.GetSpec().GetArtifact().GetGroupID()) > 0 {
			b.Log.Debug("Setting final generated", "Artifact group ID", build.GetSpec().GetArtifact().GetGroupID())
			envs = framework.EnvOverride(envs, corev1.EnvVar{Name: mavenGroupIDEnvVar, Value: build.GetSpec().GetArtifact().GetGroupID()})
		}
		if len(build.GetSpec().GetArtifact().GetArtifactID()) > 0 {
			b.Log.Debug("Setting final", "Generated artifact id", build.GetSpec().GetArtifact().GetArtifactID())
			envs = framework.EnvOverride(envs, corev1.EnvVar{Name: mavenArtifactIDEnvVar, Value: build.GetSpec().GetArtifact().GetArtifactID()})
		}
		if len(build.GetSpec().GetArtifact().GetVersion()) > 0 {
			b.Log.Debug("Setting final generated", "Artifact version", build.GetSpec().GetArtifact().GetVersion())
			envs = framework.EnvOverride(envs, corev1.EnvVar{Name: mavenArtifactVersionEnvVar, Value: build.GetSpec().GetArtifact().GetVersion()})
		}
		bc.Spec.Strategy.SourceStrategy.Env = append(bc.Spec.Strategy.SourceStrategy.Env, envs...)

		bc.Spec.Source.Type = buildv1.BuildSourceBinary
		// The comparator hits reconciliation if this are not set to empty values. TODO: fix on the operator-utils project
		bc.Spec.Source.Binary = &buildv1.BinaryBuildSource{AsFile: ""}
		// set it to an empty state, in this case we don't want OCP triggering the build for us.
		bc.Spec.Triggers = []buildv1.BuildTriggerPolicy{}
	}
}

// decoratorForSourceBuilder decorates the original BuildConfig to give basic support for Local and Remote Source builds
// `decoratorForLocalSourceBuilder` and `decoratorForRemoteSourceBuilder` know the details for each use case,
// add one of them to the `newBuildConfig` constructor to fulfill the desired use case
func (b *decoratorHandler) decoratorForSourceBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		bc.Name = GetBuildBuilderName(build)
		baseImage := corev1.ObjectReference{
			Kind:      kindImageStreamTag,
			Namespace: build.GetNamespace(),
			Name:      resolveKogitoImageStreamTagName(build, true),
		}
		// we output to a image stream with the same name of the build.
		// this image stream will be the input for the base BC.
		bc.Spec.Output.To = getBuilderImageStreamOutputTo(build)
		// whenever a change happens in the image, we will trigger a new build
		bc.Spec.Triggers = []buildv1.BuildTriggerPolicy{
			{Type: buildv1.ImageChangeBuildTriggerType, ImageChange: &buildv1.ImageChangeTrigger{From: &baseImage}},
		}
		// apply the necessary environment variables
		envs := build.GetSpec().GetEnv()
		if build.GetSpec().GetRuntime() == api.QuarkusRuntimeType {
			envs = framework.EnvOverride(envs, corev1.EnvVar{Name: nativeBuildEnvVarKey, Value: strconv.FormatBool(build.GetSpec().IsNative())})
		}
		limitCPU, limitMemory := getBuilderLimitsAsIntString(bc)
		envs = framework.EnvOverride(envs, corev1.EnvVar{Name: builderLimitCPUEnvVarKey, Value: limitCPU})
		envs = framework.EnvOverride(envs, corev1.EnvVar{Name: builderLimitMemoryEnvVarKey, Value: limitMemory})
		if len(build.GetSpec().GetMavenMirrorURL()) > 0 {
			b.Log.Info("Setting maven mirror", "Maven Mirror Url", build.GetSpec().GetMavenMirrorURL())
			envs = framework.EnvOverride(envs, corev1.EnvVar{Name: mavenMirrorURLEnvVar, Value: build.GetSpec().GetMavenMirrorURL()})
		}
		if build.GetSpec().IsEnableMavenDownloadOutput() {
			b.Log.Debug("Enable logging for transfer progress of downloading/uploading maven dependencies")
			envs = framework.EnvOverride(envs,
				corev1.EnvVar{Name: mavenDownloadOutputEnvVar, Value: strconv.FormatBool(build.GetSpec().IsEnableMavenDownloadOutput())})
		}
		incremental := !build.GetSpec().IsDisableIncremental()
		bc.Spec.Strategy = buildv1.BuildStrategy{
			Type: buildv1.SourceBuildStrategyType,
			SourceStrategy: &buildv1.SourceBuildStrategy{
				From:        baseImage,
				Env:         envs,
				Incremental: &incremental,
			},
		}
	}
}

// decoratorForBinaryRuntimeBuilder decorates the original BuildConfig to give support for Binary build type
func (b *decoratorHandler) decoratorForBinaryRuntimeBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		bc.Spec.Source.Type = buildv1.BuildSourceBinary
		bc.Spec.Strategy.SourceStrategy.Env = append(bc.Spec.Strategy.SourceStrategy.Env, corev1.EnvVar{Name: binaryBuildEnvVar, Value: "true"})
		// The comparator hits reconciliation if this are not set to empty values. TODO: fix on the operator-utils project
		bc.Spec.Source.Binary = &buildv1.BinaryBuildSource{AsFile: ""}
		bc.Spec.Triggers = []buildv1.BuildTriggerPolicy{}
	}
}

// decoratorForSourceRuntimeBuilder decorates the original BuildConfig to give support for Runtime builders, which is the second build
// after building the application from source. This BuildConfig is responsible for the final Kogito Service image, which will then be deployed
// with KogitoRuntime
func (b *decoratorHandler) decoratorForSourceRuntimeBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		fromImage := getBuilderImageStreamOutputTo(build)
		bc.Spec.Source.Type = buildv1.BuildSourceImage
		bc.Spec.Source.Images = []buildv1.ImageSource{
			{
				From: *fromImage,
				Paths: []buildv1.ImageSourcePath{
					{
						DestinationDir: destinationDir,
						SourcePath:     runnerSourcePath,
					},
				},
			},
		}
		// triggers once we have a change in the s2i produced image (builder BuildConfig)
		bc.Spec.Triggers = []buildv1.BuildTriggerPolicy{
			{
				Type:        buildv1.ImageChangeBuildTriggerType,
				ImageChange: &buildv1.ImageChangeTrigger{From: fromImage},
			},
		}
	}
}

// decoratorForRuntimeBuilder decorates the original BuildConfig to give very basic support for any Runtime builders, the
// ones responsible to build the final KogitoRuntime image.
// should be used with `decoratorForSourceRuntimeBuilder` or `decoratorForBinaryRuntimeBuilder` to add full support for these use cases.
func (b *decoratorHandler) decoratorForRuntimeBuilder() decorator {
	return func(build api.KogitoBuildInterface, bc *buildv1.BuildConfig) {
		bc.Name = build.GetName()
		baseImage := corev1.ObjectReference{
			Kind:      kindImageStreamTag,
			Namespace: build.GetNamespace(),
			Name:      resolveKogitoImageStreamTagName(build, false),
		}
		// this image stream will be the input for the KogitoRuntime deployment.
		bc.Spec.Output.To = &corev1.ObjectReference{
			Kind: kindImageStreamTag, Name: strings.Join([]string{GetApplicationName(build), tagLatest}, ":"),
		}

		bc.Spec.Strategy = buildv1.BuildStrategy{
			Type: buildv1.SourceBuildStrategyType,
			SourceStrategy: &buildv1.SourceBuildStrategy{
				From: baseImage,
				Env:  build.GetSpec().GetEnv(),
			},
		}
	}
}

// getBuilderImageStreamOutputTo gets the builder ImageStream which is the one that we will output to when building from source
// and the one we will use as input when building the final image.
func getBuilderImageStreamOutputTo(build api.KogitoBuildInterface) *corev1.ObjectReference {
	return &corev1.ObjectReference{
		Kind: kindImageStreamTag, Name: strings.Join([]string{GetBuildBuilderName(build), tagLatest}, ":"),
	}
}

// newBuildConfig creates a new reference for the very basic default OpenShift BuildConfig reference to build Kogito Services.
// Pass the required decorator(s) to create the BuildConfig for a particular use case
func newBuildConfig(build api.KogitoBuildInterface, decorators ...decorator) buildv1.BuildConfig {
	app := build.GetSpec().GetTargetKogitoRuntime()
	if len(app) == 0 {
		app = build.GetName()
	}
	bc := buildv1.BuildConfig{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: build.GetNamespace(),
			Labels: map[string]string{
				LabelKeyBuildType:     string(build.GetSpec().GetType()),
				framework.LabelAppKey: app,
			},
		},
		Spec: buildv1.BuildConfigSpec{
			RunPolicy:  buildv1.BuildRunPolicySerial,
			CommonSpec: buildv1.CommonSpec{Resources: build.GetSpec().GetResources()},
		},
	}
	for _, decorate := range decorators {
		decorate(build, &bc)
	}
	return bc
}

// Copyright 2019 Red Hat, Inc. and/or its affiliates
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
	"context"
	"fmt"
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/manager"
	"github.com/kiegroup/kogito-operator/core/operator"
	"io"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"strings"
	"time"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	buildv1 "github.com/openshift/api/build/v1"

	"k8s.io/apimachinery/pkg/api/errors"
)

const (
	// retry = 5 minutes
	checkBcRetries         = 100
	checkBcRetriesInterval = 3 * time.Second
	// BuildConfigLabelSelector default build selector for buildconfigs
	BuildConfigLabelSelector = "buildconfig"
)

// BuildHandler exposes OpenShift BuildConfig operations
type BuildHandler interface {
	TriggerBuild(bc *buildv1.BuildConfig, triggeredBy string) (bool, error)
	TriggerBuildFromFile(namespace string, r io.Reader, options *buildv1.BinaryBuildRequestOptions, binaryBuild bool, scheme *runtime.Scheme) (*buildv1.Build, error)
	GetBuildsStatus(bc *buildv1.BuildConfig, labelSelector string) (api.BuildsInterface, error)
	GetBuildsStatusByLabel(namespace, labelSelector string) (api.BuildsInterface, error)
}

type buildHandler struct {
	operator.Context
	checkBcRetries         int
	checkBcRetriesInterval time.Duration
	buildHandler           manager.KogitoBuildHandler
}

// NewBuildHandler ...
func NewBuildHandler(context operator.Context, kogitoBuildHandler manager.KogitoBuildHandler) BuildHandler {
	return &buildHandler{
		Context:                context,
		checkBcRetries:         checkBcRetries,
		checkBcRetriesInterval: checkBcRetriesInterval,
		buildHandler:           kogitoBuildHandler,
	}
}

// newBuildHandlerWithBCRetries internal use for unit tests, do not make it public
func newBuildHandlerWithBCRetries(context operator.Context, retries int, retriesInterval time.Duration, kogitoBuildHandler manager.KogitoBuildHandler) BuildHandler {
	return &buildHandler{
		Context:                context,
		checkBcRetries:         retries,
		checkBcRetriesInterval: retriesInterval,
		buildHandler:           kogitoBuildHandler,
	}
}

// TriggerBuild triggers a new build
func (b *buildHandler) TriggerBuild(bc *buildv1.BuildConfig, triggeredBy string) (bool, error) {
	if exists, err := b.checkBuildConfigExists(bc); !exists {
		b.Log.Warn("Impossible to trigger a new build, build Not exists.", "build name", bc.Name)
		return false, err
	}
	// catch panic when FakeClient Build is unable to handle dc properly
	defer func() {
		if err := recover(); err != nil {
			b.Log.Info("Skip build triggering due to a bug on FakeBuild: github.com/openshift/client-go/build/clientset/versioned/typed/build/v1/fake/fake_buildconfig.go:134")
		}
	}()
	buildRequest := newBuildRequest(triggeredBy, bc)
	build, err := b.Client.BuildCli.BuildConfigs(bc.Namespace).Instantiate(context.TODO(), bc.Name, &buildRequest, metav1.CreateOptions{})
	if err != nil {
		return false, err
	}

	b.Log.Info("Build triggered", "build name", build.Name)
	return true, nil
}

// TriggerBuildFromFile will be called by kogito-cli when a build from file is performed.
// When called a new build will be triggered with the request kogito resource or a tgz file.
func (b *buildHandler) TriggerBuildFromFile(namespace string, bodyPost io.Reader, options *buildv1.BinaryBuildRequestOptions, binaryBuild bool, scheme *runtime.Scheme) (*buildv1.Build, error) {
	result := &buildv1.Build{}

	buildName := options.Name
	if !binaryBuild {
		buildName += "-builder"
	}

	// before upload the file, make sure that the build exist
	err := b.waitForBuildConfig(b.checkBcRetries, b.checkBcRetriesInterval, func() error {
		if _, err := b.Client.BuildCli.BuildConfigs(namespace).Get(context.TODO(), buildName, metav1.GetOptions{}); errors.IsNotFound(err) {
			b.Log.Debug("BuildConfig not found.", "name", buildName, "namespace", namespace)
			return err
		} else if err != nil {
			b.Log.Debug("Error while retrieving BuildConfig.", "build name", buildName, "namespace", namespace)
			return err
		}
		return nil
	})
	if err != nil {
		return nil, err
	}

	errPost := b.Client.BuildCli.RESTClient().Post().
		Namespace(namespace).
		Resource("buildconfigs").
		Name(buildName).
		SubResource("instantiatebinary").
		Body(bodyPost).
		VersionedParams(options, runtime.NewParameterCodec(scheme)).
		Do(context.TODO()).
		Into(result)
	return result, errPost
}

// GetBuildsStatusByLabel checks the status of the builds for all builds with the given label
func (b *buildHandler) GetBuildsStatusByLabel(namespace, labelSelector string) (api.BuildsInterface, error) {
	list, err := b.Client.BuildCli.Builds(namespace).List(context.TODO(), metav1.ListOptions{
		LabelSelector: labelSelector,
	})
	if err != nil {
		return nil, err
	}
	status := b.buildHandler.CreateBuild()
	for _, item := range list.Items {
		b.Log.Debug("Checking status of build", "build name", item.Name)
		switch item.Status.Phase {
		case buildv1.BuildPhaseNew:
			status.SetNew(append(status.GetNew(), item.Name))
		case buildv1.BuildPhasePending:
			status.SetPending(append(status.GetPending(), item.Name))
		case buildv1.BuildPhaseRunning:
			status.SetRunning(append(status.GetRunning(), item.Name))
		case buildv1.BuildPhaseComplete:
			status.SetComplete(append(status.GetComplete(), item.Name))
		case buildv1.BuildPhaseFailed:
			status.SetFailed(append(status.GetFailed(), item.Name))
		case buildv1.BuildPhaseError:
			status.SetError(append(status.GetError(), item.Name))
		case buildv1.BuildPhaseCancelled:
			status.SetCancelled(append(status.GetCancelled(), item.Name))
		default:
			status.SetNew(append(status.GetNew(), item.Name))
		}
		b.Log.Debug("Build status", "build name", item.Name, "phase", item.Status.Phase)
	}

	return status, nil
}

// GetBuildsStatus checks the status of the builds for the BuildConfig
func (b *buildHandler) GetBuildsStatus(bc *buildv1.BuildConfig, labelSelector string) (api.BuildsInterface, error) {
	if exists, err := b.checkBuildConfigExists(bc); !exists {
		return nil, err
	}

	list, err := b.Client.BuildCli.Builds(bc.Namespace).List(context.TODO(), metav1.ListOptions{
		LabelSelector: labelSelector,
	})
	if err != nil {
		return nil, err
	}

	status := b.buildHandler.CreateBuild()

	for _, item := range list.Items {
		// it's the build from our buildConfig
		if strings.HasPrefix(item.Name, bc.Name) {
			b.Log.Debug("Checking status of build", "build name", item.Name)
			switch item.Status.Phase {
			case buildv1.BuildPhaseNew:
				status.SetNew(append(status.GetNew(), item.Name))
			case buildv1.BuildPhasePending:
				status.SetPending(append(status.GetPending(), item.Name))
			case buildv1.BuildPhaseRunning:
				status.SetRunning(append(status.GetRunning(), item.Name))
			case buildv1.BuildPhaseComplete:
				status.SetComplete(append(status.GetComplete(), item.Name))
			case buildv1.BuildPhaseFailed:
				status.SetFailed(append(status.GetFailed(), item.Name))
			case buildv1.BuildPhaseError:
				status.SetError(append(status.GetError(), item.Name))
			case buildv1.BuildPhaseCancelled:
				status.SetCancelled(append(status.GetCancelled(), item.Name))
			default:
				status.SetNew(append(status.GetNew(), item.Name))
			}
			b.Log.Debug("Build status", "build name", item.Name, "phase", item.Status.Phase)
		}
	}

	return status, nil
}

func (b *buildHandler) checkBuildConfigExists(bc *buildv1.BuildConfig) (bool, error) {
	if _, err := b.Client.BuildCli.BuildConfigs(bc.Namespace).Get(context.TODO(), bc.Name, metav1.GetOptions{}); err != nil && errors.IsNotFound(err) {
		b.Log.Warn("BuildConfig not found", "namespace", bc.Namespace)
		return false, nil
	} else if err != nil {
		return false, err
	}
	return true, nil
}

// newBuildRequest creates a new BuildRequest for the build
func newBuildRequest(triggeredBy string, bc *buildv1.BuildConfig) buildv1.BuildRequest {
	buildRequest := buildv1.BuildRequest{ObjectMeta: metav1.ObjectMeta{Name: bc.Name}}
	buildRequest.TriggeredBy = []buildv1.BuildTriggerCause{{Message: fmt.Sprintf("Triggered by %s operator", triggeredBy)}}
	setGroupVersionKind(&buildRequest.TypeMeta, infrastructure.KindBuildRequest)
	return buildRequest
}

// setGroupVersionKind sets the group, version and kind for the resource
func setGroupVersionKind(typeMeta *metav1.TypeMeta, kind infrastructure.DefinitionKind) {
	typeMeta.SetGroupVersionKind(schema.GroupVersionKind{
		Group:   kind.GroupVersion.Group,
		Version: kind.GroupVersion.Version,
		Kind:    kind.Name,
	})
}

func (b *buildHandler) waitForBuildConfig(retries int, retryInterval time.Duration, f func() error) (err error) {
	for i := 0; ; i++ {
		err = f()
		if err == nil {
			return
		}

		if i >= (retries - 1) {
			break
		}
		time.Sleep(retryInterval)
		b.Log.Debug("retrying after error", "error", err)
	}
	return fmt.Errorf("after %d attempts, last error: %v", retries, err)
}

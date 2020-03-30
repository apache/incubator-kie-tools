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

package framework

import (
	"fmt"
	"strings"

	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/core/v1"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/gobuffalo/packr/v2"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

const (
	boxExamplesPath       = "../../deploy/examples"
	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
	mavenMirrorURLEnvVar  = "MAVEN_MIRROR_URL"
)

// KogitoAppDeployment defines the elements for the deployment of a kogito application
type KogitoAppDeployment struct {
	AppName     string
	ContextDir  string
	Runtime     v1alpha1.RuntimeType
	Native      bool
	Persistence bool
	Events      bool
	Labels      map[string]string
}

// DeployExample deploy a Kogito example
func DeployExample(namespace string, installerType InstallerType, kogitoAppDeployment KogitoAppDeployment) error {
	GetLogger(namespace).Infof("%s deploy %s example %s with name %s, native %v, persistence %v, events %v and labels %v", installerType, kogitoAppDeployment.Runtime, kogitoAppDeployment.ContextDir, kogitoAppDeployment.AppName, kogitoAppDeployment.Native, kogitoAppDeployment.Persistence, kogitoAppDeployment.Events, kogitoAppDeployment.Labels)
	switch installerType {
	case CLIInstallerType:
		return cliDeployExample(namespace, kogitoAppDeployment)
	case CRInstallerType:
		return crDeployExample(namespace, kogitoAppDeployment)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

// DeployExample deploys an example
func crDeployExample(namespace string, kogitoAppDeployment KogitoAppDeployment) error {
	kogitoApp := getKogitoAppStub(namespace, kogitoAppDeployment.AppName, kogitoAppDeployment.Labels)
	kogitoApp.Spec.Runtime = kogitoAppDeployment.Runtime

	kogitoApp.Spec.Build.Native = kogitoAppDeployment.Native
	kogitoApp.Spec.Build.GitSource.URI = config.GetExamplesRepositoryURI()
	kogitoApp.Spec.Build.GitSource.ContextDir = kogitoAppDeployment.ContextDir
	kogitoApp.Spec.Build.GitSource.Reference = config.GetExamplesRepositoryRef()

	// Add namespace for service discovery
	// Can be removed once https://issues.redhat.com/browse/KOGITO-675 is done
	appendNewEnvToKogitoApp(kogitoApp, "NAMESPACE", namespace)

	var profiles []string
	if kogitoAppDeployment.Persistence {
		profiles = append(profiles, "persistence")
		kogitoApp.Spec.EnablePersistence = true
	}
	if kogitoAppDeployment.Events {
		profiles = append(profiles, "events")
		kogitoApp.Spec.EnableEvents = true
		appendNewEnvToKogitoApp(kogitoApp, "MP_MESSAGING_OUTGOING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
		appendNewEnvToKogitoApp(kogitoApp, "MP_MESSAGING_OUTGOING_KOGITO_USERTASKINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
	}

	if len(profiles) > 0 {
		appendNewEnvToKogitoAppBuild(kogitoApp, mavenArgsAppendEnvVar, "-P"+strings.Join(profiles, ","))
	}

	setupBuildImageStreams(kogitoApp)

	if kogitoAppDeployment.Native {
		kogitoApp.Spec.Build.Resources = corev1.ResourceRequirements{
			Requests: corev1.ResourceList{
				v1.ResourceName("memory"): resource.MustParse("4Gi"),
			},
		}
	}

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoApp); err != nil {
		return fmt.Errorf("Error creating example service %s: %v", kogitoAppDeployment.AppName, err)
	}
	return nil
}

func cliDeployExample(namespace string, kogitoAppDeployment KogitoAppDeployment) error {
	cmd := []string{"deploy-service", kogitoAppDeployment.AppName, config.GetExamplesRepositoryURI()}

	cmd = append(cmd, "-c", kogitoAppDeployment.ContextDir)
	cmd = append(cmd, "-r", fmt.Sprintf("%s", kogitoAppDeployment.Runtime))
	if kogitoAppDeployment.Native {
		cmd = append(cmd, "--native")
	}
	if ref := config.GetExamplesRepositoryRef(); len(ref) > 0 {
		cmd = append(cmd, "-b", ref)
	}

	// Add namespace for service discovery
	// Can be removed once https://issues.redhat.com/browse/KOGITO-675 is done
	cmd = append(cmd, "-e", fmt.Sprintf("NAMESPACE=%s", namespace))

	if mavenMirrorURL := config.GetMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		cmd = append(cmd, "--maven-mirror-url", mavenMirrorURL)
	}

	var profiles []string
	if kogitoAppDeployment.Persistence {
		profiles = append(profiles, "persistence")
		cmd = append(cmd, "--enable-persistence")
	}
	if kogitoAppDeployment.Events {
		profiles = append(profiles, "events")
		cmd = append(cmd, "--enable-events")
		cmd = append(cmd, "--env", "MP_MESSAGING_OUTGOING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS=")
		cmd = append(cmd, "--env", "MP_MESSAGING_OUTGOING_KOGITO_USERTASKINSTANCES_EVENTS_BOOTSTRAP_SERVERS=")
	}

	if len(profiles) > 0 {
		cmd = append(cmd, "--build-env", fmt.Sprintf("%s=-P%s", mavenArgsAppendEnvVar, strings.Join(profiles, ",")))
	}

	for labelKey, labelValue := range kogitoAppDeployment.Labels {
		cmd = append(cmd, "--svc-labels", fmt.Sprintf("%s=%s", labelKey, labelValue))
	}

	cmd = append(cmd, "--image-version", config.GetBuildImageVersion())

	// Add a resource request if native building
	if kogitoAppDeployment.Native {
		cmd = append(cmd, "--build-requests", "memory=4Gi")
	}

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// DeployServiceFromExampleFile deploy service from example YAML file (example is located in deploy/examples folder)
func DeployServiceFromExampleFile(namespace, exampleFile string) error {
	box := packr.New("examples", boxExamplesPath)
	yamlContent, err := box.FindString(exampleFile)
	if err != nil {
		return fmt.Errorf("Error reading file %s: %v ", exampleFile, err)
	}

	// Create basic KogitoApp stub
	kogitoApp := getKogitoAppStub(namespace, "name-should-be overwritten-from-yaml", nil)

	// Apply content from yaml file
	if err := yaml.NewYAMLOrJSONDecoder(strings.NewReader(yamlContent), len([]byte(yamlContent))).Decode(kogitoApp); err != nil {
		return fmt.Errorf("Error while unmarshalling file: %v ", err)
	}

	// Apply custom image configuration
	setupBuildImageStreams(kogitoApp)

	// Create application
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoApp); err != nil {
		return fmt.Errorf("Error creating service %s: %v", kogitoApp.Name, err)
	}
	return nil
}

// SetKogitoAppReplicas sets the number of replicas for a Kogito application
func SetKogitoAppReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Infof("Set Kogito application %s replica number to %d", name, nbPods)
	kogitoApp, err := getKogitoApp(namespace, name)
	if err != nil {
		return err
	} else if kogitoApp == nil {
		return fmt.Errorf("No KogitoApp found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	kogitoApp.Spec.KogitoServiceSpec.Replicas = &replicas
	return kubernetes.ResourceC(kubeClient).Update(kogitoApp)
}

func getKogitoAppStub(namespace, appName string, labels map[string]string) *v1alpha1.KogitoApp {
	kogitoApp := &v1alpha1.KogitoApp{
		ObjectMeta: metav1.ObjectMeta{
			Name:      appName,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoAppStatus{
			ConditionsMeta: v1alpha1.ConditionsMeta{Conditions: []v1alpha1.Condition{}},
			Deployments:    v1alpha1.Deployments{},
		},
		Spec: v1alpha1.KogitoAppSpec{
			Build: &v1alpha1.KogitoAppBuildObject{
				Envs:      []corev1.EnvVar{},
				GitSource: v1alpha1.GitSource{},
			},
		},
	}

	// Add labels
	if labels != nil {
		kogitoApp.Spec.Service = v1alpha1.KogitoAppServiceObject{
			Labels: labels,
		}
	}

	if mavenMirrorURL := config.GetMavenMirrorURL(); len(mavenMirrorURL) > 0 {
		kogitoApp.Spec.Build.MavenMirrorURL = mavenMirrorURL
	}

	return kogitoApp
}

func getKogitoApp(namespace, name string) (*v1alpha1.KogitoApp, error) {
	kogitoApp := &v1alpha1.KogitoApp{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, kogitoApp); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for KogitoApp %s: %v ", name, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return kogitoApp, nil
}

func appendNewEnvToKogitoAppBuild(kogitoApp *v1alpha1.KogitoApp, name, value string) {
	env := corev1.EnvVar{
		Name:  name,
		Value: value,
	}
	kogitoApp.Spec.Build.Envs = append(kogitoApp.Spec.Build.Envs, env)
}

func appendNewEnvToKogitoApp(kogitoApp *v1alpha1.KogitoApp, name, value string) {
	env := corev1.EnvVar{
		Name:  name,
		Value: value,
	}
	kogitoApp.Spec.KogitoServiceSpec.Envs = append(kogitoApp.Spec.KogitoServiceSpec.Envs, env)
}

func setupBuildImageStreams(kogitoApp *v1alpha1.KogitoApp) {
	// If "KOGITO_BUILD_IMAGE_STREAM_TAG" is defined, it is taken into account
	// If not defined then search for specific s2i and runtime tags
	// If none, let the operator manage
	kogitoApp.Spec.Build.ImageS2ITag = config.GetBuildS2IImageStreamTag()
	kogitoApp.Spec.Build.ImageRuntimeTag = config.GetBuildRuntimeImageStreamTag()
	// If "KOGITO_BUILD_IMAGE_VERSION" is defined, it's taken into account, otherwise set the current version
	kogitoApp.Spec.Build.ImageVersion = config.GetBuildImageVersion()
}

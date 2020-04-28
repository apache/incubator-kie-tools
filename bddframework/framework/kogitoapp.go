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

	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/gobuffalo/packr/v2"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/resource"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
)

const (
	boxExamplesPath = "../../deploy/examples"
)

// DeployService deploy a Kogito service
func DeployService(namespace string, installerType InstallerType, kogitoApp *v1alpha1.KogitoApp) error {
	GetLogger(namespace).Infof("%s deploy %s example %s with name %s, native %v, persistence %v, events %v and labels %v", installerType, kogitoApp.Spec.Runtime, kogitoApp.Spec.Build.GitSource.ContextDir, kogitoApp.Name, kogitoApp.Spec.Build.Native, kogitoApp.Spec.EnablePersistence, kogitoApp.Spec.EnableEvents, kogitoApp.Spec.Service.Labels)

	switch installerType {
	case CLIInstallerType:
		return cliDeployService(namespace, kogitoApp)
	case CRInstallerType:
		return crDeployService(namespace, kogitoApp)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crDeployService(namespace string, kogitoApp *v1alpha1.KogitoApp) error {
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoApp); err != nil {
		return fmt.Errorf("Error creating example service %s: %v", kogitoApp.Name, err)
	}
	return nil
}

func cliDeployService(namespace string, kogitoApp *v1alpha1.KogitoApp) error {
	cmd := []string{"deploy-service", kogitoApp.Name}

	if gitSourceURI := kogitoApp.Spec.Build.GitSource.URI; len(gitSourceURI) > 0 {
		cmd = append(cmd, gitSourceURI)

		if contextDir := kogitoApp.Spec.Build.GitSource.ContextDir; len(contextDir) > 0 {
			cmd = append(cmd, "-c", contextDir)
		}

		if ref := kogitoApp.Spec.Build.GitSource.Reference; len(ref) > 0 {
			cmd = append(cmd, "-b", ref)
		}
	}

	cmd = append(cmd, "--runtime", fmt.Sprintf("%s", kogitoApp.Spec.Runtime))

	if kogitoApp.Spec.Build.Native {
		cmd = append(cmd, "--native")
	}

	if mavenMirrorURL := kogitoApp.Spec.Build.MavenMirrorURL; len(mavenMirrorURL) > 0 {
		cmd = append(cmd, "--maven-mirror-url", mavenMirrorURL)
	}

	// Apply build env variables
	for _, envVar := range kogitoApp.Spec.Build.Envs {
		cmd = append(cmd, "--build-env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}
	// Apply runtime env variables
	for _, envVar := range kogitoApp.Spec.KogitoServiceSpec.Envs {
		cmd = append(cmd, "--env", fmt.Sprintf("%s=%s", envVar.Name, envVar.Value))
	}

	if kogitoApp.Spec.EnablePersistence {
		cmd = append(cmd, "--enable-persistence")
	}
	if kogitoApp.Spec.EnableEvents {
		cmd = append(cmd, "--enable-events")
	}

	for labelKey, labelValue := range kogitoApp.Spec.Service.Labels {
		cmd = append(cmd, "--svc-labels", fmt.Sprintf("%s=%s", labelKey, labelValue))
	}

	if buildImageVersion := kogitoApp.Spec.Build.ImageVersion; len(buildImageVersion) > 0 {
		cmd = append(cmd, "--image-version", buildImageVersion)
	}
	if buildS2iImageStreamTag := kogitoApp.Spec.Build.ImageS2ITag; len(buildS2iImageStreamTag) > 0 {
		cmd = append(cmd, "--image-s2i", buildS2iImageStreamTag)
	}
	if buildRuntimeImageStreamTag := kogitoApp.Spec.Build.ImageRuntimeTag; len(buildRuntimeImageStreamTag) > 0 {
		cmd = append(cmd, "--image-runtime", buildRuntimeImageStreamTag)
	}

	for resourceName, quantity := range kogitoApp.Spec.Build.Resources.Requests {
		cmd = append(cmd, "--build-requests", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}
	for resourceName, quantity := range kogitoApp.Spec.Build.Resources.Limits {
		cmd = append(cmd, "--build-limits", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	for resourceName, quantity := range kogitoApp.Spec.Resources.Requests {
		cmd = append(cmd, "--requests", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	for resourceName, quantity := range kogitoApp.Spec.Resources.Limits {
		cmd = append(cmd, "--limits", fmt.Sprintf("%s=%s", resourceName, quantity.String()))
	}

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// DeployServiceFromExampleFile deploy service from example YAML file (example is located in deploy/examples folder)
func DeployServiceFromExampleFile(namespace, runtimeType, exampleFile string) error {
	box := packr.New("examples", boxExamplesPath)
	yamlContent, err := box.FindString(exampleFile)
	if err != nil {
		return fmt.Errorf("Error reading file %s: %v ", exampleFile, err)
	}

	// Create basic KogitoApp stub
	kogitoApp := GetKogitoAppStub(namespace, runtimeType, "name-should-be overwritten-from-yaml")

	// Apply content from yaml file
	if err := yaml.NewYAMLOrJSONDecoder(strings.NewReader(yamlContent), len([]byte(yamlContent))).Decode(kogitoApp); err != nil {
		return fmt.Errorf("Error while unmarshalling file: %v ", err)
	}

	// Setup image streams again as example yaml can override image streams declared by default
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

// GetKogitoAppStub Get basic KogitoApp stub with all needed fields initialized
func GetKogitoAppStub(namespace, runtimeType, appName string) *v1alpha1.KogitoApp {
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
				GitSource:      v1alpha1.GitSource{},
				MavenMirrorURL: config.GetMavenMirrorURL(),
			},
			Service: v1alpha1.KogitoAppServiceObject{
				Labels: map[string]string{},
			},
			Runtime: v1alpha1.RuntimeType(runtimeType),
		},
	}

	setupBuildImageStreams(kogitoApp)

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

func setupBuildImageStreams(kogitoApp *v1alpha1.KogitoApp) {
	// If "KOGITO_BUILD_IMAGE_STREAM_TAG" is defined, it is taken into account
	// If not defined then search for specific s2i and runtime tags
	// If none, let the operator manage
	kogitoApp.Spec.Build.ImageS2ITag = getBuildS2IImageStreamTag(kogitoApp)
	kogitoApp.Spec.Build.ImageRuntimeTag = getBuildRuntimeImageStreamTag(kogitoApp)

	// If "KOGITO_BUILD_IMAGE_VERSION" is defined, it's taken into account, otherwise set the current version
	kogitoApp.Spec.Build.ImageVersion = config.GetBuildImageVersion()
}

func getBuildRuntimeImageStreamTag(kogitoApp *v1alpha1.KogitoApp) string {
	if len(config.GetBuildRuntimeImageStreamTag()) > 0 {
		return config.GetBuildRuntimeImageStreamTag()
	}

	if isBuildImageRegistryOrNamespaceSet() {
		buildType := resource.BuildTypeRuntime
		if kogitoApp.Spec.Runtime == v1alpha1.QuarkusRuntimeType && !kogitoApp.Spec.Build.Native {
			buildType = resource.BuildTypeRuntimeJvm
		}

		return getBuildImage(resource.BuildImageStreams[buildType][kogitoApp.Spec.Runtime])
	}

	return ""
}

func getBuildS2IImageStreamTag(kogitoApp *v1alpha1.KogitoApp) string {
	if len(config.GetBuildS2IImageStreamTag()) > 0 {
		return config.GetBuildS2IImageStreamTag()
	}

	if isBuildImageRegistryOrNamespaceSet() {
		return getBuildImage(resource.BuildImageStreams[resource.BuildTypeS2I][kogitoApp.Spec.Runtime])
	}

	return ""
}

func isBuildImageRegistryOrNamespaceSet() bool {
	return len(config.GetBuildImageRegistry()) > 0 || len(config.GetBuildImageNamespace()) > 0
}

func getBuildImage(imageName string) string {
	image := v1alpha1.Image{
		Domain:    config.GetBuildImageRegistry(),
		Namespace: config.GetBuildImageNamespace(),
		Name:      imageName,
		Tag:       config.GetBuildImageVersion(),
	}

	if len(image.Domain) == 0 {
		image.Domain = infrastructure.DefaultImageRegistry
	}

	if len(image.Namespace) == 0 {
		image.Namespace = infrastructure.DefaultImageNamespace
	}

	if len(image.Tag) == 0 {
		image.Tag = infrastructure.GetRuntimeImageVersion()
	}

	return framework.ConvertImageToImageTag(image)
}

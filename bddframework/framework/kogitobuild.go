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

package framework

import (
	"fmt"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/resource"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// DeployKogitoBuild deploy a KogitoBuild
func DeployKogitoBuild(namespace string, installerType InstallerType, kogitoBuild *v1alpha1.KogitoBuild) error {
	GetLogger(namespace).Infof("%s deploy %s example %s with name %s and native %v", installerType, kogitoBuild.Spec.Runtime, kogitoBuild.Spec.GitSource.ContextDir, kogitoBuild.Name, kogitoBuild.Spec.Native)

	if installerType == CRInstallerType {
		return crDeployKogitoBuild(namespace, kogitoBuild)
	}
	panic(fmt.Errorf("Unknown installer type %s", installerType))
}

func crDeployKogitoBuild(namespace string, kogitoBuild *v1alpha1.KogitoBuild) error {
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoBuild); err != nil {
		return fmt.Errorf("Error creating example service %s: %v", kogitoBuild.Name, err)
	}
	return nil
}

// GetKogitoBuildStub Get basic KogitoBuild stub with all needed fields initialized
func GetKogitoBuildStub(namespace, runtimeType, name string) *v1alpha1.KogitoBuild {
	kogitoBuild := &v1alpha1.KogitoBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoBuildStatus{
			Conditions: []v1alpha1.KogitoBuildConditions{},
		},
		Spec: v1alpha1.KogitoBuildSpec{
			Runtime:        v1alpha1.RuntimeType(runtimeType),
			MavenMirrorURL: config.GetMavenMirrorURL(),
		},
	}

	return kogitoBuild
}

// SetupKogitoBuildImageStreams sets the correct images for the KogitoBuild
func SetupKogitoBuildImageStreams(kogitoBuild *v1alpha1.KogitoBuild) {
	kogitoBuild.Spec.BuildImage = getKogitoBuildS2IImage(kogitoBuild)
	kogitoBuild.Spec.RuntimeImage = getKogitoBuildRuntimeImage(kogitoBuild)
}

func getKogitoBuildS2IImage(kogitoBuild *v1alpha1.KogitoBuild) v1alpha1.Image {
	if len(config.GetBuildS2IImageStreamTag()) > 0 {
		return framework.ConvertImageTagToImage(config.GetBuildS2IImageStreamTag())
	}

	return getKogitoBuildImage(resource.BuildImageStreams[resource.BuildTypeS2I][kogitoBuild.Spec.Runtime])
}

func getKogitoBuildRuntimeImage(kogitoBuild *v1alpha1.KogitoBuild) v1alpha1.Image {
	if len(config.GetBuildRuntimeImageStreamTag()) > 0 {
		return framework.ConvertImageTagToImage(config.GetBuildRuntimeImageStreamTag())
	}

	buildType := resource.BuildTypeRuntime
	if kogitoBuild.Spec.Runtime == v1alpha1.QuarkusRuntimeType && !kogitoBuild.Spec.Native {
		buildType = resource.BuildTypeRuntimeJvm
	}

	return getKogitoBuildImage(resource.BuildImageStreams[buildType][kogitoBuild.Spec.Runtime])
}

// getKogitoBuildImage returns a build image with defaults set
func getKogitoBuildImage(imageName string) v1alpha1.Image {
	image := v1alpha1.Image{
		Domain:    config.GetBuildImageRegistry(),
		Namespace: config.GetBuildImageNamespace(),
		Tag:       config.GetBuildImageVersion(),
	}

	// Update image name with suffix if provided
	if len(config.GetBuildImageNameSuffix()) > 0 {
		image.Name = fmt.Sprintf("%s-%s", imageName, config.GetBuildImageNameSuffix())
	}

	return image
}

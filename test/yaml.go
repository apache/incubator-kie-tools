// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package test

import (
	"bytes"
	"os"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	KogitoServerlessWorkflowSampleYamlCR                            = "sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml"
	KogitoServerlessWorkflowGenerationOneCR                         = "kogitoserverlessworkflow_generation1.yaml"
	KogitoServerlessWorkflowSampleDevModeYamlCR                     = "sw.kogito_v1alpha08_kogitoserverlessworkflow_devmode.yaml"
	KogitoServerlessWorkflowSampleDevModeWithExternalResourceYamlCR = "sw.kogito_v1alpha08_kogitoserverlessworkflow_devmodeWithExternalResource.yaml"
	KogitoServerlessWorkflowProdProfileSampleYamlCR                 = "sw.kogito_v1alpha08_kogitoserverlessworkflow_withExplicitProdProfile.yaml"
	KogitoServerlessPlatformWithCacheYamlCR                         = "sw.kogito_v1alpha08_kogitoserverlessplatform_withCacheAndCustomization.yaml"
	KogitoServerlessPlatformMinikubeYamlCR                          = "sw.kogito_v1alpha08_kogitoserverlessplatform_minikube.yaml"
	KogitoServerlessPlatformWithCacheMinikubeYamlCR                 = "sw.kogito_v1alpha08_kogitoserverlessplatform_withCache_minikube.yaml"
	KogitoServerlessPlatformYamlCR                                  = "sw.kogito_v1alpha08_kogitoserverlessplatform.yaml"
	KogitoServerlessPlatformWithBaseImageYamlCR                     = "sw.kogito_v1alpha08_kogitoserverlessplatformWithBaseImage.yaml"
	KogitoServerlessPlatformWithDevBaseImageYamlCR                  = "sw.kogito_v1alpha08_kogitoserverlessplatformWithDevBaseImage.yaml"
	kogitoServerlessOperatorBuilderConfig                           = "kogito-serverless-operator-builder-config_v1_configmap.yaml"

	manifestsPath = "bundle/manifests/"
)

// TODO: remove the path parameter from every method

func GetKogitoServerlessWorkflow(path string, namespace string) *operatorapi.KogitoServerlessWorkflow {
	ksw := &operatorapi.KogitoServerlessWorkflow{}
	yamlFile, err := os.ReadFile(path)
	if err != nil {
		log.Errorf(err, "yamlFile.Get err   #%v ", err)
		panic(err)
	}

	// Important: Here we are reading the CR deployment file from a given path and creating an &operatorapi.KogitoServerlessWorkflow struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksw)
	if err != nil {
		log.Errorf(err, "Unmarshal: #%v", err)
		panic(err)
	}
	log.Debugf("Successfully read KSW  #%v ", ksw)
	ksw.Namespace = namespace
	return ksw
}

func GetKogitoServerlessPlatform(path string) *operatorapi.KogitoServerlessPlatform {
	ksp := &operatorapi.KogitoServerlessPlatform{}
	yamlFile, err := os.ReadFile(path)
	if err != nil {
		log.Errorf(err, "yamlFile.Get err #%v ", err)
		panic(err)
	}
	// Important: Here we are reading the CR deployment file from a given path and creating a &operatorapi.KogitoServerlessPlatform struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksp)
	if err != nil {
		log.Errorf(err, "Unmarshal: %v", err)
		panic(err)
	}
	log.Debugf("Successfully read KSP  #%v ", ksp)
	return ksp
}

func GetKogitoServerlessPlatformInReadyPhase(path string, namespace string) *operatorapi.KogitoServerlessPlatform {
	ksp := GetKogitoServerlessPlatform(path)
	ksp.Status.Phase = operatorapi.PlatformPhaseReady
	ksp.Namespace = namespace
	return ksp
}

func GetNewEmptyKogitoServerlessBuild(name, namespace string) *operatorapi.KogitoServerlessBuild {
	return &operatorapi.KogitoServerlessBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: operatorapi.KogitoServerlessBuildSpec{
			BuildTemplate: operatorapi.BuildTemplate{
				Resources: corev1.ResourceRequirements{},
				Arguments: []string{},
			},
		},
		Status: operatorapi.KogitoServerlessBuildStatus{},
	}

}

func GetKogitoServerlessOperatorBuilderConfig(path, namespace string) *corev1.ConfigMap {
	cm := &corev1.ConfigMap{}
	yamlFile, err := os.ReadFile(path + manifestsPath + kogitoServerlessOperatorBuilderConfig)
	if err != nil {
		log.Errorf(err, "yamlFile.Get err   #%v ", err)
		panic(err)
	}
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(cm)
	if err != nil {
		log.Errorf(err, "Unmarshal: #%v", err)
		panic(err)
	}
	cm.Namespace = namespace
	return cm
}

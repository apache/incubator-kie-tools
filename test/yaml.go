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
	"runtime"
	"strings"

	"github.com/davecgh/go-spew/spew"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/kiegroup/kogito-serverless-operator/container-builder/util/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	kogitoServerlessWorkflowOrderProcessingFolder   = "order-processing"
	KogitoServerlessWorkflowSampleYamlCR            = "sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml"
	kogitoServerlessPlatformYamlCR                  = "sw.kogito_v1alpha08_kogitoserverlessplatform.yaml"
	kogitoServerlessPlatformWithCacheMinikubeYamlCR = "sw.kogito_v1alpha08_kogitoserverlessplatform_withCache_minikube.yaml"
	kogitoServerlessPlatformForOpenshift            = "sw.kogito_v1alpha08_kogitoserverlessplatform_openshift.yaml"
	kogitoServerlessWorkflowSampleDevModeYamlCR     = "sw.kogito_v1alpha08_kogitoserverlessworkflow_devmode.yaml"
	kogitoServerlessOperatorBuilderConfig           = "kogito-serverless-operator-builder-config_v1_configmap.yaml"

	configSamplesOneLevelPath = "../config/samples/"
	configSamplesTwoLevelPath = "../../config/samples/"
	e2eSamples                = "test/testdata/"
	manifestsPath             = "bundle/manifests/"
	repoName                  = "kogito-serverless-operator"
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
	log.Debugf("Successfully read KSW  #%v ", spew.Sprint(ksw))
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

func GetPathForSamples(path string) string {
	operatorPath := ""
	duplicatedFolderName := strings.Count(path, repoName)
	if duplicatedFolderName > 1 {
		// we are on GH and the path contains two times "kogito-serverless-operator"
		_, after, _ := strings.Cut(path, repoName)
		operatorPath = strings.Split(after, repoName)[1]
	} else {
		operatorPath = strings.Split(path, repoName)[1]
	}
	packages := strings.Count(operatorPath, "/")
	if strings.Contains(operatorPath, "/test/") || packages == 3 {
		return configSamplesTwoLevelPath
	} else {
		return configSamplesOneLevelPath
	}
}

func GetBaseServerlessWorkflow(namespace string) *operatorapi.KogitoServerlessWorkflow {
	_, file, _, ok := runtime.Caller(1)
	log.Info("caller:" + file)
	if ok {
		return GetKogitoServerlessWorkflow(GetPathForSamples(file)+KogitoServerlessWorkflowSampleYamlCR, namespace)
	} else {
		return &operatorapi.KogitoServerlessWorkflow{}
	}
}

func GetBaseServerlessWorkflowWithDevProfile(namespace string) *operatorapi.KogitoServerlessWorkflow {
	workflow := GetBaseServerlessWorkflow(namespace)
	workflow.Annotations["sw.kogito.kie.org/profile"] = "dev"
	return workflow
}

func GetBaseServerlessWorkflowWithProdProfile(namespace string) *operatorapi.KogitoServerlessWorkflow {
	workflow := GetBaseServerlessWorkflow(namespace)
	workflow.Annotations["sw.kogito.kie.org/profile"] = "prod"
	return workflow
}

func GetBasePlatformInReadyPhase(namespace string) *operatorapi.KogitoServerlessPlatform {
	_, file, _, ok := runtime.Caller(1)
	if ok {
		return GetKogitoServerlessPlatformInReadyPhase(GetPathForSamples(file)+kogitoServerlessPlatformYamlCR, namespace)
	} else {
		return &operatorapi.KogitoServerlessPlatform{}
	}

}

func GetBasePlatformWithBaseImageInReadyPhase(namespace string) *operatorapi.KogitoServerlessPlatform {
	platform := GetBasePlatform()
	platform.Namespace = namespace
	platform.Status.Phase = operatorapi.PlatformPhaseReady
	platform.Spec.BuildPlatform.BaseImage = "quay.io/customx/custom-swf-builder:24.8.17"
	return platform
}

func GetBasePlatformWithDevBaseImageInReadyPhase(namespace string) *operatorapi.KogitoServerlessPlatform {
	platform := GetBasePlatform()
	platform.Namespace = namespace
	platform.Status.Phase = operatorapi.PlatformPhaseReady
	platform.Spec.DevBaseImage = "quay.io/customgroup/custom-swf-builder-nightly:42.43.7"
	return platform
}

func GetBasePlatform() *operatorapi.KogitoServerlessPlatform {
	_, file, _, ok := runtime.Caller(1)
	if ok {
		return GetKogitoServerlessPlatform(GetPathForSamples(file) + kogitoServerlessPlatformYamlCR)
	} else {
		return &operatorapi.KogitoServerlessPlatform{}
	}
}

func GetPlatformMinikubeE2eTest() string {
	return e2eSamples + kogitoServerlessPlatformWithCacheMinikubeYamlCR
}

func GetPlatformOpenshiftE2eTest() string {
	return e2eSamples + kogitoServerlessPlatformForOpenshift
}

func GetServerlessWorkflowE2eOrderProcessingFolder() string {
	return e2eSamples + kogitoServerlessWorkflowOrderProcessingFolder
}

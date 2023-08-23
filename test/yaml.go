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
	"k8s.io/klog/v2"

	"github.com/kiegroup/kogito-serverless-operator/api"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/yaml"

	"github.com/kiegroup/kogito-serverless-operator/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	sonataFlowOrderProcessingFolder           = "order-processing"
	SonataFlowSampleYamlCR                    = "sonataflow.org_v1alpha08_sonataflow.yaml"
	SonataFlowGreetingsWithDataInputSchemaCR  = "sonataflow.org_v1alpha08_sonataflow_greetings_datainput.yaml"
	SonataFlowGreetingsDataInputSchemaConfig  = "v1_configmap_greetings_datainput.yaml"
	sonataFlowPlatformYamlCR                  = "sonataflow.org_v1alpha08_sonataflowplatform.yaml"
	sonataFlowPlatformWithCacheMinikubeYamlCR = "sonataflow.org_v1alpha08_sonataflowplatform_withCache_minikube.yaml"
	sonataFlowPlatformForOpenshift            = "sonataflow.org_v1alpha08_sonataflowplatform_openshift.yaml"
	sonataFlowBuilderConfig                   = "sonataflow-operator-builder-config_v1_configmap.yaml"
	sonataFlowBuildSucceed                    = "sonataflow.org_v1alpha08_sonataflowbuild.yaml"

	configSamplesOneLevelPath = "../config/samples/"
	configSamplesTwoLevelPath = "../../config/samples/"
	e2eSamples                = "test/testdata/"
	manifestsPath             = "bundle/manifests/"
	repoName                  = "kogito-serverless-operator"
)

// TODO: remove the path parameter from every method

func GetSonataFlow(path string, namespace string) *operatorapi.SonataFlow {
	ksw := &operatorapi.SonataFlow{}
	yamlFile, err := os.ReadFile(path)
	if err != nil {
		klog.V(log.E).ErrorS(err, "yamlFile.Get")
		panic(err)
	}

	// Important: Here we are reading the CR deployment file from a given path and creating an &operatorapi.SonataFlow struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksw)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Unmarshal")
		panic(err)
	}
	klog.V(log.D).InfoS("Successfully read KSW", "ksw", spew.Sprint(ksw))
	ksw.Namespace = namespace
	return ksw
}

func GetSonataFlowPlatform(path string) *operatorapi.SonataFlowPlatform {
	ksp := &operatorapi.SonataFlowPlatform{}
	yamlFile, err := os.ReadFile(path)
	if err != nil {
		klog.V(log.E).ErrorS(err, "yamlFile.Get")
		panic(err)
	}
	// Important: Here we are reading the CR deployment file from a given path and creating a &operatorapi.SonataFlowPlatform struct
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(ksp)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Unmarshal")
		panic(err)
	}
	klog.V(log.D).InfoS("Successfully read KSP", "ksp", ksp)
	ksp.Status.Manager().InitializeConditions()
	return ksp
}

func GetSonataFlowPlatformInReadyPhase(path string, namespace string) *operatorapi.SonataFlowPlatform {
	ksp := GetSonataFlowPlatform(path)
	ksp.Status.Manager().MarkTrue(api.SucceedConditionType)
	ksp.Namespace = namespace
	return ksp
}

func GetNewEmptySonataFlowBuild(name, namespace string) *operatorapi.SonataFlowBuild {
	return &operatorapi.SonataFlowBuild{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: operatorapi.SonataFlowBuildSpec{
			BuildTemplate: operatorapi.BuildTemplate{
				Resources: corev1.ResourceRequirements{},
				Arguments: []string{},
			},
		},
		Status: operatorapi.SonataFlowBuildStatus{},
	}
}

// GetLocalSucceedSonataFlowBuild gets a local (testdata dir ref to caller) SonataFlowBuild with Succeed status equals to true.
func GetLocalSucceedSonataFlowBuild(name, namespace string) *operatorapi.SonataFlowBuild {
	yamlFile, err := os.ReadFile("testdata/" + sonataFlowBuildSucceed)
	if err != nil {
		klog.ErrorS(err, "Yaml file not found on local testdata dir")
		panic(err)
	}
	build := &operatorapi.SonataFlowBuild{}
	if err := yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 255).Decode(build); err != nil {
		klog.ErrorS(err, "Failed to unmarshal SonataFlowBuild")
		panic(err)
	}
	build.Name = name
	build.Namespace = namespace
	return build
}

func GetSonataFlowBuilderConfig(path, namespace string) *corev1.ConfigMap {
	cm := &corev1.ConfigMap{}
	yamlFile, err := os.ReadFile(path + manifestsPath + sonataFlowBuilderConfig)
	if err != nil {
		klog.V(log.E).ErrorS(err, "yamlFile.Get")
		panic(err)
	}
	err = yaml.NewYAMLOrJSONDecoder(bytes.NewReader(yamlFile), 100).Decode(cm)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Unmarshal")
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

func GetBaseSonataFlow(namespace string) *operatorapi.SonataFlow {
	_, file, _, ok := runtime.Caller(1)
	klog.V(log.I).InfoS("caller", "file", file)
	if ok {
		return GetSonataFlow(GetPathForSamples(file)+SonataFlowSampleYamlCR, namespace)
	} else {
		return &operatorapi.SonataFlow{}
	}
}

func GetBaseSonataFlowWithDevProfile(namespace string) *operatorapi.SonataFlow {
	workflow := GetBaseSonataFlow(namespace)
	workflow.Annotations["sonataflow.org/profile"] = "dev"
	return workflow
}

func GetBaseSonataFlowWithProdProfile(namespace string) *operatorapi.SonataFlow {
	workflow := GetBaseSonataFlow(namespace)
	workflow.Annotations["sonataflow.org/profile"] = "prod"
	return workflow
}

func GetBasePlatformInReadyPhase(namespace string) *operatorapi.SonataFlowPlatform {
	_, file, _, ok := runtime.Caller(1)
	if ok {
		return GetSonataFlowPlatformInReadyPhase(GetPathForSamples(file)+sonataFlowPlatformYamlCR, namespace)
	} else {
		return &operatorapi.SonataFlowPlatform{}
	}

}

func GetBasePlatformWithBaseImageInReadyPhase(namespace string) *operatorapi.SonataFlowPlatform {
	platform := GetBasePlatform()
	platform.Namespace = namespace
	platform.Status.Manager().MarkTrue(api.SucceedConditionType)
	platform.Spec.Build.Config.BaseImage = "quay.io/customx/custom-swf-builder:24.8.17"
	return platform
}

func GetBasePlatformWithDevBaseImageInReadyPhase(namespace string) *operatorapi.SonataFlowPlatform {
	platform := GetBasePlatform()
	platform.Namespace = namespace
	platform.Status.Manager().MarkTrue(api.SucceedConditionType)
	platform.Spec.DevMode.BaseImage = "quay.io/customgroup/custom-swf-builder-nightly:42.43.7"
	return platform
}

func GetBasePlatform() *operatorapi.SonataFlowPlatform {
	_, file, _, ok := runtime.Caller(1)
	if ok {
		return GetSonataFlowPlatform(GetPathForSamples(file) + sonataFlowPlatformYamlCR)
	} else {
		ksp := &operatorapi.SonataFlowPlatform{}
		ksp.Status.Manager().InitializeConditions()
		return ksp
	}
}

func GetPlatformMinikubeE2eTest() string {
	return e2eSamples + sonataFlowPlatformWithCacheMinikubeYamlCR
}

func GetPlatformOpenshiftE2eTest() string {
	return e2eSamples + sonataFlowPlatformForOpenshift
}

func GetSonataFlowE2eOrderProcessingFolder() string {
	return e2eSamples + sonataFlowOrderProcessingFolder
}

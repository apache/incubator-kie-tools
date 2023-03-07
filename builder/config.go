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

package builder

import (
	"context"
	"fmt"
	"os"

	"github.com/pkg/errors"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/container-builder/util/log"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	envVarPodNamespaceName = "POD_NAMESPACE"
	// ConfigMapName is the default name for the Builder ConfigMap name
	ConfigMapName                       = "kogito-serverless-operator-builder-config"
	configKeyDefaultExtension           = "DEFAULT_WORKFLOW_EXTENSION"
	configKeyDefaultBuilderResourceName = "DEFAULT_BUILDER_RESOURCE_NAME"
	configKeyBuildNamespace             = "build-namespace"
	configKeyRegistrySecret             = "registry-secret"
	configKeyRegistryAddress            = "registry-address"
)

func NewCustomConfig(platform operatorapi.KogitoServerlessPlatform) (map[string]string, error) {
	customConfig := make(map[string]string)
	if platform.Namespace == "" {
		return nil, fmt.Errorf("unable to retrieve the namespace from platform %s", platform.Name)
	}
	customConfig[configKeyBuildNamespace] = platform.Namespace
	// Registry Secret and Address are not required, the inner builder will use minikube inner registry if available
	// TODO: we should review
	customConfig[configKeyRegistrySecret] = platform.Spec.BuildPlatform.Registry.Secret
	customConfig[configKeyRegistryAddress] = platform.Spec.BuildPlatform.Registry.Address
	return customConfig, nil
}

// GetCommonConfigMap retrieves the config map with the builder common configuration information
func GetCommonConfigMap(client client.Client, fallbackNS string) (corev1.ConfigMap, error) {

	namespace, found := os.LookupEnv(envVarPodNamespaceName)
	if !found {
		namespace = fallbackNS
	}

	if !found && len(namespace) == 0 {
		return corev1.ConfigMap{}, errors.Errorf("Can't find current context namespace, make sure that %s env is set", envVarPodNamespaceName)
	}

	existingConfigMap := corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      ConfigMapName,
			Namespace: namespace,
		},
		Data: map[string]string{},
	}

	err := client.Get(context.TODO(), types.NamespacedName{Name: ConfigMapName, Namespace: namespace}, &existingConfigMap)
	if err != nil {
		log.Error(err, "fetching configmap "+ConfigMapName)
		return corev1.ConfigMap{}, err
	}

	err = isValidBuilderCommonConfigMap(existingConfigMap)
	if err != nil {
		log.Error(err, "configmap "+ConfigMapName+" is not valid")
		return existingConfigMap, err
	}

	return existingConfigMap, nil
}

// isValidBuilderCommonConfigMap  function that will verify that in the builder config maps there are the required keys, and they aren't empty
func isValidBuilderCommonConfigMap(configMap corev1.ConfigMap) error {

	// Verifying that the key to hold the extension for the workflow is there and not empty
	if len(configMap.Data[configKeyDefaultExtension]) == 0 {
		return fmt.Errorf("unable to find %s key into builder config map", configMap.Data[configKeyDefaultExtension])
	}

	// Verifying that the key to hold the name of the Dockerfile for building the workflow is there and not empty
	if len(configMap.Data[configKeyDefaultBuilderResourceName]) == 0 {
		return fmt.Errorf("unable to find %s key into builder config map", configMap.Data[configKeyDefaultBuilderResourceName])
	}

	// Verifying that the key to hold the content of the Dockerfile for building the workflow is there and not empty
	if len(configMap.Data[configMap.Data[configKeyDefaultBuilderResourceName]]) == 0 {
		return fmt.Errorf("unable to find %s key into builder config map", configMap.Data[configKeyDefaultBuilderResourceName])
	}
	return nil
}

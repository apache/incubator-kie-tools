/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package builder

import (
	"context"
	"fmt"
	"os"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/cfg"
	"github.com/pkg/errors"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
)

const (
	envVarPodNamespaceName     = "POD_NAMESPACE"
	configKeyDefaultExtension  = "DEFAULT_WORKFLOW_EXTENSION"
	defaultBuilderResourceName = "Dockerfile"
)

// GetBuilderConfigMap retrieves the config map with the builder common configuration information
func GetBuilderConfigMap(client client.Client, fallbackNS string) (*corev1.ConfigMap, error) {
	namespace, found := os.LookupEnv(envVarPodNamespaceName)
	if !found {
		namespace = fallbackNS
	}

	if !found && len(namespace) == 0 {
		return nil, errors.Errorf("Can't find current context namespace, make sure that %s env is set", envVarPodNamespaceName)
	}

	existingConfigMap := &corev1.ConfigMap{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ConfigMap",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Name:      cfg.GetCfg().BuilderConfigMapName,
			Namespace: namespace,
		},
		Data: map[string]string{},
	}

	builderConfigMapName := cfg.GetCfg().BuilderConfigMapName
	err := client.Get(context.TODO(), types.NamespacedName{Name: builderConfigMapName, Namespace: namespace}, existingConfigMap)
	if err != nil {
		klog.V(log.E).ErrorS(err, "fetching configmap", "name", builderConfigMapName)
		return nil, err
	}

	err = isValidBuilderConfigMap(existingConfigMap)
	if err != nil {
		klog.V(log.E).ErrorS(err, "configmap is not valid", "name", builderConfigMapName)
		return existingConfigMap, err
	}

	return existingConfigMap, nil
}

// isValidBuilderConfigMap  function that will verify that in the builder config maps there are the required keys, and they aren't empty
func isValidBuilderConfigMap(configMap *corev1.ConfigMap) error {
	// Verifying that the key to hold the extension for the workflow is there and not empty
	if len(configMap.Data[configKeyDefaultExtension]) == 0 {
		return fmt.Errorf("unable to find %s key into builder config map", configMap.Data[configKeyDefaultExtension])
	}

	// Verifying that the key to hold the content of the Dockerfile for building the workflow is there and not empty
	if len(configMap.Data[defaultBuilderResourceName]) == 0 {
		return fmt.Errorf("unable to find %s key into builder config map", configMap.Data[defaultBuilderResourceName])
	}
	return nil
}

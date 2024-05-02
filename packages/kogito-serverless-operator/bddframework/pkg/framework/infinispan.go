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

package framework

import (
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/operator"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/meta"

	"gopkg.in/yaml.v2"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	infinispan "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/infinispan/v1"
)

// DeployInfinispanInstance deploys an instance of Infinispan
func DeployInfinispanInstance(namespace string, infinispan *infinispan.Infinispan) error {
	GetLogger(namespace).Info("Creating Infinispan instance", "name", infinispan.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(infinispan); err != nil {
		return fmt.Errorf("Error while creating Infinispan: %v ", err)
	}

	return nil
}

// CreateInfinispanSecret creates a new secret for Infinispan instance
func CreateInfinispanSecret(namespace, name string, credentialsMap map[string]string) error {
	GetLogger(namespace).Info("Create Infinispan Secret %s", "name", name)

	credentialsFileData, err := convertInfinispanCredentialsToYaml(credentialsMap)
	if err != nil {
		return err
	}

	return CreateSecret(namespace, name, map[string]string{infrastructure.InfinispanIdentityFileName: credentialsFileData})
}

// WaitForInfinispanPodsToBeRunningWithConfig waits for an Infinispan pod to be running with the expected configuration
func WaitForInfinispanPodsToBeRunningWithConfig(namespace string, expectedConfig infinispan.InfinispanContainerSpec, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Infinispan pod to be running with expected configuration: %+v", expectedConfig), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsWithLabels(namespace, map[string]string{"app": "infinispan-pod"})
			if err != nil || len(pods.Items) != numberOfPods {
				return false, err
			}

			for _, pod := range pods.Items {
				// First check that the pod is really running
				if !IsPodStatusConditionReady(&pod) {
					return false, nil
				}
				if !checkContainersResources(pod.Spec.Containers, getResourceRequirements(expectedConfig.CPU, expectedConfig.Memory)) {
					return false, nil
				}
				if !checkPodContainerHasEnvVariableWithValue(&pod, "infinispan", "EXTRA_JAVA_OPTIONS", expectedConfig.ExtraJvmOpts) {
					return false, nil
				}
			}

			return true, nil
		})

}

// SetInfinispanReplicas sets the number of replicas for an Infinispan instance
func SetInfinispanReplicas(namespace, name string, nbPods int) error {
	GetLogger(namespace).Info("Set Infinispan props for", "name", name, "replica number", nbPods)
	infinispan, err := getInfinispan(namespace, name)
	if err != nil {
		return err
	} else if infinispan == nil {
		return fmt.Errorf("No Infinispan found with name %s in namespace %s", name, namespace)
	}
	replicas := int32(nbPods)
	infinispan.Spec.Replicas = replicas
	return kubernetes.ResourceC(kubeClient).Update(infinispan)
}

// GetInfinispanStub returns the preconfigured Infinispan stub with set namespace, name and secretName
func GetInfinispanStub(namespace, name, secretName string) *infinispan.Infinispan {
	ispn := &infinispan.Infinispan{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Spec: infinispan.InfinispanSpec{
			Replicas: 1,
			Service: infinispan.InfinispanServiceSpec{
				Type: infinispan.ServiceTypeCache,
			},
		},
	}
	if storageClass := config.GetInfinispanStorageClass(); len(storageClass) > 0 {
		if ispn.Spec.Service.Container == nil {
			ispn.Spec.Service.Container = &infinispan.InfinispanServiceContainerSpec{}
		}
		ispn.Spec.Service.Container.StorageClassName = storageClass
	}

	return ispn
}

func convertInfinispanCredentialsToYaml(credentialsMap map[string]string) (string, error) {
	var credentials []infrastructure.InfinispanCredential
	for username, password := range credentialsMap {
		credentials = append(credentials, infrastructure.InfinispanCredential{Username: username, Password: password})
	}

	identity := infrastructure.InfinispanIdentity{Credentials: credentials}

	data, err := yaml.Marshal(&identity)
	if err != nil {
		return "", err
	}
	return string(data), nil
}

func getInfinispan(namespace, name string) (*infinispan.Infinispan, error) {
	infinispan := &infinispan.Infinispan{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: name, Namespace: namespace}, infinispan); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Infinispan %s: %v ", name, err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return infinispan, nil
}

// IsInfinispanAvailable checks if Infinispan CRD is available in the cluster
func IsInfinispanAvailable(namespace string) bool {
	context := operator.Context{
		Client: kubeClient,
		Log:    GetLogger(namespace),
		Scheme: meta.GetRegisteredSchema(),
	}
	return infrastructure.NewInfinispanHandler(context).IsInfinispanAvailable()
}

// GetRunningInfinispanPodLabels returns the labels set to infinispan pod instances
func GetRunningInfinispanPodLabels(crName string) map[string]string {
	return map[string]string{
		"app":           "infinispan-pod",
		"infinispan_cr": crName,
	}
}

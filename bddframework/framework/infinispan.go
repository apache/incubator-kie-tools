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

	"gopkg.in/yaml.v2"

	infinispan "github.com/infinispan/infinispan-operator/pkg/apis/infinispan/v1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	infinispaninfra "github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoinfra/infinispan"
)

// DeployInfinispanInstance deploys an instance of Infinispan
func DeployInfinispanInstance(namespace string, infinispan *infinispan.Infinispan) error {
	GetLogger(namespace).Infof("Creating Infinispan instance %s.", infinispan.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(infinispan); err != nil {
		return fmt.Errorf("Error while creating Infinispan: %v ", err)
	}

	return nil
}

// CreateInfinispanSecret creates a new secret for Infinispan instance
func CreateInfinispanSecret(namespace, name string, credentialsMap map[string]string) error {
	GetLogger(namespace).Infof("Create Infinispan Secret %s", name)

	credentialsFileData, err := convertInfinispanCredentialsToYaml(credentialsMap)
	if err != nil {
		return err
	}

	return CreateSecret(namespace, name, map[string]string{infinispaninfra.IdentityFileName: credentialsFileData})
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

// GetInfinispanStub returns the preconfigured Infinispan stub with set namespace, name and secretName
func GetInfinispanStub(namespace, name, secretName string) *infinispan.Infinispan {
	return &infinispan.Infinispan{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
		Spec: infinispan.InfinispanSpec{
			Replicas: 1,
			Security: infinispan.InfinispanSecurity{
				EndpointSecretName: secretName,
			},
		},
	}
}

func convertInfinispanCredentialsToYaml(credentialsMap map[string]string) (string, error) {
	credentials := []infinispaninfra.Credential{}
	for username, password := range credentialsMap {
		credentials = append(credentials, infinispaninfra.Credential{Username: username, Password: password})
	}

	identity := infinispaninfra.Identity{Credentials: credentials}

	data, err := yaml.Marshal(&identity)
	if err != nil {
		return "", err
	}
	return string(data), nil
}

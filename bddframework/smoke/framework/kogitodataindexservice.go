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
	"time"

	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// DeployKogitoDataIndexService deploy the Kogito Data Index service
func DeployKogitoDataIndexService(namespace string, replicas int) error {
	// Get correct image tag
	image := framework.ConvertImageTagToImage(infrastructure.DefaultDataIndexImage)
	image.Tag = getEnvServicesImageVersion()

	kogitoDataIndex := &v1alpha1.KogitoDataIndex{
		ObjectMeta: metav1.ObjectMeta{
			Name:      infrastructure.DefaultDataIndexName,
			Namespace: namespace,
		},
		Spec: v1alpha1.KogitoDataIndexSpec{
			Replicas: int32(replicas),
			Image:    framework.ConvertImageToImageTag(image),
		},
	}

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoDataIndex); err != nil {
		return fmt.Errorf("Error creating Kogito Data Index service: %v", err)
	}
	return nil
}

// GetKogitoDataIndex retrieves the running data index
func GetKogitoDataIndex(namespace string) (*v1alpha1.KogitoDataIndex, error) {
	dataIndex := &v1alpha1.KogitoDataIndex{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: infrastructure.DefaultDataIndexName, Namespace: namespace}, dataIndex); err != nil {
		return nil, fmt.Errorf("Error while trying to look for Kogito Data Index: %v ", err)
	} else if !exists {
		return nil, nil
	}
	return dataIndex, nil
}

// WaitForKogitoDataIndex waits that the jobs service has a certain number of replicas
func WaitForKogitoDataIndex(namespace string, replicas, timeoutInMin int) error {
	return WaitFor(namespace, "Kogito data index running", time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		dataIndex, err := GetKogitoDataIndex(namespace)
		if err != nil {
			return false, err
		}
		if dataIndex == nil {
			return false, nil
		}
		return dataIndex.Status.DeploymentStatus.AvailableReplicas == int32(replicas), nil
	})
}

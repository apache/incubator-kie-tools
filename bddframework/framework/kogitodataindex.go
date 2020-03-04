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
	"strconv"
	"time"

	apps "k8s.io/api/apps/v1"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// InstallKogitoDataIndexService deploy the Kogito Data Index service
func InstallKogitoDataIndexService(namespace string, installerType InstallerType, replicas int) error {
	GetLogger(namespace).Infof("%s install Kogito Data Index with %d replicas", installerType, replicas)
	switch installerType {
	case CLIInstallerType:
		return cliInstallKogitoDataIndex(namespace, replicas)
	case CRInstallerType:
		return crInstallKogitoDataIndex(namespace, replicas)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crInstallKogitoDataIndex(namespace string, replicas int) error {
	// Get correct image tag
	image := framework.ConvertImageTagToImage(infrastructure.DefaultDataIndexImageFullTag)
	image.Tag = GetConfigServicesImageVersion()

	kogitoDataIndex := &v1alpha1.KogitoDataIndex{
		ObjectMeta: metav1.ObjectMeta{
			Name:      infrastructure.DefaultDataIndexName,
			Namespace: namespace,
		},
		Spec: v1alpha1.KogitoDataIndexSpec{
			KogitoServiceSpec: v1alpha1.KogitoServiceSpec{
				Replicas: int32(replicas),
				Image:    image,
			},
		},
		Status: v1alpha1.KogitoDataIndexStatus{
			KogitoServiceStatus: v1alpha1.KogitoServiceStatus{
				ConditionsMeta: v1alpha1.ConditionsMeta{
					Conditions: []v1alpha1.Condition{},
				},
			},
		},
	}

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(kogitoDataIndex); err != nil {
		return fmt.Errorf("Error creating Kogito Data Index service: %v", err)
	}
	return nil
}

func cliInstallKogitoDataIndex(namespace string, replicas int) error {
	cmd := []string{"install", "data-index"}

	// Get correct image tag
	image := framework.ConvertImageTagToImage(infrastructure.DefaultDataIndexImageFullTag)
	image.Tag = GetConfigServicesImageVersion()
	cmd = append(cmd, "--image", framework.ConvertImageToImageTag(image))

	cmd = append(cmd, "--replicas", strconv.Itoa(replicas))

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// GetKogitoDataIndexDeployment retrieves the running data index deployment
func GetKogitoDataIndexDeployment(namespace string) (*apps.Deployment, error) {
	return GetDeployment(namespace, infrastructure.DefaultDataIndexName)
}

// WaitForKogitoDataIndex waits that the jobs service has a certain number of replicas
func WaitForKogitoDataIndex(namespace string, replicas, timeoutInMin int) error {
	return WaitFor(namespace, "Kogito data index running", time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		dataIndex, err := GetKogitoDataIndexDeployment(namespace)
		if err != nil {
			return false, err
		}
		if dataIndex == nil {
			return false, nil
		}
		return dataIndex.Status.Replicas == int32(replicas) && dataIndex.Status.AvailableReplicas == int32(replicas), nil
	})
}

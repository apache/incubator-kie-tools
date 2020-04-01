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
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	apps "k8s.io/api/apps/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// InstallKogitoManagementConsole deploy the Kogito Management Console
func InstallKogitoManagementConsole(namespace string, installerType InstallerType, replicas int) error {
	GetLogger(namespace).Infof("%s install Kogito Management Console with %d replicas", installerType, replicas)
	switch installerType {
	case CRInstallerType:
		return crInstallKogitoManagementConsole(namespace, replicas)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crInstallKogitoManagementConsole(namespace string, replicas int) error {
	service := getManagementConsoleStub(namespace, int32(replicas))

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(service); err != nil {
		return fmt.Errorf("Error creating Kogito Management Console: %v", err)
	}
	return nil
}

func getManagementConsoleImageTag() v1alpha1.Image {
	if len(config.GetManagementConsoleImageTag()) > 0 {
		return framework.ConvertImageTagToImage(config.GetManagementConsoleImageTag())
	}

	image := framework.ConvertImageTagToImage(infrastructure.DefaultMgmtConsoleImageFullTag)
	image.Tag = config.GetServicesImageVersion()
	return image
}

// GetKogitoManagementConsoleDeployment retrieves the running management console deployment
func GetKogitoManagementConsoleDeployment(namespace string) (*apps.Deployment, error) {
	return GetDeployment(namespace, infrastructure.DefaultMgmtConsoleName)
}

// WaitForKogitoManagementConsole waits that the management console has a certain number of replicas
func WaitForKogitoManagementConsole(namespace string, replicas, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, "Kogito Management Console running", timeoutInMin,
		func() (bool, error) {
			deployment, err := GetKogitoManagementConsoleDeployment(namespace)
			if err != nil {
				return false, err
			}
			if deployment == nil {
				return false, nil
			}
			return deployment.Status.Replicas == int32(replicas) && deployment.Status.AvailableReplicas == int32(replicas), nil
		})
}

func getManagementConsoleStub(namespace string, replicas int32) *v1alpha1.KogitoMgmtConsole {
	service := &v1alpha1.KogitoMgmtConsole{
		ObjectMeta: metav1.ObjectMeta{
			Name:      infrastructure.DefaultMgmtConsoleName,
			Namespace: namespace,
		},
		Spec: v1alpha1.KogitoMgmtConsoleSpec{
			KogitoServiceSpec: v1alpha1.KogitoServiceSpec{
				Replicas: &replicas,
				Image:    getManagementConsoleImageTag(),
			},
		},
		Status: v1alpha1.KogitoMgmtConsoleStatus{
			KogitoServiceStatus: v1alpha1.KogitoServiceStatus{
				ConditionsMeta: v1alpha1.ConditionsMeta{
					Conditions: []v1alpha1.Condition{},
				},
			},
		},
	}

	return service
}

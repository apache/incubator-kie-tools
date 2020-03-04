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

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	apps "k8s.io/api/apps/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

// InstallKogitoJobsService deploy the Kogito Jobs service
func InstallKogitoJobsService(namespace string, installerType InstallerType, replicas int, persistence bool) error {
	GetLogger(namespace).Infof("%s install Kogito Jobs Service with %d replicas and persistence %v", installerType, replicas, persistence)
	switch installerType {
	case CLIInstallerType:
		return cliInstallKogitoJobsService(namespace, replicas, persistence)
	case CRInstallerType:
		return crInstallKogitoJobsService(namespace, replicas, persistence)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crInstallKogitoJobsService(namespace string, replicas int, persistence bool) error {
	service := getJobsServiceStub(namespace, replicas, persistence)

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(service); err != nil {
		return fmt.Errorf("Error creating Kogito jobs service: %v", err)
	}
	return nil
}

func cliInstallKogitoJobsService(namespace string, replicas int, persistence bool) error {
	cmd := []string{"install", "jobs-service"}

	if persistence {
		cmd = append(cmd, "--enable-persistence")
	}

	// Get correct image tag
	image := framework.ConvertImageTagToImage(infrastructure.DefaultJobsServiceImageFullTag)
	image.Tag = GetConfigServicesImageVersion()
	cmd = append(cmd, "--image", framework.ConvertImageToImageTag(image))

	cmd = append(cmd, "--replicas", strconv.Itoa(replicas))

	_, err := ExecuteCliCommandInNamespace(namespace, cmd...)
	return err
}

// GetKogitoJobsService retrieves the running jobs service
func GetKogitoJobsService(namespace string) (*v1alpha1.KogitoJobsService, error) {
	service := &v1alpha1.KogitoJobsService{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: infrastructure.DefaultJobsServiceName, Namespace: namespace}, service); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Kogito jobs service: %v ", err)
	} else if !exists {
		return nil, nil
	}
	return service, nil
}

// GetKogitoJobsServiceDeployment retrieves the running jobs service deployment
func GetKogitoJobsServiceDeployment(namespace string) (*apps.Deployment, error) {
	return GetDeployment(namespace, infrastructure.DefaultJobsServiceName)
}

// WaitForKogitoJobsService waits that the jobs service has a certain number of replicas
func WaitForKogitoJobsService(namespace string, replicas, timeoutInMin int) error {
	return WaitFor(namespace, "Kogito jobs service running", time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		deployment, err := GetKogitoJobsServiceDeployment(namespace)
		if err != nil {
			return false, err
		}
		if deployment == nil {
			return false, nil
		}
		return deployment.Status.Replicas == int32(replicas) && deployment.Status.AvailableReplicas == int32(replicas), nil
	})
}

// SetKogitoJobsServiceReplicas sets the number of replicas for the Kogito Jobs Service
func SetKogitoJobsServiceReplicas(namespace string, nbPods int) error {
	GetLogger(namespace).Infof("Set Kogito jobs service replica number to %d", nbPods)
	kogitoJobsService, err := GetKogitoJobsService(namespace)
	if err != nil {
		return err
	} else if kogitoJobsService == nil {
		return fmt.Errorf("No Kogito jobs service found in namespace %s", namespace)
	}
	kogitoJobsService.Spec.Replicas = int32(nbPods)
	return kubernetes.ResourceC(kubeClient).Update(kogitoJobsService)
}

func getJobsServiceStub(namespace string, replicas int, persistence bool) *v1alpha1.KogitoJobsService {
	// Get correct image for tests
	image := framework.ConvertImageTagToImage(infrastructure.DefaultJobsServiceImageFullTag)
	image.Tag = GetConfigServicesImageVersion()

	service := &v1alpha1.KogitoJobsService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      infrastructure.DefaultJobsServiceName,
			Namespace: namespace,
		},
		Spec: v1alpha1.KogitoJobsServiceSpec{
			KogitoServiceSpec: v1alpha1.KogitoServiceSpec{
				Replicas: int32(replicas),
				Image:    image,
			},
		},
		Status: v1alpha1.KogitoJobsServiceStatus{
			KogitoServiceStatus: v1alpha1.KogitoServiceStatus{
				ConditionsMeta: v1alpha1.ConditionsMeta{
					Conditions: []v1alpha1.Condition{},
				},
			},
		},
	}

	if persistence {
		service.Spec.InfinispanProperties = v1alpha1.InfinispanConnectionProperties{
			UseKogitoInfra: true,
		}
	}

	return service
}

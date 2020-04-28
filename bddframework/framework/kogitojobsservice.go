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
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
)

// InstallKogitoJobsService install the Kogito Jobs Service component
func InstallKogitoJobsService(namespace string, installerType InstallerType, replicas int, persistence, events bool) error {
	resource := newKogitoJobsServiceResource(namespace, replicas)
	setKogitoJobsServicePersistence(resource, persistence)
	return InstallService(&KogitoServiceHolder{KogitoService: resource}, installerType, "jobs-service", GetCliFlags(persistence, events))
}

// WaitForKogitoJobsService wait for Kogito Jobs Service to be deployed
func WaitForKogitoJobsService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getJobsServiceName(), replicas, timeoutInMin)
}

// SetKogitoJobsServiceReplicas sets the number of replicas for the Kogito Jobs Service
func SetKogitoJobsServiceReplicas(namespace string, nbPods int32) error {
	GetLogger(namespace).Infof("Set Kogito jobs service replica number to %d", nbPods)
	kogitoJobsService, err := GetKogitoJobsService(namespace)
	if err != nil {
		return err
	} else if kogitoJobsService == nil {
		return fmt.Errorf("No Kogito jobs service found in namespace %s", namespace)
	}
	kogitoJobsService.Spec.Replicas = &nbPods
	return kubernetes.ResourceC(kubeClient).Update(kogitoJobsService)
}

// GetKogitoJobsService retrieves the running jobs service
func GetKogitoJobsService(namespace string) (*v1alpha1.KogitoJobsService, error) {
	service := &v1alpha1.KogitoJobsService{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: getJobsServiceName(), Namespace: namespace}, service); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Kogito jobs service: %v ", err)
	} else if !exists {
		return nil, nil
	}
	return service, nil
}

func getJobsServiceName() string {
	return infrastructure.DefaultJobsServiceName
}

func setKogitoJobsServicePersistence(resource *v1alpha1.KogitoJobsService, persistence bool) {
	if persistence {
		resource.Spec.InfinispanProperties = v1alpha1.InfinispanConnectionProperties{
			UseKogitoInfra: true,
		}
	}
}

func setKogitoJobsServiceEvents(resource *v1alpha1.KogitoJobsService, events bool) {
	if events {
		resource.Spec.KafkaProperties = v1alpha1.KafkaConnectionProperties{
			UseKogitoInfra: true,
		}
	}
}

func newKogitoJobsServiceResource(namespace string, replicas int) *v1alpha1.KogitoJobsService {
	return &v1alpha1.KogitoJobsService{
		ObjectMeta: NewObjectMetadata(namespace, getJobsServiceName()),
		Spec: v1alpha1.KogitoJobsServiceSpec{
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetJobsServiceImageTag(), infrastructure.DefaultJobsServiceImageName),
		},
		Status: v1alpha1.KogitoJobsServiceStatus{
			KogitoServiceStatus: NewKogitoServiceStatus(),
		},
	}
}

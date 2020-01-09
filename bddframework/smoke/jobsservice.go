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

package main

import (
	"fmt"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	jobsServiceName = "jobs-service"
)

// DeployKogitoJobsService deploy the Kogito Jobs service
func DeployKogitoJobsService(namespace string, replicas int, persistence bool) error {
	GetLogger(namespace).Infof("Deploy Kogito jobs service")
	service := getJobsServiceStub(namespace, replicas, persistence)

	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(service); err != nil {
		return fmt.Errorf("Error creating Kogito jobs service: %v", err)
	}
	return nil
}

// GetKogitoJobsService retrieves the running jobs service
func GetKogitoJobsService(namespace string) (*v1alpha1.KogitoJobsService, error) {
	service := &v1alpha1.KogitoJobsService{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: jobsServiceName, Namespace: namespace}, service); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Kogito jobs service: %v ", err)
	} else if errors.IsNotFound(err) || !exists {
		return nil, nil
	}
	return service, nil
}

// WaitForKogitoJobsService waits that the jobs service has a certain number of replicas
func WaitForKogitoJobsService(namespace string, replicas, timeoutInMin int) error {
	return waitFor(namespace, "Kogito jobs service running", time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		service, err := GetKogitoJobsService(namespace)
		if err != nil {
			return false, err
		}
		if service == nil {
			return false, nil
		}
		return service.Status.DeploymentStatus.AvailableReplicas == int32(replicas), nil
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
	service := &v1alpha1.KogitoJobsService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      jobsServiceName,
			Namespace: namespace,
		},
		Status: v1alpha1.KogitoJobsServiceStatus{
			ConditionsMeta: v1alpha1.ConditionsMeta{Conditions: []v1alpha1.Condition{}},
		},
		Spec: v1alpha1.KogitoJobsServiceSpec{
			Replicas: int32(replicas),
		},
	}

	if persistence {
		service.Spec.InfinispanProperties = v1alpha1.InfinispanConnectionProperties{
			UseKogitoInfra: true,
		}
	}

	return service
}

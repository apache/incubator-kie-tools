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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api/app/v1beta1"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
)

const (
	// DefaultJobsServiceImageName is the default image name for the Jobs Service image
	DefaultJobsServiceImageName = "kogito-jobs-service-ephemeral"
	// JobsServiceInfinispanImageName is the image name for the Jobs Service Service with Infinispan
	JobsServiceInfinispanImageName = "kogito-jobs-service-infinispan"
	// JobsServiceMongoDBImageName is the image name for the Jobs Service Service with MongoDB
	JobsServiceMongoDBImageName = "kogito-jobs-service-mongodb"
	// JobsServicePostgresqlImageName is the image name for the Jobs Service Service with PostgreSQL
	JobsServicePostgresqlImageName = "kogito-jobs-service-postgresql"
	// DefaultJobsServiceName is the default name for the Jobs Services instance service
	DefaultJobsServiceName = "jobs-service"
)

// InstallKogitoJobsService install the Kogito Jobs Service component
func InstallKogitoJobsService(installerType InstallerType, jobsService *bddtypes.KogitoServiceHolder) error {
	return InstallService(jobsService, installerType, "jobs-service")
}

// WaitForKogitoJobsService wait for Kogito Jobs Service to be deployed
func WaitForKogitoJobsService(namespace string, replicas int, timeoutInMin int) error {
	return WaitForService(namespace, getJobsServiceName(), replicas, timeoutInMin)
}

// SetKogitoJobsServiceReplicas sets the number of replicas for the Kogito Jobs Service
func SetKogitoJobsServiceReplicas(namespace string, nbPods int32) error {
	GetLogger(namespace).Info("Set Kogito jobs service props", "replica number", nbPods)
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
func GetKogitoJobsService(namespace string) (*v1beta1.KogitoSupportingService, error) {
	service := &v1beta1.KogitoSupportingService{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: getJobsServiceName(), Namespace: namespace}, service); err != nil && !errors.IsNotFound(err) {
		return nil, fmt.Errorf("Error while trying to look for Kogito jobs service: %v ", err)
	} else if !exists {
		return nil, nil
	}
	return service, nil
}

// WaitForKogitoJobsServiceLogContainsTextWithinMinutes waits until any pods contains a text
func WaitForKogitoJobsServiceLogContainsTextWithinMinutes(namespace, logText string, timeoutInMin int) error {
	return WaitForAnyPodsByDeploymentToContainTextInLog(namespace, getJobsServiceName(), logText, timeoutInMin)
}

func getJobsServiceName() string {
	return DefaultJobsServiceName
}

// GetKogitoJobsServiceResourceStub Get basic KogitoJobsService stub with all needed fields initialized
func GetKogitoJobsServiceResourceStub(namespace string, replicas int) *v1beta1.KogitoSupportingService {
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: NewObjectMetadata(namespace, getJobsServiceName()),
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType:       api.JobsService,
			KogitoServiceSpec: NewKogitoServiceSpec(int32(replicas), config.GetServiceImageTag(config.JobServiceImageType, config.EphemeralPersistenceType), DefaultJobsServiceImageName),
		},
	}
}

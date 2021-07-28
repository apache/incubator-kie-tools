// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package test

import (
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// CreateFakeDataIndex ...
func CreateFakeDataIndex(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("data-index", namespace, api.DataIndex)
}

// CreateFakeJobsService ...
func CreateFakeJobsService(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("jobs-service", namespace, api.JobsService)
}

// CreateFakeMgmtConsole ...
func CreateFakeMgmtConsole(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("mgmt-console", namespace, api.MgmtConsole)
}

// CreateFakeExplainabilityService ...
func CreateFakeExplainabilityService(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("explainability-service", namespace, api.Explainability)
}

// CreateFakeTaskConsole ...
func CreateFakeTaskConsole(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("task-console", namespace, api.TaskConsole)
}

// CreateFakeTrustyAIService ...
func CreateFakeTrustyAIService(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("trusty-ai", namespace, api.TrustyAI)
}

// CreateFakeTrustyUIService ...
func CreateFakeTrustyUIService(namespace string) *v1beta1.KogitoSupportingService {
	return createFakeKogitoSupportingServiceInstance("trusty-ui", namespace, api.TrustyUI)
}

func createFakeKogitoSupportingServiceInstance(name, namespace string, serviceType api.ServiceType) *v1beta1.KogitoSupportingService {
	replicas := int32(1)
	return &v1beta1.KogitoSupportingService{
		ObjectMeta: v1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: serviceType,
			KogitoServiceSpec: v1beta1.KogitoServiceSpec{
				Replicas: &replicas,
			},
		},
	}
}

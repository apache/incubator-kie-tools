// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package profiles

import (
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// devServiceCreator is an objectCreator for a basic Service for a workflow using dev profile
// aiming a vanilla Kubernetes Deployment.
// It maps the default HTTP port (80) to the target Java application webserver on port 8080.
// It configures the Service as a NodePort type service, in this way it will be easier for a developer access the service
func devServiceCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	object, _ := defaultServiceCreator(workflow)
	service := object.(*corev1.Service)
	// Let's double-check that the workflow is using the Dev Profile we would like to expose it via NodePort
	if IsDevProfile(workflow) {
		service.Spec.Type = corev1.ServiceTypeNodePort
	}
	return service, nil
}

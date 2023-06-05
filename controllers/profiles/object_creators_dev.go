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
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/workflowproj"

	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	// healthFailureThresholdDevMode exclusive threshold for devmode given that it might take longer than the immutable image to start/live/respond.
	healthFailureThresholdDevMode = 50
)

var defaultDevApplicationProperties = "quarkus.http.port=" + defaultHTTPWorkflowPortStr + "\n" +
	"quarkus.http.host=0.0.0.0\n" +
	// We disable the Knative health checks to not block the dev pod to run if Knative objects are not available
	// See: https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/eventing/consume-produce-events-with-knative-eventing.html#ref-knative-eventing-add-on-source-configuration
	"org.kie.kogito.addons.knative.eventing.health-enabled=false\n" +
	"quarkus.devservices.enabled=false\n" +
	"quarkus.kogito.devservices.enabled=false\n"

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

func devDeploymentCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	obj, err := defaultDeploymentCreator(workflow)
	if err != nil {
		return nil, err
	}
	deployment := obj.(*appsv1.Deployment)
	deployment.Spec.Template.Spec.Containers[0].StartupProbe.FailureThreshold = healthFailureThresholdDevMode
	deployment.Spec.Template.Spec.Containers[0].LivenessProbe.FailureThreshold = healthFailureThresholdDevMode
	deployment.Spec.Template.Spec.Containers[0].ReadinessProbe.FailureThreshold = healthFailureThresholdDevMode
	return deployment, nil
}

// workflowDefConfigMapCreator creates a new ConfigMap that holds the definition of a workflow specification.
func workflowDefConfigMapCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	configMap, err := workflowdef.CreateNewConfigMap(workflow)
	if err != nil {
		return nil, err
	}
	workflowproj.SetDefaultLabels(workflow, configMap)
	return configMap, nil
}

func ensureWorkflowDefConfigMapMutator(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := workflowDefConfigMapCreator(workflow)
			if err != nil {
				return err
			}
			object.(*corev1.ConfigMap).Data = original.(*corev1.ConfigMap).Data
			object.(*corev1.ConfigMap).Labels = original.GetLabels()
			return nil
		}
	}
}

func ensureWorkflowDevPropertiesConfigMapMutator(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
	return ensureWorkflowPropertiesConfigMapMutator(workflow, defaultDevApplicationProperties)
}

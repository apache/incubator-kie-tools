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

package common

import (
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
	"github.com/kiegroup/kogito-serverless-operator/workflowproj"
)

// ImageDeploymentMutateVisitor creates a visitor that mutates a vanilla Kubernetes Deployment to apply the given image in the DefaultContainerName container
// Only overrides the image if .spec.podTemplate.container.Image is empty.
func ImageDeploymentMutateVisitor(workflow *operatorapi.SonataFlow, image string) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		// noop since we already have an image in the flow container defined by the user.
		if workflow.HasFlowContainerImage() {
			return func() error {
				return nil
			}
		}
		return func() error {
			deployment := object.(*appsv1.Deployment)
			_, idx := kubeutil.GetContainerByName(operatorapi.DefaultContainerName, &deployment.Spec.Template.Spec)
			deployment.Spec.Template.Spec.Containers[idx].Image = image
			return nil
		}
	}
}

// DeploymentMutateVisitor guarantees the state of the default Deployment object
func DeploymentMutateVisitor(workflow *operatorapi.SonataFlow) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := DeploymentCreator(workflow)
			if err != nil {
				return err
			}
			EnsureDeployment(original.(*appsv1.Deployment), object.(*appsv1.Deployment))
			return nil
		}
	}
}

// EnsureDeployment Ensure that the original Deployment fields are immutable.
func EnsureDeployment(original *appsv1.Deployment, object *appsv1.Deployment) {
	object.Spec.Replicas = original.Spec.Replicas
	object.Spec.Selector = original.Spec.Selector
	object.Labels = original.GetLabels()

	object.Spec.Template.Spec = original.Spec.Template.Spec
}

func ServiceMutateVisitor(workflow *operatorapi.SonataFlow) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := ServiceCreator(workflow)
			if err != nil {
				return err
			}
			object.(*corev1.Service).Spec.Ports = original.(*corev1.Service).Spec.Ports
			object.(*corev1.Service).Labels = original.GetLabels()
			return nil
		}
	}
}

func WorkflowPropertiesMutateVisitor(workflow *operatorapi.SonataFlow) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			cm := object.(*corev1.ConfigMap)
			cm.Labels = workflow.GetLabels()
			_, hasKey := cm.Data[workflowproj.ApplicationPropertiesFileName]
			if !hasKey {
				cm.Data = make(map[string]string, 1)
				cm.Data[workflowproj.ApplicationPropertiesFileName] = ImmutableApplicationProperties(workflow)
				return nil
			}

			// In the future, if this needs change, instead we can receive an AppPropertyHandler in this mutator
			cm.Data[workflowproj.ApplicationPropertiesFileName] =
				NewAppPropertyHandler(workflow).
					WithUserProperties(cm.Data[workflowproj.ApplicationPropertiesFileName]).
					Build()

			return nil
		}
	}
}

// RolloutDeploymentIfCMChangedMutateVisitor forces a pod refresh if the workflow definition suffered any changes.
// This method can be used as an alternative to the Kubernetes ConfigMap refresher.
//
// See: https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
func RolloutDeploymentIfCMChangedMutateVisitor(cmOperationResult controllerutil.OperationResult) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if cmOperationResult == controllerutil.OperationResultUpdated {
				deployment := object.(*appsv1.Deployment)
				err := kubeutil.MarkDeploymentToRollout(deployment)
				return err
			}
			return nil
		}
	}
}

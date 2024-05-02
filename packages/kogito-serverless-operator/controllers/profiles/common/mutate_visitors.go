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

package common

import (
	"context"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/discovery"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/properties"
	"github.com/imdario/mergo"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/workflowproj"
)

// ImageDeploymentMutateVisitor creates a visitor that mutates a vanilla Kubernetes Deployment to apply the given image in the DefaultContainerName container
// Only overrides the image if .spec.podTemplate.container.Image is empty.
func ImageDeploymentMutateVisitor(workflow *operatorapi.SonataFlow, image string) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		// noop since we already have an image in the flow container defined by the user.
		if workflow.HasContainerSpecImage() {
			return func() error {
				return nil
			}
		}
		return func() error {
			deployment := object.(*appsv1.Deployment)
			_, idx := kubeutil.GetContainerByName(operatorapi.DefaultContainerName, &deployment.Spec.Template.Spec)
			deployment.Spec.Template.Spec.Containers[idx].Image = image
			deployment.Spec.Template.Spec.Containers[idx].ImagePullPolicy = kubeutil.GetImagePullPolicy(image)
			return nil
		}
	}
}

// DeploymentMutateVisitor guarantees the state of the default Deployment object
func DeploymentMutateVisitor(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := DeploymentCreator(workflow, plf)
			if err != nil {
				return err
			}
			return EnsureDeployment(original.(*appsv1.Deployment), object.(*appsv1.Deployment))
		}
	}
}

// EnsureDeployment Ensure that the original Deployment fields are immutable.
func EnsureDeployment(original *appsv1.Deployment, object *appsv1.Deployment) error {
	object.Spec.Replicas = original.Spec.Replicas
	object.Spec.Selector = original.Spec.Selector
	object.Labels = original.GetLabels()

	// Clean up the volumes, they are inherited from original, additional are added by other visitors
	object.Spec.Template.Spec.Volumes = nil
	for i := range object.Spec.Template.Spec.Containers {
		object.Spec.Template.Spec.Containers[i].VolumeMounts = nil
	}

	// we do a merge to not keep changing the spec since k8s will set default values to the podSpec
	return mergo.Merge(&object.Spec.Template.Spec, original.Spec.Template.Spec, mergo.WithOverride)
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

func ManagedPropertiesMutateVisitor(ctx context.Context, catalog discovery.ServiceCatalog,
	workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform, userProps *corev1.ConfigMap) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			managedProps := object.(*corev1.ConfigMap)
			managedProps.Labels = workflow.GetLabels()
			_, hasKey := managedProps.Data[workflowproj.GetManagedPropertiesFileName(workflow)]
			if !hasKey {
				managedProps.Data = make(map[string]string, 1)
				managedProps.Data[workflowproj.GetManagedPropertiesFileName(workflow)] = ""
			}

			userProperties, hasKey := userProps.Data[workflowproj.ApplicationPropertiesFileName]
			if !hasKey {
				userProperties = ""
			}
			propertyHandler, err := properties.NewManagedPropertyHandler(workflow, plf)
			if err != nil {
				return err
			}
			managedProps.Data[workflowproj.GetManagedPropertiesFileName(workflow)] = propertyHandler.WithUserProperties(userProperties).
				WithServiceDiscovery(ctx, catalog).
				Build()
			return nil
		}
	}
}

// RolloutDeploymentIfCMChangedMutateVisitor forces a pod refresh if the workflow definition suffered any changes.
// This method can be used as an alternative to the Kubernetes ConfigMap refresher.
//
// See: https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
func RolloutDeploymentIfCMChangedMutateVisitor(workflow *operatorapi.SonataFlow, userPropsCM *corev1.ConfigMap, managedPropsCM *corev1.ConfigMap) MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			deployment := object.(*appsv1.Deployment)
			err := kubeutil.AnnotateDeploymentConfigChecksum(workflow, deployment, userPropsCM, managedPropsCM)
			return err
		}
	}
}

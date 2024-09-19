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

package preview

import (
	"fmt"

	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	servingv1 "knative.dev/serving/pkg/apis/serving/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"
	kubeutil "github.com/apache/incubator-kie-kogito-serverless-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-kogito-serverless-operator/workflowproj"
)

const (
	imageOpenShiftTriggers            = "image.openshift.io/triggers"
	imageOpenShiftTriggersValueFormat = "[{\"from\":{\"kind\":\"ImageStreamTag\",\"name\":\"%s\"},\"fieldPath\":\"spec.template.spec.containers[?(@.name==\\\"" + v1alpha08.DefaultContainerName + "\\\")].image\"}]"
)

// addOpenShiftImageTriggerDeploymentMutateVisitor adds the ImageStream trigger annotation to the Deployment
//
// See: https://docs.openshift.com/container-platform/4.13/openshift_images/triggering-updates-on-imagestream-changes.html
func addOpenShiftImageTriggerDeploymentMutateVisitor(workflow *v1alpha08.SonataFlow, image string) common.MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		if workflow.HasContainerSpecImage() {
			// noop since we don't need to build anything
			return func() error {
				return nil
			}
		}
		return func() error {
			annotations := make(map[string]string, len(object.(*appsv1.Deployment).Annotations)+1)
			for k, v := range object.(*appsv1.Deployment).Annotations {
				annotations[k] = v
			}
			annotations[imageOpenShiftTriggers] = fmt.Sprintf(imageOpenShiftTriggersValueFormat, image)
			object.(*appsv1.Deployment).Annotations = annotations
			return nil
		}
	}
}

// mountConfigMapsMutateVisitor mounts the required configMaps in the SonataFlow instance
func mountConfigMapsMutateVisitor(workflow *operatorapi.SonataFlow, userPropsCM *v1.ConfigMap, managedPropsCM *v1.ConfigMap) common.MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			var podTemplateSpec *v1.PodSpec

			if workflow.IsKnativeDeployment() {
				ksvc := object.(*servingv1.Service)
				podTemplateSpec = &ksvc.Spec.Template.Spec.PodSpec
			} else {
				deployment := object.(*appsv1.Deployment)
				podTemplateSpec = &deployment.Spec.Template.Spec
				if err := kubeutil.AnnotateDeploymentConfigChecksum(workflow, deployment, userPropsCM, managedPropsCM); err != nil {
					return err
				}
			}

			_, idx := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, podTemplateSpec)

			if len(podTemplateSpec.Volumes) == 0 {
				podTemplateSpec.Volumes = make([]v1.Volume, 0, 1)
			}
			if len(podTemplateSpec.Containers[idx].VolumeMounts) == 0 {
				podTemplateSpec.Containers[idx].VolumeMounts = make([]v1.VolumeMount, 0, 1)
			}

			defaultResourcesVolume := v1.Volume{Name: constants.ConfigMapWorkflowPropsVolumeName, VolumeSource: v1.VolumeSource{Projected: &v1.ProjectedVolumeSource{}}}
			kubeutil.VolumeProjectionAddConfigMap(defaultResourcesVolume.Projected, userPropsCM.Name, v1.KeyToPath{Key: workflowproj.ApplicationPropertiesFileName, Path: workflowproj.ApplicationPropertiesFileName})
			kubeutil.VolumeProjectionAddConfigMap(defaultResourcesVolume.Projected, managedPropsCM.Name, v1.KeyToPath{Key: workflowproj.GetManagedPropertiesFileName(workflow), Path: workflowproj.GetManagedPropertiesFileName(workflow)})
			kubeutil.AddOrReplaceVolume(podTemplateSpec, defaultResourcesVolume)
			kubeutil.AddOrReplaceVolumeMount(&podTemplateSpec.Containers[idx],
				kubeutil.VolumeMount(constants.ConfigMapWorkflowPropsVolumeName, true, quarkusProdConfigMountPath))

			return nil
		}
	}
}

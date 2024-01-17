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

package prod

import (
	"fmt"

	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"

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

// mountDevConfigMapsMutateVisitor mounts the required configMaps in the Workflow Dev Deployment
func mountProdConfigMapsMutateVisitor(propsCM *v1.ConfigMap) common.MutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			deployment := object.(*appsv1.Deployment)
			_, idx := kubeutil.GetContainerByName(v1alpha08.DefaultContainerName, &deployment.Spec.Template.Spec)

			if len(deployment.Spec.Template.Spec.Volumes) == 0 {
				deployment.Spec.Template.Spec.Volumes = make([]v1.Volume, 0, 1)
			}
			if len(deployment.Spec.Template.Spec.Containers[idx].VolumeMounts) == 0 {
				deployment.Spec.Template.Spec.Containers[idx].VolumeMounts = make([]v1.VolumeMount, 0, 1)
			}

			kubeutil.AddOrReplaceVolume(&deployment.Spec.Template.Spec,
				kubeutil.VolumeConfigMap(constants.ConfigMapWorkflowPropsVolumeName, propsCM.Name, v1.KeyToPath{Key: workflowproj.ApplicationPropertiesFileName, Path: workflowproj.ApplicationPropertiesFileName}))
			kubeutil.AddOrReplaceVolumeMount(idx, &deployment.Spec.Template.Spec,
				kubeutil.VolumeMount(constants.ConfigMapWorkflowPropsVolumeName, true, quarkusProdConfigMountPath))

			kubeutil.AnnotateDeploymentConfigChecksum(deployment, propsCM)
			return nil
		}
	}
}

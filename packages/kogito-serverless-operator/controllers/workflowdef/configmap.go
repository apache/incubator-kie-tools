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

package workflowdef

import (
	"context"
	"path"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/workflowproj"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils/kubernetes"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
)

const KogitoWorkflowJSONFileExt = ".sw.json"

// CreateNewConfigMap creates a new configMap object instance with the workflow definition based on the given SonataFlow custom resource.
// It does not persist the CM into the Kubernetes storage.
// The name and namespace are the same of the given CR.
func CreateNewConfigMap(workflow *operatorapi.SonataFlow) (*corev1.ConfigMap, error) {
	workflowDef, err := GetJSONWorkflow(workflow, context.TODO())
	if err != nil {
		return nil, err
	}
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    workflowproj.GetMergedLabels(workflow),
		},
		Data: map[string]string{GetWorkflowDefFileName(workflow): string(workflowDef)},
	}, nil
}

// GetWorkflowDefFileName returns the default workflow file definition that should be injected/mounted to a workflow application given a SonataFlow CR.
func GetWorkflowDefFileName(workflow *operatorapi.SonataFlow) string {
	return workflow.Name + KogitoWorkflowJSONFileExt
}

// FetchExternalResourcesConfigMapsRef fetches the Resource ConfigMaps into a LocalObjectReference that a client can mount to the workflow application.
// The map format is map[<resource_type>]<ConfigMap_reference>. For example map["resource-openapi"]{MyOpenApisConfigMap}
func FetchExternalResourcesConfigMapsRef(client client.Client, workflow *operatorapi.SonataFlow) ([]operatorapi.ConfigMapWorkflowResource, error) {
	var externalConfigMaps []operatorapi.ConfigMapWorkflowResource
	for _, res := range workflow.Spec.Resources.ConfigMaps {
		// check if there's a valid reference of the given CM
		_, err := fetchConfigMapReference(client, res.ConfigMap.Name, workflow.Namespace)
		if err != nil {
			return nil, err
		} else {
			externalConfigMaps = append(externalConfigMaps, res)
		}
	}
	return externalConfigMaps, nil
}

func fetchConfigMapReference(client client.Client, configMapName, namespace string) (*corev1.LocalObjectReference, error) {
	configMap := corev1.ConfigMap{}
	err := client.Get(context.TODO(), types.NamespacedName{Name: configMapName, Namespace: namespace}, &configMap)
	if err != nil {
		return nil, err
	}
	return &corev1.LocalObjectReference{Name: configMap.Name}, nil
}

// ExternalResCMsToVolumesAndMount creates volume mounts for ExtResType ConfigMaps references.
// See FetchExternalResourcesConfigMapsRef that should return the maps needed as input for this function.
// `baseMountPath` is a string with the base mount path to join with the given relative path in the configMap reference.
func ExternalResCMsToVolumesAndMount(configMaps []operatorapi.ConfigMapWorkflowResource, baseMountPath string) ([]corev1.Volume, []corev1.VolumeMount) {
	volumes := make([]corev1.Volume, 0)
	volumeMounts := make([]corev1.VolumeMount, 0)
	for _, cm := range configMaps {
		volumes = append(volumes, kubernetes.VolumeConfigMap(cm.ConfigMap.Name, cm.ConfigMap.Name))
		volumeMounts = append(volumeMounts, kubernetes.VolumeMount(cm.ConfigMap.Name, true, path.Join(baseMountPath, cm.WorkflowPath)))
	}
	return volumes, volumeMounts
}

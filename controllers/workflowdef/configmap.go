// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package workflowdef

import (
	"context"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const KogitoWorkflowJSONFileExt = ".sw.json"

// NewConfigMap creates a new configMap object instance with the workflow definition based on the given KogitoServerlessWorkflow custom resource.
// It does not persist the CM into the Kubernetes storage.
// The name and namespace are the same of the given CR.
func NewConfigMap(workflow *operatorapi.KogitoServerlessWorkflow) (*corev1.ConfigMap, error) {
	workflowDef, err := GetJSONWorkflow(workflow, context.TODO())
	if err != nil {
		return nil, err
	}
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
		},
		Data: map[string]string{workflow.Name + KogitoWorkflowJSONFileExt: string(workflowDef)},
	}, nil
}

// FetchExternalResourcesConfigMapsRef fetches the Resource ConfigMaps into a LocalObjectReference that a client can mount to the workflow application.
// The map format is map[<resource_type>]<ConfigMap_reference>. For example map["resource-openapi"]{MyOpenApisConfigMap}
func FetchExternalResourcesConfigMapsRef(client client.Client, workflow *operatorapi.KogitoServerlessWorkflow) (map[ExternalResourceType]*corev1.LocalObjectReference, error) {
	externalConfigMaps := make(map[ExternalResourceType]*corev1.LocalObjectReference, 0)
	for k, val := range workflow.Annotations {
		resource := GetAnnotationResourceType(k)
		if len(resource) > 0 {
			cm, err := fetchConfigMapReference(client, val, workflow.Namespace)
			if err != nil {
				return nil, err
			} else {
				externalConfigMaps[resource] = cm
			}
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

// ExternalResCMsToVolumesAndMount creates volume mounts for ExternalResourceType ConfigMaps references.
// See FetchExternalResourcesConfigMapsRef that should return the maps needed as input for this function.
func ExternalResCMsToVolumesAndMount(configMaps map[ExternalResourceType]*corev1.LocalObjectReference, mountPath map[ExternalResourceType]string) ([]corev1.Volume, []corev1.VolumeMount) {
	volumes := make([]corev1.Volume, 0)
	volumeMounts := make([]corev1.VolumeMount, 0)
	for k, cm := range configMaps {
		volumes = append(volumes, kubernetes.Volume(cm.Name, cm.Name))
		volumeMounts = append(volumeMounts, kubernetes.VolumeMount(cm.Name, true, mountPath[k]))
	}
	return volumes, volumeMounts
}

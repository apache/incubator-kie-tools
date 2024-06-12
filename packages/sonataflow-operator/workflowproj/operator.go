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

package workflowproj

import (
	"fmt"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
)

const (
	workflowUserConfigMapNameSuffix = "-props"
	// ApplicationPropertiesFileName is the default application properties file name holding user properties
	ApplicationPropertiesFileName      = "application.properties"
	workflowManagedConfigMapNameSuffix = "-managed-props"
	// LabelApp key to use among object selectors, "app" is used among k8s applications to group objects in some UI consoles
	LabelApp = "app"
	// LabelService key to use among object selectors
	LabelService = metadata.Domain + "/service"
	// LabelWorkflow specialized label managed by the controller
	LabelWorkflow = metadata.Domain + "/workflow-app"
)

// SetTypeToObject sets the Kind and ApiVersion to a given object since the default constructor won't do it.
// See: https://github.com/kubernetes/client-go/issues/308#issuecomment-700099260
func SetTypeToObject(obj runtime.Object, s *runtime.Scheme) error {
	gvks, _, err := s.ObjectKinds(obj)
	if err != nil {
		return err
	}
	for _, gvk := range gvks {
		if len(gvk.Kind) == 0 {
			continue
		}
		if len(gvk.Version) == 0 || gvk.Version == runtime.APIVersionInternal {
			continue
		}
		obj.GetObjectKind().SetGroupVersionKind(gvk)
		break
	}
	return nil
}

// GetWorkflowUserPropertiesConfigMapName gets the default ConfigMap name that holds the user application property for the given workflow
func GetWorkflowUserPropertiesConfigMapName(workflow *operatorapi.SonataFlow) string {
	return workflow.Name + workflowUserConfigMapNameSuffix
}

// GetWorkflowManagedPropertiesConfigMapName gets the default ConfigMap name that holds the managed application property for the given workflow
func GetWorkflowManagedPropertiesConfigMapName(workflow *operatorapi.SonataFlow) string {
	return workflow.Name + workflowManagedConfigMapNameSuffix
}

// GetManagedPropertiesFileName gets the default ConfigMap name that holds the managed application property for the given workflow
func GetManagedPropertiesFileName(workflow *operatorapi.SonataFlow) string {
	profile := metadata.QuarkusProdProfile
	if IsDevProfile(workflow) {
		profile = metadata.QuarkusDevProfile
	}
	return fmt.Sprintf("application-%s.properties", profile)
}

// GetDefaultLabels gets the default labels based on the given workflow.
func GetDefaultLabels(workflow *operatorapi.SonataFlow) map[string]string {
	return map[string]string{
		LabelApp:      workflow.Name,
		LabelWorkflow: workflow.Name,
	}
}

// SetMergedLabels adds the merged labels to the given object.
func SetMergedLabels(workflow *operatorapi.SonataFlow, object metav1.Object) {
	object.SetLabels(GetMergedLabels(workflow))
}

// GetMergedLabels gets labels based on the given workflow, includes their own labels, merged with the default ones.
func GetMergedLabels(workflow *operatorapi.SonataFlow) map[string]string {
	mergedLabels := make(map[string]string)
	if labels := workflow.GetLabels(); labels != nil {
		for k, v := range labels {
			mergedLabels[k] = v
		}
	}
	for k, v := range GetDefaultLabels(workflow) {
		mergedLabels[k] = v
	}
	return mergedLabels
}

// CreateNewUserPropsConfigMap creates a new empty ConfigMap object to hold the user application properties of the workflow.
func CreateNewUserPropsConfigMap(workflow *operatorapi.SonataFlow) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetWorkflowUserPropertiesConfigMapName(workflow),
			Namespace: workflow.Namespace,
			Labels:    GetMergedLabels(workflow),
		},
		Data: map[string]string{ApplicationPropertiesFileName: ""},
	}
}

// CreateNewManagedPropsConfigMap creates a new ConfigMap object to hold the managed application properties of the workflos.
func CreateNewManagedPropsConfigMap(workflow *operatorapi.SonataFlow, properties string) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetWorkflowManagedPropertiesConfigMapName(workflow),
			Namespace: workflow.Namespace,
			Labels:    GetMergedLabels(workflow),
		},
		Data: map[string]string{GetManagedPropertiesFileName(workflow): properties},
	}
}

// SetWorkflowProfile adds the profile annotation to the workflow
func SetWorkflowProfile(workflow *operatorapi.SonataFlow, profile metadata.ProfileType) {
	if workflow.Annotations == nil {
		workflow.Annotations = map[string]string{}
	}
	workflow.Annotations[metadata.Profile] = string(profile)
}

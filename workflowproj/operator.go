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
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api/metadata"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
)

const (
	workflowConfigMapNameSuffix = "-props"
	// ApplicationPropertiesFileName is the default application properties file name
	ApplicationPropertiesFileName = "application.properties"
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

// GetWorkflowPropertiesConfigMapName gets the default ConfigMap name that holds the application property for the given workflow
func GetWorkflowPropertiesConfigMapName(workflow *operatorapi.SonataFlow) string {
	return workflow.Name + workflowConfigMapNameSuffix
}

// SetDefaultLabels adds the default workflow application labels to the given object.
// Overrides the defined labels.
func SetDefaultLabels(workflow *operatorapi.SonataFlow, object metav1.Object) {
	object.SetLabels(GetDefaultLabels(workflow))
}

// GetDefaultLabels gets the default labels based on the given workflow.
// You can use SetDefaultLabels that essentially does the same thing, if you don't need the labels explicitly.
func GetDefaultLabels(workflow *operatorapi.SonataFlow) map[string]string {
	return map[string]string{
		LabelApp:      workflow.Name,
		LabelWorkflow: workflow.Name,
	}
}

// CreateNewAppPropsConfigMap creates a new ConfigMap object to hold the workflow application properties.
func CreateNewAppPropsConfigMap(workflow *operatorapi.SonataFlow, properties string) *corev1.ConfigMap {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetWorkflowPropertiesConfigMapName(workflow),
			Namespace: workflow.Namespace,
			Labels:    GetDefaultLabels(workflow),
		},
		Data: map[string]string{ApplicationPropertiesFileName: properties},
	}
}

// SetWorkflowProfile adds the profile annotation to the workflow
func SetWorkflowProfile(workflow *operatorapi.SonataFlow, profile metadata.ProfileType) {
	if workflow.Annotations == nil {
		workflow.Annotations = map[string]string{}
	}
	workflow.Annotations[metadata.Profile] = string(profile)
}

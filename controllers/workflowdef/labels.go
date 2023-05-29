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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

const (
	// LabelApp key to use among object selectors
	LabelApp = "app"
)

// SetDefaultLabels adds the default workflow application labels to the given object.
// Overrides the defined labels.
func SetDefaultLabels(workflow *operatorapi.KogitoServerlessWorkflow, object metav1.Object) {
	object.SetLabels(GetDefaultLabels(workflow))
}

// GetDefaultLabels gets the default labels based on the given workflow.
// You can use SetDefaultLabels that essentially does the same thing, if you don't need the labels explicitly.
func GetDefaultLabels(workflow *operatorapi.KogitoServerlessWorkflow) map[string]string {
	return map[string]string{
		LabelApp: workflow.Name,
	}
}

// LabelSelector returns the label selector query using the default LabelApp label.
func LabelSelector(workflowName string) string {
	return LabelApp + ":" + workflowName
}

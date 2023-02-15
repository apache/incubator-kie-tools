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

package v1alpha08

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/container-builder/api"
)

// KogitoServerlessBuildSpec defines the desired state of KogitoServerlessBuild
type KogitoServerlessBuildSpec struct {
	// Workflow's unique identifier
	WorkflowId string `json:"workflowId,omitempty"`
	// Image name
	ImageName string `json:"imageName,omitempty"`
	// Middlename of the pod
	PodMiddleName string `json:"podMiddleName,omitempty"`
	// ContainerFile content used for the build
	ContainerFile []byte `json:"containerFile,omitempty"`
}

// KogitoServerlessBuildStatus defines the observed state of KogitoServerlessBuild
type KogitoServerlessBuildStatus struct {
	// Workflow's unique identifier
	WorkflowId string `json:"workflowId,omitempty"`
	// Current kaniko buildphase
	BuildPhase api.BuildPhase `json:"buildPhase,omitempty"`
	// Kaniko's build, used to ping the build to update the buildphase
	Builder api.Build `json:"builder,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// KogitoServerlessBuild is the Schema for the kogitoserverlessbuilds API
type KogitoServerlessBuild struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoServerlessBuildSpec   `json:"spec,omitempty"`
	Status KogitoServerlessBuildStatus `json:"status,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// KogitoServerlessBuildList is the Schema for the kogitoserverlessbuildsList API
type KogitoServerlessBuildList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KogitoServerlessBuild `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoServerlessBuild{}, &KogitoServerlessBuildList{})
}

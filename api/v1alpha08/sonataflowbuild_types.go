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
	"encoding/json"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
)

type BuildPhase string

const (
	// BuildPhaseNone --
	BuildPhaseNone BuildPhase = ""
	// BuildPhaseInitialization --
	BuildPhaseInitialization BuildPhase = "Initialization"
	// BuildPhaseScheduling --
	BuildPhaseScheduling BuildPhase = "Scheduling"
	// BuildPhasePending --
	BuildPhasePending BuildPhase = "Pending"
	// BuildPhaseRunning --
	BuildPhaseRunning BuildPhase = "Running"
	// BuildPhaseSucceeded --
	BuildPhaseSucceeded BuildPhase = "Succeeded"
	// BuildPhaseFailed --
	BuildPhaseFailed BuildPhase = "Failed"
	// BuildPhaseInterrupted --
	BuildPhaseInterrupted BuildPhase = "Interrupted"
	// BuildPhaseError --
	BuildPhaseError BuildPhase = "Error"
)

type BuildTemplate struct {
	// Timeout defines the Build maximum execution duration.
	// The Build deadline is set to the Build start time plus the Timeout duration.
	// If the Build deadline is exceeded, the Build context is canceled,
	// and its phase set to BuildPhaseFailed.
	// +kubebuilder:validation:Format=duration
	Timeout metav1.Duration `json:"timeout,omitempty"`
	// Resources optional compute resource requirements for the builder
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`
	// Arguments lists the command line arguments to send to the builder
	Arguments []string `json:"arguments,omitempty"`
}

// SonataFlowBuildSpec an abstraction over the actual build process performed by the platform.
type SonataFlowBuildSpec struct {
	BuildTemplate `json:",inline"`
}

// SonataFlowBuildStatus defines the observed state of SonataFlowBuild
type SonataFlowBuildStatus struct {
	// The final image tag produced by this build instance
	ImageTag string `json:"imageTag,omitempty"`
	// Current phase of the build
	BuildPhase BuildPhase `json:"buildPhase,omitempty"`
	// Last error found during build
	Error string `json:"error,omitempty"`
	// InnerBuild is a reference to an internal build object, which can be anything known only to internal builders.
	// +kubebuilder:pruning:PreserveUnknownFields
	InnerBuild runtime.RawExtension `json:"innerBuild,omitempty" patchStrategy:"replace"`
}

// SetInnerBuild use to define a new object pointer to the inner build.
func (k *SonataFlowBuildStatus) SetInnerBuild(innerBuilder interface{}) error {
	obj, err := json.Marshal(innerBuilder)
	if err != nil {
		return err
	}
	k.InnerBuild.Raw = obj
	return nil
}

// GetInnerBuild fetch into the given inner build the value from unstructured.
func (k *SonataFlowBuildStatus) GetInnerBuild(innerBuild interface{}) error {
	if len(k.InnerBuild.Raw) == 0 {
		return nil
	}
	if err := json.Unmarshal(k.InnerBuild.Raw, innerBuild); err != nil {
		return err
	}
	return nil
}

// SonataFlowBuild is the Schema for the sonataflowbuilds API
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:printcolumn:name="Image",type=string,JSONPath=`.status.imageTag`
// +kubebuilder:printcolumn:name="Phase",type=string,JSONPath=`.status.buildPhase`
// +kubebuilder:resource:shortName={"sfb", "sfbuild", "sfbuilds"}
type SonataFlowBuild struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   SonataFlowBuildSpec   `json:"spec,omitempty"`
	Status SonataFlowBuildStatus `json:"status,omitempty"`
}

// SonataFlowBuildList is the Schema for the sonataflowbuildsList API
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
type SonataFlowBuildList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []SonataFlowBuild `json:"items"`
}

func init() {
	SchemeBuilder.Register(&SonataFlowBuild{}, &SonataFlowBuildList{})
}

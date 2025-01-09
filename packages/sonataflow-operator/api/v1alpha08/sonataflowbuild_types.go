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

package v1alpha08

import (
	"encoding/json"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"
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

// BuildRestartAnnotation marks a SonataFlowBuild to restart
const BuildRestartAnnotation = metadata.Domain + "/restartBuild"

// BuildTemplate an abstraction over the actual build process performed by the platform.
// +k8s:openapi-gen=true
type BuildTemplate struct {
	// Timeout defines the Build maximum execution duration.
	// The Build deadline is set to the Build start time plus the Timeout duration.
	// If the Build deadline is exceeded, the Build context is canceled,
	// and its phase set to BuildPhaseFailed.
	// +kubebuilder:validation:Format=duration
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Timeout"
	Timeout metav1.Duration `json:"timeout,omitempty"`
	// Resources optional compute resource requirements for the builder
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Resources"
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`
	// Arguments lists the command line arguments to send to the internal builder command.
	// Depending on the build method you might set this attribute instead of BuildArgs.
	// For example: ".spec.arguments=verbose=3".
	// Please see the SonataFlow guides.
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Arguments"
	Arguments []string `json:"arguments,omitempty"`
	// Optional build arguments that can be set to the internal build (e.g. Docker ARG)
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="BuildArgs"
	BuildArgs []corev1.EnvVar `json:"buildArgs,omitempty"`
	// Optional environment variables to add to the internal build
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Envs"
	Envs []corev1.EnvVar `json:"envs,omitempty"`
}

// SonataFlowBuildSpec define the desired state of th SonataFlowBuild.
// +k8s:openapi-gen=true
type SonataFlowBuildSpec struct {
	BuildTemplate `json:",inline"`
}

// SonataFlowBuildStatus defines the observed state of SonataFlowBuild
// +k8s:openapi-gen=true
type SonataFlowBuildStatus struct {
	// ImageTag The final image tag produced by this build instance
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="ImageTag"
	ImageTag string `json:"imageTag,omitempty"`
	// BuildPhase Current phase of the build
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="BuildPhase"
	BuildPhase BuildPhase `json:"buildPhase,omitempty"`
	// Error Last error found during build
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="Error"
	Error string `json:"error,omitempty"`
	// InnerBuild is a reference to an internal build object, which can be anything known only to internal builders.
	// +kubebuilder:pruning:PreserveUnknownFields
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="InnerBuild"
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

// SonataFlowBuild is an internal custom resource to control workflow build instances in the target platform
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +k8s:openapi-gen=true
// +kubebuilder:printcolumn:name="Image",type=string,JSONPath=`.status.imageTag`
// +kubebuilder:printcolumn:name="Phase",type=string,JSONPath=`.status.buildPhase`
// +kubebuilder:resource:shortName={"sfb", "sfbuild", "sfbuilds"}
// +operator-sdk:csv:customresourcedefinitions:resources={{BuildConfig,build.openshift.io/v1,"An Openshift Build Config"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="SonataFlowBuild"
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

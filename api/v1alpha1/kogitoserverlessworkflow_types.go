/*
Copyright 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package v1alpha1

import (
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	knservingv1 "knative.dev/serving/pkg/apis/serving/v1"
)

type Constant struct {
}

type Timeout struct {
}

type Error struct {
}

type Auth struct {
}

type Event struct {
}

type Retry struct {
}

type State struct {
}

// EDIT THIS FILE!  THIS IS SCAFFOLDING FOR YOU TO OWN!
// NOTE: json tags are required.  Any new fields you add must have json tags for the fields to be serialized.

// KogitoServerlessWorkflowSpec defines the desired state of KogitoServerlessWorkflow
type KogitoServerlessWorkflowSpec struct {
	Constants   []Constant          `json:"conditions"`
	Secrets     *[]v1.Secret        `json:"secrets"`
	Start       string              `json:"start"`
	Timeouts    []Timeout           `json:"timeouts"`
	Errors      []Error             `json:"errors"`
	KeepAlive   bool                `json:"keepAlive"`
	Auth        Auth                `json:"auth"`
	Events      *[]Event            `json:"events"`
	Functions   knservingv1.Service `json:"functions"`
	AutoRetries bool                `json:"autoRetries"`
	Retries     Retry               `json:"retries"`
	States      State               `json:"states"`
}

type Endpoint struct {
	IP       string
	Port     int
	PortName string
	Protocol string // "TCP" or "UDP"; never empty
}

// KogitoServerlessWorkflowStatus defines the observed state of KogitoServerlessWorkflow
type KogitoServerlessWorkflowStatus struct {
	Conditions string             `json:"conditions,omitempty"`
	Endpoints  []Endpoint         `json:"endpoints"`
	Address    duckv1.Addressable `json:"address,omitempty"`
}

//+kubebuilder:object:root=true
//+kubebuilder:subresource:status

// KogitoServerlessWorkflow is the Schema for the kogitoserverlessworkflows API
type KogitoServerlessWorkflow struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoServerlessWorkflowSpec   `json:"spec,omitempty"`
	Status KogitoServerlessWorkflowStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// KogitoServerlessWorkflowList contains a list of KogitoServerlessWorkflow
type KogitoServerlessWorkflowList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KogitoServerlessWorkflow `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoServerlessWorkflow{}, &KogitoServerlessWorkflowList{})
}

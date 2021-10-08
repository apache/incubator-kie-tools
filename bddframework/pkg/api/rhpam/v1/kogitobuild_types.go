// Copyright 2020 Red Hat, Inc. and/or its affiliates
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

package v1

import (
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// +kubebuilder:object:root=true
// +k8s:openapi-gen=true
// +genclient
// +kubebuilder:resource:path=kogitobuilds,scope=Namespaced
// +kubebuilder:subresource:status
// +kubebuilder:printcolumn:name="Type",type="string",JSONPath=".spec.type",description="Type of this build instance"
// +kubebuilder:printcolumn:name="Runtime",type="string",JSONPath=".spec.runtime",description="Runtime used to build the service"
// +kubebuilder:printcolumn:name="Native",type="boolean",JSONPath=".spec.native",description="Indicates it's a native build"
// +kubebuilder:printcolumn:name="Maven URL",type="string",JSONPath=".spec.mavenMirrorURL",description="URL for the proxy Maven repository"
// +kubebuilder:printcolumn:name="Kogito Runtime",type="string",JSONPath=".spec.targetKogitoRuntime",description="Target KogitoRuntime for this build"
// +kubebuilder:printcolumn:name="Git Repository",type="string",JSONPath=".spec.gitSource.uri",description="Git repository URL (RemoteSource builds only)"
// +operator-sdk:csv:customresourcedefinitions:resources={{ImageStream,image.openshift.io/v1," A Openshift Image Stream"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{BuildConfig,build.openshift.io/v1," A Openshift Build Config"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="Kogito Build"

// KogitoBuild handles how to build a custom Kogito service in a Kubernetes/OpenShift cluster.
type KogitoBuild struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   v1beta1.KogitoBuildSpec   `json:"spec,omitempty"`
	Status v1beta1.KogitoBuildStatus `json:"status,omitempty"`
}

// GetSpec provide spec of Kogito Build
func (k *KogitoBuild) GetSpec() api.KogitoBuildSpecInterface {
	return &k.Spec
}

// GetStatus provide status of Kogito Build
func (k *KogitoBuild) GetStatus() api.KogitoBuildStatusInterface {
	return &k.Status
}

// +kubebuilder:object:root=true

// KogitoBuildList contains a list of KogitoBuild.
type KogitoBuildList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	// +listType=atomic
	Items []KogitoBuild `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoBuild{}, &KogitoBuildList{})
}

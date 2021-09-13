// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package v1beta1

import (
	"github.com/kiegroup/kogito-operator/apis"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// KogitoInfraSpec defines the desired state of KogitoInfra.
// +k8s:openapi-gen=true
type KogitoInfraSpec struct {
	// Add custom validation using kubebuilder tags: https://book-v1.book.kubebuilder.io/beyond_basics/generating_crd.html

	// Resource for the service. Example: Infinispan/Kafka/Keycloak.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	Resource *InfraResource `json:"resource,omitempty"`

	// +optional
	// +mapType=atomic
	// Optional properties which would be needed to setup correct runtime/service configuration, based on the resource type.
	//
	// For example, MongoDB will require `username` and `database` as properties for a correct setup, else it will fail
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	InfraProperties map[string]string `json:"infraProperties,omitempty"`

	// +optional
	// +listType=atomic
	// Environment variables to be added to the runtime container. Keys must be a C_IDENTIFIER.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	Envs []corev1.EnvVar `json:"envs,omitempty"`

	// +optional
	// +listType=atomic
	// List of secret that should be mounted to the services as envs
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	ConfigMapEnvFromReferences []string `json:"configMapEnvFromReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of configmap that should be added to the services bound to this infra instance
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	ConfigMapVolumeReferences []VolumeReference `json:"configMapVolumeReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of secret that should be mounted to the services as envs
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	SecretEnvFromReferences []string `json:"secretEnvFromReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of secret that should be munted to the services bound to this infra instance
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	SecretVolumeReferences []VolumeReference `json:"secretVolumeReferences,omitempty"`
}

// GetResource ...
func (k *KogitoInfraSpec) GetResource() api.ResourceInterface {
	return k.Resource
}

// IsResourceEmpty ...
func (k *KogitoInfraSpec) IsResourceEmpty() bool {
	return k.Resource == nil
}

// GetInfraProperties ...
func (k *KogitoInfraSpec) GetInfraProperties() map[string]string {
	return k.InfraProperties
}

// GetEnvs ...
func (k *KogitoInfraSpec) GetEnvs() []corev1.EnvVar {
	return k.Envs
}

// AddInfraProperties ...
func (k *KogitoInfraSpec) AddInfraProperties(infraProperties map[string]string) {
	ip := k.InfraProperties
	if ip == nil {
		ip = make(map[string]string)
	}
	for key, value := range infraProperties {
		ip[key] = value
	}
	k.InfraProperties = ip
}

// GetConfigMapEnvFromReferences ...
func (k *KogitoInfraSpec) GetConfigMapEnvFromReferences() []string {
	return k.ConfigMapEnvFromReferences
}

// GetConfigMapVolumeReferences ...
func (k *KogitoInfraSpec) GetConfigMapVolumeReferences() []api.VolumeReferenceInterface {
	newConfigMapVolumeReferences := make([]api.VolumeReferenceInterface, len(k.ConfigMapVolumeReferences))
	for i, v := range k.ConfigMapVolumeReferences {
		item := v
		newConfigMapVolumeReferences[i] = &item
	}
	return newConfigMapVolumeReferences
}

// GetSecretEnvFromReferences ...
func (k *KogitoInfraSpec) GetSecretEnvFromReferences() []string {
	return k.SecretEnvFromReferences
}

// GetSecretVolumeReferences ...
func (k *KogitoInfraSpec) GetSecretVolumeReferences() []api.VolumeReferenceInterface {
	newSecretVolumeReferences := make([]api.VolumeReferenceInterface, len(k.SecretVolumeReferences))
	for i, v := range k.SecretVolumeReferences {
		item := v
		newSecretVolumeReferences[i] = &item
	}
	return newSecretVolumeReferences
}

// KogitoInfraStatus defines the observed state of KogitoInfra.
// +k8s:openapi-gen=true
type KogitoInfraStatus struct {
	// +listType=atomic
	// History of conditions for the resource
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:io.kubernetes.conditions"
	Conditions *[]metav1.Condition `json:"conditions"`

	// +optional
	// +listType=atomic
	// Environment variables to be added to the runtime container. Keys must be a C_IDENTIFIER.
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Envs []corev1.EnvVar `json:"env,omitempty"`

	// +optional
	// +listType=atomic
	// List of Configmap that should be mounted to the services as envs
	// +operator-sdk:csv:customresourcedefinitions:type=status
	ConfigMapEnvFromReferences []string `json:"configMapEnvFromReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of configmap that should be added as volume mount to this infra instance
	// +operator-sdk:csv:customresourcedefinitions:type=status
	ConfigMapVolumeReferences []VolumeReference `json:"configMapVolumeReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of secret that should be mounted to the services as envs
	// +operator-sdk:csv:customresourcedefinitions:type=status
	SecretEnvFromReferences []string `json:"secretEnvFromReferences,omitempty"`

	// +optional
	// +listType=atomic
	// List of secret that should be added as volume mount to this infra instance
	// +operator-sdk:csv:customresourcedefinitions:type=status
	SecretVolumeReferences []VolumeReference `json:"secretVolumeReferences,omitempty"`
}

// GetConditions ...
func (k *KogitoInfraStatus) GetConditions() *[]metav1.Condition {
	return k.Conditions
}

// SetConditions ...
func (k *KogitoInfraStatus) SetConditions(conditions *[]metav1.Condition) {
	k.Conditions = conditions
}

// GetEnvs ...
func (k *KogitoInfraStatus) GetEnvs() []corev1.EnvVar {
	return k.Envs
}

// SetEnvs ...
func (k *KogitoInfraStatus) SetEnvs(envs []corev1.EnvVar) {
	k.Envs = envs
}

// AddEnvs ...
func (k *KogitoInfraStatus) AddEnvs(envs []corev1.EnvVar) {
	k.Envs = append(k.Envs, envs...)
}

// GetConfigMapEnvFromReferences ...
func (k *KogitoInfraStatus) GetConfigMapEnvFromReferences() []string {
	return k.ConfigMapEnvFromReferences
}

// SetConfigMapEnvFromReferences ...
func (k *KogitoInfraStatus) SetConfigMapEnvFromReferences(configMapEnvFromReferences []string) {
	k.ConfigMapEnvFromReferences = configMapEnvFromReferences
}

// AddConfigMapEnvFromReferences ...
func (k *KogitoInfraStatus) AddConfigMapEnvFromReferences(cmName string) {
	k.ConfigMapEnvFromReferences = append(k.ConfigMapEnvFromReferences, cmName)
}

// GetConfigMapVolumeReferences ...
func (k *KogitoInfraStatus) GetConfigMapVolumeReferences() []api.VolumeReferenceInterface {
	newConfigMapVolumeReferences := make([]api.VolumeReferenceInterface, len(k.ConfigMapVolumeReferences))
	for i, v := range k.ConfigMapVolumeReferences {
		item := v
		newConfigMapVolumeReferences[i] = &item
	}
	return newConfigMapVolumeReferences
}

// SetConfigMapVolumeReferences ...
func (k *KogitoInfraStatus) SetConfigMapVolumeReferences(configMapVolumeReferences []api.VolumeReferenceInterface) {
	var newConfigMapVolumeReferences []VolumeReference
	for _, produce := range configMapVolumeReferences {
		if newProduce, ok := produce.(*VolumeReference); ok {
			newConfigMapVolumeReferences = append(newConfigMapVolumeReferences, *newProduce)
		}
	}
	k.ConfigMapVolumeReferences = newConfigMapVolumeReferences
}

// AddConfigMapVolumeReference ...
func (k *KogitoInfraStatus) AddConfigMapVolumeReference(name string, mountPath string, fileMode *int32, optional *bool) {
	volumeReference := VolumeReference{
		Name:      name,
		MountPath: mountPath,
		FileMode:  fileMode,
		Optional:  optional,
	}
	k.ConfigMapVolumeReferences = append(k.ConfigMapVolumeReferences, volumeReference)
}

// GetSecretEnvFromReferences ...
func (k *KogitoInfraStatus) GetSecretEnvFromReferences() []string {
	return k.SecretEnvFromReferences
}

// SetSecretEnvFromReferences ...
func (k *KogitoInfraStatus) SetSecretEnvFromReferences(secretEnvFromReferences []string) {
	k.SecretEnvFromReferences = secretEnvFromReferences
}

// AddSecretEnvFromReferences ...
func (k *KogitoInfraStatus) AddSecretEnvFromReferences(cmName string) {
	k.SecretEnvFromReferences = append(k.SecretEnvFromReferences, cmName)
}

// GetSecretVolumeReferences ...
func (k *KogitoInfraStatus) GetSecretVolumeReferences() []api.VolumeReferenceInterface {
	newSecretVolumeReferences := make([]api.VolumeReferenceInterface, len(k.SecretVolumeReferences))
	for i, v := range k.SecretVolumeReferences {
		item := v
		newSecretVolumeReferences[i] = &item
	}
	return newSecretVolumeReferences
}

// SetSecretVolumeReferences ...
func (k *KogitoInfraStatus) SetSecretVolumeReferences(secretVolumeReferences []api.VolumeReferenceInterface) {
	var newSecretVolumeReferences []VolumeReference
	for _, produce := range secretVolumeReferences {
		if newProduce, ok := produce.(*VolumeReference); ok {
			newSecretVolumeReferences = append(newSecretVolumeReferences, *newProduce)
		}
	}
	k.SecretVolumeReferences = newSecretVolumeReferences
}

// AddSecretVolumeReference ...
func (k *KogitoInfraStatus) AddSecretVolumeReference(name string, mountPath string, fileMode *int32, optional *bool) {
	volumeReference := VolumeReference{
		Name:      name,
		MountPath: mountPath,
		FileMode:  fileMode,
		Optional:  optional,
	}
	k.SecretVolumeReferences = append(k.SecretVolumeReferences, volumeReference)
}

// InfraResource provide reference infra resource
type InfraResource struct {

	// APIVersion describes the API Version of referred Kubernetes resource for example, infinispan.org/v1
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="APIVersion"
	APIVersion string `json:"apiVersion"`

	// Kind describes the kind of referred Kubernetes resource for example, Infinispan
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Kind"
	Kind string `json:"kind"`

	// +optional
	// Namespace where referred resource exists.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Namespace"
	Namespace string `json:"namespace,omitempty"`

	// Name of referred resource.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Name"
	Name string `json:"name"`
}

// GetAPIVersion ...
func (r *InfraResource) GetAPIVersion() string {
	return r.APIVersion
}

// SetAPIVersion ...
func (r *InfraResource) SetAPIVersion(apiVersion string) {
	r.APIVersion = apiVersion
}

// GetKind ...
func (r *InfraResource) GetKind() string {
	return r.Kind
}

// SetKind ...
func (r *InfraResource) SetKind(kind string) {
	r.Kind = kind
}

// GetNamespace ...
func (r *InfraResource) GetNamespace() string {
	return r.Namespace
}

// SetNamespace ...
func (r *InfraResource) SetNamespace(namespace string) {
	r.Namespace = namespace
}

// GetName ...
func (r *InfraResource) GetName() string {
	return r.Name
}

// SetName ...
func (r *InfraResource) SetName(name string) {
	r.Name = name
}

// +kubebuilder:object:root=true
// +k8s:openapi-gen=true
// +genclient
// +groupName=app.kiegroup.org
// +groupGoName=Kogito
// +kubebuilder:resource:path=kogitoinfras,scope=Namespaced
// +kubebuilder:subresource:status
// +kubebuilder:printcolumn:name="Resource Name",type="string",JSONPath=".spec.resource.name",description="Third Party Infrastructure Resource"
// +kubebuilder:printcolumn:name="Kind",type="string",JSONPath=".spec.resource.kind",description="Kubernetes CR Kind"
// +kubebuilder:printcolumn:name="API Version",type="string",JSONPath=".spec.resource.apiVersion",description="Kubernetes CR API Version"
// +kubebuilder:printcolumn:name="Status",type="string",JSONPath=".status.condition.status",description="General Status of this resource bind"
// +kubebuilder:printcolumn:name="Reason",type="string",JSONPath=".status.condition.reason",description="Status reason"
// +operator-sdk:csv:customresourcedefinitions:displayName="Kogito Infra"
// +operator-sdk:csv:customresourcedefinitions:resources={{Kafka,kafka.strimzi.io/v1beta2,"A Kafka instance"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Infinispan,infinispan.org/v1,"A Infinispan instance"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Keycloak,keycloak.org/v1alpha1,"A Keycloak Instance"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Secret,v1,"A Kubernetes Secret"}}

// KogitoInfra is the resource to bind a Custom Resource (CR) not managed by Kogito Operator to a given deployed Kogito service.
//
// It holds the reference of a CR managed by another operator such as Strimzi. For example: one can create a Kafka CR via Strimzi
// and link this resource using KogitoInfra to a given Kogito service (custom or supporting, such as Data Index).
//
// Please refer to the Kogito Operator documentation (https://docs.jboss.org/kogito/release/latest/html_single/) for more information.
type KogitoInfra struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KogitoInfraSpec   `json:"spec,omitempty"`
	Status KogitoInfraStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// KogitoInfraList contains a list of KogitoInfra.
type KogitoInfraList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KogitoInfra `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KogitoInfra{}, &KogitoInfraList{})
}

// GetSpec provide spec of Kogito infra
func (k *KogitoInfra) GetSpec() api.KogitoInfraSpecInterface {
	return &k.Spec
}

// GetStatus provide status of Kogito infra
func (k *KogitoInfra) GetStatus() api.KogitoInfraStatusInterface {
	return &k.Status
}

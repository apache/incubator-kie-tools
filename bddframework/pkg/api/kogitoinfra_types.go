// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package api

import (
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// KogitoInfraConditionType ...
type KogitoInfraConditionType string

const (
	// KogitoInfraConfigured ...
	KogitoInfraConfigured KogitoInfraConditionType = "Configured"
)

// KogitoInfraConditionReason describes the reasons for reconciliation failure
type KogitoInfraConditionReason string

const (
	// ReconciliationFailure generic failure on reconciliation
	ReconciliationFailure KogitoInfraConditionReason = "ReconciliationFailure"
	// ResourceNotFound target resource not found
	ResourceNotFound KogitoInfraConditionReason = "ResourceNotFound"
	// ResourceAPINotFound API not available in the cluster
	ResourceAPINotFound KogitoInfraConditionReason = "ResourceAPINotFound"
	// UnsupportedAPIKind API defined in the KogitoInfra CR not supported
	UnsupportedAPIKind KogitoInfraConditionReason = "UnsupportedAPIKind"
	// ResourceNotReady related resource is not ready
	ResourceNotReady KogitoInfraConditionReason = "ResourceNotReady"
	// ResourceConfigError related resource is not configured properly
	ResourceConfigError KogitoInfraConditionReason = "ResourceConfigError"
	// ResourceMissingResourceConfig related resource is missing a config information to continue
	ResourceMissingResourceConfig KogitoInfraConditionReason = "ResourceMissingConfig"
	// ResourceSuccessfullyConfigured ..
	ResourceSuccessfullyConfigured KogitoInfraConditionReason = "ResourceSuccessfullyConfigured"
)

// KogitoInfraInterface ...
type KogitoInfraInterface interface {
	client.Object
	// GetSpec gets the Kogito Service specification structure.
	GetSpec() KogitoInfraSpecInterface
	// GetStatus gets the Kogito Service Status structure.
	GetStatus() KogitoInfraStatusInterface
}

// KogitoInfraSpecInterface ...
type KogitoInfraSpecInterface interface {
	GetResource() ResourceInterface
	IsResourceEmpty() bool
	GetInfraProperties() map[string]string
	AddInfraProperties(infraProperties map[string]string)
	GetEnvs() []v1.EnvVar
	GetConfigMapEnvFromReferences() []string
	GetConfigMapVolumeReferences() []VolumeReferenceInterface
	GetSecretEnvFromReferences() []string
	GetSecretVolumeReferences() []VolumeReferenceInterface
}

// ResourceInterface ...
type ResourceInterface interface {
	GetAPIVersion() string
	SetAPIVersion(apiVersion string)
	GetKind() string
	SetKind(kind string)
	GetNamespace() string
	SetNamespace(namespace string)
	GetName() string
	SetName(name string)
}

// KogitoInfraStatusInterface ...
type KogitoInfraStatusInterface interface {
	GetConditions() *[]metav1.Condition
	SetConditions(conditions *[]metav1.Condition)
	GetEnvs() []v1.EnvVar
	SetEnvs(envs []v1.EnvVar)
	AddEnvs(envs []v1.EnvVar)
	GetConfigMapEnvFromReferences() []string
	SetConfigMapEnvFromReferences(configMapEnvFromReferences []string)
	AddConfigMapEnvFromReferences(cmName string)
	GetConfigMapVolumeReferences() []VolumeReferenceInterface
	SetConfigMapVolumeReferences(volumeReferences []VolumeReferenceInterface)
	AddConfigMapVolumeReference(name string, mountPath string, fileMode *int32, optional *bool)
	GetSecretEnvFromReferences() []string
	SetSecretEnvFromReferences(secretEnvFromReferences []string)
	AddSecretEnvFromReferences(cmName string)
	GetSecretVolumeReferences() []VolumeReferenceInterface
	SetSecretVolumeReferences(volumeReferences []VolumeReferenceInterface)
	AddSecretVolumeReference(name string, mountPath string, fileMode *int32, optional *bool)
}

// RuntimePropertiesMap defines the map that KogitoInfraStatus
// will use to link the runtime to their variables.
type RuntimePropertiesMap map[RuntimeType]RuntimePropertiesInterface

// RuntimePropertiesInterface ...
type RuntimePropertiesInterface interface {
	GetAppProps() map[string]string
	GetEnv() []v1.EnvVar
}

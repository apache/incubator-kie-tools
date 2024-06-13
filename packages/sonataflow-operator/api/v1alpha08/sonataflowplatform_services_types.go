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

// ServicesPlatformSpec describes the desired service configuration for workflows without the `sonataflow.org/profile: dev` annotation.
type ServicesPlatformSpec struct {
	// Deploys the Data Index service for use by workflows without the `sonataflow.org/profile: dev` annotation.
	// +optional
	DataIndex *ServiceSpec `json:"dataIndex,omitempty"`
	// Deploys the Job service for use by workflows without the `sonataflow.org/profile: dev` annotation.
	// +optional
	JobService *ServiceSpec `json:"jobService,omitempty"`
}

// ServiceSpec defines the desired state of a platform service
// +k8s:openapi-gen=true
type ServiceSpec struct {
	// Determines whether workflows without the `sonataflow.org/profile: dev` annotation should be configured to use this service
	// +optional
	Enabled *bool `json:"enabled,omitempty"`
	// Persists service to a datasource of choice. Ephemeral by default.
	// +optional
	Persistence *PersistenceOptionsSpec `json:"persistence,omitempty"`
	// PodTemplate describes the deployment details of this platform service instance.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="podTemplate"
	PodTemplate PodTemplateSpec `json:"podTemplate,omitempty"`
}

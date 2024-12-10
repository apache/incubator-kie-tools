// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package v1alpha08

import (
	duckv1 "knative.dev/pkg/apis/duck/v1"
)

// ServicesPlatformSpec describes the desired service configuration for workflows without the `sonataflow.org/profile: dev` annotation.
type ServicesPlatformSpec struct {
	// Deploys the Data Index service for use by workflows without the `sonataflow.org/profile: dev` annotation.
	// +optional
	DataIndex *DataIndexServiceSpec `json:"dataIndex,omitempty"`
	// Deploys the Job service for use by workflows without the `sonataflow.org/profile: dev` annotation.
	// +optional
	JobService *JobServiceServiceSpec `json:"jobService,omitempty"`
}

// DataIndexServiceSpec defines the desired state of Dataindex service
// +k8s:openapi-gen=true
type DataIndexServiceSpec struct {
	// Defines the common spec of a platform service
	ServiceSpec `json:",inline"`
	// Defines the source where the Dataindex receives events from
	// +optional
	Source *duckv1.Destination `json:"source,omitempty"`
}

// JobServiceServiceSpec defines the desired state of Jobservice service
// +k8s:openapi-gen=true
type JobServiceServiceSpec struct {
	// Defines the common spec of a platform service
	ServiceSpec `json:",inline"`
	// Defines the sink where the Jobservice sends events to
	// +optional
	Sink *duckv1.Destination `json:"sink,omitempty"`
	// Defines the source where the Jobservice receives events from
	// +optional
	Source *duckv1.Destination `json:"source,omitempty"`
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

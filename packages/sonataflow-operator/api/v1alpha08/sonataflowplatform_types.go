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
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
)

const (
	// SonataFlowPlatformKind is the Kind name of the SonataFlowPlatform CR
	SonataFlowPlatformKind string = "SonataFlowPlatform"
)

// SonataFlowPlatformSpec defines the desired state of SonataFlowPlatform
// +k8s:openapi-gen=true
type SonataFlowPlatformSpec struct {
	// Build Attributes for building workflows in the target platform
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Build"
	Build BuildPlatformSpec `json:"build,omitempty"`
	// DevMode Attributes for running workflows in devmode (immutable, no build required)
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="DevMode"
	DevMode DevModePlatformSpec `json:"devMode,omitempty"`
	// Services attributes for deploying supporting applications like Data Index & Job Service.
	// Only workflows without the `sonataflow.org/profile: dev` annotation will be configured to use these service(s).
	// Setting this will override the use of any cluster-scoped services that might be defined via `SonataFlowClusterPlatform`.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Services"
	Services *ServicesPlatformSpec `json:"services,omitempty"`
	// Eventing describes the information required for Knative Eventing integration in the platform.
	// +optional
	Eventing *PlatformEventingSpec `json:"eventing,omitempty"`
	// Persistence defines the platform persistence configuration. When this field is set,
	// the configuration is used as the persistence for platform services and SonataFlow instances
	// that don't provide one of their own.
	// +optional
	Persistence *PlatformPersistenceOptionsSpec `json:"persistence,omitempty"`
	// Properties defines the property set for a given actor in the current context.
	// For example, the workflow managed properties. One can define here a set of properties for SonataFlow deployments
	// that will be reused across every workflow deployment.
	//
	// These properties MAY NOT be propagated to a SonataFlowClusterPlatform since PropertyVarSource can only refer local context sources.
	// +optional
	Properties *PropertyPlatformSpec `json:"properties,omitempty"`
	// Settings for Prometheus monitoring
	// +optional
	Monitoring *PlatformMonitoringOptionsSpec `json:"monitoring,omitempty"`
}

// PlatformEventingSpec specifies the Knative Eventing integration details in the platform.
// +k8s:openapi-gen=true
type PlatformEventingSpec struct {
	// Broker to communicate with workflow deployment.  It can be the default broker when the workflow, Dataindex, or Jobservice does not have a sink or source specified.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="broker"
	Broker *duckv1.Destination `json:"broker,omitempty"`
}

// PlatformMonitoringOptionsSpec specifies the settings for monitoring
// +k8s:openapi-gen=true
type PlatformMonitoringOptionsSpec struct {
	// Enabled indicates whether monitoring with Prometheus metrics is enabled
	// +optional
	// +default: false
	Enabled bool `json:"enabled,omitempty"`
}

// PlatformCluster is the kind of orchestration cluster the platform is installed into
// +kubebuilder:validation:Enum=kubernetes;openshift
type PlatformCluster string

const (
	// PlatformClusterOpenShift is used when targeting an OpenShift cluster
	PlatformClusterOpenShift PlatformCluster = "openshift"
	// PlatformClusterKubernetes is used when targeting a Kubernetes cluster
	PlatformClusterKubernetes PlatformCluster = "kubernetes"
)

const (
	PlatformCreatingReason   = "Creating"
	PlatformWarmingReason    = "Warming"
	PlatformFailureReason    = "Failure"
	PlatformDuplicatedReason = "Duplicated"
)

type DBMigrationStatus string

const (
	DBMigrationStatusStarted    DBMigrationStatus = "Started"
	DBMigrationStatusInProgress DBMigrationStatus = "In-Progress"
	DBMigrationStatusSucceeded  DBMigrationStatus = "Succeeded"
	DBMigrationStatusFailed     DBMigrationStatus = "Failed"

	MessageDBMigrationStatusStarted    string = "Started the database migrations for the services"
	MessageDBMigrationStatusInProgress string = "The database migrations for the services are in-progress"
	MessageDBMigrationStatusSucceeded  string = "The database migrations for the services are successful"
	MessageDBMigrationStatusFailed     string = "The database migrations for the services have failed"

	ReasonDBMigrationStatusStarted    string = "Started by SonataFlow operator"
	ReasonDBMigrationStatusInProgress string = "The database migration job is in-progress"
	ReasonDBMigrationStatusSucceeded  string = "The database migration job completed as expected"
	ReasonDBMigrationStatusFailed     string = "The database may be unreachable, invalid credentials supplied or flyway migration failed. Please check logs for further details."
)

type SonataFlowPlatformDBMigrationPhase struct {
	Status  DBMigrationStatus `json:"dbMigrationStatus,omitempty"`
	Message string            `json:"message,omitempty"`
	Reason  string            `json:"reason,omitempty"`
}

// SonataFlowPlatformStatus defines the observed state of SonataFlowPlatform
// +k8s:openapi-gen=true
type SonataFlowPlatformStatus struct {
	api.Status `json:",inline"`
	// Cluster what kind of cluster you're running (ie, plain Kubernetes or OpenShift)
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="cluster"
	Cluster PlatformCluster `json:"cluster,omitempty"`
	// Version the operator version controlling this Platform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="version"
	Version string `json:"version,omitempty"`
	// Info generic information related to the Platform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="info"
	Info map[string]string `json:"info,omitempty"`
	// ClusterPlatformRef information related to the (optional) active SonataFlowClusterPlatform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="clusterPlatformRef"
	ClusterPlatformRef *SonataFlowClusterPlatformRefStatus `json:"clusterPlatformRef,omitempty"`
	// Triggers list of triggers created for the SonataFlowPlatform
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="triggers"
	Triggers []SonataFlowPlatformTriggerRef `json:"triggers,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="dbMigrationStatus"
	SonataFlowPlatformDBMigrationPhase *SonataFlowPlatformDBMigrationPhase `json:"sonataFlowPlatformDBMigrationPhase,omitempty"`
}

// SonataFlowPlatformTriggerRef defines a trigger created for the SonataFlowPlatform.
type SonataFlowPlatformTriggerRef struct {
	// Name of the Trigger
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Trigger_Name"
	Name string `json:"name"`
	// Namespace of the Trigger
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Trigger_NS"
	Namespace string `json:"namespace"`
}

// SonataFlowClusterPlatformRefStatus information related to the (optional) active SonataFlowClusterPlatform
// +k8s:openapi-gen=true
type SonataFlowClusterPlatformRefStatus struct {
	// Name of the active SonataFlowClusterPlatform
	Name string `json:"name,omitempty"`
	// PlatformRef displays which SonataFlowPlatform has been referenced by the active SonataFlowClusterPlatform
	PlatformRef SonataFlowPlatformRef `json:"platformRef,omitempty"`
	// Services displays which cluster-wide services are being used by this SonataFlowPlatform
	Services *PlatformServicesStatus `json:"services,omitempty"`
}

// PlatformServicesStatus displays which cluster-wide services are being used by a SonataFlowPlatform or SonataFlow
// +k8s:openapi-gen=true
type PlatformServicesStatus struct {
	// DataIndexRef displays information on the cluster-wide Data Index service
	DataIndexRef *PlatformServiceRefStatus `json:"dataIndexRef,omitempty"`
	// JobServiceRef displays information on the cluster-wide Job Service
	JobServiceRef *PlatformServiceRefStatus `json:"jobServiceRef,omitempty"`
}

// PlatformServiceRefStatus displays information on a cluster-wide service
// +k8s:openapi-gen=true
type PlatformServiceRefStatus struct {
	// Url displays the base url of the service
	Url string `json:"url,omitempty"`
}

func (in *SonataFlowPlatformStatus) GetTopLevelConditionType() api.ConditionType {
	return api.SucceedConditionType
}

func (in *SonataFlowPlatformStatus) IsReady() bool {
	return in.GetTopLevelCondition().IsTrue()
}

func (in *SonataFlowPlatformStatus) GetTopLevelCondition() *api.Condition {
	return in.GetCondition(in.GetTopLevelConditionType())
}

func (in *SonataFlowPlatformStatus) Manager() api.ConditionsManager {
	return api.NewConditionManager(in, api.SucceedConditionType)
}

func (in *SonataFlowPlatformStatus) IsCreating() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformCreatingReason
}

func (in *SonataFlowPlatformStatus) IsWarming() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformWarmingReason
}

func (in *SonataFlowPlatformStatus) IsDuplicated() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformDuplicatedReason
}

func (in *SonataFlowPlatformStatus) IsFailure() bool {
	cond := in.GetTopLevelCondition()
	return cond.IsFalse() && cond.Reason == PlatformFailureReason
}

// SonataFlowPlatform is the descriptor for the workflow platform infrastructure.
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:shortName={"sfp", "sfplatform", "sfplatforms"}
// +kubebuilder:printcolumn:name="Cluster",type=string,JSONPath=`.status.cluster`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].status`
// +kubebuilder:printcolumn:name="Reason",type=string,JSONPath=`.status.conditions[?(@.type=='Succeed')].reason`
// +operator-sdk:csv:customresourcedefinitions:resources={{Namespace,v1,"The Namespace controlled by the platform"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="SonataFlowPlatform"
type SonataFlowPlatform struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   SonataFlowPlatformSpec   `json:"spec,omitempty"`
	Status SonataFlowPlatformStatus `json:"status,omitempty"`
}

// SonataFlowPlatformList contains a list of SonataFlowPlatform
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
type SonataFlowPlatformList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []SonataFlowPlatform `json:"items"`
}

func init() {
	SchemeBuilder.Register(&SonataFlowPlatform{}, &SonataFlowPlatformList{})
}

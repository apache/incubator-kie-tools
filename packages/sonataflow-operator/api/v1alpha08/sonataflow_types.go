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
	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"knative.dev/pkg/apis"
	duckv1 "knative.dev/pkg/apis/duck/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
)

const DefaultContainerName = "workflow"

// DeploymentModel defines how a given pod will be deployed
// +kubebuilder:validation:Enum=kubernetes;knative
type DeploymentModel string

const (
	// KubernetesDeploymentModel defines a PodSpec to be deployed as a regular Kubernetes Deployment
	KubernetesDeploymentModel DeploymentModel = "kubernetes"
	// KnativeDeploymentModel defines a PodSpec to be deployed as a Knative Serving Service
	KnativeDeploymentModel DeploymentModel = "knative"
)

// FlowPodTemplateSpec is a special PodTemplateSpec designed for SonataFlow deployments
type FlowPodTemplateSpec struct {
	// Container is the Kubernetes container where the application should run.
	// One can change this attribute in order to override the defaults provided by the operator.
	// +optional
	Container ContainerSpec `json:"container,omitempty"`
	// +optional
	PodSpec `json:",inline"`
	// +optional
	// Replicas define the number of pods to start by default for this deployment model. Ignored in "knative" deployment model.
	Replicas *int32 `json:"replicas,omitempty"`
	// Defines the kind of deployment model for this pod spec. In dev profile, only "kubernetes" is valid.
	// +optional
	DeploymentModel DeploymentModel `json:"deploymentModel,omitempty"`
}

// Flow describes the contents of the Workflow definition following the CNCF Serverless Workflow Specification.
// The attributes not part of the flow are defined by the Custom Resource metadata information, as follows:
//
// - Id, name, and key are replaced by the Custom Resource's name. Must follow the Kubernetes naming patterns (RFC1123).
//
// - Description can be added in the CR's annotation field sonataflow.org/description
//
// - Version is also defined in the CR's annotation, field sonataflow.org/version
//
// - SpecVersion is in the CR's apiVersion, for example v1alpha08 means that it follows the specification version 0.8.
type Flow struct {
	// Workflow start definition.
	// +kubebuilder:validation:Schemaless
	// +kubebuilder:pruning:PreserveUnknownFields
	// +optional
	Start *cncfmodel.Start `json:"start,omitempty"`
	// Annotations List of helpful terms describing the workflows intended purpose, subject areas, or other important
	// qualities.
	// +optional
	Annotations []string `json:"annotations,omitempty"`
	// DataInputSchema URI of the JSON Schema used to validate the workflow data input
	// +optional
	DataInputSchema *cncfmodel.DataInputSchema `json:"dataInputSchema,omitempty"`
	// Secrets allow you to access sensitive information, such as passwords, OAuth tokens, ssh keys, etc,
	// inside your Workflow Expressions.
	// +optional
	Secrets cncfmodel.Secrets `json:"secrets,omitempty"`
	// Constants Workflow constants are used to define static, and immutable, data which is available to
	// Workflow Expressions.
	// +optional
	Constants *cncfmodel.Constants `json:"constants,omitempty"`
	// Defines the workflow default timeout settings.
	// +optional
	Timeouts *cncfmodel.Timeouts `json:"timeouts,omitempty"`
	// Defines checked errors that can be explicitly handled during workflow execution.
	// +optional
	Errors cncfmodel.Errors `json:"errors,omitempty"`
	// If "true", workflow instances is not terminated when there are no active execution paths.
	// Instance can be terminated with "terminate end definition" or reaching defined "workflowExecTimeout"
	// +optional
	KeepActive bool `json:"keepActive,omitempty"`
	// Metadata custom information shared with the runtime.
	// +kubebuilder:validation:Schemaless
	// +kubebuilder:pruning:PreserveUnknownFields
	// +optional
	Metadata cncfmodel.Metadata `json:"metadata,omitempty"`
	// AutoRetries If set to true, actions should automatically be retried on unchecked errors. Default is false
	// +optional
	AutoRetries bool `json:"autoRetries,omitempty"`
	// Auth definitions can be used to define authentication information that should be applied to resources defined
	// in the operation property of function definitions. It is not used as authentication information for the
	// function invocation, but just to access the resource containing the function invocation information.
	// +kubebuilder:validation:Schemaless
	// +kubebuilder:pruning:PreserveUnknownFields
	// +optional
	Auth cncfmodel.Auths `json:"auth,omitempty" validate:"omitempty"`
	// +kubebuilder:validation:MinItems=1
	// +kubebuilder:pruning:PreserveUnknownFields
	States []cncfmodel.State `json:"states" validate:"required,min=1,dive"`
	// +optional
	Events cncfmodel.Events `json:"events,omitempty"`
	// +optional
	Functions cncfmodel.Functions `json:"functions,omitempty"`
	// +optional
	Retries cncfmodel.Retries `json:"retries,omitempty" validate:"dive"`
}

// WorkflowResources collection of local objects holding workflow resources, such as OpenAPI files
// that will be mounted in the workflow application.
type WorkflowResources struct {
	ConfigMaps []ConfigMapWorkflowResource `json:"configMaps,omitempty"`
}

// ConfigMapWorkflowResource ConfigMap local reference holding one or more workflow resources, such as OpenAPI files
// that will be mounted in the workflow application.
type ConfigMapWorkflowResource struct {
	// ConfigMap the given configMap name in the same workflow context to find the resource
	// +kubebuilder:validation:Required
	ConfigMap corev1.LocalObjectReference `json:"configMap"`
	// WorkflowPath path relative to the workflow application root file system within the pod (/<application path>/src/main/resources).
	// Starting trailing slashes will be removed.
	WorkflowPath string `json:"workflowPath,omitempty"`
}

// SonataFlowSpec defines the desired state of SonataFlow
// +k8s:openapi-gen=true
type SonataFlowSpec struct {
	// Flow the workflow definition.
	// +kubebuilder:validation:Required
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="flow"
	Flow Flow `json:"flow"`
	// Resources workflow resources that are linked to this workflow definition.
	// For example, a collection of OpenAPI specification files.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="resources"
	Resources WorkflowResources `json:"resources,omitempty"`
	// PodTemplate describes the deployment details of this SonataFlow instance.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="podTemplate"
	PodTemplate FlowPodTemplateSpec `json:"podTemplate,omitempty"`
	// Persistence defines the database persistence configuration for the workflow
	Persistence *PersistenceOptionsSpec `json:"persistence,omitempty"`
	// Sink describes the sinkBinding details of this SonataFlow instance.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="sink"
	Sink *duckv1.Destination `json:"sink,omitempty"`
	// Sources describes the list of sources used to create triggers for events consumed by this SonataFlow instance.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="sources"
	Sources []SonataFlowSourceSpec `json:"sources,omitempty"`
}

// SonataFlowSourceSpec defines the desired state of a source used for trigger creation
// +k8s:openapi-gen=true
type SonataFlowSourceSpec struct {
	// Defines the eventType to filter the events
	EventType string `json:"eventType"`
	// Defines the broker used
	duckv1.Destination `json:",inline"`
}

// SonataFlowStatus defines the observed state of SonataFlow
// +k8s:openapi-gen=true
type SonataFlowStatus struct {
	api.Status `json:",inline"`
	// Address is used as a part of Addressable interface (status.address.url) for knative
	// +optional
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="address"
	Address duckv1.Addressable `json:"address,omitempty"`
	// keeps track of how many failure recovers a given workflow had so far
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="recoverFailureAttempts"
	RecoverFailureAttempts int `json:"recoverFailureAttempts,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="lastTimeRecoverAttempt"
	LastTimeRecoverAttempt metav1.Time `json:"lastTimeRecoverAttempt,omitempty"`
	// Endpoint is an externally accessible URL of the workflow
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="endpoint"
	Endpoint *apis.URL `json:"endpoint,omitempty"`
	// Services displays which platform services are being used by this workflow
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="services"
	Services *PlatformServicesStatus `json:"services,omitempty"`
	// Platform displays which platform is being used by this workflow
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="platform"
	Platform *SonataFlowPlatformRef `json:"platform,omitempty"`
	// Triggers list of triggers created for the SonataFlow
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="triggers"
	Triggers []SonataFlowTriggerRef `json:"triggers,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="flowRevision"
	FlowCRC uint32 `json:"flowCRC,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="finalizerAttempts"
	FinalizerAttempts int `json:"finalizerAttempts,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="finalizerSucceed"
	FinalizerSucceed bool `json:"finalizerSucceed,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="lastTimeFinalizerAttempt"
	LastTimeFinalizerAttempt *metav1.Time `json:"lastTimeFinalizerAttempt,omitempty"`
	//+operator-sdk:csv:customresourcedefinitions:type=status,displayName="lastTimeStatusNotified"
	LastTimeStatusNotified *metav1.Time `json:"lastTimeStatusNotified,omitempty"`
}

// SonataFlowTriggerRef defines a trigger created for the SonataFlow.
type SonataFlowTriggerRef struct {
	// Name of the Trigger
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Trigger_Name"
	Name string `json:"name"`
	// Namespace of the Trigger
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Trigger_NS"
	Namespace string `json:"namespace"`
}

func (s *SonataFlowStatus) GetTopLevelConditionType() api.ConditionType {
	return api.RunningConditionType
}

func (s *SonataFlowStatus) IsReady() bool {
	return s.GetTopLevelCondition().IsTrue()
}

func (s *SonataFlowStatus) GetTopLevelCondition() *api.Condition {
	return s.GetCondition(s.GetTopLevelConditionType())
}

func (s *SonataFlowStatus) Manager() api.ConditionsManager {
	return api.NewConditionManager(s, api.RunningConditionType, api.BuiltConditionType)
}

func (s *SonataFlowStatus) IsWaitingForPlatform() bool {
	cond := s.GetCondition(api.BuiltConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForPlatformReason
}

func (s *SonataFlowStatus) IsWaitingForDeployment() bool {
	cond := s.GetCondition(api.RunningConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForDeploymentReason
}

// IsChildObjectsProblem indicates a problem during objects creation during reconciliation
// For example, a deployment that couldn't be created or a referenced object not found.
func (s *SonataFlowStatus) IsChildObjectsProblem() bool {
	cond := s.GetCondition(api.RunningConditionType)
	// You can add more conditions that meet this conditional here
	return cond.IsFalse() && (cond.Reason == api.ExternalResourcesNotFoundReason)
}

func (s *SonataFlowStatus) IsWaitingForBuild() bool {
	cond := s.GetCondition(api.RunningConditionType)
	return cond.IsFalse() && cond.Reason == api.WaitingForBuildReason
}

func (s *SonataFlowStatus) IsBuildRunningOrUnknown() bool {
	cond := s.GetCondition(api.BuiltConditionType)
	return cond.IsUnknown() || (cond.IsFalse() && cond.Reason == api.BuildIsRunningReason)
}

func (s *SonataFlowStatus) IsBuildRunning() bool {
	cond := s.GetCondition(api.BuiltConditionType)
	return cond.IsFalse() && cond.Reason == api.BuildIsRunningReason
}

func (s *SonataFlowStatus) IsBuildFailed() bool {
	cond := s.GetCondition(api.BuiltConditionType)
	return cond.IsFalse() && cond.Reason == api.BuildFailedReason
}

// SonataFlow is the descriptor representation for a workflow application based on the CNCF Serverless Workflow specification.
// +kubebuilder:object:root=true
// +kubebuilder:object:generate=true
// +kubebuilder:subresource:status
// +kubebuilder:resource:shortName={"sf", "workflow", "workflows"}
// +k8s:openapi-gen=true
// +kubebuilder:printcolumn:name="Profile",type=string,JSONPath=`.metadata.annotations.sonataflow\.org\/profile`
// +kubebuilder:printcolumn:name="Version",type=string,JSONPath=`.metadata.annotations.sonataflow\.org\/version`
// +kubebuilder:printcolumn:name="URL",type=string,JSONPath=`.status.endpoint`
// +kubebuilder:printcolumn:name="Ready",type=string,JSONPath=`.status.conditions[?(@.type=='Running')].status`
// +kubebuilder:printcolumn:name="Reason",type=string,JSONPath=`.status.conditions[?(@.type=='Running')].reason`
// +operator-sdk:csv:customresourcedefinitions:resources={{SonataFlowBuild,sonataflow.org/v1alpha08,"A SonataFlow Build"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Deployment,apps/v1,"A Deployment for the Flow"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Service,serving.knative.dev/v1,"A Knative Serving Service for the Flow"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Service,v1,"A Service for the Flow"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{Route,route.openshift.io/v1,"An OpenShift Route for the Flow"}}
// +operator-sdk:csv:customresourcedefinitions:resources={{ConfigMap,v1,"The ConfigMaps with Flow definition and additional configuration files"}}
// +operator-sdk:csv:customresourcedefinitions:displayName="SonataFlow"
type SonataFlow struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   SonataFlowSpec   `json:"spec,omitempty"`
	Status SonataFlowStatus `json:"status,omitempty"`
}

func (s *SonataFlow) IsKnativeDeployment() bool {
	return s.Spec.PodTemplate.DeploymentModel == KnativeDeploymentModel
}

func (s *SonataFlow) HasContainerSpecImage() bool {
	return len(s.Spec.PodTemplate.Container.Image) > 0
}

// SonataFlowList contains a list of SonataFlow
// +kubebuilder:object:root=true
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
type SonataFlowList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []SonataFlow `json:"items"`
}

func init() {
	SchemeBuilder.Register(&SonataFlow{}, &SonataFlowList{})
}

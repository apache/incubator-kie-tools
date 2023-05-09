/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api

import (
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// Build is the Schema for the builder API. Follows the Kubernetes resource structure, but it's not tied to it. Can be used in any environment.
type Build struct {
	ObjectReference `json:"meta,omitempty"`
	Spec            BuildSpec   `json:"spec,omitempty"`
	Status          BuildStatus `json:"status,omitempty"`
}

// BuildStrategy specifies how the Build should be executed.
// It will trigger a Maven process that will take care of producing the expected runtime.
// +kubebuilder:validation:Enum=routine;pod
type BuildStrategy string

const (
	// BuildStrategyRoutine performs the build in a routine (will be executed as a process inside the same owner `Pod` or local process).
	// A routine may be preferred to a `pod` strategy since it reuse the Maven repository dependency cached locally. It is executed as
	// a parallel process, so you may need to consider the quantity of concurrent build process running simultaneously.
	BuildStrategyRoutine BuildStrategy = "routine"
	// BuildStrategyPod performs the build in a `Pod` (will schedule a new builder ephemeral `Pod` which will take care of the build action).
	// This strategy has the limitation that every build will have to download all the dependencies required by the Maven build.
	BuildStrategyPod BuildStrategy = "pod"
)

// BuildSpec defines the Build operation to be executed
type BuildSpec struct {
	// The sequence of Build tasks to be performed as part of the Build execution.
	Tasks []Task `json:"tasks,omitempty"`
	// The strategy that should be used to perform the Build.
	Strategy BuildStrategy `json:"strategy,omitempty"`
	// Timeout defines the Build maximum execution duration.
	// The Build deadline is set to the Build start time plus the Timeout duration.
	// If the Build deadline is exceeded, the Build context is canceled,
	// and its phase set to BuildPhaseFailed.
	// +kubebuilder:validation:Format=duration
	Timeout metav1.Duration `json:"timeout,omitempty"`
}

// RegistrySpec provides the configuration for the container registry
type RegistrySpec struct {
	// if the container registry is insecure (ie, http only)
	Insecure bool `json:"insecure,omitempty"`
	// the URI to access
	Address string `json:"address,omitempty"`
	// the secret where credentials are stored
	Secret string `json:"secret,omitempty"`
	// the configmap which stores the Certificate Authority
	CA string `json:"ca,omitempty"`
	// the registry organization
	Organization string `json:"organization,omitempty"`
}

// Task represents the abstract task. Only one of the task should be configured to represent the specific task chosen.
type Task struct {
	// a KanikoTask, for Kaniko strategy
	Kaniko *KanikoTask `json:"kaniko,omitempty"`
}

// BaseTask is a base for the struct hierarchy
type BaseTask struct {
	// name of the task
	Name string `json:"name,omitempty"`
}

// PublishTask image publish configuration
type PublishTask struct {
	// can be useful to share info with other tasks
	ContextDir string `json:"contextDir,omitempty"`
	// base image layer
	BaseImage string `json:"baseImage,omitempty"`
	// final image name
	Image string `json:"image,omitempty"`
	// where to publish the final image
	Registry RegistrySpec `json:"registry,omitempty"`
}

// KanikoTask is used to configure Kaniko
type KanikoTask struct {
	BaseTask    `json:",inline"`
	PublishTask `json:",inline"`
	// log more information
	Verbose *bool `json:"verbose,omitempty"`
	// use a cache
	Cache KanikoTaskCache `json:"cache,omitempty"`
	// Resources -- optional compute resource requirements for the Kaniko container
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`
	// AdditionalFlags -- List of additional flags for  the Kaniko process (see https://github.com/GoogleContainerTools/kaniko/blob/main/README.md#additional-flags)
	AdditionalFlags []string `json:"additionalFlags,omitempty"`
}

// KanikoTaskCache is used to configure Kaniko cache
type KanikoTaskCache struct {
	// true if a cache is enabled
	Enabled *bool `json:"enabled,omitempty"`
	// the PVC used to store the cache
	PersistentVolumeClaim string `json:"persistentVolumeClaim,omitempty"`
}

// BuildPhase --
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
	BuildPhaseInterrupted = "Interrupted"
	// BuildPhaseError --
	BuildPhaseError BuildPhase = "Error"
)

// BuildConditionType --
type BuildConditionType string

// BuildCondition describes the state of a resource at a certain point.
type BuildCondition struct {
	// Type of integration condition.
	Type BuildConditionType `json:"type"`
	// Status of the condition, one of True, False, Unknown.
	Status corev1.ConditionStatus `json:"status"`
	// The last time this condition was updated.
	LastUpdateTime metav1.Time `json:"lastUpdateTime,omitempty"`
	// Last time the condition transitioned from one status to another.
	LastTransitionTime metav1.Time `json:"lastTransitionTime,omitempty"`
	// The reason for the condition's last transition.
	Reason string `json:"reason,omitempty"`
	// A human-readable message indicating details about the transition.
	Message string `json:"message,omitempty"`
}

// BuildStatus defines the observed state of Build
type BuildStatus struct {
	// ObservedGeneration is the most recent generation observed for this Build.
	ObservedGeneration int64 `json:"observedGeneration,omitempty"`
	// describes the phase
	Phase BuildPhase `json:"phase,omitempty"`
	// the image name built
	Image string `json:"image,omitempty"`
	// the digest from image
	Digest string `json:"digest,omitempty"`
	// the base image used for this build
	BaseImage string `json:"baseImage,omitempty"`
	// the error description (if any)
	Error string `json:"error,omitempty"`
	// the reason of the failure (if any)
	Failure *Failure `json:"failure,omitempty"`
	// the time when it started
	StartedAt *metav1.Time `json:"startedAt,omitempty"`
	// a list of conditions occurred during the build
	Conditions []BuildCondition `json:"conditions,omitempty"`
	// how long it took for the build
	// Change to Duration / ISO 8601 when CRD uses OpenAPI spec v3
	// https://github.com/OAI/OpenAPI-Specification/issues/845
	Duration string `json:"duration,omitempty"`
	// reference to where the build resources are located
	ResourceVolume *ResourceVolume `json:"resourceVolume,omitempty"`
}

// Failure represent a message specifying the reason and the time of an event failure
type Failure struct {
	// a short text specifying the reason
	Reason string `json:"reason"`
	// the time when the failure has happened
	Time metav1.Time `json:"time"`
	// the recovery attempted for this failure
	Recovery FailureRecovery `json:"recovery"`
}

// FailureRecovery defines the attempts to recover a failure
type FailureRecovery struct {
	// attempt number
	Attempt int `json:"attempt"`
	// maximum number of attempts
	AttemptMax int `json:"attemptMax"`
	// time of the attempt execution
	// +optional
	AttemptTime metav1.Time `json:"attemptTime"`
}

type ResourceReferenceType string

const (
	ResourceReferenceTypeConfigMap ResourceReferenceType = "configMap"
)

// ResourceVolume dictates where the build resources are mount
type ResourceVolume struct {
	// ReferenceName name of the object holding the resources reference
	ReferenceName string `json:"referenceName"`
	// ReferenceType type of the resource holding the reference
	ReferenceType ResourceReferenceType `json:"referenceType"`
}

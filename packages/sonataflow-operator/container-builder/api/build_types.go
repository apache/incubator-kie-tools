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

package api

import (
	"fmt"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// ContainerBuild is the Schema for the builder API. Follows the Kubernetes resource structure, but it's not tied to it. Can be used in any environment.
type ContainerBuild struct {
	ObjectReference `json:"metadata,omitempty"`
	Spec            ContainerBuildSpec   `json:"spec,omitempty"`
	Status          ContainerBuildStatus `json:"status,omitempty"`
}

// ContainerBuildStrategy specifies how the ContainerBuild should be executed.
// It will trigger a Maven process that will take care of producing the expected runtime.
// +kubebuilder:validation:Enum=routine;pod
type ContainerBuildStrategy string

const (
	// ContainerBuildStrategyRoutine performs the build in a routine (will be executed as a process inside the same owner `Pod` or local process).
	// A routine may be preferred to a `pod` strategy since it reuse the Maven repository dependency cached locally. It is executed as
	// a parallel process, so you may need to consider the quantity of concurrent build process running simultaneously.
	ContainerBuildStrategyRoutine ContainerBuildStrategy = "routine"
	// ContainerBuildStrategyPod performs the build in a `Pod` (will schedule a new builder ephemeral `Pod` which will take care of the build action).
	// This strategy has the limitation that every build will have to download all the dependencies required by the Maven build.
	ContainerBuildStrategyPod ContainerBuildStrategy = "pod"
)

// ContainerBuildSpec defines the ContainerBuild operation to be executed
type ContainerBuildSpec struct {
	// The sequence of ContainerBuild tasks to be performed as part of the ContainerBuild execution.
	Tasks []ContainerBuildTask `json:"tasks,omitempty"`
	// The strategy that should be used to perform the ContainerBuild.
	Strategy ContainerBuildStrategy `json:"strategy,omitempty"`
	// Timeout defines the ContainerBuild maximum execution duration.
	// The ContainerBuild deadline is set to the ContainerBuild start time plus the Timeout duration.
	// If the ContainerBuild deadline is exceeded, the ContainerBuild context is canceled,
	// and its phase set to ContainerBuildPhaseFailed.
	// +kubebuilder:validation:Format=duration
	Timeout metav1.Duration `json:"timeout,omitempty"`
}

// ContainerRegistrySpec provides the configuration for the container registry
type ContainerRegistrySpec struct {
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

// ContainerBuildTask represents the abstract task. Only one of the task should be configured to represent the specific task chosen.
type ContainerBuildTask struct {
	// a KanikoTask, for Kaniko strategy
	Kaniko *KanikoTask `json:"kaniko,omitempty"`
}

// ContainerBuildBaseTask is a base for the struct hierarchy
type ContainerBuildBaseTask struct {
	// name of the task
	Name string `json:"name,omitempty"`
	// Resources -- optional compute resource requirements for the Kaniko container
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`
	// Build arguments passed to the internal build system (e.g. Dockerfile ARG)
	BuildArgs []corev1.EnvVar
	// Environment variable passed to the internal build container.
	Envs []corev1.EnvVar `json:"envs,omitempty"`
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
	Registry ContainerRegistrySpec `json:"registry,omitempty"`
}

// GetRepositoryImageTag gets the full qualified Repository Name for the given image in the PublishTask.
// For example registry.org/myrepo/myimage:latest.
func (p *PublishTask) GetRepositoryImageTag() string {
	if len(p.Registry.Address) > 0 {
		return fmt.Sprintf("%s/%s", p.Registry.Address, p.Image)
	}
	return p.Image
}

// KanikoTask is used to configure Kaniko
type KanikoTask struct {
	ContainerBuildBaseTask `json:",inline"`
	PublishTask            `json:",inline"`
	// log more information
	Verbose *bool `json:"verbose,omitempty"`
	// use a cache
	Cache KanikoTaskCache `json:"cache,omitempty"`
	// AdditionalFlags -- List of additional flags for  the Kaniko process (see https://github.com/GoogleContainerTools/kaniko/blob/main/README.md#additional-flags)
	AdditionalFlags []string `json:"additionalFlags,omitempty"`
	// Image used by the created Kaniko pod executor
	KanikoExecutorImage string `json:"kanikoExecutorImage,omitempty"`
}

// KanikoTaskCache is used to configure Kaniko cache
type KanikoTaskCache struct {
	// true if a cache is enabled
	Enabled *bool `json:"enabled,omitempty"`
	// the PVC used to store the cache
	PersistentVolumeClaim string `json:"persistentVolumeClaim,omitempty"`
}

// ContainerBuildPhase --
type ContainerBuildPhase string

const (
	// ContainerBuildPhaseNone --
	ContainerBuildPhaseNone ContainerBuildPhase = ""
	// ContainerBuildPhaseInitialization --
	ContainerBuildPhaseInitialization ContainerBuildPhase = "Initialization"
	// ContainerBuildPhaseScheduling --
	ContainerBuildPhaseScheduling ContainerBuildPhase = "Scheduling"
	// ContainerBuildPhasePending --
	ContainerBuildPhasePending ContainerBuildPhase = "Pending"
	// ContainerBuildPhaseRunning --
	ContainerBuildPhaseRunning ContainerBuildPhase = "Running"
	// ContainerBuildPhaseSucceeded --
	ContainerBuildPhaseSucceeded ContainerBuildPhase = "Succeeded"
	// ContainerBuildPhaseFailed --
	ContainerBuildPhaseFailed ContainerBuildPhase = "Failed"
	// ContainerBuildPhaseInterrupted --
	ContainerBuildPhaseInterrupted ContainerBuildPhase = "Interrupted"
	// ContainerBuildPhaseError --
	ContainerBuildPhaseError ContainerBuildPhase = "Error"
)

// ContainerBuildConditionType --
type ContainerBuildConditionType string

// ContainerBuildCondition describes the state of a resource at a certain point.
type ContainerBuildCondition struct {
	// Type of integration condition.
	Type ContainerBuildConditionType `json:"type"`
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

// ContainerBuildStatus defines the observed state of ContainerBuild
type ContainerBuildStatus struct {
	// ObservedGeneration is the most recent generation observed for this ContainerBuild.
	ObservedGeneration int64 `json:"observedGeneration,omitempty"`
	// describes the phase
	Phase ContainerBuildPhase `json:"phase,omitempty"`
	// the image name built
	RepositoryImageTag string `json:"repositoryImageTag,omitempty"`
	// the digest from image
	Digest string `json:"digest,omitempty"`
	// the base image used for this build
	BaseImage string `json:"baseImage,omitempty"`
	// the error description (if any)
	Error string `json:"error,omitempty"`
	// the reason of the failure (if any)
	Failure *ContainerBuildFailure `json:"failure,omitempty"`
	// the time when it started
	StartedAt *metav1.Time `json:"startedAt,omitempty"`
	// a list of conditions occurred during the build
	Conditions []ContainerBuildCondition `json:"conditions,omitempty"`
	// how long it took for the build
	// Change to Duration / ISO 8601 when CRD uses OpenAPI spec v3
	// https://github.com/OAI/OpenAPI-Specification/issues/845
	Duration string `json:"duration,omitempty"`
	// reference to where the build resources are located
	ResourceVolumes []ContainerBuildResourceVolume `json:"resourceVolumes,omitempty"`
}

// ContainerBuildFailure represent a message specifying the reason and the time of an event failure
type ContainerBuildFailure struct {
	// a short text specifying the reason
	Reason string `json:"reason"`
	// the time when the failure has happened
	Time metav1.Time `json:"time"`
	// the recovery attempted for this failure
	Recovery ContainerBuildFailureRecovery `json:"recovery"`
}

// ContainerBuildFailureRecovery defines the attempts to recover a failure
type ContainerBuildFailureRecovery struct {
	// attempt number
	Attempt int `json:"attempt"`
	// maximum number of attempts
	AttemptMax int `json:"attemptMax"`
	// time of the attempt execution
	// +optional
	AttemptTime metav1.Time `json:"attemptTime"`
}

type ContainerBuildResourceReferenceType string

const (
	ResourceReferenceTypeConfigMap ContainerBuildResourceReferenceType = "configMap"
)

// ContainerBuildResourceVolume dictates where the build resources are mount
type ContainerBuildResourceVolume struct {
	// ReferenceName name of the object holding the resources reference
	ReferenceName string `json:"referenceName"`
	// ReferenceType type of the resource holding the reference
	ReferenceType ContainerBuildResourceReferenceType `json:"referenceType"`
	// DestinationDir where to mount the given volume in the build context
	DestinationDir string `json:"destinationDir,omitempty"`
}

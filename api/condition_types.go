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

package api

import (
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// Conditions ...
// +kubebuilder:object:generate=true
type Conditions []Condition

type ConditionType string

const (
	// RunningConditionType describes the readiness condition of a "live" resource, like the workflow application
	RunningConditionType ConditionType = "Running"
	// SucceedConditionType describes the readiness condition of a static resource, like a platform, a builder, a configuration, etc.
	SucceedConditionType ConditionType = "Succeed"
	// BuiltConditionType describes the condition of a resource that needs to be build.
	BuiltConditionType ConditionType = "Built"
)

const (
	WaitingForDeploymentReason  = "WaitingForDeployment"
	DeploymentFailureReason     = "DeploymentFailure"
	DeploymentUnavailableReason = "DeploymentIsUnavailable"
	RedeploymentExhaustedReason = "AttemptToRedeployFailed"
	WaitingForPlatformReason    = "WaitingForPlatform"
	BuildFailedReason           = "BuildFailedReason"
	WaitingForBuildReason       = "WaitingForBuild"
	BuildIsRunningReason        = "BuildIsRunning"
)

// Condition describes the common structure for conditions in our types
// +kubebuilder:object:generate=true
type Condition struct {
	// Type condition for the given object
	// +required
	Type ConditionType `json:"type"`
	// Status of the condition, one of True, False, Unknown.
	// +required
	Status v1.ConditionStatus `json:"status"`
	// The last time this condition was updated.
	LastUpdateTime metav1.Time `json:"lastUpdateTime,omitempty"`
	// The reason for the condition's last transition.
	Reason string `json:"reason,omitempty"`
	// A human-readable message indicating details about the transition.
	Message string `json:"message,omitempty"`
}

// IsTrue is true if the condition is True
func (c *Condition) IsTrue() bool {
	if c == nil {
		return false
	}
	return c.Status == v1.ConditionTrue
}

// IsFalse is true if the condition is False
func (c *Condition) IsFalse() bool {
	if c == nil {
		return false
	}
	return c.Status == v1.ConditionFalse
}

// IsUnknown is true if the condition is Unknown
func (c *Condition) IsUnknown() bool {
	if c == nil {
		return true
	}
	return c.Status == v1.ConditionUnknown
}

// GetReason returns a nil save string of Reason
func (c *Condition) GetReason() string {
	if c == nil {
		return ""
	}
	return c.Reason
}

// GetMessage returns a nil save string of Message
func (c *Condition) GetMessage() string {
	if c == nil {
		return ""
	}
	return c.Message
}

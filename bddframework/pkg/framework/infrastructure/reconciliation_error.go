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

package infrastructure

import (
	"errors"
	"fmt"
	"github.com/kiegroup/kogito-operator/core/operator"
	ctrl "sigs.k8s.io/controller-runtime"
	"time"
)

// ConditionReason is the type of reason
type ConditionReason string

const (
	// CreateResourceFailedReason - Unable to create the requested resources
	CreateResourceFailedReason ConditionReason = "CreateResourceFailed"
	// KogitoInfraNotReadyReason - Unable to deploy Kogito Infra
	KogitoInfraNotReadyReason ConditionReason = "KogitoInfraNotReadyReason"
	// ServiceReconciliationFailure - Unable to determine the error
	ServiceReconciliationFailure ConditionReason = "ReconciliationFailure"
	// MessagingIntegrationFailureReason ...
	MessagingIntegrationFailureReason ConditionReason = "MessagingProvisionFailure"
	// MonitoringIntegrationFailureReason ...
	MonitoringIntegrationFailureReason ConditionReason = "MonitoringIntegrationFailure"
	// InternalServiceNotReachable ...
	InternalServiceNotReachable ConditionReason = "InternalServiceNotReachable"
	// SuccessfulDeployedReason ...
	SuccessfulDeployedReason ConditionReason = "AtLeastOnePodAvailable"
	// FailedDeployedReason ...
	FailedDeployedReason ConditionReason = "NoPodAvailable"
	// ProvisioningInProgressReason ...
	ProvisioningInProgressReason ConditionReason = "RequestedReplicasNotEqualToAvailableReplicas"
	// FailedProvisioningReason ...
	FailedProvisioningReason ConditionReason = "UnrecoverableError"
	// FinishedProvisioningReason ...
	FinishedProvisioningReason ConditionReason = "RequestedReplicasEqualToAvailableReplicas"
	// TrustStoreMountFailureReason happens when the controller tries to mount a given TrustStore in the target service and fails
	TrustStoreMountFailureReason ConditionReason = "TrustStoreMountFailure"
	// ImageStreamNotReadyReason - Unable to deploy Kogito Infra
	ImageStreamNotReadyReason ConditionReason = "ImageStreamNotReadyReason"
	// DeploymentNotAvailable ...
	DeploymentNotAvailable ConditionReason = "DeploymentNotAvailable"
	// ProcessingImageStreamDelta ...
	ProcessingImageStreamDelta ConditionReason = "ProcessingImageStreamDelta"
	// ProcessingProtoBufConfigMapDelta ...
	ProcessingProtoBufConfigMapDelta ConditionReason = "ProcessingProtoBufConfigMapDelta"
	// ImageNotFound ...
	ImageNotFound ConditionReason = "ImageNotFound"
)

const (
	// ReconciliationAfterThreeMinutes ...
	ReconciliationAfterThreeMinutes = time.Minute * 3
	// ReconciliationAfterThirty ...
	ReconciliationAfterThirty = time.Second * 30
	// ReconciliationAfterTen ...
	ReconciliationAfterTen = time.Second * 10
	// ReconciliationAfterFive ...
	ReconciliationAfterFive = time.Second * 5
	// ReconciliationAfterOneMinute ...
	ReconciliationAfterOneMinute = time.Minute
)

// ReconciliationError ...
type ReconciliationError struct {
	reason                 ConditionReason
	reconciliationInterval time.Duration
	innerError             error
}

// String stringer implementation
func (e ReconciliationError) String() string {
	return e.innerError.Error()
}

// Error error implementation
func (e ReconciliationError) Error() string {
	return e.innerError.Error()
}

// ErrorForInfraNotReady ...
func ErrorForInfraNotReady(serviceName string, infraName string, conditionReason string) ReconciliationError {
	return ReconciliationError{
		reconciliationInterval: ReconciliationAfterOneMinute,
		reason:                 KogitoInfraNotReadyReason,
		innerError: fmt.Errorf("KogitoService '%s' is waiting for infra dependency; skipping deployment; KogitoInfra not ready: %s; Status: %s",
			serviceName, infraName, conditionReason),
	}
}

// ErrorForMessaging ...
func ErrorForMessaging(err error) ReconciliationError {
	return ReconciliationError{
		reconciliationInterval: ReconciliationAfterThirty,
		reason:                 MessagingIntegrationFailureReason,
		innerError:             err,
	}
}

// ErrorForMonitoring ...
func ErrorForMonitoring(err error) ReconciliationError {
	return ReconciliationError{
		reconciliationInterval: ReconciliationAfterTen,
		reason:                 MonitoringIntegrationFailureReason,
		innerError:             err,
	}
}

// ErrorForDashboards ...
func ErrorForDashboards(err error) ReconciliationError {
	return ReconciliationError{
		reconciliationInterval: ReconciliationAfterThirty,
		reason:                 MonitoringIntegrationFailureReason,
		innerError:             err,
	}
}

// ErrorForServiceNotReachable ...
func ErrorForServiceNotReachable(statusCode int, requestURL string, method string) ReconciliationError {
	return ReconciliationError{
		reason:                 InternalServiceNotReachable,
		reconciliationInterval: ReconciliationAfterThreeMinutes,
		innerError:             fmt.Errorf("Received NOT expected status code %d while making a %s request to %s ", statusCode, method, requestURL),
	}
}

// ErrorForTrustStoreMount ...
func ErrorForTrustStoreMount(message string) ReconciliationError {
	return ReconciliationError{
		reason:                 TrustStoreMountFailureReason,
		reconciliationInterval: ReconciliationAfterThirty,
		innerError:             errors.New(message),
	}
}

// ErrorForDeploymentNotReachable ...
func ErrorForDeploymentNotReachable(instance string) ReconciliationError {
	return ReconciliationError{
		reason:                 DeploymentNotAvailable,
		reconciliationInterval: ReconciliationAfterThirty,
		innerError:             fmt.Errorf("Deployment is not yet available for service instance %s ", instance),
	}
}

// ErrorForProcessingImageStreamDelta ...
func ErrorForProcessingImageStreamDelta() ReconciliationError {
	return ReconciliationError{
		reason:                 ProcessingImageStreamDelta,
		reconciliationInterval: ReconciliationAfterTen,
		innerError:             fmt.Errorf("Processing Image stream "),
	}
}

// ErrorForProcessingProtoBufConfigMapDelta ...
func ErrorForProcessingProtoBufConfigMapDelta() ReconciliationError {
	return ReconciliationError{
		reason:                 ProcessingProtoBufConfigMapDelta,
		reconciliationInterval: ReconciliationAfterFive,
		innerError:             fmt.Errorf("Processing Protobuf configmap "),
	}
}

// ErrorForImageNotFound ...
func ErrorForImageNotFound() ReconciliationError {
	return ReconciliationError{
		reason:                 ImageNotFound,
		reconciliationInterval: ReconciliationAfterTen,
		innerError:             fmt.Errorf("Image not found "),
	}
}

// ReconciliationErrorHandler ...
type ReconciliationErrorHandler interface {
	IsReconciliationError(err error) bool
	GetReconcileResultFor(err error) (ctrl.Result, error)
	GetReasonForError(err error) ConditionReason
}

type reconciliationErrorHandler struct {
	operator.Context
}

// NewReconciliationErrorHandler ...
func NewReconciliationErrorHandler(context operator.Context) ReconciliationErrorHandler {
	return &reconciliationErrorHandler{
		context,
	}
}

func (r *reconciliationErrorHandler) IsReconciliationError(err error) bool {
	switch err.(type) {
	case ReconciliationError:
		return true
	}
	return false
}

func (r *reconciliationErrorHandler) GetReconcileResultFor(err error) (ctrl.Result, error) {
	reconcileResult := ctrl.Result{}

	// reconciliation always happens if we return an error
	if r.IsReconciliationError(err) {
		reconcileError := err.(ReconciliationError)
		r.Log.Info("Waiting for all resources to be created, re-scheduling.", "reason", reconcileError.reason, "requeueAfter", reconcileError.reconciliationInterval)
		reconcileResult.RequeueAfter = reconcileError.reconciliationInterval
		reconcileResult.Requeue = true
		return reconcileResult, nil
	}
	return reconcileResult, err
}

func (r *reconciliationErrorHandler) GetReasonForError(err error) ConditionReason {
	if err == nil {
		return ""
	}
	if r.IsReconciliationError(err) {
		reconcileError := err.(ReconciliationError)
		return reconcileError.reason
	}
	return ServiceReconciliationFailure
}

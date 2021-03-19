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

package kogitoservice

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"time"
)

const (
	reconciliationIntervalAfterInfraError                = time.Minute
	reconciliationIntervalAfterMessagingError            = time.Second * 30
	reconciliationIntervalMonitoringEndpointNotAvailable = time.Second * 10
	reconciliationIntervalAfterDashboardsError           = time.Second * 30
	reconciliationIntervalAfterFetchService              = time.Minute * 3
)

type reconciliationError struct {
	reason                 api.KogitoServiceConditionReason
	reconciliationInterval time.Duration
	innerError             error
}

// String stringer implementation
func (e reconciliationError) String() string {
	return e.innerError.Error()
}

// Error error implementation
func (e reconciliationError) Error() string {
	return e.innerError.Error()
}

func errorForInfraNotReady(service api.KogitoService, infraName string, conditionReason string) reconciliationError {
	return reconciliationError{
		reconciliationInterval: reconciliationIntervalAfterInfraError,
		reason:                 api.KogitoInfraNotReadyReason,
		innerError: fmt.Errorf("KogitoService '%s' is waiting for infra dependency; skipping deployment; KogitoInfra not ready: %s; Status: %s",
			service.GetName(), infraName, conditionReason),
	}
}

func errorForMessaging(err error) reconciliationError {
	return reconciliationError{
		reconciliationInterval: reconciliationIntervalAfterMessagingError,
		reason:                 api.MessagingIntegrationFailureReason,
		innerError:             err,
	}
}

func errorForMonitoring(err error) reconciliationError {
	return reconciliationError{
		reconciliationInterval: reconciliationIntervalMonitoringEndpointNotAvailable,
		reason:                 api.MonitoringIntegrationFailureReason,
		innerError:             err,
	}
}

func errorForDashboards(err error) reconciliationError {
	return reconciliationError{
		reconciliationInterval: reconciliationIntervalAfterDashboardsError,
		reason:                 api.MonitoringIntegrationFailureReason,
		innerError:             err,
	}
}

func errorForServiceNotReachable(statusCode int, requestURL string, method string) reconciliationError {
	return reconciliationError{
		reason:                 api.InternalServiceNotReachable,
		reconciliationInterval: reconciliationIntervalAfterFetchService,
		innerError:             fmt.Errorf("Received NOT expected status code %d while making a %s request to %s ", statusCode, method, requestURL),
	}
}

func reasonForError(err error) api.KogitoServiceConditionReason {
	if err == nil {
		return ""
	}
	switch t := err.(type) {
	case reconciliationError:
		return t.reason
	}
	return api.ServiceReconciliationFailure
}

func isReconciliationError(err error) bool {
	switch err.(type) {
	case reconciliationError:
		return true
	}
	return false
}

func reconciliationIntervalForError(err error) time.Duration {
	switch t := err.(type) {
	case reconciliationError:
		return t.reconciliationInterval
	}
	return 0
}

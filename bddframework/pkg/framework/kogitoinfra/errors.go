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

package kogitoinfra

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/apis"
)

// reconciliationError type for KogitoInfra reconciliation cycle cases.
type reconciliationError struct {
	Reason     api.KogitoInfraConditionReason
	innerError error
}

// String stringer implementation
func (e reconciliationError) String() string {
	return e.innerError.Error()
}

// Error error implementation
func (e reconciliationError) Error() string {
	return e.innerError.Error()
}

func errorForResourceNotFound(kind, instance, namespace string) reconciliationError {
	return reconciliationError{
		Reason:     api.ResourceNotFound,
		innerError: fmt.Errorf("%s resource(%s) not found in namespace %s", kind, instance, namespace),
	}
}

func errorForResourceAPINotFound(apiVersion string) reconciliationError {
	return reconciliationError{
		Reason:     api.ResourceAPINotFound,
		innerError: fmt.Errorf("%s CRD is not available in the cluster, this feature is not available. Please install the required Operator first. ", apiVersion),
	}
}

func errorForUnsupportedAPI(context infraContext) reconciliationError {
	return reconciliationError{
		Reason: api.UnsupportedAPIKind,
		innerError: fmt.Errorf("API %s is not supported for kind %s. Supported APIs are: %v",
			context.instance.GetSpec().GetResource().GetAPIVersion(),
			context.instance.GetSpec().GetResource().GetKind(),
			getSupportedResources(context)),
	}
}

func errorForMissingResourceConfig(instance api.KogitoInfraInterface, configName string) reconciliationError {
	return reconciliationError{
		Reason:     api.ResourceMissingResourceConfig,
		innerError: fmt.Errorf("Resource %s, configuration information (%s) is missing", instance.GetName(), configName),
	}
}

func errorForResourceConfigError(instance api.KogitoInfraInterface, errorMsg string) reconciliationError {
	return reconciliationError{
		Reason:     api.ResourceConfigError,
		innerError: fmt.Errorf("Error in configuration for the infrastructure resource %s. Error is: %s", instance.GetName(), errorMsg),
	}
}

func errorForResourceNotReadyError(err error) reconciliationError {
	return reconciliationError{
		Reason:     api.ResourceNotReady,
		innerError: err,
	}
}

func getSupportedResources(context infraContext) []string {
	res := getSupportedInfraResources(context)
	keys := make([]string, 0, len(res))
	for k := range res {
		keys = append(keys, k)
	}
	return keys
}

func reasonForError(err error) api.KogitoInfraConditionReason {
	if err == nil {
		return ""
	}
	switch t := err.(type) {
	case reconciliationError:
		return t.Reason
	}
	return api.ReconciliationFailure
}

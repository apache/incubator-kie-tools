// Copyright 2019 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/internal"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"reflect"

	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
)

// StatusHandler ...
type StatusHandler interface {
	UpdateBaseStatus(instance api.KogitoInfraInterface, err *error)
}

type statusHandler struct {
	operator.Context
}

// NewStatusHandler ...
func NewStatusHandler(context operator.Context) StatusHandler {
	return &statusHandler{
		context,
	}
}

// updateBaseStatus updates the base status for the KogitoInfra instance
func (s *statusHandler) UpdateBaseStatus(instance api.KogitoInfraInterface, err *error) {
	s.Log.Info("Updating Kogito Infra status")
	if instance.GetStatus().GetConditions() == nil {
		instance.GetStatus().SetConditions(&[]metav1.Condition{})
	}
	if *err != nil {
		s.Log.Info("Seems that an error occurred, setting failure state", "Error", *err)
		s.setResourceFailed(instance.GetStatus().GetConditions(), *err)
	} else {
		s.setResourceSuccess(instance.GetStatus().GetConditions())
		s.Log.Info("Kogito Infra successfully reconciled")
	}

	if s.isStatusChanged(instance) {
		s.Log.Info("Updating kogitoInfra value with new properties.")
		if resultErr := kubernetes.ResourceC(s.Client).UpdateStatus(instance); resultErr != nil {
			s.Log.Error(resultErr, "reconciliationError occurs while update kogitoInfra values")
		}
		s.Log.Info("Successfully Update Kogito Infra status")
	}
}

func (s *statusHandler) isStatusChanged(instance api.KogitoInfraInterface) bool {
	infraHandler := internal.NewKogitoInfraHandler(s.Context)
	deployedInstance, resultErr := infraHandler.FetchKogitoInfraInstance(types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()})
	if resultErr != nil {
		s.Log.Error(resultErr, "Error occurs while checking status change state")
	}

	return !reflect.DeepEqual(instance.GetStatus(), deployedInstance.GetStatus())
}

// setResourceFailed sets the instance as failed
func (s *statusHandler) setResourceFailed(conditions *[]metav1.Condition, err error) {
	reason := reasonForError(err)
	failedCondition := s.newConfiguredCondition(metav1.ConditionFalse, reason, err.Error())
	meta.SetStatusCondition(conditions, failedCondition)
}

// setResourceSuccess sets the instance as success
func (s *statusHandler) setResourceSuccess(conditions *[]metav1.Condition) {
	successCondition := s.newConfiguredCondition(metav1.ConditionTrue, api.ResourceSuccessfullyConfigured, "")
	meta.SetStatusCondition(conditions, successCondition)
}

// NewFailedCondition ...
func (s *statusHandler) newConfiguredCondition(status metav1.ConditionStatus, reason api.KogitoInfraConditionReason, message string) metav1.Condition {
	return metav1.Condition{
		Type:    string(api.KogitoInfraConfigured),
		Status:  status,
		Reason:  string(reason),
		Message: message,
	}
}

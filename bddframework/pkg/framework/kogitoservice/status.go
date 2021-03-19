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

package kogitoservice

import (
	"fmt"
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"k8s.io/apimachinery/pkg/api/meta"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

// StatusHandler ...
type StatusHandler interface {
	HandleStatusUpdate(instance api.KogitoService, err *error)
}

type statusHandler struct {
	*operator.Context
}

// NewStatusHandler ...
func NewStatusHandler(context *operator.Context) StatusHandler {
	return &statusHandler{
		context,
	}
}

func (s *statusHandler) HandleStatusUpdate(instance api.KogitoService, err *error) {
	s.Log.Info("Updating status for Kogito Service")
	if statusErr := s.ensureResourcesStatusChanges(instance, *err); statusErr != nil {
		s.Log.Error(statusErr, "Error while updating Status for Kogito Service")
		return
	}
	s.Log.Info("Finished Kogito Service reconciliation")
}

func (s *statusHandler) ensureResourcesStatusChanges(instance api.KogitoService, errCondition error) (err error) {
	instance.GetStatus().SetConditions(&[]metav1.Condition{})
	if errCondition != nil {
		if err = s.setFailedConditions(instance, reasonForError(errCondition), errCondition); err != nil {
			return err
		}
	} else {
		if err = s.handleConditionTransition(instance); err != nil {
			return err
		}
		if err = s.updateImageStatus(instance); err != nil {
			return err
		}
		if err = s.updateRouteStatus(instance); err != nil {
			return err
		}
		if err = s.updateDeploymentStatus(instance); err != nil {
			return err
		}
	}
	if err := s.updateStatus(instance); err != nil {
		s.Log.Error(err, "Error while trying to update status")
		return err
	}
	return nil
}

func (s *statusHandler) setFailedConditions(instance api.KogitoService, reason api.KogitoServiceConditionReason, errCondition error) error {
	s.setFailed(instance.GetStatus().GetConditions(), reason, errCondition)
	if isReconciliationError(errCondition) {
		s.setProvisioning(instance.GetStatus().GetConditions(), metav1.ConditionTrue, api.ProvisioningInProgressReason)
	} else {
		s.setProvisioning(instance.GetStatus().GetConditions(), metav1.ConditionFalse, api.FailedProvisioningReason)
	}

	availableReplicas, err := s.fetchReadyReplicas(instance)
	if err != nil {
		return err
	}
	if availableReplicas > 0 {
		s.setDeployed(instance.GetStatus().GetConditions(), metav1.ConditionTrue)
	} else {
		s.setDeployed(instance.GetStatus().GetConditions(), metav1.ConditionFalse)
	}
	return nil
}

func (s *statusHandler) handleConditionTransition(instance api.KogitoService) error {
	availableReplicas, err := s.fetchReadyReplicas(instance)
	if err != nil {
		return err
	}
	expectedReplicas := *instance.GetSpec().GetReplicas()
	if expectedReplicas == availableReplicas {
		s.setDeployed(instance.GetStatus().GetConditions(), metav1.ConditionTrue)
		s.setProvisioning(instance.GetStatus().GetConditions(), metav1.ConditionFalse, api.FinishedProvisioningReason)
	} else if availableReplicas > 0 && availableReplicas < expectedReplicas {
		s.setDeployed(instance.GetStatus().GetConditions(), metav1.ConditionTrue)
		s.setProvisioning(instance.GetStatus().GetConditions(), metav1.ConditionTrue, api.ProvisioningInProgressReason)
	} else if availableReplicas == 0 {
		s.setDeployed(instance.GetStatus().GetConditions(), metav1.ConditionFalse)
		s.setProvisioning(instance.GetStatus().GetConditions(), metav1.ConditionTrue, api.ProvisioningInProgressReason)
	}
	return nil
}

func (s *statusHandler) updateStatus(instance api.KogitoService) error {
	err := kubernetes.ResourceC(s.Client).UpdateStatus(instance)
	if err != nil {
		return err
	}
	return nil
}

func (s *statusHandler) updateImageStatus(instance api.KogitoService) error {
	deploymentHandler := infrastructure.NewDeploymentHandler(s.Context)
	deployment, err := deploymentHandler.MustFetchDeployment(types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()})
	if err != nil {
		return err
	}
	if len(deployment.Spec.Template.Spec.Containers) > 0 {
		image := deployment.Spec.Template.Spec.Containers[0].Image
		instance.GetStatus().SetImage(image)
	}
	return nil
}

func (s *statusHandler) updateDeploymentStatus(instance api.KogitoService) error {
	deploymentHandler := infrastructure.NewDeploymentHandler(s.Context)
	deployment, err := deploymentHandler.MustFetchDeployment(types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()})
	if err != nil {
		return err
	}
	instance.GetStatus().SetDeploymentConditions(deployment.Status.Conditions)
	return nil
}

func (s *statusHandler) updateRouteStatus(instance api.KogitoService) error {
	if s.Client.IsOpenshift() {
		routeHandler := infrastructure.NewRouteHandler(s.Context)
		route, err := routeHandler.GetHostFromRoute(types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()})
		if err != nil {
			return err
		}

		if len(route) > 0 {
			uri := fmt.Sprintf("http://%s", route)
			instance.GetStatus().SetExternalURI(uri)
		}
	}
	return nil
}

// NewDeployedCondition ...
func (s *statusHandler) newDeployedCondition(status metav1.ConditionStatus) metav1.Condition {
	reason := api.SuccessfulDeployedReason
	if status == metav1.ConditionFalse {
		reason = api.FailedDeployedReason
	}
	return metav1.Condition{
		Type:               string(api.DeployedConditionType),
		Status:             status,
		LastTransitionTime: metav1.Now(),
		Reason:             string(reason),
	}
}

// NewProvisioningCondition ...
func (s *statusHandler) newProvisioningCondition(status metav1.ConditionStatus, reason api.KogitoServiceConditionReason) metav1.Condition {
	return metav1.Condition{
		Type:               string(api.ProvisioningConditionType),
		Status:             status,
		LastTransitionTime: metav1.Now(),
		Reason:             string(reason),
	}
}

// NewFailedCondition ...
func (s *statusHandler) newFailedCondition(reason api.KogitoServiceConditionReason, err error) metav1.Condition {
	return metav1.Condition{
		Type:               string(api.FailedConditionType),
		Status:             metav1.ConditionTrue,
		LastTransitionTime: metav1.Now(),
		Reason:             string(reason),
		Message:            err.Error(),
	}
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setProvisioning(conditions *[]metav1.Condition, status metav1.ConditionStatus, reason api.KogitoServiceConditionReason) {
	provisionCondition := s.newProvisioningCondition(status, reason)
	meta.SetStatusCondition(conditions, provisionCondition)
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setDeployed(conditions *[]metav1.Condition, status metav1.ConditionStatus) {
	deployedCondition := s.newDeployedCondition(status)
	meta.SetStatusCondition(conditions, deployedCondition)
}

// SetProvisioning Sets the condition type to Provisioning and status True if not yet set.
func (s *statusHandler) setFailed(conditions *[]metav1.Condition, reason api.KogitoServiceConditionReason, err error) {
	failedCondition := s.newFailedCondition(reason, err)
	meta.SetStatusCondition(conditions, failedCondition)
}

func (s *statusHandler) fetchReadyReplicas(instance api.KogitoService) (int32, error) {
	deploymentHandler := infrastructure.NewDeploymentHandler(s.Context)
	readyReplicas, err := deploymentHandler.FetchReadyReplicas(types.NamespacedName{Name: instance.GetName(), Namespace: instance.GetNamespace()})
	if err != nil {
		return 0, err
	}
	return readyReplicas, nil
}

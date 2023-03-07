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

package profiles

import (
	"context"
	"fmt"

	"github.com/go-logr/logr"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// ProfileReconciler is the public interface to have access to this package and perform the actual reconciliation flow.
//
// There are a few concepts in this package that you need to understand before attempting to maintain it:
//
// 1. ProfileReconciler: it's the main interface that internal structs implement via the baseReconciler.
// Every profile must embed the baseReconciler.
//
// 2. stateSupport: is a struct with a few support objects passed around the reconciliation states like the client and logger.
//
// 3. reconciliationStateMachine: is a struct within the ProfileReconciler that do the actual reconciliation.
// Each part of the reconciliation algorithm is a ReconciliationState that will be executed based on the ReconciliationState.CanReconcile call.
//
// 4. ReconciliationState: is where your business code should be focused on. Each state should react to a specific operatorapi.KogitoServerlessWorkflowConditionType.
// The least conditions your state handles, the better.
// The ReconciliationState can provide specific code that will only be triggered if the workflow is in that specific condition.
//
// 5. objectCreator: are functions to create a specific Kubernetes object based on a given workflow instance. This function should return the desired default state.
//
// 6. mutateVisitor: is a function that states can pass to objectEnsurer that will be applied to a given live object during the reconciliation cycle.
// For example, if you wish to guarantee that an image in a specific container in the Deployment that you control and own won't change, make sure that your
// mutate function guarantees that.
//
// 7. objectEnsurer: is a struct for a given objectCreator to control the reconciliation and merge conditions to an object.
// A ReconciliationState may or may not have one or more ensurers. Depends on their role. There are states that just read objects, so no need to keep their desired state.
//
// See the already implemented reconciliation profiles to have a better understanding.
//
// While debugging, focus on the ReconciliationState(s), not in the profile implementation since the base algorithm is the same for every profile.
type ProfileReconciler interface {
	Reconcile(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, error)
	GetProfile() Profile
}

// stateSupport is the shared structure with common accessors used throughout the whole reconciliation profiles
type stateSupport struct {
	logger *logr.Logger
	client client.Client
}

// performStatusUpdate updates the KogitoServerlessWorkflow Status conditions
func (s stateSupport) performStatusUpdate(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (bool, error) {
	var err error
	workflow.Status.Applied = workflow.Spec
	workflow.Status.ObservedGeneration = workflow.Generation
	if err = s.client.Status().Update(ctx, workflow); err != nil {
		s.logger.Error(err, "Failed to update Workflow status")
		return false, err
	}
	return true, err
}

// baseReconciler is the base structure used by every reconciliation profile.
// Use newBaseProfileReconciler to build a new reference.
type baseReconciler struct {
	*stateSupport
	reconciliationStateMachine *reconciliationStateMachine
	objects                    []client.Object
}

func newBaseProfileReconciler(support *stateSupport, stateMachine *reconciliationStateMachine) baseReconciler {
	return baseReconciler{
		stateSupport:               support,
		reconciliationStateMachine: stateMachine,
	}
}

// Reconcile does the actual reconciliation algorithm based on a set of ReconciliationState
func (b baseReconciler) Reconcile(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, error) {
	workflow.Status.Manager().InitializeConditions()
	result, objects, err := b.reconciliationStateMachine.do(ctx, workflow)
	if err != nil {
		return result, err
	}
	b.objects = objects
	b.logger.Info("Returning from reconciliation", "Result", result)
	return result, err
}

// ReconciliationState is an interface implemented internally by different reconciliation algorithms to perform the adequate logic for a given workflow profile
type ReconciliationState interface {
	// CanReconcile checks if this state can perform its reconciliation task
	CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool
	// Do perform the reconciliation task. It returns the controller result, the objects updated, and an error if any.
	// Objects can be nil if the reconciliation state doesn't perform any updates in any Kubernetes object.
	Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error)
}

// newReconciliationStateMachine builder for the reconciliationStateMachine
func newReconciliationStateMachine(logger *logr.Logger, states ...ReconciliationState) *reconciliationStateMachine {
	return &reconciliationStateMachine{
		states: states,
		logger: logger,
	}
}

// reconciliationStateMachine implements (sort of) the command pattern and delegate to a chain of ReconciliationState
// the actual task to reconcile in a given workflow condition
//
// TODO: implement state transition, so based on a given condition we do the status update which actively transition the object state
type reconciliationStateMachine struct {
	states []ReconciliationState
	logger *logr.Logger
}

func (r *reconciliationStateMachine) do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	for _, h := range r.states {
		if h.CanReconcile(workflow) {
			r.logger.Info("Found a condition to reconcile.", "Conditions", workflow.Status.Conditions)
			return h.Do(ctx, workflow)
		}
	}
	return ctrl.Result{}, nil, fmt.Errorf("the workflow %s in the namespace %s is in an unknown state condition. Can't reconcilie. Status is: %v", workflow.Name, workflow.Namespace, workflow.Status)
}

// NewReconciler creates a new ProfileReconciler based on the given workflow and context.
func NewReconciler(client client.Client, logger *logr.Logger, workflow *operatorapi.KogitoServerlessWorkflow) ProfileReconciler {
	return profileBuilder(workflow)(client, logger)
}

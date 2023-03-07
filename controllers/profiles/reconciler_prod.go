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
	"time"

	"github.com/go-logr/logr"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/types"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/kiegroup/kogito-serverless-operator/api"

	builderapi "github.com/kiegroup/container-builder/api"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
	"github.com/kiegroup/kogito-serverless-operator/platform"
	"github.com/kiegroup/kogito-serverless-operator/utils"
)

var _ ProfileReconciler = &prodProfile{}

type prodProfile struct {
	baseReconciler
}

const (
	requeueAfterStartingBuild   = 3 * time.Minute
	requeueWhileWaitForBuild    = 1 * time.Minute
	requeueWhileWaitForPlatform = 5 * time.Second
)

// prodObjectEnsurers is a struct for the objects that ReconciliationState needs to create in the platform for the Production profile.
// ReconciliationState that needs access to it must include this struct as an attribute and initialize it in the profile builder.
// Use newProdObjectEnsurers to facilitate building this struct
type prodObjectEnsurers struct {
	deployment *objectEnsurer
	service    *objectEnsurer
}

func newProdObjectEnsurers(support *stateSupport) *prodObjectEnsurers {
	return &prodObjectEnsurers{
		deployment: newObjectEnsurer(support.client, support.logger, defaultDeploymentCreator),
		service:    newObjectEnsurer(support.client, support.logger, defaultServiceCreator),
	}
}

func newProdProfileReconciler(client client.Client, logger *logr.Logger) ProfileReconciler {
	support := &stateSupport{
		logger: logger,
		client: client,
	}
	// the reconciliation state machine
	stateMachine := newReconciliationStateMachine(
		logger,
		&newBuilderReconciliationState{stateSupport: support},
		&followBuildStatusReconciliationState{stateSupport: support},
		&deployWorkflowReconciliationState{stateSupport: support, ensurers: newProdObjectEnsurers(support)},
	)
	reconciler := &prodProfile{
		baseReconciler: newBaseProfileReconciler(support, stateMachine),
	}

	return reconciler
}

func (p prodProfile) GetProfile() Profile {
	return Production
}

type newBuilderReconciliationState struct {
	*stateSupport
}

func (h *newBuilderReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.GetTopLevelCondition().IsUnknown() || workflow.Status.IsWaitingForPlatform()
}

func (h *newBuilderReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	buildable := builder.NewBuildable(h.client, ctx)
	_, err := platform.GetActivePlatform(ctx, h.client, workflow.Namespace)
	if err != nil {
		if errors.IsNotFound(err) {
			workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForPlatformReason,
				"No active Platform for namespace %s so the workflow cannot be built.", workflow.Namespace)
			_, err = h.performStatusUpdate(ctx, workflow)
			return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
		}
		h.logger.Error(err, "Failed to get active platform")
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}
	// If there is an active platform we have got all the information to build but...
	// ...let's check before if we have got already a build!
	build, err := buildable.GetWorkflowBuild(workflow.Name, workflow.Namespace)
	if err != nil {
		return ctrl.Result{}, nil, err
	}
	if build == nil {
		//If there isn't a build let's create and start the first one!
		build, err = buildable.CreateWorkflowBuild(workflow.Name, workflow.Namespace)
		if err != nil {
			return ctrl.Result{}, nil, err
		}
	}
	//If there is a build, let's ask to restart it
	build.Status.BuildPhase = builderapi.BuildPhaseNone
	build.Status.Builder.Status = builderapi.BuildStatus{}
	if err = h.client.Status().Update(ctx, build); err != nil {
		h.logger.Error(err, fmt.Sprintf("Failed to update Build status for Workflow %s", workflow.Name))
		return ctrl.Result{}, nil, err
	}
	workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "")
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForBuildReason, "")

	_, err = h.performStatusUpdate(ctx, workflow)
	return ctrl.Result{RequeueAfter: requeueAfterStartingBuild}, nil, err
}

type followBuildStatusReconciliationState struct {
	*stateSupport
}

func (h *followBuildStatusReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.IsBuildRunning()
}

func (h *followBuildStatusReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	// Let's retrieve the build to check the status
	build := &operatorapi.KogitoServerlessBuild{}
	err := h.client.Get(ctx, types.NamespacedName{Namespace: workflow.Namespace, Name: workflow.Name}, build)
	if err != nil {
		if !errors.IsNotFound(err) {
			return ctrl.Result{}, nil, err
		}
		h.logger.Error(err, "Build not found for this workflow, starting over by recreating a new build", "Workflow", workflow.Name)
		workflow.Status.Manager().MarkUnknown(api.BuiltConditionType, "", "")
		if _, err = h.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{}, nil, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterStartingBuild}, nil, nil
	}

	if build.Status.Builder.Status.Phase == builderapi.BuildPhaseSucceeded {
		h.logger.Info("Workflow build has finished")
		//If we have finished a build and the workflow is not running, we will start the provisioning phase
		workflow.Status.Manager().MarkTrue(api.BuiltConditionType)
		_, err = h.performStatusUpdate(ctx, workflow)
	} else if build.Status.Builder.Status.Phase == builderapi.BuildPhaseFailed || build.Status.Builder.Status.Phase == builderapi.BuildPhaseError {
		// TODO: we should handle build failures https://issues.redhat.com/browse/KOGITO-8792
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason,
			"Workflow %s build failed. Error: %s", workflow.Name, build.Status.Builder.Status.Error)
		_, err = h.performStatusUpdate(ctx, workflow)
	}
	if err != nil {
		return ctrl.Result{}, nil, err
	}
	return ctrl.Result{RequeueAfter: requeueWhileWaitForBuild}, nil, nil
}

type deployWorkflowReconciliationState struct {
	*stateSupport
	ensurers *prodObjectEnsurers
}

func (h *deployWorkflowReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.GetCondition(api.BuiltConditionType).IsTrue()
}

func (h *deployWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	pl, err := platform.GetActivePlatform(ctx, h.client, workflow.Namespace)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForPlatformReason,
			"No active Platform for namespace %s so the workflow cannot be deployed. Waiting for an active platform", workflow.Namespace)
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}

	if markRunningUnknownIfWorkflowChanged(workflow) { // Let's check that the 2 workflow definition are different
		_, err = h.performStatusUpdate(ctx, workflow)
		return ctrl.Result{Requeue: true}, nil, err
	}

	// didn't change, business as usual
	image := pl.Spec.BuildPlatform.Registry.Address + "/" + workflow.Name + utils.GetWorkflowImageTag(workflow)
	return h.handleObjects(ctx, workflow, image)
}

func (h *deployWorkflowReconciliationState) handleObjects(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow, image string) (reconcile.Result, []client.Object, error) {
	// Check if this Deployment already exists
	existingDeployment := &appsv1.Deployment{}
	requeue := false
	if err := h.client.Get(ctx, client.ObjectKeyFromObject(workflow), existingDeployment); err != nil {
		if !errors.IsNotFound(err) {
			return reconcile.Result{Requeue: false}, nil, err
		}
		deployment, _, err := h.ensurers.deployment.ensure(ctx, workflow, defaultDeploymentMutateVisitor(workflow), naiveApplyImageDeploymentMutateVisitor(image))
		if err != nil {
			return reconcile.Result{}, nil, err
		}
		existingDeployment, _ = deployment.(*appsv1.Deployment)
		requeue = true
	}
	// TODO: verify if deployment is ready. See https://issues.redhat.com/browse/KOGITO-8524

	existingService := &v1.Service{}
	if err := h.client.Get(ctx, client.ObjectKeyFromObject(workflow), existingService); err != nil {
		if !errors.IsNotFound(err) {
			return reconcile.Result{Requeue: false}, nil, err
		}
		service, _, err := h.ensurers.service.ensure(ctx, workflow, defaultServiceMutateVisitor(workflow))
		if err != nil {
			return reconcile.Result{}, nil, err
		}
		existingService, _ = service.(*v1.Service)
		requeue = true
	}
	// TODO: verify if service is ready. See https://issues.redhat.com/browse/KOGITO-8524

	objs := []client.Object{existingDeployment, existingService}

	if !requeue {
		h.logger.Info("Skip reconcile: Deployment and service already exists",
			"Deployment.Namespace", existingDeployment.Namespace, "Deployment.Name", existingDeployment.Name)
		// TODO: very naive, the state should observe the Deployment's status: https://issues.redhat.com/browse/KOGITO-8524
		workflow.Status.Manager().MarkTrue(api.RunningConditionType)
		if _, err := h.performStatusUpdate(ctx, workflow); err != nil {
			return reconcile.Result{Requeue: false}, nil, err
		}
		return reconcile.Result{RequeueAfter: requeueAfterIsRunning}, objs, nil
	}

	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
	if _, err := h.performStatusUpdate(ctx, workflow); err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}
	return reconcile.Result{RequeueAfter: requeueAfterFollowDeployment}, objs, nil
}

// markRunningUnknownIfWorkflowChanged marks the workflow status as unknown to require a new build reconciliation
func markRunningUnknownIfWorkflowChanged(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	// TODO: make sure that we handle this differently and not a copy of the spec in the status attribute, this can potentially make the status field unreadable. See: https://issues.redhat.com/browse/KOGITO-8644
	if !utils.Compare(utils.GetWorkflowSpecHash(workflow.Status.Applied), utils.GetWorkflowSpecHash(workflow.Spec)) { // Let's check that the 2 workflow definition are different
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		return true
	}
	return false
}

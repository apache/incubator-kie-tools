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

package prod

import (
	"context"

	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/kiegroup/kogito-serverless-operator/api"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/controllers/builder"
	"github.com/kiegroup/kogito-serverless-operator/controllers/platform"
	"github.com/kiegroup/kogito-serverless-operator/controllers/profiles/common"
	"github.com/kiegroup/kogito-serverless-operator/log"
	"github.com/kiegroup/kogito-serverless-operator/utils"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
)

type newBuilderState struct {
	*common.StateSupport
}

func (h *newBuilderState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.GetTopLevelCondition().IsUnknown() ||
		workflow.Status.IsWaitingForPlatform() ||
		workflow.Status.IsBuildFailed()
}

func (h *newBuilderState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	_, err := platform.GetActivePlatform(ctx, h.C, workflow.Namespace)
	if err != nil {
		if errors.IsNotFound(err) {
			workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.WaitingForPlatformReason,
				"No active Platform for namespace %s so the workflow cannot be built.", workflow.Namespace)
			_, err = h.PerformStatusUpdate(ctx, workflow)
			return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
		}
		klog.V(log.E).ErrorS(err, "Failed to get active platform")
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}
	// If there is an active platform we have got all the information to build but...
	// ...let's check before if we have got already a build!
	buildManager := builder.NewSonataFlowBuildManager(ctx, h.C)
	build, err := buildManager.GetOrCreateBuild(workflow)
	if err != nil {
		//If we are not able to retrieve or create a Build CR for this Workflow we will mark
		klog.V(log.E).ErrorS(err, "Failed to retrieve or create a Build CR")
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.WaitingForBuildReason,
			"Failed to retrieve or create a Build CR", workflow.Namespace)
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	if build.Status.BuildPhase != operatorapi.BuildPhaseFailed {
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForBuildReason, "")
		_, err = h.PerformStatusUpdate(ctx, workflow)
	} else {
		// TODO: not ideal, but we will improve it on https://issues.redhat.com/browse/KOGITO-8792
		klog.V(log.I).InfoS("Build is in failed state, try to delete the SonataFlowBuild to restart a new build cycle")
	}

	return ctrl.Result{RequeueAfter: requeueAfterStartingBuild}, nil, err
}

type followBuildStatusState struct {
	*common.StateSupport
}

func (h *followBuildStatusState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsBuildRunningOrUnknown()
}

func (h *followBuildStatusState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// Let's retrieve the build to check the status
	build, err := builder.NewSonataFlowBuildManager(ctx, h.C).GetOrCreateBuild(workflow)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Failed to get or create the build for the workflow.")
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason, build.Status.Error)
		if _, err = h.PerformStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{}, nil, err
		}
		return ctrl.Result{RequeueAfter: common.RequeueAfterFailure}, nil, nil
	}

	if build.Status.BuildPhase == operatorapi.BuildPhaseSucceeded {
		klog.V(log.I).InfoS("Workflow build has finished")
		//If we have finished a build and the workflow is not running, we will start the provisioning phase
		workflow.Status.Manager().MarkTrue(api.BuiltConditionType)
		_, err = h.PerformStatusUpdate(ctx, workflow)
	} else if build.Status.BuildPhase == operatorapi.BuildPhaseFailed || build.Status.BuildPhase == operatorapi.BuildPhaseError {
		// TODO: we should handle build failures https://issues.redhat.com/browse/KOGITO-8792
		// TODO: ideally, we can have a configuration per platform of how many attempts we try to rebuild
		// TODO: to rebuild, just do buildManager.MarkToRestart. The controller will then try to rebuild the workflow.
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason,
			"Workflow %s build failed. Error: %s", workflow.Name, build.Status.Error)
		_, err = h.PerformStatusUpdate(ctx, workflow)
	}
	if err != nil {
		return ctrl.Result{}, nil, err
	}
	return ctrl.Result{RequeueAfter: requeueWhileWaitForBuild}, nil, nil
}

type deployWorkflowState struct {
	*common.StateSupport
	ensurers           *objectEnsurers
	deploymentVisitors []common.MutateVisitor
}

func (h *deployWorkflowState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	// If we have a built ready, we should deploy the object
	return workflow.Status.GetCondition(api.BuiltConditionType).IsTrue()
}

func (h *deployWorkflowState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// Guard to avoid errors while getting a new builder manager.
	// Maybe we can do typed errors in the buildManager and
	// have something like sonataerr.IsPlatformNotFound(err) instead.
	_, err := platform.GetActivePlatform(ctx, h.C, workflow.Namespace)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForPlatformReason,
			"No active Platform for namespace %s so the resWorkflowDef cannot be deployed. Waiting for an active platform", workflow.Namespace)
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}

	buildManager := builder.NewSonataFlowBuildManager(ctx, h.C)
	build, err := buildManager.GetOrCreateBuild(workflow)
	if err != nil {
		return ctrl.Result{}, nil, err
	}

	if h.isWorkflowChanged(workflow) { // Let's check that the 2 resWorkflowDef definition are different
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		if err = buildManager.MarkToRestart(build); err != nil {
			return ctrl.Result{}, nil, err
		}
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "Marked to restart")
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{Requeue: false}, nil, err
	}

	// didn't change, business as usual
	return h.handleObjects(ctx, workflow, build.Status.ImageTag)
}

func (h *deployWorkflowState) handleObjects(ctx context.Context, workflow *operatorapi.SonataFlow, image string) (reconcile.Result, []client.Object, error) {
	propsCM, _, err := h.ensurers.propertiesConfigMap.Ensure(ctx, workflow, common.WorkflowPropertiesMutateVisitor(workflow, common.DefaultApplicationProperties))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "Unable to retrieve the properties config map")
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	// Check if this Deployment already exists
	// TODO: we should NOT do this. The ensurers are there to do exactly this fetch. Review once we refactor this reconciliation algorithm. See https://issues.redhat.com/browse/KOGITO-8524
	existingDeployment := &appsv1.Deployment{}
	requeue := false
	if err := h.C.Get(ctx, client.ObjectKeyFromObject(workflow), existingDeployment); err != nil {
		if !errors.IsNotFound(err) {
			workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to verify if deployment is available due to ", err)
			_, err = h.PerformStatusUpdate(ctx, workflow)
			return reconcile.Result{Requeue: false}, nil, err
		}
		deployment, _, err :=
			h.ensurers.deployment.Ensure(
				ctx,
				workflow,
				h.getDeploymentMutateVisitors(workflow, image, propsCM.(*v1.ConfigMap))...,
			)
		if err != nil {
			workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, "Unable to perform the deploy due to ", err)
			_, err = h.PerformStatusUpdate(ctx, workflow)
			return reconcile.Result{}, nil, err
		}
		existingDeployment, _ = deployment.(*appsv1.Deployment)
		requeue = true
	}
	// TODO: verify if deployment is ready. See https://issues.redhat.com/browse/KOGITO-8524

	existingService := &v1.Service{}
	if err := h.C.Get(ctx, client.ObjectKeyFromObject(workflow), existingService); err != nil {
		if !errors.IsNotFound(err) {
			return reconcile.Result{Requeue: false}, nil, err
		}
		service, _, err := h.ensurers.service.Ensure(ctx, workflow, common.ServiceMutateVisitor(workflow))
		if err != nil {
			workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to make the service available due to ", err)
			_, err = h.PerformStatusUpdate(ctx, workflow)
			return reconcile.Result{}, nil, err
		}
		existingService, _ = service.(*v1.Service)
		requeue = true
	}
	// TODO: verify if service is ready. See https://issues.redhat.com/browse/KOGITO-8524

	objs := []client.Object{existingDeployment, existingService, propsCM}

	if !requeue {
		klog.V(log.I).InfoS("Skip reconcile: Deployment and service already exists",
			"Deployment.Namespace", existingDeployment.Namespace, "Deployment.Name", existingDeployment.Name)
		result, err := common.DeploymentHandler(h.C).SyncDeploymentStatus(ctx, workflow)
		if err != nil {
			return reconcile.Result{Requeue: false}, nil, err
		}

		if _, err := h.PerformStatusUpdate(ctx, workflow); err != nil {
			return reconcile.Result{Requeue: false}, nil, err
		}
		return result, objs, nil
	}

	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
	if _, err := h.PerformStatusUpdate(ctx, workflow); err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}
	return reconcile.Result{RequeueAfter: common.RequeueAfterFollowDeployment}, objs, nil
}

// getDeploymentMutateVisitors gets the deployment mutate visitors based on the current plat
func (h *deployWorkflowState) getDeploymentMutateVisitors(
	workflow *operatorapi.SonataFlow,
	image string,
	configMap *v1.ConfigMap) []common.MutateVisitor {
	if utils.IsOpenShift() {
		return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow),
			mountProdConfigMapsMutateVisitor(configMap),
			addOpenShiftImageTriggerDeploymentMutateVisitor(image),
			common.ImageDeploymentMutateVisitor(image)}
	}
	return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow),
		common.ImageDeploymentMutateVisitor(image),
		mountProdConfigMapsMutateVisitor(configMap)}
}

// isWorkflowChanged marks the workflow status as unknown to require a new build reconciliation
func (h *deployWorkflowState) isWorkflowChanged(workflow *operatorapi.SonataFlow) bool {
	generation := kubeutil.GetLastGeneration(workflow.Namespace, workflow.Name, h.C, context.TODO())
	if generation > workflow.Status.ObservedGeneration {
		return true
	}
	return false
}

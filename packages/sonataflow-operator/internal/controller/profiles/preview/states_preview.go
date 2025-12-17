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

package preview

import (
	"context"
	"fmt"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"

	klog "k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/builder"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

type newBuilderState struct {
	*common.StateSupport
	ensurers *ObjectEnsurers
}

func (h *newBuilderState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.GetTopLevelCondition().IsUnknown() ||
		workflow.Status.IsWaitingForPlatform() ||
		workflow.Status.IsBuildFailed()
}

func (h *newBuilderState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	pl, err := platform.GetActivePlatform(ctx, h.C, workflow.Namespace, true)
	if err != nil {
		if errors.IsNotFound(err) {
			workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.WaitingForPlatformReason,
				"No active Platform for namespace %s so the workflow cannot be built.", workflow.Namespace)
			_, err = h.PerformStatusUpdate(ctx, workflow)
			return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
		}
		// We won't record events here to avoid spamming multiple events to the object, the status should alert the admin
		// since a namespace without a platform means incorrect configuration.
		klog.V(log.E).ErrorS(err, "Failed to get active platform")
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}

	// Perform status updated to ensure workflow.Status.Services references are set before properties calculation.
	_, err = h.PerformStatusUpdate(ctx, workflow)
	// Ensure the user and managed properties are prepared before starting the build process, and thus, we make them
	// available at build time.
	userPropsCM, _, err := h.ensurers.userPropsConfigMap.Ensure(ctx, workflow)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, fmt.Sprintf("Unable to retrieve the user properties config map: %v", err))
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	_, _, err = h.ensurers.managedPropsConfigMap.Ensure(ctx, workflow, pl,
		common.ManagedPropertiesMutateVisitor(ctx, h.StateSupport.Catalog, workflow, pl, userPropsCM.(*corev1.ConfigMap)))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, fmt.Sprintf("Unable to retrieve the managed properties config map: %v", err))
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	// If there is an active platform we have got all the information to build but...
	// ...let's check before if we have got already a build!
	buildManager := builder.NewSonataFlowBuildManager(ctx, h.C)
	build, err := buildManager.GetOrCreateBuild(workflow)
	if err != nil {
		//If we are not able to retrieve or create a Build CR for this Workflow we will mark
		klog.V(log.E).ErrorS(err, "Failed to retrieve or create a Build CR")
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason,
			"Failed to retrieve or create a Build CR", workflow.Namespace)
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	if build.Status.BuildPhase != operatorapi.BuildPhaseFailed {
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForBuildReason, "")
		_, err = h.PerformStatusUpdate(ctx, workflow)
		h.Recorder.Eventf(workflow, corev1.EventTypeNormal, api.BuildIsRunningReason, "Workflow %s build has started.", workflow.Name)
	} else {
		klog.V(log.I).InfoS("Build is in failed state, you can mark the build to rebuild by setting to 'true' the ", "annotation", operatorapi.BuildRestartAnnotation)
	}

	return ctrl.Result{RequeueAfter: requeueAfterStartingBuild}, nil, err
}

func (h *newBuilderState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	//By default, we don't want to perform anything after the reconciliation, and so we will simply return no error
	return nil
}

type followBuildStatusState struct {
	*common.StateSupport
}

func (h *followBuildStatusState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsBuildRunningOrUnknown() || workflow.Status.IsWaitingForBuild()
}

func (h *followBuildStatusState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// Let's retrieve the build to check the status
	build, err := builder.NewSonataFlowBuildManager(ctx, h.C).GetOrCreateBuild(workflow)
	if err != nil {
		klog.V(log.E).ErrorS(err, "Failed to get or create the build for the workflow.")
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason, err.Error())
		if _, err = h.PerformStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{}, nil, err
		}
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, err
	}

	if build.Status.BuildPhase == operatorapi.BuildPhaseSucceeded {
		klog.V(log.I).InfoS("Workflow build has finished")
		if workflow.Status.IsReady() {
			// Rollout our deployment to take the latest changes in the new image.
			if err := common.DeploymentManager(h.C).RolloutDeployment(ctx, workflow); err != nil {
				return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, err
			}
			h.Recorder.Eventf(workflow, corev1.EventTypeNormal, api.WaitingForDeploymentReason, "Rolling out workflow %s deployment.", workflow.Name)
		}
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "Build has finished, rolling out deployment")
		//If we have finished a build and the workflow is not running, we will start the provisioning phase
		workflow.Status.Manager().MarkTrue(api.BuiltConditionType)
		_, err = h.PerformStatusUpdate(ctx, workflow)
		h.Recorder.Eventf(workflow, corev1.EventTypeNormal, api.BuildSuccessfulReason, "Workflow %s build has been finished successfully.", workflow.Name)
	} else if build.Status.BuildPhase == operatorapi.BuildPhaseFailed || build.Status.BuildPhase == operatorapi.BuildPhaseError {
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason,
			"Workflow %s build failed. Error: %s", workflow.Name, build.Status.Error)
		_, err = h.PerformStatusUpdate(ctx, workflow)
		h.Recorder.Eventf(workflow, corev1.EventTypeWarning, api.BuildFailedReason, "Workflow %s build has failed. Error: %s", workflow.Name, build.Status.Error)
	} else if build.Status.BuildPhase == operatorapi.BuildPhaseRunning && !workflow.Status.IsBuildRunning() {
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "")
		_, err = h.PerformStatusUpdate(ctx, workflow)
		h.Recorder.Eventf(workflow, corev1.EventTypeNormal, api.BuildIsRunningReason, "Workflow %s build is running.", workflow.Name)
	}

	if err != nil {
		return ctrl.Result{}, nil, err
	}

	return ctrl.Result{RequeueAfter: requeueWhileWaitForBuild}, nil, nil
}

func (h *followBuildStatusState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	//By default, we don't want to perform anything after the reconciliation, and so we will simply return no error
	return nil
}

type deployWithBuildWorkflowState struct {
	*common.StateSupport
	ensurers           *ObjectEnsurers
	deploymentVisitors []common.MutateVisitor
}

func (h *deployWithBuildWorkflowState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	// If we have a built ready, we should deploy the object
	return workflow.Status.GetCondition(api.BuiltConditionType).IsTrue()
}

func (h *deployWithBuildWorkflowState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// Guard to avoid errors while getting a new builder manager.
	// Maybe we can do typed errors in the buildManager and
	// have something like sonataerr.IsPlatformNotFound(err) instead.
	_, err := platform.GetActivePlatform(ctx, h.C, workflow.Namespace, true)
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

	hasChanged, err := h.isWorkflowChanged(workflow)
	if err != nil {
		return ctrl.Result{}, nil, err
	}
	if hasChanged { // Let's check that the 2 resWorkflowDef definition are different
		if err = buildManager.MarkToRestart(build); err != nil {
			return ctrl.Result{}, nil, err
		}
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "Build marked to restart")
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		_, err = h.PerformStatusUpdate(ctx, workflow)
		h.Recorder.Eventf(workflow, corev1.EventTypeNormal, api.BuildMarkedToRestartReason, "Workflow %s will start a new build.", workflow.Name)
		return ctrl.Result{Requeue: false}, nil, err
	}

	// didn't change, business as usual
	result, objs, err := NewDeploymentReconciler(h.StateSupport, h.ensurers).reconcileWithImage(ctx, workflow, build.Status.ImageTag)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, fmt.Sprintf("Error in deploy the workflow:%s", err))
		_, err = h.PerformStatusUpdate(ctx, workflow)
		return result, nil, err
	}
	return result, objs, err
}

func (h *deployWithBuildWorkflowState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	// Clean up the outdated Knative revisions, if any
	return common.CleanupOutdatedRevisions(ctx, h.Cfg, workflow)
}

// isWorkflowChanged checks whether the contents of .spec.flow of the given workflow has changed.
func (h *deployWithBuildWorkflowState) isWorkflowChanged(workflow *operatorapi.SonataFlow) (bool, error) {
	// Added this guard for backward compatibility for workflows deployed with a previous operator version, so we won't kick thousands of builds on users' cluster.
	// After this reconciliation cycle, the CRC should be updated
	if workflow.Status.FlowCRC == 0 {
		return false, nil
	}
	actualCRC, err := utils.Crc32Checksum(workflow.Spec.Flow)
	if err != nil {
		return false, err
	}
	return actualCRC != workflow.Status.FlowCRC, nil
}

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
	"time"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	"github.com/kiegroup/kogito-serverless-operator/utils"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
	"github.com/kiegroup/kogito-serverless-operator/workflowproj"

	"k8s.io/client-go/rest"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"

	"github.com/go-logr/logr"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/kiegroup/kogito-serverless-operator/api"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/controllers/builder"
	"github.com/kiegroup/kogito-serverless-operator/controllers/platform"
)

var _ ProfileReconciler = &prodProfile{}

type prodProfile struct {
	baseReconciler
}

const (
	requeueAfterStartingBuild   = 3 * time.Minute
	requeueWhileWaitForBuild    = 1 * time.Minute
	requeueWhileWaitForPlatform = 5 * time.Second

	quarkusProdConfigMountPath = "/deployments/config"
)

// prodObjectEnsurers is a struct for the objects that ReconciliationState needs to create in the platform for the Production profile.
// ReconciliationState that needs access to it must include this struct as an attribute and initialize it in the profile builder.
// Use newProdObjectEnsurers to facilitate building this struct
type prodObjectEnsurers struct {
	deployment          ObjectEnsurer
	service             ObjectEnsurer
	propertiesConfigMap ObjectEnsurer
}

func newProdObjectEnsurers(support *stateSupport) *prodObjectEnsurers {
	return &prodObjectEnsurers{
		deployment:          newDefaultObjectEnsurer(support.client, support.logger, defaultDeploymentCreator),
		service:             newDefaultObjectEnsurer(support.client, support.logger, defaultServiceCreator),
		propertiesConfigMap: newDefaultObjectEnsurer(support.client, support.logger, workflowPropsConfigMapCreator),
	}
}

func newProdProfileReconciler(client client.Client, config *rest.Config, logger *logr.Logger) ProfileReconciler {
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

func (p prodProfile) GetProfile() metadata.ProfileType {
	return metadata.ProdProfile
}

type newBuilderReconciliationState struct {
	*stateSupport
}

func (h *newBuilderReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.GetTopLevelCondition().IsUnknown() ||
		workflow.Status.IsWaitingForPlatform() ||
		workflow.Status.IsBuildFailed()
}

func (h *newBuilderReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
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
	buildManager := builder.NewKogitoServerlessBuildManager(ctx, h.client)
	build, err := buildManager.GetOrCreateBuild(workflow)
	if err != nil {
		return ctrl.Result{}, nil, err
	}

	if build.Status.BuildPhase != operatorapi.BuildPhaseFailed {
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForBuildReason, "")
		_, err = h.performStatusUpdate(ctx, workflow)
	} else {
		// TODO: not ideal, but we will improve it on https://issues.redhat.com/browse/KOGITO-8792
		h.logger.Info("Build is in failed state, try to delete the KogitoServerlessBuild to restart a new build cycle")
	}

	return ctrl.Result{RequeueAfter: requeueAfterStartingBuild}, nil, err
}

type followBuildStatusReconciliationState struct {
	*stateSupport
}

func (h *followBuildStatusReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.IsBuildRunningOrUnknown()
}

func (h *followBuildStatusReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	// Let's retrieve the build to check the status
	build, err := builder.NewKogitoServerlessBuildManager(ctx, h.client).GetOrCreateBuild(workflow)
	if err != nil {
		h.logger.Error(err, "Failed to get or create the build for the workflow.")
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason, build.Status.Error)
		if _, err = h.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{}, nil, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, nil
	}

	if build.Status.BuildPhase == operatorapi.BuildPhaseSucceeded {
		h.logger.Info("Workflow build has finished")
		//If we have finished a build and the workflow is not running, we will start the provisioning phase
		workflow.Status.Manager().MarkTrue(api.BuiltConditionType)
		_, err = h.performStatusUpdate(ctx, workflow)
	} else if build.Status.BuildPhase == operatorapi.BuildPhaseFailed || build.Status.BuildPhase == operatorapi.BuildPhaseError {
		// TODO: we should handle build failures https://issues.redhat.com/browse/KOGITO-8792
		// TODO: ideally, we can have a configuration per platform of how many attempts we try to rebuild
		// TODO: to rebuild, just do buildManager.MarkToRestart. The controller will then try to rebuild the workflow.
		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildFailedReason,
			"Workflow %s build failed. Error: %s", workflow.Name, build.Status.Error)
		_, err = h.performStatusUpdate(ctx, workflow)
	}
	if err != nil {
		return ctrl.Result{}, nil, err
	}
	return ctrl.Result{RequeueAfter: requeueWhileWaitForBuild}, nil, nil
}

type deployWorkflowReconciliationState struct {
	*stateSupport
	ensurers           *prodObjectEnsurers
	deploymentVisitors []mutateVisitor
}

func (h *deployWorkflowReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.GetCondition(api.BuiltConditionType).IsTrue()
}

func (h *deployWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	pl, err := platform.GetActivePlatform(ctx, h.client, workflow.Namespace)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForPlatformReason,
			"No active Platform for namespace %s so the resWorkflowDef cannot be deployed. Waiting for an active platform", workflow.Namespace)
		return ctrl.Result{RequeueAfter: requeueWhileWaitForPlatform}, nil, err
	}

	if h.isWorkflowChanged(workflow) { // Let's check that the 2 resWorkflowDef definition are different
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		buildManager := builder.NewKogitoServerlessBuildManager(ctx, h.client)
		build, err := buildManager.GetOrCreateBuild(workflow)
		if err != nil {
			return ctrl.Result{}, nil, err
		}
		if err = buildManager.MarkToRestart(build); err != nil {
			return ctrl.Result{}, nil, err
		}

		workflow.Status.Manager().MarkFalse(api.BuiltConditionType, api.BuildIsRunningReason, "Marked to restart")
		workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
		_, err = h.performStatusUpdate(ctx, workflow)
		return ctrl.Result{Requeue: false}, nil, err
	}

	// didn't change, business as usual
	image := workflowdef.GetWorkflowAppImageNameTag(workflow)
	if len(pl.Spec.BuildPlatform.Registry.Address) > 0 {
		image = pl.Spec.BuildPlatform.Registry.Address + "/" + image
	}
	return h.handleObjects(ctx, workflow, image)
}

func (h *deployWorkflowReconciliationState) handleObjects(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow, image string) (reconcile.Result, []client.Object, error) {
	// the dev one is ok for now
	propsCM, _, err := h.ensurers.propertiesConfigMap.ensure(ctx, workflow, ensureProdWorkflowPropertiesConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{}, nil, err
	}

	// Check if this Deployment already exists
	// TODO: we should NOT do this. The ensurers are there to do exactly this fetch. Review once we refactor this reconciliation algorithm. See https://issues.redhat.com/browse/KOGITO-8524
	existingDeployment := &appsv1.Deployment{}
	requeue := false
	if err := h.client.Get(ctx, client.ObjectKeyFromObject(workflow), existingDeployment); err != nil {
		if !errors.IsNotFound(err) {
			return reconcile.Result{Requeue: false}, nil, err
		}
		deployment, _, err :=
			h.ensurers.deployment.ensure(
				ctx,
				workflow,
				h.getDeploymentMutateVisitors(workflow, image, propsCM.(*v1.ConfigMap))...,
			)
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

	objs := []client.Object{existingDeployment, existingService, propsCM}

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

// getDeploymentMutateVisitors gets the deployment mutate visitors based on the current plat
func (h *deployWorkflowReconciliationState) getDeploymentMutateVisitors(workflow *operatorapi.KogitoServerlessWorkflow, image string, configMap *v1.ConfigMap) []mutateVisitor {
	if utils.IsOpenShift() {
		return []mutateVisitor{defaultDeploymentMutateVisitor(workflow),
			mountProdConfigMapsMutateVisitor(configMap),
			addOpenShiftImageTriggerDeploymentMutateVisitor(image),
			naiveApplyImageDeploymentMutateVisitor(image)}
	}
	return []mutateVisitor{defaultDeploymentMutateVisitor(workflow),
		naiveApplyImageDeploymentMutateVisitor(image),
		mountProdConfigMapsMutateVisitor(configMap)}
}

// mountDevConfigMapsMutateVisitor mounts the required configMaps in the Workflow Dev Deployment
func mountProdConfigMapsMutateVisitor(propsCM *v1.ConfigMap) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			deployment := object.(*appsv1.Deployment)
			volumes := make([]v1.Volume, 0)
			volumeMounts := make([]v1.VolumeMount, 0)

			volumes = append(volumes, kubeutil.VolumeConfigMap(configMapWorkflowPropsVolumeName, propsCM.Name, v1.KeyToPath{Key: workflowproj.ApplicationPropertiesFileName, Path: workflowproj.ApplicationPropertiesFileName}))

			volumeMounts = append(volumeMounts, kubeutil.VolumeMount(configMapWorkflowPropsVolumeName, true, quarkusProdConfigMountPath))

			deployment.Spec.Template.Spec.Volumes = make([]v1.Volume, 0)
			deployment.Spec.Template.Spec.Volumes = volumes
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = make([]v1.VolumeMount, 0)
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = volumeMounts

			return nil
		}
	}
}

// isWorkflowChanged marks the workflow status as unknown to require a new build reconciliation
func (h *deployWorkflowReconciliationState) isWorkflowChanged(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	generation := kubeutil.GetLastGeneration(workflow.Namespace, workflow.Name, h.client, context.TODO(), h.logger)
	if generation > workflow.Status.ObservedGeneration {
		return true
	}
	return false
}

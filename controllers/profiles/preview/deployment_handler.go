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

package preview

import (
	"context"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/knative"
	v1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
)

type DeploymentReconciler struct {
	*common.StateSupport
	ensurers *ObjectEnsurers
}

func NewDeploymentReconciler(stateSupport *common.StateSupport, ensurer *ObjectEnsurers) *DeploymentReconciler {
	return &DeploymentReconciler{
		StateSupport: stateSupport,
		ensurers:     ensurer,
	}
}

func (d *DeploymentReconciler) Reconcile(ctx context.Context, workflow *operatorapi.SonataFlow) (reconcile.Result, []client.Object, error) {
	return d.reconcileWithImage(ctx, workflow, "")
}

func (d *DeploymentReconciler) reconcileWithImage(ctx context.Context, workflow *operatorapi.SonataFlow, image string) (reconcile.Result, []client.Object, error) {
	// Checks if we need Knative installed and is not present.
	if requires, err := d.ensureKnativeServingRequired(workflow); requires || err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}

	// Ensure objects
	result, objs, err := d.ensureObjects(ctx, workflow, image)
	if err != nil || result.Requeue {
		return result, objs, err
	}

	// Follow deployment status
	result, err = common.DeploymentManager(d.C).SyncDeploymentStatus(ctx, workflow)
	if err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}

	if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}
	return result, objs, nil
}

// ensureKnativeServingRequired returns true if the SonataFlow instance requires Knative deployment and Knative Serving is not available.
func (d *DeploymentReconciler) ensureKnativeServingRequired(workflow *operatorapi.SonataFlow) (bool, error) {
	if workflow.IsKnativeDeployment() {
		avail, err := knative.GetKnativeAvailability(d.Cfg)
		if err != nil {
			return true, err
		}
		if !avail.Serving {
			d.Recorder.Eventf(workflow, v1.EventTypeWarning,
				"KnativeServingNotAvailable",
				"Knative Serving is not available in this cluster, can't deploy workflow. Please update the deployment model to %s",
				operatorapi.KubernetesDeploymentModel)
			return true, nil
		}
	}
	return false, nil
}

func (d *DeploymentReconciler) ensureObjects(ctx context.Context, workflow *operatorapi.SonataFlow, image string) (reconcile.Result, []client.Object, error) {
	pl, _ := platform.GetActivePlatform(ctx, d.C, workflow.Namespace)
	userPropsCM, _, err := d.ensurers.userPropsConfigMap.Ensure(ctx, workflow)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "Unable to retrieve the user properties config map")
		_, _ = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}
	managedPropsCM, _, err := d.ensurers.managedPropsConfigMap.Ensure(ctx, workflow, pl,
		common.ManagedPropertiesMutateVisitor(ctx, d.StateSupport.Catalog, workflow, pl, userPropsCM.(*v1.ConfigMap)))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "Unable to retrieve the managed properties config map")
		_, _ = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}

	deployment, deploymentOp, err :=
		d.ensurers.DeploymentByDeploymentModel(workflow).Ensure(ctx, workflow, pl,
			d.deploymentModelMutateVisitors(workflow, pl, image, userPropsCM.(*v1.ConfigMap), managedPropsCM.(*v1.ConfigMap))...)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to perform the deploy due to ", err)
		_, _ = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}

	service, _, err := d.ensurers.ServiceByDeploymentModel(workflow).Ensure(ctx, workflow, common.ServiceMutateVisitor(workflow))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to make the service available due to ", err)
		_, _ = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}

	eventingObjs, err := common.NewKnativeEventingHandler(d.StateSupport).Ensure(ctx, workflow)
	if err != nil {
		return reconcile.Result{}, nil, err
	}

	objs := []client.Object{deployment, managedPropsCM, service}
	if deploymentOp == controllerutil.OperationResultCreated {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
			return reconcile.Result{}, nil, err
		}
		return reconcile.Result{RequeueAfter: constants.RequeueAfterFollowDeployment, Requeue: true}, objs, nil
	}
	objs = append(objs, eventingObjs...)

	return reconcile.Result{}, objs, nil
}

func (d *DeploymentReconciler) deploymentModelMutateVisitors(
	workflow *operatorapi.SonataFlow,
	plf *operatorapi.SonataFlowPlatform,
	image string,
	userPropsCM *v1.ConfigMap,
	managedPropsCM *v1.ConfigMap) []common.MutateVisitor {

	if workflow.IsKnativeDeployment() {
		return []common.MutateVisitor{common.KServiceMutateVisitor(workflow, plf),
			common.ImageKServiceMutateVisitor(workflow, image),
			mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM)}
	}

	if utils.IsOpenShift() {
		return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow, plf),
			mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM),
			addOpenShiftImageTriggerDeploymentMutateVisitor(workflow, image),
			common.ImageDeploymentMutateVisitor(workflow, image),
			common.RolloutDeploymentIfCMChangedMutateVisitor(workflow, userPropsCM, managedPropsCM),
		}
	}
	return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow, plf),
		common.ImageDeploymentMutateVisitor(workflow, image),
		mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM),
		common.RolloutDeploymentIfCMChangedMutateVisitor(workflow, userPropsCM, managedPropsCM)}
}

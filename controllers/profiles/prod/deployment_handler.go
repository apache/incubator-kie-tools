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

	v1 "k8s.io/api/core/v1"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api"
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
)

type deploymentHandler struct {
	*common.StateSupport
	ensurers *objectEnsurers
}

func newDeploymentHandler(stateSupport *common.StateSupport, ensurer *objectEnsurers) *deploymentHandler {
	return &deploymentHandler{
		StateSupport: stateSupport,
		ensurers:     ensurer,
	}
}

func (d *deploymentHandler) handle(ctx context.Context, workflow *operatorapi.SonataFlow) (reconcile.Result, []client.Object, error) {
	return d.handleWithImage(ctx, workflow, "")
}

func (d *deploymentHandler) handleWithImage(ctx context.Context, workflow *operatorapi.SonataFlow, image string) (reconcile.Result, []client.Object, error) {
	pl, _ := platform.GetActivePlatform(ctx, d.C, workflow.Namespace)
	propsCM, _, err := d.ensurers.propertiesConfigMap.Ensure(ctx, workflow, common.WorkflowPropertiesMutateVisitor(ctx, d.StateSupport.Catalog, workflow, pl))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "Unable to retrieve the properties config map")
		_, err = d.PerformStatusUpdate(ctx, workflow)
		return ctrl.Result{}, nil, err
	}

	deployment, deploymentOp, err :=
		d.ensurers.deployment.Ensure(
			ctx,
			workflow,
			d.getDeploymentMutateVisitors(workflow, image, propsCM.(*v1.ConfigMap))...,
		)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to perform the deploy due to ", err)
		_, err = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}

	service, _, err := d.ensurers.service.Ensure(ctx, workflow, common.ServiceMutateVisitor(workflow))
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Unable to make the service available due to ", err)
		_, err = d.PerformStatusUpdate(ctx, workflow)
		return reconcile.Result{}, nil, err
	}

	objs := []client.Object{deployment, service, propsCM}

	if deploymentOp == controllerutil.OperationResultCreated {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
			return reconcile.Result{Requeue: false}, nil, err
		}
		return reconcile.Result{RequeueAfter: common.RequeueAfterFollowDeployment, Requeue: true}, objs, nil
	}

	// Follow deployment status
	result, err := common.DeploymentHandler(d.C).SyncDeploymentStatus(ctx, workflow)
	if err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}

	if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}
	return result, objs, nil
}

func (d *deploymentHandler) getDeploymentMutateVisitors(
	workflow *operatorapi.SonataFlow,
	image string,
	configMap *v1.ConfigMap) []common.MutateVisitor {
	if utils.IsOpenShift() {
		return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow),
			mountProdConfigMapsMutateVisitor(configMap),
			addOpenShiftImageTriggerDeploymentMutateVisitor(workflow, image),
			common.ImageDeploymentMutateVisitor(workflow, image)}
	}
	return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow),
		common.ImageDeploymentMutateVisitor(workflow, image),
		mountProdConfigMapsMutateVisitor(configMap)}
}

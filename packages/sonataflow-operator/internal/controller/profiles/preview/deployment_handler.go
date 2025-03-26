// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package preview

import (
	"context"
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/manager"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/eventing"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"

	"k8s.io/client-go/util/retry"

	"k8s.io/apimachinery/pkg/types"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/properties"

	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/workflowdef"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"

	v1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/monitoring"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
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

	previousStatus := workflow.Status
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

	d.updateLastTimeStatusNotified(workflow, previousStatus)
	if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
		return reconcile.Result{Requeue: false}, nil, err
	}
	d.scheduleWorkflowStatusChangeNotification(ctx, workflow)
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
	pl, _ := platform.GetActivePlatform(ctx, d.C, workflow.Namespace, true)
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

	objs := []client.Object{deployment, managedPropsCM, service}
	eventingObjs, err := common.NewKnativeEventingHandler(d.StateSupport, pl).Ensure(ctx, workflow)
	if err != nil {
		return reconcile.Result{}, nil, err
	}
	objs = append(objs, eventingObjs...)

	serviceMonitor, err := d.ensureServiceMonitor(ctx, workflow, pl)
	if err != nil {
		return reconcile.Result{}, nil, err
	}
	if serviceMonitor != nil {
		objs = append(objs, serviceMonitor)
	}

	if deploymentOp == controllerutil.OperationResultCreated {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err := d.PerformStatusUpdate(ctx, workflow); err != nil {
			return reconcile.Result{}, nil, err
		}
		return reconcile.Result{RequeueAfter: constants.RequeueAfterFollowDeployment, Requeue: true}, objs, nil
	}
	return reconcile.Result{}, objs, nil
}

func (d *DeploymentReconciler) ensureServiceMonitor(ctx context.Context, workflow *operatorapi.SonataFlow, pl *operatorapi.SonataFlowPlatform) (client.Object, error) {
	if monitoring.IsMonitoringEnabled(pl) {
		serviceMonitor, _, err := d.ensurers.ServiceMonitorByDeploymentModel(workflow).Ensure(ctx, workflow)
		return serviceMonitor, err
	}
	return nil, nil
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
			mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM),
			common.RestoreKServiceVolumeAndVolumeMountMutateVisitor(),
		}
	}

	if utils.IsOpenShift() {
		return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow, plf),
			mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM),
			addOpenShiftImageTriggerDeploymentMutateVisitor(workflow, image),
			common.ImageDeploymentMutateVisitor(workflow, image),
			common.RestoreDeploymentVolumeAndVolumeMountMutateVisitor(),
			common.RolloutDeploymentIfCMChangedMutateVisitor(workflow, userPropsCM, managedPropsCM),
		}
	}
	return []common.MutateVisitor{common.DeploymentMutateVisitor(workflow, plf),
		common.ImageDeploymentMutateVisitor(workflow, image),
		mountConfigMapsMutateVisitor(workflow, userPropsCM, managedPropsCM),
		common.RestoreDeploymentVolumeAndVolumeMountMutateVisitor(),
		common.RolloutDeploymentIfCMChangedMutateVisitor(workflow, userPropsCM, managedPropsCM)}
}

func (d *DeploymentReconciler) updateLastTimeStatusNotified(workflow *operatorapi.SonataFlow, previousStatus operatorapi.SonataFlowStatus) {
	previousRunningCondition := previousStatus.GetCondition(api.RunningConditionType)
	currentRunningCondition := workflow.Status.GetCondition(api.RunningConditionType)

	if previousRunningCondition == nil {
		previousRunningCondition = currentRunningCondition
	}
	if previousRunningCondition.Status != currentRunningCondition.Status || workflow.Status.LastTimeStatusNotified != nil && workflow.Status.LastTimeStatusNotified.Time.Before(manager.GetOperatorStartTime()) {
		workflow.Status.LastTimeStatusNotified = nil
	}
}

func (d *DeploymentReconciler) scheduleWorkflowStatusChangeNotification(ctx context.Context, workflow *operatorapi.SonataFlow) {
	if workflow.Status.LastTimeStatusNotified == nil {
		manager.GetSFCWorker().RunAsync(func() {
			if err := notifyWorkflowStatusChange(d.C, workflow.Name, workflow.Namespace); err != nil {
				klog.V(log.E).ErrorS(err, "Failed to notify workflow status change, controller will schedule a new retry.", "workflow", "namespace", workflow.Name, workflow.Namespace, err)
			}
		})
	}
}

func notifyWorkflowStatusChange(cli client.Client, wfName, wfNamespace string) error {
	var err error
	var uri string
	retryErr := retry.RetryOnConflict(retry.DefaultBackoff, func() error {
		workflow := &operatorapi.SonataFlow{}
		if err = cli.Get(context.Background(), types.NamespacedName{Name: wfName, Namespace: wfNamespace}, workflow); err != nil {
			return err
		}
		workflow = workflow.DeepCopy()
		available := workflow.Status.GetCondition(api.RunningConditionType).IsTrue()
		if uri, err = eventing.GetWorkflowDefinitionEventsTargetURL(cli, workflow); err != nil {
			return fmt.Errorf("failed to get workflow definition events target url to send the workflow definition status update event: %v", err)
		}
		if len(uri) == 0 {
			klog.V(log.D).Infof("No enabled DataIndex, nor Broker, nor Sink configuration was found to send the workflow definition status update event for workflow: %s, namespace: %s", workflow.Name, workflow.Namespace)
			return nil
		}

		ctx, cancel := context.WithTimeout(context.Background(), constants.EventDeliveryTimeout)
		defer cancel()
		evt := workflowdef.NewWorkflowDefinitionAvailabilityEvent(workflow, workflowdef.SonataFlowOperatorSource, properties.GetWorkflowEndpointUrl(workflow), available)
		if err = utils.SendCloudEventWithContext(evt, ctx, uri); err != nil {
			return fmt.Errorf("failed to send workflow definition status update event: %v", err)
			// Controller handle to program a new notification based on the LastTimeStatusNotified.
		} else {
			now := metav1.Now()
			// Register the LastTimeStatusNotified, the controller knows how to react based on that value.
			workflow.Status.LastTimeStatusNotified = &now
			if err = cli.Status().Update(context.Background(), workflow); err != nil {
				return err
			}
		}
		return nil
	})
	return retryErr
}

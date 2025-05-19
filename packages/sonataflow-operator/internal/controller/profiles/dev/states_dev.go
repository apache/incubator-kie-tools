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

package dev

import (
	"context"
	"time"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/client-go/util/retry"
	"k8s.io/klog/v2"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/monitoring"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/workflowdef"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"
)

const (
	configMapResourcesVolumeName               = "resources"
	configMapExternalResourcesVolumeNamePrefix = "res-"
	// quarkusDevConfigMountPath mount path for application properties file in the Workflow Quarkus Application
	// See: https://quarkus.io/guides/config-reference#application-properties-file
	quarkusDevConfigMountPath = "/home/kogito/serverless-workflow-project/src/main/resources"
)

type ensureRunningWorkflowState struct {
	*common.StateSupport
	ensurers *objectEnsurers
}

func (e *ensureRunningWorkflowState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsReady() || workflow.Status.GetTopLevelCondition().IsUnknown() || workflow.Status.IsChildObjectsProblem()
}

func (e *ensureRunningWorkflowState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	var objs []client.Object

	flowDefCM, _, err := e.ensurers.definitionConfigMap.Ensure(ctx, workflow, ensureWorkflowDefConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, flowDefCM)

	devBaseContainerImage := workflowdef.GetDefaultWorkflowDevModeImageTag()
	// check if the Platform available
	pl, err := platform.GetActivePlatform(context.TODO(), e.C, workflow.Namespace, true)
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	if pl != nil && len(pl.Spec.DevMode.BaseImage) > 0 {
		devBaseContainerImage = pl.Spec.DevMode.BaseImage
	}
	// make the route available before the properties calculation
	route, _, err := e.ensurers.network.Ensure(ctx, workflow)
	if err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
	}
	objs = append(objs, route)

	userPropsCM, _, err := e.ensurers.userPropsConfigMap.Ensure(ctx, workflow)
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	managedPropsCM, _, err := e.ensurers.managedPropsConfigMap.Ensure(ctx, workflow, pl, common.ManagedPropertiesMutateVisitor(ctx, e.Catalog, workflow, pl, userPropsCM.(*corev1.ConfigMap)))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, managedPropsCM)

	externalCM, err := workflowdef.FetchExternalResourcesConfigMapsRef(e.C, workflow)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "External Resources ConfigMap not found: %s", err.Error())
		if _, err = e.PerformStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
		}
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, nil
	}

	deployment, _, err := e.ensurers.deployment.Ensure(ctx, workflow, pl,
		deploymentMutateVisitor(workflow, pl),
		common.ImageDeploymentMutateVisitor(workflow, devBaseContainerImage),
		mountDevConfigMapsMutateVisitor(workflow, flowDefCM.(*corev1.ConfigMap), userPropsCM.(*corev1.ConfigMap), managedPropsCM.(*corev1.ConfigMap), externalCM))
	if err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
	}
	objs = append(objs, deployment)

	service, _, err := e.ensurers.service.Ensure(ctx, workflow, common.ServiceMutateVisitor(workflow))
	if err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
	}
	objs = append(objs, service)

	serviceMonitor, err := e.ensureServiceMonitor(ctx, workflow, pl)
	if err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
	}
	if serviceMonitor != nil {
		objs = append(objs, serviceMonitor)
	}

	// First time reconciling this object, mark as wait for deployment
	if workflow.Status.GetTopLevelCondition().IsUnknown() {
		klog.V(log.I).InfoS("Workflow is in WaitingForDeployment Condition")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err = e.PerformStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
		}
		return ctrl.Result{RequeueAfter: constants.RequeueAfterIsRunning}, objs, nil
	}

	// Is the deployment still available?
	convertedDeployment := deployment.(*appsv1.Deployment)
	if !kubeutil.IsDeploymentAvailable(convertedDeployment) {
		klog.V(log.I).InfoS("Workflow is not running due to a problem in the Deployment. Attempt to recover.")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType,
			api.DeploymentUnavailableReason,
			common.GetDeploymentUnavailabilityMessage(convertedDeployment))
		if _, err = e.PerformStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, objs, err
		}
	}

	return ctrl.Result{RequeueAfter: constants.RequeueAfterIsRunning}, objs, nil
}

func (e *ensureRunningWorkflowState) ensureServiceMonitor(ctx context.Context, workflow *operatorapi.SonataFlow, pl *operatorapi.SonataFlowPlatform) (client.Object, error) {
	if monitoring.IsMonitoringEnabled(pl) {
		serviceMonitor, _, err := e.ensurers.serviceMonitor.Ensure(ctx, workflow)
		return serviceMonitor, err
	}
	return nil, nil
}

func (e *ensureRunningWorkflowState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	//By default, we don't want to perform anything after the reconciliation, and so we will simply return no error
	return nil
}

type followWorkflowDeploymentState struct {
	*common.StateSupport
	enrichers *statusEnrichers
}

func (f *followWorkflowDeploymentState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsWaitingForDeployment()
}

func (f *followWorkflowDeploymentState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	result, err := common.DeploymentManager(f.C).SyncDeploymentStatus(ctx, workflow)
	if err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, err
	}

	if _, err := f.PerformStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, err
	}

	return result, nil, nil
}

func (f *followWorkflowDeploymentState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	deployment := &appsv1.Deployment{}
	if err := f.C.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		return err
	}
	if deployment != nil && kubeutil.IsDeploymentAvailable(deployment) {
		// Enriching Workflow CR status with needed network info
		if _, err := f.enrichers.networkInfo.Enrich(ctx, workflow); err != nil {
			return err
		}
		if _, err := f.PerformStatusUpdate(ctx, workflow); err != nil {
			return err
		}
	}
	return nil
}

type recoverFromFailureState struct {
	*common.StateSupport
}

func (r *recoverFromFailureState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.GetCondition(api.RunningConditionType).IsFalse()
}

func (r *recoverFromFailureState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// for now, a very basic attempt to recover by rolling out the deployment
	deployment := &appsv1.Deployment{}
	if err := r.C.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		// if the deployment is not there, let's try to reset the status condition and make the reconciliation fix the objects
		if errors.IsNotFound(err) {
			klog.V(log.I).InfoS("Tried to recover from failed state, no deployment found, trying to reset the workflow conditions")
			workflow.Status.RecoverFailureAttempts = 0
			workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
			if _, updateErr := r.PerformStatusUpdate(ctx, workflow); updateErr != nil {
				return ctrl.Result{Requeue: false}, nil, updateErr
			}
			return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, nil
		}
		return ctrl.Result{Requeue: false}, nil, err
	}

	// if the deployment is progressing we might have good news
	if kubeutil.IsDeploymentAvailable(deployment) {
		workflow.Status.RecoverFailureAttempts = 0
		workflow.Status.Manager().MarkTrue(api.RunningConditionType)
		if _, updateErr := r.PerformStatusUpdate(ctx, workflow); updateErr != nil {
			return ctrl.Result{Requeue: false}, nil, updateErr
		}
		return ctrl.Result{RequeueAfter: constants.RequeueAfterFailure}, nil, nil
	}

	if workflow.Status.RecoverFailureAttempts >= constants.RecoverDeploymentErrorRetries {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.RedeploymentExhaustedReason,
			"Can't recover workflow from failure after maximum attempts: %d", workflow.Status.RecoverFailureAttempts)
		if _, updateErr := r.PerformStatusUpdate(ctx, workflow); updateErr != nil {
			return ctrl.Result{}, nil, updateErr
		}
		return ctrl.Result{RequeueAfter: constants.RequeueRecoverDeploymentErrorInterval}, nil, nil
	}

	// TODO: we can improve deployment failures https://issues.redhat.com/browse/KOGITO-8812

	// Guard to avoid consecutive reconciliations to mess with the recover interval
	if !workflow.Status.LastTimeRecoverAttempt.IsZero() &&
		metav1.Now().Sub(workflow.Status.LastTimeRecoverAttempt.Time).Minutes() > 10 {
		return ctrl.Result{RequeueAfter: time.Minute * constants.RecoverDeploymentErrorInterval}, nil, nil
	}

	// let's try rolling out the deployment
	if err := kubeutil.MarkDeploymentToRollout(deployment); err != nil {
		return ctrl.Result{}, nil, err
	}
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		updateErr := r.C.Update(ctx, deployment)
		return updateErr
	})

	if retryErr != nil {
		klog.V(log.E).ErrorS(retryErr, "Error during Deployment rollout")
		return ctrl.Result{RequeueAfter: constants.RequeueRecoverDeploymentErrorInterval}, nil, nil
	}

	workflow.Status.RecoverFailureAttempts += 1
	workflow.Status.LastTimeRecoverAttempt = metav1.Now()
	if _, err := r.PerformStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{Requeue: false}, nil, err
	}
	return ctrl.Result{RequeueAfter: constants.RequeueRecoverDeploymentErrorInterval}, nil, nil
}

func (r *recoverFromFailureState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	//By default, we don't want to perform anything after the reconciliation, and so we will simply return no error
	return nil
}

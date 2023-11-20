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

package controllers

import (
	"context"
	"fmt"
	"time"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"k8s.io/klog/v2"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api"

	clientr "github.com/apache/incubator-kie-kogito-serverless-operator/container-builder/client"

	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform"

	ctrlrun "sigs.k8s.io/controller-runtime"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
)

// SonataFlowPlatformReconciler reconciles a SonataFlowPlatform object
type SonataFlowPlatformReconciler struct {
	// This Client, initialized using mgr.Client() above, is a split Client
	// that reads objects from the cache and writes to the API server
	ctrl.Client
	// Non-caching Client
	Reader   ctrl.Reader
	Scheme   *runtime.Scheme
	Config   *rest.Config
	Recorder record.EventRecorder
}

//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowplatforms,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowplatforms/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowplatforms/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// the SonataFlowPlatform object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.12.1/pkg/reconcile
func (r *SonataFlowPlatformReconciler) Reconcile(ctx context.Context, req reconcile.Request) (reconcile.Result, error) {
	// Make sure the operator is allowed to act on namespace
	if ok, err := platform.IsOperatorAllowedOnNamespace(ctx, r.Reader, req.Namespace); err != nil {
		return reconcile.Result{}, err
	} else if !ok {
		klog.V(log.I).InfoS("Ignoring request because the operator hasn't got the permissions to work on namespace", "namespace", req.Namespace)
		return reconcile.Result{}, nil
	}

	// Fetch the Platform instance
	var instance operatorapi.SonataFlowPlatform

	if err := r.Reader.Get(ctx, req.NamespacedName, &instance); err != nil {
		if errors.IsNotFound(err) {
			// Request object not found, could have been deleted after reconcile request.
			// Owned objects are automatically garbage collected. For additional cleanup
			// logic use finalizers.

			// Return and don't requeue
			return reconcile.Result{}, nil
		}
		// Error reading the object - requeue the request.
		return reconcile.Result{}, err
	}

	instance.Status.Manager().InitializeConditions()

	// Only process resources assigned to the operator
	if !platform.IsOperatorHandlerConsideringLock(ctx, r.Reader, req.Namespace, &instance) {
		klog.V(log.I).InfoS("Ignoring request because resource is not assigned to current operator")
		return reconcile.Result{}, nil
	}
	actions := []platform.Action{
		platform.NewInitializeAction(),
		platform.NewServiceAction(),
		platform.NewWarmAction(r.Reader),
		platform.NewCreateAction(),
		platform.NewMonitorAction(),
	}

	var err error

	target := instance.DeepCopy()

	for _, a := range actions {
		cli, _ := clientr.FromCtrlClientSchemeAndConfig(r.Client, r.Scheme, r.Config)
		a.InjectClient(cli)

		if a.CanHandle(target) {

			klog.V(log.I).InfoS("Invoking action", "Name", a.Name())

			target, err = a.Handle(ctx, target)
			if err != nil {
				target.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformFailureReason, err.Error())
				if err := r.Client.Status().Patch(ctx, target, ctrl.MergeFrom(&instance)); err != nil {
					return reconcile.Result{}, err
				}
				r.Recorder.Event(&instance, corev1.EventTypeWarning, "Failed", fmt.Sprintf("Failed to update SonataFlowPlaform: %s", err))
				return reconcile.Result{}, err
			}

			if target != nil {
				target.Status.ObservedGeneration = instance.Generation

				if err := r.Client.Status().Patch(ctx, target, ctrl.MergeFrom(&instance)); err != nil {
					r.Recorder.Event(&instance, corev1.EventTypeNormal, "Status Updated", fmt.Sprintf("Updated platform condition %s", instance.Status.GetTopLevelCondition()))
					return reconcile.Result{}, err
				}

				if err := r.Client.Update(ctx, target); err != nil {
					r.Recorder.Event(&instance, corev1.EventTypeNormal, "Spec Updated", fmt.Sprintf("Updated platform condition to %s", instance.Status.GetTopLevelCondition()))
					return reconcile.Result{}, err
				}
			}

			// handle one action at time so the resource
			// is always at its latest state
			r.Recorder.Event(&instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated platform condition to  %s", instance.Status.GetTopLevelCondition()))
			break
		}
	}

	if target != nil && target.Status.IsReady() {
		return reconcile.Result{}, nil
	}

	// Requeue
	return reconcile.Result{
		RequeueAfter: 5 * time.Second,
	}, nil

}

// SetupWithManager sets up the controller with the Manager.
func (r *SonataFlowPlatformReconciler) SetupWithManager(mgr ctrlrun.Manager) error {
	return ctrlrun.NewControllerManagedBy(mgr).
		For(&operatorapi.SonataFlowPlatform{}).
		Owns(&appsv1.Deployment{}).
		Owns(&corev1.Service{}).
		Owns(&corev1.ConfigMap{}).
		Complete(r)
}

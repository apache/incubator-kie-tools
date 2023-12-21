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

	"k8s.io/klog/v2"

	profiles "github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/factory"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/client-go/rest"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/handler"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-kogito-serverless-operator/api"

	"github.com/apache/incubator-kie-kogito-serverless-operator/log"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/platform"
)

// SonataFlowReconciler reconciles a SonataFlow object
type SonataFlowReconciler struct {
	Client   client.Client
	Scheme   *runtime.Scheme
	Config   *rest.Config
	Recorder record.EventRecorder
}

//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflows,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflows/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflows/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// the SonataFlow object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.11.2/pkg/reconcile
func (r *SonataFlowReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {

	// Make sure the operator is allowed to act on namespace
	if ok, err := platform.IsOperatorAllowedOnNamespace(ctx, r.Client, req.Namespace); err != nil {
		return reconcile.Result{}, err
	} else if !ok {
		klog.V(log.I).InfoS("Ignoring request because the operator hasn't got the permissions to work on namespace", "namespace", req.Namespace)
		return reconcile.Result{}, nil
	}

	// Fetch the Workflow instance
	workflow := &operatorapi.SonataFlow{}
	err := r.Client.Get(ctx, req.NamespacedName, workflow)
	if err != nil {
		if errors.IsNotFound(err) {
			return ctrl.Result{}, nil
		}
		klog.V(log.E).ErrorS(err, "Failed to get SonataFlow")
		return ctrl.Result{}, err
	}

	// Only process resources assigned to the operator
	if !platform.IsOperatorHandlerConsideringLock(ctx, r.Client, req.Namespace, workflow) {
		klog.V(log.I).InfoS("Ignoring request because resource is not assigned to current operator")
		return reconcile.Result{}, nil
	}

	return profiles.NewReconciler(r.Client, r.Config, r.Recorder, workflow).Reconcile(ctx, workflow)
}

func platformEnqueueRequestsFromMapFunc(c client.Client, p *operatorapi.SonataFlowPlatform) []reconcile.Request {
	var requests []reconcile.Request

	if p.Status.IsReady() {
		list := &operatorapi.SonataFlowList{}

		// Do global search in case of global operator (it may be using a global platform)
		var opts []client.ListOption
		if !platform.IsCurrentOperatorGlobal() {
			opts = append(opts, client.InNamespace(p.Namespace))
		}

		if err := c.List(context.Background(), list, opts...); err != nil {
			klog.V(log.E).ErrorS(err, "Failed to list workflows")
			return requests
		}

		for _, workflow := range list.Items {
			cond := workflow.Status.GetTopLevelCondition()
			if cond.IsFalse() && api.WaitingForPlatformReason == cond.Reason {
				klog.V(log.I).InfoS("Platform ready, wake-up workflow", "platform", p.Name, "workflow", workflow.Name)
				requests = append(requests, reconcile.Request{
					NamespacedName: types.NamespacedName{
						Namespace: workflow.Namespace,
						Name:      workflow.Name,
					},
				})
			}
		}
	}
	return requests
}

func buildEnqueueRequestsFromMapFunc(c client.Client, b *operatorapi.SonataFlowBuild) []reconcile.Request {
	var requests []reconcile.Request

	if b.Status.BuildPhase == operatorapi.BuildPhaseSucceeded {
		// Fetch the Workflow instance
		workflow := &operatorapi.SonataFlow{}
		namespacedName := types.NamespacedName{
			Namespace: workflow.Namespace,
			Name:      workflow.Name,
		}
		err := c.Get(context.Background(), namespacedName, workflow)
		if err != nil {
			if errors.IsNotFound(err) {
				return requests
			}
			klog.V(log.I).ErrorS(err, "Failed to get SonataFlow")
			return requests
		}

		if workflow.Status.IsBuildRunningOrUnknown() {
			klog.V(log.I).InfoS("Build %s ready, wake-up workflow: %s", b.Name, workflow.Name)
			requests = append(requests, reconcile.Request{
				NamespacedName: types.NamespacedName{
					Namespace: workflow.Namespace,
					Name:      workflow.Name,
				},
			})
		}

	}
	return requests
}

// SetupWithManager sets up the controller with the Manager.
func (r *SonataFlowReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&operatorapi.SonataFlow{}).
		Owns(&appsv1.Deployment{}).
		Owns(&corev1.Service{}).
		Owns(&corev1.ConfigMap{}).
		Owns(&operatorapi.SonataFlowBuild{}).
		Watches(&operatorapi.SonataFlowPlatform{}, handler.EnqueueRequestsFromMapFunc(func(c context.Context, a client.Object) []reconcile.Request {
			plat, ok := a.(*operatorapi.SonataFlowPlatform)
			if !ok {
				klog.V(log.E).InfoS("Failed to retrieve workflow list. Type assertion failed", "assertion", a)
				return []reconcile.Request{}
			}
			return platformEnqueueRequestsFromMapFunc(mgr.GetClient(), plat)
		})).
		Watches(&operatorapi.SonataFlowBuild{}, handler.EnqueueRequestsFromMapFunc(func(c context.Context, a client.Object) []reconcile.Request {
			build, ok := a.(*operatorapi.SonataFlowBuild)
			if !ok {
				klog.V(log.I).ErrorS(fmt.Errorf("type assertion failed: %v", a), "Failed to retrieve workflow list")
				return []reconcile.Request{}
			}
			return buildEnqueueRequestsFromMapFunc(mgr.GetClient(), build)
		})).
		Complete(r)
}

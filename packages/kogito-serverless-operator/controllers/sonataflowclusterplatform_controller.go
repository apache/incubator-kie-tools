// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//   http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package controllers

import (
	"context"
	"fmt"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	clientr "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/clusterplatform"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"k8s.io/klog/v2"
	ctrlrun "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/handler"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
)

// SonataFlowClusterPlatformReconciler reconciles a SonataFlowClusterPlatform object
type SonataFlowClusterPlatformReconciler struct {
	// This Client, initialized using mgr.Client() above, is a split Client
	// that reads objects from the cache and writes to the API server
	ctrl.Client
	// Non-caching Client
	Reader   ctrl.Reader
	Scheme   *runtime.Scheme
	Config   *rest.Config
	Recorder record.EventRecorder
}

//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowclusterplatforms,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowclusterplatforms/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=sonataflow.org,resources=sonataflowclusterplatforms/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// TODO(user): Modify the Reconcile function to compare the state specified by
// the SonataFlowClusterPlatform object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.13.0/pkg/reconcile
func (r *SonataFlowClusterPlatformReconciler) Reconcile(ctx context.Context, req reconcile.Request) (reconcile.Result, error) {

	// Fetch the SonataFlowClusterPlatform instance
	var instance operatorapi.SonataFlowClusterPlatform

	err := r.Client.Get(ctx, req.NamespacedName, &instance)
	if err != nil {
		if errors.IsNotFound(err) {
			return reconcile.Result{}, nil
		}
		klog.V(log.E).ErrorS(err, "Failed to get SonataFlowClusterPlatform")
		return reconcile.Result{}, err
	}

	instance.Status.Manager().InitializeConditions()

	cli, _ := clientr.FromCtrlClientSchemeAndConfig(r.Client, r.Scheme, r.Config)
	action := clusterplatform.NewInitializeAction()
	action.InjectClient(cli)
	klog.V(log.I).InfoS("Invoking action", "Name", action.Name())

	target := instance.DeepCopy()

	if action.CanHandle(ctx, target) {
		if err = action.Handle(ctx, target); err != nil {
			target.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformFailureReason, err.Error())
			if err := r.Client.Status().Patch(ctx, target, ctrl.MergeFrom(&instance)); err != nil {
				return reconcile.Result{}, err
			}
			r.Recorder.Event(&instance, corev1.EventTypeWarning, "Failed", fmt.Sprintf("Failed to update SonataFlowClusterPlaform: %s", err))
			return reconcile.Result{}, err
		}

		if target != nil {
			target.Status.ObservedGeneration = instance.Generation

			if err := r.Client.Status().Patch(ctx, target, ctrl.MergeFrom(&instance)); err != nil {
				r.Recorder.Event(&instance, corev1.EventTypeNormal, "Status Updated", fmt.Sprintf("Updated cluster platform condition %s", instance.Status.GetTopLevelCondition()))
				return reconcile.Result{}, err
			}
		}

		// handle one action at time so the resource
		// is always at its latest state
		r.Recorder.Event(&instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated cluster platform condition to  %s", instance.Status.GetTopLevelCondition()))

		if target != nil && target.Status.IsReady() {
			return reconcile.Result{}, nil
		}

		// Requeue
		return reconcile.Result{
			RequeueAfter: 5 * time.Second,
		}, nil
	}

	return reconcile.Result{}, nil
}

// SetupWithManager sets up the controller with the Manager.
func (r *SonataFlowClusterPlatformReconciler) SetupWithManager(mgr ctrlrun.Manager) error {
	return ctrlrun.NewControllerManagedBy(mgr).
		For(&operatorapi.SonataFlowClusterPlatform{}).
		Watches(&operatorapi.SonataFlowPlatform{}, handler.EnqueueRequestsFromMapFunc(r.mapPlatformToClusterPlatformRequests)).
		Watches(&operatorapi.SonataFlowClusterPlatform{}, handler.EnqueueRequestsFromMapFunc(r.mapClusterPlatformToClusterPlatformRequests)).
		Complete(r)
}

// if actively referenced sonataflowplatform object is changed, reconcile the active SonataFlowClusterPlatform.
func (r *SonataFlowClusterPlatformReconciler) mapPlatformToClusterPlatformRequests(ctx context.Context, object client.Object) []reconcile.Request {
	sfcPlatform, err := clusterplatform.GetActiveClusterPlatform(ctx, r.Client)
	if err != nil && !errors.IsNotFound(err) {
		klog.V(log.E).ErrorS(err, "Failed to get active SonataFlowClusterPlatform")
		return nil
	}

	if sfcPlatform != nil {
		sfpcRefNsName := types.NamespacedName{Namespace: sfcPlatform.Spec.PlatformRef.Namespace, Name: sfcPlatform.Spec.PlatformRef.Name}
		if client.ObjectKeyFromObject(object) == sfpcRefNsName {
			return []reconcile.Request{{NamespacedName: client.ObjectKeyFromObject(sfcPlatform)}}
		}
	}
	return nil
}

// if active sonataflowclusterplatform is changed, reconcile other SonataFlowClusterPlatforms.
func (r *SonataFlowClusterPlatformReconciler) mapClusterPlatformToClusterPlatformRequests(ctx context.Context, object client.Object) []reconcile.Request {
	sfcPlatform := object.(*operatorapi.SonataFlowClusterPlatform)
	var requests []reconcile.Request
	if sfcPlatform != nil && clusterplatform.IsActive(sfcPlatform) {
		var scpList operatorapi.SonataFlowClusterPlatformList
		if err := r.List(ctx, &scpList); err != nil {
			klog.V(log.E).ErrorS(err, "Could not list SonataFlowClusterPlatforms. "+
				"SonataFlowClusterPlatforms affected by changes to the active SonataFlowClusterPlatform %s will not be reconciled.",
				sfcPlatform.Name)
			return nil
		}

		scpNamespacedName := client.ObjectKeyFromObject(sfcPlatform)
		for _, cPlatform := range scpList.Items {
			namespacedName := client.ObjectKeyFromObject(&cPlatform)
			// this check is required so that the active clusterplatform object doesn't  reconcile
			if scpNamespacedName != namespacedName {
				requests = append(requests, reconcile.Request{NamespacedName: namespacedName})
			}
		}
	}
	return requests
}

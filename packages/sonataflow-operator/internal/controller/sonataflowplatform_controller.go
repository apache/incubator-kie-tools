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

package controller

import (
	"context"
	"fmt"
	"time"

	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/monitoring"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"k8s.io/klog/v2"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	ctrlrun "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/handler"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	clientr "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/clusterplatform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
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
//+kubebuilder:rbac:groups=batch,resources=jobs,verbs=get;list;watch;create;update;patch;delete

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

	if err = r.updateIfActiveClusterPlatformExists(ctx, req, target); err != nil {
		return reconcile.Result{}, err
	}

	// If the platform is being deleted, clean up the triggers on a different namespace
	if instance.DeletionTimestamp != nil && controllerutil.ContainsFinalizer(&instance, constants.TriggerFinalizer) {
		err := r.cleanupTriggers(ctx, &instance)
		if err != nil {
			klog.V(log.E).ErrorS(err, "Failed to clean up triggers for platform %s in namespace %s", instance.Name, instance.Namespace)
			return reconcile.Result{}, err
		}
		return reconcile.Result{}, nil
	}

	if monitoring.IsMonitoringEnabled(&instance) {
		monitoringAvail, err := monitoring.GetPrometheusAvailability(r.Config)
		if err != nil {
			return reconcile.Result{}, err
		}
		if !monitoringAvail {
			r.Recorder.Event(&instance, corev1.EventTypeWarning, "PrometheusNotAvailable", fmt.Sprintf("Monitoring is enabled in platform %s, but Prometheus is not installed", instance.Name))
		}
	}

	for _, a := range actions {
		cli, _ := clientr.FromCtrlClientSchemeAndConfig(r.Client, r.Scheme, r.Config)
		a.InjectClient(cli)

		if a.CanHandle(target) {

			klog.V(log.I).InfoS("Invoking action", "Name", a.Name())

			target, event, err := a.Handle(ctx, target)
			if event != nil {
				r.Recorder.Event(&instance, event.Type, event.Reason, event.Message)
			}
			if err != nil {
				if target != nil {
					target.Status.Manager().MarkFalse(api.SucceedConditionType, operatorapi.PlatformFailureReason, err.Error())
					if err = platform.SafeUpdatePlatformStatus(ctx, target); err != nil {
						return reconcile.Result{}, err
					}
				}
				r.Recorder.Event(&instance, corev1.EventTypeWarning, "Failed", fmt.Sprintf("Failed to update SonataFlowPlaform: %s", err))
				return reconcile.Result{}, err
			}

			if target != nil {
				target.Status.ObservedGeneration = instance.Generation
				if err = platform.SafeUpdatePlatform(ctx, target); err != nil {
					return reconcile.Result{}, err
				}

				err = platform.SafeUpdatePlatformStatus(ctx, target)
				if err != nil {
					return reconcile.Result{}, err
				}
			} else {
				return reconcile.Result{
					RequeueAfter: 5 * time.Second,
				}, nil
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

func (r *SonataFlowPlatformReconciler) cleanupTriggers(ctx context.Context, platform *operatorapi.SonataFlowPlatform) error {
	for _, triggerRef := range platform.Status.Triggers {
		trigger := &eventingv1.Trigger{
			ObjectMeta: metav1.ObjectMeta{
				Name:      triggerRef.Name,
				Namespace: triggerRef.Namespace,
			},
		}
		if err := r.Client.Delete(ctx, trigger); err != nil && !errors.IsNotFound(err) {
			return err
		}
	}
	controllerutil.RemoveFinalizer(platform, constants.TriggerFinalizer)
	return r.Client.Update(ctx, platform)
}

// sonataFlowPlatformUpdateStatus If an active cluster platform exists, update platform.Status accordingly
func (r *SonataFlowPlatformReconciler) updateIfActiveClusterPlatformExists(ctx context.Context, req reconcile.Request, target *operatorapi.SonataFlowPlatform) error {
	// Fetch the active SonataFlowClusterPlatform instance
	sfcPlatform, err := clusterplatform.GetActiveClusterPlatform(ctx)
	if err != nil && !errors.IsNotFound(err) {
		klog.V(log.E).ErrorS(err, "Failed to get active SonataFlowClusterPlatform")
		return err
	}

	if sfcPlatform != nil {
		sfPlatform := &operatorapi.SonataFlowPlatform{}

		platformRef := sfcPlatform.Spec.PlatformRef
		namespacedName := types.NamespacedName{Namespace: platformRef.Namespace, Name: platformRef.Name}
		if req.NamespacedName == namespacedName {
			sfPlatform = target.DeepCopy()
		} else {
			// retrieve referenced platform object
			err := r.Reader.Get(ctx, namespacedName, sfPlatform)
			if err != nil && !errors.IsNotFound(err) {
				klog.V(log.E).ErrorS(err, "Failed to get referenced SonataFlowPlatform", namespacedName)
				return err
			}
		}

		target.Status.ClusterPlatformRef = &operatorapi.SonataFlowClusterPlatformRefStatus{
			Name: sfcPlatform.Name,
			PlatformRef: operatorapi.SonataFlowPlatformRef{
				Name:      platformRef.Name,
				Namespace: platformRef.Namespace,
			},
		}

		if sfcPlatform.Spec.Capabilities != nil && contains(sfcPlatform.Spec.Capabilities.Workflows, clusterplatform.PlatformServices) {
			tpsDI := services.NewDataIndexHandler(target)
			tpsDI.SetServiceUrlInPlatformStatus(sfPlatform)

			tpsJS := services.NewJobServiceHandler(target)
			tpsJS.SetServiceUrlInPlatformStatus(sfPlatform)
		}
	} else {
		target.Status.ClusterPlatformRef = nil
	}

	return nil
}

// SetupWithManager sets up the controller with the Manager.
func (r *SonataFlowPlatformReconciler) SetupWithManager(mgr ctrlrun.Manager) error {
	builder := ctrlrun.NewControllerManagedBy(mgr).
		For(&operatorapi.SonataFlowPlatform{}).
		Owns(&appsv1.Deployment{}).
		Owns(&corev1.Service{}).
		Owns(&corev1.ConfigMap{}).
		Watches(&operatorapi.SonataFlowPlatform{}, handler.EnqueueRequestsFromMapFunc(r.mapPlatformToPlatformRequests)).
		Watches(&operatorapi.SonataFlowClusterPlatform{}, handler.EnqueueRequestsFromMapFunc(r.mapClusterPlatformToPlatformRequests))

	knativeAvail, err := knative.GetKnativeAvailability(mgr.GetConfig())
	if err != nil {
		return err
	}
	if knativeAvail.Eventing {
		builder = builder.Owns(&eventingv1.Trigger{}).
			Owns(&sourcesv1.SinkBinding{}).
			Watches(&eventingv1.Trigger{}, handler.EnqueueRequestsFromMapFunc(knative.MapTriggerToPlatformRequests))
	}
	return builder.Complete(r)
}

// if active clusterplatform object is changed, reconcile all SonataFlowPlatforms in the cluster.
func (r *SonataFlowPlatformReconciler) mapClusterPlatformToPlatformRequests(ctx context.Context, object client.Object) []reconcile.Request {
	sfcPlatform := object.(*operatorapi.SonataFlowClusterPlatform)
	if sfcPlatform != nil && clusterplatform.IsActive(sfcPlatform) {
		return r.platformRequests(ctx, sfcPlatform, true)
	}
	return nil
}

// if actively referenced sonataflowplatform is changed, reconcile other SonataFlowPlatforms in the cluster.
func (r *SonataFlowPlatformReconciler) mapPlatformToPlatformRequests(ctx context.Context, object client.Object) []reconcile.Request {
	platform := object.(*operatorapi.SonataFlowPlatform)
	sfcPlatform, err := clusterplatform.GetActiveClusterPlatform(ctx)
	if err != nil && !errors.IsNotFound(err) {
		klog.V(log.E).ErrorS(err, "Failed to get active SonataFlowClusterPlatform")
		return nil
	}

	if sfcPlatform != nil {
		sfpcRefNsName := types.NamespacedName{Namespace: sfcPlatform.Spec.PlatformRef.Namespace, Name: sfcPlatform.Spec.PlatformRef.Name}
		if client.ObjectKeyFromObject(platform) == sfpcRefNsName {
			return r.platformRequests(ctx, sfcPlatform, false)
		}
	}
	return nil
}

func (r *SonataFlowPlatformReconciler) platformRequests(ctx context.Context, sfcPlatform *operatorapi.SonataFlowClusterPlatform, allPlatforms bool) []reconcile.Request {
	var plList operatorapi.SonataFlowPlatformList
	if err := r.List(ctx, &plList, client.InNamespace("")); err != nil {
		klog.V(log.E).ErrorS(err, "could not list SonataFlowPlatforms. "+
			"SonataFlowPlatforms affected by changes to the active SonataFlowPlatform or SonataFlowClusterPlatform object will not be reconciled.")
		return nil
	}

	sfpcRefNsName := types.NamespacedName{Namespace: sfcPlatform.Spec.PlatformRef.Namespace, Name: sfcPlatform.Spec.PlatformRef.Name}
	var requests []reconcile.Request
	for _, platform := range plList.Items {
		sfpNsName := client.ObjectKeyFromObject(&platform)
		// this check is required so that the cluster-referenced platform object doesn't infinitely reconcile
		if sfpNsName != sfpcRefNsName || allPlatforms {
			requests = append(requests, reconcile.Request{NamespacedName: sfpNsName})
		}
	}
	return requests
}

func contains(slice []operatorapi.WorkFlowCapability, s operatorapi.WorkFlowCapability) bool {
	for _, a := range slice {
		if a == s {
			return true
		}
	}
	return false
}

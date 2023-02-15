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

package controllers

import (
	"context"
	"fmt"
	"time"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/record"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"

	clientr "github.com/kiegroup/container-builder/client"
	klog "github.com/kiegroup/container-builder/util/log"

	"github.com/kiegroup/kogito-serverless-operator/platform"

	ctrlrun "sigs.k8s.io/controller-runtime"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/log"

	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
)

// KogitoServerlessPlatformReconciler reconciles a KogitoServerlessPlatform object
type KogitoServerlessPlatformReconciler struct {
	// This Client, initialized using mgr.Client() above, is a split Client
	// that reads objects from the cache and writes to the API server
	client.Client
	// Non-caching Client
	Reader   ctrl.Reader
	Scheme   *runtime.Scheme
	Config   *rest.Config
	Recorder record.EventRecorder
}

//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessplatforms,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessplatforms/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessplatforms/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// the KogitoServerlessPlatform object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.12.1/pkg/reconcile
func (r *KogitoServerlessPlatformReconciler) Reconcile(ctx context.Context, req reconcile.Request) (reconcile.Result, error) {
	logger := log.FromContext(ctx)

	// Make sure the operator is allowed to act on namespace
	if ok, err := platform.IsOperatorAllowedOnNamespace(ctx, r.Reader, req.Namespace); err != nil {
		return reconcile.Result{}, err
	} else if !ok {
		logger.Info(fmt.Sprintf("Ignoring request because the operator hasn't got the permissions to work on namespace %s", req.Namespace))
		return reconcile.Result{}, nil
	}

	// Fetch the Platform instance
	var instance v08.KogitoServerlessPlatform

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

	// Only process resources assigned to the operator
	if !platform.IsOperatorHandlerConsideringLock(ctx, r.Reader, req.Namespace, &instance) {
		logger.Info("Ignoring request because resource is not assigned to current operator")
		return reconcile.Result{}, nil
	}
	actions := []platform.Action{
		platform.NewInitializeAction(),
		platform.NewWarmAction(r.Reader),
		platform.NewCreateAction(),
		platform.NewMonitorAction(),
	}

	var targetPhase v08.PlatformPhase

	var err error

	target := instance.DeepCopy()
	targetLog := klog.Log

	for _, a := range actions {
		cli, _ := clientr.FromCtrlClientSchemeAndConfig(r.Client, r.Scheme, r.Config)
		a.InjectClient(cli)
		a.InjectLogger(targetLog)

		if a.CanHandle(target) {
			targetLog.Info("Invoking action", "Name", a.Name())

			phaseFrom := target.Status.Phase

			target, err = a.Handle(ctx, target)
			if err != nil {
				r.Recorder.Event(&instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated platform phase to  %s", instance.Status.Phase))
				return reconcile.Result{}, err
			}

			if target != nil {
				target.Status.ObservedGeneration = instance.Generation

				if err := r.Client.Status().Patch(ctx, target, ctrl.MergeFrom(&instance)); err != nil {
					r.Recorder.Event(&instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated platform phase to  %s", instance.Status.Phase))
					return reconcile.Result{}, err
				}

				targetPhase = target.Status.Phase

				if targetPhase != phaseFrom {
					logger.Info(
						"state transition",
						"phase-from", phaseFrom,
						"phase-to", target.Status.Phase,
					)
				}
			}

			// handle one action at time so the resource
			// is always at its latest state
			r.Recorder.Event(&instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated platform phase to  %s", instance.Status.Phase))
			break
		}
	}

	if targetPhase == v08.PlatformPhaseReady {
		return reconcile.Result{}, nil
	}

	// Requeue
	return reconcile.Result{
		RequeueAfter: 5 * time.Second,
	}, nil

}

// SetupWithManager sets up the controller with the Manager.
func (r *KogitoServerlessPlatformReconciler) SetupWithManager(mgr ctrlrun.Manager) error {
	return ctrlrun.NewControllerManagedBy(mgr).
		For(&v08.KogitoServerlessPlatform{}).
		Complete(r)
}

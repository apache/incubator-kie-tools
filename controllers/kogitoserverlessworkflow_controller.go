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

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/handler"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"sigs.k8s.io/controller-runtime/pkg/source"

	"github.com/kiegroup/kogito-serverless-operator/controllers/profiles"

	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/util/log"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/platform"
	"github.com/kiegroup/kogito-serverless-operator/utils"
)

// KogitoServerlessWorkflowReconciler reconciles a KogitoServerlessWorkflow object
type KogitoServerlessWorkflowReconciler struct {
	Client   client.Client
	Scheme   *runtime.Scheme
	Recorder record.EventRecorder
}

//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessworkflows,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessworkflows/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessworkflows/finalizers,verbs=update
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=pods,verbs=get;watch;list

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// the KogitoServerlessWorkflow object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.11.2/pkg/reconcile
func (r *KogitoServerlessWorkflowReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {
	logger := ctrllog.FromContext(ctx)

	// Make sure the operator is allowed to act on namespace
	if ok, err := platform.IsOperatorAllowedOnNamespace(ctx, r.Client, req.Namespace); err != nil {
		return reconcile.Result{}, err
	} else if !ok {
		logger.Info(fmt.Sprintf("Ignoring request because the operator hasn't got the permissions to work on namespace %s", req.Namespace))
		return reconcile.Result{}, nil
	}

	// Fetch the Workflow instance
	workflow := &operatorapi.KogitoServerlessWorkflow{}
	err := r.Client.Get(ctx, req.NamespacedName, workflow)
	if err != nil {
		if errors.IsNotFound(err) {
			return ctrl.Result{}, nil
		}
		logger.Error(err, "Failed to get KogitoServerlessWorkflow")
		return ctrl.Result{}, err
	}

	// Only process resources assigned to the operator
	if !platform.IsOperatorHandlerConsideringLock(ctx, r.Client, req.Namespace, workflow) {
		logger.Info("Ignoring request because resource is not assigned to current operator")
		return reconcile.Result{}, nil
	}

	return profiles.NewReconciler(r.Client, &logger, workflow).Reconcile(ctx, workflow)
}

func buildEnqueueRequestsFromMapFunc(c client.Client, build *operatorapi.KogitoServerlessBuild) []reconcile.Request {
	var requests []reconcile.Request
	if build.Status.BuildPhase != api.BuildPhaseSucceeded && build.Status.BuildPhase != api.BuildPhaseError {
		return requests
	}

	list := &operatorapi.KogitoServerlessWorkflowList{}
	// Do global search in case of global operator (it may be using a global platform)
	var opts []client.ListOption
	if !platform.IsCurrentOperatorGlobal() {
		opts = append(opts, client.InNamespace(build.Namespace))
	}
	if err := c.List(context.Background(), list, opts...); err != nil {
		log.Error(err, "Failed to retrieve workflow list")
		return requests
	}

	for i := range list.Items {
		workflow := &list.Items[i]

		match, err := utils.SameOrMatch(build, workflow)
		if err != nil {
			log.Errorf(err, "Error matching workflow %q with build %q", workflow.Name, build.Name)
			continue
		}
		if !match {
			continue
		}

		if workflow.Status.Condition == operatorapi.BuildingConditionType || workflow.Status.Condition == operatorapi.RunningConditionType {
			log.Infof("Build %s ready, notify workflow: %s in condition %s", build.Name, workflow.Name, workflow.Status.Condition)
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

func platformEnqueueRequestsFromMapFunc(c client.Client, p *operatorapi.KogitoServerlessPlatform) []reconcile.Request {
	var requests []reconcile.Request

	if p.Status.Phase == operatorapi.PlatformPhaseReady {
		list := &operatorapi.KogitoServerlessWorkflowList{}

		// Do global search in case of global operator (it may be using a global platform)
		var opts []client.ListOption
		if !platform.IsCurrentOperatorGlobal() {
			opts = append(opts, client.InNamespace(p.Namespace))
		}

		if err := c.List(context.Background(), list, opts...); err != nil {
			log.Error(err, "Failed to list workflows")
			return requests
		}

		for _, workflow := range list.Items {
			if workflow.Status.Condition == operatorapi.WaitingForPlatformConditionType {
				log.Infof("Platform %s ready, wake-up workflow: %s", p.Name, workflow.Name)
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

// SetupWithManager sets up the controller with the Manager.
func (r *KogitoServerlessWorkflowReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&operatorapi.KogitoServerlessWorkflow{}).
		Watches(&source.Kind{Type: &operatorapi.KogitoServerlessBuild{}}, handler.EnqueueRequestsFromMapFunc(func(c client.Object) []reconcile.Request {
			build, ok := c.(*operatorapi.KogitoServerlessBuild)
			if !ok {
				log.Error(fmt.Errorf("type assertion failed: %v", c), "Failed to retrieve workflow list")
				return []reconcile.Request{}
			}
			return buildEnqueueRequestsFromMapFunc(mgr.GetClient(), build)
		})).
		Watches(&source.Kind{Type: &operatorapi.KogitoServerlessPlatform{}}, handler.EnqueueRequestsFromMapFunc(func(a client.Object) []reconcile.Request {
			platform, ok := a.(*operatorapi.KogitoServerlessPlatform)
			if !ok {
				log.Error(fmt.Errorf("type assertion failed: %v", a), "Failed to retrieve workflow list")
				return []reconcile.Request{}
			}
			return platformEnqueueRequestsFromMapFunc(mgr.GetClient(), platform)
		})).
		Complete(r)
}

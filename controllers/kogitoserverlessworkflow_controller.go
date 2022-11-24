/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import (
	"context"
	"fmt"
	"github.com/kiegroup/container-builder/api"
	"github.com/kiegroup/container-builder/util/log"
	apiv08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
	"github.com/kiegroup/kogito-serverless-operator/platform"
	"github.com/kiegroup/kogito-serverless-operator/utils"
	"github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
	appsv1 "k8s.io/api/apps/v1"
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
	"time"
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
	log := ctrllog.FromContext(ctx)

	// Make sure the operator is allowed to act on namespace
	if ok, err := platform.IsOperatorAllowedOnNamespace(ctx, r.Client, req.Namespace); err != nil {
		return reconcile.Result{}, err
	} else if !ok {
		log.Info(fmt.Sprintf("Ignoring request because the operator hasn't got the permissions to work on namespace %s", req.Namespace))
		return reconcile.Result{}, nil
	}

	// Fetch the Workflow instance
	workflow := &apiv08.KogitoServerlessWorkflow{}
	err := r.Client.Get(ctx, req.NamespacedName, workflow)
	if err != nil {
		if errors.IsNotFound(err) {
			return ctrl.Result{}, nil
		}
		log.Error(err, "Failed to get KogitoServerlessWorkflow")
		return ctrl.Result{}, err
	}

	// Only process resources assigned to the operator
	if !platform.IsOperatorHandlerConsideringLock(ctx, r.Client, req.Namespace, workflow) {
		log.Info("Ignoring request because resource is not assigned to current operator")
		return reconcile.Result{}, nil
	}

	switch workflow.Status.Condition {

	// If the status condition is None or Waiting for platform let's try to start a build!
	case apiv08.NoneConditionType, apiv08.WaitingForPlatformConditionType:
		_, err := platform.GetActivePlatform(ctx, r.Client, req.Namespace)
		if err != nil {
			log.Error(err, "No active Platform for namespace %s so the workflow cannot be built. Waiting for an active platform")
			workflow.Status.Condition = apiv08.WaitingForPlatformConditionType
			_, err = r.performStatusUpdate(ctx, workflow)
			return ctrl.Result{RequeueAfter: 5 * time.Second}, err
		}
		// If there is an active platform we have got all the information to build but...
		// ...let's check before if we have got already a build!
		build := &apiv08.KogitoServerlessBuild{}
		err = r.Client.Get(ctx, req.NamespacedName, build)
		if err != nil {
			if !errors.IsNotFound(err) {
				return ctrl.Result{}, err
			}
			//If there isn't a build let's create and start the first one!
			buildable := builder.NewBuildable(r.Client, ctx)
			_, err = buildable.HandleWorkflowBuild(workflow.Name, req)
		} else {
			//If there is a build, let's ask to restart it
			build.Status.BuildPhase = api.BuildPhaseNone
			build.Status.Builder.Status = api.BuildStatus{}
			if err = r.Client.Status().Update(ctx, build); err != nil {
				log.Error(err, fmt.Sprintf("Failed to update Build status for Workflow %s", workflow.Name))
				return ctrl.Result{}, err
			}
		}
		workflow.Status.Condition = apiv08.BuildingConditionType
		_, err = r.performStatusUpdate(ctx, workflow)
		return ctrl.Result{}, err

	// If the status condition is Building let's check if the build is finished!
	case apiv08.BuildingConditionType, apiv08.RunningConditionType:
		// Let's retrieve the build to check the status
		build := &apiv08.KogitoServerlessBuild{}
		err = r.Client.Get(ctx, req.NamespacedName, build)
		if err != nil {
			if !errors.IsNotFound(err) {
				return ctrl.Result{}, err
			}
			log.Error(err, "Build not found for this workflow", "Workflow", req.Name)
			return ctrl.Result{}, nil
		}

		if build.Status.Builder.Status.Phase == api.BuildPhaseSucceeded && workflow.Status.Condition != apiv08.RunningConditionType {
			//If we have finished a build and the workflow is not running, we will start the provisioning phase
			workflow.Status.Condition = apiv08.ProvisioningConditionType
			r.performStatusUpdate(ctx, workflow)
			return ctrl.Result{}, err
		} else if (build.Status.Builder.Status.Phase == api.BuildPhaseFailed || build.Status.Builder.Status.Phase == api.BuildPhaseError) && workflow.Status.Condition != apiv08.RunningConditionType {
			log.Info(fmt.Sprintf("Workflow %s build is failed!", workflow.Name))
			workflow.Status.Condition = apiv08.FailedConditionType
			r.performStatusUpdate(ctx, workflow)
		} else if (build.Status.Builder.Status.Phase == api.BuildPhaseSucceeded || build.Status.Builder.Status.Phase == api.BuildPhaseFailed || build.Status.Builder.Status.Phase == api.BuildPhaseError) && workflow.Status.Condition == apiv08.RunningConditionType {
			//If we have finished a build and the workflow is running, we have to rebuild it because there was a change in the workflow definition and requeue the request
			if !utils.Compare(utils.GetWorkflowSpecHash(workflow.Status.Applied), utils.GetWorkflowSpecHash(workflow.Spec)) { // Let's check that the 2 workflow definition are different
				workflow.Status.Condition = apiv08.NoneConditionType
				r.performStatusUpdate(ctx, workflow)
				return ctrl.Result{Requeue: true}, err
			}

		} else {
			if workflow.Status.Condition != apiv08.BuildingConditionType {
				workflow.Status.Condition = apiv08.BuildingConditionType
				r.performStatusUpdate(ctx, workflow)
			}
			return ctrl.Result{}, err
		}

	// If the status condition is Deploying let's check if the deployment is finished!
	case apiv08.ProvisioningConditionType, apiv08.DeployingConditionType:
		pl, err := platform.GetActivePlatform(ctx, r.Client, req.Namespace)
		if err != nil {
			log.Error(err, "No active Platform for namespace %s so the workflow cannot be deployed. Waiting for an active platform")
			return ctrl.Result{RequeueAfter: 5 * time.Second}, err
		}
		return r.manageBasicWorkflowDeployment(ctx, workflow, pl)

	default:
		log.Info(fmt.Sprintf("Workflow %s is in status %s but at the moment we are not supporting it!", workflow.Name, workflow.Status.Condition))
		return ctrl.Result{}, err
	}
	return ctrl.Result{}, err
}

func (r *KogitoServerlessWorkflowReconciler) performStatusUpdate(ctx context.Context, workflow *apiv08.KogitoServerlessWorkflow) (bool, error) {
	var err error
	workflow.Status.Applied = workflow.Spec
	if err = r.Client.Status().Update(ctx, workflow); err != nil {
		log.Error(err, "Failed to update Workflow status")
		return false, err
	}
	return true, err
}

func (r *KogitoServerlessWorkflowReconciler) manageBasicWorkflowDeployment(ctx context.Context, workflow *apiv08.KogitoServerlessWorkflow, platform *apiv08.KogitoServerlessPlatform) (reconcile.Result, error) {
	// Check if this Deployment already exists
	found := &appsv1.Deployment{}
	err := r.Client.Get(ctx, types.NamespacedName{Name: workflow.Name, Namespace: workflow.Namespace}, found)
	var result *reconcile.Result
	result, err = kubernetes.EnsureDeployment(ctx, r.Client, r.Scheme, workflow, platform.Spec.BuildPlatform.Registry.Address)
	if result != nil {
		log.Error(err, "Deployment Not ready")
		if workflow.Status.Condition != apiv08.DeployingConditionType {
			workflow.Status.Condition = apiv08.DeployingConditionType
			r.performStatusUpdate(ctx, workflow)
		}
		result.RequeueAfter = 5 * time.Second
		return *result, err
	}

	// Check if this Service already exists
	result, err = kubernetes.EnsureService(r.Client, r.Scheme, workflow)
	if result != nil {
		log.Error(err, "Service Not ready")
		if workflow.Status.Condition != apiv08.DeployingConditionType {
			workflow.Status.Condition = apiv08.DeployingConditionType
			r.performStatusUpdate(ctx, workflow)
		}
		result.RequeueAfter = 5 * time.Second
		return *result, err
	}

	// Deployment and Service already exists - don't requeue
	log.Info("Skip reconcile: Deployment and service already exists",
		"Deployment.Namespace", found.Namespace, "Deployment.Name", found.Name)

	//We can now update the workflow status to running
	if workflow.Status.Condition != apiv08.RunningConditionType {
		workflow.Status.Condition = apiv08.RunningConditionType
		r.performStatusUpdate(ctx, workflow)
		return reconcile.Result{Requeue: false}, err
	}

	return reconcile.Result{Requeue: false}, err
}

func buildEnqueueRequestsFromMapFunc(c client.Client, build *apiv08.KogitoServerlessBuild) []reconcile.Request {
	var requests []reconcile.Request
	if build.Status.BuildPhase != api.BuildPhaseSucceeded && build.Status.BuildPhase != api.BuildPhaseError {
		return requests
	}

	list := &apiv08.KogitoServerlessWorkflowList{}
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

		if workflow.Status.Condition == apiv08.BuildingConditionType || workflow.Status.Condition == apiv08.RunningConditionType {
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

func platformEnqueueRequestsFromMapFunc(c client.Client, p *apiv08.KogitoServerlessPlatform) []reconcile.Request {
	var requests []reconcile.Request

	if p.Status.Phase == apiv08.PlatformPhaseReady {
		list := &apiv08.KogitoServerlessWorkflowList{}

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
			if workflow.Status.Condition == apiv08.WaitingForPlatformConditionType {
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
		For(&apiv08.KogitoServerlessWorkflow{}).
		Watches(&source.Kind{Type: &apiv08.KogitoServerlessBuild{}}, handler.EnqueueRequestsFromMapFunc(func(c client.Object) []reconcile.Request {
			build, ok := c.(*apiv08.KogitoServerlessBuild)
			if !ok {
				log.Error(fmt.Errorf("type assertion failed: %v", c), "Failed to retrieve workflow list")
				return []reconcile.Request{}
			}
			return buildEnqueueRequestsFromMapFunc(mgr.GetClient(), build)
		})).
		Watches(&source.Kind{Type: &apiv08.KogitoServerlessPlatform{}}, handler.EnqueueRequestsFromMapFunc(func(a client.Object) []reconcile.Request {
			platform, ok := a.(*apiv08.KogitoServerlessPlatform)
			if !ok {
				log.Error(fmt.Errorf("type assertion failed: %v", a), "Failed to retrieve workflow list")
				return []reconcile.Request{}
			}
			return platformEnqueueRequestsFromMapFunc(mgr.GetClient(), platform)
		})).
		Complete(r)
}

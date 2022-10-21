/*
Copyright 2022.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package controllers

import (
	"context"
	"fmt"
	"github.com/go-logr/logr"
	api08 "github.com/kiegroup/kogito-serverless-operator/api/v08"
	"github.com/kiegroup/kogito-serverless-operator/builder"
	"github.com/kiegroup/kogito-serverless-operator/constants"
	"github.com/kiegroup/kogito-serverless-operator/utils"
	"github.com/ricardozanini/kogito-builder/api"
	clientr "github.com/ricardozanini/kogito-builder/client"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/runtime/schema"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/client-go/tools/record"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	ctrllog "sigs.k8s.io/controller-runtime/pkg/log"
	"time"
)

// KogitoServerlessBuildReconciler reconciles a KogitoServerlessBuild object
type KogitoServerlessBuildReconciler struct {
	client.Client
	Scheme           *runtime.Scheme
	Recorder         record.EventRecorder
	defaultBuildConf corev1.ConfigMap
}

//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessbuilds,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=sw.kogito.kie.org,resources=kogitoserverlessbuilds/status,verbs=get;update;patch

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// the KogitoServerlessBuild object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.12.1/pkg/reconcile
func (r *KogitoServerlessBuildReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {
	log := ctrllog.FromContext(ctx)
	instance := &api08.KogitoServerlessBuild{}
	err := r.Client.Get(ctx, req.NamespacedName, instance)
	if err == nil {
		phase := instance.Status.BuildPhase
		if r.defaultBuildConf.Data == nil {
			r.defaultBuildConf, err = utils.GetConfigMap(r.Client)
		}

		if err != nil || len(r.defaultBuildConf.Data[r.defaultBuildConf.Data[constants.DEFAULT_BUILDER_RESOURCE_NAME_KEY]]) == 0 {
			return ctrl.Result{}, errors.NewNotFound(schema.GroupResource{
				Resource: "ConfigMap",
			}, "builder-config")
		}

		builder := builder.NewBuilder(ctx, r.defaultBuildConf)

		if phase == api.BuildPhaseNone {
			workflow, err := r.retrieveWorkflowFromCR(instance.Spec.WorkflowId, ctx, req)
			if err == nil {
				build, err := builder.ScheduleNewBuildWithContainerFile(instance.Spec.WorkflowId, workflow)
				if err == nil {
					manageStatusUpdate(ctx, build, instance, r, log)
					return ctrl.Result{RequeueAfter: 1 * time.Second}, nil
				}
			}
		} else if phase != api.BuildPhaseSucceeded && phase != api.BuildPhaseError && phase != api.BuildPhaseFailed {
			cli, _ := clientr.NewClient(true)
			build, err := builder.ReconcileBuild(&instance.Status.Builder, cli)
			if err == nil {
				manageStatusUpdate(ctx, build, instance, r, log)
				return ctrl.Result{RequeueAfter: 5 * time.Second}, nil
			}
		}
	}
	return ctrl.Result{}, err
}

func (r *KogitoServerlessBuildReconciler) retrieveWorkflowFromCR(workflowId string, ctx context.Context, req ctrl.Request) ([]byte, error) {
	instance := &api08.KogitoServerlessWorkflow{}
	error := r.Client.Get(ctx, types.NamespacedName{Name: workflowId, Namespace: req.Namespace}, instance)
	workflowBytes, error := utils.GetWorkflowFromCR(instance, ctx)
	return workflowBytes, error
}

func manageStatusUpdate(ctx context.Context, build *api.Build, instance *api08.KogitoServerlessBuild, r *KogitoServerlessBuildReconciler, log logr.Logger) {
	if build.Status.Phase != instance.Status.BuildPhase {
		instance.Status.Builder = *build
		instance.Status.BuildPhase = build.Status.Phase
		err := r.Status().Update(ctx, instance)
		if err == nil {
			r.Recorder.Event(instance, corev1.EventTypeNormal, "Updated", fmt.Sprintf("Updated buildphase to  %s", instance.Status.BuildPhase))
		}
	}
}

// SetupWithManager sets up the controller with the Manager.
func (r *KogitoServerlessBuildReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&api08.KogitoServerlessBuild{}).
		Complete(r)
}

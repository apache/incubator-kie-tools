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

package profiles

import (
	"context"
	"path"
	"time"

	"k8s.io/klog/v2"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-serverless-operator/workflowproj"

	"k8s.io/client-go/rest"
	"k8s.io/client-go/util/retry"

	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	"github.com/kiegroup/kogito-serverless-operator/controllers/workflowdef"
	"github.com/kiegroup/kogito-serverless-operator/log"
	"github.com/kiegroup/kogito-serverless-operator/utils"

	"github.com/kiegroup/kogito-serverless-operator/controllers/platform"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/api"
	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
)

const (
	configMapResourcesVolumeName               = "resources"
	configMapExternalResourcesVolumeNamePrefix = configMapResourcesVolumeName + "-"

	// quarkusDevConfigMountPath mount path for application properties file in the Workflow Quarkus Application
	// See: https://quarkus.io/guides/config-reference#application-properties-file
	quarkusDevConfigMountPath    = "/home/kogito/serverless-workflow-project/src/main/resources"
	requeueAfterFailure          = 3 * time.Minute
	requeueAfterFollowDeployment = 5 * time.Second
	requeueAfterIsRunning        = 1 * time.Minute
	// recoverDeploymentErrorRetries how many times the operator should try to recover from a failure before giving up
	recoverDeploymentErrorRetries = 3
	// requeueRecoverDeploymentErrorInterval interval between recovering from failures
	requeueRecoverDeploymentErrorInterval = recoverDeploymentErrorInterval * time.Minute
	recoverDeploymentErrorInterval        = 10
)

var _ ProfileReconciler = &developmentProfile{}

type developmentProfile struct {
	baseReconciler
}

func (d developmentProfile) GetProfile() metadata.ProfileType {
	return metadata.DevProfile
}

func newDevProfileReconciler(client client.Client, config *rest.Config) ProfileReconciler {
	support := &stateSupport{
		client: client,
	}

	var ensurers *devProfileObjectEnsurers
	var enrichers *devProfileObjectEnrichers
	if utils.IsOpenShift() {
		ensurers = newDevelopmentObjectEnsurersForOpenShift(support)
		enrichers = newDevelopmentObjectEnrichersForOpenShift(support)
	} else {
		ensurers = newDevelopmentObjectEnsurers(support)
		enrichers = newDevelopmentObjectEnrichers(support)
	}

	stateMachine := newReconciliationStateMachine(
		&ensureRunningDevWorkflowReconciliationState{stateSupport: support, ensurers: ensurers},
		&followDeployDevWorkflowReconciliationState{stateSupport: support, enrichers: enrichers},
		&recoverFromFailureDevReconciliationState{stateSupport: support})

	profile := &developmentProfile{
		baseReconciler: newBaseProfileReconciler(support, stateMachine),
	}

	klog.V(log.I).InfoS("Reconciling in", "profile", profile.GetProfile())
	return profile
}

func newDevelopmentObjectEnsurers(support *stateSupport) *devProfileObjectEnsurers {
	return &devProfileObjectEnsurers{
		deployment:          newDefaultObjectEnsurer(support.client, devDeploymentCreator),
		service:             newDefaultObjectEnsurer(support.client, devServiceCreator),
		network:             newDummyObjectEnsurer(),
		definitionConfigMap: newDefaultObjectEnsurer(support.client, workflowDefConfigMapCreator),
		propertiesConfigMap: newDefaultObjectEnsurer(support.client, workflowPropsConfigMapCreator),
	}
}

func newDevelopmentObjectEnsurersForOpenShift(support *stateSupport) *devProfileObjectEnsurers {
	return &devProfileObjectEnsurers{
		deployment:          newDefaultObjectEnsurer(support.client, devDeploymentCreator),
		service:             newDefaultObjectEnsurer(support.client, defaultServiceCreator),
		network:             newDefaultObjectEnsurer(support.client, defaultNetworkCreator),
		definitionConfigMap: newDefaultObjectEnsurer(support.client, workflowDefConfigMapCreator),
		propertiesConfigMap: newDefaultObjectEnsurer(support.client, workflowPropsConfigMapCreator),
	}
}

func newDevelopmentObjectEnrichers(support *stateSupport) *devProfileObjectEnrichers {
	return &devProfileObjectEnrichers{
		networkInfo: newStatusEnricher(support.client, defaultDevStatusEnricher),
	}
}

func newDevelopmentObjectEnrichersForOpenShift(support *stateSupport) *devProfileObjectEnrichers {
	return &devProfileObjectEnrichers{
		networkInfo: newStatusEnricher(support.client, devStatusEnricherForOpenShift),
	}
}

type devProfileObjectEnsurers struct {
	deployment          ObjectEnsurer
	service             ObjectEnsurer
	network             ObjectEnsurer
	definitionConfigMap ObjectEnsurer
	propertiesConfigMap ObjectEnsurer
}

type devProfileObjectEnrichers struct {
	networkInfo *statusEnricher
	//Here we can add more enrichers if we need in future to enrich objects with more info coming from reconciliation
}

type ensureRunningDevWorkflowReconciliationState struct {
	*stateSupport
	ensurers *devProfileObjectEnsurers
}

func (e *ensureRunningDevWorkflowReconciliationState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsReady() || workflow.Status.GetTopLevelCondition().IsUnknown() || workflow.Status.IsChildObjectsProblem()
}

func (e *ensureRunningDevWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	var objs []client.Object

	flowDefCM, _, err := e.ensurers.definitionConfigMap.ensure(ctx, workflow, ensureWorkflowDefConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, flowDefCM)

	propsCM, _, err := e.ensurers.propertiesConfigMap.ensure(ctx, workflow, ensureWorkflowDevPropertiesConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, propsCM)

	externalCM, err := workflowdef.FetchExternalResourcesConfigMapsRef(e.client, workflow)
	if err != nil {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.ExternalResourcesNotFoundReason, "External Resources ConfigMap not found: %s", err.Error())
		if _, err = e.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, nil
	}

	devBaseContainerImage := workflowdef.GetDefaultWorkflowDevModeImageTag()
	pl, errPl := platform.GetActivePlatform(ctx, e.client, workflow.Namespace)
	// check if the Platform available
	if errPl == nil && len(pl.Spec.DevMode.BaseImage) > 0 {
		devBaseContainerImage = pl.Spec.DevMode.BaseImage
	}

	deployment, _, err := e.ensurers.deployment.ensure(ctx, workflow,
		devDeploymentMutateVisitor(workflow),
		naiveApplyImageDeploymentMutateVisitor(devBaseContainerImage),
		mountDevConfigMapsMutateVisitor(flowDefCM.(*corev1.ConfigMap), propsCM.(*corev1.ConfigMap), externalCM))
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
	}
	objs = append(objs, deployment)

	service, _, err := e.ensurers.service.ensure(ctx, workflow, defaultServiceMutateVisitor(workflow))
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
	}
	objs = append(objs, service)

	route, _, err := e.ensurers.network.ensure(ctx, workflow)
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
	}
	objs = append(objs, route)

	// First time reconciling this object, mark as wait for deployment
	if workflow.Status.GetTopLevelCondition().IsUnknown() {
		klog.V(log.I).InfoS("Workflow is in WaitingForDeployment Condition")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err = e.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterIsRunning}, objs, nil
	}

	// Is the deployment still available?
	convertedDeployment := deployment.(*appsv1.Deployment)
	if !kubeutil.IsDeploymentAvailable(convertedDeployment) {
		klog.V(log.I).InfoS("Workflow is not running due to a problem in the Deployment. Attempt to recover.")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, getDeploymentUnavailabilityMessage(convertedDeployment))
		if _, err = e.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
		}
	}

	return ctrl.Result{RequeueAfter: requeueAfterIsRunning}, objs, nil
}

type followDeployDevWorkflowReconciliationState struct {
	*stateSupport
	enrichers *devProfileObjectEnrichers
}

func (f *followDeployDevWorkflowReconciliationState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.IsWaitingForDeployment()
}

func (f *followDeployDevWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	result, err := DeploymentHandler(f.client).SyncDeploymentStatus(ctx, workflow)
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
	}

	if _, err := f.performStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
	}

	return result, nil, nil
}

func (f *followDeployDevWorkflowReconciliationState) PostReconcile(ctx context.Context, workflow *operatorapi.SonataFlow) error {
	deployment := &appsv1.Deployment{}
	if err := f.client.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		return err
	}
	if deployment != nil && kubeutil.IsDeploymentAvailable(deployment) {
		// Enriching Workflow CR status with needed network info
		if _, err := f.enrichers.networkInfo.Enrich(ctx, workflow); err != nil {
			return err
		}
		if _, err := f.performStatusUpdate(ctx, workflow); err != nil {
			return err
		}
	}
	return nil
}

type recoverFromFailureDevReconciliationState struct {
	*stateSupport
}

func (r *recoverFromFailureDevReconciliationState) CanReconcile(workflow *operatorapi.SonataFlow) bool {
	return workflow.Status.GetCondition(api.RunningConditionType).IsFalse()
}

func (r *recoverFromFailureDevReconciliationState) Do(ctx context.Context, workflow *operatorapi.SonataFlow) (ctrl.Result, []client.Object, error) {
	// for now, a very basic attempt to recover by rolling out the deployment
	deployment := &appsv1.Deployment{}
	if err := r.client.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		// if the deployment is not there, let's try to reset the status condition and make the reconciliation fix the objects
		if errors.IsNotFound(err) {
			klog.V(log.I).InfoS("Tried to recover from failed state, no deployment found, trying to reset the workflow conditions")
			workflow.Status.RecoverFailureAttempts = 0
			workflow.Status.Manager().MarkUnknown(api.RunningConditionType, "", "")
			if _, updateErr := r.performStatusUpdate(ctx, workflow); updateErr != nil {
				return ctrl.Result{Requeue: false}, nil, updateErr
			}
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, nil
		}
		return ctrl.Result{Requeue: false}, nil, err
	}

	// if the deployment is progressing we might have good news
	if kubeutil.IsDeploymentAvailable(deployment) {
		workflow.Status.RecoverFailureAttempts = 0
		workflow.Status.Manager().MarkTrue(api.RunningConditionType)
		if _, updateErr := r.performStatusUpdate(ctx, workflow); updateErr != nil {
			return ctrl.Result{Requeue: false}, nil, updateErr
		}
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, nil
	}

	if workflow.Status.RecoverFailureAttempts >= recoverDeploymentErrorRetries {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.RedeploymentExhaustedReason,
			"Can't recover workflow from failure after maximum attempts: %d", workflow.Status.RecoverFailureAttempts)
		if _, updateErr := r.performStatusUpdate(ctx, workflow); updateErr != nil {
			return ctrl.Result{}, nil, updateErr
		}
		return ctrl.Result{RequeueAfter: requeueRecoverDeploymentErrorInterval}, nil, nil
	}

	// TODO: we can improve deployment failures https://issues.redhat.com/browse/KOGITO-8812

	// Guard to avoid consecutive reconciliations to mess with the recover interval
	if !workflow.Status.LastTimeRecoverAttempt.IsZero() &&
		metav1.Now().Sub(workflow.Status.LastTimeRecoverAttempt.Time).Minutes() > 10 {
		return ctrl.Result{RequeueAfter: time.Minute * recoverDeploymentErrorInterval}, nil, nil
	}

	// let's try rolling out the deployment
	if err := kubeutil.MarkDeploymentToRollout(deployment); err != nil {
		return ctrl.Result{}, nil, err
	}
	retryErr := retry.RetryOnConflict(retry.DefaultRetry, func() error {
		updateErr := r.client.Update(ctx, deployment)
		return updateErr
	})

	if retryErr != nil {
		klog.V(log.E).ErrorS(retryErr, "Error during Deployment rollout")
		return ctrl.Result{RequeueAfter: requeueRecoverDeploymentErrorInterval}, nil, nil
	}

	workflow.Status.RecoverFailureAttempts += 1
	workflow.Status.LastTimeRecoverAttempt = metav1.Now()
	if _, err := r.performStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{Requeue: false}, nil, err
	}
	return ctrl.Result{RequeueAfter: requeueRecoverDeploymentErrorInterval}, nil, nil
}

// mountDevConfigMapsMutateVisitor mounts the required configMaps in the Workflow Dev Deployment
func mountDevConfigMapsMutateVisitor(flowDefCM, propsCM *corev1.ConfigMap, workflowResCMs []operatorapi.ConfigMapWorkflowResource) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			deployment := object.(*appsv1.Deployment)

			volumes := make([]corev1.Volume, 0)
			volumeMounts := []corev1.VolumeMount{
				kubeutil.VolumeMount(configMapResourcesVolumeName, true, quarkusDevConfigMountPath),
			}

			// defaultResourcesVolume holds every ConfigMap mount required on src/main/resources
			defaultResourcesVolume := corev1.Volume{Name: configMapResourcesVolumeName, VolumeSource: corev1.VolumeSource{Projected: &corev1.ProjectedVolumeSource{}}}
			kubeutil.VolumeProjectionAddConfigMap(defaultResourcesVolume.Projected, propsCM.Name, corev1.KeyToPath{Key: workflowproj.ApplicationPropertiesFileName, Path: workflowproj.ApplicationPropertiesFileName})
			kubeutil.VolumeProjectionAddConfigMap(defaultResourcesVolume.Projected, flowDefCM.Name)

			// resourceVolumes holds every resource that needs to be mounted on src/main/resources/<specific_dir>
			resourceVolumes := make([]corev1.Volume, 0)

			for _, workflowResCM := range workflowResCMs {
				// if we need to mount at the root dir, we use the defaultResourcesVolume
				if len(workflowResCM.WorkflowPath) == 0 {
					kubeutil.VolumeProjectionAddConfigMap(defaultResourcesVolume.Projected, workflowResCM.ConfigMap.Name)
					continue
				}
				// the resource configMap needs a specific dir, inside the src/main/resources
				// to avoid clashing with other configMaps trying to mount on the same dir, we create one projected per path
				volumeMountName := configMapExternalResourcesVolumeNamePrefix + utils.PathToString(workflowResCM.WorkflowPath)
				volumeMounts = kubeutil.VolumeMountAdd(volumeMounts, volumeMountName, path.Join(quarkusDevConfigMountPath, workflowResCM.WorkflowPath))
				resourceVolumes = kubeutil.VolumeAddVolumeProjectionConfigMap(resourceVolumes, workflowResCM.ConfigMap.Name, volumeMountName)
			}

			volumes = append(volumes, defaultResourcesVolume)
			volumes = append(volumes, resourceVolumes...)

			deployment.Spec.Template.Spec.Volumes = make([]corev1.Volume, 0)
			deployment.Spec.Template.Spec.Volumes = volumes
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = make([]corev1.VolumeMount, 0)
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = volumeMounts

			return nil
		}
	}
}

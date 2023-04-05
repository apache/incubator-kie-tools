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
	"fmt"
	"strings"
	"time"

	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-serverless-operator/version"

	"github.com/kiegroup/kogito-serverless-operator/platform"

	"github.com/go-logr/logr"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/api"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
)

var _ ProfileReconciler = &developmentProfile{}

var configMountPathMap = map[ConfigPath]string{
	QUARKUS:  quarkusDevConfigMountPath,
	CAMEL:    quarkusDevConfigMountPath + "/routes",
	ASYNCAPI: quarkusDevConfigMountPath + "/templates",
	OPENAPI:  quarkusDevConfigMountPath + "/specs",
	WORKFLOW: quarkusDevConfigMountPath + "/workflows",
	GENERIC:  quarkusDevConfigMountPath + "/generic",
}

var prefixMountPathMap = map[string]string{
	extResCamelSuffix:    configMountPathMap[CAMEL],
	extResGenericSuffix:  configMountPathMap[GENERIC],
	extResAsyncAPISuffix: configMountPathMap[ASYNCAPI],
	extResOpenAPISuffix:  configMountPathMap[OPENAPI],
}

const (
	// TODO: read from the platform config. Default tag MUST align with the current operator's version. See: https://issues.redhat.com/browse/KOGITO-8675
	defaultKogitoServerlessWorkflowDevImage = "quay.io/kiegroup/kogito-swf-builder"
	configMapWorkflowDefVolumeName          = "workflow-definition"
	configMapWorkflowDefMountPath           = "/home/kogito/serverless-workflow-project/src/main/resources/workflows"
	// quarkusDevConfigMountPath mount path for application properties file in the Workflow Quarkus Application
	// See: https://quarkus.io/guides/config-reference#application-properties-file
	quarkusDevConfigMountPath    = "/home/kogito/serverless-workflow-project/src/main/resources"
	requeueAfterFailure          = 3 * time.Minute
	requeueAfterFollowDeployment = 5 * time.Second
	requeueAfterIsRunning        = 1 * time.Minute
	// recoverDeploymentErrorRetries how many times the operator should try to recover from a failure before giving up
	recoverDeploymentErrorRetries = 3
	// recoverDeploymentErrorInterval interval between recovering from failures
	recoverDeploymentErrorInterval = 5 * time.Minute
	extResCamelSuffix              = "resource-camel"
	extResGenericSuffix            = "resource-generic"
	extResOpenAPISuffix            = "resource-openapi"
	extResAsyncAPISuffix           = "resource-asyncapi"
	nightlySuffix                  = "nightly"
	snapshotSuffix                 = "snapshot"
)

type developmentProfile struct {
	baseReconciler
}

type ConfigPath string

const (
	QUARKUS  = "QUARKUS"
	CAMEL    = "CAMEL"
	ASYNCAPI = "ASYNCAPI"
	OPENAPI  = "OPENAPI"
	WORKFLOW = "WORKFLOW"
	GENERIC  = "GENERIC"
)

func (d developmentProfile) GetProfile() Profile {
	return Development
}

func newDevProfileReconciler(client client.Client, logger *logr.Logger) ProfileReconciler {
	support := &stateSupport{
		logger: logger,
		client: client,
	}
	ensurers := newDevelopmentObjectEnsurers(support)

	stateMachine := newReconciliationStateMachine(logger,
		&ensureRunningDevWorkflowReconciliationState{stateSupport: support, ensurers: ensurers},
		&followDeployDevWorkflowReconciliationState{stateSupport: support},
		&recoverFromFailureDevReconciliationState{stateSupport: support})

	profile := &developmentProfile{
		baseReconciler: newBaseProfileReconciler(support, stateMachine),
	}
	logger.Info("Reconciling in", "profile", profile.GetProfile())
	return profile
}

func newDevelopmentObjectEnsurers(support *stateSupport) *devProfileObjectEnsurers {
	return &devProfileObjectEnsurers{
		deployment:          newObjectEnsurer(support.client, support.logger, defaultDeploymentCreator),
		service:             newObjectEnsurer(support.client, support.logger, defaultServiceCreator),
		definitionConfigMap: newObjectEnsurer(support.client, support.logger, workflowDefConfigMapCreator),
		propertiesConfigMap: newObjectEnsurer(support.client, support.logger, workflowDevPropsConfigMapCreator),
	}
}

type devProfileObjectEnsurers struct {
	deployment          *objectEnsurer
	service             *objectEnsurer
	definitionConfigMap *objectEnsurer
	propertiesConfigMap *objectEnsurer
}

type ensureRunningDevWorkflowReconciliationState struct {
	*stateSupport
	ensurers *devProfileObjectEnsurers
}

func (e *ensureRunningDevWorkflowReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.IsReady() || workflow.Status.GetTopLevelCondition().IsUnknown()
}

func (e *ensureRunningDevWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	var objs []client.Object

	flowDefCM, _, err := e.ensurers.definitionConfigMap.ensure(ctx, workflow, ensureWorkflowSpecConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, flowDefCM)

	propsCM, _, err := e.ensurers.propertiesConfigMap.ensure(ctx, workflow, ensureWorkflowDevPropertiesConfigMapMutator(workflow))
	if err != nil {
		return ctrl.Result{Requeue: false}, objs, err
	}
	objs = append(objs, propsCM)

	externalCM, err := e.fetchExternalResourcesConfigMaps(workflow)
	if err != nil {
		e.logger.Error(err, "External Resources ConfigMap not found")
	}

	devBaseContainerImage := defaultKogitoServerlessWorkflowDevImage + ":" + version.OperatorVersion
	pl, errPl := platform.GetActivePlatform(ctx, e.client, workflow.Namespace)
	// check if the Platform available
	if errPl == nil && len(pl.Spec.DevBaseImage) > 0 {

		devBaseContainerImage = pl.Spec.DevBaseImage

	} else {
		// is the operator version is a snapshot we add the nightly version
		if isSnapshot(version.OperatorVersion) {
			devBaseContainerImage = defaultKogitoServerlessWorkflowDevImage + "-" + nightlySuffix + ":latest"
		}
	}

	deployment, _, err := e.ensurers.deployment.ensure(ctx, workflow,
		defaultDeploymentMutateVisitor(workflow),
		naiveApplyImageDeploymentMutateVisitor(devBaseContainerImage),
		mountDevConfigMapsMutateVisitor(flowDefCM.(*v1.ConfigMap), propsCM.(*v1.ConfigMap), externalCM))
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
	}
	objs = append(objs, deployment)

	service, _, err := e.ensurers.service.ensure(ctx, workflow, defaultServiceMutateVisitor(workflow))
	if err != nil {
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
	}
	objs = append(objs, service)

	// First time reconciling this object, mark as wait for deployment
	if workflow.Status.GetTopLevelCondition().IsUnknown() {
		e.logger.Info("Workflow is in WaitingForDeployment Condition")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		if _, err = e.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterIsRunning}, objs, nil
	}

	// Is the deployment still available?
	convertedDeployment := deployment.(*appsv1.Deployment)
	if !kubeutil.IsDeploymentAvailable(convertedDeployment) {
		e.logger.Info("Workflow is not running due to a problem in the Deployment. Attempt to recover.")
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, getDeploymentFailureMessage(convertedDeployment))
		if _, err = e.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, objs, err
		}
	}

	return ctrl.Result{RequeueAfter: requeueAfterIsRunning}, objs, nil
}

func isSnapshot(operatorVersion string) bool {
	return strings.HasSuffix(operatorVersion, snapshotSuffix)
}

type followDeployDevWorkflowReconciliationState struct {
	*stateSupport
}

func (f *followDeployDevWorkflowReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.IsWaitingForDeployment()
}

func (f *followDeployDevWorkflowReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	deployment := &appsv1.Deployment{}
	if err := f.client.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		// we should have the deployment by this time, so even if the error above is not found, we should halt.
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentUnavailableReason, "Couldn't find deployment anymore while waiting for the deploy")
		if _, err := f.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
	}

	if kubeutil.IsDeploymentAvailable(deployment) {
		workflow.Status.Manager().MarkTrue(api.RunningConditionType)
		f.logger.Info("Workflow is in Running Condition")
		if _, err := f.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterIsRunning}, nil, nil
	}

	if kubeutil.IsDeploymentProgressing(deployment) {
		workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.WaitingForDeploymentReason, "")
		f.logger.Info("Workflow is in WaitingForDeployment Condition")
		if _, err := f.performStatusUpdate(ctx, workflow); err != nil {
			return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
		}
		return ctrl.Result{RequeueAfter: requeueAfterFollowDeployment}, nil, nil
	}

	failedReason := getDeploymentFailureMessage(deployment)
	workflow.Status.Manager().MarkFalse(api.RunningConditionType, api.DeploymentFailureReason, failedReason)
	f.logger.Info("Workflow deployment failed", "Reason Message", failedReason)
	_, err := f.performStatusUpdate(ctx, workflow)
	return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, err
}

type recoverFromFailureDevReconciliationState struct {
	*stateSupport
}

func (r *recoverFromFailureDevReconciliationState) CanReconcile(workflow *operatorapi.KogitoServerlessWorkflow) bool {
	return workflow.Status.GetCondition(api.RunningConditionType).IsFalse()
}

func (r *recoverFromFailureDevReconciliationState) Do(ctx context.Context, workflow *operatorapi.KogitoServerlessWorkflow) (ctrl.Result, []client.Object, error) {
	// for now, a very basic attempt to recover by rolling out the deployment
	deployment := &appsv1.Deployment{}
	if err := r.client.Get(ctx, client.ObjectKeyFromObject(workflow), deployment); err != nil {
		// if the deployment is not there, let's try to reset the status condition and make the reconciliation fix the objects
		if errors.IsNotFound(err) {
			r.logger.Info("Tried to recover from failed state, no deployment found, trying to reset the workflow conditions")
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
		return ctrl.Result{RequeueAfter: requeueAfterFailure}, nil, nil
	}

	// TODO: we can improve deployment failures https://issues.redhat.com/browse/KOGITO-8812

	// let's try rolling out the deployment
	if err := kubeutil.MarkDeploymentToRollout(deployment); err != nil {
		return ctrl.Result{}, nil, err
	}
	if err := r.client.Update(ctx, deployment); err != nil {
		// TODO we should implement a channel to wait for this rollout before returning the handler to the reconciliation manager. And future reconciliations should not call this method again. See https://issues.redhat.com/browse/KOGITO-8748
		if errors.IsConflict(err) {
			r.logger.Info("Deployment just got updated, waiting to rollout again")
			return ctrl.Result{RequeueAfter: recoverDeploymentErrorInterval}, nil, nil
		}
		return ctrl.Result{}, nil, err
	}

	workflow.Status.RecoverFailureAttempts += 1
	if _, err := r.performStatusUpdate(ctx, workflow); err != nil {
		return ctrl.Result{Requeue: false}, nil, err
	}
	return ctrl.Result{RequeueAfter: recoverDeploymentErrorInterval}, nil, nil
}

// getDeploymentFailureMessage gets the replica failure reason.
// MUST be called after checking that the Deployment is NOT available.
// If there's no reason, the Deployment state has no apparent reason to be in failed state.
func getDeploymentFailureMessage(deployment *appsv1.Deployment) string {
	failure := kubeutil.GetDeploymentUnavailabilityMessage(deployment)
	if len(failure) == 0 {
		failure = fmt.Sprintf("Workflow Deployment %s is unavailable", deployment.Name)
	}
	return failure
}

// mountDevConfigMapsMutateVisitor mounts the required configMaps in the Workflow Dev Deployment
func mountDevConfigMapsMutateVisitor(flowDefCM, propsCM *v1.ConfigMap, externalResourceConfigMaps map[string]v1.ConfigMap) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			deployment := object.(*appsv1.Deployment)
			volumes := make([]v1.Volume, 0)
			volumeMounts := make([]v1.VolumeMount, 0)

			volumes = append(volumes,
				kubeutil.Volume(configMapWorkflowDefVolumeName, flowDefCM.Name),
				kubeutil.VolumeWithItems(configMapWorkflowPropsVolumeName, propsCM.Name,
					[]v1.KeyToPath{{Key: applicationPropertiesFileName, Path: applicationPropertiesFileName}}))

			volumeMounts = append(volumeMounts,
				kubeutil.VolumeMount(configMapWorkflowDefVolumeName, true, configMapWorkflowDefMountPath),
				kubeutil.VolumeMount(configMapWorkflowPropsVolumeName, true, quarkusDevConfigMountPath),
			)

			externalVolumes, externalVolumesMount := kubeutil.ConfigMapsToVolumesAndMount(externalResourceConfigMaps, prefixMountPathMap)
			volumes = append(volumes, externalVolumes...)
			volumeMounts = append(volumeMounts, externalVolumesMount...)

			deployment.Spec.Template.Spec.Volumes = make([]v1.Volume, 0)
			deployment.Spec.Template.Spec.Volumes = volumes
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = make([]v1.VolumeMount, 0)
			deployment.Spec.Template.Spec.Containers[0].VolumeMounts = volumeMounts

			return nil
		}
	}
}

func (e ensureRunningDevWorkflowReconciliationState) fetchExternalResourcesConfigMaps(workflow *operatorapi.KogitoServerlessWorkflow) (map[string]v1.ConfigMap, error) {
	externalConfigMaps := make(map[string]v1.ConfigMap, 0)
	for k, val := range workflow.Annotations {
		resource := kubeutil.GetAnnotationResource(k)
		if len(resource) > 0 {
			cm, err := e.fetchConfigMap(val, workflow.Namespace)
			if err != nil {
				return externalConfigMaps, err
			} else {
				externalConfigMaps[resource] = cm
			}
		}
	}
	return externalConfigMaps, nil
}

func (e ensureRunningDevWorkflowReconciliationState) fetchConfigMap(configMapName string, namespace string) (v1.ConfigMap, error) {

	existingConfigMap := v1.ConfigMap{}
	err := e.stateSupport.client.Get(context.TODO(), types.NamespacedName{Name: configMapName, Namespace: namespace}, &existingConfigMap)
	if err != nil {
		e.logger.Error(err, "reading configmap")
		return v1.ConfigMap{}, err
	}
	return existingConfigMap, nil
}

// rolloutDeploymentIfCMChangedMutateVisitor forces a pod refresh if the workflow definition suffered any changes.
// This method can be used as an alternative to the Kubernetes ConfigMap refresher.
//
// See: https://kubernetes.io/docs/concepts/configuration/configmap/#mounted-configmaps-are-updated-automatically
func rolloutDeploymentIfCMChangedMutateVisitor(cmOperationResult controllerutil.OperationResult) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if cmOperationResult == controllerutil.OperationResultUpdated {
				deployment := object.(*appsv1.Deployment)
				err := kubeutil.MarkDeploymentToRollout(deployment)
				return err
			}
			return nil
		}
	}
}

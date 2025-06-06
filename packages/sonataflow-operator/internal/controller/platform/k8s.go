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

package platform

import (
	"context"
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"k8s.io/klog/v2"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/metadata"

	"github.com/imdario/mergo"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/variables"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

// NewServiceAction returns an action that deploys the services.
func NewServiceAction() Action {
	return &serviceAction{}
}

type serviceAction struct {
	baseAction
}

func (action *serviceAction) Name() string {
	return "service"
}

func (action *serviceAction) CanHandle(platform *operatorapi.SonataFlowPlatform) bool {
	return platform.Status.IsReady()
}

func (action *serviceAction) Handle(ctx context.Context, platform *operatorapi.SonataFlowPlatform) (*operatorapi.SonataFlowPlatform, *corev1.Event, error) {
	// Refresh applied configuration
	if err := CreateOrUpdateWithDefaults(ctx, platform, false); err != nil {
		return nil, nil, err
	}

	psDI := services.NewDataIndexHandler(platform)
	psJS := services.NewJobServiceHandler(platform)

	if IsJobsBasedDBMigration(platform, psDI, psJS) {
		p, err := HandleDBMigrationJob(ctx, action.client, platform, psDI, psJS)
		if p == nil && err == nil { // DB migration is in-progress
			return nil, nil, nil
		} else if p == nil && err != nil { // DB migration failed
			klog.V(log.E).ErrorS(err, "Error handling DB migration job", "namespace", platform.Namespace)
			return nil, nil, err
		}
	}

	if psDI.IsServiceSetInSpec() {
		if event, err := createOrUpdateServiceComponents(ctx, action.client, platform, psDI); err != nil {
			return nil, event, err
		}
	}

	if psJS.IsServiceSetInSpec() {
		if event, err := createOrUpdateServiceComponents(ctx, action.client, platform, psJS); err != nil {
			return nil, event, err
		}
	}

	return platform, nil, nil
}

func createOrUpdateServiceComponents(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) (*corev1.Event, error) {
	if err := createOrUpdateConfigMap(ctx, client, platform, psh); err != nil {
		return nil, err
	}
	if err := createOrUpdateDeployment(ctx, client, platform, psh); err != nil {
		return nil, err
	}
	if err := createOrUpdateService(ctx, client, platform, psh); err != nil {
		return nil, err
	}
	return createOrUpdateKnativeResources(ctx, client, platform, psh)
}

func createOrUpdateDeployment(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) error {
	readyProbe := &corev1.Probe{
		ProbeHandler: corev1.ProbeHandler{
			HTTPGet: &corev1.HTTPGetAction{
				Path:   constants.QuarkusHealthPathReady,
				Port:   variables.DefaultHTTPWorkflowPortIntStr,
				Scheme: corev1.URISchemeHTTP,
			},
		},
		InitialDelaySeconds: int32(45),
		TimeoutSeconds:      int32(10),
		PeriodSeconds:       int32(30),
		SuccessThreshold:    int32(1),
		FailureThreshold:    int32(4),
	}
	liveProbe := readyProbe.DeepCopy()
	liveProbe.ProbeHandler.HTTPGet.Path = constants.QuarkusHealthPathLive
	imageTag := psh.GetServiceImageName(constants.PersistenceTypeEphemeral)
	serviceContainer := &corev1.Container{
		Image:           imageTag,
		ImagePullPolicy: kubeutil.GetImagePullPolicy(imageTag),
		Env:             psh.GetEnvironmentVariables(),
		Resources:       psh.GetPodResourceRequirements(),
		ReadinessProbe:  readyProbe,
		LivenessProbe:   liveProbe,
		Ports: []corev1.ContainerPort{
			{
				Name:          utils.DefaultServicePortName,
				ContainerPort: int32(constants.DefaultHTTPWorkflowPortInt),
				Protocol:      corev1.ProtocolTCP,
			},
		},
		VolumeMounts: []corev1.VolumeMount{
			{
				Name:      "application-config",
				MountPath: "/home/kogito/config",
			},
		},
	}
	serviceContainer = psh.ConfigurePersistence(serviceContainer)
	serviceContainer, err := psh.MergeContainerSpec(serviceContainer)
	if err != nil {
		return err
	}

	// immutable
	serviceContainer.Name = psh.GetContainerName()

	replicas := psh.GetReplicaCount()
	kSinkInjected, err := psh.CheckKSinkInjected()
	if err != nil {
		return nil
	}
	if !kSinkInjected {
		replicas = 0 // Wait for K_SINK injection
	}
	lbl, selectorLbl := getLabels(platform, psh)
	serviceDeploymentSpec := appsv1.DeploymentSpec{
		Selector: &metav1.LabelSelector{
			MatchLabels: selectorLbl,
		},
		Replicas: &replicas,
		Strategy: psh.GetDeploymentStrategy(),
		Template: corev1.PodTemplateSpec{
			ObjectMeta: metav1.ObjectMeta{
				Labels: lbl,
			},
			Spec: corev1.PodSpec{
				Volumes: []corev1.Volume{
					{
						Name: "application-config",
						VolumeSource: corev1.VolumeSource{
							ConfigMap: &corev1.ConfigMapVolumeSource{
								LocalObjectReference: corev1.LocalObjectReference{
									Name: psh.GetServiceCmName(),
								},
							},
						},
					},
				},
			},
		},
	}

	serviceDeploymentSpec.Template.Spec, err = psh.MergePodSpec(serviceDeploymentSpec.Template.Spec)
	if err != nil {
		return err
	}
	kubeutil.AddOrReplaceContainer(serviceContainer.Name, *serviceContainer, &serviceDeploymentSpec.Template.Spec)

	serviceDeployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: platform.Namespace,
			Name:      psh.GetServiceName(),
			Labels:    lbl,
		}}
	if err := controllerutil.SetControllerReference(platform, serviceDeployment, client.Scheme()); err != nil {
		return err
	}

	// Create or Update the deployment
	if op, err := controllerutil.CreateOrUpdate(ctx, client, serviceDeployment, func() error {
		knative.SaveKnativeData(&serviceDeploymentSpec.Template.Spec, &serviceDeployment.Spec.Template.Spec)
		err := mergo.Merge(&(serviceDeployment.Spec), serviceDeploymentSpec, mergo.WithOverride)
		// mergo.Merge algorithm is not setting the serviceDeployment.Spec.Replicas when the
		// *serviceDeploymentSpec.Replicas is 0. Making impossible to scale to zero. Ensure the value.
		serviceDeployment.Spec.Replicas = serviceDeploymentSpec.Replicas
		if err != nil {
			return err
		}
		return nil
	}); err != nil {
		return err
	} else {
		klog.V(log.I).InfoS("Deployment successfully reconciled", "operation", op)
	}
	return nil
}

func createOrUpdateService(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) error {
	lbl, selectorLbl := getLabels(platform, psh)
	dataSvcSpec := corev1.ServiceSpec{
		Ports: []corev1.ServicePort{
			{
				Name:       utils.DefaultServicePortName,
				Protocol:   corev1.ProtocolTCP,
				Port:       80,
				TargetPort: variables.DefaultHTTPWorkflowPortIntStr,
			},
		},
		Selector: selectorLbl,
	}
	dataSvc := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: platform.Namespace,
			Name:      psh.GetServiceName(),
			Labels:    lbl,
		}}
	if err := controllerutil.SetControllerReference(platform, dataSvc, client.Scheme()); err != nil {
		return err
	}

	// Create or Update the service
	if op, err := controllerutil.CreateOrUpdate(ctx, client, dataSvc, func() error {
		dataSvc.Spec = dataSvcSpec

		return nil
	}); err != nil {
		return err
	} else {
		klog.V(log.I).InfoS("Service successfully reconciled", "operation", op)
	}

	return nil
}

// getServicesLabelsMap A common utility function for use by SonataFlow Services (e.g. DI/JS and DB Migrator) to obtain standard common labels by passing parameters
func getServicesLabelsMap(app string, appNamespace string, service string, k8sName string, k8sComponent string, k8sPartOf string, k8sManagedBy string) (map[string]string, map[string]string) {
	lbl := map[string]string{
		workflowproj.LabelApp:             app,
		workflowproj.LabelAppNamespace:    appNamespace,
		workflowproj.LabelService:         service,
		metadata.KubernetesLabelInstance:  app,
		metadata.KubernetesLabelName:      k8sName,
		metadata.KubernetesLabelComponent: k8sComponent,
		metadata.KubernetesLabelPartOf:    k8sPartOf,
		metadata.KubernetesLabelManagedBy: k8sManagedBy,
		metadata.KubernetesLabelVersion:   version.GetImageTagVersion(),
	}

	selectorLbl := map[string]string{
		workflowproj.LabelService: service,
	}

	return lbl, selectorLbl
}

// getLabels Specifically used by services implementing services.PlatformServiceHandler interface such as DI/JS
func getLabels(platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) (map[string]string, map[string]string) {
	return getServicesLabelsMap(platform.Name, platform.Namespace, psh.GetServiceName(), psh.GetContainerName(), psh.GetServiceName(), platform.Name, "sonataflow-operator")
}

func createOrUpdateConfigMap(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) error {
	handler, err := services.NewServiceAppPropertyHandler(psh)
	if err != nil {
		return err
	}
	lbl, _ := getLabels(platform, psh)
	dataStr := handler.Build()
	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      psh.GetServiceCmName(),
			Namespace: platform.Namespace,
			Labels:    lbl,
		},
		Data: map[string]string{
			workflowproj.ApplicationPropertiesFileName: dataStr,
		},
	}
	if err := controllerutil.SetControllerReference(platform, configMap, client.Scheme()); err != nil {
		return err
	}

	// Create or Update the service
	if op, err := controllerutil.CreateOrUpdate(ctx, client, configMap, func() error {
		configMap.Data[workflowproj.ApplicationPropertiesFileName] = handler.WithUserProperties(dataStr).Build()

		return nil
	}); err != nil {
		return err
	} else {
		klog.V(log.I).InfoS("ConfigMap successfully reconciled", "operation", op)
	}
	return nil
}

func setSonataFlowPlatformFinalizer(ctx context.Context, c client.Client, platform *operatorapi.SonataFlowPlatform) error {
	if !controllerutil.ContainsFinalizer(platform, constants.TriggerFinalizer) {
		controllerutil.AddFinalizer(platform, constants.TriggerFinalizer)
		return c.Update(ctx, platform)
	}
	return nil
}

func createOrUpdateKnativeResources(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) (*corev1.Event, error) {
	lbl, _ := getLabels(platform, psh)
	objs, event, err := psh.GenerateKnativeResources(platform, lbl)
	if err != nil {
		return event, err
	}
	// Create or update triggers
	for _, obj := range objs {
		if triggerDef, ok := obj.(*eventingv1.Trigger); ok {
			if platform.Namespace == obj.GetNamespace() {
				if err := controllerutil.SetControllerReference(platform, obj, client.Scheme()); err != nil {
					return nil, err
				}
			} else {
				// This is for Knative trigger in a different namespace
				// Set the finalizer for trigger cleanup when the platform is deleted
				if err := setSonataFlowPlatformFinalizer(ctx, client, platform); err != nil {
					return nil, err
				}
			}
			trigger := &eventingv1.Trigger{
				ObjectMeta: triggerDef.ObjectMeta,
			}
			_, err := controllerutil.CreateOrUpdate(ctx, client, trigger, func() error {
				trigger.Spec = triggerDef.Spec
				return nil
			})
			if err != nil {
				return nil, err
			}
			addToSonataFlowPlatformTriggerList(platform, trigger)
		}
	}

	if err := SafeUpdatePlatformStatus(ctx, platform); err != nil {
		return nil, err
	}

	// Create or update sinkbindings
	for _, obj := range objs {
		if sbDef, ok := obj.(*sourcesv1.SinkBinding); ok {
			if err := controllerutil.SetControllerReference(platform, obj, client.Scheme()); err != nil {
				return nil, err
			}
			sinkBinding := &sourcesv1.SinkBinding{
				ObjectMeta: sbDef.ObjectMeta,
			}
			_, err = controllerutil.CreateOrUpdate(ctx, client, sinkBinding, func() error {
				sinkBinding.Spec = sbDef.Spec
				return nil
			})
			if err != nil {
				return nil, err
			}
			kSinkInjected, err := psh.CheckKSinkInjected()
			if err != nil {
				return nil, err
			}
			if !kSinkInjected {
				msg := fmt.Sprintf("waiting for K_SINK injection for service %s to complete", psh.GetServiceName())
				event := &corev1.Event{
					Type:    corev1.EventTypeWarning,
					Reason:  services.WaitingKnativeEventing,
					Message: msg,
				}
				return event, fmt.Errorf("%s", msg)
			}
		}
	}
	return nil, nil
}

func addToSonataFlowPlatformTriggerList(platform *operatorapi.SonataFlowPlatform, trigger *eventingv1.Trigger) {
	for _, t := range platform.Status.Triggers {
		if t.Name == trigger.Name && t.Namespace == trigger.Namespace {
			return // trigger already exists
		}
	}
	platform.Status.Triggers = append(platform.Status.Triggers, operatorapi.SonataFlowPlatformTriggerRef{Name: trigger.Name, Namespace: trigger.Namespace})
}

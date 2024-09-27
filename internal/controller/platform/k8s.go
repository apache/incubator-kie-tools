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

	"k8s.io/klog/v2"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-kogito-serverless-operator/internal/controller/profiles/common/variables"
	"github.com/apache/incubator-kie-kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
	kubeutil "github.com/apache/incubator-kie-kogito-serverless-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-kogito-serverless-operator/workflowproj"
	"github.com/imdario/mergo"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
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

func (action *serviceAction) Handle(ctx context.Context, platform *operatorapi.SonataFlowPlatform) (*operatorapi.SonataFlowPlatform, error) {
	// Refresh applied configuration
	if err := CreateOrUpdateWithDefaults(ctx, platform, false); err != nil {
		return nil, err
	}

	psDI := services.NewDataIndexHandler(platform)
	if psDI.IsServiceSetInSpec() {
		if err := createOrUpdateServiceComponents(ctx, action.client, platform, psDI); err != nil {
			return nil, err
		}
	}

	psJS := services.NewJobServiceHandler(platform)
	if psJS.IsServiceSetInSpec() {
		if err := createOrUpdateServiceComponents(ctx, action.client, platform, psJS); err != nil {
			return nil, err
		}
	}

	return platform, nil
}

func createOrUpdateServiceComponents(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) error {
	if err := createOrUpdateConfigMap(ctx, client, platform, psh); err != nil {
		return err
	}
	if err := createOrUpdateDeployment(ctx, client, platform, psh); err != nil {
		return err
	}
	if err := createOrUpdateService(ctx, client, platform, psh); err != nil {
		return err
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

func getLabels(platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) (map[string]string, map[string]string) {
	lbl := map[string]string{
		workflowproj.LabelApp:          platform.Name,
		workflowproj.LabelAppNamespace: platform.Namespace,
		workflowproj.LabelService:      psh.GetServiceName(),
		workflowproj.LabelK8SName:      psh.GetContainerName(),
		workflowproj.LabelK8SComponent: psh.GetServiceName(),
		workflowproj.LabelK8SPartOF:    platform.Name,
		workflowproj.LabelK8SManagedBy: "sonataflow-operator",
	}
	selectorLbl := map[string]string{
		workflowproj.LabelService: psh.GetServiceName(),
	}
	return lbl, selectorLbl
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

func createOrUpdateKnativeResources(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psh services.PlatformServiceHandler) error {
	lbl, _ := getLabels(platform, psh)
	objs, err := psh.GenerateKnativeResources(platform, lbl)
	if err != nil {
		return err
	}
	// Create or update triggers
	for _, obj := range objs {
		if triggerDef, ok := obj.(*eventingv1.Trigger); ok {
			if platform.Namespace == obj.GetNamespace() {
				if err := controllerutil.SetControllerReference(platform, obj, client.Scheme()); err != nil {
					return err
				}
			} else {
				// This is for Knative trigger in a different namespace
				// Set the finalizer for trigger cleanup when the platform is deleted
				if err := setSonataFlowPlatformFinalizer(ctx, client, platform); err != nil {
					return err
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
				return err
			}
			addToSonataFlowPlatformTriggerList(platform, trigger)
		}
	}

	if err := SafeUpdatePlatformStatus(ctx, platform); err != nil {
		return err
	}

	// Create or update sinkbindings
	for _, obj := range objs {
		if sbDef, ok := obj.(*sourcesv1.SinkBinding); ok {
			if err := controllerutil.SetControllerReference(platform, obj, client.Scheme()); err != nil {
				return err
			}
			sinkBinding := &sourcesv1.SinkBinding{
				ObjectMeta: sbDef.ObjectMeta,
			}
			_, err = controllerutil.CreateOrUpdate(ctx, client, sinkBinding, func() error {
				sinkBinding.Spec = sbDef.Spec
				return nil
			})
			if err != nil {
				return err
			}
			kSinkInjected, err := psh.CheckKSinkInjected()
			if err != nil {
				return err
			}
			if !kSinkInjected {
				return fmt.Errorf("waiting for K_SINK injection for %s to complete", psh.GetServiceName())
			}
		}
	}
	return nil
}

func addToSonataFlowPlatformTriggerList(platform *operatorapi.SonataFlowPlatform, trigger *eventingv1.Trigger) {
	for _, t := range platform.Status.Triggers {
		if t.Name == trigger.Name && t.Namespace == trigger.Namespace {
			return // trigger already exists
		}
	}
	platform.Status.Triggers = append(platform.Status.Triggers, operatorapi.SonataFlowPlatformTriggerRef{Name: trigger.Name, Namespace: trigger.Namespace})
}

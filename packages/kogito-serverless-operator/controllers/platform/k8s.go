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

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/platform/services"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/variables"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/log"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils"
	kubeutil "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/workflowproj"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
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
	if err := ConfigureDefaults(ctx, action.client, platform, false); err != nil {
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
	return createOrUpdateService(ctx, client, platform, psh)
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
	dataDeployContainer := &corev1.Container{
		Image:           imageTag,
		ImagePullPolicy: kubeutil.GetImagePullPolicy(imageTag),
		Env:             psh.GetEnvironmentVariables(),
		Resources:       psh.GetPodResourceRequirements(),
		ReadinessProbe:  readyProbe,
		LivenessProbe:   liveProbe,
		Ports: []corev1.ContainerPort{
			{
				Name:          utils.HttpScheme,
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
	dataDeployContainer = psh.ConfigurePersistence(dataDeployContainer)
	dataDeployContainer, err := psh.MergeContainerSpec(dataDeployContainer)
	if err != nil {
		return err
	}

	// immutable
	dataDeployContainer.Name = psh.GetContainerName()

	replicas := psh.GetReplicaCount()
	lbl, selectorLbl := getLabels(platform, psh)
	dataDeploySpec := appsv1.DeploymentSpec{
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

	dataDeploySpec.Template.Spec, err = psh.MergePodSpec(dataDeploySpec.Template.Spec)
	if err != nil {
		return err
	}
	kubeutil.AddOrReplaceContainer(dataDeployContainer.Name, *dataDeployContainer, &dataDeploySpec.Template.Spec)

	dataDeploy := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: platform.Namespace,
			Name:      psh.GetServiceName(),
			Labels:    lbl,
		}}
	if err := controllerutil.SetControllerReference(platform, dataDeploy, client.Scheme()); err != nil {
		return err
	}

	// Create or Update the deployment
	if op, err := controllerutil.CreateOrUpdate(ctx, client, dataDeploy, func() error {
		dataDeploy.Spec = dataDeploySpec

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
				Name:       utils.HttpScheme,
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
		workflowproj.LabelApp:     platform.Name,
		workflowproj.LabelService: psh.GetServiceName(),
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
	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      psh.GetServiceCmName(),
			Namespace: platform.Namespace,
			Labels:    lbl,
		},
		Data: map[string]string{
			workflowproj.ApplicationPropertiesFileName: handler.Build(),
		},
	}
	if err := controllerutil.SetControllerReference(platform, configMap, client.Scheme()); err != nil {
		return err
	}

	// Create or Update the service
	if op, err := controllerutil.CreateOrUpdate(ctx, client, configMap, func() error {
		configMap.Data[workflowproj.ApplicationPropertiesFileName] = handler.WithUserProperties(configMap.Data[workflowproj.ApplicationPropertiesFileName]).Build()

		return nil
	}); err != nil {
		return err
	} else {
		klog.V(log.I).InfoS("ConfigMap successfully reconciled", "operation", op)
	}

	return nil
}

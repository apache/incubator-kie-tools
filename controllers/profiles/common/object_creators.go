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

package common

import (
	"github.com/imdario/mergo"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/persistence"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/properties"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils"
	kubeutil "github.com/apache/incubator-kie-kogito-serverless-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-kogito-serverless-operator/utils/openshift"
	"github.com/apache/incubator-kie-kogito-serverless-operator/workflowproj"
)

// ObjectCreator is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type ObjectCreator func(workflow *operatorapi.SonataFlow) (client.Object, error)

const (
	defaultHTTPServicePort = 80

	// Quarkus Health Check Probe configuration.
	// See: https://quarkus.io/guides/smallrye-health#running-the-health-check

	quarkusHealthPathStarted = "/q/health/started"
	QuarkusHealthPathReady   = "/q/health/ready"
	QuarkusHealthPathLive    = "/q/health/live"

	// Default deployment health check configuration
	// See: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

	healthTimeoutSeconds             = 3
	healthStartedFailureThreshold    = 5
	healthStartedPeriodSeconds       = 15
	healthStartedInitialDelaySeconds = 10
	defaultSchemaName                = "default"
)

var (
	DefaultHTTPWorkflowPortIntStr = intstr.FromInt(constants.DefaultHTTPWorkflowPortInt)
)

// DeploymentCreator is an objectCreator for a base Kubernetes Deployments for profiles that need to deploy the workflow on a vanilla deployment.
// It serves as a basis for a basic Quarkus Java application, expected to listen on http 8080.
func DeploymentCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	lbl := workflowproj.GetDefaultLabels(workflow)

	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: getReplicasOrDefault(workflow),
			Selector: &metav1.LabelSelector{
				MatchLabels: lbl,
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: lbl,
				},
				Spec: corev1.PodSpec{},
			},
		},
	}

	if err := mergo.Merge(&deployment.Spec.Template.Spec, workflow.Spec.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride); err != nil {
		return nil, err
	}

	flowContainer, err := defaultContainer(workflow)
	if err != nil {
		return nil, err
	}
	kubeutil.AddOrReplaceContainer(operatorapi.DefaultContainerName, *flowContainer, &deployment.Spec.Template.Spec)

	return deployment, nil
}

func getReplicasOrDefault(workflow *operatorapi.SonataFlow) *int32 {
	var dReplicas int32 = 1
	if workflow.Spec.PodTemplate.Replicas == nil {
		return &dReplicas
	}
	return workflow.Spec.PodTemplate.Replicas
}

func defaultContainer(workflow *operatorapi.SonataFlow) (*corev1.Container, error) {
	defaultContainerPort := corev1.ContainerPort{
		ContainerPort: DefaultHTTPWorkflowPortIntStr.IntVal,
		Name:          utils.HttpScheme,
		Protocol:      corev1.ProtocolTCP,
	}
	defaultFlowContainer := &corev1.Container{
		Name:                     operatorapi.DefaultContainerName,
		Ports:                    []corev1.ContainerPort{defaultContainerPort},
		TerminationMessagePolicy: corev1.TerminationMessageFallbackToLogsOnError,
		LivenessProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: QuarkusHealthPathLive,
					Port: DefaultHTTPWorkflowPortIntStr,
				},
			},
			TimeoutSeconds: healthTimeoutSeconds,
		},
		ReadinessProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: QuarkusHealthPathReady,
					Port: DefaultHTTPWorkflowPortIntStr,
				},
			},
			TimeoutSeconds: healthTimeoutSeconds,
		},
		StartupProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: quarkusHealthPathStarted,
					Port: DefaultHTTPWorkflowPortIntStr,
				},
			},
			InitialDelaySeconds: healthStartedInitialDelaySeconds,
			TimeoutSeconds:      healthTimeoutSeconds,
			FailureThreshold:    healthStartedFailureThreshold,
			PeriodSeconds:       healthStartedPeriodSeconds,
		},
		SecurityContext: kubeutil.SecurityDefaults(),
	}
	// Merge with flowContainer
	if err := mergo.Merge(defaultFlowContainer, workflow.Spec.PodTemplate.Container.ToContainer(), mergo.WithOverride); err != nil {
		return nil, err
	}
	defaultFlowContainer = ConfigurePersistence(defaultFlowContainer, workflow.Spec.Persistence, defaultSchemaName, workflow.Namespace)
	// immutable
	defaultFlowContainer.Name = operatorapi.DefaultContainerName
	portIdx := -1
	for i := range defaultFlowContainer.Ports {
		if defaultFlowContainer.Ports[i].Name == utils.HttpScheme ||
			defaultFlowContainer.Ports[i].ContainerPort == DefaultHTTPWorkflowPortIntStr.IntVal {
			portIdx = i
			break
		}
	}
	if portIdx < 0 {
		defaultFlowContainer.Ports = append(defaultFlowContainer.Ports, defaultContainerPort)
	} else {
		defaultFlowContainer.Ports[portIdx] = defaultContainerPort
	}

	return defaultFlowContainer, nil
}

// ServiceCreator is an objectCreator for a basic Service aiming a vanilla Kubernetes Deployment.
// It maps the default HTTP port (80) to the target Java application webserver on port 8080.
func ServiceCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	lbl := workflowproj.GetDefaultLabels(workflow)

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: corev1.ServiceSpec{
			Selector: lbl,
			Ports: []corev1.ServicePort{{
				Protocol:   corev1.ProtocolTCP,
				Port:       defaultHTTPServicePort,
				TargetPort: DefaultHTTPWorkflowPortIntStr,
			}},
		},
	}

	return service, nil
}

// OpenShiftRouteCreator is an ObjectCreator for a basic Route for a workflow running on OpenShift.
// It enables the exposition of the service using an OpenShift Route.
// See: https://github.com/openshift/api/blob/d170fcdc0fa638b664e4f35f2daf753cb4afe36b/route/v1/route.crd.yaml
func OpenShiftRouteCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	route, err := openshift.RouteForWorkflow(workflow)
	return route, err
}

// WorkflowPropsConfigMapCreator creates a ConfigMap to hold the external application properties
func WorkflowPropsConfigMapCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	props, err := properties.ImmutableApplicationProperties(workflow, nil)
	if err != nil {
		return nil, err
	}
	return workflowproj.CreateNewAppPropsConfigMap(workflow, props), nil
}

func ConfigurePersistence(serviceContainer *corev1.Container, options *operatorapi.PersistenceOptions, defaultSchema, namespace string) *corev1.Container {
	c := serviceContainer.DeepCopy()
	if options != nil && options.PostgreSql != nil {
		c.Env = append(c.Env, persistence.ConfigurePostgreSqlEnv(options.PostgreSql, defaultSchema, namespace)...)
	}
	return c
}

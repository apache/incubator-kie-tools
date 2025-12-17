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
	"context"
	"fmt"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles"

	servingv1 "knative.dev/serving/pkg/apis/serving/v1"

	cncfmodel "github.com/serverlessworkflow/sdk-go/v2/model"

	"github.com/imdario/mergo"
	prometheus "github.com/prometheus-operator/prometheus-operator/pkg/apis/monitoring/v1"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	"knative.dev/pkg/kmeta"
	"knative.dev/pkg/tracker"
	"sigs.k8s.io/controller-runtime/pkg/client"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/persistence"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/properties"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/variables"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
	kubeutil "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/openshift"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
)

const (
	knativeServingAPIVersion = "serving.knative.dev/v1"
	knativeServiceKind       = "Service"
	deploymentAPIVersion     = "apps/v1"
	deploymentKind           = "Deployment"
	k8sServiceAPIVersion     = "v1"
	k8sServiceKind           = "Service"
	k8sServicePortName       = "web"
	metricsServicePortPath   = "/q/metrics"
)

// ObjectCreator is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type ObjectCreator func(workflow *operatorapi.SonataFlow) (client.Object, error)

// ObjectCreatorWithPlatform is the func equivalent to ObjectCreator to use when the resource being created needs a reference to the
// SonataFlowPlatform
type ObjectCreatorWithPlatform func(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (client.Object, error)

// ObjectsCreator creates multiple resources
type ObjectsCreator func(workflow *operatorapi.SonataFlow) ([]client.Object, error)

// ObjectsCreatorWithPlatform creates multiple resources
type ObjectsCreatorWithPlatform func(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) ([]client.Object, error)

const (
	defaultHTTPServicePort = 80

	// Default deployment health check configuration
	// See: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

	healthTimeoutSeconds             = 3
	healthStartedFailureThreshold    = 5
	healthStartedPeriodSeconds       = 15
	healthStartedInitialDelaySeconds = 10
)

// DeploymentCreator is an objectCreator for a base Kubernetes Deployments for profiles that need to deploy the workflow on a vanilla deployment.
// It serves as a basis for a basic Quarkus Java application, expected to listen on http 8080.
func DeploymentCreator(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) (client.Object, error) {
	lbl := workflowproj.GetMergedLabels(workflow)

	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: getReplicasOrDefault(workflow),
			Selector: &metav1.LabelSelector{
				MatchLabels: workflowproj.GetSelectorLabels(workflow),
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
	flowContainer, err := defaultContainer(workflow, plf)
	if err != nil {
		return nil, err
	}
	kubeutil.AddOrReplaceContainer(operatorapi.DefaultContainerName, *flowContainer, &deployment.Spec.Template.Spec)

	return deployment, nil
}

// KServiceCreator creates the default Knative Service object for SonataFlow instances. It's based on the default DeploymentCreator.
func KServiceCreator(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) (client.Object, error) {
	lbl := workflowproj.GetMergedLabels(workflow)
	ksvc := &servingv1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: servingv1.ServiceSpec{
			ConfigurationSpec: servingv1.ConfigurationSpec{
				Template: servingv1.RevisionTemplateSpec{
					ObjectMeta: metav1.ObjectMeta{
						Labels: lbl,
					},
					Spec: servingv1.RevisionSpec{
						PodSpec: corev1.PodSpec{},
					},
				},
			},
		},
	}

	if err := mergo.Merge(&ksvc.Spec.Template.Spec.PodSpec, workflow.Spec.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride); err != nil {
		return nil, err
	}
	flowContainer, err := defaultContainer(workflow, plf)
	if err != nil {
		return nil, err
	}
	kubeutil.AddOrReplaceContainer(operatorapi.DefaultContainerName, *flowContainer, &ksvc.Spec.Template.Spec.PodSpec)

	return ksvc, nil
}

func getReplicasOrDefault(workflow *operatorapi.SonataFlow) *int32 {
	var dReplicas int32 = 1
	if workflow.Spec.PodTemplate.Replicas == nil {
		return &dReplicas
	}
	return workflow.Spec.PodTemplate.Replicas
}

func defaultContainer(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) (*corev1.Container, error) {
	defaultContainerPort := corev1.ContainerPort{
		ContainerPort: variables.DefaultHTTPWorkflowPortIntStr.IntVal,
		Name:          utils.DefaultServicePortName,
		Protocol:      corev1.ProtocolTCP,
	}
	defaultFlowContainer := &corev1.Container{
		Name:                     operatorapi.DefaultContainerName,
		Ports:                    []corev1.ContainerPort{defaultContainerPort},
		TerminationMessagePolicy: corev1.TerminationMessageFallbackToLogsOnError,
		LivenessProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: constants.QuarkusHealthPathLive,
					Port: variables.DefaultHTTPWorkflowPortIntStr,
				},
			},
			TimeoutSeconds: healthTimeoutSeconds,
			PeriodSeconds:  healthStartedPeriodSeconds,
		},
		ReadinessProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: constants.QuarkusHealthPathReady,
					Port: variables.DefaultHTTPWorkflowPortIntStr,
				},
			},
			TimeoutSeconds: healthTimeoutSeconds,
			PeriodSeconds:  healthStartedPeriodSeconds,
		},
		StartupProbe: &corev1.Probe{
			ProbeHandler: corev1.ProbeHandler{
				HTTPGet: &corev1.HTTPGetAction{
					Path: constants.QuarkusHealthPathStarted,
					Port: variables.DefaultHTTPWorkflowPortIntStr,
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
	if !profiles.IsDevProfile(workflow) {
		var pper *operatorapi.PlatformPersistenceOptionsSpec
		if plf != nil && plf.Spec.Persistence != nil {
			pper = plf.Spec.Persistence
		}
		if p := persistence.RetrieveConfiguration(workflow.Spec.Persistence, pper, workflow.Name); p != nil {
			defaultFlowContainer = persistence.ConfigureWorkflowPersistence(defaultFlowContainer, p, workflow.Name, workflow.Namespace)
		}
	}
	// immutable
	defaultFlowContainer.Name = operatorapi.DefaultContainerName
	portIdx := -1
	for i := range defaultFlowContainer.Ports {
		if defaultFlowContainer.Ports[i].Name == utils.DefaultServicePortName ||
			defaultFlowContainer.Ports[i].ContainerPort == variables.DefaultHTTPWorkflowPortIntStr.IntVal {
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
	lbl := workflowproj.GetMergedLabels(workflow)

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: corev1.ServiceSpec{
			Selector: lbl,
			Ports: []corev1.ServicePort{{
				Name:       k8sServicePortName,
				Protocol:   corev1.ProtocolTCP,
				Port:       defaultHTTPServicePort,
				TargetPort: variables.DefaultHTTPWorkflowPortIntStr,
			}},
		},
	}

	return service, nil
}

// SinkBindingCreator is an ObjectsCreator for SinkBinding.
// It will create v1.SinkBinding based on events defined in workflow.
func SinkBindingCreator(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) (client.Object, error) {
	lbl := workflowproj.GetMergedLabels(workflow)

	sink, err := knative.GetWorkflowSink(workflow, plf)
	if err != nil {
		return nil, err
	}
	if sink == nil {
		return nil, nil /*nothing to do*/
	}

	apiVersion := deploymentAPIVersion
	kind := deploymentKind
	if workflow.Spec.PodTemplate.DeploymentModel == operatorapi.KnativeDeploymentModel {
		apiVersion = knativeServingAPIVersion // use knative serving API Version
		kind = knativeServiceKind
	}

	// subject must be deployment to inject K_SINK, service won't work
	sinkBinding := &sourcesv1.SinkBinding{
		ObjectMeta: metav1.ObjectMeta{
			Name:      strings.ToLower(fmt.Sprintf("%s-sb", workflow.Name)),
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: sourcesv1.SinkBindingSpec{
			SourceSpec: duckv1.SourceSpec{
				Sink: *sink,
			},
			BindingSpec: duckv1.BindingSpec{
				Subject: tracker.Reference{
					Name:       workflow.Name,
					Namespace:  workflow.Namespace,
					APIVersion: apiVersion,
					Kind:       kind,
				},
			},
		},
	}
	return sinkBinding, nil
}

func getBrokerRefFromPlatform(plf *operatorapi.SonataFlowPlatform, checkRemote bool) (*duckv1.KReference, error) {
	// check the local platform
	if plf.Spec.Eventing != nil && plf.Spec.Eventing.Broker != nil && plf.Spec.Eventing.Broker.Ref != nil {
		ref := plf.Spec.Eventing.Broker.Ref.DeepCopy()
		if len(ref.Namespace) == 0 {
			ref.Namespace = plf.Namespace // default to the platform namespace
		}
		return ref, nil
	}
	// Check the cluster platform
	if checkRemote && plf.Status.ClusterPlatformRef != nil && len(plf.Status.ClusterPlatformRef.PlatformRef.Name) > 0 {
		platform := &operatorapi.SonataFlowPlatform{}
		if err := utils.GetClient().Get(context.TODO(), types.NamespacedName{Namespace: plf.Status.ClusterPlatformRef.PlatformRef.Namespace, Name: plf.Status.ClusterPlatformRef.PlatformRef.Name}, platform); err != nil {
			if errors.IsNotFound(err) {
				return nil, nil
			}
			return nil, err
		}
		return getBrokerRefFromPlatform(platform, false)
	}
	return nil, nil
}

func getBrokerRefForEventType(eventType string, workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) (*duckv1.KReference, error) {
	// Check the workflow
	for _, source := range workflow.Spec.Sources {
		if source.EventType == eventType {
			ref := source.Ref.DeepCopy()
			if len(ref.Namespace) == 0 {
				ref.Namespace = workflow.Namespace // default to the workflow namespace
			}
			return ref, nil
		}
	}
	// get the broker from the local platform or cluster platform
	return getBrokerRefFromPlatform(plf, true)
}

// TriggersCreator is an ObjectsCreator for Triggers.
// It will create a list of eventingv1.Trigger based on events defined in workflow.
func TriggersCreator(workflow *operatorapi.SonataFlow, plf *operatorapi.SonataFlowPlatform) ([]client.Object, error) {
	var resultObjects []client.Object
	lbl := workflowproj.GetMergedLabels(workflow)

	apiVersion := k8sServiceAPIVersion
	kind := k8sServiceKind
	if workflow.Spec.PodTemplate.DeploymentModel == operatorapi.KnativeDeploymentModel {
		apiVersion = knativeServingAPIVersion // use knative serving API Version
		kind = knativeServiceKind
	}
	//consumed
	events := workflow.Spec.Flow.Events
	for _, event := range events {
		// filter out produce events
		if event.Kind == cncfmodel.EventKindProduced {
			continue
		}
		brokerRef, err := getBrokerRefForEventType(event.Type, workflow, plf)
		if err != nil {
			return nil, err
		}
		if brokerRef == nil || !knative.IsKnativeBroker(brokerRef) {
			// No broker configured for the eventType. Skip and will not create trigger for it.
			continue
		}
		if _, err := knative.ValidateBroker(brokerRef.Name, brokerRef.Namespace); err != nil {
			return nil, err
		}
		// construct eventingv1.Trigger
		// The trigger must be created in the same namespace as the broker
		trigger := &eventingv1.Trigger{
			ObjectMeta: metav1.ObjectMeta{
				Name:      kmeta.ChildName(strings.ToLower(fmt.Sprintf("%s-%s-", workflow.Name, event.Name)), string(workflow.GetUID())),
				Namespace: brokerRef.Namespace,
				Labels:    lbl,
			},
			Spec: eventingv1.TriggerSpec{
				Broker: brokerRef.Name,
				Filter: &eventingv1.TriggerFilter{
					Attributes: eventingv1.TriggerFilterAttributes{
						"type": event.Type,
					},
				},
				Subscriber: duckv1.Destination{
					Ref: &duckv1.KReference{
						Name:       workflow.Name,
						Namespace:  workflow.Namespace,
						APIVersion: apiVersion,
						Kind:       kind,
					},
				},
			},
		}
		resultObjects = append(resultObjects, trigger)
	}
	return resultObjects, nil
}

// OpenShiftRouteCreator is an ObjectCreator for a basic Route for a workflow running on OpenShift.
// It enables the exposition of the service using an OpenShift Route.
// See: https://github.com/openshift/api/blob/d170fcdc0fa638b664e4f35f2daf753cb4afe36b/route/v1/route.crd.yaml
func OpenShiftRouteCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	route, err := openshift.RouteForWorkflow(workflow)
	return route, err
}

// UserPropsConfigMapCreator creates an empty ConfigMap to hold the user application properties
func UserPropsConfigMapCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	return workflowproj.CreateNewUserPropsConfigMap(workflow), nil
}

// ManagedPropsConfigMapCreator creates an empty ConfigMap to hold the external application properties
func ManagedPropsConfigMapCreator(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) (client.Object, error) {
	props, err := properties.ApplicationManagedProperties(workflow, platform)
	if err != nil {
		return nil, err
	}
	return workflowproj.CreateNewManagedPropsConfigMap(workflow, props), nil
}

// ServiceMonitorCreator is an ObjectsCreator for Service Monitor for the workflow service.
func ServiceMonitorCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	lbl := workflowproj.GetMergedLabels(workflow)
	spec := &prometheus.ServiceMonitorSpec{
		Selector: metav1.LabelSelector{
			MatchLabels: map[string]string{
				workflowproj.LabelWorkflow:          workflow.Name,
				workflowproj.LabelWorkflowNamespace: workflow.Namespace,
			},
		},
		Endpoints: []prometheus.Endpoint{
			{
				Port: k8sServicePortName,
				Path: metricsServicePortPath,
			},
		},
	}
	serviceMonitor := &prometheus.ServiceMonitor{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: *spec,
	}
	return serviceMonitor, nil
}

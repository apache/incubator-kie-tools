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
	"github.com/magiconair/properties"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/kiegroup/kogito-serverless-operator/workflowproj"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"
	"github.com/kiegroup/kogito-serverless-operator/utils/openshift"
)

const (
	defaultHTTPWorkflowPortInt = 8080
	defaultHTTPWorkflowPortStr = "8080"
	defaultContainerName       = "workflow"

	defaultHTTPServicePort = 80

	configMapWorkflowPropsVolumeName = "workflow-properties"

	// Quarkus Health Check Probe configuration.
	// See: https://quarkus.io/guides/smallrye-health#running-the-health-check

	quarkusHealthPathStarted = "/q/health/started"
	quarkusHealthPathReady   = "/q/health/ready"
	quarkusHealthPathLive    = "/q/health/live"

	// Default deployment health check configuration
	// See: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/

	healthTimeoutSeconds             = 3
	healthStartedFailureThreshold    = 5
	healthStartedPeriodSeconds       = 15
	healthStartedInitialDelaySeconds = 10
)

var defaultHTTPWorkflowPortIntStr = intstr.FromInt(defaultHTTPWorkflowPortInt)

// same for now
var defaultProdApplicationProperties = defaultDevApplicationProperties

// objectCreator is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type objectCreator func(workflow *operatorapi.SonataFlow) (client.Object, error)

// defaultDeploymentCreator is an objectCreator for a base Kubernetes Deployments for profiles that need to deploy the workflow on a vanilla deployment.
// It serves as a basis for a basic Quarkus Java application, expected to listen on http 8080.
func defaultDeploymentCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	lbl := workflowproj.GetDefaultLabels(workflow)
	size := int32(1)
	deployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: &size,
			Selector: &metav1.LabelSelector{
				MatchLabels: lbl,
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: lbl,
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{{
						Name: defaultContainerName,
						Ports: []corev1.ContainerPort{{
							ContainerPort: defaultHTTPWorkflowPortInt,
							Name:          "http",
							Protocol:      corev1.ProtocolTCP,
						}},
						TerminationMessagePolicy: corev1.TerminationMessageFallbackToLogsOnError,
						LivenessProbe: &corev1.Probe{
							ProbeHandler: corev1.ProbeHandler{
								HTTPGet: &corev1.HTTPGetAction{
									Path: quarkusHealthPathLive,
									Port: defaultHTTPWorkflowPortIntStr,
								},
							},
							TimeoutSeconds: healthTimeoutSeconds,
						},
						ReadinessProbe: &corev1.Probe{
							ProbeHandler: corev1.ProbeHandler{
								HTTPGet: &corev1.HTTPGetAction{
									Path: quarkusHealthPathReady,
									Port: defaultHTTPWorkflowPortIntStr,
								},
							},
							TimeoutSeconds: healthTimeoutSeconds,
						},
						StartupProbe: &corev1.Probe{
							ProbeHandler: corev1.ProbeHandler{
								HTTPGet: &corev1.HTTPGetAction{
									Path: quarkusHealthPathStarted,
									Port: defaultHTTPWorkflowPortIntStr,
								},
							},
							InitialDelaySeconds: healthStartedInitialDelaySeconds,
							TimeoutSeconds:      healthTimeoutSeconds,
							FailureThreshold:    healthStartedFailureThreshold,
							PeriodSeconds:       healthStartedPeriodSeconds,
						},
						ImagePullPolicy: corev1.PullAlways,
						SecurityContext: kubeutil.SecurityDefaults(),
					}},
				},
			},
		},
	}
	return deployment, nil
}

// naiveApplyImageDeploymentMutateVisitor creates a visitor that mutates a vanilla Kubernetes Deployment to apply the given image in the first container
func naiveApplyImageDeploymentMutateVisitor(image string) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			object.(*appsv1.Deployment).Spec.Template.Spec.Containers[0].Image = image
			return nil
		}
	}
}

// defaultDeploymentMutateVisitor guarantees the state of the default Deployment object
func defaultDeploymentMutateVisitor(workflow *operatorapi.SonataFlow) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := defaultDeploymentCreator(workflow)
			if err != nil {
				return err
			}
			ensureDefaultDeployment(original.(*appsv1.Deployment), object.(*appsv1.Deployment))
			return nil
		}
	}
}

// ensureDefaultDeployment ensure that the original Deployment fields are immutable.
func ensureDefaultDeployment(original *appsv1.Deployment, object *appsv1.Deployment) {
	object.Spec.Replicas = original.Spec.Replicas
	object.Spec.Selector = original.Spec.Selector
	object.Labels = original.GetLabels()

	workflowContainer := kubeutil.GetContainerByName(defaultContainerName, object)
	if workflowContainer == nil {
		object.Spec.Template.Spec.Containers = make([]corev1.Container, 0)
		object.Spec.Template.Spec.Containers = original.Spec.Template.Spec.Containers
	} else {
		originalContainer := original.Spec.Template.Spec.Containers[0]
		workflowContainer.SecurityContext = originalContainer.SecurityContext
		workflowContainer.Ports = originalContainer.Ports
		object.Spec.Template.Spec.Containers = make([]corev1.Container, 0)
		object.Spec.Template.Spec.Containers = append(object.Spec.Template.Spec.Containers, *workflowContainer)
	}
}

// defaultServiceCreator is an objectCreator for a basic Service aiming a vanilla Kubernetes Deployment.
// It maps the default HTTP port (80) to the target Java application webserver on port 8080.
func defaultServiceCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
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
				TargetPort: defaultHTTPWorkflowPortIntStr,
			}},
		},
	}

	return service, nil
}

// defaultNetworkCreator is an objectCreator for a basic Route for a workflow using dev profile
// running on OpenShift.
// It enables the exposition of the dev service using an OpenShift Route.
// See: https://github.com/openshift/api/blob/d170fcdc0fa638b664e4f35f2daf753cb4afe36b/route/v1/route.crd.yaml
func defaultNetworkCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	route, err := openshift.RouteForWorkflow(workflow)
	return route, err
}

func defaultServiceMutateVisitor(workflow *operatorapi.SonataFlow) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := defaultServiceCreator(workflow)
			if err != nil {
				return err
			}
			object.(*corev1.Service).Spec.Ports = original.(*corev1.Service).Spec.Ports
			object.(*corev1.Service).Labels = original.GetLabels()
			return nil
		}
	}
}

func ensureWorkflowPropertiesConfigMapMutator(workflow *operatorapi.SonataFlow, defaultProperties string) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := workflowPropsConfigMapCreator(workflow)
			if err != nil {
				return err
			}
			cm := object.(*corev1.ConfigMap)
			cm.Labels = original.GetLabels()

			_, hasKey := cm.Data[workflowproj.ApplicationPropertiesFileName]
			if !hasKey {
				cm.Data = make(map[string]string, 1)
				cm.Data[workflowproj.ApplicationPropertiesFileName] = defaultProperties
			} else {
				props, propErr := properties.LoadString(cm.Data[workflowproj.ApplicationPropertiesFileName])
				if propErr != nil {
					// can't load user's properties, replace with default
					cm.Data[workflowproj.ApplicationPropertiesFileName] = defaultProperties
					return nil
				}
				originalProps := properties.MustLoadString(original.(*corev1.ConfigMap).Data[workflowproj.ApplicationPropertiesFileName])
				// we overwrite with the defaults
				props.Merge(originalProps)
				// Disable expansions since it's not our responsibility
				// Property expansion means resolving ${} within the properties and environment context. Quarkus will do that in runtime.
				props.DisableExpansion = true
				cm.Data[workflowproj.ApplicationPropertiesFileName] = props.String()
			}

			return nil
		}
	}
}

func ensureProdWorkflowPropertiesConfigMapMutator(workflow *operatorapi.SonataFlow) mutateVisitor {
	return ensureWorkflowPropertiesConfigMapMutator(workflow, defaultProdApplicationProperties)
}

// workflowPropsConfigMapCreator creates a ConfigMap to hold the external application properties
func workflowPropsConfigMapCreator(workflow *operatorapi.SonataFlow) (client.Object, error) {
	return workflowproj.CreateNewAppPropsConfigMap(workflow, defaultDevApplicationProperties), nil
}

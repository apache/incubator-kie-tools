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
	"strconv"

	"github.com/magiconair/properties"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	kubeutil "github.com/kiegroup/kogito-serverless-operator/utils/kubernetes"

	operatorapi "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/utils"
)

const (
	defaultHTTPWorkflowPort   = 8080
	defaultHTTPServicePort    = 80
	labelApp                  = "app"
	kogitoWorkflowJSONFileExt = ".sw.json"

	applicationPropertiesFileName = "application.properties"

	workflowConfigMapNameSuffix      = "-props"
	configMapWorkflowPropsVolumeName = "workflow-properties"
)

var defaultApplicationProperties = "quarkus.http.port=" + strconv.Itoa(defaultHTTPWorkflowPort) + "\n" +
	"quarkus.http.host=0.0.0.0\n"

// objectCreator is the func that creates the initial reference object, if the object doesn't exist in the cluster, this one is created.
// Can be used as a reference to keep the object immutable
type objectCreator func(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error)

func labels(v *operatorapi.KogitoServerlessWorkflow) map[string]string {
	// Fetches and sets labels
	return map[string]string{
		labelApp: v.Name,
	}
}

// defaultDeploymentCreator is an objectCreator for a base Kubernetes Deployments for profiles that need to deploy the workflow on a vanilla deployment.
// It serves as a basis for a basic Quarkus Java application, expected to listen on http 8080.
//
// TODO: add probes to check the default port or the quarkus health check: https://issues.redhat.com/browse/KOGITO-8642
func defaultDeploymentCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	lbl := labels(workflow)
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
						SecurityContext: &corev1.SecurityContext{
							AllowPrivilegeEscalation: utils.Pbool(false),
							Privileged:               utils.Pbool(false),
							RunAsNonRoot:             utils.Pbool(true),
							SeccompProfile: &corev1.SeccompProfile{
								Type: corev1.SeccompProfileTypeRuntimeDefault,
							},
							Capabilities: &corev1.Capabilities{
								Drop: []corev1.Capability{corev1.Capability("ALL")},
							},
						},
						ImagePullPolicy: corev1.PullAlways,
						Name:            workflow.Name,
						Ports: []corev1.ContainerPort{{
							ContainerPort: defaultHTTPWorkflowPort,
							Name:          "http",
						}},
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

func defaultDeploymentMutateVisitor(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := defaultDeploymentCreator(workflow)
			if err != nil {
				return err
			}
			object.(*appsv1.Deployment).Spec.Template.Spec.Volumes = make([]corev1.Volume, 0)
			object.(*appsv1.Deployment).Spec.Template.Spec.Volumes = original.(*appsv1.Deployment).Spec.Template.Spec.Volumes
			object.(*appsv1.Deployment).Spec.Template.Spec.Containers = make([]corev1.Container, 0)
			object.(*appsv1.Deployment).Spec.Template.Spec.Containers = original.(*appsv1.Deployment).Spec.Template.Spec.Containers
			object.(*appsv1.Deployment).Spec.Replicas = original.(*appsv1.Deployment).Spec.Replicas
			object.(*appsv1.Deployment).Labels = original.GetLabels()
			return nil
		}
	}
}

// defaultServiceCreator is an objectCreator for a basic Service aiming a vanilla Kubernetes Deployment.
// It maps the default HTTP port (80) to the target Java application webserver on port 8080.
func defaultServiceCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	lbl := labels(workflow)

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
				TargetPort: intstr.FromInt(defaultHTTPWorkflowPort),
			}},
		},
	}
	return service, nil
}

func defaultServiceMutateVisitor(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
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

// workflowDefConfigMapCreator creates a new ConfigMap that holds the definition of a workflow specification.
func workflowDefConfigMapCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	workflowDef, err := utils.GetJSONWorkflow(workflow, context.TODO())
	if err != nil {
		return nil, err
	}
	lbl := labels(workflow)
	configMap := &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      workflow.Name,
			Namespace: workflow.Namespace,
			Labels:    lbl,
		},
		Data: map[string]string{workflow.Name + kogitoWorkflowJSONFileExt: string(workflowDef)},
	}
	return configMap, nil
}

func ensureWorkflowSpecConfigMapMutator(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
	return func(object client.Object) controllerutil.MutateFn {
		return func() error {
			if kubeutil.IsObjectNew(object) {
				return nil
			}
			original, err := workflowDefConfigMapCreator(workflow)
			if err != nil {
				return err
			}
			object.(*corev1.ConfigMap).Data = original.(*corev1.ConfigMap).Data
			object.(*corev1.ConfigMap).Labels = original.GetLabels()
			return nil
		}
	}
}

// workflowPropsConfigMapCreator creates a ConfigMap to hold the external application properties
func workflowPropsConfigMapCreator(workflow *operatorapi.KogitoServerlessWorkflow) (client.Object, error) {
	return &corev1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      getWorkflowPropertiesConfigMapName(workflow),
			Namespace: workflow.Namespace,
			Labels:    labels(workflow),
		},
		// we could use utils.NewJavaProperties, but this way is faster
		Data: map[string]string{applicationPropertiesFileName: defaultApplicationProperties},
	}, nil
}

func getWorkflowPropertiesConfigMapName(workflow *operatorapi.KogitoServerlessWorkflow) string {
	return workflow.Name + workflowConfigMapNameSuffix
}

func ensureWorkflowPropertiesConfigMapMutator(workflow *operatorapi.KogitoServerlessWorkflow) mutateVisitor {
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

			_, hasKey := cm.Data[applicationPropertiesFileName]
			if !hasKey {
				cm.Data = make(map[string]string, 1)
				cm.Data[applicationPropertiesFileName] = defaultApplicationProperties
			} else {
				props, propErr := properties.LoadString(cm.Data[applicationPropertiesFileName])
				if propErr != nil {
					// can't load user's properties, replace with default
					cm.Data[applicationPropertiesFileName] = defaultApplicationProperties
					return nil
				}
				originalProps := properties.MustLoadString(original.(*corev1.ConfigMap).Data[applicationPropertiesFileName])
				// we overwrite with the defaults
				props.Merge(originalProps)
				cm.Data[applicationPropertiesFileName] = props.String()
			}

			return nil
		}
	}
}

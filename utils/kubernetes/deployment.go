/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kubernetes

import (
	"context"
	v08 "github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/utils"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/apimachinery/pkg/types"
	"k8s.io/apimachinery/pkg/util/intstr"
	ctrl "sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
)

func labels(v *v08.KogitoServerlessWorkflow) map[string]string {
	// Fetches and sets labels

	return map[string]string{
		"app": v.Name,
	}
}

// EnsureDeployment ensures Deployment resource presence in given namespace.
func EnsureDeployment(ctx context.Context, c ctrl.Client,
	scheme *runtime.Scheme,
	instance *v08.KogitoServerlessWorkflow,
	registryAddress string,
) (*reconcile.Result, error) {
	dep := createDeployment(scheme, instance, registryAddress)
	// See if deployment already exists and create if it doesn't
	found := &appsv1.Deployment{}
	err := c.Get(ctx, types.NamespacedName{
		Name:      dep.Name,
		Namespace: instance.Namespace,
	}, found)
	if err != nil && errors.IsNotFound(err) {

		// Create the deployment
		err = c.Create(context.TODO(), dep)

		if err != nil {
			// Deployment failed
			return &reconcile.Result{}, err
		} else {
			// Deployment was successful
			return nil, nil
		}
	} else if err != nil {
		// Error that isn't due to the deployment not existing
		return &reconcile.Result{}, err
	} else {
		// If the deployment exists already there is an update to do
		updateErr := c.Update(context.TODO(), dep)
		if err != nil {
			// Error that isn't due to the deployment not existing
			return &reconcile.Result{}, updateErr
		} else {
			// Deployment was successful
			return nil, nil
		}
	}
}

// createDeployment is a code for Creating Deployment
func createDeployment(scheme *runtime.Scheme, v *v08.KogitoServerlessWorkflow, registryAddress string) *appsv1.Deployment {

	labels := labels(v)
	size := int32(1)
	dep := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      v.Name,
			Namespace: v.Namespace,
		},
		Spec: appsv1.DeploymentSpec{
			Replicas: &size,
			Selector: &metav1.LabelSelector{
				MatchLabels: labels,
			},
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{
					Labels: labels,
				},
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{{
						Image:           registryAddress + "/" + v.Name + utils.GetWorkflowImageTag(v),
						ImagePullPolicy: corev1.PullAlways,
						Name:            v.Name,
						Ports: []corev1.ContainerPort{{
							ContainerPort: 8080,
							Name:          "http",
						}},
					}},
				},
			},
		},
	}

	controllerutil.SetControllerReference(v, dep, scheme)
	return dep
}

// EnsureService ensures Service is Running in a namespace.
func EnsureService(c ctrl.Client,
	scheme *runtime.Scheme,
	instance *v08.KogitoServerlessWorkflow,
) (*reconcile.Result, error) {
	service := createService(scheme, instance)
	// See if service already exists and create if it doesn't
	found := &corev1.Service{}
	err := c.Get(context.TODO(), types.NamespacedName{
		Name:      service.Name,
		Namespace: instance.Namespace,
	}, found)
	if err != nil && errors.IsNotFound(err) {

		// Create the service
		err = c.Create(context.TODO(), service)

		if err != nil {
			// Service creation failed
			return &reconcile.Result{}, err
		} else {
			// Service creation was successful
			return nil, nil
		}
	} else if err != nil {
		// Error that isn't due to the service not existing
		return &reconcile.Result{}, err
	}

	return nil, nil
}

// createService is a code for creating a Service
func createService(scheme *runtime.Scheme, v *v08.KogitoServerlessWorkflow) *corev1.Service {
	labels := labels(v)

	service := &corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Name:      v.Name,
			Namespace: v.Namespace,
		},
		Spec: corev1.ServiceSpec{
			Selector: labels,
			Ports: []corev1.ServicePort{{
				Protocol:   corev1.ProtocolTCP,
				Port:       80,
				TargetPort: intstr.FromInt(8080),
			}},
		},
	}

	controllerutil.SetControllerReference(v, service, scheme)
	return service
}

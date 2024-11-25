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

package discovery

import (
	"context"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	networkingV1 "k8s.io/api/networking/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// findService finds a service by name in the given namespace.
func findService(ctx context.Context, cli client.Client, namespace string, name string) (*corev1.Service, error) {
	service := &corev1.Service{}
	if err := cli.Get(ctx, types.NamespacedName{Namespace: namespace, Name: name}, service); err != nil {
		return nil, err
	}
	return service, nil
}

// findServicesBySelectorTarget finds the services for which all the configured selector labels are present in the
// selection target map.
func findServicesBySelectorTarget(ctx context.Context, cli client.Client, namespace string, selectorTarget map[string]string) (*corev1.ServiceList, error) {
	serviceList := &corev1.ServiceList{}
	items := make([]corev1.Service, 0)
	if err := cli.List(ctx, serviceList, client.InNamespace(namespace)); err != nil {
		return nil, err
	} else {
		for _, service := range serviceList.Items {
			if len(service.Spec.Selector) > 0 && containsSubset(selectorTarget, service.Spec.Selector) {
				items = append(items, service)
			}
		}
	}
	return &corev1.ServiceList{Items: items}, nil
}

// selectBestSuitedServiceByCustomLabels In situations where a previous query returned many Services, for example, to
// access a set of pods, or a deployment, we can filter them by a set of customLabels, to determine which one is the best suited.
func selectBestSuitedServiceByCustomLabels(serviceList *corev1.ServiceList, customLabels map[string]string) *corev1.Service {
	var filteredService *corev1.Service = nil
	if len(serviceList.Items) > 0 {
		if len(serviceList.Items) == 1 {
			filteredService = &serviceList.Items[0]
		} else {
			filteredService = &serviceList.Items[0]
			if len(customLabels) > 0 {
				if filteredServiceList := filterServiceListByLabelsSubset(serviceList, customLabels); len(filteredServiceList.Items) > 0 {
					filteredService = &filteredServiceList.Items[0]
				}
			}
		}
	}
	return filteredService
}

func filterServiceListByLabelsSubset(serviceList *corev1.ServiceList, labels map[string]string) *corev1.ServiceList {
	var items = make([]corev1.Service, 0)
	for _, service := range serviceList.Items {
		if containsSubset(service.Labels, labels) {
			items = append(items, service)
		}
	}
	return &corev1.ServiceList{Items: items}
}

func containsSubset(container map[string]string, subset map[string]string) bool {
	if container == nil {
		return subset == nil
	} else if subset == nil {
		return true
	} else {
		for k, v := range subset {
			if cv := container[k]; cv != v {
				return false
			}
		}
	}
	return true
}

// findPod finds a pod by name in the given namespace.
func findPod(ctx context.Context, cli client.Client, namespace string, name string) (*corev1.Pod, error) {
	pod := &corev1.Pod{}
	if err := cli.Get(ctx, types.NamespacedName{Namespace: namespace, Name: name}, pod); err != nil {
		return nil, err
	}
	return pod, nil
}

// findPodAndReferenceServices finds a pod by name in the given namespace, at the same time it piggybacks potential
// reference services if any. The reference services are determined by looking if the corresponding selector labels
// matches the pod labels.
func findPodAndReferenceServices(ctx context.Context, cli client.Client, namespace string, name string) (*corev1.Pod, *corev1.ServiceList, error) {
	if pod, err := findPod(ctx, cli, namespace, name); err != nil {
		return nil, nil, err
	} else {
		if len(pod.Labels) > 0 {
			if serviceList, err := findServicesBySelectorTarget(ctx, cli, namespace, pod.Labels); err != nil {
				return nil, nil, err
			} else if len(serviceList.Items) > 0 {
				return pod, serviceList, nil
			}
		}
		return pod, nil, nil
	}
}

// findDeployment finds a deployment by name in the given namespace.
func findDeployment(ctx context.Context, cli client.Client, namespace string, name string) (*appsv1.Deployment, error) {
	deployment := &appsv1.Deployment{}
	if err := cli.Get(ctx, types.NamespacedName{Namespace: namespace, Name: name}, deployment); err != nil {
		return nil, err
	}
	return deployment, nil
}

// findStatefulSet finds a stateful set by name in the given namespace.
func findStatefulSet(ctx context.Context, cli client.Client, namespace string, name string) (*appsv1.StatefulSet, error) {
	statefulSet := &appsv1.StatefulSet{}
	if err := cli.Get(ctx, types.NamespacedName{Namespace: namespace, Name: name}, statefulSet); err != nil {
		return nil, err
	}
	return statefulSet, nil
}

// findIngress finds an ingress by name in the given namespace.
func findIngress(ctx context.Context, cli client.Client, namespace string, name string) (*networkingV1.Ingress, error) {
	ingress := &networkingV1.Ingress{}
	if err := cli.Get(ctx, types.NamespacedName{Namespace: namespace, Name: name}, ingress); err != nil {
		return nil, err
	}
	return ingress, nil
}

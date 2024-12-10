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
	"fmt"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	networkingV1 "k8s.io/api/networking/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	namespace1       = "namespace1"
	service1Name     = "service1Name"
	service2Name     = "service2Name"
	service3Name     = "service3Name"
	deployment1Name  = "deployment1Name"
	statefulSet1Name = "statefulSet1Name"
	pod1Name         = "pod1Name"
	pod2Name         = "pod2Name"
	pod3Name         = "pod3Name"
	container1Name   = "container1Name"
	container2Name   = "container2Name"
	ingress1Name     = "ingress1Name"
	label1           = "label1"
	valueLabel1      = "valueLabel1"
	label2           = "label2"
	valueLabel2      = "valueLabel2"
	label3           = "label3"
	valueLabel3      = "valueLabel3"
	customPortName   = "my-custom-port"
	tcp              = "TCP"
	uidOwner1        = "uidOwner1"
	uidOwner2        = "uidOwner2"
	replicaSet1Name  = "replicaSet1Name"
	replicaSet2Name  = "replicaSet2Name"
	replicaSet3Name  = "replicaSet3Name"

	knServiceName1 = "knServiceName1"
	knBrokerName1  = "knBrokerName1"

	openShiftRouteName1 = "openShiftRouteName1"
	openShiftRouteHost1 = "openshiftroutehost1"

	openShiftDeploymentConfigName1 = "openShiftDeploymentConfigName1"
)

func mockService(namespace string, name string, labels *map[string]string, selectorLabels *map[string]string) *corev1.Service {
	service := &corev1.Service{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Service",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
	}
	if labels != nil {
		service.ObjectMeta.Labels = *labels
	}
	if selectorLabels != nil {
		service.Spec.Selector = *selectorLabels
	}
	return service
}

func mockServiceWithPorts(namespace string, name string, ports ...corev1.ServicePort) *corev1.Service {
	service := mockService(namespace, name, &map[string]string{}, nil)
	service.Spec.Ports = ports
	return service
}

func mockServicePort(name string, protocol string, port int32) corev1.ServicePort {
	return corev1.ServicePort{
		Name:     name,
		Protocol: corev1.Protocol(protocol),
		Port:     port,
	}
}

func mockPod(namespace string, name string, labels *map[string]string) *corev1.Pod {
	pod := &corev1.Pod{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Pod",
			APIVersion: "v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
	}
	if labels != nil {
		pod.ObjectMeta.Labels = *labels
	}
	return pod
}

func mockPodWithContainers(namespace string, name string, containers ...corev1.Container) *corev1.Pod {
	pod := mockPod(namespace, name, &map[string]string{})
	pod.Spec.Containers = containers
	return pod
}

func mockContainerWithPorts(name string, ports ...corev1.ContainerPort) *corev1.Container {
	return &corev1.Container{
		Name:  name,
		Ports: ports,
	}
}

func mockContainerPort(name string, protocol string, port int32) corev1.ContainerPort {
	return corev1.ContainerPort{
		Name:          name,
		HostPort:      0,
		ContainerPort: port,
		Protocol:      corev1.Protocol(protocol),
	}
}

func mockReplicaSet(namespace string, name string, ownerReferenceUID string) *appsv1.ReplicaSet {
	replicaSet := &appsv1.ReplicaSet{
		TypeMeta: metav1.TypeMeta{
			Kind:       "ReplicaSet",
			APIVersion: "apps/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace:       namespace,
			Name:            name,
			OwnerReferences: []metav1.OwnerReference{{UID: types.UID(ownerReferenceUID)}},
			UID:             types.UID(fmt.Sprintf("%s-%s-mock-replicaset-uid", namespace, name)),
		},
	}
	return replicaSet
}

func mockDeployment(namespace string, name string, labels *map[string]string, selector *map[string]string) *appsv1.Deployment {
	deployment := &appsv1.Deployment{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Deployment",
			APIVersion: "apps/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
			UID:       types.UID(fmt.Sprintf("%s-%s-mock-deployment-uid", namespace, name)),
		},
	}
	if labels != nil {
		deployment.ObjectMeta.Labels = *labels
	}
	if selector != nil {
		deployment.Spec.Selector = &metav1.LabelSelector{MatchLabels: *selector}
	}
	return deployment
}

func mockStatefulSet(namespace string, name string, labels *map[string]string, selector *map[string]string) *appsv1.StatefulSet {
	statefulSet := &appsv1.StatefulSet{
		TypeMeta: metav1.TypeMeta{
			Kind:       "StatefulSet",
			APIVersion: "apps/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
			UID:       types.UID(fmt.Sprintf("%s-%s-mock-statefulset-uid", namespace, name)),
		},
	}
	if labels != nil {
		statefulSet.ObjectMeta.Labels = *labels
	}
	if selector != nil {
		statefulSet.Spec.Selector = &metav1.LabelSelector{MatchLabels: *selector}
	}
	return statefulSet
}

func mockIngress(namespace string, name string) *networkingV1.Ingress {
	ingress := &networkingV1.Ingress{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Ingress",
			APIVersion: "networking.k8s.io/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      name,
		},
	}
	return ingress
}

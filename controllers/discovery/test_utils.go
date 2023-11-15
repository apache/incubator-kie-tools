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

package discovery

import (
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	namespace1   = "namespace1"
	service1     = "service1"
	deployment1  = "deployment1"
	statefulSet1 = "statefulSet1"
	pod1         = "pod1"
	container1   = "container1"
	container2   = "container2"
	ingress1     = "ingress1"
	label1       = "label1"
	valueLabel1  = "valueLabel1"
	label2       = "label2"
	valueLabel2  = "valueLabel2"

	customPortName = "my-custom-port"
	defaultHttp    = 80
	defaultHttps   = 443
	tcp            = "TCP"
)

func mockService(namespace string, name string, labels *map[string]string) *corev1.Service {
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
	return service
}

func mockServiceWithPorts(namespace string, name string, ports ...corev1.ServicePort) *corev1.Service {
	service := mockService(namespace, name, &map[string]string{})
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

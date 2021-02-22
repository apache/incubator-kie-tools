// Copyright 2019 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package framework

import (
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/util/intstr"
)

const (
	defaultHTTPPort = 80
)

// ExtractPortsFromContainer converts ports defined in the given container to ServicePorts
func ExtractPortsFromContainer(container *corev1.Container) []corev1.ServicePort {
	if container == nil {
		return make([]corev1.ServicePort, 0)
	}
	svcPorts := make([]corev1.ServicePort, len(container.Ports))
	for i, port := range container.Ports {
		svcPorts[i] = corev1.ServicePort{
			Name:       port.Name,
			Protocol:   port.Protocol,
			Port:       defaultHTTPPort,
			TargetPort: intstr.FromInt(int(port.ContainerPort)),
		}
	}
	return svcPorts
}

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
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils/kubernetes"
	corev1 "k8s.io/api/core/v1"
)

func isSecurePort(port int) bool {
	return port == defaultHttpsPort || port == defaultAppSecurePort
}

// findBestSuitedServicePort returns the best suited ServicePort to connect to a service.
// The optional customPort can be used to determine which port should be used for the communication, when not set,
// the best suited port is returned. For this last, a secure port has precedence over a no-secure port.
func findBestSuitedServicePort(service *corev1.Service, customPort string) *corev1.ServicePort {
	// customPort is provided and is configured?
	if len(customPort) > 0 {
		if result, _ := kubernetes.GetServicePortByName(customPort, service); result != nil {
			return result
		}
	}
	// has ssl port?
	if result, _ := kubernetes.GetServicePortByName(httpsProtocol, service); result != nil {
		return result
	}
	// has http port?
	if result, _ := kubernetes.GetServicePortByName(httpProtocol, service); result != nil {
		return result
	}
	// has web port?
	if result, _ := kubernetes.GetServicePortByName(webProtocol, service); result != nil {
		return result
	}
	// by definition a service must always have at least one port, get the first port.
	return &service.Spec.Ports[0]
}

func isSecureServicePort(servicePort *corev1.ServicePort) bool {
	return servicePort.Name == httpsProtocol || isSecurePort(int(servicePort.Port))
}

// findBestSuitedContainerPort returns the best suited PortPort to connect to a pod, or nil if the pod has no ports at all.
// The optional customPort can be used to determine which port should be used for the communication, when not set,
// the best suited port is returned. For this last, a secure port has precedence over a non-secure port.
func findBestSuitedContainerPort(container *corev1.Container, customPort string) *corev1.ContainerPort {
	// containers with no ports are permitted, we must check.
	if len(container.Ports) == 0 {
		return nil
	}
	// customPort is provided and configured?
	if len(customPort) > 0 {
		if result, _ := kubernetes.GetContainerPortByName(customPort, container); result != nil {
			return result
		}
	}
	// has ssl port?
	if result, _ := kubernetes.GetContainerPortByName(httpsProtocol, container); result != nil {
		return result
	}
	// has http port?
	if result, _ := kubernetes.GetContainerPortByName(httpProtocol, container); result != nil {
		return result
	}
	// has web port?
	if result, _ := kubernetes.GetContainerPortByName(webProtocol, container); result != nil {
		return result
	}
	// when defined, a ContainerPort must always have containerPort (Required value)
	return &container.Ports[0]
}

func isSecureContainerPort(containerPort *corev1.ContainerPort) bool {
	return containerPort.Name == httpsProtocol || isSecurePort(int(containerPort.ContainerPort))
}

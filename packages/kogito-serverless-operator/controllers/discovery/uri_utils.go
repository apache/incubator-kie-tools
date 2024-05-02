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
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/utils/kubernetes"
	corev1 "k8s.io/api/core/v1"
)

func resolveServiceUri(service *corev1.Service, customPort string, outputFormat string) (string, error) {
	var port int
	var protocol string
	var host string
	var err error = nil

	switch service.Spec.Type {
	case corev1.ServiceTypeExternalName:
		// ExternalName may not work properly with SSL:
		// https://kubernetes.io/docs/concepts/services-networking/service/#externalname
		protocol = httpProtocol
		host = service.Spec.ExternalName
		port = 80
	case corev1.ServiceTypeClusterIP:
		protocol, host, port = resolveClusterIPOrTypeNodeServiceUriParams(service, customPort)
	case corev1.ServiceTypeNodePort:
		protocol, host, port = resolveClusterIPOrTypeNodeServiceUriParams(service, customPort)
	case corev1.ServiceTypeLoadBalancer:
		err = fmt.Errorf("Service type %s is not yet supported", service.Spec.Type)
	default:
		err = fmt.Errorf("Service type %s is not yet supported", service.Spec.Type)
	}
	if err != nil {
		return "", err
	}
	if service.Spec.Type == corev1.ServiceTypeExternalName || outputFormat == KubernetesIPAddress {
		return buildURI(protocol, host, port), nil
	} else {
		return buildKubernetesServiceDNSUri(protocol, service.Namespace, service.Name, port), nil
	}
}

// resolveClusterIPOrTypeNodeServiceUriParams returns the uri parameters for a service of type ClusterIP or TypeNode.
// The optional customPort can be used to determine which port should be used for the communication, when not set,
// the best suited port is returned. For this last, a secure port has precedence over a no-secure port.
func resolveClusterIPOrTypeNodeServiceUriParams(service *corev1.Service, customPort string) (protocol string, host string, port int) {
	servicePort := findBestSuitedServicePort(service, customPort)
	if isSecureServicePort(servicePort) {
		protocol = httpsProtocol
	} else {
		protocol = httpProtocol
	}
	host = service.Spec.ClusterIP
	port = int(servicePort.Port)
	return protocol, host, port
}

func resolvePodUri(pod *corev1.Pod, customContainer string, customPort string, outputFormat string) (string, error) {
	if podIp := pod.Status.PodIP; len(podIp) == 0 {
		return "", fmt.Errorf("pod: %s in namespace: %s, has no allocated address", pod.Name, pod.Namespace)
	} else {
		var container *corev1.Container
		if len(customContainer) > 0 {
			container, _ = kubernetes.GetContainerByName(customContainer, &pod.Spec)
		}
		if container == nil {
			container = &pod.Spec.Containers[0]
		}
		if containerPort := findBestSuitedContainerPort(container, customPort); containerPort == nil {
			return "", fmt.Errorf("no container port was found for pod: %s in namespace: %s", pod.Name, pod.Namespace)
		} else {
			protocol := httpProtocol
			if isSecure := isSecureContainerPort(containerPort); isSecure {
				protocol = httpsProtocol
			}
			if outputFormat == KubernetesDNSAddress {
				return buildKubernetesPodDNSUri(protocol, pod.Namespace, podIp, int(containerPort.ContainerPort)), nil
			} else {
				return buildURI(protocol, podIp, int(containerPort.ContainerPort)), nil
			}
		}
	}
}

func buildURI(scheme string, host string, port int) string {
	return fmt.Sprintf("%s://%s:%v", scheme, host, port)
}

func buildKubernetesServiceDNSUri(scheme string, namespace string, name string, port int) string {
	return fmt.Sprintf("%s://%s.%s.svc:%v", scheme, name, namespace, port)
}

func buildKubernetesPodDNSUri(scheme string, namespace string, podIP string, port int) string {
	hyphenedIp := strings.Replace(podIP, ".", "-", -1)
	return fmt.Sprintf("%s://%s.%s.pod:%v", scheme, hyphenedIp, namespace, port)
}

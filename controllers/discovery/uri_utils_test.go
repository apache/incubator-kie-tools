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
	"testing"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
)

func Test_resolveServiceUriClusterIPServiceDNSMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	doTestResolveServiceUri(t, service, corev1.ServiceTypeClusterIP, KubernetesDNSAddress, "http://service1Name.namespace1.svc:80")
}

func Test_resolveServiceUriClusterIPServiceIPAddressMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.ClusterIP = "10.1.15.16"
	doTestResolveServiceUri(t, service, corev1.ServiceTypeClusterIP, KubernetesIPAddress, "http://10.1.15.16:80")
}

func Test_resolveServiceUriNodeTypeServiceDNSMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	doTestResolveServiceUri(t, service, corev1.ServiceTypeNodePort, KubernetesDNSAddress, "http://service1Name.namespace1.svc:80")
}

func Test_resolveServiceUriNodeTypeServiceIPAddressMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.ClusterIP = "10.1.15.16"
	doTestResolveServiceUri(t, service, corev1.ServiceTypeNodePort, KubernetesIPAddress, "http://10.1.15.16:80")
}

func Test_resolveServiceUriExternalNameServiceDNSMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.ExternalName = "external.service.com"
	doTestResolveServiceUri(t, service, corev1.ServiceTypeExternalName, KubernetesIPAddress, "http://external.service.com:80")
}

func Test_resolveServiceUriExternalNameServiceIPAddressMode(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.ExternalName = "external.service.com"
	doTestResolveServiceUri(t, service, corev1.ServiceTypeExternalName, KubernetesIPAddress, "http://external.service.com:80")
}

func doTestResolveServiceUri(t *testing.T, service *corev1.Service, serviceType corev1.ServiceType, outputMode string, expectedUri string) {
	service.Spec.Type = serviceType
	result, err := resolveServiceUri(service, "", outputMode)
	assert.NoError(t, err)
	assert.Equal(t, expectedUri, result)
}

func Test_resolvePodUriDNSMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpProtocol, tcp, defaultHttpPort)),
		*mockContainerWithPorts(container2Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)))
	pod.Status.PodIP = "10.1.15.16"
	doTestResolvePodUri(t, pod, "", "", KubernetesDNSAddress, "http://10-1-15-16.namespace1.pod:80")
}

func Test_resolvePodUriIPAddressMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpProtocol, tcp, defaultHttpPort)),
		*mockContainerWithPorts(container2Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)))
	pod.Status.PodIP = "10.1.15.17"
	doTestResolvePodUri(t, pod, "", "", KubernetesIPAddress, "http://10.1.15.17:80")
}

func Test_resolvePodUriByCustomContainerDNSMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)),
		*mockContainerWithPorts("custom-container", mockContainerPort(httpProtocol, tcp, defaultHttpPort)))
	pod.Status.PodIP = "10.1.15.16"
	doTestResolvePodUri(t, pod, "custom-container", "", KubernetesDNSAddress, "http://10-1-15-16.namespace1.pod:80")
}

func Test_resolvePodUriByCustomContainerIPAddressMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)),
		*mockContainerWithPorts("custom-container", mockContainerPort(httpProtocol, tcp, defaultHttpPort)))
	pod.Status.PodIP = "10.1.15.17"
	doTestResolvePodUri(t, pod, "custom-container", "", KubernetesIPAddress, "http://10.1.15.17:80")
}

func Test_resolvePodUriByCustomContainerAndCustomPortDNSMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)),
		*mockContainerWithPorts("custom-container",
			mockContainerPort("not-wanted", tcp, 8008),
			mockContainerPort("custom-port", tcp, 8181)))
	pod.Status.PodIP = "10.1.15.16"
	doTestResolvePodUri(t, pod, "custom-container", "custom-port", KubernetesDNSAddress, "http://10-1-15-16.namespace1.pod:8181")
}

func Test_resolvePodUriByCustomContainerAndCustomPortIPAddressMode(t *testing.T) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts(container1Name, mockContainerPort(httpsProtocol, tcp, defaultHttpsPort)),
		*mockContainerWithPorts("custom-container",
			mockContainerPort("not-wanted", tcp, 8008),
			mockContainerPort("custom-port", tcp, 8181)))
	pod.Status.PodIP = "10.1.15.17"
	doTestResolvePodUri(t, pod, "custom-container", "custom-port", KubernetesIPAddress, "http://10.1.15.17:8181")
}

func doTestResolvePodUri(t *testing.T, pod *corev1.Pod, customContainer string, customPort, outputMode string, expectedUri string) {
	result, err := resolvePodUri(pod, customContainer, customPort, outputMode)
	assert.NoError(t, err)
	assert.Equal(t, expectedUri, result)
}

func Test_buildURI(t *testing.T) {
	assert.Equal(t, "http://10.1.15.16:8383", buildURI("http", "10.1.15.16", 8383))
}

func Test_buildKubernetesServiceDNSUri(t *testing.T) {
	assert.Equal(t, "http://service1Name.namespace1.svc:8383", buildKubernetesServiceDNSUri("http", namespace1, service1Name, 8383))
}

func Test_buildKubernetesPodDNSUri(t *testing.T) {
	assert.Equal(t, "http://pod1Name.namespace1.pod:8484", buildKubernetesPodDNSUri("http", namespace1, pod1Name, 8484))
}

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

func TestIsSecurePort(t *testing.T) {
	assert.False(t, isSecurePort(80))
	assert.False(t, isSecurePort(8080))
	assert.True(t, isSecurePort(443))
	assert.True(t, isSecurePort(8443))
}

func TestBestSuitedServicePort_BestIsCustomPort(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort("not-wanted", tcp, 8282),
		mockServicePort(httpsProtocol, tcp, defaultHttpsPort),
		mockServicePort(customPortName, tcp, defaultHttpPort))
	doTestBestSuitedServicePort(t, service, customPortName, &service.Spec.Ports[2])
}

func TestBestSuitedServicePort_BestIsHttpsPort(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort("not-wanted", tcp, 8282),
		mockServicePort(httpProtocol, tcp, defaultHttpPort),
		mockServicePort(httpsProtocol, tcp, defaultHttpsPort))
	doTestBestSuitedServicePort(t, service, "", &service.Spec.Ports[2])
}

func TestBestSuitedServicePort_BestIsHttpPort(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort("not-wanted", tcp, 8282),
		mockServicePort(webProtocol, tcp, 81),
		mockServicePort(httpProtocol, tcp, defaultHttpPort))
	doTestBestSuitedServicePort(t, service, "", &service.Spec.Ports[2])
}

func TestBestSuitedServicePort_BestWebPort(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort("not-wanted", tcp, 8282),
		mockServicePort(webProtocol, tcp, 81))
	doTestBestSuitedServicePort(t, service, "", &service.Spec.Ports[1])
}

func TestBestSuitedServicePort_BestIsFirst(t *testing.T) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort("first-port", tcp, 8282),
		mockServicePort("second-port", tcp, 8383))
	doTestBestSuitedServicePort(t, service, "", &service.Spec.Ports[0])
}

func TestIsSecureServicePort(t *testing.T) {
	servicePort := mockServicePort(httpsProtocol, tcp, 443)
	assert.True(t, isSecureServicePort(&servicePort))
	servicePort = mockServicePort("other-secure-port", tcp, 443)
	assert.True(t, isSecureServicePort(&servicePort))
	servicePort = mockServicePort(httpProtocol, tcp, 80)
	assert.False(t, isSecureServicePort(&servicePort))
}

func doTestBestSuitedServicePort(t *testing.T, service *corev1.Service, customPort string, expectedPort *corev1.ServicePort) {
	result := findBestSuitedServicePort(service, customPort)
	assert.Equal(t, result, expectedPort)
}

func TestBestSuitedContainerPort_ContainerWithNoPorts(t *testing.T) {
	doTestBestSuitedContainerPort(t, mockContainerWithPorts(""), "", nil)
}

func TestBestSuitedContainerPort_BestIsCustomPort(t *testing.T) {
	container := mockContainerWithPorts("", mockContainerPort("not-wanted", tcp, 8282),
		mockContainerPort(httpsProtocol, tcp, defaultHttpsPort),
		mockContainerPort(customPortName, tcp, defaultHttpPort))
	doTestBestSuitedContainerPort(t, container, customPortName, &container.Ports[2])
}

func TestBestSuitedContainerPort_BestIsHttpsPort(t *testing.T) {
	container := mockContainerWithPorts("", mockContainerPort("not-wanted", tcp, 8282),
		mockContainerPort(httpProtocol, tcp, defaultHttpPort),
		mockContainerPort(httpsProtocol, tcp, defaultHttpsPort))
	doTestBestSuitedContainerPort(t, container, "", &container.Ports[2])
}

func TestBestSuitedContainerPort_BestIsHttpPort(t *testing.T) {
	container := mockContainerWithPorts("", mockContainerPort("not-wanted", tcp, 8282),
		mockContainerPort(webProtocol, tcp, 81),
		mockContainerPort(httpProtocol, tcp, defaultHttpsPort))
	doTestBestSuitedContainerPort(t, container, "", &container.Ports[2])
}

func TestBestSuitedContainerPort_BestWebPort(t *testing.T) {
	container := mockContainerWithPorts("", mockContainerPort("not-wanted", tcp, 8282),
		mockContainerPort(webProtocol, tcp, 81))
	doTestBestSuitedContainerPort(t, container, "", &container.Ports[1])
}

func TestBestSuitedContainerPort_BestIsFirst(t *testing.T) {
	container := mockContainerWithPorts("", mockContainerPort("first-port", tcp, 8282),
		mockContainerPort("second-port", tcp, 8383))
	doTestBestSuitedContainerPort(t, container, "", &container.Ports[0])
}

func doTestBestSuitedContainerPort(t *testing.T, container *corev1.Container, customPort string, expectedPort *corev1.ContainerPort) {
	result := findBestSuitedContainerPort(container, customPort)
	assert.Equal(t, result, expectedPort)
}

func TestIsSecureContainerPort(t *testing.T) {
	containerPort := mockContainerPort(httpsProtocol, tcp, 443)
	assert.True(t, isSecureContainerPort(&containerPort))
	containerPort = mockContainerPort("other-secure-port", tcp, 443)
	assert.True(t, isSecureContainerPort(&containerPort))
	containerPort = mockContainerPort(httpProtocol, tcp, 80)
	assert.False(t, isSecureContainerPort(&containerPort))
}

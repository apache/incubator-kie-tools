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
	"fmt"
	"testing"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/networking/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func Test_NewResourceUriBuilder(t *testing.T) {
	resourceUri := NewResourceUriBuilder(KubernetesScheme).
		Kind("deployments").
		Group("apps").
		Version("v1").
		Namespace(namespace1).
		Name(service1Name).
		WithPort("custom-port-value").
		WithQueryParam(label1, valueLabel1).Build()

	assert.Equal(t, "deployments", resourceUri.GVK.Kind)
	assert.Equal(t, "apps", resourceUri.GVK.Group)
	assert.Equal(t, "v1", resourceUri.GVK.Version)
	assert.Equal(t, namespace1, resourceUri.Namespace)
	assert.Equal(t, service1Name, resourceUri.Name)
	assert.Equal(t, 2, len(resourceUri.QueryParams))
	assert.Equal(t, "custom-port-value", resourceUri.GetPort())
	assert.Equal(t, valueLabel1, resourceUri.QueryParams[label1])
	assert.Equal(t, 1, len(resourceUri.GetCustomLabels()))
	assert.Equal(t, valueLabel1, resourceUri.GetCustomLabels()[label1])
}

func Test_QueryKubernetesServiceDNSMode(t *testing.T) {
	doTestQueryKubernetesService(t, KubernetesDNSAddress, "http://service1Name.namespace1.svc:80")
}

func Test_QueryKubernetesServiceIPAddressMode(t *testing.T) {
	doTestQueryKubernetesService(t, KubernetesIPAddress, "http://10.1.5.18:80")
}

func doTestQueryKubernetesService(t *testing.T, outputFormat string, expectedUri string) {
	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.Type = corev1.ServiceTypeNodePort
	service.Spec.ClusterIP = "10.1.5.18"
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	ctg := NewServiceCatalog(cli, nil, nil)
	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Namespace(namespace1).
		Name(service1Name).Build(), outputFormat, expectedUri)
}

func Test_QueryKubernetesPodDNSMode(t *testing.T) {
	doTestQueryKubernetesPod(t, KubernetesDNSAddress, "http://10-1-12-13.namespace1.pod:80")
}

func Test_QueryKubernetesPodIPAddressMode(t *testing.T) {
	doTestQueryKubernetesPod(t, KubernetesIPAddress, "http://10.1.12.13:80")
}

func doTestQueryKubernetesPod(t *testing.T, outputFormat string, expectedUri string) {
	pod := mockPodWithContainers(namespace1, pod1Name,
		*mockContainerWithPorts("container1Name", mockContainerPort(httpProtocol, tcp, defaultHttpPort)))
	pod.Status.PodIP = "10.1.12.13"
	cli := fake.NewClientBuilder().WithRuntimeObjects(pod).Build()
	ctg := NewServiceCatalog(cli, nil, nil)
	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Kind("pods").
		Version("v1").
		Namespace(namespace1).
		Name(pod1Name).Build(), outputFormat, expectedUri)
}

func Test_QueryKubernetesDeploymentWithServiceDNSMode(t *testing.T) {
	doTesQueryKubernetesDeploymentWithService(t, KubernetesDNSAddress, "http://service1Name.namespace1.svc:80")
}

func Test_QueryKubernetesDeploymentWithServiceIPAddressMode(t *testing.T) {
	doTesQueryKubernetesDeploymentWithService(t, KubernetesIPAddress, "http://10.1.15.16:80")
}

func doTesQueryKubernetesDeploymentWithService(t *testing.T, outputFormat string, expectedUri string) {
	selector := map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}

	deployment := mockDeployment(namespace1, deployment1Name, nil, &selector)

	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.Selector = selector
	service.Spec.ClusterIP = "10.1.15.16"
	service.Spec.Type = corev1.ServiceTypeNodePort

	cli := fake.NewClientBuilder().WithRuntimeObjects(deployment, service).Build()
	ctg := NewServiceCatalog(cli, nil, nil)

	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Group("apps").
		Version("v1").
		Kind("deployments").
		Namespace(namespace1).
		Name(deployment1Name).Build(),
		outputFormat, expectedUri)
}

func Test_QueryKubernetesDeploymentWithoutServiceDNSMode(t *testing.T) {
	doTestQueryKubernetesDeploymentWithoutService(t, KubernetesDNSAddress)
}

func Test_QueryKubernetesDeploymentWithoutServiceIPAddressMode(t *testing.T) {
	doTestQueryKubernetesDeploymentWithoutService(t, KubernetesIPAddress)
}

func doTestQueryKubernetesDeploymentWithoutService(t *testing.T, outputFormat string) {
	selector := map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}

	deployment := mockDeployment(namespace1, deployment1Name, nil, &selector)
	ctg := NewServiceCatalog(fake.NewClientBuilder().WithRuntimeObjects(deployment).Build(), nil, nil)

	uri := *NewResourceUriBuilder(KubernetesScheme).
		Group("apps").
		Version("v1").
		Kind("deployments").
		Namespace(namespace1).
		Name(deployment1Name).Build()

	doTestQueryWithError(t, ctg, uri, outputFormat, fmt.Sprintf("no service was found for the deployment: %s", uri.Name))
}

func Test_QueryKubernetesStatefulSetWithServiceDNSMode(t *testing.T) {
	doTestQueryKubernetesStatefulSetWithService(t, KubernetesDNSAddress, "http://service1Name.namespace1.svc:80")
}

func Test_QueryKubernetesStatefulSetWithServiceIPAddressMode(t *testing.T) {
	doTestQueryKubernetesStatefulSetWithService(t, KubernetesIPAddress, "http://10.1.18.19:80")
}

func doTestQueryKubernetesStatefulSetWithService(t *testing.T, outputFormat string, expectedUri string) {
	selector := map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}

	statefulSet := mockStatefulSet(namespace1, statefulSet1Name, nil, &selector)

	service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
	service.Spec.Selector = selector
	service.Spec.ClusterIP = "10.1.18.19"
	service.Spec.Type = corev1.ServiceTypeNodePort

	cli := fake.NewClientBuilder().WithRuntimeObjects(statefulSet, service).Build()
	ctg := NewServiceCatalog(cli, nil, nil)

	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Group("apps").
		Version("v1").
		Kind("statefulsets").
		Namespace(namespace1).
		Name(statefulSet1Name).Build(),
		outputFormat, expectedUri)
}

func Test_QueryKubernetesStatefulSetWithoutServiceDNSMode(t *testing.T) {
	doTestQueryKubernetesStatefulSetWithoutService(t, KubernetesDNSAddress)
}

func Test_QueryKubernetesStatefulSetWithoutServiceIPAddressMode(t *testing.T) {
	doTestQueryKubernetesStatefulSetWithoutService(t, KubernetesIPAddress)
}

func doTestQueryKubernetesStatefulSetWithoutService(t *testing.T, outputFormat string) {
	selector := map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}

	statefulSet := mockStatefulSet(namespace1, statefulSet1Name, nil, &selector)
	ctg := NewServiceCatalog(fake.NewClientBuilder().WithRuntimeObjects(statefulSet).Build(), nil, nil)

	uri := *NewResourceUriBuilder(KubernetesScheme).
		Group("apps").
		Version("v1").
		Kind("statefulsets").
		Namespace(namespace1).
		Name(statefulSet1Name).Build()
	doTestQueryWithError(t, ctg, uri, outputFormat, fmt.Sprintf("no service was found for the statefulset: %s", uri.Name))
}

func Test_QueryKubernetesIngressHostNoTLS(t *testing.T) {
	doTestQueryKubernetesIngress(t, "myingresshost.com.uy", "", false, KubernetesIPAddress, "http://myingresshost.com.uy:80")
}

func Test_QueryKubernetesIngressHostWithTLS(t *testing.T) {
	doTestQueryKubernetesIngress(t, "myingresshost.com.uy", "", true, KubernetesIPAddress, "https://myingresshost.com.uy:443")
}

func Test_QueryKubernetesIngressIPNoTLS(t *testing.T) {
	doTestQueryKubernetesIngress(t, "", "142.250.184.174", false, KubernetesIPAddress, "http://142.250.184.174:80")
}

func Test_QueryKubernetesIngressIPWithTLS(t *testing.T) {
	doTestQueryKubernetesIngress(t, "", "142.250.184.174", true, KubernetesIPAddress, "https://142.250.184.174:443")
}

func doTestQueryKubernetesIngress(t *testing.T, hostName string, ip string, tls bool, outputFormat string, expectedUri string) {
	ingress := mockIngress(namespace1, ingress1Name)

	ingress.Status.LoadBalancer.Ingress = []v1.IngressLoadBalancerIngress{{Hostname: hostName, IP: ip}}
	if tls {
		ingress.Spec.TLS = []v1.IngressTLS{{}}
	}
	cli := fake.NewClientBuilder().WithRuntimeObjects(ingress).Build()
	ctg := NewServiceCatalog(cli, nil, nil)
	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Kind("ingresses").
		Group("networking.k8s.io").
		Version("v1").
		Namespace(namespace1).
		Name(ingress1Name).Build(), outputFormat, expectedUri)
}

func doTestQuery(t *testing.T, ctg ServiceCatalog, resourceUri ResourceUri, outputFormat, expectedUri string) {
	uri, err := ctg.Query(context.TODO(), resourceUri, outputFormat)
	assert.NoError(t, err)
	assert.Equal(t, expectedUri, uri)
}

func doTestQueryWithError(t *testing.T, ctg ServiceCatalog, resourceUri ResourceUri, outputFormat string, expectedErrorMessage string) {
	_, err := ctg.Query(context.TODO(), resourceUri, outputFormat)
	assert.ErrorContains(t, err, expectedErrorMessage)
}

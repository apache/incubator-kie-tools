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
	appsv1 "github.com/openshift/api/apps/v1"
	routev1 "github.com/openshift/api/route/v1"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"

	fakeappsclient "github.com/openshift/client-go/apps/clientset/versioned/fake"
	fakerouteclient "github.com/openshift/client-go/route/clientset/versioned/fake"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"testing"
)

func Test_QueryOpenShiftRoute(t *testing.T) {
	doTestQueryOpenShiftRoute(t, false, "http://openshiftroutehost1:80")
}

func Test_QueryOpenShiftRouteWithTLS(t *testing.T) {
	doTestQueryOpenShiftRoute(t, true, "https://openshiftroutehost1:443")
}

func doTestQueryOpenShiftRoute(t *testing.T, tls bool, expectedUri string) {
	route := &routev1.Route{
		TypeMeta: metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace1,
			Name:      openShiftRouteName1,
		},
		Spec: routev1.RouteSpec{
			Host: openShiftRouteHost1,
		},
		Status: routev1.RouteStatus{},
	}
	if tls {
		route.Spec.TLS = &routev1.TLSConfig{}
	}
	fakeRoutesClient := fakerouteclient.NewSimpleClientset(route)
	ctg := NewServiceCatalog(nil, nil, newOpenShiftDiscoveryClient(nil, fakeRoutesClient.RouteV1(), nil))
	doTestQuery(t, ctg, *NewResourceUriBuilder(OpenshiftScheme).
		Kind("routes").
		Group("route.openshift.io").
		Version("v1").
		Namespace(namespace1).
		Name(openShiftRouteName1).Build(), "", expectedUri)
}

func Test_QueryOpenShiftDeploymentConfigWithServiceDNSMode(t *testing.T) {
	doTestQueryOpenShiftDeploymentConfig(t, KubernetesDNSAddress, true, "http://service1Name.namespace1.svc:80", "")
}

func Test_QueryOpenShiftDeploymentConfigWithServiceIPAddressMode(t *testing.T) {
	doTestQueryOpenShiftDeploymentConfig(t, KubernetesIPAddress, true, "http://10.1.15.16:80", "")
}

func Test_QueryOpenShiftDeploymentConfigWithoutServiceDNSMode(t *testing.T) {
	doTestQueryOpenShiftDeploymentConfig(t, KubernetesDNSAddress, false, "", "no service was found for the deploymentConfig: openShiftDeploymentConfigName1")
}

func Test_QueryOpenShiftDeploymentConfigWithoutServiceIPAddressMode(t *testing.T) {
	doTestQueryOpenShiftDeploymentConfig(t, KubernetesIPAddress, false, "", "no service was found for the deploymentConfig: openShiftDeploymentConfigName1")
}

func doTestQueryOpenShiftDeploymentConfig(t *testing.T, outputFormat string, withService bool, expectedUri string, expectedError string) {
	selector := map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	deploymentConfig := &appsv1.DeploymentConfig{
		TypeMeta: metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace1,
			Name:      openShiftDeploymentConfigName1,
		},
		Spec: appsv1.DeploymentConfigSpec{
			Selector: selector,
		},
	}
	fakeClientBuilder := fake.NewClientBuilder()
	if withService {
		service := mockServiceWithPorts(namespace1, service1Name, mockServicePort(httpProtocol, tcp, defaultHttpPort))
		service.Spec.Selector = selector
		service.Spec.ClusterIP = "10.1.15.16"
		service.Spec.Type = corev1.ServiceTypeNodePort
		fakeClientBuilder.WithRuntimeObjects(service)
	}
	cli := fakeClientBuilder.Build()
	fakeAppsClient := fakeappsclient.NewSimpleClientset(deploymentConfig)
	ctg := NewServiceCatalog(nil, nil, newOpenShiftDiscoveryClient(cli, nil, fakeAppsClient.AppsV1()))

	resourceUri := *NewResourceUriBuilder(OpenshiftScheme).
		Kind("deploymentconfigs").
		Group("apps.openshift.io").
		Version("v1").
		Namespace(namespace1).
		Name(openShiftDeploymentConfigName1).Build()

	if withService {
		doTestQuery(t, ctg, resourceUri, outputFormat, expectedUri)
	} else {
		doTestQueryWithError(t, ctg, resourceUri, outputFormat, expectedError)
	}
}

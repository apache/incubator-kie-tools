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
	"context"
	"testing"

	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func Test_NewResourceUriBuilder(t *testing.T) {
	resourceUri := NewResourceUriBuilder(KubernetesScheme).
		Kind("deployments").
		Group("apps").
		Version("v1").
		Namespace(namespace1).
		Name(service1).
		Port("custom-port").
		WithLabel(label1, valueLabel1).Build()

	assert.Equal(t, "deployments", resourceUri.GVK.Kind)
	assert.Equal(t, "apps", resourceUri.GVK.Group)
	assert.Equal(t, "v1", resourceUri.GVK.Version)
	assert.Equal(t, namespace1, resourceUri.Namespace)
	assert.Equal(t, service1, resourceUri.Name)
	assert.Equal(t, 2, len(resourceUri.CustomLabels))
	assert.Equal(t, "custom-port", resourceUri.CustomLabels["port"])
	assert.Equal(t, valueLabel1, resourceUri.CustomLabels[label1])
}

func Test_QueryKubernetesServiceDNSMode(t *testing.T) {
	doTestQueryKubernetesService(t, KubernetesDNSAddress, "http://service1.namespace1.svc:80")
}

func Test_QueryKubernetesServiceIPAddressMode(t *testing.T) {
	doTestQueryKubernetesService(t, KubernetesIPAddress, "http://10.1.5.18:80")
}

func doTestQueryKubernetesService(t *testing.T, outputFormat string, expectedUri string) {
	service := mockServiceWithPorts(namespace1, service1, mockServicePort(httpProtocolName, tcp, defaultHttp))
	service.Spec.Type = corev1.ServiceTypeNodePort
	service.Spec.ClusterIP = "10.1.5.18"
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	ctg := NewServiceCatalog(cli)
	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Kind("services").
		Version("v1").
		Namespace(namespace1).
		Name(service1).Build(), outputFormat, expectedUri)
}

func Test_QueryKubernetesPodDNSMode(t *testing.T) {
	doTestQueryKubernetesPod(t, KubernetesDNSAddress, "http://10-1-12-13.namespace1.pod:80")
}

func Test_QueryKubernetesPodIPAddressMode(t *testing.T) {
	doTestQueryKubernetesPod(t, KubernetesIPAddress, "http://10.1.12.13:80")
}

func doTestQueryKubernetesPod(t *testing.T, outputFormat string, expectedUri string) {
	pod := mockPodWithContainers(namespace1, pod1,
		*mockContainerWithPorts("container1", mockContainerPort(httpProtocolName, tcp, defaultHttp)))
	pod.Status.PodIP = "10.1.12.13"
	cli := fake.NewClientBuilder().WithRuntimeObjects(pod).Build()
	ctg := NewServiceCatalog(cli)
	doTestQuery(t, ctg, *NewResourceUriBuilder(KubernetesScheme).
		Kind("pods").
		Version("v1").
		Namespace(namespace1).
		Name(pod1).Build(), outputFormat, expectedUri)
}

func doTestQuery(t *testing.T, ctg ServiceCatalog, resourceUri ResourceUri, outputFormat, expectedUri string) {
	uri, err := ctg.Query(context.TODO(), resourceUri, outputFormat)
	assert.NoError(t, err)
	assert.Equal(t, expectedUri, uri)
}

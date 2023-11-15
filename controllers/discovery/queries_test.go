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
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	networkingV1 "k8s.io/api/networking/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func Test_findService(t *testing.T) {
	service := mockService1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	result, err := findService(context.TODO(), cli, namespace1, service1)

	assert.NoError(t, err)
	assert.Equal(t, service, result)
}

func Test_findServiceNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findService(context.TODO(), cli, namespace1, service1)
	assert.ErrorContains(t, err, "\"service1\" not found")
}

func Test_findServiceByLabels(t *testing.T) {
	labels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	service := mockService1(labels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	serviceList, err := findServiceByLabels(context.TODO(), cli, namespace1, *labels)

	assert.NoError(t, err)
	assert.Len(t, serviceList.Items, 1)
	assert.Equal(t, service, &serviceList.Items[0])
}

func Test_findServiceByLabelsNotFound(t *testing.T) {
	labels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	queryLabels := map[string]string{
		label1: valueLabel1,
	}
	service := mockService1(labels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	serviceList, err := findServiceByLabels(context.TODO(), cli, namespace1, queryLabels)

	assert.NoError(t, err)
	assert.Len(t, serviceList.Items, 1)
	assert.Equal(t, service, &serviceList.Items[0])
}

func Test_findPod(t *testing.T) {
	pod := mockPod1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(pod).Build()
	result, err := findPod(context.TODO(), cli, namespace1, pod1)

	assert.NoError(t, err)
	assert.Equal(t, pod, result)
}

func Test_findPodNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findPod(context.TODO(), cli, namespace1, pod1)
	assert.ErrorContains(t, err, "\"pod1\" not found")
}

func Test_findPodAndReferenceServiceByPodLabelsWithReferenceService(t *testing.T) {
	podLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	service := mockService1(podLabels)
	pod := mockPod1(podLabels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service, pod).Build()
	resultPod, resultService, err := findPodAndReferenceServiceByPodLabels(context.TODO(), cli, namespace1, pod1)
	assert.NoError(t, err)
	assert.Equal(t, pod, resultPod)
	assert.Equal(t, service, resultService)
}

func Test_findPodAndReferenceServiceByPodLabelsWithoutReferenceService(t *testing.T) {
	podLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	serviceLabels := &map[string]string{
		label1: valueLabel1,
	}
	service := mockService1(serviceLabels)
	pod := mockPod1(podLabels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service, pod).Build()
	resultPod, resultService, err := findPodAndReferenceServiceByPodLabels(context.TODO(), cli, namespace1, pod1)
	assert.NoError(t, err)
	assert.Equal(t, pod, resultPod)
	assert.Nil(t, resultService)
}

func Test_findPodAndReferenceServiceByPodLabelsNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	resultPod, resultService, err := findPodAndReferenceServiceByPodLabels(context.TODO(), cli, namespace1, pod1)
	assert.ErrorContains(t, err, "\"pod1\" not found")
	assert.Nil(t, resultPod)
	assert.Nil(t, resultService)
}

func Test_findDeployment(t *testing.T) {
	deployment := mockDeployment1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(deployment).Build()
	result, err := findDeployment(context.TODO(), cli, namespace1, deployment1)

	assert.NoError(t, err)
	assert.Equal(t, deployment, result)
}

func Test_findDeploymentNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findDeployment(context.TODO(), cli, namespace1, deployment1)
	assert.ErrorContains(t, err, "\"deployment1\" not found")
}

func Test_findStatefulSet(t *testing.T) {
	statefulSet := mockStatefulSet1()
	cli := fake.NewClientBuilder().WithRuntimeObjects(statefulSet).Build()
	result, err := findStatefulSet(context.TODO(), cli, namespace1, statefulSet1)

	assert.NoError(t, err)
	assert.Equal(t, statefulSet, result)
}

func Test_findStatefulSetNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findStatefulSet(context.TODO(), cli, namespace1, statefulSet1)
	assert.ErrorContains(t, err, "\"statefulSet1\" not found")
}

func Test_findIngress(t *testing.T) {
	ingress := mockIngress1()
	cli := fake.NewClientBuilder().WithRuntimeObjects(ingress).Build()
	result, err := findIngress(context.TODO(), cli, namespace1, ingress1)

	assert.NoError(t, err)
	assert.Equal(t, ingress, result)
}

func Test_findIngressNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findIngress(context.TODO(), cli, namespace1, ingress1)
	assert.ErrorContains(t, err, "\"ingress1\" not found")
}

func mockService1(labels *map[string]string) *corev1.Service {
	return mockService(namespace1, service1, labels)
}

func mockPod1(labels *map[string]string) *corev1.Pod {
	return mockPod(namespace1, pod1, labels)
}

func mockDeployment1(labels *map[string]string) *appsv1.Deployment {
	deployment := &appsv1.Deployment{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Deployment",
			APIVersion: "apps/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace1,
			Name:      deployment1,
		},
	}
	if labels != nil {
		deployment.ObjectMeta.Labels = *labels
	}
	return deployment
}

func mockStatefulSet1() *appsv1.StatefulSet {
	statefulSet := &appsv1.StatefulSet{
		TypeMeta: metav1.TypeMeta{
			Kind:       "StatefulSet",
			APIVersion: "apps/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace1,
			Name:      statefulSet1,
		},
	}
	return statefulSet
}

func mockIngress1() *networkingV1.Ingress {
	ingress := &networkingV1.Ingress{
		TypeMeta: metav1.TypeMeta{
			Kind:       "Ingress",
			APIVersion: "networking.k8s.io/v1",
		},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace1,
			Name:      ingress1,
		},
	}
	return ingress
}

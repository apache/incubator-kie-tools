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
	"testing"

	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	networkingV1 "k8s.io/api/networking/v1"
	"sigs.k8s.io/controller-runtime/pkg/client/fake"
)

func Test_findService(t *testing.T) {
	service := mockService1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	result, err := findService(context.TODO(), cli, namespace1, service1Name)

	assert.NoError(t, err)
	assert.Equal(t, service, result)
}

func Test_findServiceNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findService(context.TODO(), cli, namespace1, service1Name)
	assert.ErrorContains(t, err, "\"service1Name\" not found")
}

func Test_findServicesBySelectorTarget(t *testing.T) {
	selector1Labels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	selector2Labels := &map[string]string{
		label1: valueLabel1,
		label3: valueLabel3,
	}
	selector3Labels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}

	service1 := mockService1(selector1Labels)
	service2 := mockService2(selector2Labels)
	service3 := mockService3(selector3Labels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service1, service2, service3).Build()
	serviceList, err := findServicesBySelectorTarget(context.TODO(), cli, namespace1, *selector1Labels)

	assert.NoError(t, err)
	assert.Len(t, serviceList.Items, 2)
	assert.Equal(t, service1, &serviceList.Items[0])
	assert.Equal(t, service3, &serviceList.Items[1])
}

func Test_findServicesBySelectorTargetNotFound(t *testing.T) {
	selectorLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	queryLabels := map[string]string{
		label1: valueLabel1,
	}
	service := mockService1(selectorLabels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service).Build()
	serviceList, err := findServicesBySelectorTarget(context.TODO(), cli, namespace1, queryLabels)

	assert.NoError(t, err)
	assert.Len(t, serviceList.Items, 0)
}

func Test_selectBestSuitedServiceByCustomLabels(t *testing.T) {
	service1 := mockService1(nil)
	service1.Labels = map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	service2 := mockService2(nil)
	service2.Labels = map[string]string{
		label1:        valueLabel1,
		label3:        valueLabel3,
		"environment": "dev",
	}
	service3 := mockService2(nil)
	service3.Labels = map[string]string{
		label2: valueLabel2,
		label3: valueLabel3,
	}
	serviceList := &corev1.ServiceList{
		Items: []corev1.Service{*service1, *service2, *service3},
	}
	bestSuitedService := selectBestSuitedServiceByCustomLabels(serviceList, map[string]string{"environment": "dev"})
	assert.Equal(t, service2, bestSuitedService)
}

func Test_filterServiceListByLabelsSubset(t *testing.T) {
	service1 := mockService1(nil)
	service1.Labels = map[string]string{
		label1: valueLabel1,
	}
	service2 := mockService2(nil)
	service2.Labels = map[string]string{
		label1: valueLabel1,
		label3: valueLabel3,
	}
	service3 := mockService3(nil)
	service3.Labels = map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
		label3: valueLabel3,
	}
	serviceList := &corev1.ServiceList{
		Items: []corev1.Service{*service1, *service2, *service3},
	}
	filteredServiceList := filterServiceListByLabelsSubset(serviceList, map[string]string{
		label1: valueLabel1,
		label3: valueLabel3})

	assert.Len(t, filteredServiceList.Items, 2)
	assert.Equal(t, *service2, filteredServiceList.Items[0])
	assert.Equal(t, *service3, filteredServiceList.Items[1])
}

func Test_findPod(t *testing.T) {
	pod := mockPod1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(pod).Build()
	result, err := findPod(context.TODO(), cli, namespace1, pod1Name)

	assert.NoError(t, err)
	assert.Equal(t, pod, result)
}

func Test_findPodNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findPod(context.TODO(), cli, namespace1, pod1Name)
	assert.ErrorContains(t, err, "\"pod1Name\" not found")
}

func Test_findPodAndReferenceServicesWithReferenceService(t *testing.T) {
	podLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
		label3: valueLabel3,
	}
	selectorLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	service := mockService1(selectorLabels)
	pod := mockPod1(podLabels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service, pod).Build()
	resultPod, referenceServices, err := findPodAndReferenceServices(context.TODO(), cli, namespace1, pod1Name)
	assert.NoError(t, err)
	assert.Equal(t, pod, resultPod)
	assert.Len(t, referenceServices.Items, 1)
	assert.Equal(t, *service, referenceServices.Items[0])
}

func Test_findPodAndReferenceServicesWithoutReferenceService(t *testing.T) {
	podLabels := &map[string]string{
		label1: valueLabel1,
	}
	selectorLabels := &map[string]string{
		label1: valueLabel1,
		label2: valueLabel2,
	}
	service := mockService1(selectorLabels)
	pod := mockPod1(podLabels)
	cli := fake.NewClientBuilder().WithRuntimeObjects(service, pod).Build()
	resultPod, referenceServices, err := findPodAndReferenceServices(context.TODO(), cli, namespace1, pod1Name)
	assert.NoError(t, err)
	assert.Equal(t, pod, resultPod)
	assert.Nil(t, referenceServices)
}

func Test_findPodAndReferenceServicesNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	resultPod, resultService, err := findPodAndReferenceServices(context.TODO(), cli, namespace1, pod1Name)
	assert.ErrorContains(t, err, "\"pod1Name\" not found")
	assert.Nil(t, resultPod)
	assert.Nil(t, resultService)
}

func Test_findDeployment(t *testing.T) {
	deployment := mockDeployment1(nil)
	cli := fake.NewClientBuilder().WithRuntimeObjects(deployment).Build()
	result, err := findDeployment(context.TODO(), cli, namespace1, deployment1Name)

	assert.NoError(t, err)
	assert.Equal(t, deployment, result)
}

func Test_findDeploymentNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findDeployment(context.TODO(), cli, namespace1, deployment1Name)
	assert.ErrorContains(t, err, "\"deployment1Name\" not found")
}

func Test_findStatefulSet(t *testing.T) {
	statefulSet := mockStatefulSet1()
	cli := fake.NewClientBuilder().WithRuntimeObjects(statefulSet).Build()
	result, err := findStatefulSet(context.TODO(), cli, namespace1, statefulSet1Name)

	assert.NoError(t, err)
	assert.Equal(t, statefulSet, result)
}

func Test_findStatefulSetNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findStatefulSet(context.TODO(), cli, namespace1, statefulSet1Name)
	assert.ErrorContains(t, err, "\"statefulSet1Name\" not found")
}

func Test_findIngress(t *testing.T) {
	ingress := mockIngress1()
	cli := fake.NewClientBuilder().WithRuntimeObjects(ingress).Build()
	result, err := findIngress(context.TODO(), cli, namespace1, ingress1Name)

	assert.NoError(t, err)
	assert.Equal(t, ingress, result)
}

func Test_findIngressNotFound(t *testing.T) {
	cli := fake.NewClientBuilder().Build()
	_, err := findIngress(context.TODO(), cli, namespace1, ingress1Name)
	assert.ErrorContains(t, err, "\"ingress1Name\" not found")
}

func mockService1(selectorLabels *map[string]string) *corev1.Service {
	return mockService(namespace1, service1Name, nil, selectorLabels)
}

func mockService2(selectorLabels *map[string]string) *corev1.Service {
	return mockService(namespace1, service2Name, nil, selectorLabels)
}

func mockService3(selectorLabels *map[string]string) *corev1.Service {
	return mockService(namespace1, service3Name, nil, selectorLabels)
}

func mockPod1(labels *map[string]string) *corev1.Pod {
	return mockPod(namespace1, pod1Name, labels)
}

func mockPod2(labels *map[string]string) *corev1.Pod {
	return mockPod(namespace1, pod2Name, labels)
}

func mockPod3(labels *map[string]string) *corev1.Pod {
	return mockPod(namespace1, pod3Name, labels)
}

func mockDeployment1(labels *map[string]string) *appsv1.Deployment {
	return mockDeployment(namespace1, deployment1Name, labels, nil)
}

func mockStatefulSet1() *appsv1.StatefulSet {
	return mockStatefulSet(namespace1, statefulSet1Name, nil, nil)
}

func mockIngress1() *networkingV1.Ingress {
	return mockIngress(namespace1, ingress1Name)
}

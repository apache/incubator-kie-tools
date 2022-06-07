// Copyright 2022 Red Hat, Inc. and/or its affiliates
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

package deployment

import (
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/kogitoservice"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestNewDeploymentProcessorTesting(t *testing.T) {
	ns := t.Name()
	runtimeService := test.CreateFakeKogitoRuntime(ns)
	container := corev1.Container{}
	runtimeDeployment := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      runtimeService.Name,
			Namespace: runtimeService.Namespace,
		},
		Spec: appsv1.DeploymentSpec{

			Selector: nil,
			Template: corev1.PodTemplateSpec{
				ObjectMeta: metav1.ObjectMeta{},
				Spec: corev1.PodSpec{

					Containers: []corev1.Container{container},
				},
			},
			Strategy:        appsv1.DeploymentStrategy{},
			MinReadySeconds: 0,
		},
		Status: appsv1.DeploymentStatus{
			AvailableReplicas: 1,
		},
	}

	data := make(map[string]string)
	serviceendpoints := kogitoservice.ServiceEndpoints{
		HTTPRouteEnv: "",
		HTTPRouteURI: "",
	}
	data[serviceendpoints.HTTPRouteEnv] = serviceendpoints.HTTPRouteURI
	configmap := &corev1.ConfigMap{

		TypeMeta: metav1.TypeMeta{},
		ObjectMeta: metav1.ObjectMeta{
			Namespace: ns,
			Name:      "data-index-endpoint",
		},
		Data: data,
	}

	supportingServiceList := createList(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(runtimeDeployment, configmap, supportingServiceList).Build()

	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: "1.0-SNAPSHOT",
	}
	runtimeHandler := app.NewKogitoRuntimeHandler(context)
	supportingservicehandler := app.NewKogitoSupportingServiceHandler(context)

	deploymentProcessor := NewDeploymentProcessor(context, runtimeDeployment, runtimeHandler, supportingservicehandler)
	err := deploymentProcessor.Process()

	assert.NoError(t, err)
	assert.Equal(t, runtimeDeployment.Spec.Template.Spec.Containers[0].EnvFrom[0].ConfigMapRef.Name, configmap.Name)

	v := make(map[string]string)
	v[framework.KogitoOperatorVersionAnnotation] = "1.0-SNAPSHOT"
	assert.Equal(t, v, runtimeDeployment.Annotations)
	assert.Equal(t, v, runtimeDeployment.Spec.Template.Annotations)
}

func createList(namespace string) *v1beta1.KogitoSupportingServiceList {
	items := []v1beta1.KogitoSupportingService{*test.CreateFakeJobsService(namespace), *test.CreateFakeDataIndex(namespace), *test.CreateFakeTrustyAIService(namespace)}

	return &v1beta1.KogitoSupportingServiceList{
		Items: items,
	}
}

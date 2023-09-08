// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package connector

import (
	api "github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal/app"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"testing"
)

func TestInjectDataIndexEndPointOnKogitoRuntimeServices(t *testing.T) {
	ns := t.Name()
	kogitoRuntime := test.CreateFakeKogitoRuntime(ns)
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      kogitoRuntime.Name,
			Namespace: kogitoRuntime.Namespace,
			Annotations: map[string]string{
				operator.KogitoRuntimeKey: "true",
			},
		},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{Containers: []v1.Container{{Name: "test"}}},
			},
		},
	}
	dataIndex := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
	}

	endPointConfigMap := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index-endpoint",
			Namespace: ns,
		},
		Data: map[string]string{
			"KOGITO_DATAINDEX_HTTP_URL": "http://data-index.kogito-operator-system",
			"KOGITO_DATAINDEX_WS_URL":   "ws://data-index.kogito-operator-system",
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(dc, kogitoRuntime, dataIndex, endPointConfigMap).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := app.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := app.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	err := urlHandler.InjectDataIndexEndPointOnKogitoRuntimeServices(types.NamespacedName{Name: dataIndex.Name, Namespace: dataIndex.Namespace})
	assert.NoError(t, err)

	exist, err := kubernetes.ResourceC(cli).Fetch(dc)
	assert.NoError(t, err)
	assert.True(t, exist)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].EnvFrom[0].ConfigMapRef.LocalObjectReference.Name, endPointConfigMap.Name)
}

func TestInjectDataIndexEndpointOnDeployment(t *testing.T) {
	ns := t.Name()
	dc := &appsv1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "travels",
			Namespace: ns,
		},
		Spec: appsv1.DeploymentSpec{
			Template: v1.PodTemplateSpec{
				Spec: v1.PodSpec{Containers: []v1.Container{{Name: "test"}}},
			},
		},
	}

	dataIndex := &v1beta1.KogitoSupportingService{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoSupportingServiceSpec{
			ServiceType: api.DataIndex,
		},
	}

	endPointConfigMap := &v1.ConfigMap{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "data-index-endpoint",
			Namespace: ns,
		},
		Data: map[string]string{
			"KOGITO_DATAINDEX_HTTP_URL": "http://data-index.kogito-operator-system",
			"KOGITO_DATAINDEX_WS_URL":   "ws://data-index.kogito-operator-system",
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex, endPointConfigMap).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	runtimeHandler := app.NewKogitoRuntimeHandler(context)
	supportingServiceHandler := app.NewKogitoSupportingServiceHandler(context)
	urlHandler := NewURLHandler(context, runtimeHandler, supportingServiceHandler)
	err := urlHandler.InjectDataIndexEndpointOnDeployment(dc)
	assert.NoError(t, err)
	assert.Contains(t, dc.Spec.Template.Spec.Containers[0].EnvFrom[0].ConfigMapRef.LocalObjectReference.Name, endPointConfigMap.Name)
}

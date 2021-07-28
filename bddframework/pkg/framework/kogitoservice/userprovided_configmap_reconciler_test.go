// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package kogitoservice

import (
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestUserProvidedConfigMapReconcile(t *testing.T) {
	ns := t.Name()
	configMap := &corev1.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      "custom_config",
			Namespace: ns,
		},
	}
	instance := test.CreateFakeKogitoRuntime(ns)
	instance.UID = test.GenerateUID()
	instance.Spec.PropertiesConfigMap = "custom_config"
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance, configMap).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	userProvidedConfigMapReconciler := NewUserProvidedConfigConfigMapReconciler(context, instance)
	err := userProvidedConfigMapReconciler.Reconcile()
	assert.NoError(t, err)

	expectedConfigMap := &corev1.ConfigMap{ObjectMeta: v1.ObjectMeta{Name: "custom_config", Namespace: ns}}
	exists, err := kubernetes.ResourceC(cli).Fetch(expectedConfigMap)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, instance.Name, expectedConfigMap.Labels[framework.LabelAppKey])
}

func TestUserProvidedConfigMapReconcile_configMapNotExists(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeKogitoRuntime(ns)
	instance.Spec.PropertiesConfigMap = "custom_config"
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	userProvidedConfigMapReconciler := NewUserProvidedConfigConfigMapReconciler(context, instance)
	err := userProvidedConfigMapReconciler.Reconcile()
	assert.Error(t, err)
}

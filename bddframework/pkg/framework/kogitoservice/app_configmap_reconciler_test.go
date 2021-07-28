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
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestReconcile(t *testing.T) {
	ns := t.Name()
	instance := test.CreateFakeKogitoRuntime(ns)
	instance.Spec.Config = map[string]string{
		"key1": "value1",
	}

	cli := test.NewFakeClientBuilder().Build()
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	appConfigMapReconciler := NewAppConfigMapReconciler(context, instance, infraHandler)
	err := appConfigMapReconciler.Reconcile()
	assert.NoError(t, err)

	appConfigMapHandler := NewAppConfigMapHandler(context)
	configMap := &corev1.ConfigMap{ObjectMeta: v1.ObjectMeta{Name: appConfigMapHandler.GetAppConfigMapName(instance), Namespace: instance.Namespace}}
	exists, err := kubernetes.ResourceC(cli).Fetch(configMap)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, 1, len(configMap.Data))
	assert.Equal(t, "value1", configMap.Data["key1"])
}

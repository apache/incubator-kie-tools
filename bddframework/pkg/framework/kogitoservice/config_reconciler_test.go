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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/framework"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestInfraPropertiesReconciler_CustomConfig(t *testing.T) {
	instance := &v1beta1.KogitoRuntime{
		ObjectMeta: v1.ObjectMeta{Name: "process-springboot-example", Namespace: t.Name()},
		Spec: v1beta1.KogitoRuntimeSpec{
			Runtime: api.SpringBootRuntimeType,
			KogitoServiceSpec: v1beta1.KogitoServiceSpec{
				Config: map[string]string{
					"key1": "value1",
					"key2": "value2",
				},
			},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	serviceDefinition := ServiceDefinition{}
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraPropertiesReconciler := newConfigReconciler(context, instance, &serviceDefinition)
	err := infraPropertiesReconciler.Reconcile()
	assert.NoError(t, err)

	cm := &v12.ConfigMap{ObjectMeta: v1.ObjectMeta{Name: "process-springboot-example-properties", Namespace: t.Name()}}
	exists, err := kubernetes.ResourceC(cli).Fetch(cm)
	assert.NoError(t, err)
	assert.True(t, exists)
	assert.Equal(t, instance.GetName(), cm.Labels[framework.LabelAppKey])
}

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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestInfraPropertiesReconciler(t *testing.T) {
	ns := t.Name()
	kogitoInfinispanInstance := &v1beta1.KogitoInfra{
		ObjectMeta: v1.ObjectMeta{
			Name:      "kogito-Infra",
			Namespace: ns,
		},
		Spec: v1beta1.KogitoInfraSpec{
			InfraProperties: map[string]string{
				"key1": "value1",
				"key2": "value2",
			},
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects().Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoInfinispanInstance,
	}
	infraPropertiesReconciler := initInfraPropertiesReconciler(infraContext)
	err := infraPropertiesReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 1, len(kogitoInfinispanInstance.GetStatus().GetConfigMapEnvFromReferences()))

	cmName := kogitoInfinispanInstance.GetStatus().GetConfigMapEnvFromReferences()[0]
	infraPropConfigMap := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      cmName,
			Namespace: ns,
		},
	}
	exist, err := kubernetes.ResourceC(cli).Fetch(infraPropConfigMap)
	assert.True(t, exist)
	assert.NoError(t, err)
	assert.Equal(t, 2, len(infraPropConfigMap.Data))
	assert.Equal(t, "value1", infraPropConfigMap.Data["key1"])
	assert.Equal(t, "value2", infraPropConfigMap.Data["key2"])
}

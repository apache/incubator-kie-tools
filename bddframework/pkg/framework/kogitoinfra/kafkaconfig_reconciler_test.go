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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestKafkaConfigReconciler(t *testing.T) {
	ns := t.Name()
	kogitoKafkaInstance := test.CreateFakeKogitoKafka(ns)
	kafkaInstance := test.CreateFakeKafka(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(kafkaInstance).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoKafkaInstance,
	}

	kafkaConfigReconciler := newKafkaConfigReconciler(infraContext, kafkaInstance, api.QuarkusRuntimeType)
	err := kafkaConfigReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 1, len(kogitoKafkaInstance.GetStatus().GetConfigMapEnvFromReferences()))
	cmName := kogitoKafkaInstance.GetStatus().GetConfigMapEnvFromReferences()[0]
	kafkaConfigMap := &v12.ConfigMap{
		ObjectMeta: v1.ObjectMeta{
			Name:      cmName,
			Namespace: ns,
		},
	}
	exist, err := kubernetes.ResourceC(cli).Fetch(kafkaConfigMap)
	assert.True(t, exist)
	assert.NoError(t, err)
	assert.Equal(t, 2, len(kafkaConfigMap.Data))
	assert.Equal(t, "true", kafkaConfigMap.Data[enableEventsEnvKey])
	assert.True(t, len(kafkaConfigMap.Data["kafka.bootstrap.servers"]) > 0)
}

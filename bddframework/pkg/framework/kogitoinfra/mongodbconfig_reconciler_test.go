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
	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/core/v1"
	v12 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestMongoDBConfigReconciler(t *testing.T) {
	ns := t.Name()
	kogitoMongoDBInstance := test.CreateFakeKogitoMongoDB(ns)
	mongoDBInstance := test.CreateFakeMongoDB(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects().Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoMongoDBInstance,
	}
	mongoDBConfigReconciler := newMongoDBConfigReconciler(infraContext, mongoDBInstance, api.QuarkusRuntimeType)
	err := mongoDBConfigReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 1, len(kogitoMongoDBInstance.GetStatus().GetConfigMapEnvFromReferences()))

	configMap := &v1.ConfigMap{
		ObjectMeta: v12.ObjectMeta{
			Name:      kogitoMongoDBInstance.GetStatus().GetConfigMapEnvFromReferences()[0],
			Namespace: ns,
		},
	}
	exist, err := kubernetes.ResourceC(cli).Fetch(configMap)
	assert.True(t, exist)
	assert.NoError(t, err)
	assert.Equal(t, "true", configMap.Data["ENABLE_PERSISTENCE"])
	assert.True(t, len(configMap.Data["quarkus.mongodb.connection-string"]) > 0)
}

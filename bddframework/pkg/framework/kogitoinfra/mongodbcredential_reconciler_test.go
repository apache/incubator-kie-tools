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

func TestMongoDBCredentialReconciler(t *testing.T) {
	ns := t.Name()
	kogitoMongoDBInstance := test.CreateFakeKogitoMongoDB(ns)
	mongoDBInstance := test.CreateFakeMongoDB(ns)
	mongoDBSecret := test.CreateFakeMongoDBSecret(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(mongoDBSecret).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoMongoDBInstance,
	}
	mongoDBCredentialReconciler := newMongoDBCredentialReconciler(infraContext, mongoDBInstance, api.QuarkusRuntimeType)
	err := mongoDBCredentialReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 1, len(kogitoMongoDBInstance.GetStatus().GetSecretEnvFromReferences()))
	secretName := kogitoMongoDBInstance.GetStatus().GetSecretEnvFromReferences()[0]
	credentialSecret := &v1.Secret{
		ObjectMeta: v12.ObjectMeta{
			Name:      secretName,
			Namespace: ns,
		},
	}
	exist, err := kubernetes.ResourceC(cli).Fetch(credentialSecret)
	assert.True(t, exist)
	assert.NoError(t, err)
	assert.NotNil(t, credentialSecret.StringData["QUARKUS_MONGODB_CREDENTIALS_AUTH_SOURCE"])
	assert.NotNil(t, credentialSecret.StringData["QUARKUS_MONGODB_CREDENTIALS_USERNAME"])
	assert.NotNil(t, credentialSecret.StringData["QUARKUS_MONGODB_CREDENTIALS_PASSWORD"])
	assert.NotNil(t, credentialSecret.StringData["QUARKUS_MONGODB_DATABASE"])
}

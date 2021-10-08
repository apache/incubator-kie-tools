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
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/kiegroup/kogito-operator/version/app"
	"github.com/stretchr/testify/assert"
	v12 "k8s.io/api/core/v1"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"testing"
)

func TestTrustStoreReconciler(t *testing.T) {
	instance := test.CreateFakeKogitoRuntime(t.Name())
	instance.Spec.TrustStoreSecret = "truststore-secret"
	trustStoreSecret := &v12.Secret{
		ObjectMeta: v1.ObjectMeta{
			Name:      "truststore-secret",
			Namespace: t.Name(),
		},
		Data: map[string][]byte{
			trustStoreSecretPasswordKey: []byte("password"),
			trustStoreSecretFileKey:     []byte("cert"),
		},
	}

	cli := test.NewFakeClientBuilder().AddK8sObjects(instance, trustStoreSecret).Build()
	serviceDefinition := ServiceDefinition{}
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	trustStoreReconciler := newTrustStoreReconciler(context, instance, &serviceDefinition)
	err := trustStoreReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 2, len(serviceDefinition.Envs))
	assert.Equal(t, 1, len(serviceDefinition.SecretVolumeReferences))
}

func TestTrustStoreReconcilerTrustStore_MissingCM(t *testing.T) {
	instance := test.CreateFakeKogitoRuntime(t.Name())
	instance.Spec.TrustStoreSecret = "missingSecret"
	cli := test.NewFakeClientBuilder().AddK8sObjects(instance).Build()
	serviceDefinition := ServiceDefinition{}
	context := operator.Context{
		Client:  cli,
		Log:     test.TestLogger,
		Scheme:  meta.GetRegisteredSchema(),
		Version: app.Version,
	}
	trustStoreReconciler := newTrustStoreReconciler(context, instance, &serviceDefinition)
	err := trustStoreReconciler.Reconcile()
	assert.Error(t, err)
	errorHandler := infrastructure.NewReconciliationErrorHandler(context)
	assert.Equal(t, infrastructure.TrustStoreMountFailureReason, errorHandler.GetReasonForError(err))
}

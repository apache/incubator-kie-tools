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
	"testing"

	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

func Test_InfinispanInfraReconciler_WrongInfinispanName(t *testing.T) {
	ns := t.Name()
	kogitoInfinispanInstance := test.CreateFakeKogitoInfinispan(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoInfinispanInstance).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoInfinispanInstance,
	}
	infinispanInfraReconciler := initInfinispanInfraReconciler(infraContext)
	err := infinispanInfraReconciler.Reconcile()
	assert.Errorf(t, err, "%s resource(%s) not found in namespace %s", "Infinispan", kogitoInfinispanInstance.GetName(), kogitoInfinispanInstance.GetNamespace())
}

func Test_InfinispanInfraReconciler_InfinispanInstanceNotReady(t *testing.T) {
	ns := t.Name()
	kogitoInfinispanInstance := test.CreateFakeKogitoInfinispan(ns)
	infinispanInstance := test.CreateFakeInfinispan(ns)
	infinispanInstance.Status.Conditions[0].Status = v1.ConditionTrue
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoInfinispanInstance, infinispanInstance).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoInfinispanInstance,
	}
	infinispanInfraReconciler := initInfinispanInfraReconciler(infraContext)
	err := infinispanInfraReconciler.Reconcile()
	assert.Errorf(t, err, "infinispan instance %s not ready", infinispanInstance)
}

func Test_Reconcile_Infinispan(t *testing.T) {
	ns := t.Name()
	kogitoInfinispanInstance := test.CreateFakeKogitoInfinispan(ns)
	infinispanInstance := test.CreateFakeInfinispan(ns)
	infinispanService := test.CreateFakeInfinispanService(ns)
	infinispanCredential := test.CreateFakeInfinispanCredentialSecret(ns)
	infinispanCertSecret, err := test.CreateFakeInfinispanCertSecret(ns)
	assert.NoError(t, err)
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoInfinispanInstance, infinispanInstance, infinispanService, infinispanCredential, infinispanCertSecret).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoInfinispanInstance,
	}
	infinispanInfraReconciler := initInfinispanInfraReconciler(infraContext)
	err = infinispanInfraReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 2, len(kogitoInfinispanInstance.GetStatus().GetConfigMapEnvFromReferences()))
	assert.Equal(t, 4, len(kogitoInfinispanInstance.GetStatus().GetSecretEnvFromReferences()))
	assert.Equal(t, 1, len(kogitoInfinispanInstance.GetStatus().GetSecretVolumeReferences()))
}

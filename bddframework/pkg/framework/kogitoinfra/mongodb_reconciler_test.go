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

package kogitoinfra

import (
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/meta"
	v1 "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestMongoDBInfraReconciler(t *testing.T) {
	ns := t.Name()
	kogitoMongoDBInstance := test.CreateFakeKogitoMongoDB(ns)
	mongoDBInstance := test.CreateFakeMongoDB(ns)
	mongoDBSecret := test.CreateFakeMongoDBSecret(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoMongoDBInstance, mongoDBInstance, mongoDBSecret).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoMongoDBInstance,
	}
	mongoDBInfraReconciler := initMongoDBInfraReconciler(infraContext)
	err := mongoDBInfraReconciler.Reconcile()
	assert.NoError(t, err)
	assert.Equal(t, 2, len(kogitoMongoDBInstance.GetStatus().GetConfigMapEnvFromReferences()))
	assert.Equal(t, 2, len(kogitoMongoDBInstance.GetStatus().GetSecretEnvFromReferences()))
}

func TestMongoDBInfraReconciler_WrongMongoDBName(t *testing.T) {
	ns := t.Name()
	kogitoMongoDBInstance := test.CreateFakeKogitoMongoDB(ns)
	cli := test.NewFakeClientBuilder().AddK8sObjects().Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoMongoDBInstance,
	}
	mongoDBInfraReconciler := initMongoDBInfraReconciler(infraContext)
	err := mongoDBInfraReconciler.Reconcile()
	assert.Errorf(t, err, "MongoDB resource(kogito-mongodb) not found in namespace %s", ns)
}

func TestMongoDBInfraReconciler_MongoDBInstanceNotReady(t *testing.T) {
	ns := t.Name()
	kogitoMongoDBInstance := test.CreateFakeKogitoMongoDB(ns)
	mongoDBInstance := test.CreateFakeMongoDB(ns)
	mongoDBInstance.Status.Phase = v1.Pending
	cli := test.NewFakeClientBuilder().AddK8sObjects(kogitoMongoDBInstance, mongoDBInstance).Build()
	infraContext := infraContext{
		Context: operator.Context{
			Client: cli,
			Log:    test.TestLogger,
			Scheme: meta.GetRegisteredSchema(),
		},
		instance: kogitoMongoDBInstance,
	}
	mongoDBInfraReconciler := initMongoDBInfraReconciler(infraContext)
	err := mongoDBInfraReconciler.Reconcile()
	assert.Errorf(t, err, "mongoDB instance kogito-mongodb not ready. Waiting for Status.Phase == Running")
}

// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
	"testing"
)

func newReconcileRequest(namespace string) reconcile.Request {
	return reconcile.Request{NamespacedName: types.NamespacedName{Namespace: namespace}}
}

func Test_serviceDeployer_DataIndex_InfraNotReady(t *testing.T) {
	infraKafka := test.CreateFakeKogitoKafka(t.Name())
	infraInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	dataIndex := test.CreateFakeDataIndex(t.Name())
	dataIndex.GetSpec().AddInfra(infraKafka.GetName())
	dataIndex.GetSpec().AddInfra(infraInfinispan.GetName())

	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex).Build()
	definition := ServiceDefinition{
		DefaultImageName: "kogito-data-index-infinispan",
		Request:          newReconcileRequest(t.Name()),
		KafkaTopics:      []string{"mytopic"},
	}

	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	err := deployer.Deploy()
	assert.Error(t, err)

	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)

	// Infinispan is not ready :)
	infraCondition := &[]v1.Condition{
		{
			Message: "Headaches",
			Status:  v1.ConditionFalse,
			Reason:  string(api.ResourceNotReady),
			Type:    string(api.KogitoInfraConfigured),
		},
	}
	infraInfinispan.GetStatus().SetConditions(infraCondition)

	test.AssertCreate(t, cli, infraInfinispan)
	test.AssertCreate(t, cli, infraKafka)

	err = deployer.Deploy()
	assert.Error(t, err)
	errorHandler := infrastructure.NewReconciliationErrorHandler(context)
	assert.True(t, errorHandler.IsReconciliationError(err))
	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)
}

func Test_serviceDeployer_DataIndex_InfraNotReconciled(t *testing.T) {
	infraKafka := test.CreateFakeKogitoKafka(t.Name())
	infraInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	dataIndex := test.CreateFakeDataIndex(t.Name())
	dataIndex.GetSpec().AddInfra(infraKafka.GetName())
	dataIndex.GetSpec().AddInfra(infraInfinispan.GetName())

	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex).Build()
	definition := ServiceDefinition{
		DefaultImageName: "kogito-data-index-infinispan",
		Request:          newReconcileRequest(t.Name()),
		KafkaTopics:      []string{"mytopic"},
	}

	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	err := deployer.Deploy()
	assert.Error(t, err)
	errorHandler := infrastructure.NewReconciliationErrorHandler(context)
	assert.False(t, errorHandler.IsReconciliationError(err))

	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)

	// Infinispan is not reconciled yet, conditions are empty
	var infraCondition *[]v1.Condition
	infraInfinispan.GetStatus().SetConditions(infraCondition)

	test.AssertCreate(t, cli, infraInfinispan)
	test.AssertCreate(t, cli, infraKafka)

	err = deployer.Deploy()
	assert.Error(t, err)
	assert.True(t, errorHandler.IsReconciliationError(err))
	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)
}

func Test_serviceDeployer_DataIndex(t *testing.T) {
	requiredTopic := "dataindex-required-topic"
	kafka := test.CreateFakeKafka(t.Name())
	kafkaConfig := test.CreateFakeKogitoKafkaConfig(t.Name())
	infraKafka := test.CreateFakeKogitoKafka(t.Name())
	infraKafka.GetSpec().GetResource().SetName(kafka.Name)
	infraInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	dataIndex := test.CreateFakeDataIndex(t.Name())
	dataIndex.GetSpec().AddInfra(infraKafka.GetName())
	dataIndex.GetSpec().AddInfra(infraInfinispan.GetName())

	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex, infraKafka, infraInfinispan, kafka, kafkaConfig).Build()
	definition := ServiceDefinition{
		DefaultImageName: "kogito-data-index-infinispan",
		Request:          newReconcileRequest(t.Name()),
		KafkaTopics:      []string{requiredTopic},
	}
	context := operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	err := deployer.Deploy()
	assert.NoError(t, err)

	topic := &v1beta2.KafkaTopic{
		ObjectMeta: v1.ObjectMeta{
			Name:      requiredTopic,
			Namespace: t.Name(),
		},
	}
	test.AssertFetchMustExist(t, cli, topic)
}

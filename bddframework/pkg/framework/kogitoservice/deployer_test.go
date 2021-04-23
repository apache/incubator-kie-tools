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
	"testing"
	"time"

	"github.com/kiegroup/kogito-operator/api"
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	"github.com/kiegroup/kogito-operator/core/operator"
	"github.com/kiegroup/kogito-operator/core/test"
	"github.com/kiegroup/kogito-operator/internal"
	"github.com/kiegroup/kogito-operator/meta"
	appsv1 "k8s.io/api/apps/v1"

	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
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

	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	reconcileAfter, err := deployer.Deploy()
	assert.Error(t, err)
	assert.Equal(t, time.Duration(0), reconcileAfter)

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

	reconcileAfter, err = deployer.Deploy()
	assert.NoError(t, err)
	assert.Equal(t, reconcileAfter, reconciliationAfterOneMinute)
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

	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	reconcileAfter, err := deployer.Deploy()
	assert.Error(t, err)
	assert.Equal(t, time.Duration(0), reconcileAfter)

	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)

	// Infinispan is not reconciled yet, conditions are empty
	var infraCondition *[]v1.Condition
	infraInfinispan.GetStatus().SetConditions(infraCondition)

	test.AssertCreate(t, cli, infraInfinispan)
	test.AssertCreate(t, cli, infraKafka)

	reconcileAfter, err = deployer.Deploy()
	assert.NoError(t, err)
	assert.Equal(t, reconcileAfter, reconciliationAfterOneMinute)
	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, *dataIndex.GetStatus().GetConditions(), 3)
}

func Test_serviceDeployer_DataIndex(t *testing.T) {
	requiredTopic := "dataindex-required-topic"
	kafka := test.CreateFakeKafka("my-kafka", t.Name())
	infraKafka := test.CreateFakeKogitoKafka(t.Name())
	infraKafka.GetSpec().GetResource().SetName(kafka.Name)
	infraInfinispan := test.CreateFakeKogitoInfinispan(t.Name())
	dataIndex := test.CreateFakeDataIndex(t.Name())
	dataIndex.GetSpec().AddInfra(infraKafka.GetName())
	dataIndex.GetSpec().AddInfra(infraInfinispan.GetName())

	cli := test.NewFakeClientBuilder().AddK8sObjects(dataIndex, infraKafka, infraInfinispan, kafka).Build()
	definition := ServiceDefinition{
		DefaultImageName: "kogito-data-index-infinispan",
		Request:          newReconcileRequest(t.Name()),
		KafkaTopics:      []string{requiredTopic},
	}
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, dataIndex, infraHandler)
	reconcileAfter, err := deployer.Deploy()
	assert.NoError(t, err)
	assert.Equal(t, time.Duration(0), reconcileAfter)

	topic := &v1beta2.KafkaTopic{
		ObjectMeta: v1.ObjectMeta{
			Name:      requiredTopic,
			Namespace: t.Name(),
		},
	}
	test.AssertFetchMustExist(t, cli, topic)
}

func Test_serviceDeployer_Deploy(t *testing.T) {
	service := test.CreateFakeJobsService(t.Name())

	deployment := &appsv1.Deployment{
		ObjectMeta: v1.ObjectMeta{
			Name:      service.GetName(),
			Namespace: service.GetNamespace(),
		},
		Status: appsv1.DeploymentStatus{
			AvailableReplicas: singleReplica,
		},
	}
	cli := test.NewFakeClientBuilder().AddK8sObjects(service, deployment).OnOpenShift().Build()

	definition := ServiceDefinition{
		DefaultImageName: "kogito-jobs-service",
		Request:          newReconcileRequest(t.Name()),
	}
	context := &operator.Context{
		Client: cli,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	deployer := NewServiceDeployer(context, definition, service, infraHandler)
	requeueAfter, err := deployer.Deploy()
	assert.NoError(t, err)
	assert.True(t, requeueAfter == 0)

	exists, err := kubernetes.ResourceC(cli).Fetch(service)
	assert.NoError(t, err)
	assert.True(t, exists)
	conditions := *service.Status.Conditions
	assert.Equal(t, 2, len(conditions))
	provisioningCondition := getSpecificCondition(conditions, api.ProvisioningConditionType)
	assert.NotNil(t, provisioningCondition)
	assert.Equal(t, v1.ConditionFalse, provisioningCondition.Status)

	deployedCondition := getSpecificCondition(conditions, api.DeployedConditionType)
	assert.NotNil(t, deployedCondition)
	assert.Equal(t, v1.ConditionTrue, deployedCondition.Status)

	assert.Equal(t, int32(1), *service.GetSpec().GetReplicas())
}

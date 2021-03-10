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
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/internal"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	corev1 "k8s.io/api/core/v1"
	"testing"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	kafkav1beta1 "github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
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
	assert.Len(t, dataIndex.GetStatus().GetConditions(), 1)
	assert.Equal(t, dataIndex.GetStatus().GetConditions()[0].GetReason(), api.ServiceReconciliationFailure)

	// Infinispan is not ready :)
	infraInfinispan.GetStatus().GetCondition().SetMessage("Headaches")
	infraInfinispan.GetStatus().GetCondition().SetStatus(corev1.ConditionFalse)
	infraInfinispan.GetStatus().GetCondition().SetReason(api.ResourceNotReady)
	infraInfinispan.GetStatus().GetCondition().SetType(api.FailureInfraConditionType)

	test.AssertCreate(t, cli, infraInfinispan)
	test.AssertCreate(t, cli, infraKafka)

	reconcileAfter, err = deployer.Deploy()
	assert.NoError(t, err)
	assert.Equal(t, reconcileAfter, reconciliationIntervalAfterInfraError)
	test.AssertFetchMustExist(t, cli, dataIndex)
	assert.NotNil(t, dataIndex.GetStatus())
	assert.Len(t, dataIndex.GetStatus().GetConditions(), 2)
	for _, condition := range dataIndex.GetStatus().GetConditions() {
		assert.Equal(t, condition.GetType(), api.FailedConditionType)
		assert.Equal(t, condition.GetStatus(), corev1.ConditionFalse)
	}
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

	topic := &kafkav1beta1.KafkaTopic{
		ObjectMeta: v1.ObjectMeta{
			Name:      requiredTopic,
			Namespace: t.Name(),
		},
	}
	test.AssertFetchMustExist(t, cli, topic)
}

func Test_serviceDeployer_Deploy(t *testing.T) {
	service := test.CreateFakeJobsService(t.Name())
	cli := test.NewFakeClientBuilder().AddK8sObjects(service).OnOpenShift().Build()
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
	assert.Equal(t, 1, len(service.GetStatus().GetConditions()))
	assert.Equal(t, int32(1), *service.GetSpec().GetReplicas())
	assert.Equal(t, api.ProvisioningConditionType, service.GetStatus().GetConditions()[0].GetType())
}

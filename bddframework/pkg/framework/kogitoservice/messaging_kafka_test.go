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
	"github.com/kiegroup/kogito-cloud-operator/core/kogitoinfra"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	"github.com/kiegroup/kogito-cloud-operator/core/test"
	"github.com/kiegroup/kogito-cloud-operator/internal"
	"github.com/kiegroup/kogito-cloud-operator/meta"
	"testing"

	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	kafkav1beta1 "github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
	"github.com/stretchr/testify/assert"
	"k8s.io/apimachinery/pkg/types"
)

func Test_createKafkaTopics(t *testing.T) {

	appProps := map[string]string{}
	appProps[kogitoinfra.QuarkusKafkaBootstrapAppProp] = "kogito-kafka:9092"

	infraKafka := test.CreateFakeKogitoKafka(t.Name())
	kafka := test.CreateFakeKafka("my-kafka", t.Name())
	infraKafka.GetSpec().GetResource().SetName(kafka.GetName())
	service := test.CreateFakeDataIndex(t.Name())
	service.GetSpec().AddInfra(infraKafka.GetName())
	client := test.NewFakeClientBuilder().AddK8sObjects(infraKafka, service, kafka).Build()
	context := &operator.Context{
		Client: client,
		Log:    test.TestLogger,
		Scheme: meta.GetRegisteredSchema(),
	}
	infraHandler := internal.NewKogitoInfraHandler(context)
	k := kafkaMessagingDeployer{
		messagingDeployer{
			Context:      context,
			infraHandler: infraHandler,
			definition: ServiceDefinition{
				KafkaTopics: []string{
					"kogito-processinstances-events",
				},
			}}}
	err := k.CreateRequiredResources(service)
	assert.NoError(t, err)

	kafkaTopic := &kafkav1beta1.KafkaTopic{}
	exists, err := kubernetes.ResourceC(client).FetchWithKey(types.NamespacedName{Namespace: t.Name(), Name: "kogito-processinstances-events"}, kafkaTopic)
	assert.NoError(t, err)
	assert.True(t, exists)
}

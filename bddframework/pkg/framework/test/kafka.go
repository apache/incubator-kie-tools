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

package test

import (
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// CreateFakeKafka creates a Kafka resource with default configuration
func CreateFakeKafka(namespace string) *v1beta2.Kafka {
	return &v1beta2.Kafka{
		ObjectMeta: metav1.ObjectMeta{
			Name:      "kogito-kafka",
			Namespace: namespace,
		},
		Spec: v1beta2.KafkaSpec{
			EntityOperator: v1beta2.EntityOperatorSpec{
				TopicOperator: v1beta2.EntityTopicOperatorSpec{},
				UserOperator:  v1beta2.EntityUserOperatorSpec{},
			},
			Kafka: v1beta2.KafkaClusterSpec{
				Replicas: 1,
				Storage:  v1beta2.KafkaStorage{StorageType: v1beta2.KafkaEphemeralStorage},
				Listeners: []v1beta2.GenericKafkaListener{
					{
						Name:         "plain",
						Port:         9092,
						TLS:          false,
						ListenerType: "internal",
					},
					{
						Name:         "tls",
						Port:         9093,
						TLS:          true,
						ListenerType: "internal",
					},
				},
				JvmOptions: map[string]interface{}{"gcLoggingEnabled": false},
				Config: map[string]interface{}{
					"log.message.format.version":               "2.3",
					"offsets.topic.replication.factor":         1,
					"transaction.state.log.min.isr":            1,
					"transaction.state.log.replication.factor": 1,
					"auto.create.topics.enable":                true,
				},
			},
			Zookeeper: v1beta2.ZookeeperClusterSpec{
				Replicas: 1,
				Storage:  v1beta2.KafkaStorage{StorageType: v1beta2.KafkaEphemeralStorage},
			},
		},
		Status: v1beta2.KafkaStatus{
			Listeners: []v1beta2.ListenerStatus{
				{
					Type: "plain",
					Addresses: []v1beta2.ListenerAddress{
						{
							Host: "kafka-host",
							Port: int32(9092),
						},
					},
				},
			},
		},
	}
}

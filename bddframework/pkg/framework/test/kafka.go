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
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// CreateFakeKafka creates a Kafka resource with default configuration
func CreateFakeKafka(name, namespace string) *v1beta1.Kafka {
	return &v1beta1.Kafka{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: v1beta1.KafkaSpec{
			EntityOperator: v1beta1.EntityOperatorSpec{
				TopicOperator: v1beta1.EntityTopicOperatorSpec{},
				UserOperator:  v1beta1.EntityUserOperatorSpec{},
			},
			Kafka: v1beta1.KafkaClusterSpec{
				Replicas: 1,
				Storage:  v1beta1.KafkaStorage{StorageType: v1beta1.KafkaEphemeralStorage},
				Listeners: v1beta1.KafkaListeners{
					Plain: v1beta1.KafkaListenerPlain{},
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
			Zookeeper: v1beta1.ZookeeperClusterSpec{
				Replicas: 1,
				Storage:  v1beta1.KafkaStorage{StorageType: v1beta1.KafkaEphemeralStorage},
			},
		},
	}
}

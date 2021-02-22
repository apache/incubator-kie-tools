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

package framework

import (
	"fmt"

	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"

	kafkabetav1 "github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// DeployKafkaInstance deploys an instance of Kafka
func DeployKafkaInstance(namespace string, kafka *kafkabetav1.Kafka) error {
	GetLogger(namespace).Info("Creating Kafka instance %s.", "name", kafka.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(kafka); err != nil {
		return fmt.Errorf("Error while creating Kafka: %v ", err)
	}

	return nil
}

// DeployKafkaTopic deploys a Kafka topic
func DeployKafkaTopic(namespace, kafkaTopicName, kafkaInstanceName string) error {
	GetLogger(namespace).Info("Creating Kafka", "topic", kafkaTopicName, "instanceName", kafkaInstanceName)

	kafkaTopic := &kafkabetav1.KafkaTopic{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      kafkaTopicName,
			Labels:    map[string]string{"strimzi.io/cluster": kafkaInstanceName},
		},
		Spec: kafkabetav1.KafkaTopicSpec{
			Replicas:   1,
			Partitions: 1,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Create(kafkaTopic); err != nil {
		return fmt.Errorf("Error while creating Kafka Topic: %v ", err)
	}

	return nil
}

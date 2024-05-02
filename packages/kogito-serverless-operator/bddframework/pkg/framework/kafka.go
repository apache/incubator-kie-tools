/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package framework

import (
	"encoding/json"
	"fmt"
	"strings"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	infrastructure "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/kafka/v1beta2"

	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

// DeployKafkaInstance deploys an instance of Kafka
func DeployKafkaInstance(namespace string, kafka *v1beta2.Kafka) error {
	GetLogger(namespace).Info("Creating Kafka instance %s.", "name", kafka.Name)

	if err := kubernetes.ResourceC(kubeClient).Create(kafka); err != nil {
		return fmt.Errorf("Error while creating Kafka: %v ", err)
	}

	return nil
}

// DeployKafkaTopic deploys a Kafka topic
func DeployKafkaTopic(namespace, kafkaTopicName, kafkaInstanceName string) error {
	GetLogger(namespace).Info("Creating Kafka", "topic", kafkaTopicName, "instanceName", kafkaInstanceName)

	kafkaTopic := &v1beta2.KafkaTopic{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: namespace,
			Name:      kafkaTopicName,
			Labels:    map[string]string{"strimzi.io/cluster": kafkaInstanceName},
		},
		Spec: v1beta2.KafkaTopicSpec{
			Replicas:   1,
			Partitions: 1,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Create(kafkaTopic); err != nil {
		return fmt.Errorf("Error while creating Kafka Topic: %v ", err)
	}

	return nil
}

// ScaleKafkaInstanceDown scales a Kafka instance down by killing its pod temporarily
func ScaleKafkaInstanceDown(namespace, kafkaInstanceName string) error {
	GetLogger(namespace).Info("Scaling Kafka Instance down", "instance name", kafkaInstanceName)
	pods, err := GetKafkaPods(namespace, kafkaInstanceName)
	if err != nil {
		return err
	} else if len(pods.Items) != 1 {
		return fmt.Errorf("Kafka instance should have just one kafka pod running")
	}
	if err = DeleteObject(&pods.Items[0]); err != nil {
		return fmt.Errorf("Error scaling Kafka instance down by deleting a kafka pod. The nested error is: %v", err)
	}

	return nil
}

// GetKafkaPods return the Kafka pods (suffixed with `-kafka`)
func GetKafkaPods(namespace, kafkaInstanceName string) (*v1.PodList, error) {
	return GetPodsWithLabels(namespace, map[string]string{"strimzi.io/name": kafkaInstanceName + "-kafka"})
}

// WaitForMessagesOnTopic waits for at least a certain number of messages are present on the given topic
func WaitForMessagesOnTopic(namespace, kafkaInstanceName, topic string, numberOfMsg int, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%d message(s) available on topic %s withing %d minutes", numberOfMsg, topic, timeoutInMin), timeoutInMin,
		func() (bool, error) {
			messages, err := GetMessagesOnTopic(namespace, kafkaInstanceName, topic)
			if err != nil {
				return false, err
			}
			GetLogger(namespace).Info(fmt.Sprintf("Got %d messages", len(messages)))
			for _, msg := range messages {
				GetLogger(namespace).Debug(fmt.Sprintf("Got message: %s", msg))
			}
			return len(messages) >= numberOfMsg, nil
		})
}

// GetMessagesOnTopic gets all messages for a topic
func GetMessagesOnTopic(namespace, kafkaInstanceName, topic string) ([]string, error) {
	kafkaInstance, err := GetKafkaInstance(namespace, kafkaInstanceName)
	if err != nil {
		return nil, err
	}
	GetLogger(namespace).Debug("Got kafka instance", "instance", kafkaInstance.Name)
	bootstrapServer := infrastructure.ResolveKafkaServerURI(kafkaInstance)
	if len(bootstrapServer) <= 0 {
		GetLogger(namespace).Debug("Not able resolve URI for given kafka instance")
		return nil, fmt.Errorf("not able resolve URI for given kafka instance %s", kafkaInstance.Name)
	}
	GetLogger(namespace).Debug("Got bootstrapServer", "server", bootstrapServer)

	var kafkaPods *v1.PodList
	kafkaPods, err = GetKafkaPods(namespace, kafkaInstanceName)
	if err != nil {
		return nil, fmt.Errorf("Error while retrieving Kafka pods: %v", err)
	} else if len(kafkaPods.Items) <= 0 {
		return nil, fmt.Errorf("No pods found for Kafka instance")
	}
	args := []string{"exec", kafkaPods.Items[0].Name}
	args = append(args, "-n", namespace)
	args = append(args, "--")
	args = append(args, "bin/kafka-console-consumer.sh")
	args = append(args, "--bootstrap-server", bootstrapServer)
	args = append(args, "--topic", topic)
	args = append(args, "--from-beginning")
	args = append(args, "--timeout-ms", "10000")

	var output string
	output, err = CreateCommand("kubectl", args...).WithLoggerContext(namespace).Execute()
	if err != nil {
		return nil, err
	}
	GetLogger(namespace).Debug("Got output", "output", output)
	lines := strings.Split(output, "\n")

	var messages []string
	var result map[string]interface{}
	for _, line := range lines {
		err = json.Unmarshal([]byte(line), &result)
		if err == nil {
			messages = append(messages, line)
		}
	}

	return messages, nil
}

// GetKafkaInstance retrieves the Kafka instance
func GetKafkaInstance(namespace, kafkaInstanceName string) (*v1beta2.Kafka, error) {
	key := types.NamespacedName{
		Namespace: namespace,
		Name:      kafkaInstanceName,
	}
	kafkaInstance := &v1beta2.Kafka{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(key, kafkaInstance); err != nil {
		GetLogger(namespace).Error(err, "Error occurs while fetching kogito kafka instance")
		return nil, err
	} else if !exists {
		GetLogger(namespace).Error(err, "kafka instance does not exist")
		return nil, nil
	} else {
		GetLogger(namespace).Debug("kafka instance found")
		return kafkaInstance, nil
	}
}

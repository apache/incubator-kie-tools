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

package steps

import (
	"github.com/cucumber/godog"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/kafka/v1beta2"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

func registerKafkaSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Kafka Operator is deployed$`, data.kafkaOperatorIsDeployed)
	ctx.Step(`^Kafka instance "([^"]*)" has (\d+) (?:pod|pods) running within (\d+) (?:minute|minutes)$`, data.kafkaInstanceHasPodsRunningWithinMinutes)
	ctx.Step(`^Kafka instance "([^"]*)" has (\d+) kafka (?:pod|pods) running within (\d+) (?:minute|minutes)$`, data.kafkaInstanceHasKafkaPodsRunningWithinMinutes)
	ctx.Step(`^Kafka instance "([^"]*)" is deployed$`, data.kafkaInstanceIsDeployed)
	ctx.Step(`^Scale Kafka instance "([^"]*)" down`, data.scaleKafkaInstanceDown)
	ctx.Step(`^Kafka topic "([^"]*)" is deployed$`, data.kafkaTopicIsDeployed)
	ctx.Step(`^Kafka instance "([^"]*)" should contain at least (\d+) (?:message|messages) on topic "([^"]*)" within (\d+) (?:minute|minutes)$`, data.kafkaInstanceShouldContainAtLeastMessagesOnTopicWithinMinutes)
}

func (data *Data) kafkaOperatorIsDeployed() error {
	if config.UseProductOperator() {
		return installers.GetAmqStreamsInstaller().Install(data.Namespace)
	}
	return installers.GetKafkaInstaller().Install(data.Namespace)
}

func (data *Data) kafkaInstanceHasPodsRunningWithinMinutes(name string, numberOfPods, timeOutInMin int) error {
	return framework.WaitForPodsWithLabel(data.Namespace, "strimzi.io/name", name+"-entity-operator", numberOfPods, timeOutInMin)
}

func (data *Data) kafkaInstanceHasKafkaPodsRunningWithinMinutes(name string, numberOfPods, timeOutInMin int) error {
	return framework.WaitForPodsWithLabel(data.Namespace, "strimzi.io/name", name+"-kafka", numberOfPods, timeOutInMin)
}

func (data *Data) kafkaInstanceIsDeployed(name string) error {
	kafka := getKafkaDefaultResource(name, data.Namespace)

	if err := framework.DeployKafkaInstance(data.Namespace, kafka); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, "strimzi.io/name", name+"-entity-operator", 1, 5)
}

func (data *Data) scaleKafkaInstanceDown(name string) error {
	return framework.ScaleKafkaInstanceDown(data.Namespace, name)
}

func (data *Data) kafkaTopicIsDeployed(name string) error {
	return framework.DeployKafkaTopic(data.Namespace, name, infrastructure.KafkaInstanceName)
}

func (data *Data) kafkaInstanceShouldContainAtLeastMessagesOnTopicWithinMinutes(instanceName string, numberOfMsg int, topic string, timeoutInMinutes int) error {
	return framework.WaitForMessagesOnTopic(data.Namespace, instanceName, topic, numberOfMsg, timeoutInMinutes)
}

func getKafkaDefaultResource(name, namespace string) *v1beta2.Kafka {
	return &v1beta2.Kafka{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
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
	}
}

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
	"fmt"
	"github.com/kiegroup/kogito-cloud-operator/api"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure"
	infra2 "github.com/kiegroup/kogito-cloud-operator/core/kogitoinfra"
	"k8s.io/apimachinery/pkg/types"
)

// kafkaMessagingDeployer implementation of messagingHandler
type kafkaMessagingDeployer struct {
	messagingDeployer
}

func (k *kafkaMessagingDeployer) CreateRequiredResources(service api.KogitoService) error {
	infra, err := k.fetchInfraDependency(service, IsKafkaResource)
	if err != nil || infra == nil {
		return err
	}
	if err := k.createRequiredKafkaTopics(infra, service); err != nil {
		return err
	}
	return nil
}

func (k *kafkaMessagingDeployer) createRequiredKafkaTopics(infra api.KogitoInfraInterface, service api.KogitoService) error {
	k.Log.Debug("Going to apply kafka topic configurations required by the deployed service")
	kafkaURI := infra.GetStatus().GetRuntimeProperties()[api.QuarkusRuntimeType].GetAppProps()[infra2.QuarkusKafkaBootstrapAppProp]
	if len(kafkaURI) == 0 {
		k.Log.Debug("Ignoring Kafka Topics creation, Kafka URI is empty from the given KogitoInfra", "KogitoInfra", infra.GetName())
		return nil
	}
	// topics required by definition
	for _, kafkaTopic := range k.definition.KafkaTopics {
		err := k.createKafkaTopicIfNotExists(kafkaTopic, infra)
		if err != nil {
			return err
		}
	}
	// topics required by the deployed service
	topics, err := k.fetchTopicsAndSetCloudEventsStatus(service)
	if err != nil {
		return err
	}
	for _, topic := range topics {
		if err := k.createKafkaTopicIfNotExists(topic.Name, infra); err != nil {
			return err
		}
	}
	return nil
}

func (k *kafkaMessagingDeployer) createKafkaTopicIfNotExists(topicName string, instance api.KogitoInfraInterface) error {
	k.Log.Debug("Going to create kafka topic it is not exists", "topicName", topicName)

	kafkaNamespaceName, err := k.getKafkaInstanceNamespaceName(instance)
	if err != nil {
		return err
	}
	k.Log.Debug("Resolved kafka instance", "name", kafkaNamespaceName.Name, "namespace", kafkaNamespaceName.Namespace)

	kafkaHandler := infrastructure.NewKafkaHandler(k.Context)
	kafkaTopic, err := kafkaHandler.FetchKafkaTopic(types.NamespacedName{Name: topicName, Namespace: kafkaNamespaceName.Namespace})
	if err != nil {
		return err
	}

	if kafkaTopic == nil {
		_, err := kafkaHandler.CreateKafkaTopic(topicName, kafkaNamespaceName.Name, kafkaNamespaceName.Namespace)
		if err != nil {
			return err
		}
	}
	return nil
}

func (k *kafkaMessagingDeployer) getKafkaInstanceNamespaceName(instance api.KogitoInfraInterface) (*types.NamespacedName, error) {
	if len(instance.GetSpec().GetResource().GetName()) > 0 {
		k.Log.Debug("Custom kafka instance reference is provided")
		namespace := instance.GetSpec().GetResource().GetNamespace()
		if len(namespace) == 0 {
			namespace = instance.GetNamespace()
			k.Log.Debug("Namespace is not provided for custom resource, taking instance namespace as default", "instance namespace", namespace)
		}
		return &types.NamespacedName{Namespace: namespace, Name: instance.GetSpec().GetResource().GetName()}, nil
	}
	k.Log.Debug("Custom kafka instance reference is not provided")
	return nil, fmt.Errorf("no Kafka instances found on KogitoInfra reference: %s", instance.GetName())
}

// IsKafkaResource checks if provided KogitoInfra instance is for kafka resource
func IsKafkaResource(instance api.KogitoInfraInterface) bool {
	return infrastructure.IsKafkaResource(instance.GetSpec().GetResource().GetAPIVersion(), instance.GetSpec().GetResource().GetKind())
}

// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package infrastructure

import (
	"fmt"
	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/core/infrastructure/kafka/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

const (
	strimziServerGroup = "kafka.strimzi.io"

	strimziBrokerLabel         = "strimzi.io/cluster"
	defaultKafkaTopicPartition = 1
	defaultKafkaTopicReplicas  = 1

	// KafkaKind refers to Kafka Kind as defined by Strimzi
	KafkaKind = "Kafka"

	// KafkaInstanceName is the default name for the Kafka cluster managed by KogitoInfra
	KafkaInstanceName = "kogito-kafka"
)

var (
	// KafkaAPIVersion refers to kafka APIVersion
	KafkaAPIVersion = v1beta1.SchemeGroupVersion.String()
)

// KafkaHandler ...
type KafkaHandler interface {
	IsStrimziAvailable() bool
	FetchKafkaInstance(key types.NamespacedName) (*v1beta1.Kafka, error)
	FetchKafkaTopic(key types.NamespacedName) (*v1beta1.KafkaTopic, error)
	CreateKafkaTopic(topicName, kafkaName, kafkaNamespace string) (*v1beta1.KafkaTopic, error)
	ResolveKafkaServerURI(kafka *v1beta1.Kafka) (string, error)
}

type kafkaHandler struct {
	*operator.Context
}

// NewKafkaHandler ...
func NewKafkaHandler(context *operator.Context) KafkaHandler {
	return &kafkaHandler{
		context,
	}
}

// IsStrimziAvailable checks if Strimzi CRD is available in the cluster
func (k *kafkaHandler) IsStrimziAvailable() bool {
	return k.Client.HasServerGroup(strimziServerGroup)
}

func (k *kafkaHandler) FetchKafkaInstance(key types.NamespacedName) (*v1beta1.Kafka, error) {
	k.Log.Debug("fetching deployed kafka instance")
	kafkaInstance := &v1beta1.Kafka{}
	if exists, err := kubernetes.ResourceC(k.Client).FetchWithKey(key, kafkaInstance); err != nil {
		k.Log.Error(err, "Error occurs while fetching kogito kafka instance")
		return nil, err
	} else if !exists {
		k.Log.Debug("kafka instance does not exist")
		return nil, nil
	} else {
		k.Log.Debug("kafka instance found")
		return kafkaInstance, nil
	}
}

func (k *kafkaHandler) FetchKafkaTopic(key types.NamespacedName) (*v1beta1.KafkaTopic, error) {
	k.Log.Debug("Going to load deployed kafka topic", "topicName", key.Name)
	kafkaTopic := &v1beta1.KafkaTopic{}
	if exits, err := kubernetes.ResourceC(k.Client).FetchWithKey(key, kafkaTopic); err != nil {
		k.Log.Error(err, "Error occurs while fetching kogito kafka topic", "topicName", key.Name)
		return nil, err
	} else if exits {
		k.Log.Debug("kafka topic found", "topicName", key.Name)
		return kafkaTopic, nil
	}
	k.Log.Debug("kafka topic not exists", "topicName", key.Name)
	return nil, nil
}

func (k *kafkaHandler) CreateKafkaTopic(topicName, kafkaName, kafkaNamespace string) (*v1beta1.KafkaTopic, error) {
	k.Log.Debug("Going to create kafka topic", "topicName", topicName)
	kafkaTopic := getKafkaTopic(topicName, kafkaNamespace, kafkaName)
	if err := kubernetes.ResourceC(k.Client).Create(kafkaTopic); err != nil {
		k.Log.Error(err, "Error occurs while creating kogito Kafka topic")
		return nil, err
	}
	k.Log.Debug("Kogito Kafka topic created successfully", "topicName", topicName)
	return kafkaTopic, nil
}

// getKafkaTopic returns a Kafka topic resource with default configuration
func getKafkaTopic(name, namespace, kafkaBroker string) *v1beta1.KafkaTopic {

	labels := make(map[string]string)
	labels[strimziBrokerLabel] = kafkaBroker

	return &v1beta1.KafkaTopic{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
			Labels:    labels,
		},
		Spec: v1beta1.KafkaTopicSpec{
			Partitions: defaultKafkaTopicPartition,
			Replicas:   defaultKafkaTopicReplicas,
			TopicName:  name,
		},
	}
}

// ResolveKafkaServerURI returns the uri of the kafka instance
func (k *kafkaHandler) ResolveKafkaServerURI(kafka *v1beta1.Kafka) (string, error) {
	k.Log.Debug("Resolving kafka URI", "kafka instance", kafka.Name)
	if len(kafka.Status.Listeners) > 0 {
		for _, listenerStatus := range kafka.Status.Listeners {
			if listenerStatus.Type == "plain" && len(listenerStatus.Addresses) > 0 {
				for _, listenerAddress := range listenerStatus.Addresses {
					if len(listenerAddress.Host) > 0 && listenerAddress.Port > 0 {
						kafkaURI := fmt.Sprintf("%s:%d", listenerAddress.Host, listenerAddress.Port)
						k.Log.Debug("Success fetch Kafka URI", "kafka instance", kafka.Name, "kafka URI", kafkaURI)
						return kafkaURI, nil
					}
				}
			}
		}
	}
	k.Log.Debug("Not able resolve URI for given kafka instance")
	return "", fmt.Errorf("not able resolve URI for given kafka instance %s", kafka.Name)
}

// IsKafkaResource checks if provided KogitoInfra instance is for kafka resource
func IsKafkaResource(apiVersion, kind string) bool {
	return apiVersion == KafkaAPIVersion && kind == KafkaKind
}

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

package installers

import (
	"github.com/kiegroup/kogito-operator/core/infrastructure/kafka/v1beta2"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

var (
	// kafkaOlmClusterWideInstaller installs Kafka cluster wide using OLM
	kafkaOlmClusterWideInstaller = OlmClusterWideServiceInstaller{
		SubscriptionName:                   kafkaOperatorSubscriptionName,
		Channel:                            kafkaOperatorSubscriptionChannel,
		Catalog:                            framework.GetCommunityCatalog,
		InstallationTimeoutInMinutes:       kafkaOperatorTimeoutInMin,
		GetAllClusterWideOlmCrsInNamespace: getKafkaCrsInNamespace,
	}

	kafkaOperatorSubscriptionName    = "strimzi-kafka-operator"
	kafkaOperatorSubscriptionChannel = "stable"
	kafkaOperatorTimeoutInMin        = 10
)

// GetKafkaInstaller returns Kafka installer
func GetKafkaInstaller() ServiceInstaller {
	return &kafkaOlmClusterWideInstaller
}

func getKafkaCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	kafkas := &v1beta2.KafkaList{}
	if err := framework.GetObjectsInNamespace(namespace, kafkas); err != nil {
		return nil, err
	}
	for i := range kafkas.Items {
		crs = append(crs, &kafkas.Items[i])
	}

	kafkaTopics := &v1beta2.KafkaTopicList{}
	if err := framework.GetObjectsInNamespace(namespace, kafkaTopics); err != nil {
		return nil, err
	}
	for i := range kafkaTopics.Items {
		crs = append(crs, &kafkaTopics.Items[i])
	}

	return crs, nil
}

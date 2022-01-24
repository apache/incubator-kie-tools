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
	// amqStreamsOlmClusterWideInstaller installs AmqStreams cluster wide using OLM
	amqStreamsOlmClusterWideInstaller = OlmClusterWideServiceInstaller{
		SubscriptionName:                   amqStreamsOperatorSubscriptionName,
		Channel:                            amqStreamsOperatorSubscriptionChannel,
		StartingCSV:                        amqStreamsOperatorSubscriptionStartingCsv,
		Catalog:                            framework.GetProductCatalog,
		InstallationTimeoutInMinutes:       amqStreamsOperatorTimeoutInMin,
		GetAllClusterWideOlmCrsInNamespace: getAmqStreamsCrsInNamespace,
	}

	amqStreamsOperatorSubscriptionName        = "amq-streams"
	amqStreamsOperatorSubscriptionChannel     = "amq-streams-1.8.x"
	amqStreamsOperatorSubscriptionStartingCsv = "amqstreams.v1.8.3"
	amqStreamsOperatorTimeoutInMin            = 10
)

// GetAmqStreamsInstaller returns AmqStreams installer
func GetAmqStreamsInstaller() ServiceInstaller {
	return &amqStreamsOlmClusterWideInstaller
}

func getAmqStreamsCrsInNamespace(namespace string) ([]client.Object, error) {
	crs := []client.Object{}

	amqStreams := &v1beta2.KafkaList{}
	if err := framework.GetObjectsInNamespace(namespace, amqStreams); err != nil {
		return nil, err
	}
	for i := range amqStreams.Items {
		crs = append(crs, &amqStreams.Items[i])
	}

	amqStreamsTopics := &v1beta2.KafkaTopicList{}
	if err := framework.GetObjectsInNamespace(namespace, amqStreamsTopics); err != nil {
		return nil, err
	}
	for i := range amqStreamsTopics.Items {
		crs = append(crs, &amqStreamsTopics.Items[i])
	}

	return crs, nil
}

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

package steps

import (
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

const defaultReplicas = 1

func registerKafkaSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Kafka instance "([^"]*)" is deployed$`, data.kafkaInstanceIsDeployed)
}

func (data *Data) kafkaInstanceIsDeployed(name string) error {
	kafka := infrastructure.GetKafkaDefaultResource(name, data.Namespace, defaultReplicas)

	if err := framework.DeployKafkaInstance(data.Namespace, kafka); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, "strimzi.io/name", name+"-entity-operator", 1, 5)
}

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

package steps

import (
	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
)

/*
	DataTable for Kogito Infra:
	| config          | <key>       | <value>      |
*/

func registerKogitoInfraSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install (Infinispan|MongoDB|Kafka|Keycloak) Kogito Infra "([^"]*)" within (\d+) (?:minute|minutes)$`, data.installKogitoInfraWithinMinutes)
	ctx.Step(`^Install (Infinispan|MongoDB|Kafka|Keycloak) Kogito Infra "([^"]*)" within (\d+) (?:minute|minutes) with configuration:$`, data.installKogitoInfraWithinMinutesWithConfiguration)
}

func (data *Data) installKogitoInfraWithinMinutes(targetResourceType, name string, timeoutInMin int) error {
	return data.installKogitoInfraWithinMinutesWithConfiguration(targetResourceType, name, timeoutInMin, &messages.PickleStepArgument_PickleTable{})
}

func (data *Data) installKogitoInfraWithinMinutesWithConfiguration(targetResourceType, name string, timeoutInMin int, table *godog.Table) error {
	infraResource, err := framework.GetKogitoInfraResourceStub(data.Namespace, name, targetResourceType)
	if err != nil {
		return err
	}

	if err := mappers.MapKogitoInfraTable(table, infraResource); err != nil {
		return err
	}

	framework.GetLogger(data.Namespace).Debug("Got kogitoInfra config", "config", infraResource.Spec.InfraProperties)
	err = framework.InstallKogitoInfraComponent(data.Namespace, framework.GetDefaultInstallerType(), infraResource)
	if err != nil {
		return err
	}

	return framework.WaitForKogitoInfraResource(data.Namespace, name, timeoutInMin)
}

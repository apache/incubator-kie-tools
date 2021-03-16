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
	"fmt"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-operator/test/framework"
	"github.com/kiegroup/kogito-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-operator/test/types"
)

/*
	DataTable for Explainability:
	| config          | infra       | <KogitoInfra name>        |
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| runtime-env     | varName     | varValue                  |
*/

func registerKogitoExplainabilityServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Explainability with (\d+) replicas$`, data.installKogitoExplainabilityServiceWithReplicas)
	ctx.Step(`^Install Kogito Explainability with (\d+) replicas with configuration:$`, data.installKogitoExplainabilityServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Explainability has (\d+) pods running within (\d+) minutes$`, data.kogitoExplainabilityHasPodsRunningWithinMinutes)
	ctx.Step(`^Explainability result for execution "([^"]*)" in the Trusty service within (\d+) minutes with saliences:$`, data.explainabilityResultForExecutionWithinMinutesWithSaliences)
}

func (data *Data) installKogitoExplainabilityServiceWithReplicas(replicas int) error {
	explainability := framework.GetKogitoExplainabilityResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoExplainabilityService(data.Namespace, framework.GetDefaultInstallerType(), &bddtypes.KogitoServiceHolder{KogitoService: explainability})
}

func (data *Data) installKogitoExplainabilityServiceWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	explainability := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoExplainabilityResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, explainability); err != nil {
		return err
	}

	return framework.InstallKogitoExplainabilityService(data.Namespace, framework.GetDefaultInstallerType(), explainability)
}

func (data *Data) kogitoExplainabilityHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoExplainabilityService(data.Namespace, podNb, timeoutInMin)
}

func (data *Data) explainabilityResultForExecutionWithinMinutesWithSaliences(executionName string, timeoutInMin int, table *godog.Table) error {
	resp, err := framework.GetKogitoTrustyDecisionsByExecutionName(data.Namespace, executionName, timeoutInMin)
	if err != nil {
		return err
	}

	if len(table.Rows) > 0 {
		for _, row := range table.Rows {
			outcomeNameExpected := row.Cells[0].Value
			found := false
			for _, salience := range resp.Saliences {
				if salience.OutcomeName == outcomeNameExpected {
					found = true
					break
				}
			}

			if !found {
				return fmt.Errorf("Outcome %s for execution %s was not found", outcomeNameExpected, executionName)
			}
		}
	}

	return nil
}

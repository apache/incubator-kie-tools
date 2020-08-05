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
	"fmt"
	"strings"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerOperatorSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Kogito operator should be installed with dependencies$`, data.kogitoOperatorShouldBeInstalledWithDependencies)
	ctx.Step(`^Kogito Operator is deployed$`, data.kogitoOperatorIsDeployed)
	ctx.Step(`^Kogito Operator is deployed with ((?:Infinispan|Kafka|Keycloak|, | and )+) (?:operator|operators)$`, data.kogitoOperatorIsDeployedWithDependencies)

	ctx.Step(`^CLI install Kogito operator$`, data.cliInstallKogitoOperator)
}

func (data *Data) kogitoOperatorShouldBeInstalledWithDependencies() error {
	return framework.WaitForKogitoOperatorRunningWithDependencies(data.Namespace)
}

func (data *Data) kogitoOperatorIsDeployed() error {
	// if operator not available, then install via yaml files
	if exists, err := framework.IsKogitoOperatorRunning(data.Namespace); err != nil {
		return fmt.Errorf("Error while trying to retrieve the operator: %v ", err)
	} else if !exists {
		if err := framework.DeployKogitoOperatorFromYaml(data.Namespace); err != nil {
			return fmt.Errorf("Error while deploying operator: %v", err)
		}

		if err := framework.WaitForKogitoOperatorRunning(data.Namespace); err != nil {
			return fmt.Errorf("Error while checking operator running: %v", err)
		}
	}

	return nil
}

func (data *Data) kogitoOperatorIsDeployedWithDependencies(dependencies string) error {
	// First install and wait for kogito operator
	if exists, err := framework.IsKogitoOperatorRunning(data.Namespace); err != nil {
		return fmt.Errorf("Error while trying to retrieve the operator: %v ", err)
	} else if !exists {
		if err := framework.DeployKogitoOperatorFromYaml(data.Namespace); err != nil {
			return fmt.Errorf("Error while deploying operator: %v", err)
		}
	}

	// Dependent operators
	operatorSource := framework.CommunityCatalog
	if !framework.IsOpenshift() {
		operatorSource = framework.OperatorHubCatalog
	}

	// Install and wait for operator dependencies
	// Do it one by one due to racing condition in OLM (https://github.com/operator-framework/operator-lifecycle-manager/issues/1704)
	for _, dependentOperator := range framework.KogitoOperatorDependencies {
		if strings.Contains(dependencies, dependentOperator) {
			if err := framework.InstallKogitoOperatorDependency(data.Namespace, dependentOperator, operatorSource); err != nil {
				return err
			}
			if err := framework.WaitForKogitoOperatorDependencyRunning(data.Namespace, dependentOperator, operatorSource); err != nil {
				return err
			}
		}
	}

	// Wait for Kogito operator running
	// This is put at the end because there should not be any racing condition as install is done via yaml and not OLM
	if err := framework.WaitForKogitoOperatorRunning(data.Namespace); err != nil {
		return fmt.Errorf("Error while checking operator running: %v", err)
	}

	return nil
}

func (data *Data) cliInstallKogitoOperator() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "operator")
	return err
}

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

	"github.com/DATA-DOG/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
)

// RegisterCliSteps register all CLI steps existing
func registerOperatorSteps(s *godog.Suite, data *Data) {
	s.Step(`^Kogito operator should be installed with dependencies$`, data.kogitoOperatorShouldBeInstalledWithDependencies)
	s.Step(`^Kogito Operator is deployed$`, data.kogitoOperatorIsDeployed)
	s.Step(`^Kogito Operator is deployed with dependencies$`, data.kogitoOperatorIsDeployedWithDependencies)
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

func (data *Data) kogitoOperatorIsDeployedWithDependencies() error {
	if exists, err := framework.IsKogitoOperatorRunning(data.Namespace); err != nil {
		return fmt.Errorf("Error while trying to retrieve the operator: %v ", err)
	} else if !exists {
		if err := framework.DeployKogitoOperatorFromYaml(data.Namespace); err != nil {
			return fmt.Errorf("Error while deploying operator: %v", err)
		}
	}

	// Install operator dependencies
	if err := framework.InstallCommunityKogitoOperatorDependencies(data.Namespace); err != nil {
		return err
	}

	return framework.WaitForKogitoOperatorRunningWithDependencies(data.Namespace)
}

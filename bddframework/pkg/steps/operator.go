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
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"github.com/kiegroup/kogito-operator/test/pkg/installers"
)

func registerOperatorSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Kogito operator should be installed$`, data.kogitoOperatorShouldBeInstalled)
	ctx.Step(`^Kogito Operator is deployed$`, data.kogitoOperatorIsDeployed)

	ctx.Step(`^CLI install Kogito operator$`, data.cliInstallKogitoOperator)
}

func (data *Data) kogitoOperatorShouldBeInstalled() error {
	return framework.WaitForKogitoOperatorRunning(data.Namespace)
}

func (data *Data) kogitoOperatorIsDeployed() error {
	installer, err := installers.GetKogitoInstaller()
	if err != nil {
		return err
	}
	if err := installer.Install(data.Namespace); err != nil {
		return err
	}
	return nil
}

func (data *Data) cliInstallKogitoOperator() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "operator")
	return err
}

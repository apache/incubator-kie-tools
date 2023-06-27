// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package steps

import (
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-serverless-operator/testbdd/installers"

	"github.com/kiegroup/kogito-operator/test/pkg/config"
	kogitoInstallers "github.com/kiegroup/kogito-operator/test/pkg/installers"
)

func registerOperatorSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^SonataFlow Operator is deployed$`, data.sonataFlowOperatorIsDeployed)
	// Unused currently
	//ctx.Step(`^SonataFlow Operator has (\d+) (?:pod|pods) running"$`, data.sonataFlowOperatorHasPodsRunning)
	// Not migrated yet
	//ctx.Step(`^Kogito operator should be installed$`, data.kogitoOperatorShouldBeInstalled)
	//ctx.Step(`^CLI install Kogito operator$`, data.cliInstallKogitoOperator)
}

func (data *Data) sonataFlowOperatorIsDeployed() (err error) {
	var installer kogitoInstallers.ServiceInstaller
	if config.UseProductOperator() {
		installer, err = kogitoInstallers.GetRhpamKogitoInstaller()
	} else {
		installer, err = installers.GetSonataFlowInstaller()
	}
	if err != nil {
		return err
	}
	return installer.Install(data.Namespace)
}

//func (data *Data) sonataFlowOperatorHasPodsRunning(numberOfPods int, name, phase string) error {
//	return kogitoFramework.WaitForPodsWithLabel(data.Namespace, "control-plane", "controller-manager", numberOfPods, 1)
//}

//func (data *Data) kogitoOperatorShouldBeInstalled() error {
//	return kogitoFramework.WaitForKogitoOperatorRunning(data.Namespace)
//}

//func (data *Data) cliInstallKogitoOperator() error {
//	_, err := kogitoFramework.ExecuteCliCommandInNamespace(data.Namespace, "install", "operator")
//	return err
//}

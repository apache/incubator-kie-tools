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
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerKogitoInfraSteps register all Kogito Infra steps existing
func registerKogitoInfraSteps(s *godog.Suite, data *Data) {
	s.Step(`^"([^"]*)" install Kogito Infra "([^"]*)"$`, data.installKogitoInfra)
	s.Step(`^"([^"]*)" remove Kogito Infra "([^"]*)"$`, data.removeKogitoInfra)
	s.Step(`^Kogito Infra "([^"]*)" should be running within (\d+) minutes$`, data.kogitoInfraShouldBeRunningWithinMinutes)
	s.Step(`^Kogito Infra "([^"]*)" should NOT be running within (\d+) minutes$`, data.kogitoInfraShouldNOTBeRunningWithinMinutes)
}

func (data *Data) installKogitoInfra(installerType, component string) error {
	return framework.InstallKogitoInfraComponent(data.Namespace, framework.MustParseInstallerType(installerType), framework.ParseKogitoInfraComponent(component))
}

func (data *Data) removeKogitoInfra(installerType, component string) error {
	return framework.RemoveKogitoInfraComponent(data.Namespace, framework.MustParseInstallerType(installerType), framework.ParseKogitoInfraComponent(component))
}

func (data *Data) kogitoInfraShouldBeRunningWithinMinutes(component string, timeoutInMin int) error {
	return framework.WaitForKogitoInfraComponent(data.Namespace, framework.ParseKogitoInfraComponent(component), true, timeoutInMin)
}

func (data *Data) kogitoInfraShouldNOTBeRunningWithinMinutes(component string, timeoutInMin int) error {
	return framework.WaitForKogitoInfraComponent(data.Namespace, framework.ParseKogitoInfraComponent(component), false, timeoutInMin)
}

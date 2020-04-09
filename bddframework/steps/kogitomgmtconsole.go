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
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// RegisterCliSteps register all CLI steps existing
func registerKogitoManagementConsoleSteps(s *godog.Suite, data *Data) {
	s.Step(`^Install Kogito Management Console with (\d+) replicas$`, data.installKogitoManagementConsoleWithReplicas)
	s.Step(`^Kogito Management Console has (\d+) pods running within (\d+) minutes$`, data.kogitoManagementConsoleHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoManagementConsoleWithReplicas(replicas int) error {
	return framework.InstallKogitoManagementConsole(data.Namespace, framework.GetDefaultInstallerType(), replicas)
}

func (data *Data) kogitoManagementConsoleHasPodsRunningWithinMinutes(pods, timeoutInMin int) error {
	return framework.WaitForKogitoManagementConsoleService(data.Namespace, pods, timeoutInMin)
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package steps

import (
	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
)

/*
	DataTable for Task console:
	| config          | infra       | <KogitoInfra name>        |
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| runtime-env     | varName     | varValue                  |
*/

// RegisterCliSteps register all CLI steps existing
func registerKogitoTaskConsoleSteps(s *godog.ScenarioContext, data *Data) {
	s.Step(`^Install Kogito Task Console with (\d+) replicas$`, data.installKogitoTaskConsoleWithReplicas)
	s.Step(`^Install Kogito Task Console with (\d+) replicas with configuration:$`, data.installKogitoTaskConsoleWithReplicasWithConfiguration)
	s.Step(`^Kogito Task Console has (\d+) pods running within (\d+) minutes$`, data.kogitoTaskConsoleHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoTaskConsoleWithReplicas(replicas int) error {
	taskConsole := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoTaskConsoleResourceStub(data.Namespace, replicas),
	}

	// Can be removed once https://issues.redhat.com/browse/KOGITO-1141 is implemented
	if !framework.IsOpenshift() {
		if err := addIngressURIEnvVariableToTaskConsole(data.Namespace, taskConsole); err != nil {
			return err
		}
	}

	return framework.InstallKogitoTaskConsole(framework.GetDefaultInstallerType(), taskConsole)
}

func (data *Data) installKogitoTaskConsoleWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	taskConsole := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoTaskConsoleResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, taskConsole); err != nil {
		return err
	}

	// Can be removed once https://issues.redhat.com/browse/KOGITO-1141 is implemented
	if !framework.IsOpenshift() {
		if err := addIngressURIEnvVariableToTaskConsole(data.Namespace, taskConsole); err != nil {
			return err
		}
	}

	return framework.InstallKogitoTaskConsole(framework.GetDefaultInstallerType(), taskConsole)
}

func (data *Data) kogitoTaskConsoleHasPodsRunningWithinMinutes(pods, timeoutInMin int) error {
	return framework.WaitForKogitoTaskConsoleService(data.Namespace, pods, timeoutInMin)
}

func addIngressURIEnvVariableToTaskConsole(namespace string, taskConsole *bddtypes.KogitoServiceHolder) error {
	dataIndexURI, err := framework.GetIngressURI(namespace, "data-index")
	if err != nil {
		return err
	}
	taskConsole.KogitoService.GetSpec().AddEnvironmentVariable("KOGITO_DATAINDEX_HTTP_URL", dataIndexURI)
	return nil
}

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
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/resource"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

/*
	DataTable for KogitoRuntime:
	| infinispan       | useKogitoInfra | enabled/disabled          |
	| infinispan       | username       | developer                 |
	| infinispan       | password       | mypass                    |
	| infinispan       | uri            | external-infinispan:11222 |
	| kafka            | useKogitoInfra | enabled/disabled          |
	| kafka            | externalURI    | kafka-bootstrap:9092      |
	| kafka            | instance       | external-kafka            |
	| service-label    | labelKey       | labelValue                |
	| deployment-label | labelKey       | labelValue                |
	| runtime-request  | cpu/memory     | value                     |
	| runtime-limit    | cpu/memory     | value                     |
	| runtime-env      | varName        | varValue                  |
*/

func registerKogitoRuntimeSteps(ctx *godog.ScenarioContext, data *Data) {
	// Deploy steps
	ctx.Step(`^Deploy (quarkus|springboot) example service "([^"]*)" from runtime registry with configuration:$`, data.deployExampleServiceFromRuntimeRegistryWithConfiguration)
	ctx.Step(`^Deploy runtime (quarkus|springboot) example service "([^"]*)" with configuration:$`, data.deployRuntimeExampleServiceWithConfiguration)

	// Deployment steps
	ctx.Step(`^Kogito Runtime "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoRuntimeHasPodsRunningWithinMinutes)

	// Kogito Runtime steps
	ctx.Step(`^Scale Kogito Runtime "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoRuntimeToPodsWithinMinutes)
}

// Deploy service steps
func (data *Data) deployExampleServiceFromRuntimeRegistryWithConfiguration(runtimeType, kogitoApplicationName string, table *godog.Table) error {
	imageTag := data.ScenarioContext[getBuiltRuntimeImageTagContextKey(kogitoApplicationName)]
	return data.deployExampleServiceFromImageWithConfiguration(runtimeType, kogitoApplicationName, imageTag, table)
}

// Can be renamed to deployExampleServiceWithConfiguration once KogitoApp is removed
func (data *Data) deployRuntimeExampleServiceWithConfiguration(runtimeType, kogitoApplicationName string, table *godog.Table) error {
	// Passing empty image tag so image values are not filled
	return data.deployExampleServiceFromImageWithConfiguration(runtimeType, kogitoApplicationName, "", table)
}

func (data *Data) deployExampleServiceFromImageWithConfiguration(runtimeType, kogitoApplicationName, imageTag string, table *godog.Table) error {
	kogitoRuntime, err := getKogitoRuntimeExamplesStub(data.Namespace, runtimeType, kogitoApplicationName, imageTag, table)
	if err != nil {
		return err
	}

	return framework.DeployRuntimeService(data.Namespace, framework.GetDefaultInstallerType(), kogitoRuntime)
}

// Deployment steps
func (data *Data) kogitoRuntimeHasPodsRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	if err := framework.WaitForDeploymentRunning(data.Namespace, dcName, podNb, timeoutInMin); err != nil {
		return err
	}

	// Workaround because two pods are created at the same time when adding a Kogito Runtime.
	// We need wait for only one (wait until the wrong one is deleted)
	return framework.WaitForPodsWithLabel(data.Namespace, resource.LabelKeyAppName, dcName, podNb, timeoutInMin)
}

// Scale steps
func (data *Data) scaleKogitoRuntimeToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetKogitoRuntimeReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForDeploymentRunning(data.Namespace, name, nbPods, timeoutInMin)
}

// Misc methods

// getKogitoRuntimeExamplesStub Get basic KogitoRuntime stub with GIT properties initialized to common Kogito examples
func getKogitoRuntimeExamplesStub(namespace, runtimeType, name, imageTag string, table *godog.Table) (*bddtypes.KogitoServiceHolder, error) {
	kogitoRuntime := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoRuntimeStub(namespace, runtimeType, name, imageTag),
	}

	if err := mappers.MapKogitoServiceTable(table, kogitoRuntime); err != nil {
		return nil, err
	}

	return kogitoRuntime, nil
}

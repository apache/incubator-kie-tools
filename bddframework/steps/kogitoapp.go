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
	"path/filepath"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^Deploy (quarkus|springboot) example service "([^"]*)" with configuration:$`, data.deployExampleServiceWithConfiguration)
	s.Step(`^Create (quarkus|springboot) service "([^"]*)"$`, data.createService)
	s.Step(`^Create (quarkus|springboot) service "([^"]*)" with configuration:$`, data.createServiceWithConfiguration)
	s.Step(`^Deploy (quarkus|springboot) service from example file "([^"]*)"$`, data.deployServiceFromExampleFile)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)
	s.Step(`^Kogito application "([^"]*)" has pods with runtime resources within (\d+) minutes:$`, data.kogitoApplicationHaveResourcesWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// Logging steps
	s.Step(`^Kogito application "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoApplicationLogContainsTextWithinMinutes)
}

// Deploy service steps

func (data *Data) deployExampleServiceWithConfiguration(runtimeType, contextDir string, table *messages.PickleStepArgument_PickleTable) error {
	kogitoApp, err := getKogitoAppExamplesStub(data.Namespace, runtimeType, contextDir, table)
	if err != nil {
		return err
	}

	if kogitoApp.Spec.Runtime != v1alpha1.QuarkusRuntimeType && kogitoApp.Spec.Build.Native {
		return fmt.Errorf(runtimeType + " does not support native build")
	}

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) createService(runtimeType, serviceName string) error {
	return data.createServiceWithConfiguration(runtimeType, serviceName, &messages.PickleStepArgument_PickleTable{})
}

func (data *Data) createServiceWithConfiguration(runtimeType, serviceName string, table *messages.PickleStepArgument_PickleTable) error {
	kogitoApp := framework.GetKogitoAppStub(data.Namespace, runtimeType, serviceName)
	if err := configureKogitoAppFromTable(table, kogitoApp); err != nil {
		return err
	}

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployServiceFromExampleFile(runtimeType, exampleFile string) error {
	return framework.DeployServiceFromExampleFile(data.Namespace, runtimeType, exampleFile)
}

// DeploymentConfig steps
func (data *Data) kogitoApplicationHasPodsRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	return framework.WaitForDeploymentConfigRunning(data.Namespace, dcName, podNb, timeoutInMin)
}

func (data *Data) kogitoApplicationHaveResourcesWithinMinutes(dcName string, timeoutInMin int, dt *messages.PickleStepArgument_PickleTable) error {
	_, requirements, err := parseResourceRequirementsTable(dt)

	if err != nil {
		return err
	}

	return framework.WaitForPodsToHaveResources(data.Namespace, dcName, *requirements, timeoutInMin)
}

// Scale steps
func (data *Data) scaleKogitoApplicationToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetKogitoAppReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForDeploymentConfigRunning(data.Namespace, name, nbPods, timeoutInMin)
}

// Logging steps
func (data *Data) kogitoApplicationLogContainsTextWithinMinutes(dcName, logText string, timeoutInMin int) error {
	return framework.WaitForAllPodsToContainTextInLog(data.Namespace, dcName, logText, timeoutInMin)
}

// Misc methods

// getKogitoAppExampleStub Get basic KogitoApp stub with GIT properties initialized to common Kogito examples
func getKogitoAppExamplesStub(namespace, runtimeType, contextDir string, table *messages.PickleStepArgument_PickleTable) (*v1alpha1.KogitoApp, error) {
	kogitoApp := framework.GetKogitoAppStub(namespace, runtimeType, filepath.Base(contextDir))

	kogitoApp.Spec.Build.GitSource.URI = config.GetExamplesRepositoryURI()
	kogitoApp.Spec.Build.GitSource.ContextDir = contextDir

	if ref := config.GetExamplesRepositoryRef(); len(ref) > 0 {
		kogitoApp.Spec.Build.GitSource.Reference = ref
	}

	if err := configureKogitoAppFromTable(table, kogitoApp); err != nil {
		return nil, err
	}

	return kogitoApp, nil
}

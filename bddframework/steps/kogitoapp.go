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
	"path/filepath"

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/rdumont/assistdog"
)

var assist = assistdog.NewDefault()

func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled)$`, data.deployQuarkusExampleServiceWithNative)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and labels$`, data.deployQuarkusExampleServiceWithNativeAndLabels)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and persistence$`, data.deployQuarkusExampleServiceWithNativeAndPersistence)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and persistence and events$`, data.deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents)
	s.Step(`^Deploy spring boot example service "([^"]*)"$`, data.deploySpringBootExampleService)
	s.Step(`^Deploy spring boot example service "([^"]*)" with persistence$`, data.deploySpringBootExampleServiceWithPersistence)
	s.Step(`^Create service "([^"]*)"$`, data.createService)
	s.Step(`^Deploy service from example file "([^"]*)"$`, data.deployServiceFromExampleFile)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// Logging steps
	s.Step(`^Kogito application "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoApplicationLogContainsTextWithinMinutes)
}

// Deploy service steps
func (data *Data) deployQuarkusExampleServiceWithNative(contextDir, native string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.QuarkusRuntimeType,
			Native:             framework.MustParseEnabledDisabled(native),
			Persistence:        false,
			Events:             false,
			Labels:             nil,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndLabels(contextDir, native string, dt *gherkin.DataTable) error {
	labels, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.QuarkusRuntimeType,
			Native:             framework.MustParseEnabledDisabled(native),
			Persistence:        false,
			Events:             false,
			Labels:             labels,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistence(contextDir, native string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.QuarkusRuntimeType,
			Native:             framework.MustParseEnabledDisabled(native),
			Persistence:        true,
			Events:             false,
			Labels:             nil,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents(contextDir, native string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.QuarkusRuntimeType,
			Native:             framework.MustParseEnabledDisabled(native),
			Persistence:        true,
			Events:             true,
			Labels:             nil,
		})
}

func (data *Data) deploySpringBootExampleService(contextDir string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.SpringbootRuntimeType,
			Native:             false,
			Persistence:        false,
			Events:             false,
			Labels:             nil,
		})
}

func (data *Data) deploySpringBootExampleServiceWithPersistence(contextDir string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName:            filepath.Base(contextDir),
			GitSourceURI:       config.GetExamplesRepositoryURI(),
			GitSourceReference: config.GetExamplesRepositoryRef(),
			ContextDir:         contextDir,
			Runtime:            v1alpha1.SpringbootRuntimeType,
			Native:             false,
			Persistence:        true,
			Events:             false,
			Labels:             nil,
		})
}

func (data *Data) createService(serviceName string) error {
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(),
		framework.KogitoAppDeployment{
			AppName: serviceName,
			Runtime: v1alpha1.SpringbootRuntimeType,
		})
}

func (data *Data) deployServiceFromExampleFile(exampleFile string) error {
	return framework.DeployServiceFromExampleFile(data.Namespace, exampleFile)
}

// DeploymentConfig steps
func (data *Data) kogitoApplicationHasPodsRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	return framework.WaitForDeploymentConfigRunning(data.Namespace, dcName, podNb, timeoutInMin)
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

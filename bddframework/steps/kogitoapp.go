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
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/rdumont/assistdog"
)

var assist = assistdog.NewDefault()

// RegisterCliSteps register all CLI steps existing
func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^"([^"]*)" deploy quarkus example service "([^"]*)" with native "([^"]*)"$`, data.deployQuarkusExampleServiceWithNative)
	s.Step(`^"([^"]*)" deploy quarkus example service "([^"]*)" with native "([^"]*)" and labels$`, data.deployQuarkusExampleServiceWithNativeAndLabels)
	s.Step(`^"([^"]*)" deploy quarkus example service "([^"]*)" with native "([^"]*)" and persistence$`, data.deployQuarkusExampleServiceWithNativeAndPersistence)
	s.Step(`^"([^"]*)" deploy quarkus example service "([^"]*)" with native "([^"]*)" and persistence and events$`, data.deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents)
	s.Step(`^"([^"]*)" deploy spring boot example service "([^"]*)"$`, data.deploySpringBootExampleService)
	s.Step(`^"([^"]*)" deploy spring boot example service "([^"]*)" with persistence$`, data.deploySpringBootExampleServiceWithPersistence)

	// Build steps
	s.Step(`^Build "([^"]*)" is complete after (\d+) minutes$`, data.buildIsCompleteAfterMinutes)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// Logging steps
	s.Step(`^Kogito application "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoApplicationLogContainsTextWithinMinutes)
}

// Deploy service steps
func (data *Data) deployQuarkusExampleServiceWithNative(installerType, contextDir, native string) error {
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.QuarkusRuntimeType,
			Native:      util.MustParseBool(native),
			Persistence: false,
			Events:      false,
			Labels:      nil,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndLabels(installerType, contextDir, native string, dt *gherkin.DataTable) error {
	labels, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.QuarkusRuntimeType,
			Native:      util.MustParseBool(native),
			Persistence: false,
			Events:      false,
			Labels:      labels,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistence(installerType, contextDir, native string) error {
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.QuarkusRuntimeType,
			Native:      util.MustParseBool(native),
			Persistence: true,
			Events:      false,
			Labels:      nil,
		})
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents(installerType, contextDir, native string) error {
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.QuarkusRuntimeType,
			Native:      util.MustParseBool(native),
			Persistence: true,
			Events:      true,
			Labels:      nil,
		})
}

func (data *Data) deploySpringBootExampleService(installerType, contextDir string) error {
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.SpringbootRuntimeType,
			Native:      false,
			Persistence: false,
			Events:      false,
			Labels:      nil,
		})
}

func (data *Data) deploySpringBootExampleServiceWithPersistence(installerType, contextDir string) error {
	return framework.DeployExample(data.Namespace, framework.MustParseInstallerType(installerType),
		framework.KogitoAppDeployment{
			AppName:     filepath.Base(contextDir),
			ContextDir:  contextDir,
			Runtime:     v1alpha1.SpringbootRuntimeType,
			Native:      false,
			Persistence: true,
			Events:      false,
			Labels:      nil,
		})
}

// Build steps
func (data *Data) buildIsCompleteAfterMinutes(buildName string, timeoutInMin int) error {
	return framework.WaitForBuildComplete(data.Namespace, buildName, timeoutInMin)
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

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
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// RegisterCliSteps register all CLI steps existing
func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^Deploy quarkus example service "([^"]*)" with native "([^"]*)"$`, data.deployQuarkusExampleServiceWithNative)
	s.Step(`^Deploy quarkus example service "([^"]*)" with persistence enabled and native "([^"]*)"$`, data.deployQuarkusExampleServiceWithPersistenceAndNative)
	s.Step(`^Deploy quarkus example service "([^"]*)" with persistence enabled and native "([^"]*)" and events "([^"]*)"$`, data.deployQuarkusExampleServiceWithPersistenceAndNativeAndEvents)
	s.Step(`^Deploy spring boot example service "([^"]*)"$`, data.deploySpringBootExampleService)
	s.Step(`^Deploy spring boot example service "([^"]*)" with persistence enabled$`, data.deploySpringBootExampleServiceWithPersistence)

	// Build steps
	s.Step(`^Build "([^"]*)" is complete after (\d+) minutes$`, data.buildIsCompleteAfterMinutes)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)
}

// Deploy service steps
func (data *Data) deployQuarkusExampleServiceWithNative(contextDir, native string) error {
	return framework.DeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", false, false)
}

func (data *Data) deployQuarkusExampleServiceWithPersistenceAndNative(contextDir, native string) error {
	return framework.DeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", true, false)
}

func (data *Data) deployQuarkusExampleServiceWithPersistenceAndNativeAndEvents(contextDir, native, events string) error {
	return framework.DeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", true, events == "enabled")
}

func (data *Data) deploySpringBootExampleService(contextDir string) error {
	return framework.DeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, false, false)
}

func (data *Data) deploySpringBootExampleServiceWithPersistence(contextDir string) error {
	return framework.DeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, true, false)
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

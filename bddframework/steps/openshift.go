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
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerOpenShiftSteps(s *godog.Suite, data *Data) {
	// Build steps
	s.Step(`^Start build with name "([^"]*)" from local example service path "([^"]*)"$`, data.startBuildFromExampleServicePath)
	s.Step(`^Build "([^"]*)" is complete after (\d+) minutes$`, data.buildIsCompleteAfterMinutes)

	// BuildConfig steps
	s.Step(`^BuildConfig "([^"]*)" is created after (\d+) minutes$`, data.buildConfigIsCreatedAfterMinutes)
	s.Step(`^BuildConfig "([^"]*)" is created with build resources within (\d+) minutes:$`, data.buildConfigHasResourcesWithinMinutes)
}

// Build steps
func (data *Data) startBuildFromExampleServicePath(buildName, localExamplePath string) error {
	examplesRepositoryPath := data.KogitoExamplesLocation
	_, err := framework.CreateCommand("oc", "start-build", buildName, "--from-dir="+examplesRepositoryPath+"/"+localExamplePath, "-n", data.Namespace).WithLoggerContext(data.Namespace).Execute()
	return err
}

func (data *Data) buildIsCompleteAfterMinutes(buildName string, timeoutInMin int) error {
	return framework.WaitForBuildComplete(data.Namespace, buildName, timeoutInMin)
}

func (data *Data) buildConfigIsCreatedAfterMinutes(buildConfigName string, timeoutInMin int) error {
	return framework.WaitForBuildConfigCreated(data.Namespace, buildConfigName, timeoutInMin)
}

func (data *Data) buildConfigHasResourcesWithinMinutes(buildConfigName string, timeoutInMin int, dt *messages.PickleStepArgument_PickleTable) error {
	requirements, _, err := parseResourceRequirementsTable(dt)

	if err != nil {
		return err
	}

	return framework.WaitForBuildConfigToHaveResources(data.Namespace, buildConfigName, *requirements, timeoutInMin)
}

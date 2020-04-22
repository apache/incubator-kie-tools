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

// registerMavenSteps register all existing Maven steps
func registerMavenSteps(s *godog.Suite, data *Data) {
	s.Step(`^Local example service "([^"]*)" is built by Maven$`, data.localServiceBuiltByMaven)
	s.Step(`^Local example service "([^"]*)" is built by Maven using profile "([^"]*)"$`, data.localServiceBuiltByMavenWithProfile)
}

// Build local service
func (data *Data) localServiceBuiltByMaven(serviceName string) error {
	serviceRepositoryPath := data.KogitoExamplesLocation + "/" + serviceName
	_, err := framework.CreateCommand("mvn", "clean", "package", "-DskipTests").InDirectory(serviceRepositoryPath).WithLoggerContext(data.Namespace).Execute()
	return err
}

// Build local service
func (data *Data) localServiceBuiltByMavenWithProfile(serviceName, profile string) error {
	serviceRepositoryPath := data.KogitoExamplesLocation + "/" + serviceName
	_, err := framework.CreateCommand("mvn", "clean", "package", "-DskipTests", "-P"+profile).InDirectory(serviceRepositoryPath).WithLoggerContext(data.Namespace).Execute()
	return err
}

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
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
)

/*
	DataTable for Maven:
	| profile | profile        |
	| profile | profile2       |
	| option  | -Doption=true  |
	| option  | -Doption2=true |
	| native  | enabled        |
*/

// registerMavenSteps register all existing Maven steps
func registerMavenSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Local example service "([^"]*)" is built by Maven$`, data.localServiceBuiltByMaven)
	ctx.Step(`^Local example service "([^"]*)" is built by Maven with configuration:$`, data.localServiceBuiltByMavenWithConfiguration)
}

// Build local service
func (data *Data) localServiceBuiltByMaven(serviceName string) error {
	return data.localServiceBuiltByMavenWithConfiguration(serviceName, nil)
}

// Build local service with configuration
func (data *Data) localServiceBuiltByMavenWithConfiguration(serviceName string, table *godog.Table) error {
	config := &mappers.MavenCommandConfig{}
	if table != nil && len(table.Rows) > 0 {
		err := mappers.MapMavenCommandConfigTable(table, config)
		if err != nil {
			return err
		}
	}
	return data.localServiceBuiltByMavenWithProfileAndOptions(serviceName, config.Profiles, config.Options)
}

// Build local service with profile and additional options
func (data *Data) localServiceBuiltByMavenWithProfileAndOptions(serviceName string, profiles, options []string) error {
	serviceRepositoryPath := data.KogitoExamplesLocation + "/" + serviceName
	mvnCmd := framework.CreateMavenCommand(serviceRepositoryPath).
		SkipTests().
		UpdateArtifacts().
		Options(options...).
		WithLoggerContext(data.Namespace)
	if len(profiles) > 0 {
		mvnCmd = mvnCmd.Profiles(profiles...)
	}
	output, err := mvnCmd.Execute("clean", "package")
	framework.GetLogger(data.Namespace).Debug(output)
	return err
}

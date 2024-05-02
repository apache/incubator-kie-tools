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
	"fmt"

	"github.com/cucumber/godog"
	v1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
)

const defaultTimeoutToStartBuildInMin = 5

/*
	DataTable for BuildConfig build resources:
	| build-request  | cpu/memory     | value  |
	| build-limit    | cpu/memory     | value  |
*/

func registerOpenShiftSteps(ctx *godog.ScenarioContext, data *Data) {
	// Build steps
	ctx.Step(`^Start build with name "([^"]*)" from local example service path "([^"]*)"$`, data.startBuildFromExampleServicePath)
	ctx.Step(`^Start build with name "([^"]*)" from local example service file "([^"]*)"$`, data.startBuildFromExampleServiceFile)

	// BuildConfig steps
	ctx.Step(`^BuildConfig "([^"]*)" is created after (\d+) minutes$`, data.buildConfigIsCreatedAfterMinutes)
	ctx.Step(`^BuildConfig "([^"]*)" is created with build resources within (\d+) minutes:$`, data.buildConfigHasResourcesWithinMinutes)
}

// Build steps
func (data *Data) startBuildFromExampleServicePath(buildName, localExamplePath string) error {
	examplesRepositoryPath := data.KogitoExamplesLocation
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Build '%s' to start", buildName), defaultTimeoutToStartBuildInMin,
		func() (bool, error) {
			_, err := framework.CreateCommand("oc", "start-build", buildName, "--from-dir="+examplesRepositoryPath+"/"+localExamplePath, "-n", data.Namespace).WithLoggerContext(data.Namespace).Execute()
			return err == nil, err
		})
}

func (data *Data) startBuildFromExampleServiceFile(buildName, localExampleFilePath string) error {
	examplesRepositoryPath := data.KogitoExamplesLocation
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Build '%s' to start", buildName), defaultTimeoutToStartBuildInMin,
		func() (bool, error) {
			_, err := framework.CreateCommand("oc", "start-build", buildName, "--from-file="+examplesRepositoryPath+"/"+localExampleFilePath, "-n", data.Namespace).WithLoggerContext(data.Namespace).Execute()
			return err == nil, err
		})
}

func (data *Data) buildConfigIsCreatedAfterMinutes(buildConfigName string, timeoutInMin int) error {
	return framework.WaitForBuildConfigCreated(data.Namespace, buildConfigName, timeoutInMin)
}

func (data *Data) buildConfigHasResourcesWithinMinutes(buildConfigName string, timeoutInMin int, dt *godog.Table) error {
	build := &v1.ResourceRequirements{Limits: v1.ResourceList{}, Requests: v1.ResourceList{}}
	err := mappers.MapBuildResourceRequirementsTable(dt, build)

	if err != nil {
		return err
	}

	return framework.WaitForBuildConfigToHaveResources(data.Namespace, buildConfigName, *build, timeoutInMin)
}

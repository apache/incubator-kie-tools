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
	"fmt"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

const sourceLocation = "src/main/resources"

func registerKogitoDeployFilesSteps(ctx *godog.ScenarioContext, data *Data) {
	// Deploy steps
	ctx.Step(`^Deploy (quarkus|springboot) file "([^"]*)" from example service "([^"]*)"$`, data.deployFileFromExampleService)
	ctx.Step(`^Deploy (quarkus|springboot) folder from example service "([^"]*)"$`, data.deployFolderFromExampleService)
}

// Deploy steps

func (data *Data) deployFileFromExampleService(runtimeType, file, serviceName string) error {
	sourceFilePath := fmt.Sprintf(`%s/%s/%s/%s`, data.KogitoExamplesLocation, serviceName, sourceLocation, file)
	return deploySourceFilesFromPath(data.Namespace, runtimeType, serviceName, sourceFilePath)
}

func (data *Data) deployFolderFromExampleService(runtimeType, serviceName string) error {
	sourceFolderPath := fmt.Sprintf(`%s/%s/%s`, data.KogitoExamplesLocation, serviceName, sourceLocation)
	return deploySourceFilesFromPath(data.Namespace, runtimeType, serviceName, sourceFolderPath)
}

func deploySourceFilesFromPath(namespace, runtimeType, serviceName, path string) error {
	framework.GetLogger(namespace).Infof("Deploy %s example %s with source files in path %s", runtimeType, serviceName, path)

	buildHolder, err := getKogitoBuildConfiguredStub(namespace, runtimeType, serviceName, nil)
	if err != nil {
		return err
	}

	buildHolder.KogitoBuild.Spec.Type = v1alpha1.LocalSourceBuildType
	buildHolder.KogitoBuild.Spec.GitSource.URI = path

	return framework.DeployKogitoBuild(namespace, framework.CLIInstallerType, buildHolder)
}

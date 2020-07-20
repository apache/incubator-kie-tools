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
	"path/filepath"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

/*
	DataTable for KogitoRuntime:
	| config | native | enabled/disabled |
*/

const (
	// DataTable first column
	kogitoBuildConfigKey = "config"

	// DataTable second column
	kogitoBuildNativeKey = "native"
)

func registerKogitoBuildSteps(ctx *godog.ScenarioContext, data *Data) {
	// Deploy steps
	ctx.Step(`^Build (quarkus|springboot) example service "([^"]*)" with configuration:$`, data.buildExampleServiceWithConfiguration)
	ctx.Step(`^Build binary (quarkus|springboot) service "([^"]*)" with configuration:$`, data.buildBinaryServiceWithConfiguration)
	ctx.Step(`^Build (quarkus|springboot) example service "([^"]*)" from local file with configuration:$`, data.buildExampleServiceFromLocalFileWithConfiguration)
}

// Build service steps

func (data *Data) buildExampleServiceWithConfiguration(runtimeType, contextDir string, table *godog.Table) error {
	kogitoBuild, err := getKogitoBuildConfiguredStub(data.Namespace, runtimeType, filepath.Base(contextDir), table)
	if err != nil {
		return err
	}

	kogitoBuild.Spec.Type = v1alpha1.RemoteSourceBuildType
	kogitoBuild.Spec.GitSource.URI = config.GetExamplesRepositoryURI()
	kogitoBuild.Spec.GitSource.ContextDir = contextDir
	if ref := config.GetExamplesRepositoryRef(); len(ref) > 0 {
		kogitoBuild.Spec.GitSource.Reference = ref
	}

	// Only working using CR installer. CLI support will come in KOGITO-1998?
	return framework.DeployKogitoBuild(data.Namespace, framework.CRInstallerType, kogitoBuild)
}

func (data *Data) buildBinaryServiceWithConfiguration(runtimeType, serviceName string, table *godog.Table) error {
	kogitoBuild, err := getKogitoBuildConfiguredStub(data.Namespace, runtimeType, serviceName, table)
	if err != nil {
		return err
	}

	kogitoBuild.Spec.Type = v1alpha1.BinaryBuildType

	// Only working using CR installer. CLI support will come in KOGITO-1998?
	return framework.DeployKogitoBuild(data.Namespace, framework.CRInstallerType, kogitoBuild)
}

func (data *Data) buildExampleServiceFromLocalFileWithConfiguration(runtimeType, serviceName string, table *godog.Table) error {
	kogitoBuild, err := getKogitoBuildConfiguredStub(data.Namespace, runtimeType, serviceName, table)
	if err != nil {
		return err
	}

	kogitoBuild.Spec.Type = v1alpha1.LocalSourceBuildType

	// Only working using CR installer. CLI support will come in KOGITO-1998?
	return framework.DeployKogitoBuild(data.Namespace, framework.CRInstallerType, kogitoBuild)
}

// Misc methods

// getKogitoBuildConfiguredStub Get basic KogitoBuild stub initialized from table
func getKogitoBuildConfiguredStub(namespace, runtimeType, serviceName string, table *godog.Table) (*v1alpha1.KogitoBuild, error) {
	kogitoBuild := framework.GetKogitoBuildStub(namespace, runtimeType, serviceName)

	if err := configureKogitoBuildFromTable(table, kogitoBuild); err != nil {
		return nil, err
	}

	framework.SetupKogitoBuildImageStreams(kogitoBuild)

	return kogitoBuild, nil
}

// Table parsing

func configureKogitoBuildFromTable(table *godog.Table, kogitoBuild *v1alpha1.KogitoBuild) (err error) {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 3 {
		return fmt.Errorf("expected table to have exactly three columns")
	}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)

		switch firstColumn {
		case kogitoBuildConfigKey:
			err = parseKogitoBuildConfigRow(row, kogitoBuild)

		default:
			err = fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}

		if err != nil {
			return
		}
	}

	return
}

func parseKogitoBuildConfigRow(row *TableRow, kogitoBuild *v1alpha1.KogitoBuild) error {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoBuildNativeKey:
		kogitoBuild.Spec.Native = framework.MustParseEnabledDisabled(getThirdColumn(row))

	default:
		return fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return nil
}

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

package examples

import (
	"fmt"
	"regexp"
	"strings"
	"testing"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-operator/test/pkg/executor"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"github.com/kiegroup/kogito-operator/test/pkg/gherkin"
	"github.com/kiegroup/kogito-operator/test/pkg/meta"
	"github.com/kiegroup/kogito-operator/test/pkg/steps"
	"github.com/kiegroup/kogito-operator/test/pkg/steps/mappers"
)

func TestMain(m *testing.M) {
	// Create kube client
	if err := framework.InitKubeClient(meta.GetRegisteredSchema()); err != nil {
		panic(err)
	}
	executor.ExecuteBDDTests(func(godogOpts *godog.Options) error {
		return GenerateBuildExamplesFeatures(godogOpts)
	})
}

// GenerateBuildExamplesFeatures generate build examples features files
// and update the godog options to point to them
func GenerateBuildExamplesFeatures(godogOpts *godog.Options) error {
	features, err := gherkin.ParseFeatures(godogOpts.Tags, godogOpts.Paths)
	if err != nil {
		return fmt.Errorf("Error parsing features: %v", err)
	}

	tmpFolder, err := framework.CreateTemporaryFolder("examples-features")
	if err != nil {
		return fmt.Errorf("Error creating new temporary folder: %v", err)
	}
	framework.GetMainLogger().Info("Created feature folder = " + tmpFolder)

	// Parse all build scenarios
	mavenBuildScenarioMap, err := retrieveMavenBuildScenarios(features)
	if err != nil {
		return err
	}

	// Write features
	err = writeMavenBuildFeatures(tmpFolder, mavenBuildScenarioMap)
	if err != nil {
		return err
	}

	godogOpts.Paths = []string{tmpFolder}
	godogOpts.Tags = ""

	return nil
}

type mavenBuildScenario struct {
	name string
	step *godog.Step
}

func retrieveMavenBuildScenarios(features []*gherkin.Feature) (map[string][]*mavenBuildScenario, error) {
	re := regexp.MustCompile("^" + steps.DefaultMavenBuiltExampleRegex + " and deployed to runtime registry.*$")
	var mavenBuildScenarioMap = make(map[string][]*mavenBuildScenario)
	for _, ft := range features {
		for _, scenario := range ft.Pickles {
			for _, step := range scenario.Steps {
				if re.MatchString(step.Text) {
					exampleName := re.FindStringSubmatch(step.Text)[1]
					buildScenarioName, err := getBuildMavenScenarioName(exampleName, step)
					if err != nil {
						return nil, err
					}

					buildScenarioExists := false
					for _, bs := range mavenBuildScenarioMap[exampleName] {
						if bs.name == buildScenarioName {
							buildScenarioExists = true
							break
						}
					}
					if !buildScenarioExists {
						mavenBuildScenarioMap[exampleName] = append(mavenBuildScenarioMap[exampleName], &mavenBuildScenario{
							name: buildScenarioName,
							step: step,
						})
					}
				}
			}
		}
	}
	return mavenBuildScenarioMap, nil
}

func getBuildMavenScenarioName(exampleName string, step *godog.Step) (string, error) {
	scenarioName := fmt.Sprintf("Build %s image", exampleName)
	mavenConfig := &mappers.MavenCommandConfig{}
	if step.Argument != nil &&
		step.Argument.DataTable != nil {
		if err := mappers.MapMavenCommandConfigTable(step.Argument.DataTable, mavenConfig); err != nil {
			return "", err
		}
	}

	var addedInfo []string
	if mavenConfig.Native {
		addedInfo = append(addedInfo, "native enabled")
	}
	if len(mavenConfig.Profiles) > 0 {
		addedInfo = append(addedInfo, "profile(s) "+strings.Join(mavenConfig.Profiles, ","))
	}
	if len(mavenConfig.Options) > 0 {
		addedInfo = append(addedInfo, "option(s) "+strings.Join(mavenConfig.Options, ","))
	}
	if len(addedInfo) > 0 {
		scenarioName += " with " + strings.Join(addedInfo, " and ")
	}
	return scenarioName, nil
}

func writeMavenBuildFeatures(outputFolder string, mavenBuildScenarioMap map[string][]*mavenBuildScenario) error {
	for exampleName := range mavenBuildScenarioMap {
		framework.GetMainLogger().Debug(fmt.Sprintf("%s.feature", exampleName))
		featureFileContent := fmt.Sprintf("Feature: Build %s images\n\n", exampleName)
		featureFileContent += "  Background:\n"
		featureFileContent += "    Given Clone Kogito examples into local directory\n\n"

		for _, buildScenario := range mavenBuildScenarioMap[exampleName] {
			featureFileContent += fmt.Sprintf("  Scenario: %s\n", buildScenario.name)
			featureFileContent += fmt.Sprintf("    Then %s\n", buildScenario.step.Text)
			if buildScenario.step.Argument != nil &&
				buildScenario.step.Argument.DataTable != nil {
				for _, row := range buildScenario.step.Argument.DataTable.Rows {
					rowContent := "| "
					for _, cell := range row.Cells {
						rowContent += fmt.Sprintf("%s | ", cell.Value)
					}
					featureFileContent += fmt.Sprintf("        %s\n", rowContent)
				}
			}
			featureFileContent += "\n"
		}
		framework.GetMainLogger().Debug(featureFileContent)
		err := framework.CreateFile(outputFolder, fmt.Sprintf("%s.feature", strings.ReplaceAll(exampleName, "/", "-")), featureFileContent)
		if err != nil {
			return err
		}
	}
	return nil
}

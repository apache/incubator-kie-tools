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
	"fmt"
	"strings"
	"time"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// Data contains all data needed by Gherkin steps to run
type Data struct {
	Namespace              string
	StartTime              time.Time
	KogitoExamplesLocation string
	ScenarioName           string
	ScenarioContext        map[string]string
}

// RegisterAllSteps register all steps available to the test suite
func (data *Data) RegisterAllSteps(s *godog.Suite) {
	registerGitSteps(s, data)
	registerGraphQLSteps(s, data)
	registerHTTPSteps(s, data)
	registerInfinispanSteps(s, data)
	registerKogitoAppSteps(s, data)
	registerKogitoDataIndexServiceSteps(s, data)
	registerKogitoInfraSteps(s, data)
	registerKogitoJobsServiceSteps(s, data)
	registerKogitoManagementConsoleSteps(s, data)
	registerKubernetesSteps(s, data)
	registerMavenSteps(s, data)
	registerOpenShiftSteps(s, data)
	registerOperatorSteps(s, data)
	registerPrometheusSteps(s, data)
	registerProcessSteps(s, data)
	registerTaskSteps(s, data)
}

// BeforeScenario configure the data before a scenario is launched
func (data *Data) BeforeScenario(pickle *messages.Pickle) {
	data.StartTime = time.Now()
	data.Namespace = getNamespaceName()
	data.KogitoExamplesLocation = createTemporaryFolder()
	data.ScenarioName = pickle.GetName()
	data.ScenarioContext = map[string]string{}

	framework.GetLogger(data.Namespace).Info(fmt.Sprintf("Scenario %s", pickle.GetName()))
	go framework.StartPodLogCollector(data.Namespace)
}

func getNamespaceName() string {
	if namespaceName := config.GetNamespaceName(); len(namespaceName) > 0 {
		return namespaceName
	}
	return framework.GenerateNamespaceName("cucumber")
}

func createTemporaryFolder() string {
	dir, err := framework.CreateTemporaryFolder("kogito-examples")
	if err != nil {
		panic(fmt.Errorf("Error creating new temporary folder: %v", err))
	}
	return dir
}

// AfterScenario executes some actions on data after a scenario is finished
func (data *Data) AfterScenario(pickle *messages.Pickle, err error) {
	framework.OperateOnNamespaceIfExists(data.Namespace, func(namespace string) error {
		if err := framework.StopPodLogCollector(namespace); err != nil {
			framework.GetMainLogger().Errorf("Error stopping log collector on namespace %s: %v", namespace, err)
		}
		if err := framework.FlushLogger(namespace); err != nil {
			framework.GetMainLogger().Errorf("Error flushing running logs for namespace %s: %v", namespace, err)
		}
		if err := framework.BumpEvents(data.Namespace); err != nil {
			framework.GetMainLogger().Errorf("Error bumping events for namespace %s: %v", namespace, err)
		}
		return nil
	})

	handleScenarioResult(data, pickle, err)
	logScenarioDuration(data)
	deleteTemporaryExamplesFolder(data)
}

// ResolveWithScenarioContext replaces all the variables in the string with their values.
func (data *Data) ResolveWithScenarioContext(str string) string {
	result := str
	for name, value := range data.ScenarioContext {
		result = strings.ReplaceAll(result, "{"+name+"}", value)
	}

	return result
}

func logScenarioDuration(data *Data) {
	endTime := time.Now()
	duration := endTime.Sub(data.StartTime)
	framework.GetLogger(data.Namespace).Infof("Scenario duration: %s", duration.String())
}

func handleScenarioResult(data *Data, pickle *messages.Pickle, err error) {
	newLogFolderName := fmt.Sprintf("%s - %s", strings.ReplaceAll(pickle.GetName(), "/", "_"), data.Namespace)
	if err != nil {
		framework.GetLogger(data.Namespace).Errorf("Error in scenario '%s': %v", pickle.GetName(), err)

		newLogFolderName = "error - " + newLogFolderName
	} else {
		framework.GetLogger(data.Namespace).Infof("Successful scenario '%s'", pickle.GetName())
	}
	err = framework.RenameLogFolder(data.Namespace, newLogFolderName)
	if err != nil {
		framework.GetMainLogger().Errorf("Error while moving log foler for namespace %s. New name is %s. Error = %v", data.Namespace, newLogFolderName, err)
	}
}

func deleteTemporaryExamplesFolder(data *Data) {
	err := framework.DeleteFolder(data.KogitoExamplesLocation)
	if err != nil {
		framework.GetMainLogger().Errorf("Error while deleting temporary examples folder %s: %v", data.KogitoExamplesLocation, err)
	}
}

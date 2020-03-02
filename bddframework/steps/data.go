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

	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// Data contains all data needed by Gherkin steps to run
type Data struct {
	Namespace string
	StartTime time.Time
}

// RegisterAllSteps register all steps available to the test suite
func (data *Data) RegisterAllSteps(s *godog.Suite) {
	registerGraphQLSteps(s, data)
	registerHTTPSteps(s, data)
	registerKogitoAppSteps(s, data)
	registerKogitoDataIndexServiceSteps(s, data)
	registerKogitoInfraSteps(s, data)
	registerKogitoJobsServiceSteps(s, data)
	registerKubernetesSteps(s, data)
	registerOperatorSteps(s, data)
	registerPrometheusSteps(s, data)
}

// BeforeScenario configure the data before a scenario is launched
func (data *Data) BeforeScenario(s interface{}) {
	data.StartTime = time.Now()
	data.Namespace = framework.GenerateNamespaceName()

	framework.GetLogger(data.Namespace).Info(fmt.Sprintf("Scenario %s", framework.GetScenarioName(s)))
	go framework.StartPodLogCollector(data.Namespace)
}

// AfterScenario executes some actions on data after a scenario is finished
func (data *Data) AfterScenario(s interface{}, err error) {
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

	logScenarioDuration(data, s)
	handleScenarioResult(data, s, err)
}

func logScenarioDuration(data *Data, s interface{}) {
	endTime := time.Now()
	duration := endTime.Sub(data.StartTime)
	framework.GetLogger(data.Namespace).Infof("Scenario '%s'. Duration = %s", framework.GetScenarioName(s), duration.String())
}

func handleScenarioResult(data *Data, s interface{}, err error) {
	scenarioName := strings.ReplaceAll(framework.GetScenarioName(s), "/", "_")
	newLogFolderName := fmt.Sprintf("%s - %s", scenarioName, data.Namespace)
	if err != nil {
		framework.GetLogger(data.Namespace).Errorf("Error in scenario '%s': %v", framework.GetScenarioName(s), err)

		newLogFolderName = "error - " + newLogFolderName
	}
	err = framework.RenameLogFolder(data.Namespace, newLogFolderName)
	if err != nil {
		framework.GetMainLogger().Errorf("Error while moving log foler for namespace %s. New name is %s. Error = %v", data.Namespace, newLogFolderName, err)
	}
}

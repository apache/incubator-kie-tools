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
	"time"

	"github.com/DATA-DOG/godog"
	"github.com/DATA-DOG/godog/gherkin"

	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
)

// Data contains all data needed by Gherkin steps to run
type Data struct {
	Namespace string
	StartTime time.Time
}

// RegisterAllSteps register all steps available to the test suite
func (data *Data) RegisterAllSteps(s *godog.Suite) {
	registerCliSteps(s, data)
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

	framework.GetLogger(data.Namespace).Info(fmt.Sprintf("Scenario %s", getScenarioName(s)))
}

// BeforeStep configure the data before a scenario is launched
func (data *Data) BeforeStep(s *gherkin.Step) {
	framework.GetLogger(data.Namespace).Infof("Step %s", s.Text)
}

// AfterScenario executes some actions on data after a scenario is finished
func (data *Data) AfterScenario(s interface{}, err error) {
	endTime := time.Now()
	duration := endTime.Sub(data.StartTime)
	framework.GetLogger(data.Namespace).Infof("Scenario '%s'. Duration = %s", getScenarioName(s), duration.String())

	if err != nil {
		framework.GetLogger(data.Namespace).Errorf("Error in scenario '%s': %v", getScenarioName(s), err)
	}
}

func getScenarioName(s interface{}) string {
	if scenario, ok := s.(*gherkin.Scenario); ok {
		return scenario.Name
	}
	return s.(*gherkin.ScenarioOutline).Name
}

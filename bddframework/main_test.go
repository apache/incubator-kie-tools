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

package test

import (
	"flag"
	"fmt"
	"io"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/colors"
	"github.com/cucumber/godog/gherkin"

	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps"
)

const (
	disabledTag = "@disabled"
	cliTag      = "@cli"
	smokeTag    = "@smoke"
)

var opt = godog.Options{
	Output:    colors.Colored(os.Stdout),
	Format:    "junit",
	Randomize: time.Now().UTC().UnixNano(),
	Tags:      disabledTag,
}

func init() {
	godog.BindFlags("godog.", flag.CommandLine, &opt)
	framework.BindTestsConfigFlags(flag.CommandLine)
}

func TestMain(m *testing.M) {
	flag.Parse()
	opt.Paths = flag.Args()

	configureTags()
	configureTestOutput()

	features, err := parseFeatures(opt.Tags, opt.Paths)
	if err != nil {
		panic(fmt.Errorf("Error parsing features: %v", err))
	}
	if framework.IsConfigShowScenarios() {
		showScenarios(features)
	}

	if !framework.IsConfigDryRun() {
		if matchingFeature(cliTag, features) {
			// Check CLI binary is existing if needed
			if exits, err := framework.CheckCliBinaryExist(); err != nil {
				panic(fmt.Errorf("Error trying to get CLI binary %v", err))
			} else if !exits {
				panic("CLI Binary does not exist on specified path")
			}
		}

		status := godog.RunWithOptions("godogs", func(s *godog.Suite) {
			FeatureContext(s)
		}, opt)

		if st := m.Run(); st > status {
			status = st
		}
		os.Exit(status)
	}
	os.Exit(0)
}

func configureTags() {
	if framework.IsSmokeTests() {
		if len(opt.Tags) > 0 {
			opt.Tags += " && "
		}
		// Filter with smoke tag
		opt.Tags += smokeTag
	}

	if !strings.Contains(opt.Tags, disabledTag) {
		if len(opt.Tags) > 0 {
			opt.Tags += " && "
		}
		// Ignore disabled tag
		opt.Tags += "~" + disabledTag
	}
}

func configureTestOutput() {
	if framework.IsSmokeTests() {
		framework.SetLogSubFolder("smoke")
	} else {
		framework.SetLogSubFolder("full")
	}
	logFolder := framework.GetLogFolder()
	if err := framework.CreateFolder(logFolder); err != nil {
		panic(fmt.Errorf("Error while creating log folder %s: %v", logFolder, err))
	}

	mainLogFile, err := os.Create(fmt.Sprintf("%s/%s", logFolder, "junit.xml"))
	if err != nil {
		panic(fmt.Errorf("Error creating junit file: %v", err))
	}

	opt.Format = "junit"
	opt.Output = io.MultiWriter(opt.Output, mainLogFile)
}

func FeatureContext(s *godog.Suite) {
	// Create kube client
	framework.InitKubeClient()

	data := &steps.Data{}
	data.RegisterAllSteps(s)

	// Scenario handlers
	s.BeforeScenario(func(s interface{}) {
		data.BeforeScenario(s)
	})
	s.AfterScenario(func(s interface{}, err error) {
		data.AfterScenario(s, err)

		// Namespace should be deleted after all other operations have been done
		if !framework.IsConfigKeepNamespace() {
			deleteNamespaceIfExists(data.Namespace)
		}
	})

	// Step handlers
	s.BeforeStep(func(s *gherkin.Step) {
		framework.GetLogger(data.Namespace).Infof("Step %s", s.Text)
	})
	s.AfterStep(func(s *gherkin.Step, err error) {
		if err != nil {
			framework.GetLogger(data.Namespace).Errorf("Error in step '%s': %v", s.Text, err)
		}
	})
}

func deleteNamespaceIfExists(namespace string) {
	if ok, er := framework.IsNamespace(namespace); er != nil {
		framework.GetLogger(namespace).Errorf("Error while checking namespace: %v", er)
	} else if ok {
		framework.GetLogger(namespace).Infof("Delete created namespace %s", namespace)
		if e := framework.DeleteNamespace(namespace); e != nil {
			framework.GetLogger(namespace).Errorf("Error while deleting the namespace: %v", e)
		}
	}
}

func matchingFeature(tags string, features []*gherkin.Feature) bool {
	for _, ft := range features {
		if len(getMatchingScenarios(tags, ft)) > 0 {
			return true
		}
	}
	return false
}

func showScenarios(features []*gherkin.Feature) {
	mainLogger := framework.GetMainLogger()
	mainLogger.Info("------------------ SHOW SCENARIOS ------------------")
	for _, ft := range features {
		if len(ft.ScenarioDefinitions) > 0 {
			mainLogger.Infof("Feature: %s", ft.Name)
			for _, scenario := range ft.ScenarioDefinitions {
				mainLogger.Info(framework.GetScenarioName(scenario))
			}
		}
	}
	mainLogger.Info("------------------ END SHOW SCENARIOS ------------------")
}

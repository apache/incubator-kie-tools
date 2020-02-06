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

package operator

import (
	"flag"
	"fmt"
	"os"
	"strings"
	"testing"
	"time"

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/colors"
	"github.com/cucumber/godog/gherkin"

	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/steps"
)

const (
	disabledTag = "@disabled"
	cliTag      = "@cli"
)

var mainLogger = framework.GetLogger("main")

var opt = godog.Options{
	Output:    colors.Colored(os.Stdout),
	Format:    "progress",
	Randomize: time.Now().UTC().UnixNano(),
	Tags:      disabledTag,
}

func init() {
	godog.BindFlags("godog.", flag.CommandLine, &opt)
	framework.BindEnvFlags(flag.CommandLine)
}

func TestMain(m *testing.M) {
	flag.Parse()
	opt.Paths = flag.Args()

	if !strings.Contains(opt.Tags, disabledTag) {
		if opt.Tags != "" {
			opt.Tags += " && "
		}
		// Ignore disabled tag
		opt.Tags += "~" + disabledTag
	}

	features, err := parseFeatures(opt.Tags, opt.Paths)
	if err != nil {
		panic(fmt.Errorf("Error parsing features: %v", err))
	}
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

func FeatureContext(s *godog.Suite) {
	// Create kube client
	framework.InitKubeClient()

	data := &steps.Data{}
	data.RegisterAllSteps(s)

	s.BeforeScenario(func(s interface{}) {
		data.BeforeScenario(s)
	})
	s.BeforeStep(data.BeforeStep)
	s.AfterScenario(func(s interface{}, err error) {
		deleteNamespaceIfExists(data.Namespace)

		data.AfterScenario(s, err)
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

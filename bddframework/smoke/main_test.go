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
	"github.com/cucumber/godog"

	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/steps"
)

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

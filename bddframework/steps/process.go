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
	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerProcessSteps register all process steps
func registerProcessSteps(s *godog.Suite, data *Data) {
	s.Step(`^Start "([^"]*)" process on service "([^"]*)" with body:$`, data.startProcessOnService)
	s.Step(`^Service "([^"]*)" contains (\d+) (?:instance|instances) of process with name "([^"]*)"$`, data.serviceContainsInstancesOfProcess)
}

func (data *Data) startProcessOnService(processName, serviceName string, body *gherkin.DocString) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	err = framework.StartProcess(data.Namespace, routeURI, processName, body.ContentType, body.Content)
	if err != nil {
		return err
	}
	return nil
}

func (data *Data) serviceContainsInstancesOfProcess(serviceName string, processInstances int, processName string) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	foundProcessInstances, err := framework.GetProcessInstances(data.Namespace, routeURI, processName)
	if err != nil {
		return err
	}
	if len(foundProcessInstances) != processInstances {
		return fmt.Errorf("expected %d of process instances, but got %d", processInstances, len(foundProcessInstances))
	}
	return nil
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package steps

import (
	"fmt"

	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/bddframework/pkg/framework"
)

func registerProcessSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Start "([^"]*)" process on service "([^"]*)" with body:$`, data.startProcessOnService)
	ctx.Step(`^Start "([^"]*)" process on service "([^"]*)" within (\d+) minutes with body:$`, data.startProcessOnServiceWithinMinutes)
	ctx.Step(`^Service "([^"]*)" with process name "([^"]*)" is available$`, data.serviceWithProcessNameIsAvailable)
	ctx.Step(`^Service "([^"]*)" with process name "([^"]*)" is available within (\d+) minutes$`, data.serviceWithProcessNameIsAvailableWithinMinutes)
	ctx.Step(`^Service "([^"]*)" contains (\d+) (?:instance|instances) of process with name "([^"]*)"$`, data.serviceContainsInstancesOfProcess)
	ctx.Step(`^Service "([^"]*)" contains (\d+) (?:instance|instances) of process with name "([^"]*)" within (\d+) minutes$`, data.serviceContainsInstancesOfProcessWithinMinutes)
}

func (data *Data) startProcessOnService(processName, serviceName string, body *godog.DocString) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	bodyContent := data.ResolveWithScenarioContext(body.Content)
	err = framework.StartProcess(data.Namespace, uri, processName, body.MediaType, bodyContent)
	if err != nil {
		return err
	}
	return nil
}

func (data *Data) startProcessOnServiceWithinMinutes(processName, serviceName string, timeoutInMin int, body *godog.DocString) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Service %s is not available yet", serviceName), timeoutInMin,
		func() (bool, error) {
			err = framework.StartProcess(data.Namespace, uri, processName, body.MediaType, body.Content)
			if err != nil {
				return false, err
			}

			return true, nil
		})
}

func (data *Data) serviceWithProcessNameIsAvailable(serviceName, processName string) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	_, err = framework.GetProcessInstances(data.Namespace, uri, processName)
	if err != nil {
		return err
	}

	return nil
}

func (data *Data) serviceWithProcessNameIsAvailableWithinMinutes(serviceName, processName string, timeoutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Process %s is not available yet", processName), timeoutInMin,
		func() (bool, error) {
			_, err = framework.GetProcessInstances(data.Namespace, uri, processName)
			if err != nil {
				return false, err
			}

			return true, nil
		})
}

func (data *Data) serviceContainsInstancesOfProcess(serviceName string, processInstances int, processName string) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	foundProcessInstances, err := framework.GetProcessInstances(data.Namespace, uri, processName)
	if err != nil {
		return err
	}
	if len(foundProcessInstances) != processInstances {
		return fmt.Errorf("expected %d of process instances, but got %d", processInstances, len(foundProcessInstances))
	}
	return nil
}

func (data *Data) serviceContainsInstancesOfProcessWithinMinutes(serviceName string, processInstances int, processName string, timeoutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("Process %s has %d instances", processName, processInstances), timeoutInMin,
		func() (bool, error) {
			foundProcessInstances, err := framework.GetProcessInstances(data.Namespace, uri, processName)
			if err != nil {
				return false, err
			}
			if len(foundProcessInstances) != processInstances {
				return false, nil
			}
			return true, nil
		})
}

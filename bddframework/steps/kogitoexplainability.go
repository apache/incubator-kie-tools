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
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

/*
	DataTable for Explainability:
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| runtime-env     | varName     | varValue                  |
	| kafka           | externalURI | kafka-bootstrap:9092      |
	| kafka           | instance    | external-kafka            |
*/

func registerKogitoExplainabilityServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Explainability with (\d+) replicas$`, data.installKogitoExplainabilityServiceWithReplicas)
	ctx.Step(`^Install Kogito Explainability with (\d+) replicas with configuration:$`, data.installKogitoExplainabilityServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Explainability has (\d+) pods running within (\d+) minutes$`, data.kogitoExplainabilityHasPodsRunningWithinMinutes)
	ctx.Step(`^Explainability result is available in the Trusty service within (\d+) minutes$`, data.explainabilityResultIsAvailable)
}

func (data *Data) installKogitoExplainabilityServiceWithReplicas(replicas int) error {
	explainability := framework.GetKogitoExplainabilityResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoExplainabilityService(data.Namespace, framework.GetDefaultInstallerType(), &bddtypes.KogitoServiceHolder{KogitoService: explainability})
}

func (data *Data) installKogitoExplainabilityServiceWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	explainability := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoExplainabilityResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, explainability); err != nil {
		return err
	}

	return framework.InstallKogitoExplainabilityService(data.Namespace, framework.GetDefaultInstallerType(), explainability)
}

func (data *Data) kogitoExplainabilityHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoExplainabilityService(data.Namespace, podNb, timeoutInMin)
}

type executionsResponse struct {
	Executions []execution `json:"headers"`
}

type execution struct {
	ExecutionID string `json:"executionId"`
}

func (data *Data) explainabilityResultIsAvailable(timeoutInMin int) error {
	// Retrieve the execution id from the trusty service
	executionsPath := "executions"
	responseContent := "DECISION"
	trustyServiceName := "trusty"
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, trustyServiceName)
	if err != nil {
		return err
	}

	executionsPath = data.ResolveWithScenarioContext(executionsPath)
	requestInfo := framework.NewGETHTTPRequestInfo(uri, executionsPath)
	responseContent = data.ResolveWithScenarioContext(responseContent)
	err = framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return response content '%s'", executionsPath, responseContent), timeoutInMin,
		func() (bool, error) {
			return framework.DoesHTTPResponseContain(data.Namespace, requestInfo, responseContent)
		})

	if err != nil {
		return err
	}

	response, err := framework.ExecuteHTTPRequest(data.Namespace, requestInfo)
	if err != nil {
		return err
	}

	executionsResponse := new(executionsResponse)
	err = getJSON(response, &executionsResponse)
	if err != nil {
		return err
	}

	executionID := executionsResponse.Executions[0].ExecutionID

	// Retrieve explainability result for the given execution ID
	executionsPath = fmt.Sprintf("executions/decisions/%s/explanations/saliencies", executionID)
	responseContent = "SUCCEEDED"

	if err != nil {
		return err
	}
	executionsPath = data.ResolveWithScenarioContext(executionsPath)
	requestInfo = framework.NewGETHTTPRequestInfo(uri, executionsPath)
	responseContent = data.ResolveWithScenarioContext(responseContent)
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return response content '%s'", executionsPath, responseContent), timeoutInMin,
		func() (bool, error) {
			return framework.DoesHTTPResponseContain(data.Namespace, requestInfo, responseContent)
		})
}

func getJSON(r *http.Response, target interface{}) error {
	defer r.Body.Close()

	return json.NewDecoder(r.Body).Decode(target)
}

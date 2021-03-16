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
	"github.com/kiegroup/kogito-operator/core/kogitosupportingservice"
	"strconv"
	"strings"
	"time"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-operator/test/framework"
)

func registerGraphQLSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^GraphQL request on service "([^"]*)" is successful within (\d+) minutes with path "([^"]*)" and query:$`, data.graphqlRequestOnServiceIsSuccessfulWithinMinutesWithPathAndQuery)
	ctx.Step(`^GraphQL request on service "([^"]*)" is successful using access token "([^"]*)" within (\d+) minutes with path "([^"]*)" and query:$`, data.graphqlRequestOnServiceIsSuccessfulUsingAccessTokenWithinMinutesWithPathAndQuery)
	ctx.Step(`^GraphQL request on Data Index service returns ProcessInstances processName "([^"]*)" within (\d+) minutes$`, data.graphqlRequestOnDataIndexReturnsProcessInstancesProcessNameWithinMinutes)
	ctx.Step(`^GraphQL request on Data Index service returns (\d+) (?:instance|instances) of process with name "([^"]*)" within (\d+) minutes$`, data.graphqlRequestOnDataIndexReturnsInstancesOfProcessWithNameWithinMinutes)
	ctx.Step(`^GraphQL request on Data Index service returns Jobs ID "([^"]*)" within (\d+) minutes$`, data.graphqlRequestOnDataIndexReturnsJobsIDWithinMinutes)
}

func (data *Data) graphqlRequestOnServiceIsSuccessfulWithinMinutesWithPathAndQuery(serviceName string, timeoutInMin int, path string, query *godog.DocString) error {
	framework.GetLogger(data.Namespace).Debug("graphqlRequestOnServiceWithPathAndBodyIsSuccessfulWithinMinutes", "service", serviceName, "path", path, "query", query, "timeout", timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	var response interface{}
	return framework.WaitForSuccessfulGraphQLRequest(data.Namespace, uri, path, query.GetContent(), timeoutInMin, response, nil)
}

func (data *Data) graphqlRequestOnServiceIsSuccessfulUsingAccessTokenWithinMinutesWithPathAndQuery(serviceName, accessToken string, timeoutInMin int, path string, query *godog.DocString) error {
	accessToken = data.ResolveWithScenarioContext(accessToken)
	framework.GetLogger(data.Namespace).Debug("graphqlRequestOnServiceIsSuccessfulUsingAccessTokenWithinMinutesWithPathAndQuery", "service", serviceName, "path", path, "query", query, "access token", accessToken, "timeout", timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	var response interface{}
	return framework.WaitForSuccessfulGraphQLRequestUsingAccessToken(data.Namespace, uri, path, query.GetContent(), accessToken, timeoutInMin, response, nil)
}

func (data *Data) graphqlRequestOnDataIndexReturnsProcessInstancesProcessNameWithinMinutes(processName string, timeoutInMin int) error {
	serviceName := kogitosupportingservice.DefaultDataIndexName
	query := getProcessInstancesNameQuery
	path := "graphql"

	framework.GetLogger(data.Namespace).Debug("graphqlProcessNameRequestOnDataIndexIsSuccessfulWithinMinutes", "service", serviceName, "path", path, "query", query, "timeout", timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	response := GraphqlDataIndexProcessInstancesQueryResponse{}
	return framework.WaitForSuccessfulGraphQLRequest(data.Namespace, uri, path, query, timeoutInMin, &response, func(response interface{}) (bool, error) {
		resp := response.(*GraphqlDataIndexProcessInstancesQueryResponse)
		for _, processInstance := range resp.ProcessInstances {
			if processInstance.ProcessName == processName {
				return true, nil
			}
		}
		return false, nil
	})
}

func (data *Data) graphqlRequestOnDataIndexReturnsInstancesOfProcessWithNameWithinMinutes(processInstances int, processName string, timeoutInMin int) error {
	serviceName := kogitosupportingservice.DefaultDataIndexName
	pageSize := 1000
	preProcessedQuery := strings.ReplaceAll(getProcessInstancesIDByNameQuery, "$name", processName)
	preProcessedQuery = strings.ReplaceAll(preProcessedQuery, "$limit", strconv.Itoa(pageSize))
	path := "graphql"

	framework.GetLogger(data.Namespace).Debug("graphqlRequestOnDataIndexReturnsInstancesOfProcessWithNameWithinMinutes", "service", serviceName, "path", path, "query", preProcessedQuery, "timeout", timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	response := GraphqlDataIndexProcessInstancesIDQueryResponse{}
	var allQueriedProcessInstances GraphqlDataIndexProcessInstancesIDQueryResponse

	startTime := time.Now()

	err = framework.WaitForSuccessfulGraphQLRequestUsingPagination(data.Namespace, uri, path, preProcessedQuery, timeoutInMin, pageSize, processInstances, &response,
		func(response interface{}) (bool, error) {
			resp := response.(*GraphqlDataIndexProcessInstancesIDQueryResponse)
			allQueriedProcessInstances.ProcessInstances = append(allQueriedProcessInstances.ProcessInstances, resp.ProcessInstances...)
			return true, nil
		},
		func() (bool, error) {
			queried := len(allQueriedProcessInstances.ProcessInstances)
			framework.GetLogger(data.Namespace).Info("Queried records", "got", queried, "expected", processInstances)
			conditionMet := queried == processInstances
			if !conditionMet { // delete all results so we can start again
				allQueriedProcessInstances.ProcessInstances = nil
			}
			return conditionMet, nil
		})

	duration := time.Since(startTime)
	// TODO include reporting
	framework.GetLogger(data.Namespace).Info(fmt.Sprintf("%d process instances retrieved from Data Index after %s", processInstances, duration))

	return err
}

func (data *Data) graphqlRequestOnDataIndexReturnsJobsIDWithinMinutes(id string, timeoutInMin int) error {
	serviceName := kogitosupportingservice.DefaultDataIndexName
	query := getJobsIDQuery
	path := "graphql"

	framework.GetLogger(data.Namespace).Debug("graphqlRequestOnDataIndexReturnsJobsIDWithinMinutes", "service", serviceName, "path", path, "query", query, "timeout", timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	response := GraphqlDataIndexJobsQueryResponse{}
	return framework.WaitForSuccessfulGraphQLRequest(data.Namespace, uri, path, query, timeoutInMin, &response, func(response interface{}) (bool, error) {
		resp := response.(*GraphqlDataIndexJobsQueryResponse)
		for _, job := range resp.Jobs {
			if job.ID == id {
				return true, nil
			}
		}
		return false, nil
	})
}

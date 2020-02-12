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

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerHTTPSteps register all HTTP steps existing
func registerGraphQLSteps(s *godog.Suite, data *Data) {
	s.Step(`^GraphQL request on service "([^"]*)" is successful within (\d+) minutes with path "([^"]*)" and query:$`, data.graphqlRequestOnServiceWithPathAndBodyIsSuccessfulWithinMinutes)
	s.Step(`^GraphQL request on Data Index service returns ProcessInstances processName "([^"]*)" within (\d+) minutes$`, data.graphqlProcessNameRequestOnDataIndexIsSuccessfulWithinMinutes)
}

func (data *Data) graphqlRequestOnServiceWithPathAndBodyIsSuccessfulWithinMinutes(serviceName string, timeoutInMin int, path string, query *gherkin.DocString) error {
	framework.GetLogger(data.Namespace).Debugf("graphqlRequestOnServiceWithPathAndBodyIsSuccessfulWithinMinutes with service %s, path %s, query %s and timeout %d", serviceName, path, query, timeoutInMin)
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return framework.WaitForSuccessfulGraphQLRequest(data.Namespace, routeURI, path, query.Content, timeoutInMin)
}

func (data *Data) graphqlProcessNameRequestOnDataIndexIsSuccessfulWithinMinutes(processName string, timeoutInMin int) error {
	query := getProcessInstancesNameQuery
	path := "graphql"
	serviceName := infrastructure.DefaultDataIndexName
	framework.GetLogger(data.Namespace).Debugf("graphqlProcessNameRequestOnDataIndexIsSuccessfulWithinMinutes with service %s, path %s, query %s and timeout %d", serviceName, path, query, timeoutInMin)
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	return framework.WaitFor(data.Namespace, fmt.Sprintf("GraphQL query %s on path '%s' to be successful and contain process name '%s'", query, path, processName), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		response := GraphqlDataIndexProcessInstanceQueryResponse{}
		if err := framework.ExecuteGraphQLRequest(data.Namespace, routeURI, path, query, &response); err != nil {
			return false, err
		}
		for _, processInstance := range response.ProcessInstances {
			if processInstance.ProcessName == processName {
				return true, nil
			}
		}
		return false, nil
	})
}

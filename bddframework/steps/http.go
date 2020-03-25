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

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerHTTPSteps(s *godog.Suite, data *Data) {
	s.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" is successful within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes)
	s.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" should return an array of size (\d+) within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes)
	s.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" should contain a string "([^"]*)" within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathShouldContainAstringWithinMinutes)
	s.Step(`^HTTP POST request on service "([^"]*)" with path "([^"]*)" and body:$`, data.httpPostRequestOnServiceWithPathAndBody)
	s.Step(`^HTTP POST request on service "([^"]*)" is successful within (\d+) minutes with path "([^"]*)" and body:$`, data.httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody)
}

func (data *Data) httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes(serviceName, path string, timeoutInMin int) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return framework.WaitForSuccessfulHTTPRequest(data.Namespace, "GET", routeURI, path, "", "", timeoutInMin)
}

func (data *Data) httpPostRequestOnServiceWithPathAndBody(serviceName, path string, body *gherkin.DocString) error {
	framework.GetLogger(data.Namespace).Infof("httpPostRequestOnServiceWithPathAndBody with service %s, path %s and %s bodyContent %s", serviceName, path, body.ContentType, body.Content)
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	if success, err := framework.IsHTTPRequestSuccessful(data.Namespace, "POST", routeURI, path, body.ContentType, body.Content); err != nil {
		return err
	} else if !success {
		return fmt.Errorf("HTTP POST request to path %s was not successful", path)
	}
	return nil
}

func (data *Data) httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody(serviceName string, timeoutInMin int, path string, body *gherkin.DocString) error {
	framework.GetLogger(data.Namespace).Infof("httpPostRequestOnServiceWithPathAndBody with service %s, path %s, %s bodyContent %s and timeout %d", serviceName, path, body.ContentType, body.Content, timeoutInMin)
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return framework.WaitForSuccessfulHTTPRequest(data.Namespace, "POST", routeURI, path, body.ContentType, body.Content, timeoutInMin)
}

func (data *Data) httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes(serviceName, path string, size, timeoutInMin int) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return array of size %d", path, size), timeoutInMin,
		func() (bool, error) {
			return framework.IsHTTPResponseArraySize(data.Namespace, "GET", routeURI, path, "", "", size)
		})
}

func (data *Data) httpGetRequestOnServiceWithPathShouldContainAstringWithinMinutes(serviceName, path, responseContent string, timeoutInMin int) error {
	routeURI, err := framework.WaitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return response content '%s'", path, responseContent), timeoutInMin,
		func() (bool, error) {
			return framework.DoesHTTPResponseContain(data.Namespace, "GET", routeURI, path, "", "", responseContent)
		})
}

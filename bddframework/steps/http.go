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
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

func registerHTTPSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" is successful within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes)
	ctx.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" should return an array of size (\d+) within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes)
	ctx.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" should contain a string "([^"]*)" within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathShouldContainAstringWithinMinutes)
	ctx.Step(`^HTTP POST request on service "([^"]*)" with path "([^"]*)" and body:$`, data.httpPostRequestOnServiceWithPathAndBody)
	ctx.Step(`^HTTP POST request on service "([^"]*)" is successful within (\d+) minutes with path "([^"]*)" and body:$`, data.httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody)
	ctx.Step(`^(\d+) HTTP POST requests using (\d+) threads on service "([^"]*)" with path "([^"]*)" and body:$`, data.httpPostRequestsUsingThreadsOnServiceWithPathAndBody)
	ctx.Step(`^(\d+) HTTP POST requests with report using (\d+) threads on service "([^"]*)" with path "([^"]*)" and body:$`, data.httpPostRequestsWithReportUsingThreadsOnServiceWithPathAndBody)
}

func (data *Data) httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes(serviceName, path string, timeoutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	requestInfo := framework.NewGETHTTPRequestInfo(uri, data.ResolveWithScenarioContext(path))
	return framework.WaitForSuccessfulHTTPRequest(data.Namespace, requestInfo, timeoutInMin)
}

func (data *Data) httpPostRequestOnServiceWithPathAndBody(serviceName, path string, body *godog.DocString) error {
	path = data.ResolveWithScenarioContext(path)
	bodyContent := data.ResolveWithScenarioContext(body.GetContent())
	framework.GetLogger(data.Namespace).Debugf("httpPostRequestOnServiceWithPathAndBody with service %s, path %s and %s bodyContent %s", serviceName, path, body.GetMediaType(), bodyContent)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	requestInfo := framework.NewPOSTHTTPRequestInfo(uri, path, body.GetMediaType(), bodyContent)
	if success, err := framework.IsHTTPRequestSuccessful(data.Namespace, requestInfo); err != nil {
		return err
	} else if !success {
		return fmt.Errorf("HTTP POST request to path %s was not successful", path)
	}
	return nil
}

func (data *Data) httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody(serviceName string, timeoutInMin int, path string, body *godog.DocString) error {
	path = data.ResolveWithScenarioContext(path)
	bodyContent := data.ResolveWithScenarioContext(body.GetContent())
	framework.GetLogger(data.Namespace).Debugf("httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody with service %s, path %s, %s bodyContent %s and timeout %d", serviceName, path, body.GetMediaType(), bodyContent, timeoutInMin)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	requestInfo := framework.NewPOSTHTTPRequestInfo(uri, path, body.GetMediaType(), bodyContent)
	return framework.WaitForSuccessfulHTTPRequest(data.Namespace, requestInfo, timeoutInMin)
}

func (data *Data) httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes(serviceName, path string, size, timeoutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	path = data.ResolveWithScenarioContext(path)
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return array of size %d", path, size), timeoutInMin,
		func() (bool, error) {
			requestInfo := framework.NewGETHTTPRequestInfo(uri, path)
			return framework.IsHTTPResponseArraySize(data.Namespace, requestInfo, size)
		})
}

func (data *Data) httpGetRequestOnServiceWithPathShouldContainAstringWithinMinutes(serviceName, path, responseContent string, timeoutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	path = data.ResolveWithScenarioContext(path)
	requestInfo := framework.NewGETHTTPRequestInfo(uri, path)
	responseContent = data.ResolveWithScenarioContext(responseContent)
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("GET request on path %s to return response content '%s'", path, responseContent), timeoutInMin,
		func() (bool, error) {
			return framework.DoesHTTPResponseContain(data.Namespace, requestInfo, responseContent)
		})
}

func (data *Data) httpPostRequestsUsingThreadsOnServiceWithPathAndBody(requestCount, threadCount int, serviceName, path string, body *godog.DocString) error {
	return executePostRequestsWithOptionalReportingUsingThreadsOnServiceWithPathAndBody(data, requestCount, threadCount, false, serviceName, path, body)
}

func (data *Data) httpPostRequestsWithReportUsingThreadsOnServiceWithPathAndBody(requestCount, threadCount int, serviceName, path string, body *godog.DocString) error {
	return executePostRequestsWithOptionalReportingUsingThreadsOnServiceWithPathAndBody(data, requestCount, threadCount, true, serviceName, path, body)
}

func executePostRequestsWithOptionalReportingUsingThreadsOnServiceWithPathAndBody(data *Data, requestCount int, threadCount int, report bool, serviceName string, path string, body *godog.DocString) error {
	path = data.ResolveWithScenarioContext(path)
	bodyContent := data.ResolveWithScenarioContext(body.GetContent())
	framework.GetLogger(data.Namespace).Infof("httpPostRequestsUsingThreadsOnServiceWithPathAndBody with requests %d, threads %d, report %t, service %s, path %s and %s bodyContent %s", requestCount, threadCount, report, serviceName, path, body.GetMediaType(), bodyContent)
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}

	requestInfo := framework.NewPOSTHTTPRequestInfo(uri, path, body.GetMediaType(), "")

	startTime := time.Now()
	results, err := framework.ExecuteHTTPRequestsInThreads(data.Namespace, requestCount, threadCount, requestInfo)
	duration := time.Since(startTime)

	if err != nil {
		return err
	}

	if report {
		metricName := fmt.Sprintf("%s - %s - %d requests", data.Namespace, data.ScenarioName, requestCount)
		framework.ReportPerformanceMetric(metricName, fmt.Sprintf("%.5f", duration.Seconds()), "s")
	}

	for i := 0; i < threadCount; i++ {
		if result := results[i]; result != framework.HTTPRequestResultSuccess {
			return fmt.Errorf("One or more go routines have failed, see logs for more information")
		}
	}
	return nil
}

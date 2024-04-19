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

package framework

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"strings"
	"sync"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
)

type (
	// HTTPRequestResult represents the success or error of an HTTP request
	HTTPRequestResult string

	// HTTPRequestInfo structure encapsulates all information needed to execute an HTTP request
	HTTPRequestInfo struct {
		HTTPMethod, URI, Path, BodyFormat, BodyContent, Token string
		Unsecure                                              bool
		Headers                                               map[string]string
	}
)

const (
	// HTTPRequestResultSuccess in case of success
	HTTPRequestResultSuccess HTTPRequestResult = "success"
	// HTTPRequestResultError in case of error
	HTTPRequestResultError HTTPRequestResult = "error"
)

// WaitAndRetrieveEndpointURI waits for a route and returns its URI
func WaitAndRetrieveEndpointURI(namespace, serviceName string) (string, error) {
	var uri string
	var err error
	if IsOpenshift() {
		uri, err = GetRouteURI(namespace, serviceName)
	} else {
		uri, err = GetIngressURI(namespace, serviceName)
	}

	if err != nil {
		return "", fmt.Errorf("Error retrieving URI for route %s in namespace %s: %v", serviceName, namespace, err)
	} else if len(uri) <= 0 {
		return "", fmt.Errorf("No URI found for route name %s in namespace %s: %v", serviceName, namespace, err)
	}
	GetLogger(namespace).Debug("Got", "route", uri)
	return uri, nil
}

// WaitForSuccessfulHTTPRequest waits for an HTTP request to be successful
func WaitForSuccessfulHTTPRequest(namespace string, requestInfo HTTPRequestInfo, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("HTTP %s request on path '%s' to be successful", requestInfo.HTTPMethod, requestInfo.Path), timeoutInMin,
		func() (bool, error) {
			return IsHTTPRequestSuccessful(namespace, requestInfo)
		})
}

// WaitForForbiddenHTTPRequest waits for an HTTP request to be unauthorized
func WaitForForbiddenHTTPRequest(namespace string, requestInfo HTTPRequestInfo, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("HTTP %s request on path '%s' to be forbidden", requestInfo.HTTPMethod, requestInfo.Path), timeoutInMin,
		func() (bool, error) {
			return IsHTTPRequestForbidden(namespace, requestInfo)
		})
}

// WaitForFailedHTTPRequest waits for an HTTP request to fail
func WaitForFailedHTTPRequest(namespace string, requestInfo HTTPRequestInfo, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("HTTP %s request on path '%s' to fail", requestInfo.HTTPMethod, requestInfo.Path), timeoutInMin,
		func() (bool, error) {
			return IsHTTPRequestFailed(namespace, requestInfo)
		})
}

// ExecuteHTTPRequest executes an HTTP request
func ExecuteHTTPRequest(namespace string, requestInfo HTTPRequestInfo) (*http.Response, error) {
	// Setup a retry in case the first time it did not work
	retry := 0
	var resp *http.Response
	var err error
	for retry < config.GetHTTPRetryNumber() {
		resp, err = ExecuteHTTPRequestC(createDefaultClient(), namespace, requestInfo)
		if err == nil && checkHTTPResponseSuccessful(resp) {
			return resp, err
		} else if err != nil {
			GetLogger(namespace).Warn("http call was not successful", "error", err)
		} else if resp != nil {
			GetLogger(namespace).Warn("Http call was not successful.", "Response code", resp.StatusCode)
			if resp.StatusCode == 500 {
				// In case of 500, it is server error and we don't need to call again
				return resp, err
			}
		} else {
			GetLogger(namespace).Warn("Http call was not successful. No response available ...")
		}
		GetLogger(namespace).Warn("Retrying in 1 second ...")
		retry++
		time.Sleep(1 * time.Second)
	}
	return resp, err
}

// ExecuteHTTPRequestC executes an HTTP request using a given client
func ExecuteHTTPRequestC(client *http.Client, namespace string, requestInfo HTTPRequestInfo) (*http.Response, error) {
	GetLogger(namespace).Debug("ExecuteHTTPRequest", "method", requestInfo.HTTPMethod, "uri", requestInfo.URI, "path", requestInfo.Path, "bodyFormat", requestInfo.BodyFormat, "bodyContent", requestInfo.BodyContent)

	request, err := http.NewRequest(requestInfo.HTTPMethod, requestInfo.URI+"/"+requestInfo.Path, strings.NewReader(requestInfo.BodyContent))
	if len(requestInfo.BodyContent) > 0 && len(requestInfo.BodyFormat) > 0 {
		switch requestInfo.BodyFormat {
		case "json":
			request.Header.Add("Content-Type", "application/json")
		case "xml":
			request.Header.Add("Content-Type", "application/xml")
		case "x-www-form-urlencoded":
			request.Header.Add("Content-Type", "application/x-www-form-urlencoded")
		case "yaml":
			request.Header.Add("Content-Type", "text/vnd.yaml")
		default:
			return nil, fmt.Errorf("Unknown body format to set into request: %s", requestInfo.BodyFormat)
		}
	}

	if err != nil {
		return nil, err
	}

	addHTTPAuthentication(request, requestInfo)
	if requestInfo.Unsecure {
		client.Transport = &http.Transport{
			TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
		}
	}

	for key, value := range requestInfo.Headers {
		request.Header.Add(key, value)
	}

	return client.Do(request)
}

// ExecuteHTTPRequestWithStringResponse executes an HTTP request and returns a string response in case there is no error
func ExecuteHTTPRequestWithStringResponse(namespace string, requestInfo HTTPRequestInfo) (string, error) {
	httpResponse, err := ExecuteHTTPRequest(namespace, requestInfo)
	if err != nil {
		return "", err
	}
	if !checkHTTPResponseSuccessful(httpResponse) {
		return "", nil
	}
	// Check response
	defer httpResponse.Body.Close()
	buf := new(bytes.Buffer)
	if _, err = buf.ReadFrom(httpResponse.Body); err != nil {
		return "", err
	}
	resultBody := buf.String()

	GetLogger(namespace).Debug("Retrieved", "resdultBody", resultBody)
	return resultBody, nil
}

// ExecuteHTTPRequestWithUnmarshalledResponse executes an HTTP request and returns response unmarshalled into specific structure in case there is no error
func ExecuteHTTPRequestWithUnmarshalledResponse(namespace string, requestInfo HTTPRequestInfo, response interface{}) error {
	resultBody, err := ExecuteHTTPRequestWithStringResponse(namespace, requestInfo)
	if err != nil {
		return err
	}

	if err := json.NewDecoder(strings.NewReader(resultBody)).Decode(response); err != nil {
		return err
	}
	return nil
}

// IsHTTPRequestSuccessful makes and checks whether an http request is successful
func IsHTTPRequestSuccessful(namespace string, requestInfo HTTPRequestInfo) (bool, error) {
	return checkHTTPRequestConditionC(createDefaultClient(), namespace, requestInfo, checkHTTPResponseSuccessful)
}

// IsHTTPRequestForbidden makes and checks whether an http request is unauthorized
func IsHTTPRequestForbidden(namespace string, requestInfo HTTPRequestInfo) (bool, error) {
	return checkHTTPRequestConditionC(createDefaultClient(), namespace, requestInfo, checkHTTPResponseForbidden)
}

// IsHTTPRequestFailed makes and checks whether an http request fails
func IsHTTPRequestFailed(namespace string, requestInfo HTTPRequestInfo) (bool, error) {
	return checkHTTPRequestConditionC(createDefaultClient(), namespace, requestInfo, checkHTTPResponseFailed)
}

// checkHTTPRequestConditionC makes and checks whether an http request matches a condition using a given HTTP client
func checkHTTPRequestConditionC(client *http.Client, namespace string, requestInfo HTTPRequestInfo, condition func(response *http.Response) bool) (bool, error) {
	response, err := ExecuteHTTPRequestC(client, namespace, requestInfo)
	if err != nil {
		return false, err
	}
	if _, err = io.Copy(ioutil.Discard, response.Body); err != nil { // Just read the response to be able to close the connection properly
		return false, err
	}
	defer response.Body.Close()
	GetLogger(namespace).Debug("Got response", "status code", response.StatusCode)
	if !condition(response) {
		GetLogger(namespace).Warn("Request not expected", "status code", response.StatusCode)
		return false, nil
	}

	return true, nil
}

// checkHTTPResponseSuccessful checks the HTTP response is successful
func checkHTTPResponseSuccessful(response *http.Response) bool {
	return response.StatusCode >= 200 && response.StatusCode < 300
}

// checkHTTPResponseForbidden checks the HTTP response is forbidden
func checkHTTPResponseForbidden(response *http.Response) bool {
	return response.StatusCode == 401
}

// checkHTTPResponseFailed checks the HTTP response failed
func checkHTTPResponseFailed(response *http.Response) bool {
	return response.StatusCode >= 404
}

// ExecuteHTTPRequestsInThreads executes given number of requests using given number of threads (Go routines).
// Returns []HTTPRequestResult with the outcome of each thread (HTTPRequestResultSuccess or HTTPRequestResultError).
// Returns error if the desired number of requests cannot be precisely divided to the threads.
// Useful for performance testing.
func ExecuteHTTPRequestsInThreads(namespace string, requestCount, threadCount int, requestInfo HTTPRequestInfo) ([]HTTPRequestResult, error) {
	if requestCount%threadCount != 0 {
		return nil, fmt.Errorf("Cannot precisely divide %d requests to %d threads. Use different numbers", requestCount, threadCount)
	}
	requestPerThread := requestCount / threadCount
	results := make([]HTTPRequestResult, threadCount)
	waitGroup := &sync.WaitGroup{}
	waitGroup.Add(threadCount)

	GetLogger(namespace).Info("Starting request threads")
	startTime := time.Now()

	for threadID := 0; threadID < threadCount; threadID++ {
		client := createDefaultClient()
		go runRequestRoutine(threadID, waitGroup, client, namespace, requestPerThread, requestInfo, results)
	}

	GetLogger(namespace).Info("Waiting for requests to finish")
	waitGroup.Wait()

	duration := time.Since(startTime)
	GetLogger(namespace).Info("requests finished", "requestCount", requestCount, "duration", duration)
	return results, nil
}

func createDefaultClient() *http.Client {
	defaultTransport, ok := http.DefaultTransport.(*http.Transport)
	if !ok {
		panic("DefaultTransport is not of type *http.Transport")
	}
	customTransport := defaultTransport.Clone()
	// Allow executing requests against HTTPS endpoints with insecure certificate
	customTransport.TLSClientConfig = &tls.Config{InsecureSkipVerify: true}
	return &http.Client{Transport: customTransport, Timeout: (time.Duration(10*config.GetLoadFactor()) * time.Second)}
}

func runRequestRoutine(threadID int, waitGroup *sync.WaitGroup, client *http.Client, namespace string, requestPerThread int, requestInfo HTTPRequestInfo, results []HTTPRequestResult) {
	defer waitGroup.Done()
	GetLogger(namespace).Info("Starting Go routine", "threadID", threadID)
	for i := 0; i < requestPerThread; i++ {
		if success, err := checkHTTPRequestConditionC(client, namespace, requestInfo, checkHTTPResponseSuccessful); err != nil {
			GetLogger(namespace).Error(err, "Go routine failed", "threadID", threadID)
			results[threadID] = HTTPRequestResultError
			return
		} else if !success {
			GetLogger(namespace).Error(fmt.Errorf("Go routine #%d - HTTP POST request to path %s was not successful", threadID, requestInfo.Path), "go routine failed")
			results[threadID] = HTTPRequestResultError
			return
		}
	}
	GetLogger(namespace).Info("Go routine finished", "threadID", threadID)
	results[threadID] = HTTPRequestResultSuccess
}

// IsHTTPResponseArraySize makes and checks whether an http request returns an array of a specific size
func IsHTTPResponseArraySize(namespace string, requestInfo HTTPRequestInfo, arraySize int) (bool, error) {
	var httpResponseArray []map[string]interface{}
	err := ExecuteHTTPRequestWithUnmarshalledResponse(namespace, requestInfo, &httpResponseArray)
	if err != nil {
		return false, err
	}

	return len(httpResponseArray) == arraySize, nil
}

// DoesHTTPResponseContain checks whether the response of an http request contains a certain string
func DoesHTTPResponseContain(namespace string, requestInfo HTTPRequestInfo, responseContent string) (bool, error) {
	resultBody, err := ExecuteHTTPRequestWithStringResponse(namespace, requestInfo)
	if err != nil {
		return false, err
	}

	return strings.Contains(resultBody, responseContent), nil
}

// DoesNotHTTPResponseContain checks whether the response of an http request does not contain a certain string
func DoesNotHTTPResponseContain(namespace string, requestInfo HTTPRequestInfo, responseContent string) (bool, error) {
	resultBody, err := ExecuteHTTPRequestWithStringResponse(namespace, requestInfo)
	if err != nil {
		return false, err
	}

	return !strings.Contains(resultBody, responseContent), nil
}

// NewGETHTTPRequestInfo constructor creates a new HTTPRequestInfo struct with the GET HTTP method
func NewGETHTTPRequestInfo(uri, path string) HTTPRequestInfo {
	return HTTPRequestInfo{
		HTTPMethod: "GET",
		URI:        uri,
		Path:       path,
	}
}

// NewPOSTHTTPRequestInfo constructor creates a new HTTPRequestInfo struct with the POST HTTP method
func NewPOSTHTTPRequestInfo(uri, path, bodyFormat, bodyContent string) HTTPRequestInfo {
	return HTTPRequestInfo{
		HTTPMethod:  "POST",
		URI:         uri,
		Path:        path,
		BodyFormat:  bodyFormat,
		BodyContent: bodyContent,
	}
}

// NewPOSTHTTPRequestInfoWithHeaders constructor creates a new HTTPRequestInfo struct with the POST HTTP method and provided headers
func NewPOSTHTTPRequestInfoWithHeaders(uri, path string, headers map[string]string, bodyFormat, bodyContent string) HTTPRequestInfo {
	request := NewPOSTHTTPRequestInfo(uri, path, bodyFormat, bodyContent)
	request.Headers = headers
	return request
}

func addHTTPAuthentication(request *http.Request, requestInfo HTTPRequestInfo) {
	if len(requestInfo.Token) > 0 {
		// Bearer authentication
		request.Header.Add("Authorization", "Bearer "+requestInfo.Token)
	}
}

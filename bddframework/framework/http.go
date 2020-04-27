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

package framework

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"net/http"
	"strings"
	"sync"
	"time"
)

// HTTPRequestResult represents the success or error of an HTTP request
type HTTPRequestResult string

const (
	// HTTPRequestResultSuccess in case of success
	HTTPRequestResultSuccess HTTPRequestResult = "success"
	// HTTPRequestResultError in case of error
	HTTPRequestResultError HTTPRequestResult = "error"
)

// WaitForSuccessfulHTTPRequest waits for an HTTP request to be successful
func WaitForSuccessfulHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, bodyContent string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("HTTP %s request on path '%s' to be successful", httpMethod, path), timeoutInMin,
		func() (bool, error) {
			return IsHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat, bodyContent)
		})
}

// ExecuteHTTPRequest executes an HTTP request
func ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, bodyContent string) (*http.Response, error) {
	return ExecuteHTTPRequestC(&http.Client{}, namespace, httpMethod, uri, path, bodyFormat, bodyContent)
}

// ExecuteHTTPRequestC executes an HTTP request using a given client
func ExecuteHTTPRequestC(client *http.Client, namespace, httpMethod, uri, path, bodyFormat, bodyContent string) (*http.Response, error) {
	GetLogger(namespace).Debugf("ExecuteHTTPRequest %s on uri %s, with path %s, %s bodyContent %s", httpMethod, uri, path, bodyFormat, bodyContent)

	request, err := http.NewRequest(httpMethod, uri+"/"+path, strings.NewReader(bodyContent))
	if len(bodyContent) > 0 && len(bodyFormat) > 0 {
		switch bodyFormat {
		case "json":
			request.Header.Add("Content-Type", "application/json")
		case "xml":
			request.Header.Add("Content-Type", "application/xml")
		default:
			return nil, fmt.Errorf("Unknown body format to set into request: %s", bodyFormat)
		}
	}

	if err != nil {
		return nil, err
	}

	return client.Do(request)
}

// ExecuteHTTPRequestWithStringResponse executes an HTTP request and returns a string response in case there is no error
func ExecuteHTTPRequestWithStringResponse(namespace, httpMethod, uri, path string, bodyFormat, bodyContent string) (string, error) {
	httpResponse, err := ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, bodyContent)
	if err != nil {
		return "", err
	}
	if !checkHTTPResponseSuccessful(namespace, httpResponse) {
		return "", nil
	}
	// Check response
	defer httpResponse.Body.Close()
	buf := new(bytes.Buffer)
	buf.ReadFrom(httpResponse.Body)
	resultBody := buf.String()

	GetLogger(namespace).Debugf("Retrieved body %v", resultBody)
	return resultBody, nil
}

// ExecuteHTTPRequestWithUnmarshalledResponse executes an HTTP request and returns response unmarshalled into specific structure in case there is no error
func ExecuteHTTPRequestWithUnmarshalledResponse(namespace, httpMethod, uri, path string, bodyFormat, bodyContent string, response interface{}) error {
	resultBody, err := ExecuteHTTPRequestWithStringResponse(namespace, httpMethod, uri, path, bodyFormat, bodyContent)
	if err != nil {
		return err
	}

	if err := json.NewDecoder(strings.NewReader(resultBody)).Decode(response); err != nil {
		return err
	}
	return nil
}

// IsHTTPRequestSuccessful makes and checks whether an http request is successful
func IsHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat, bodyContent string) (bool, error) {
	return IsHTTPRequestSuccessfulC(&http.Client{}, namespace, httpMethod, uri, path, bodyFormat, bodyContent)
}

// IsHTTPRequestSuccessfulC makes and checks whether an http request is successful using a given client
func IsHTTPRequestSuccessfulC(client *http.Client, namespace, httpMethod, uri, path, bodyFormat, bodyContent string) (bool, error) {
	response, err := ExecuteHTTPRequestC(client, namespace, httpMethod, uri, path, bodyFormat, bodyContent)
	if err != nil {
		return false, err
	}
	io.Copy(ioutil.Discard, response.Body) // Just read the response to be able to close the connection properly
	defer response.Body.Close()
	return checkHTTPResponseSuccessful(namespace, response), nil
}

// ExecuteHTTPRequestsInThreads executes given number of requests using given number of threads (Go routines).
// Returns []HTTPRequestResult with the outcome of each thread (HTTPRequestResultSuccess or HTTPRequestResultError).
// Returns error if the desired number of requests cannot be precisely divided to the threads.
// Useful for performance testing.
func ExecuteHTTPRequestsInThreads(namespace, httpMethod string, requestCount, threadCount int, routeURI, path, bodyFormat, bodyContent string) ([]HTTPRequestResult, error) {
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
		client := createCustomClient()
		go runRequestRoutine(threadID, waitGroup, client, namespace, httpMethod, requestPerThread, routeURI, path, bodyFormat, bodyContent, results)
	}

	GetLogger(namespace).Info("Waiting for requests to finish")
	waitGroup.Wait()

	duration := time.Since(startTime)
	GetLogger(namespace).Infof("%d requests finished in %s", requestCount, duration)
	return results, nil
}

func createCustomClient() *http.Client {
	defaultTransport, ok := http.DefaultTransport.(*http.Transport)
	if !ok {
		panic("DefaultTransport is not of type *http.Transport")
	}
	customTransport := defaultTransport.Clone()
	client := &http.Client{Transport: customTransport}
	return client
}

func runRequestRoutine(threadID int, waitGroup *sync.WaitGroup, client *http.Client, namespace, httpMethod string, requestPerThread int, routeURI, path, bodyFormat, bodyContent string, results []HTTPRequestResult) {
	defer waitGroup.Done()
	GetLogger(namespace).Infof("Starting Go routine #%d", threadID)
	for i := 0; i < requestPerThread; i++ {
		if success, err := IsHTTPRequestSuccessfulC(client, namespace, httpMethod, routeURI, path, bodyFormat, bodyContent); err != nil {
			GetLogger(namespace).Errorf("Go routine #%d - Failed with error: %v", threadID, err)
			results[threadID] = HTTPRequestResultError
			return
		} else if !success {
			GetLogger(namespace).Errorf("Go routine #%d - HTTP POST request to path %s was not successful", threadID, path)
			results[threadID] = HTTPRequestResultError
			return
		}
	}
	GetLogger(namespace).Infof("Go routine #%d finished", threadID)
	results[threadID] = HTTPRequestResultSuccess
}

// checkHTTPResponseSuccessful checks the HTTP response is successful
func checkHTTPResponseSuccessful(namespace string, response *http.Response) bool {
	GetLogger(namespace).Debugf("Got response status code %d", response.StatusCode)
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		GetLogger(namespace).Warnf("Request not successful. Got status code %d", response.StatusCode)
		return false
	}
	return true
}

// IsHTTPResponseArraySize makes and checks whether an http request returns an array of a specific size
func IsHTTPResponseArraySize(namespace, httpMethod, uri, path string, bodyFormat, bodyContent string, arraySize int) (bool, error) {
	var httpResponseArray []map[string]interface{}
	err := ExecuteHTTPRequestWithUnmarshalledResponse(namespace, httpMethod, uri, path, bodyFormat, bodyContent, &httpResponseArray)
	if err != nil {
		return false, err
	}

	return len(httpResponseArray) == arraySize, nil
}

// DoesHTTPResponseContain checks whether the response of an http request contains a certain string
func DoesHTTPResponseContain(namespace, httpMethod, uri, path string, bodyFormat, bodyContent string, responseContent string) (bool, error) {
	resultBody, err := ExecuteHTTPRequestWithStringResponse(namespace, httpMethod, uri, path, bodyFormat, bodyContent)
	if err != nil {
		return false, err
	}

	return strings.Contains(resultBody, responseContent), nil
}

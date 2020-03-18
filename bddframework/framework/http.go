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
	"net/http"
	"strings"
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

	client := &http.Client{}
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

	GetLogger(namespace).Infof("Retrieved body %v", resultBody)
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
	response, err := ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, bodyContent)
	if err != nil {
		return false, err
	}
	return checkHTTPResponseSuccessful(namespace, response), nil
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

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
	"net/http"
	"strings"
	"time"
)

// WaitForSuccessfulHTTPRequest waits for an HTTP request to be successful
func WaitForSuccessfulHTTPRequest(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader, timeoutInMin int) error {
	return WaitFor(namespace, fmt.Sprintf("HTTP %s request on path '%s' to be successful", httpMethod, path), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		return IsHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat, body)
	})
}

// ExecuteHTTPRequest executes an HTTP request
func ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader) (*http.Response, error) {
	request, err := http.NewRequest(httpMethod, uri+"/"+path, body)
	if body != nil && len(bodyFormat) > 0 {
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

// IsHTTPRequestSuccessful makes and checks whether an http request is successful
func IsHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader) (bool, error) {
	response, err := ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	return CheckHTTPResponseSuccessful(namespace, response), nil
}

// CheckHTTPResponseSuccessful checks the HTTP response is successful
func CheckHTTPResponseSuccessful(namespace string, response *http.Response) bool {
	GetLogger(namespace).Debugf("Got response status code %d", response.StatusCode)
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		GetLogger(namespace).Warnf("Request not successful. Got status code %d", response.StatusCode)
		return false
	}
	return true
}

// IsHTTPResponseArraySize makes and checks whether an http request returns an array of a specific size
func IsHTTPResponseArraySize(namespace, httpMethod, uri, path string, bodyFormat string, body io.Reader, arraySize int) (bool, error) {
	response, err := ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	if !CheckHTTPResponseSuccessful(namespace, response) {
		return false, nil
	}
	// Check response
	defer response.Body.Close()
	buf := new(bytes.Buffer)
	buf.ReadFrom(response.Body)
	resultBody := buf.String()

	values := make([]httpGenericObject, 0)
	if err := json.NewDecoder(strings.NewReader(resultBody)).Decode(&values); err != nil {
		return false, err
	}
	GetLogger(namespace).Infof("Retrieved body %v", resultBody)
	return len(values) == arraySize, nil
}

// DoesHTTPResponseContain checks whether the response of an http request contains a certain string
func DoesHTTPResponseContain(namespace, httpMethod, uri, path string, bodyFormat string, body io.Reader, responseContent string) (bool, error) {
	response, err := ExecuteHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	if !CheckHTTPResponseSuccessful(namespace, response) {
		return false, nil
	}
	// Check response
	defer response.Body.Close()

	buf := new(bytes.Buffer)
	buf.ReadFrom(response.Body)
	text := buf.String()

	return strings.Contains(text, responseContent), nil
}

type httpGenericObject struct {
}

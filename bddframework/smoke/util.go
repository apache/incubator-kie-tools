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

package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil"
	"math/rand"
	"net/http"
	"strings"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"

	"go.uber.org/zap"
)

// GetLogger retrieves the logger for a namespace
func GetLogger(namespace string) *zap.SugaredLogger {
	return logger.GetLogger(fmt.Sprintf("%s - tests", namespace))
}

func randSeq(n int) string {
	randomLetters := []rune("abcdefghijklmnopqrstuvwxyz0123456789")
	b := make([]rune, n)
	for i := range b {
		b[i] = randomLetters[rand.Intn(len(randomLetters))]
	}
	return string(b)
}

func readFromURI(uri string) (string, error) {
	var data []byte
	var err error
	if strings.HasPrefix(uri, "http") {
		resp, err := http.Get(uri)
		if err != nil {
			return "", err
		}
		defer resp.Body.Close()
		data, err = ioutil.ReadAll(resp.Body)
	} else {
		data, err = ioutil.ReadFile(uri)
		if err != nil {
			return "", err
		}
	}
	return string(data), nil
}

func waitFor(namespace, display string, timeout time.Duration, condition func() (bool, error)) error {
	GetLogger(namespace).Infof("Wait %s for %s", timeout.String(), display)

	timeoutChan := time.After(timeout)
	tick := time.NewTicker(timeout / 60)
	defer tick.Stop()

	for {
		select {
		case <-timeoutChan:
			return fmt.Errorf("Timeout waiting for %s", display)
		case <-tick.C:
			running, err := condition()
			if err != nil {
				return err
			}
			if running {
				GetLogger(namespace).Infof("'%s' is successful", display)
				return nil
			}
		}
	}
}

func waitForSuccessfulHTTPRequest(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader, timeoutInMin int) error {
	return waitFor(namespace, fmt.Sprintf("%s request on path '%s' to be successful", httpMethod, path), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		return isHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat, body)
	})
}

func executeHTTPRequest(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader) (*http.Response, error) {
	request, err := http.NewRequest(httpMethod, uri+"/"+path, body)
	if body != nil && bodyFormat != "" {
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

func isHTTPRequestSuccessful(namespace, httpMethod, uri, path, bodyFormat string, body io.Reader) (bool, error) {
	response, err := executeHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	return checkResponseSuccessful(namespace, response), nil
}

func checkResponseSuccessful(namespace string, response *http.Response) bool {
	GetLogger(namespace).Debugf("Got response status code %d", response.StatusCode)
	if response.StatusCode < 200 || response.StatusCode >= 300 {
		GetLogger(namespace).Warnf("Request not successful. Got status code %d", response.StatusCode)
		return false
	}
	return true
}

func isResultArraySize(namespace, httpMethod, uri, path string, bodyFormat string, body io.Reader, arraySize int) (bool, error) {
	response, err := executeHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	if !checkResponseSuccessful(namespace, response) {
		return false, nil
	}
	// Check response
	defer response.Body.Close()
	buf := new(bytes.Buffer)
	buf.ReadFrom(response.Body)
	resultBody := buf.String()

	values := make([]genericObject, 0)
	if err := json.NewDecoder(strings.NewReader(resultBody)).Decode(&values); err != nil {
		return false, err
	}
	GetLogger(namespace).Infof("Retrieved body %v", resultBody)
	return len(values) == arraySize, nil
}

func doesResponseContain(namespace, httpMethod, uri, path string, bodyFormat string, body io.Reader, responseContent string) (bool, error) {
	response, err := executeHTTPRequest(namespace, httpMethod, uri, path, bodyFormat, body)
	if err != nil {
		return false, err
	}
	if !checkResponseSuccessful(namespace, response) {
		return false, nil
	}
	// Check response
	defer response.Body.Close()

	buf := new(bytes.Buffer)
	buf.ReadFrom(response.Body)
	text := buf.String()

	return strings.Contains(text, responseContent), nil
}

type genericObject struct {
}

func getOsMultipleEnv(env1, env2, defaultValue string) string {
	return util.GetOSEnv(env1, util.GetOSEnv(env2, defaultValue))
}

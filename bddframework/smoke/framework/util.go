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
	"fmt"
	"io/ioutil"
	"math/rand"
	"net/http"
	"path/filepath"
	"strings"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"

	"go.uber.org/zap"
)

// GetLogger retrieves the logger for a namespace
func GetLogger(namespace string) *zap.SugaredLogger {
	return logger.GetLogger(fmt.Sprintf("%s - tests", namespace))
}

// GenerateNamespaceName generates a namespace name, taking configuration into account (local or not)
func GenerateNamespaceName() string {
	rand.Seed(time.Now().UnixNano())
	ns := fmt.Sprintf("cucumber-%s", RandSeq(4))
	if getEnvLocalTests() {
		username := getEnvUsername()
		ns = fmt.Sprintf("%s-local-%s", username, ns)
	}
	return ns
}

// RandSeq returns a generated string
func RandSeq(size int) string {
	randomLetters := []rune("abcdefghijklmnopqrstuvwxyz0123456789")
	b := make([]rune, size)
	for i := range b {
		b[i] = randomLetters[rand.Intn(len(randomLetters))]
	}
	return string(b)
}

// ReadFromURI reads string content from given URI (URL or Filesystem)
func ReadFromURI(uri string) (string, error) {
	var data []byte
	if strings.HasPrefix(uri, "http") {
		resp, err := http.Get(uri)
		if err != nil {
			return "", err
		}
		defer resp.Body.Close()
		data, err = ioutil.ReadAll(resp.Body)
	} else {
		// It should be a Filesystem uri
		absPath, err := filepath.Abs(uri)
		data, err = ioutil.ReadFile(absPath)
		if err != nil {
			return "", err
		}
	}
	return string(data), nil
}

// WaitFor waits for a specific condition to be met
func WaitFor(namespace, display string, timeout time.Duration, condition func() (bool, error)) error {
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

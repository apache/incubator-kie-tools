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

package e2e

import (
	"io/ioutil"

	"net/http"
	"testing"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"
	"github.com/stretchr/testify/assert"
)

var (
	verifierLog = logger.GetLogger("verifier")
)

func verifyDroolsQuarkusExample(t *testing.T, kogitoService *v1alpha1.KogitoApp) {
	resp, err := http.Get(kogitoService.Status.Route + "/hello")
	if err != nil {
		verifierLog.Fatalf("Error while trying to get the application endpoint: %s", err)
	}
	assert.Equal(t, 200, resp.StatusCode)

	defer resp.Body.Close()

	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		verifierLog.Fatalf("Error while trying to read application response body: %s", err)
	}
	assert.NotNil(t, body)
	assert.NotEmpty(t, body)
	assert.Contains(t, string(body), "older")
}

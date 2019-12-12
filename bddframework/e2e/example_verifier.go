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
	goctx "context"
	"io/ioutil"
	"strconv"
	"strings"
	"time"

	appsv1 "github.com/openshift/api/apps/v1"
	"k8s.io/apimachinery/pkg/types"

	"net/http"
	"testing"

	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/logger"
	framework "github.com/operator-framework/operator-sdk/pkg/test"
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

func verifyJbpmSpringBootExample(t *testing.T, kogitoService *v1alpha1.KogitoApp) {
	orderContent := strings.NewReader("{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}")
	resp, err := http.Post(kogitoService.Status.Route+"/orders", "application/json", orderContent)
	if err != nil {
		verifierLog.Fatalf("Error while trying to get the application endpoint: %s", err)
	}
	assert.Equal(t, 200, resp.StatusCode)
}

func verifyJbpmQuarkusPersistentExample(t *testing.T, f *framework.Framework, kogitoService *v1alpha1.KogitoApp) {
	// Create an order
	orderContent := strings.NewReader("{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}")
	resp, err := http.Post(kogitoService.Status.Route+"/orders", "application/json", orderContent)
	if err != nil {
		verifierLog.Fatalf("Error while trying to get the application endpoint: %s", err)
	}
	assert.Equal(t, 200, resp.StatusCode)

	// Scale to 0 and back to 1 to spin up new application pod
	scaleKogitoService(f, kogitoService, 0)
	scaleKogitoService(f, kogitoService, 1)

	// Check that order is still available
	resp, err = http.Get(kogitoService.Status.Route + "/orders")
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
	assert.Contains(t, string(body), "\"orderNumber\":\"12345\"")
	assert.Contains(t, string(body), "\"id\"")
}

func scaleKogitoService(f *framework.Framework, kogitoApp *v1alpha1.KogitoApp, replicas int) {
	verifierLog.Infof("Scaling Kogito application %s to %d replicas", kogitoApp.Name, replicas)

	// Load actual Kogito application state
	f.Client.Get(goctx.TODO(), types.NamespacedName{Name: kogitoApp.Name, Namespace: kogitoApp.Namespace}, kogitoApp)

	// Adjust number of replicas
	convertedReplicas := int32(replicas)
	kogitoApp.Spec.Replicas = &convertedReplicas

	// Update Kogito application
	if err := f.Client.Update(goctx.TODO(), kogitoApp); err != nil {
		verifierLog.Fatalf("Error while trying to update Kogito application: %s", err)
	}

	//wait until desired number of replicas are available
	dc := appsv1.DeploymentConfig{}
	waitFor("deployment config to have "+strconv.Itoa(replicas)+" replicas", time.Minute*1, time.Second*1, func() (bool, error) {
		if err := f.Client.Get(goctx.TODO(), types.NamespacedName{Name: kogitoApp.Name, Namespace: kogitoApp.Namespace}, &dc); err != nil {
			return false, err
		}
		return dc.Status.AvailableReplicas == convertedReplicas, nil
	})

	// If scaled to more than 0 make sure that the route is ready (doesn't return 503)
	if dc.Status.AvailableReplicas > 0 {
		waitFor("Kogito application endpoint to become available", time.Minute*1, time.Second*1, func() (bool, error) {
			resp, err := http.Get(kogitoApp.Status.Route)
			if err != nil {
				return false, err
			}
			return resp.StatusCode != 503, nil
		})
	}
}

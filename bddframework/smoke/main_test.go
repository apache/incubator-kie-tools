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
	"fmt"
	"math/rand"
	"path/filepath"
	"strings"
	"time"

	"github.com/DATA-DOG/godog"

	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
)

type Data struct {
	Namespace string
	StartTime time.Time
}

func (data *Data) setUp(interface{}) {
	data.StartTime = time.Now()

	// Define and create namespace
	rand.Seed(time.Now().UnixNano())
	ns := fmt.Sprintf("cucumber-%s", randSeq(4))
	if local := util.GetOSEnv("LOCAL_TESTS", "false"); local == "true" {
		username := util.GetOSEnv("USERNAME", "none")
		ns = fmt.Sprintf("%s-local-%s", username, ns)
	}
	if err := CreateNamespace(ns); err != nil {
		panic(err)
	}

	data.Namespace = ns
}

func (data *Data) tearDown(fn interface{}, err error) {
	if e := DeleteNamespace(data.Namespace); e != nil {
		panic(e)
	}

	endTime := time.Now()
	duration := endTime.Sub(data.StartTime)
	GetLogger(data.Namespace).Infof("Scenario duration = %s", duration.String())
}

// Operator steps
func (data *Data) kogitoOperatorIsDeployed() error {
	// if operator not available, then install via yaml files
	if exists, err := IsKogitoOperatorRunning(data.Namespace); err != nil {
		return fmt.Errorf("Error while trying to retrieve the operator: %v ", err)
	} else if !exists {
		if err := DeployKogitoOperatorFromYaml(data.Namespace); err != nil {
			return fmt.Errorf("Error while deploying operator: %v", err)
		}

		if err := WaitForKogitoOperatorRunning(data.Namespace); err != nil {
			return fmt.Errorf("Error while checking operator running: %v", err)
		}
	}

	return nil
}

func (data *Data) kogitoOperatorIsDeployedWithDependencies() error {
	if err := data.kogitoOperatorIsDeployed(); err != nil {
		return err
	}

	// Install Infinispan
	return InstallCommunityOperator(data.Namespace, "infinispan", "stable")
}

// Jobs service steps
func (data *Data) deployKogitoJobsServiceWithReplicasWithinMinutes(replicas, timeoutInMin int) error {
	if err := DeployKogitoJobsService(data.Namespace, replicas, false); err != nil {
		return err
	}

	return WaitForKogitoJobsService(data.Namespace, replicas, timeoutInMin)
}

func (data *Data) deployKogitoJobsServiceWithPersistenceAndReplicasWithinMinutes(replicas, timeoutInMin int) error {
	if err := DeployKogitoJobsService(data.Namespace, replicas, true); err != nil {
		return err
	}

	return WaitForKogitoJobsService(data.Namespace, replicas, timeoutInMin)
}

func (data *Data) scaleKogitoJobsServiceToPodsWithinMinutes(nbPods, timeoutInMin int) error {
	err := SetKogitoJobsServiceReplicas(data.Namespace, nbPods)
	if err != nil {
		return err
	}
	return WaitForKogitoJobsService(data.Namespace, nbPods, timeoutInMin)
}

// Deploy service steps
func (data *Data) deployQuarkusExampleServiceWithNative(contextDir, native string) error {
	return DeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", false)
}

func (data *Data) deployQuarkusExampleServiceWithPersistenceAndNative(contextDir, native string) error {
	return DeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", true)
}

func (data *Data) deploySpringBootExampleService(contextDir string) error {
	return DeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, false)
}

func (data *Data) deploySpringBootExampleServiceWithPersistence(contextDir string) error {
	return DeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, true)
}

// Build steps
func (data *Data) buildIsCompleteAfterMinutes(buildName string, timeoutInMin int) error {
	return WaitForBuildComplete(data.Namespace, buildName, timeoutInMin)
}

// DeploymentConfig steps
func (data *Data) deploymentConfigHasPodRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	return WaitForDeploymentConfigRunning(data.Namespace, dcName, podNb, timeoutInMin)
}

// Kogito application steps
func (data *Data) scaleKogitoApplicationToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := SetKogitoAppReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return WaitForDeploymentConfigRunning(data.Namespace, name, nbPods, timeoutInMin)
}

// HTTP call steps
func (data *Data) httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes(serviceName, path string, timeoutInMin int) error {
	routeURI, err := waitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return waitForSuccessfulHTTPRequest(data.Namespace, "GET", routeURI, path, "", nil, timeoutInMin)
}

func (data *Data) httpPostRequestOnServiceWithPathAndBody(serviceName, path, bodyFormat, bodyContent string) error {
	GetLogger(data.Namespace).Debugf("httpPostRequestOnServiceWithPathAndBody with service %s, path %s and %s bodyContent %s", serviceName, path, bodyFormat, bodyContent)
	routeURI, err := waitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	if success, err := isHTTPRequestSuccessful(data.Namespace, "POST", routeURI, path, bodyFormat, strings.NewReader(bodyContent)); err != nil {
		return err
	} else if !success {
		return fmt.Errorf("HTTP POST request to path %s was not successful", path)
	}
	return nil
}

func (data *Data) httpPostRequestOnServiceWithPathAndBodyWithinMinutes(serviceName, path, bodyFormat, bodyContent string, timeoutInMin int) error {
	GetLogger(data.Namespace).Debugf("httpPostRequestOnServiceWithPathAndBody with service %s, path %s, %s bodyContent %s and timeout %d", serviceName, path, bodyFormat, bodyContent, timeoutInMin)
	routeURI, err := waitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return waitForSuccessfulHTTPRequest(data.Namespace, "POST", routeURI, path, bodyFormat, strings.NewReader(bodyContent), timeoutInMin)
}

func (data *Data) httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes(serviceName, path string, size, timeoutInMin int) error {
	routeURI, err := waitAndRetrieveRouteURI(data.Namespace, serviceName)
	if err != nil {
		return err
	}
	return waitFor(data.Namespace, fmt.Sprintf("GET request on path %s to return array of size %d", path, size), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		return isResultArraySize(data.Namespace, "GET", routeURI, path, "", nil, size)
	})
}

func FeatureContext(s *godog.Suite) {
	data := &Data{}
	// Create kube client
	initKubeClient()

	s.BeforeScenario(data.setUp)
	s.AfterScenario(data.tearDown)

	// Operator steps
	s.Step(`^Kogito Operator is deployed$`, data.kogitoOperatorIsDeployed)
	s.Step(`^Kogito Operator is deployed with dependencies$`, data.kogitoOperatorIsDeployedWithDependencies)

	// Jobs service steps
	s.Step(`^Deploy Kogito jobs service with (\d+) replicas within (\d+) minutes$`, data.deployKogitoJobsServiceWithReplicasWithinMinutes)
	s.Step(`^Deploy Kogito jobs service with persistence and (\d+) replicas within (\d+) minutes$`, data.deployKogitoJobsServiceWithPersistenceAndReplicasWithinMinutes)
	s.Step(`^Scale Kogito jobs service to (\d+) pods within (\d+) minutes$`, data.scaleKogitoJobsServiceToPodsWithinMinutes)

	// Deploy steps
	s.Step(`^Deploy quarkus example service "([^"]*)" with native "([^"]*)"$`, data.deployQuarkusExampleServiceWithNative)
	s.Step(`^Deploy quarkus example service "([^"]*)" with persistence enabled and native "([^"]*)"$`, data.deployQuarkusExampleServiceWithPersistenceAndNative)
	s.Step(`^Deploy spring boot example service "([^"]*)"$`, data.deploySpringBootExampleService)
	s.Step(`^Deploy spring boot example service "([^"]*)" with persistence enabled$`, data.deploySpringBootExampleServiceWithPersistence)

	// Build steps
	s.Step(`^Build "([^"]*)" is complete after (\d+) minutes$`, data.buildIsCompleteAfterMinutes)

	// DeploymentConfig steps
	s.Step(`^DeploymentConfig "([^"]*)" has (\d+) pod running within (\d+) minutes$`, data.deploymentConfigHasPodRunningWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// HTTP call steps
	s.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" is successful within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathIsSuccessfulWithinMinutes)
	s.Step(`^HTTP GET request on service "([^"]*)" with path "([^"]*)" should return an array of size (\d+) within (\d+) minutes$`, data.httpGetRequestOnServiceWithPathShouldReturnAnArrayofSizeWithinMinutes)
	s.Step(`^HTTP POST request on service "([^"]*)" with path "([^"]*)" and "([^"]*)" body '(.*)'$`, data.httpPostRequestOnServiceWithPathAndBody)
	s.Step(`^HTTP POST request on service "([^"]*)" with path "([^"]*)" and "([^"]*)" body '(.*)' within (\d+) minutes$`, data.httpPostRequestOnServiceWithPathAndBodyWithinMinutes)
}

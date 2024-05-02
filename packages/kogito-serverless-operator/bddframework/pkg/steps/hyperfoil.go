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

package steps

import (
	"errors"
	"fmt"

	"github.com/cucumber/godog"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	hyperfoilv1alpha2 "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api/hyperfoil/v1alpha2"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

const (
	hyperfoilRunContextKey = "hyperfoil-run"
)

func registerHyperfoilSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Hyperfoil Operator is deployed$`, data.hyperfoilOperatorIsDeployed)
	ctx.Step(`^Hyperfoil instance "([^"]*)" is deployed within (\d+) (?:minute|minutes)$`, data.hyperfoilInstanceIsDeployedWithinMinutes)

	ctx.Step(`^Hyperfoil Node scraper is deployed$`, data.hyperfoilNodeScraperIsDeployed)

	ctx.Step(`^Create benchmark on Hyperfoil instance "([^"]*)" within (\d+) (?:minute|minutes) with content:$`, data.createBenchmarkOnHyperfoilInstanceWithinMinutesWithBody)
	ctx.Step(`^Start benchmark "([^"]*)" on Hyperfoil instance "([^"]*)" within (\d+) (?:minute|minutes)$`, data.startBenchmarkOnHyperfoilInstanceWithinMinutes)
	ctx.Step(`^Benchmark run on Hyperfoil instance "([^"]*)" finished within (\d+) (?:minute|minutes)$`, data.benchmarkRunOnHyperfoilInstanceFinishedWithinMinutes)
	ctx.Step(`^Store benchmark statistics of Hyperfoil instance "([^"]*)" as "([^"]*)"$`, data.storeBenchmarkStatisticsOfHyperfoilInstance)
}

func (data *Data) hyperfoilOperatorIsDeployed() error {
	return installers.GetHyperfoilInstaller().Install(data.Namespace)
}

func (data *Data) hyperfoilNodeScraperIsDeployed() error {
	return installers.GetHyperfoilNodeScraperInstaller().Install(data.Namespace)
}

func (data *Data) hyperfoilInstanceIsDeployedWithinMinutes(name string, timeOutInMin int) error {
	hyperfoil := getHyperfoilDefaultResource(name, data.Namespace)

	framework.GetLogger(data.Namespace).Info("Creating Hyperfoil instance", "name", hyperfoil.Name)
	if err := framework.CreateObject(hyperfoil); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, "role", "controller", 1, 5)
}

func (data *Data) createBenchmarkOnHyperfoilInstanceWithinMinutesWithBody(hyperfoilName string, timeOutInMin int, body *godog.DocString) error {
	return data.httpPostRequestOnServiceIsSuccessfulWithinMinutesWithPathAndBody(hyperfoilName, timeOutInMin, "benchmark", body)
}

func (data *Data) startBenchmarkOnHyperfoilInstanceWithinMinutes(benchmarkName, hyperfoilName string, timeOutInMin int) error {
	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, hyperfoilName)
	if err != nil {
		return err
	}

	run := &HyperfoilRun{}
	// Yes, GET request actually triggers benchmark run
	requestInfo := framework.NewGETHTTPRequestInfo(uri, fmt.Sprintf("benchmark/%s/start", benchmarkName))
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("HTTP %s request on path '%s' to be successful", requestInfo.HTTPMethod, requestInfo.Path), timeOutInMin,
		func() (bool, error) {
			err := framework.ExecuteHTTPRequestWithUnmarshalledResponse(data.Namespace, requestInfo, run)
			if err != nil {
				return false, err
			}
			if run != nil {
				// Persist run ID into context, it is expected that one scenario run will operate on one Hyperfoil run
				data.ScenarioContext[hyperfoilRunContextKey] = run.ID
				return true, nil
			}
			return false, err
		})
}

func (data *Data) benchmarkRunOnHyperfoilInstanceFinishedWithinMinutes(hyperfoilName string, timeOutInMin int) error {
	runID := data.ScenarioContext[hyperfoilRunContextKey]
	if len(runID) == 0 {
		return errors.New("Hyperfoil run ID not found. Did you start the benchmark?")
	}

	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, hyperfoilName)
	if err != nil {
		return err
	}

	run := &HyperfoilRun{}
	requestInfo := framework.NewGETHTTPRequestInfo(uri, fmt.Sprintf("run/%s", runID))
	return framework.WaitForOnOpenshift(data.Namespace, fmt.Sprintf("HTTP %s request on path '%s' to be successful", requestInfo.HTTPMethod, requestInfo.Path), timeOutInMin,
		func() (bool, error) {
			err := framework.ExecuteHTTPRequestWithUnmarshalledResponse(data.Namespace, requestInfo, run)
			if err != nil {
				return false, err
			}
			if run != nil && run.Completed {
				return true, nil
			}
			return false, err
		})
}

func (data *Data) storeBenchmarkStatisticsOfHyperfoilInstance(hyperfoilName, benchmarkFileName string) error {
	runID := data.ScenarioContext[hyperfoilRunContextKey]
	if len(runID) == 0 {
		return errors.New("Hyperfoil run ID not found. Did you start the benchmark?")
	}

	uri, err := framework.WaitAndRetrieveEndpointURI(data.Namespace, hyperfoilName)
	if err != nil {
		return err
	}
	requestInfo := framework.NewGETHTTPRequestInfo(uri, fmt.Sprintf("run/%s/stats/all/json", runID))
	stats, err := framework.ExecuteHTTPRequestWithStringResponse(data.Namespace, requestInfo)
	if err != nil {
		return err
	}

	err = framework.CreateFile(config.GetHyperfoilOutputDirectory(), benchmarkFileName, stats)
	return err
}

func getHyperfoilDefaultResource(name, namespace string) *hyperfoilv1alpha2.Hyperfoil {
	hyperfoil := &hyperfoilv1alpha2.Hyperfoil{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Spec: hyperfoilv1alpha2.HyperfoilSpec{
			PreHooks:  []string{},
			PostHooks: []string{},
		},
	}
	if exists, err := framework.IsConfigMapExist(types.NamespacedName{Namespace: namespace, Name: installers.NodeScraperStartConfigMapName}); err != nil {
		framework.GetLogger(namespace).Error(err, "Cannot fetch ConfigMap", "name", name, "namespace", namespace)
	} else if exists {
		hyperfoil.Spec.PreHooks = append(hyperfoil.Spec.PreHooks, installers.NodeScraperStartConfigMapName)
	}
	if exists, err := framework.IsConfigMapExist(types.NamespacedName{Namespace: namespace, Name: installers.NodeScraperStopConfigMapName}); err != nil {
		framework.GetLogger(namespace).Error(err, "Cannot fetch ConfigMap", "name", name, "namespace", namespace)
	} else if exists {
		hyperfoil.Spec.PostHooks = append(hyperfoil.Spec.PostHooks, installers.NodeScraperStopConfigMapName)
	}

	if imageVersion := config.GetHyperfoilControllerImageVersion(); len(imageVersion) > 0 {
		hyperfoil.Spec.Version = imageVersion
	}

	return hyperfoil
}

// HyperfoilRun represents informations about the Hyperfoil run
type HyperfoilRun struct {
	ID        string `json:"id"`
	Completed bool   `json:"completed"`
}

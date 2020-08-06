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

package steps

import (
	"fmt"
	"strings"
	"time"

	"github.com/cucumber/godog"
	appv1alpha1 "github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	imgv1 "github.com/openshift/api/image/v1"
)

// Data contains all data needed by Gherkin steps to run
type Data struct {
	Namespace              string
	StartTime              time.Time
	KogitoExamplesLocation string
	ScenarioName           string
	ScenarioContext        map[string]string
	// Remove this once new Strimzi version with https://github.com/strimzi/strimzi-kafka-operator/issues/3092 fix is released
	stopZookeeperMonitoring chan bool
}

// RegisterAllSteps register all steps available to the test suite
func (data *Data) RegisterAllSteps(ctx *godog.ScenarioContext) {
	registerGitSteps(ctx, data)
	registerGraphQLSteps(ctx, data)
	registerHTTPSteps(ctx, data)
	registerImageRegistrySteps(ctx, data)
	registerInfinispanSteps(ctx, data)
	registerKafkaSteps(ctx, data)
	registerKogitoBuildSteps(ctx, data)
	registerKogitoRuntimeSteps(ctx, data)
	registerKogitoDataIndexServiceSteps(ctx, data)
	registerKogitoInfraSteps(ctx, data)
	registerKogitoJobsServiceSteps(ctx, data)
	registerKogitoManagementConsoleSteps(ctx, data)
	registerKubernetesSteps(ctx, data)
	registerMavenSteps(ctx, data)
	registerOpenShiftSteps(ctx, data)
	registerOperatorSteps(ctx, data)
	registerPrometheusSteps(ctx, data)
	registerProcessSteps(ctx, data)
	registerTaskSteps(ctx, data)
	registerKogitoDeployFilesSteps(ctx, data)
}

// BeforeScenario configure the data before a scenario is launched
func (data *Data) BeforeScenario(scenario *godog.Scenario) error {
	data.StartTime = time.Now()
	data.Namespace = getNamespaceName()
	data.KogitoExamplesLocation = createTemporaryFolder()
	data.ScenarioName = scenario.GetName()
	data.ScenarioContext = map[string]string{}

	var err error
	framework.GetLogger(data.Namespace).Info(fmt.Sprintf("Scenario %s", scenario.GetName()))
	go func() {
		err = framework.StartPodLogCollector(data.Namespace)
	}()
	if err != nil {
		return err
	}

	// Remove this once new Strimzi version with https://github.com/strimzi/strimzi-kafka-operator/issues/3092 fix is released
	go func() {
		respinZookeeperWhenStuck(data)
	}()

	return nil
}

func getNamespaceName() string {
	if namespaceName := config.GetNamespaceName(); len(namespaceName) > 0 {
		return namespaceName
	}
	return framework.GenerateNamespaceName("cucumber")
}

func createTemporaryFolder() string {
	dir, err := framework.CreateTemporaryFolder("kogito-examples")
	if err != nil {
		panic(fmt.Errorf("Error creating new temporary folder: %v", err))
	}
	return dir
}

// AfterScenario executes some actions on data after a scenario is finished
func (data *Data) AfterScenario(scenario *godog.Scenario, err error) error {
	error := framework.OperateOnNamespaceIfExists(data.Namespace, func(namespace string) error {
		if err := framework.StopPodLogCollector(namespace); err != nil {
			framework.GetMainLogger().Errorf("Error stopping log collector on namespace %s: %v", namespace, err)
		}
		if err := framework.FlushLogger(namespace); err != nil {
			framework.GetMainLogger().Errorf("Error flushing running logs for namespace %s: %v", namespace, err)
		}
		if err := framework.BumpEvents(data.Namespace); err != nil {
			framework.GetMainLogger().Errorf("Error bumping events for namespace %s: %v", namespace, err)
		}
		if err := framework.LogKubernetesObjects(data.Namespace, &imgv1.ImageStreamList{}, &appv1alpha1.KogitoRuntimeList{}, &appv1alpha1.KogitoBuildList{}, &appv1alpha1.KogitoDataIndexList{}, &appv1alpha1.KogitoInfraList{}, &appv1alpha1.KogitoJobsServiceList{}, &appv1alpha1.KogitoMgmtConsoleList{}); err != nil {
			framework.GetMainLogger().Errorf("Error logging Kubernetes objects for namespace %s: %v", namespace, err)
		}
		return nil
	})

	handleScenarioResult(data, scenario, err)
	logScenarioDuration(data)
	deleteTemporaryExamplesFolder(data)

	// Remove this once new Strimzi version with https://github.com/strimzi/strimzi-kafka-operator/issues/3092 fix is released
	data.stopZookeeperMonitoring <- true

	if error != nil {
		return error
	}

	return nil
}

// ResolveWithScenarioContext replaces all the variables in the string with their values.
func (data *Data) ResolveWithScenarioContext(str string) string {
	result := str
	for name, value := range data.ScenarioContext {
		result = strings.ReplaceAll(result, "{"+name+"}", value)
	}

	return result
}

func logScenarioDuration(data *Data) {
	endTime := time.Now()
	duration := endTime.Sub(data.StartTime)
	framework.GetLogger(data.Namespace).Infof("Scenario duration: %s", duration.String())
}

func handleScenarioResult(data *Data, scenario *godog.Scenario, err error) {
	newLogFolderName := fmt.Sprintf("%s - %s", strings.ReplaceAll(scenario.GetName(), "/", "_"), data.Namespace)
	if err != nil {
		framework.GetLogger(data.Namespace).Errorf("Error in scenario '%s': %v", scenario.GetName(), err)

		newLogFolderName = "error - " + newLogFolderName
	} else {
		framework.GetLogger(data.Namespace).Infof("Successful scenario '%s'", scenario.GetName())
	}
	err = framework.RenameLogFolder(data.Namespace, newLogFolderName)
	if err != nil {
		framework.GetMainLogger().Errorf("Error while moving log foler for namespace %s. New name is %s. Error = %v", data.Namespace, newLogFolderName, err)
	}
}

func deleteTemporaryExamplesFolder(data *Data) {
	err := framework.DeleteFolder(data.KogitoExamplesLocation)
	if err != nil {
		framework.GetMainLogger().Errorf("Error while deleting temporary examples folder %s: %v", data.KogitoExamplesLocation, err)
	}
}

// Remove this once new Strimzi version with https://github.com/strimzi/strimzi-kafka-operator/issues/3092 fix is released
func respinZookeeperWhenStuck(data *Data) {
	data.stopZookeeperMonitoring = make(chan bool)
	scanningPeriod := time.NewTicker(5 * time.Second)
	defer scanningPeriod.Stop()
	for {
		select {
		case <-data.stopZookeeperMonitoring:
			return
		case <-scanningPeriod.C:
			namespaceExists, err := framework.IsNamespace(data.Namespace)
			if err != nil {
				framework.GetLogger(data.Namespace).Errorf("Error while checking existence of namespace for zookeeper workaround: %v", err)
			} else if namespaceExists {
				pods, err := framework.GetPodsWithLabels(data.Namespace, map[string]string{"app.kubernetes.io/name": "zookeeper"})
				if err != nil {
					framework.GetLogger(data.Namespace).Errorf("Error while getting zookeeper pods for zookeeper workaround: %v", err)
				} else {
					for _, pod := range pods.Items {
						// Ignore possible errors as container may not be initialized yet
						log, _ := framework.GetContainerLog(data.Namespace, pod.GetName(), "zookeeper")
						if strings.Contains(log, "java.io.FileNotFoundException: /tmp/zookeeper/cluster.keystore.p12") {
							// If zookeeper is stuck just respin the pod
							err := framework.DeletePod(&pod)
							if err != nil {
								framework.GetLogger(data.Namespace).Errorf("Error while terminating zookeeper pod %s: %v", pod.GetName(), err)
							}
						}
					}
				}
			}
		}
	}
}

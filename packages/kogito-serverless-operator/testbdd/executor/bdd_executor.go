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

package executor

import (
	"context"
	"fmt"
	"io"
	"os"
	"strings"
	"time"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/testbdd/installers"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/testbdd/steps"

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/colors"
	"github.com/cucumber/messages-go/v16"
	imgv1 "github.com/openshift/api/image/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
	flag "github.com/spf13/pflag"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/gherkin"
	frameworkInstallers "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
	kogitoSteps "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps"
)

const (
	disabledTag    = "@disabled"
	cliTag         = "@cli"
	smokeTag       = "@smoke"
	performanceTag = "@performance"
)

var (
	godogOpts = godog.Options{
		Output:    colors.Colored(os.Stdout),
		Format:    "junit",
		Randomize: time.Now().UTC().UnixNano(),
		Tags:      disabledTag,
	}

	// PreRegisterStepsHook appends hooks to be executed before default steps are registered
	PreRegisterStepsHook func(ctx *godog.ScenarioContext, d *steps.Data)
	// AfterScenarioHook appends hooks to be executed before default AfterScenario phase
	AfterScenarioHook func(scenario *godog.Scenario, d *steps.Data) error
)

func init() {
	config.BindFlags(flag.CommandLine)
	godog.BindCommandLineFlags("godog.", &godogOpts)
}

// ExecuteBDDTests executes BDD tests
func ExecuteBDDTests(beforeTestsExecution func(godogOpts *godog.Options) error) {
	flag.Parse()
	godogOpts.Paths = flag.Args()

	configureTags()
	configureTestOutput()

	if beforeTestsExecution != nil {
		if err := beforeTestsExecution(&godogOpts); err != nil {
			panic(err)
		}
	}

	features, err := gherkin.ParseFeatures(godogOpts.Tags, godogOpts.Paths)
	if err != nil {
		panic(fmt.Errorf("Error parsing features: %v", err))
	}
	if config.IsShowScenarios() || config.IsShowSteps() {
		showScenarios(features, config.IsShowSteps())
	}

	if !config.IsDryRun() {
		if !config.IsCrDeploymentOnly() || gherkin.MatchingFeatureWithTags(cliTag, features) {
			// Check CLI binary is existing if needed
			if exits, err := framework.CheckCliBinaryExist(); err != nil {
				panic(fmt.Errorf("Error trying to get CLI binary %v", err))
			} else if !exits {
				panic("CLI Binary does not exist on specified path")
			}
		}

		status := godog.TestSuite{
			Name:                 "godogs",
			TestSuiteInitializer: initializeTestSuite,
			ScenarioInitializer:  initializeScenario,
			Options:              &godogOpts,
		}.Run()

		os.Exit(status)
	}
	os.Exit(0)
}

func configureTags() {
	if config.IsSmokeTests() {
		// Filter with smoke tag
		appendTag(smokeTag)
	} else if !strings.Contains(godogOpts.Tags, performanceTag) {
		if config.IsPerformanceTests() {
			// Turn on performance tests
			appendTag(performanceTag)
		} else {
			// Turn off performance tests
			appendTag("~" + performanceTag)
		}
	}

	if !strings.Contains(godogOpts.Tags, disabledTag) {
		// Ignore disabled tag
		appendTag("~" + disabledTag)
	}
}

func appendTag(tag string) {
	if len(godogOpts.Tags) > 0 {
		godogOpts.Tags += " && "
	}
	godogOpts.Tags += tag
}

func configureTestOutput() {
	logFolder := framework.GetLogFolder()
	if err := framework.CreateFolder(logFolder); err != nil {
		panic(fmt.Errorf("Error while creating log folder %s: %v", logFolder, err))
	}

	mainLogFile, err := os.Create(fmt.Sprintf("%s/%s", logFolder, "junit.xml"))
	if err != nil {
		panic(fmt.Errorf("Error creating junit file: %v", err))
	}

	godogOpts.Output = io.MultiWriter(godogOpts.Output, mainLogFile)
}

func initializeTestSuite(ctx *godog.TestSuiteContext) {
	// Verify Setup
	if err := framework.CheckSetup(); err != nil {
		panic(err)
	}

	// Initialization of cluster wide resources
	ctx.BeforeSuite(func() {
		if config.IsOperatorProfiling() {
			framework.GetMainLogger().Info("Running testing with profiling")
		}

		monitorOlmNamespace()

		if config.IsOperatorInstalledByOlm() {
			if err := installKogitoOperatorCatalogSource(); err != nil {
				panic(err)
			}
		}
	})

	// Final cleanup once test suite finishes
	ctx.AfterSuite(func() {
		if !config.IsKeepNamespace() {
			if config.IsOperatorProfiling() {
				retrieveProfilingData()
			}

			// Delete all operators created by test suite
			if success := frameworkInstallers.UninstallServicesFromCluster(); !success {
				framework.GetMainLogger().Warn("Some services weren't uninstalled propertly from cluster, see error logs above")
			}
		}

		if config.IsOperatorInstalledByOlm() {
			deleteKogitoOperatorCatalogSource()
		}

		stopOlmNamespaceMonitoring()
	})
}

func initializeScenario(ctx *godog.ScenarioContext) {
	// Register Steps
	kogitoData := &kogitoSteps.Data{}
	data := &steps.Data{Data: kogitoData}

	if PreRegisterStepsHook != nil {
		PreRegisterStepsHook(ctx, data)
	}

	data.RegisterAllSteps(ctx)
	kogitoData.RegisterAllSteps(ctx)

	// Unused for now
	// Log objects
	//if config.UseProductOperator() {
	//	data.RegisterLogsKubernetesObjects(&v1.KogitoRuntimeList{}, &v1.KogitoBuildList{})
	//} else {
	//	data.RegisterLogsKubernetesObjects(&v1beta1.KogitoRuntimeList{}, &v1beta1.KogitoBuildList{}, &v1beta1.KogitoSupportingServiceList{}, &v1beta1.KogitoInfraList{})
	//}

	if config.IsOperatorInstalledByOlm() { // framework.IsOlmInstalled() may be included in the framework to properly resolve this
		data.RegisterLogsKubernetesObjects(&olmapiv1alpha1.ClusterServiceVersionList{})
	}

	if framework.IsOpenshift() {
		data.RegisterLogsKubernetesObjects(&imgv1.ImageStreamList{})
	}

	// Scenario handlers
	ctx.Before(func(ctx context.Context, scenario *messages.Pickle) (context.Context, error) {
		if err := data.BeforeScenario(scenario); err != nil {
			framework.GetLogger(data.Namespace).Error(err, "Error in configuring data for before scenario")
		}
		return ctx, nil
	})
	ctx.After(func(ctx context.Context, scenario *godog.Scenario, err error) (context.Context, error) {

		if AfterScenarioHook != nil {
			if err := AfterScenarioHook(scenario, data); err != nil {
				framework.GetLogger(data.Namespace).Error(err, "Error in executing AfterScenarioHook")
			}
		}

		if err := data.AfterScenario(scenario, err); err != nil {
			framework.GetLogger(data.Namespace).Error(err, "Error in configuring data for After scenario")
		}

		// Namespace should be deleted after all other operations have been done
		if !config.IsKeepNamespace() {
			if success := frameworkInstallers.UninstallServicesFromNamespace(data.Namespace); !success {
				framework.GetMainLogger().Warn("Some services weren't uninstalled propertly from namespace, see error logs above", "namespace", data.Namespace)
			}

			deleteNamespaceIfExists(data.Namespace)
		}

		return ctx, nil
	})

	// Step handlers
	ctx.StepContext().Before(func(ctx context.Context, s *godog.Step) (context.Context, error) {
		framework.GetLogger(data.Namespace).Info("Step", "stepText", s.Text)
		return ctx, nil
	})
	ctx.StepContext().After(func(ctx context.Context, s *godog.Step, status godog.StepResultStatus, err error) (context.Context, error) {
		if err != nil {
			framework.GetLogger(data.Namespace).Error(err, "Error in step", "step", s.Text)
		}
		return ctx, nil
	})
}

func deleteNamespaceIfExists(namespace string) {
	err := framework.OperateOnNamespaceIfExists(namespace, func(namespace string) error {
		framework.GetLogger(namespace).Info("Delete created namespace", "namespace", namespace)
		if e := framework.DeleteNamespace(namespace); e != nil {
			return fmt.Errorf("Error while deleting the namespace: %v", e)
		}
		return nil
	})
	if err != nil {
		framework.GetLogger(namespace).Error(err, "Error while doing operator on namespace")
	}
}

func showScenarios(features []*gherkin.Feature, showSteps bool) {
	mainLogger := framework.GetMainLogger()
	mainLogger.Info("------------------ SHOW SCENARIOS ------------------")
	for _, ft := range features {
		// Placeholders in names are now replaced directly into names for each scenario
		if len(ft.Pickles) > 0 {
			mainLogger.Info(fmt.Sprintf("Feature: %s", ft.Document.Feature.Name))
			for _, scenario := range ft.Pickles {
				mainLogger.Info(fmt.Sprintf("    Scenario: %s", scenario.Name))
				if showSteps {
					for _, step := range scenario.Steps {
						mainLogger.Info(fmt.Sprintf("        Step: %s", step.Text))
					}
				}
			}
		}
	}
	mainLogger.Info("------------------ END SHOW SCENARIOS ------------------")
}

func monitorOlmNamespace() {
	monitorNamespace(framework.GetClusterOperatorNamespace())
}

func monitorNamespace(namespace string) {
	go func() {
		err := framework.StartPodLogCollector(namespace)
		if err != nil {
			framework.GetLogger(namespace).Error(err, "Error starting log collector", "namespace", namespace)
		}
	}()
}

func stopOlmNamespaceMonitoring() {
	stopNamespaceMonitoring(framework.GetClusterOperatorNamespace())
}

func stopNamespaceMonitoring(namespace string) {
	if err := framework.StopPodLogCollector(namespace); err != nil {
		framework.GetMainLogger().Error(err, "Error stopping log collector", "namespace", namespace)
	}
	// Framework uses deprecated v1beta1 events which are deprecated as of v1.25, see https://kubernetes.io/docs/reference/using-api/deprecation-guide/#v1-25
	//if err := framework.BumpEvents(namespace); err != nil {
	//	framework.GetMainLogger().Error(err, "Error bumping events", "namespace", namespace)
	//}
}

// Install cluster wide Kogito operator from OLM
func installKogitoOperatorCatalogSource() error {
	// Create CatalogSource
	if _, err := framework.CreateKogitoOperatorCatalogSource(); err != nil {
		return fmt.Errorf("Error installing custer wide Kogito operator using OLM: %v", err)
	}

	// Wait for the CatalogSource
	if err := framework.WaitForKogitoOperatorCatalogSourceReady(); err != nil {
		return fmt.Errorf("Error while waiting for Kogito operator CatalogSource initialization: %v", err)
	}

	return nil
}

func deleteKogitoOperatorCatalogSource() {
	if err := framework.DeleteKogitoOperatorCatalogSource(); err != nil {
		framework.GetMainLogger().Error(err, "Error deleting Kogito operator CatalogSource")
	}
}

func retrieveProfilingData() {
	framework.GetMainLogger().Info("Retrieve Profiling Data")

	if err := framework.RemoveKogitoOperatorDeployment(installers.SonataFlowNamespace); err != nil {
		framework.GetMainLogger().Error(err, "Unable to delete Kogito Operator Deployment")
		return
	}

	// Apply dataaccess
	if _, err := framework.CreateCommand("oc", "apply", "-f", config.GetOperatorProfilingDataAccessYamlURI()).Execute(); err != nil {
		framework.GetMainLogger().Error(err, "Error while installing Kogito operator from YAML file")
		return
	}

	// Wait for dataaccess pod
	if err := framework.WaitForPodsWithLabel(installers.SonataFlowNamespace, "name", "profiling-data-access", 1, 2); err != nil {
		framework.GetMainLogger().Error(err, "Error while waiting for profiling data access pod")
		return
	}

	// Copy coverage data
	dataFileInContainer := fmt.Sprintf("%s:/data/cover.out", "kogito-operator-profiling-data-access")
	if _, err := framework.CreateCommand("oc", "cp", dataFileInContainer, config.GetOperatorProfilingOutputFileURI(), "-n", installers.SonataFlowNamespace).Execute(); err != nil {
		framework.GetMainLogger().Error(err, "Error while installing Kogito operator from YAML file")
		return
	}
}

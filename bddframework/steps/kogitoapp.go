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
	"path/filepath"
	"strings"

	"github.com/cucumber/godog"
	"github.com/cucumber/godog/gherkin"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/rdumont/assistdog"
	corev1 "k8s.io/api/core/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
)

const (
	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
)

var assist = assistdog.NewDefault()

func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled)$`, data.deployQuarkusExampleServiceWithNative)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and labels$`, data.deployQuarkusExampleServiceWithNativeAndLabels)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and persistence$`, data.deployQuarkusExampleServiceWithNativeAndPersistence)
	s.Step(`^Deploy quarkus example service "([^"]*)" with native (enabled|disabled) and persistence and events$`, data.deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents)
	s.Step(`^Deploy quarkus example service "([^"]*)" with build resources:$`, data.deployQuarkusExampleServiceWithBuildResources)
	s.Step(`^Deploy spring boot example service "([^"]*)"$`, data.deploySpringBootExampleService)
	s.Step(`^Deploy spring boot example service "([^"]*)" with persistence$`, data.deploySpringBootExampleServiceWithPersistence)
	s.Step(`^Create service "([^"]*)"$`, data.createService)
	s.Step(`^Create service "([^"]*)" with runtime resources:$`, data.createServiceWithRuntimeResources)
	s.Step(`^Deploy service from example file "([^"]*)"$`, data.deployServiceFromExampleFile)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)
	s.Step(`^Kogito application "([^"]*)" has pods with runtime resources within (\d+) minutes:$`, data.kogitoApplicationHaveResourcesWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// Logging steps
	s.Step(`^Kogito application "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoApplicationLogContainsTextWithinMinutes)
}

// Deploy service steps
func (data *Data) deployQuarkusExampleServiceWithNative(contextDir, native string) error {
	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, framework.MustParseEnabledDisabled(native), false, false)
	kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndLabels(contextDir, native string, dt *gherkin.DataTable) error {
	labels, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}

	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, framework.MustParseEnabledDisabled(native), false, false)
	kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType
	kogitoApp.Spec.Service.Labels = labels

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistence(contextDir, native string) error {
	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, framework.MustParseEnabledDisabled(native), true, false)
	kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployQuarkusExampleServiceWithNativeAndPersistenceAndEvents(contextDir, native string) error {
	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, framework.MustParseEnabledDisabled(native), true, true)
	kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployQuarkusExampleServiceWithBuildResources(contextDir string, dt *gherkin.DataTable) error {
	resources, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}

	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, false, false, false)
	kogitoApp.Spec.Runtime = v1alpha1.QuarkusRuntimeType
	kogitoApp.Spec.Build.Resources = framework.ToResourceRequirements(resources["requests"], resources["limits"])
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deploySpringBootExampleService(contextDir string) error {
	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, false, false, false)
	kogitoApp.Spec.Runtime = v1alpha1.SpringbootRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deploySpringBootExampleServiceWithPersistence(contextDir string) error {
	kogitoApp := getKogitoAppExamplesStub(data.Namespace, contextDir, false, true, false)
	kogitoApp.Spec.Runtime = v1alpha1.SpringbootRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) createService(serviceName string) error {
	kogitoApp := framework.GetKogitoAppStub(data.Namespace, serviceName)
	kogitoApp.Spec.Runtime = v1alpha1.SpringbootRuntimeType

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) createServiceWithRuntimeResources(serviceName string, dt *gherkin.DataTable) error {
	resources, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}

	kogitoApp := framework.GetKogitoAppStub(data.Namespace, serviceName)
	kogitoApp.Spec.Runtime = v1alpha1.SpringbootRuntimeType
	kogitoApp.Spec.Resources = framework.ToResourceRequirements(resources["requests"], resources["limits"])
	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoApp)
}

func (data *Data) deployServiceFromExampleFile(exampleFile string) error {
	return framework.DeployServiceFromExampleFile(data.Namespace, exampleFile)
}

// DeploymentConfig steps
func (data *Data) kogitoApplicationHasPodsRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	return framework.WaitForDeploymentConfigRunning(data.Namespace, dcName, podNb, timeoutInMin)
}

func (data *Data) kogitoApplicationHaveResourcesWithinMinutes(dcName string, timeoutInMin int, dt *gherkin.DataTable) error {
	resources, err := assist.ParseMap(dt)
	if err != nil {
		return err
	}

	requirements := framework.ToResourceRequirements(resources["requests"], resources["limits"])
	return framework.WaitForPodsToHaveResources(data.Namespace, dcName, requirements, timeoutInMin)
}

// Scale steps
func (data *Data) scaleKogitoApplicationToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetKogitoAppReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForDeploymentConfigRunning(data.Namespace, name, nbPods, timeoutInMin)
}

// Logging steps
func (data *Data) kogitoApplicationLogContainsTextWithinMinutes(dcName, logText string, timeoutInMin int) error {
	return framework.WaitForAllPodsToContainTextInLog(data.Namespace, dcName, logText, timeoutInMin)
}

// Misc methods

// getKogitoAppExampleStub Get basic KogitoApp stub with GIT properties initialized to common Kogito examples
func getKogitoAppExamplesStub(namespace, contextDir string, native, persistence, events bool) *v1alpha1.KogitoApp {
	kogitoApp := framework.GetKogitoAppStub(namespace, filepath.Base(contextDir))

	kogitoApp.Spec.Build.GitSource.URI = config.GetExamplesRepositoryURI()
	kogitoApp.Spec.Build.GitSource.ContextDir = contextDir

	if ref := config.GetExamplesRepositoryRef(); len(ref) > 0 {
		kogitoApp.Spec.Build.GitSource.Reference = ref
	}

	var profiles []string
	if persistence {
		profiles = append(profiles, "persistence")
		kogitoApp.Spec.EnablePersistence = true
	}
	if events {
		profiles = append(profiles, "events")
		kogitoApp.Spec.EnableEvents = true
		kogitoApp.Spec.KogitoServiceSpec.AddEnvironmentVariable("MP_MESSAGING_OUTGOING_KOGITO_PROCESSINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
		kogitoApp.Spec.KogitoServiceSpec.AddEnvironmentVariable("MP_MESSAGING_OUTGOING_KOGITO_USERTASKINSTANCES_EVENTS_BOOTSTRAP_SERVERS", "")
	}

	if len(profiles) > 0 {
		kogitoApp.Spec.Build.AddEnvironmentVariable(mavenArgsAppendEnvVar, "-P"+strings.Join(profiles, ","))
	}

	if native {
		kogitoApp.Spec.Build.Native = native
		// Make sure that enough memory is allocated for builder pod in case of native build
		kogitoApp.Spec.Build.Resources = corev1.ResourceRequirements{
			Requests: corev1.ResourceList{
				v1.ResourceName("memory"): resource.MustParse("4Gi"),
			},
		}
	}

	return kogitoApp
}

// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/pkg/controller/kogitoapp/resource"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

/*
	DataTable for KogitoRuntime:
	| config           | enableEvents      | enabled/disabled          |
	| config           | enablePersistence | enabled/disabled          |
	| infinispan       | username          | developer                 |
	| infinispan       | password          | mypass                    |
	| infinispan       | uri               | external-infinispan:11222 |
	| kafka            | externalURI       | kafka-bootstrap:9092      |
	| kafka            | instance          | external-kafka            |
	| service-label    | labelKey          | labelValue                |
	| deployment-label | labelKey          | labelValue                |
	| runtime-request  | cpu/memory        | value                     |
	| runtime-limit    | cpu/memory        | value                     |
	| runtime-env      | varName           | varValue                  |
*/

const (
	javaOptionsEnvVar = "JAVA_OPTIONS"
)

func registerKogitoRuntimeSteps(ctx *godog.ScenarioContext, data *Data) {
	// Deploy steps
	ctx.Step(`^Deploy (quarkus|springboot) example service "([^"]*)" from runtime registry with configuration:$`, data.deployExampleServiceFromRuntimeRegistryWithConfiguration)

	// Deployment steps
	ctx.Step(`^Kogito Runtime "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoRuntimeHasPodsRunningWithinMinutes)

	// Kogito Runtime steps
	ctx.Step(`^Scale Kogito Runtime "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoRuntimeToPodsWithinMinutes)

	// Logging steps
	ctx.Step(`^Kogito Runtime "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoRuntimeLogContainsTextWithinMinutes)
}

// Deploy service steps
func (data *Data) deployExampleServiceFromRuntimeRegistryWithConfiguration(runtimeType, kogitoApplicationName string, table *godog.Table) error {
	imageTag := data.ScenarioContext[getBuiltRuntimeImageTagContextKey(kogitoApplicationName)]
	kogitoRuntime, err := getKogitoRuntimeExamplesStub(data.Namespace, runtimeType, kogitoApplicationName, imageTag, table)
	if err != nil {
		return err
	}

	addDefaultJavaOptionsIfNotProvided(kogitoRuntime.KogitoService.GetSpec())

	if kogitoRuntime.IsInfinispanUsernameSpecified() && framework.GetDefaultInstallerType() == framework.CRInstallerType {
		// If Infinispan authentication is set and CR installer is used, the Secret holding Infinispan credentials needs to be created and passed to Data index CR.
		if err := framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: kogitoRuntime.Infinispan.Username, passwordSecretKey: kogitoRuntime.Infinispan.Password}); err != nil {
			return err
		}
		kogitoRuntime.KogitoService.(*v1alpha1.KogitoRuntime).Spec.InfinispanProperties.Credentials.SecretName = kogitoExternalInfinispanSecret
		kogitoRuntime.KogitoService.(*v1alpha1.KogitoRuntime).Spec.InfinispanProperties.Credentials.UsernameKey = usernameSecretKey
		kogitoRuntime.KogitoService.(*v1alpha1.KogitoRuntime).Spec.InfinispanProperties.Credentials.PasswordKey = passwordSecretKey
	}

	return framework.DeployRuntimeService(data.Namespace, framework.GetDefaultInstallerType(), kogitoRuntime)
}

// Deployment steps
func (data *Data) kogitoRuntimeHasPodsRunningWithinMinutes(dName string, podNb, timeoutInMin int) error {
	if err := framework.WaitForDeploymentRunning(data.Namespace, dName, podNb, timeoutInMin); err != nil {
		return err
	}

	// Workaround because two pods are created at the same time when adding a Kogito Runtime.
	// We need wait for only one (wait until the wrong one is deleted)
	return framework.WaitForPodsWithLabel(data.Namespace, resource.LabelKeyAppName, dName, podNb, timeoutInMin)
}

// Scale steps
func (data *Data) scaleKogitoRuntimeToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetKogitoRuntimeReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForDeploymentRunning(data.Namespace, name, nbPods, timeoutInMin)
}

// Logging steps
func (data *Data) kogitoRuntimeLogContainsTextWithinMinutes(dName, logText string, timeoutInMin int) error {
	return framework.WaitForAllPodsByDeploymentToContainTextInLog(data.Namespace, dName, logText, timeoutInMin)
}

// Misc methods

// getKogitoRuntimeExamplesStub Get basic KogitoRuntime stub with GIT properties initialized to common Kogito examples
func getKogitoRuntimeExamplesStub(namespace, runtimeType, name, imageTag string, table *godog.Table) (*bddtypes.KogitoServiceHolder, error) {
	kogitoRuntime := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoRuntimeStub(namespace, runtimeType, name, imageTag),
	}

	if err := mappers.MapKogitoServiceTable(table, kogitoRuntime); err != nil {
		return nil, err
	}

	return kogitoRuntime, nil
}

// If JAVA_OPTIONS env variable is not set, it will be set to -Xmx2G so we have more stable resources assignment to test with.
func addDefaultJavaOptionsIfNotProvided(spec v1alpha1.KogitoServiceSpecInterface) {
	javaOptionsProvided := false
	for _, env := range spec.GetEnvs() {
		if env.Name == javaOptionsEnvVar {
			javaOptionsProvided = true
		}
	}

	if !javaOptionsProvided {
		spec.AddEnvironmentVariable(javaOptionsEnvVar, "-Xmx2G")
	}
}

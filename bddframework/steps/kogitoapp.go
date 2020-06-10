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
	"path/filepath"
	"strings"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/config"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"k8s.io/apimachinery/pkg/util/yaml"
)

/*
	DataTable for KogitoApp:
	| config          | native      | true/false                |
	| config          | persistence | true/false                |
	| config          | events      | true/false                |
	| build-env       | varName     | varValue                  |
	| runtime-env     | varName     | varValue                  |
	| label           | labelKey 	| labelValue                |
	| build-request   | cpu/memory  | value                     |
	| build-limit     | cpu/memory  | value                     |
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| infinispan      | username    | developer                 |
	| infinispan      | password    | mypass                    |
	| infinispan      | uri         | external-infinispan:11222 |
	| kafka           | externalURI | external-kafka:9092       |
*/

const (
	mavenArgsAppendEnvVar = "MAVEN_ARGS_APPEND"
	javaOptionsEnvVar     = "JAVA_OPTIONS"

	// DataTable first column
	kogitoAppConfigKey         = "config"
	kogitoAppBuildEnvKey       = "build-env"
	kogitoAppRuntimeEnvKey     = "runtime-env"
	kogitoAppLabelKey          = "label"
	kogitoAppBuildRequestKey   = "build-request"
	kogitoAppBuildLimitKey     = "build-limit"
	kogitoAppRuntimeRequestKey = "runtime-request"
	kogitoAppRuntimeLimitKey   = "runtime-limit"
	kogitoAppInfinispanKey     = "infinispan"
	kogitoAppKafkaKey          = "kafka"

	// DataTable Config second column
	kogitoAppNativeKey      = "native"
	kogitoAppPersistenceKey = "persistence"
	kogitoAppEventsKey      = "events"

	// DataTable Infinispan second column
	kogitoAppInfinispanUsernameKey = "username"
	kogitoAppInfinispanPasswordKey = "password"
	kogitoAppURIKey                = "uri"

	// DataTable Kafka second column
	kogitoAppKafkaExternalURIKey = "externalURI"

	// Infinispan environment variables
	// Quarkus
	quarkusEnvVarInfinispanServerList    = "QUARKUS_INFINISPAN_CLIENT_SERVER_LIST"
	quarkusEnvVarInfinispanUseAuth       = "QUARKUS_INFINISPAN_CLIENT_USE_AUTH"
	quarkusEnvVarInfinispanUser          = "QUARKUS_INFINISPAN_CLIENT_AUTH_USERNAME"
	quarkusEnvVarInfinispanPassword      = "QUARKUS_INFINISPAN_CLIENT_AUTH_PASSWORD"
	quarkusEnvVarInfinispanSaslMechanism = "QUARKUS_INFINISPAN_CLIENT_SASL_MECHANISM"
	// Spring Boot
	springBootEnvVarInfinispanServerList    = "INFINISPAN_REMOTE_SERVER_LIST"
	springBootEnvVarInfinispanUseAuth       = "INFINISPAN_REMOTE_USE_AUTH"
	springBootEnvVarInfinispanUser          = "INFINISPAN_REMOTE_AUTH_USERNAME"
	springBootEnvVarInfinispanPassword      = "INFINISPAN_REMOTE_AUTH_PASSWORD"
	springBootEnvVarInfinispanSaslMechanism = "INFINISPAN_REMOTE_SASL_MECHANISM"

	// Kafka environment variables
	envVarKafkaBootstrapServers = "KAFKA_BOOTSTRAP_SERVERS"
)

func registerKogitoAppSteps(s *godog.Suite, data *Data) {
	// Deploy steps
	s.Step(`^Deploy (quarkus|springboot) example service "([^"]*)" with configuration:$`, data.deployExampleServiceWithConfiguration)
	s.Step(`^Create (quarkus|springboot) service "([^"]*)"$`, data.createService)
	s.Step(`^Create (quarkus|springboot) service "([^"]*)" with configuration:$`, data.createServiceWithConfiguration)
	s.Step(`^Deploy (quarkus|springboot) service from example file "([^"]*)"$`, data.deployServiceFromExampleFile)

	// DeploymentConfig steps
	s.Step(`^Kogito application "([^"]*)" has (\d+) pods running within (\d+) minutes$`, data.kogitoApplicationHasPodsRunningWithinMinutes)
	s.Step(`^Kogito application "([^"]*)" has pods with runtime resources within (\d+) minutes:$`, data.kogitoApplicationHaveResourcesWithinMinutes)

	// Kogito applications steps
	s.Step(`^Scale Kogito application "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleKogitoApplicationToPodsWithinMinutes)

	// Logging steps
	s.Step(`^Kogito application "([^"]*)" log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoApplicationLogContainsTextWithinMinutes)
}

// Deploy service steps

func (data *Data) deployExampleServiceWithConfiguration(runtimeType, contextDir string, table *messages.PickleStepArgument_PickleTable) error {
	kogitoAppHolder, err := getKogitoAppHolder(data.Namespace, runtimeType, filepath.Base(contextDir), table)
	if err != nil {
		return err
	}

	kogitoAppHolder.Spec.Build.GitSource.URI = config.GetExamplesRepositoryURI()
	kogitoAppHolder.Spec.Build.GitSource.ContextDir = contextDir

	if ref := config.GetExamplesRepositoryRef(); len(ref) > 0 {
		kogitoAppHolder.Spec.Build.GitSource.Reference = ref
	}

	if kogitoAppHolder.IsInfinispanUsernameSpecified() {
		// Can be implemented when https://issues.redhat.com/browse/KOGITO-2119 is resolved
		// If Infinispan authentication is set, a secret holding Infinispan credentials needs to be created and KogitoApp env variables set
		//framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: kogitoAppHolder.Infinispan.Username, passwordSecretKey: kogitoAppHolder.Infinispan.Password})
		addInfinispanEnvVars(kogitoAppHolder)
	}

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoAppHolder.KogitoApp)
}

func (data *Data) createService(runtimeType, serviceName string) error {
	return data.createServiceWithConfiguration(runtimeType, serviceName, &messages.PickleStepArgument_PickleTable{})
}

func (data *Data) createServiceWithConfiguration(runtimeType, serviceName string, table *messages.PickleStepArgument_PickleTable) error {
	kogitoAppHolder, err := getKogitoAppHolder(data.Namespace, runtimeType, serviceName, table)
	if err != nil {
		return err
	}

	if kogitoAppHolder.IsInfinispanUsernameSpecified() {
		// Can be implemented when https://issues.redhat.com/browse/KOGITO-2119 is resolved
		// If Infinispan authentication is set, a secret holding Infinispan credentials needs to be created and KogitoApp env variables set
		//framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: kogitoAppHolder.Infinispan.Username, passwordSecretKey: kogitoAppHolder.Infinispan.Password})
		addInfinispanEnvVars(kogitoAppHolder)
	}

	return framework.DeployService(data.Namespace, framework.GetDefaultInstallerType(), kogitoAppHolder.KogitoApp)
}

func (data *Data) deployServiceFromExampleFile(runtimeType, exampleFile string) error {
	return deployServiceFromExampleFile(data.Namespace, runtimeType, exampleFile)
}

// DeploymentConfig steps
func (data *Data) kogitoApplicationHasPodsRunningWithinMinutes(dcName string, podNb, timeoutInMin int) error {
	return framework.WaitForDeploymentConfigRunning(data.Namespace, dcName, podNb, timeoutInMin)
}

func (data *Data) kogitoApplicationHaveResourcesWithinMinutes(dcName string, timeoutInMin int, dt *messages.PickleStepArgument_PickleTable) error {
	_, requirements, err := parseResourceRequirementsTable(dt)

	if err != nil {
		return err
	}

	return framework.WaitForPodsToHaveResources(data.Namespace, dcName, *requirements, timeoutInMin)
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

// DeployServiceFromExampleFile deploy service from example YAML file (example is located in deploy/examples folder)
func deployServiceFromExampleFile(namespace, runtimeType, exampleFile string) error {
	kogitoAppHolder, err := getKogitoAppHolder(namespace, runtimeType, "name-should-be overwritten-from-yaml", &messages.PickleStepArgument_PickleTable{})
	if err != nil {
		return err
	}

	// Apply content from yaml file
	yamlContent, err := getExampleFileContent(exampleFile)
	if err != nil {
		return err
	}
	if err := yaml.NewYAMLOrJSONDecoder(strings.NewReader(yamlContent), len([]byte(yamlContent))).Decode(kogitoAppHolder); err != nil {
		return fmt.Errorf("Error while unmarshalling file: %v ", err)
	}

	// Setup image streams again as KogitoApp has changed
	framework.SetupKogitoAppBuildImageStreams(kogitoAppHolder.KogitoApp)

	return framework.DeployService(namespace, framework.CRInstallerType, kogitoAppHolder.KogitoApp)
}

// getKogitoAppHolder Get basic KogitoApp stub with GIT properties initialized to common Kogito examples
func getKogitoAppHolder(namespace, runtimeType, serviceName string, table *messages.PickleStepArgument_PickleTable) (*framework.KogitoAppHolder, error) {
	kogitoApp := &framework.KogitoAppHolder{
		KogitoApp: framework.GetKogitoAppStub(namespace, runtimeType, serviceName),
	}

	if err := configureKogitoAppFromTable(table, kogitoApp); err != nil {
		return nil, err
	}

	framework.SetupKogitoAppBuildImageStreams(kogitoApp.KogitoApp)

	if kogitoApp.Spec.Runtime != v1alpha1.QuarkusRuntimeType && kogitoApp.Spec.Build.Native {
		return nil, fmt.Errorf(runtimeType + " does not support native build")
	}

	return kogitoApp, nil
}

func addInfinispanEnvVars(kogitoApp *framework.KogitoAppHolder) {
	if kogitoApp.Spec.Runtime == v1alpha1.QuarkusRuntimeType {
		kogitoApp.Spec.AddEnvironmentVariable(quarkusEnvVarInfinispanUseAuth, "true")
		kogitoApp.Spec.AddEnvironmentVariable(quarkusEnvVarInfinispanUser, kogitoApp.Infinispan.Username)
		kogitoApp.Spec.AddEnvironmentVariable(quarkusEnvVarInfinispanPassword, kogitoApp.Infinispan.Password)
		// Can be implemented when https://issues.redhat.com/browse/KOGITO-2119 is resolved
		//kogitoApp.Spec.AddEnvironmentVariableFromSecret(quarkusEnvVarInfinispanUser, kogitoExternalInfinispanSecret, usernameSecretKey)
		//kogitoApp.Spec.AddEnvironmentVariableFromSecret(quarkusEnvVarInfinispanPassword, kogitoExternalInfinispanSecret, passwordSecretKey)
		kogitoApp.Spec.AddEnvironmentVariable(quarkusEnvVarInfinispanSaslMechanism, string(v1alpha1.SASLPlain))
	} else {
		kogitoApp.Spec.AddEnvironmentVariable(springBootEnvVarInfinispanUseAuth, "true")
		kogitoApp.Spec.AddEnvironmentVariable(springBootEnvVarInfinispanUser, kogitoApp.Infinispan.Username)
		kogitoApp.Spec.AddEnvironmentVariable(springBootEnvVarInfinispanPassword, kogitoApp.Infinispan.Password)
		// Can be implemented when https://issues.redhat.com/browse/KOGITO-2119 is resolved
		//kogitoApp.Spec.AddEnvironmentVariableFromSecret(springBootEnvVarInfinispanUser, kogitoExternalInfinispanSecret, usernameSecretKey)
		//kogitoApp.Spec.AddEnvironmentVariableFromSecret(springBootEnvVarInfinispanPassword, kogitoExternalInfinispanSecret, passwordSecretKey)
		kogitoApp.Spec.AddEnvironmentVariable(springBootEnvVarInfinispanSaslMechanism, string(v1alpha1.SASLPlain))
	}
}

func configureKogitoAppFromTable(table *messages.PickleStepArgument_PickleTable, kogitoApp *framework.KogitoAppHolder) error {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 3 {
		return fmt.Errorf("expected table to have exactly three columns")
	}

	var profiles []string

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case kogitoAppConfigKey:
			parseKogitoAppConfigRow(row, kogitoApp, &profiles)

		case kogitoAppBuildEnvKey:
			kogitoApp.Spec.Build.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppRuntimeEnvKey:
			kogitoApp.Spec.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppLabelKey:
			kogitoApp.Spec.Service.Labels[getSecondColumn(row)] = getThirdColumn(row)

		case kogitoAppBuildRequestKey:
			kogitoApp.Spec.Build.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppBuildLimitKey:
			kogitoApp.Spec.Build.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppRuntimeRequestKey:
			kogitoApp.Spec.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppRuntimeLimitKey:
			kogitoApp.Spec.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		case kogitoAppInfinispanKey:
			parseKogitoAppInfinispanRow(row, kogitoApp, &profiles)

		case kogitoAppKafkaKey:
			parseKogitoAppKafkaRow(row, kogitoApp, &profiles)

		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}

	if len(profiles) > 0 {
		kogitoApp.Spec.Build.AddEnvironmentVariable(mavenArgsAppendEnvVar, "-P"+strings.Join(profiles, ","))
	}

	addDefaultJavaOptionsIfNotProvided(kogitoApp.Spec.KogitoServiceSpec)

	return nil
}

func parseKogitoAppConfigRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoApp *framework.KogitoAppHolder, profilesPtr *[]string) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoAppNativeKey:
		native := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if native {
			kogitoApp.Spec.Build.Native = native
			// Make sure that enough memory is allocated for builder pod in case of native build
			kogitoApp.Spec.Build.AddResourceRequest("memory", "4Gi")
		}

	case kogitoAppPersistenceKey:
		persistence := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if persistence {
			*profilesPtr = append(*profilesPtr, "persistence")
			kogitoApp.Spec.EnablePersistence = true
		}

	case kogitoAppEventsKey:
		events := framework.MustParseEnabledDisabled(getThirdColumn(row))
		if events {
			*profilesPtr = append(*profilesPtr, "events")
			kogitoApp.Spec.EnableEvents = true
		}
	}
}

func parseKogitoAppInfinispanRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoApp *framework.KogitoAppHolder, profilesPtr *[]string) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoAppInfinispanUsernameKey:
		kogitoApp.Infinispan.Username = getThirdColumn(row)

	case kogitoAppInfinispanPasswordKey:
		kogitoApp.Infinispan.Password = getThirdColumn(row)

	case kogitoAppURIKey:
		infinispanServerListVariable := quarkusEnvVarInfinispanServerList
		if kogitoApp.Spec.Runtime == v1alpha1.SpringbootRuntimeType {
			infinispanServerListVariable = springBootEnvVarInfinispanServerList
		}
		kogitoApp.Spec.AddEnvironmentVariable(infinispanServerListVariable, getThirdColumn(row))
		*profilesPtr = append(*profilesPtr, "persistence")
	}
}

func parseKogitoAppKafkaRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoApp *framework.KogitoAppHolder, profilesPtr *[]string) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoAppKafkaExternalURIKey:
		kogitoApp.Spec.AddEnvironmentVariable(envVarKafkaBootstrapServers, getThirdColumn(row))
		*profilesPtr = append(*profilesPtr, "events")
	}
}

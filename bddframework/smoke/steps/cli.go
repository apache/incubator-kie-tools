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

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/smoke/framework"
)

// RegisterCliSteps register all CLI steps existing
func registerCliSteps(s *godog.Suite, data *Data) {
	s.Step(`^CLI create namespace$`, data.cliCreateNamespace)
	s.Step(`^CLI use namespace$`, data.cliUseNamespace)

	s.Step(`^CLI install Kogito operator$`, data.cliInstallOperator)
	s.Step(`^CLI install Kogito Data Index with (\d+) replicas$`, data.cliInstallDataIndexWithReplicas)
	s.Step(`^CLI install Kogito Jobs Service with (\d+) replicas$`, data.cliInstallKogitoJobsServiceWithReplicas)
	s.Step(`^CLI install Kogito Jobs Service with persistence and (\d+) replicas$`, data.cliInstallKogitoJobsServiceWithPersistenceAndReplicas)
	s.Step(`^CLI install Kogito Infra Infinispan$`, data.cliInstallKogitoInfraInfinispan)
	s.Step(`^CLI install Kogito Infra Kafka$`, data.cliInstallKogitoInfraKafka)
	s.Step(`^CLI install Kogito Infra Keycloak$`, data.cliInstallKogitoInfraKeycloak)

	s.Step(`^CLI remove Kogito Infra Infinispan$`, data.cliRemoveKogitoInfraInfinispan)
	s.Step(`^CLI remove Kogito Infra Kafka$`, data.cliRemoveKogitoInfraKafka)
	s.Step(`^CLI remove Kogito Infra Keycloak$`, data.cliRemoveKogitoInfraKeycloak)

	s.Step(`^CLI deploy quarkus example service "([^"]*)" with native "([^"]*)"$`, data.cliDeployQuarkusExampleServiceWithNative)
	s.Step(`^CLI deploy quarkus example service "([^"]*)" with persistence enabled and native "([^"]*)"$`, data.cliDeployQuarkusExampleServiceWithPersistenceAndNative)
	s.Step(`^CLI deploy spring boot example service "([^"]*)"$`, data.cliDeploySpringBootExampleService)
	s.Step(`^CLI deploy spring boot example service "([^"]*)" with persistence enabled$`, data.cliDeploySpringBootExampleServiceWithPersistence)
}

// cliCreateNamespace create a namespace with the CLI
func (data *Data) cliCreateNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "new-project", data.Namespace)
	return err
}

// cliUseNamespace use an existing namespace with the CLI
func (data *Data) cliUseNamespace() error {
	_, err := framework.ExecuteCliCommand(data.Namespace, "use-project", data.Namespace)
	return err
}

// cliInstallOperator install the Kogito Operator with the CLI
func (data *Data) cliInstallOperator() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "operator")
	return err
}

func (data *Data) cliDeployQuarkusExampleServiceWithNative(contextDir, native string) error {
	return framework.CliDeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", false)
}

func (data *Data) cliDeployQuarkusExampleServiceWithPersistenceAndNative(contextDir, native string) error {
	return framework.CliDeployQuarkusExample(data.Namespace, filepath.Base(contextDir), contextDir, native == "enabled", true)
}

func (data *Data) cliDeploySpringBootExampleService(contextDir string) error {
	return framework.CliDeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, false)
}

func (data *Data) cliDeploySpringBootExampleServiceWithPersistence(contextDir string) error {
	return framework.CliDeploySpringBootExample(data.Namespace, filepath.Base(contextDir), contextDir, true)
}

func (data *Data) cliInstallDataIndexWithReplicas(replicas int) error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "data-index")
	return err
}

func (data *Data) cliInstallKogitoJobsServiceWithReplicas(replicas int) error {
	return framework.CliInstallKogitoJobsService(data.Namespace, replicas, false)
}

func (data *Data) cliInstallKogitoJobsServiceWithPersistenceAndReplicas(replicas int) error {
	return framework.CliInstallKogitoJobsService(data.Namespace, replicas, true)
}

func (data *Data) cliInstallKogitoInfraInfinispan() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "infinispan")
	return err
}

func (data *Data) cliInstallKogitoInfraKafka() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "kafka")
	return err
}

func (data *Data) cliInstallKogitoInfraKeycloak() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "install", "keycloak")
	return err
}

func (data *Data) cliRemoveKogitoInfraInfinispan() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "remove", "infinispan")
	return err
}

func (data *Data) cliRemoveKogitoInfraKafka() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "remove", "kafka")
	return err
}

func (data *Data) cliRemoveKogitoInfraKeycloak() error {
	_, err := framework.ExecuteCliCommandInNamespace(data.Namespace, "remove", "keycloak")
	return err
}

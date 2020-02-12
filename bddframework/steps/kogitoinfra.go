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
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// registerKogitoInfraSteps register all Kogito Infra steps existing
func registerKogitoInfraSteps(s *godog.Suite, data *Data) {
	s.Step(`^Install Kogito Infra Infinispan$`, data.installKogitoInfraInfinispan)
	s.Step(`^Install Kogito Infra Kafka$`, data.installKogitoInfraKafka)
	s.Step(`^Install Kogito Infra Keycloak$`, data.installKogitoInfraKeycloak)

	s.Step(`^Remove Kogito Infra Infinispan$`, data.removeKogitoInfraInfinispan)
	s.Step(`^Remove Kogito Infra Kafka$`, data.removeKogitoInfraKafka)
	s.Step(`^Remove Kogito Infra Keycloak$`, data.removeKogitoInfraKeycloak)

	s.Step(`^Kogito Infra Infinispan should be running within (\d+) minutes$`, data.kogitoInfraInfinispanShouldBeRunningWithinMinutes)
	s.Step(`^Kogito Infra Kafka should be running within (\d+) minutes$`, data.kogitoInfraKafkaShouldBeRunningWithinMinutes)
	s.Step(`^Kogito Infra Keycloak should be running within (\d+) minutes$`, data.kogitoInfraKeycloakShouldBeRunningWithinMinutes)

	s.Step(`^Kogito Infra Infinispan should NOT be running within (\d+) minutes$`, data.kogitoInfraInfinispanShouldNOTBeRunningWithinMinutes)
	s.Step(`^Kogito Infra Kafka should NOT be running within (\d+) minutes$`, data.kogitoInfraKafkaShouldNOTBeRunningWithinMinutes)
	s.Step(`^Kogito Infra Keycloak should NOT be running within (\d+) minutes$`, data.kogitoInfraKeycloakShouldNOTBeRunningWithinMinutes)
}

func (data *Data) installKogitoInfraInfinispan() error {
	return framework.InstallKogitoInfraInfinispan(data.Namespace)
}

func (data *Data) installKogitoInfraKafka() error {
	return framework.InstallKogitoInfraKafka(data.Namespace)
}

func (data *Data) installKogitoInfraKeycloak() error {
	return framework.InstallKogitoInfraKeycloak(data.Namespace)
}

func (data *Data) removeKogitoInfraInfinispan() error {
	return framework.RemoveKogitoInfraInfinispan(data.Namespace)
}

func (data *Data) removeKogitoInfraKafka() error {
	return framework.RemoveKogitoInfraKafka(data.Namespace)
}

func (data *Data) removeKogitoInfraKeycloak() error {
	return framework.RemoveKogitoInfraKeycloak(data.Namespace)
}

func (data *Data) kogitoInfraInfinispanShouldBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraInfinispan(data.Namespace, true, timeoutInMin)
}

func (data *Data) kogitoInfraKafkaShouldBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraKafka(data.Namespace, true, timeoutInMin)
}

func (data *Data) kogitoInfraKeycloakShouldBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraKeycloak(data.Namespace, true, timeoutInMin)
}

func (data *Data) kogitoInfraInfinispanShouldNOTBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraInfinispan(data.Namespace, false, timeoutInMin)
}

func (data *Data) kogitoInfraKafkaShouldNOTBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraKafka(data.Namespace, false, timeoutInMin)
}

func (data *Data) kogitoInfraKeycloakShouldNOTBeRunningWithinMinutes(timeoutInMin int) error {
	return framework.WaitForKogitoInfraKeycloak(data.Namespace, false, timeoutInMin)
}

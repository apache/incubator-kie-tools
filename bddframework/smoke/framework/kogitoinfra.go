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

package framework

import (
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
)

// InstallKogitoInfraKafka sets kogitoinfra to install Kafka
func InstallKogitoInfraKafka(namespace string) error {
	// Setup Kogitoinfra to true
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKafka().Apply()
	return err
}

// WaitForKogitoInfraKafka waits for kafka to be installed
func WaitForKogitoInfraKafka(namespace string) error {
	return WaitFor(namespace, "kafka is running", 10*time.Minute, func() (bool, error) {
		return IsKogitoInfraKafkaRunning(namespace)
	})
}

// IsKogitoInfraKafkaRunning checks whether Kafka is running from KogitoInfra
func IsKogitoInfraKafkaRunning(namespace string) (bool, error) {
	_, ready, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKafka().Apply()
	return ready, err
}

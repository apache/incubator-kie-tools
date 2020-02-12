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
	"fmt"
	"time"

	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
)

// InstallKogitoInfraInfinispan sets kogitoinfra to install Infinispan
func InstallKogitoInfraInfinispan(namespace string) error {
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithInfinispan().Apply()
	return err
}

// RemoveKogitoInfraInfinispan sets kogitoinfra to remove Infinispan
func RemoveKogitoInfraInfinispan(namespace string) error {
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithoutInfinispan().Apply()
	return err
}

// WaitForKogitoInfraInfinispan waits for Infinispan to be installed
func WaitForKogitoInfraInfinispan(namespace string, shouldRun bool, timeoutInMin int) error {
	return WaitFor(namespace, getWaitRunningMessage("infinispan", shouldRun), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		run, err := IsKogitoInfraInfinispanRunning(namespace)
		if !shouldRun {
			return !run, err
		}
		return run, err
	})
}

// IsKogitoInfraInfinispanRunning checks whether Infinispan is running from KogitoInfra
func IsKogitoInfraInfinispanRunning(namespace string) (bool, error) {
	_, ready, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithInfinispan().Apply()
	return ready, err
}

// InstallKogitoInfraKafka sets kogitoinfra to install Kafka
func InstallKogitoInfraKafka(namespace string) error {
	// Setup Kogitoinfra to true
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKafka().Apply()
	return err
}

// RemoveKogitoInfraKafka sets kogitoinfra to remove Kafka
func RemoveKogitoInfraKafka(namespace string) error {
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithoutKafka().Apply()
	return err
}

// WaitForKogitoInfraKafka waits for Kafka to be installed
func WaitForKogitoInfraKafka(namespace string, shouldRun bool, timeoutInMin int) error {
	return WaitFor(namespace, getWaitRunningMessage("kafka", shouldRun), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		run, err := IsKogitoInfraKafkaRunning(namespace)
		if !shouldRun {
			return !run, err
		}
		return run, err
	})
}

// IsKogitoInfraKafkaRunning checks whether Kafka is running from KogitoInfra
func IsKogitoInfraKafkaRunning(namespace string) (bool, error) {
	_, ready, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKafka().Apply()
	return ready, err
}

// InstallKogitoInfraKeycloak sets kogitoinfra to install Keycloak
func InstallKogitoInfraKeycloak(namespace string) error {
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKeycloak().Apply()
	return err
}

// RemoveKogitoInfraKeycloak sets kogitoinfra to remove Keycloak
func RemoveKogitoInfraKeycloak(namespace string) error {
	_, _, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithoutKeycloak().Apply()
	return err
}

// WaitForKogitoInfraKeycloak waits for Keycloak to be installed
func WaitForKogitoInfraKeycloak(namespace string, shouldRun bool, timeoutInMin int) error {
	return WaitFor(namespace, getWaitRunningMessage("keycloak", shouldRun), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		run, err := IsKogitoInfraKeycloakRunning(namespace)
		if !shouldRun {
			return !run, err
		}
		return run, err
	})
}

// IsKogitoInfraKeycloakRunning checks whether Keycloak is running from KogitoInfra
func IsKogitoInfraKeycloakRunning(namespace string) (bool, error) {
	_, ready, err := infrastructure.EnsureKogitoInfra(namespace, kubeClient).WithKeycloak().Apply()
	return ready, err
}

func getWaitRunningMessage(component string, shouldRun bool) string {
	msg := "running"
	if !shouldRun {
		msg = fmt.Sprintf("not %s", msg)
	}
	return fmt.Sprintf("%s is %s", component, msg)
}

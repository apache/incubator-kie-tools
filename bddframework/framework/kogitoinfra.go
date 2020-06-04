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
	"strings"

	"github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
)

// KogitoInfraComponent defines the KogitoInfra component
type KogitoInfraComponent struct {
	name string
}

const (
	infinispanKey = "infinispan"
	kafkaKey      = "kafka"
	keycloakKey   = "keycloak"
)

var (
	// InfinispanKogitoInfraComponent is for infinispan
	InfinispanKogitoInfraComponent = KogitoInfraComponent{name: infinispanKey}
	// KafkaKogitoInfraComponent is for kafka
	KafkaKogitoInfraComponent = KogitoInfraComponent{name: kafkaKey}
	// KeycloakKogitoInfraComponent is for keycloak
	KeycloakKogitoInfraComponent = KogitoInfraComponent{name: keycloakKey}
)

// ParseKogitoInfraComponent retrieves the correspoding KogitoInfraComponent
func ParseKogitoInfraComponent(component string) KogitoInfraComponent {
	switch cmp := strings.ToLower(component); cmp {
	case infinispanKey:
		return InfinispanKogitoInfraComponent
	case kafkaKey:
		return KafkaKogitoInfraComponent
	case keycloakKey:
		return KeycloakKogitoInfraComponent
	default:
		return KogitoInfraComponent{name: cmp}
	}
}

// InstallKogitoInfraComponent installs the desired component with the given installer type
func InstallKogitoInfraComponent(namespace string, installerType InstallerType, component KogitoInfraComponent) error {
	GetLogger(namespace).Infof("%s install Kogito Infra Component %s", installerType, component.name)
	switch installerType {
	case CLIInstallerType:
		return cliInstallKogitoInfraComponent(namespace, component)
	case CRInstallerType:
		return crInstallKogitoInfraComponent(namespace, component)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crInstallKogitoInfraComponent(namespace string, component KogitoInfraComponent) error {
	ensureComponent := infrastructure.EnsureKogitoInfra(namespace, kubeClient)
	switch component {
	case InfinispanKogitoInfraComponent:
		ensureComponent = ensureComponent.WithInfinispan()
	case KafkaKogitoInfraComponent:
		ensureComponent = ensureComponent.WithKafka()
	case KeycloakKogitoInfraComponent:
		ensureComponent = ensureComponent.WithKeycloak()
	}
	_, _, err := ensureComponent.Apply()
	return err
}

func cliInstallKogitoInfraComponent(namespace string, component KogitoInfraComponent) error {
	_, err := ExecuteCliCommandInNamespace(namespace, "install", component.name)
	return err
}

// RemoveKogitoInfraComponent removes the desired component with the given installer type
func RemoveKogitoInfraComponent(namespace string, installerType InstallerType, component KogitoInfraComponent) error {
	GetLogger(namespace).Infof("%s remove Kogito Infra Component %s", installerType, component.name)
	switch installerType {
	case CLIInstallerType:
		return cliRemoveKogitoInfraComponent(namespace, component)
	case CRInstallerType:
		return crRemoveKogitoInfraComponent(namespace, component)
	default:
		panic(fmt.Errorf("Unknown installer type %s", installerType))
	}
}

func crRemoveKogitoInfraComponent(namespace string, component KogitoInfraComponent) error {
	ensureComponent := infrastructure.EnsureKogitoInfra(namespace, kubeClient)
	switch component {
	case InfinispanKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutInfinispan()
	case KafkaKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutKafka()
	case KeycloakKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutKeycloak()
	}
	_, _, err := ensureComponent.Apply()
	return err
}

func cliRemoveKogitoInfraComponent(namespace string, component KogitoInfraComponent) error {
	_, err := ExecuteCliCommandInNamespace(namespace, "remove", component.name)
	return err
}

// WaitForKogitoInfraComponent waits for the given component to be installed or removed
func WaitForKogitoInfraComponent(namespace string, component KogitoInfraComponent, shouldRun bool, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, getWaitRunningMessage(component.name, shouldRun), timeoutInMin,
		func() (bool, error) {
			if shouldRun {
				return IsKogitoInfraComponentRunning(namespace, component)
			}
			return IsKogitoInfraComponentTerminated(namespace, component)
		})
}

// IsKogitoInfraComponentRunning checks whether the given component is running from KogitoInfra
func IsKogitoInfraComponentRunning(namespace string, component KogitoInfraComponent) (bool, error) {
	ensureComponent := infrastructure.EnsureKogitoInfra(namespace, kubeClient)
	switch component {
	case InfinispanKogitoInfraComponent:
		ensureComponent = ensureComponent.WithInfinispan()
	case KafkaKogitoInfraComponent:
		ensureComponent = ensureComponent.WithKafka()
	case KeycloakKogitoInfraComponent:
		ensureComponent = ensureComponent.WithKeycloak()
	}
	_, ready, err := ensureComponent.Apply()
	return ready, err
}

// IsKogitoInfraComponentTerminated checks whether the given component is terminated from KogitoInfra
func IsKogitoInfraComponentTerminated(namespace string, component KogitoInfraComponent) (bool, error) {
	ensureComponent := infrastructure.EnsureKogitoInfra(namespace, kubeClient)
	switch component {
	case InfinispanKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutInfinispan()
	case KafkaKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutKafka()
	case KeycloakKogitoInfraComponent:
		ensureComponent = ensureComponent.WithoutKeycloak()
	}
	_, ready, err := ensureComponent.Apply()
	return ready, err
}

func getWaitRunningMessage(component string, shouldRun bool) string {
	msg := "running"
	if !shouldRun {
		msg = fmt.Sprintf("not %s", msg)
	}
	return fmt.Sprintf("%s is %s", component, msg)
}

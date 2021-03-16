// Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package installers

import (
	"errors"
	"fmt"
	"strings"

	"github.com/kiegroup/kogito-operator/api/v1beta1"
	"github.com/kiegroup/kogito-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-operator/test/config"
	"github.com/kiegroup/kogito-operator/test/framework"
	"github.com/kiegroup/kogito-operator/version"
)

var (
	// kogitoYamlClusterInstaller installs Kogito operator cluster wide using YAMLs
	kogitoYamlClusterInstaller = YamlClusterWideServiceInstaller{
		installClusterYaml:               installKogitoUsingYaml,
		installationNamespace:            kogitoNamespace,
		waitForClusterYamlServiceRunning: waitForKogitoOperatorUsingYamlRunning,
		getAllClusterYamlCrsInNamespace:  getKogitoCrsInNamespace,
		uninstallClusterYaml:             uninstallKogitoUsingYaml,
		clusterYamlServiceName:           kogitoServiceName,
		cleanupClusterYamlCrsInNamespace: cleanupKogitoCrsInNamespace,
	}

	// kogitoOlmNamespacedInstaller installs Kogito in the namespace using OLM
	kogitoOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		subscriptionName:                   kogitoOperatorSubscriptionName,
		channel:                            kogitoOperatorSubscriptionChannel,
		catalog:                            framework.CustomKogitoOperatorCatalog,
		installationTimeoutInMinutes:       5,
		getAllNamespacedOlmCrsInNamespace:  getKogitoCrsInNamespace,
		cleanupNamespacedOlmCrsInNamespace: cleanupKogitoCrsInNamespace,
	}

	// kogitoOlmClusterWideInstaller installs Kogito cluster wide using OLM
	kogitoOlmClusterWideInstaller = OlmClusterWideServiceInstaller{
		subscriptionName:                    kogitoOperatorSubscriptionName,
		channel:                             kogitoOperatorSubscriptionChannel,
		catalog:                             framework.CustomKogitoOperatorCatalog,
		installationTimeoutInMinutes:        5,
		getAllClusterWideOlmCrsInNamespace:  getKogitoCrsInNamespace,
		cleanupClusterWideOlmCrsInNamespace: cleanupKogitoCrsInNamespace,
	}

	kogitoNamespace   = "kogito-operator-system"
	kogitoServiceName = "Kogito operator"

	kogitoOperatorSubscriptionName    = "kogito-operator"
	kogitoOperatorSubscriptionChannel = "alpha"
)

// GetKogitoInstaller returns Kogito installer
func GetKogitoInstaller() (ServiceInstaller, error) {
	if config.IsOperatorInstalledByYaml() {
		if config.IsOperatorNamespaced() {
			return nil, errors.New("Installing namespace scoped Kogito operator using YAML files is not supported")
		}
		return &kogitoYamlClusterInstaller, nil
	}

	if config.IsOperatorInstalledByOlm() {
		if config.IsOperatorNamespaced() {
			return &kogitoOlmNamespacedInstaller, nil
		}
		return &kogitoOlmClusterWideInstaller, nil
	}

	return nil, errors.New("No Kogito operator installer available for provided configuration")
}

func installKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Installing Kogito operator")

	yamlContent, err := framework.ReadFromURI(config.GetOperatorYamlURI())
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while reading kogito-operator.yaml file")
		return err
	}

	yamlContent = strings.ReplaceAll(yamlContent, "quay.io/kiegroup/kogito-operator:"+version.Version, framework.GetOperatorImageNameAndTag())

	tempFilePath, err := framework.CreateTemporaryFile("kogito-operator*.yaml", yamlContent)
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while storing adjusted YAML content to temporary file")
		return err
	}

	_, err = framework.CreateCommand("oc", "apply", "-f", tempFilePath).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while installing Kogito operator from YAML file")
		return err
	}

	return nil
}

func waitForKogitoOperatorUsingYamlRunning() error {
	return framework.WaitForKogitoOperatorRunning(kogitoNamespace)
}

func uninstallKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Uninstalling Kogito operator")

	output, err := framework.CreateCommand("oc", "delete", "-f", config.GetOperatorYamlURI(), "--timeout=30s").Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting Kogito operator failed, output: %s", output))
		return err
	}

	return nil
}

func getKogitoCrsInNamespace(namespace string) ([]kubernetes.ResourceObject, error) {
	crs := []kubernetes.ResourceObject{}

	kogitoRuntimes := &v1beta1.KogitoRuntimeList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoRuntimes); err != nil {
		return nil, err
	}
	for i := range kogitoRuntimes.Items {
		crs = append(crs, &kogitoRuntimes.Items[i])
	}

	kogitoBuilds := &v1beta1.KogitoBuildList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoBuilds); err != nil {
		return nil, err
	}
	for i := range kogitoBuilds.Items {
		crs = append(crs, &kogitoBuilds.Items[i])
	}

	kogitoSupportingServices := &v1beta1.KogitoSupportingServiceList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoSupportingServices); err != nil {
		return nil, err
	}
	for i := range kogitoSupportingServices.Items {
		crs = append(crs, &kogitoSupportingServices.Items[i])
	}

	kogitoInfras := &v1beta1.KogitoInfraList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoInfras); err != nil {
		return nil, err
	}
	for i := range kogitoInfras.Items {
		crs = append(crs, &kogitoInfras.Items[i])
	}

	return crs, nil
}

func cleanupKogitoCrsInNamespace(namespace string) bool {
	crs, err := getKogitoCrsInNamespace(namespace)
	if err != nil {
		framework.GetLogger(namespace).Error(err, "Error getting Kogito CRs.")
		return false
	}

	for _, cr := range crs {
		if err := framework.DeleteObject(cr); err != nil {
			framework.GetLogger(namespace).Error(err, "Error deleting Kogito CR.", "CR name", cr.GetName())
			return false
		}
	}
	return true
}

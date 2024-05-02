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

package installers

import (
	"errors"
	"fmt"
	"regexp"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/workflowdef"
	srvframework "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/testbdd/framework"
)

const defaultOperatorImage = "quay.io/kiegroup/kogito-serverless-operator"

var (
	// sonataFlowYamlClusterInstaller installs SonataFlow operator cluster wide using YAMLs
	sonataFlowYamlClusterInstaller = installers.YamlClusterWideServiceInstaller{
		InstallClusterYaml:               installSonataFlowUsingYaml,
		InstallationNamespace:            SonataFlowNamespace,
		WaitForClusterYamlServiceRunning: waitForSonataFlowOperatorUsingYamlRunning,
		GetAllClusterYamlCrsInNamespace:  getSonataFlowCrsInNamespace,
		UninstallClusterYaml:             uninstallSonataFlowUsingYaml,
		ClusterYamlServiceName:           sonataFlowServiceName,
		CleanupClusterYamlCrsInNamespace: cleanupSonataFlowCrsInNamespace,
	}

	// sonataFlowCustomOlmClusterWideInstaller installs SonataFlow cluster wide using OLM with custom catalog
	sonataFlowCustomOlmClusterWideInstaller = installers.OlmClusterWideServiceInstaller{
		SubscriptionName:                    sonataFlowOperatorSubscriptionName,
		Channel:                             sonataFlowOperatorSubscriptionChannel,
		Catalog:                             framework.GetCustomKogitoOperatorCatalog,
		InstallationTimeoutInMinutes:        5,
		GetAllClusterWideOlmCrsInNamespace:  getSonataFlowCrsInNamespace,
		CleanupClusterWideOlmCrsInNamespace: cleanupSonataFlowCrsInNamespace,
	}

	// sonataFlowOlmClusterWideInstaller installs SonataFlow cluster wide using OLM with community catalog
	sonataFlowOlmClusterWideInstaller = installers.OlmClusterWideServiceInstaller{
		SubscriptionName:                    sonataFlowOperatorSubscriptionName,
		Channel:                             sonataFlowOperatorSubscriptionChannel,
		Catalog:                             framework.GetCommunityCatalog,
		InstallationTimeoutInMinutes:        5,
		GetAllClusterWideOlmCrsInNamespace:  getSonataFlowCrsInNamespace,
		CleanupClusterWideOlmCrsInNamespace: cleanupSonataFlowCrsInNamespace,
	}

	// SonataFlowNamespace is the SonataFlow namespace for yaml cluster-wide deployment
	SonataFlowNamespace   = "sonataflow-operator-system"
	sonataFlowServiceName = "SonataFlow operator"

	sonataFlowOperatorSubscriptionName    = "sonataflow-operator"
	sonataFlowOperatorSubscriptionChannel = "alpha"
)

// GetSonataFlowInstaller returns SonataFlow installer
func GetSonataFlowInstaller() (installers.ServiceInstaller, error) {
	// If user doesn't pass SonataFlow operator image then use community OLM catalog to install operator
	if len(config.GetOperatorImageTag()) == 0 {
		framework.GetMainLogger().Info("Installing SonataFlow operator using community catalog.")
		return &sonataFlowOlmClusterWideInstaller, nil
	}

	if config.IsOperatorInstalledByYaml() || config.IsOperatorProfiling() {
		return &sonataFlowYamlClusterInstaller, nil
	}

	if config.IsOperatorInstalledByOlm() {
		return &sonataFlowCustomOlmClusterWideInstaller, nil
	}

	return nil, errors.New("no SonataFlow operator installer available for provided configuration")
}

func installSonataFlowUsingYaml() error {
	framework.GetMainLogger().Info("Installing SonataFlow operator")

	yamlContent, err := framework.ReadFromURI(config.GetOperatorYamlURI())
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while reading the operator YAML file")
		return err
	}

	regexp, err := regexp.Compile(getDefaultOperatorImageTag())
	if err != nil {
		return err
	}
	yamlContent = regexp.ReplaceAllString(yamlContent, config.GetOperatorImageTag())

	tempFilePath, err := framework.CreateTemporaryFile("kogito-serverless-operator*.yaml", yamlContent)
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while storing adjusted YAML content to temporary file")
		return err
	}

	_, err = framework.CreateCommand("oc", "create", "-f", tempFilePath).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while installing SonataFlow operator from YAML file")
		return err
	}

	return nil
}

func waitForSonataFlowOperatorUsingYamlRunning() error {
	return srvframework.WaitForSonataFlowOperatorRunning(SonataFlowNamespace)
}

func uninstallSonataFlowUsingYaml() error {
	framework.GetMainLogger().Info("Uninstalling SonataFlow operator")

	output, err := framework.CreateCommand("oc", "delete", "-f", config.GetOperatorYamlURI(), "--timeout=30s", "--ignore-not-found=true").Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting SonataFlow operator failed, output: %s", output))
		return err
	}

	return nil
}

func getSonataFlowCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	//kogitoRuntimes := &v1beta1.KogitoRuntimeList{}
	//if err := framework.GetObjectsInNamespace(namespace, kogitoRuntimes); err != nil {
	//	return nil, err
	//}
	//for i := range kogitoRuntimes.Items {
	//	crs = append(crs, &kogitoRuntimes.Items[i])
	//}
	//
	//kogitoBuilds := &v1beta1.KogitoBuildList{}
	//if err := framework.GetObjectsInNamespace(namespace, kogitoBuilds); err != nil {
	//	return nil, err
	//}
	//for i := range kogitoBuilds.Items {
	//	crs = append(crs, &kogitoBuilds.Items[i])
	//}
	//
	//kogitoSupportingServices := &v1beta1.KogitoSupportingServiceList{}
	//if err := framework.GetObjectsInNamespace(namespace, kogitoSupportingServices); err != nil {
	//	return nil, err
	//}
	//for i := range kogitoSupportingServices.Items {
	//	crs = append(crs, &kogitoSupportingServices.Items[i])
	//}
	//
	//kogitoInfras := &v1beta1.KogitoInfraList{}
	//if err := framework.GetObjectsInNamespace(namespace, kogitoInfras); err != nil {
	//	return nil, err
	//}
	//for i := range kogitoInfras.Items {
	//	crs = append(crs, &kogitoInfras.Items[i])
	//}

	return crs, nil
}

func cleanupSonataFlowCrsInNamespace(namespace string) bool {
	crs, err := getSonataFlowCrsInNamespace(namespace)
	if err != nil {
		framework.GetLogger(namespace).Error(err, "Error getting SonataFlow CRs.")
		return false
	}

	for _, cr := range crs {
		if err := framework.DeleteObject(cr); err != nil {
			framework.GetLogger(namespace).Error(err, "Error deleting SonataFlow CR.", "CR name", cr.GetName())
			return false
		}
	}
	return true
}

func getDefaultOperatorImageTag() string {
	return workflowdef.GetDefaultImageTag(defaultOperatorImage)
}

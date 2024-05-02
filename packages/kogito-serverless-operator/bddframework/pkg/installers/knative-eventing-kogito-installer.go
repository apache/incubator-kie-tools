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
	"fmt"

	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

var (
	// knativeEventingKogitoYamlClusterInstaller installs Knative eventing KogitoSource cluster wide using YAMLs
	knativeEventingKogitoYamlClusterInstaller = YamlClusterWideServiceInstaller{
		InstallClusterYaml:               installKnativeEventingKogitoUsingYaml,
		InstallationNamespace:            knativeEventingKogitoNamespace,
		WaitForClusterYamlServiceRunning: waitForKnativeEventingKogitoUsingYamlRunning,
		GetAllClusterYamlCrsInNamespace:  getKnativeEventingKogitoCrsInNamespace,
		UninstallClusterYaml:             uninstallKnativeEventingKogitoUsingYaml,
		ClusterYamlServiceName:           knativeEventingKogitoServiceName,
	}

	knativeEventingKogitoNamespace    = "knative-kogito"
	knativeEventingKogitoNumberOfPods = 2
	knativeEventingKogitoServiceName  = "Knative Eventing Kogito Source"
)

// GetKnativeEventingKogitoInstaller returns KnativeEventing KogitoSource installer
func GetKnativeEventingKogitoInstaller() ServiceInstaller {
	return &knativeEventingKogitoYamlClusterInstaller
}

func installKnativeEventingKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Installing Knative eventing KogitoSource")

	output, err := framework.CreateCommand("oc", "apply", "-f", fmt.Sprintf("https://github.com/knative-sandbox/eventing-kogito/releases/download/%s/kogito.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying Knative eventing KogitoSource, output: %s", output))
	}

	return err
}

func waitForKnativeEventingKogitoUsingYamlRunning() error {
	return framework.WaitForPodsInNamespace(knativeEventingKogitoNamespace, knativeEventingKogitoNumberOfPods, 3)
}

func uninstallKnativeEventingKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Uninstalling Knative eventing KogitoSource")

	output, err := framework.CreateCommand("oc", "delete", "-f", fmt.Sprintf("https://github.com/knative-sandbox/eventing-kogito/releases/download/%s/kogito.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting Knative eventing KogitoSource failed, output: %s", output))
	}

	return err
}

func getKnativeEventingKogitoCrsInNamespace(namespace string) ([]client.Object, error) {
	crs := []client.Object{}

	// Quick workaround, needs to be refactored once BDD tests are moved to separate module
	return crs, nil
}

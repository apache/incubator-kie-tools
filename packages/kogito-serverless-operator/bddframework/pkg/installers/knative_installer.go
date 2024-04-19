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

	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
)

var (
	// knativeEventingYamlClusterInstaller installs Knative eventing cluster wide using YAMLs
	knativeEventingYamlClusterInstaller = YamlClusterWideServiceInstaller{
		InstallClusterYaml:               installKnativeEventingUsingYaml,
		InstallationNamespace:            knativeEventingNamespace,
		WaitForClusterYamlServiceRunning: waitForKnativeEventingUsingYamlRunning,
		GetAllClusterYamlCrsInNamespace:  getKnativeEventingCrsInNamespace,
		UninstallClusterYaml:             uninstallKnativeEventingUsingYaml,
		ClusterYamlServiceName:           knativeEventingServiceName,
	}

	knativeEventingNamespace    = "knative-eventing"
	knativeEventingVersion      = "v0.26.0"
	knativeEventingNumberOfPods = 7
	knativeEventingServiceName  = "Knative eventing"
)

// GetKnativeEventingInstaller returns KnativeEventing installer
func GetKnativeEventingInstaller() ServiceInstaller {
	return &knativeEventingYamlClusterInstaller
}

func installKnativeEventingUsingYaml() error {
	framework.GetMainLogger().Info("Installing Knative eventing")

	output, err := framework.CreateCommand("oc", "apply", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/eventing-crds.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying eventing CRDs failed, output: %s", output))
		return fmt.Errorf("Error applying eventing CRDs")
	}

	output, err = framework.CreateCommand("oc", "apply", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/eventing-core.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying eventing core components failed, output: %s", output))
		return fmt.Errorf("Error applying eventing core components")
	}

	output, err = framework.CreateCommand("oc", "apply", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/in-memory-channel.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying eventing in-memory channel failed, output: %s", output))
		return fmt.Errorf("Error applying eventing in-memory channel")
	}

	output, err = framework.CreateCommand("oc", "apply", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/mt-channel-broker.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Applying eventing mt-channel broker failed, output: %s", output))
		return fmt.Errorf("Error applying eventing mt-channel broker")
	}

	return nil
}

func waitForKnativeEventingUsingYamlRunning() error {
	return framework.WaitForPodsInNamespace(knativeEventingNamespace, knativeEventingNumberOfPods, 3)
}

func uninstallKnativeEventingUsingYaml() error {
	framework.GetMainLogger().Info("Uninstalling Knative eventing")

	var originalError error

	output, err := framework.CreateCommand("oc", "delete", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/mt-channel-broker.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting eventing mt-channel broker failed, output: %s", output))
		originalError = err
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/in-memory-channel.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting eventing in-memory channel failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", fmt.Sprintf("https://github.com/knative/eventing/releases/download/%s/eventing-core.yaml", knativeEventingVersion)).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting eventing core components failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	return originalError
}

func getKnativeEventingCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	triggers := &eventingv1.TriggerList{}
	if err := framework.GetObjectsInNamespace(namespace, triggers); err != nil {
		return nil, err
	}
	for i := range triggers.Items {
		crs = append(crs, &triggers.Items[i])
	}

	brokers := &eventingv1.BrokerList{}
	if err := framework.GetObjectsInNamespace(namespace, brokers); err != nil {
		return nil, err
	}
	for i := range brokers.Items {
		crs = append(crs, &brokers.Items[i])
	}

	return crs, nil
}

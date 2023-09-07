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
	"regexp"
	"strings"

	v1 "github.com/kiegroup/kogito-operator/apis/rhpam/v1"
	"github.com/kiegroup/kogito-operator/test/pkg/config"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

const (
	openShiftInternalRegistryURL             = "image-registry.openshift-image-registry.svc:5000"
	rhpmaKogitoOperatorPullImageSecretPrefix = "rhpam-kogito-operator-controller-manager-dockercfg"
)

var (
	// rhpamKogitoYamlClusterInstaller installs RHPAM Kogito operator cluster wide using YAMLs
	rhpamKogitoYamlClusterInstaller = YamlClusterWideServiceInstaller{
		InstallClusterYaml:               installRhpamKogitoUsingYaml,
		InstallationNamespace:            rhpamKogitoNamespace,
		WaitForClusterYamlServiceRunning: waitForRhpamKogitoOperatorUsingYamlRunning,
		GetAllClusterYamlCrsInNamespace:  getRhpamKogitoCrsInNamespace,
		UninstallClusterYaml:             uninstallRhpamKogitoUsingYaml,
		ClusterYamlServiceName:           rhpamKogitoServiceName,
	}

	// rhpamKogitoOlmClusterWideInstaller installs RHPAM Kogito cluster wide using OLM
	rhpamKogitoOlmClusterWideInstaller = OlmClusterWideServiceInstaller{
		SubscriptionName:                   rhpamKogitoOperatorSubscriptionName,
		Channel:                            rhpamKogitoOperatorSubscriptionChannel,
		Catalog:                            framework.GetCustomKogitoOperatorCatalog,
		InstallationTimeoutInMinutes:       5,
		GetAllClusterWideOlmCrsInNamespace: getRhpamKogitoCrsInNamespace,
	}

	rhpamKogitoNamespace            = "rhpam-kogito-operator-system"
	rhpamKogitoServiceName          = "RHPAM Kogito operator"
	rhpamKogitoOperatorTimeoutInMin = 5
	rhpamKogitoImageStreamName      = "rhpam-kogito-operator"

	rhpamKogitoOperatorSubscriptionName    = "rhpam-kogito-operator"
	rhpamKogitoOperatorSubscriptionChannel = "7.x"
)

// GetRhpamKogitoInstaller returns RHPAM Kogito installer
func GetRhpamKogitoInstaller() (ServiceInstaller, error) {
	if config.IsOperatorInstalledByYaml() {
		return &rhpamKogitoYamlClusterInstaller, nil
	}

	if config.IsOperatorInstalledByOlm() {
		return &rhpamKogitoOlmClusterWideInstaller, nil
	}

	return nil, errors.New("No RHPAM Kogito operator installer available for provided configuration")
}

func installRhpamKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Installing RHPAM Kogito operator")

	// Create namespace first so ImageStream can be placed there
	if err := framework.CreateNamespace(rhpamKogitoNamespace); err != nil {
		return err
	}

	yamlContent, err := framework.ReadFromURI(config.GetRhpamOperatorYamlURI())
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while reading rhpam-operator.yaml file")
		return err
	}

	operatorImageTag := config.GetOperatorImageTag()
	// Use insecure ImageStream when deploying on OpenShift to support using insecure registries, unless the operator tag already points to internal registry
	if framework.IsOpenshift() && !strings.Contains(config.GetOperatorImageTag(), openShiftInternalRegistryURL) {
		imageTag := strings.Split(config.GetOperatorImageTag(), ":")[1]
		if err := framework.CreateInsecureImageStream(rhpamKogitoNamespace, rhpamKogitoImageStreamName, imageTag, config.GetOperatorImageTag()); err != nil {
			return err
		}

		operatorImageTag = fmt.Sprintf("%s/%s/%s:%s", openShiftInternalRegistryURL, rhpamKogitoNamespace, rhpamKogitoImageStreamName, imageTag)
	}

	regexp, err := regexp.Compile("registry.stage.redhat.io/rhpam-7/rhpam-kogito-rhel8-operator:.*")
	if err != nil {
		return err
	}
	yamlContent = regexp.ReplaceAllString(yamlContent, operatorImageTag)

	tempFilePath, err := framework.CreateTemporaryFile("rhpam-operator*.yaml", yamlContent)
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while storing adjusted YAML content to temporary file")
		return err
	}

	_, err = framework.CreateCommand("oc", "apply", "-f", tempFilePath).Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, "Error while installing RHPAM Kogito operator from YAML file")
		return err
	}

	return nil
}

func waitForRhpamKogitoOperatorUsingYamlRunning() error {
	return framework.WaitForOnOpenshift(rhpamKogitoNamespace, "RHPAM Kogito operator running", rhpamKogitoOperatorTimeoutInMin,
		func() (bool, error) {
			podList, err := framework.GetPods(rhpamKogitoNamespace)
			if err != nil {
				framework.GetLogger(rhpamKogitoNamespace).Error(err, "Error while trying to retrieve RHPAM Kogito Operator pods")
				return false, nil
			}
			if len(podList.Items) != 1 {
				return false, nil
			}

			running := framework.CheckPodsAreReady(podList)

			// If not running, make sure the image pull secret is present in pod
			// If not present, delete the pod to allow its reconstruction with correct pull secret
			// Note that this is specific to Openshift
			if !running && framework.IsOpenshift() {
				for _, pod := range podList.Items {
					if !framework.CheckPodHasImagePullSecretWithPrefix(&pod, rhpmaKogitoOperatorPullImageSecretPrefix) {
						// Delete pod as it has been misconfigured (missing pull secret)
						framework.GetLogger(rhpamKogitoNamespace).Info("RHPAM Kogito Operator pod does not have the image pull secret needed. Deleting it to renew it.")
						err := framework.DeleteObject(&pod)
						if err != nil {
							framework.GetLogger(rhpamKogitoNamespace).Error(err, "Error while trying to delete RHPAM Kogito Operator pod")
							return false, nil
						}
					}
				}
			}
			return running, nil
		})
}

func uninstallRhpamKogitoUsingYaml() error {
	framework.GetMainLogger().Info("Uninstalling Kogito operator")

	output, err := framework.CreateCommand("oc", "delete", "-f", config.GetRhpamOperatorYamlURI(), "--timeout=30s").Execute()
	if err != nil {
		framework.GetMainLogger().Error(err, fmt.Sprintf("Deleting RHPAM Kogito operator failed, output: %s", output))
		return err
	}

	return nil
}

func getRhpamKogitoCrsInNamespace(namespace string) ([]client.Object, error) {
	crs := []client.Object{}

	kogitoRuntimes := &v1.KogitoRuntimeList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoRuntimes); err != nil {
		return nil, err
	}
	for i := range kogitoRuntimes.Items {
		crs = append(crs, &kogitoRuntimes.Items[i])
	}

	kogitoBuilds := &v1.KogitoBuildList{}
	if err := framework.GetObjectsInNamespace(namespace, kogitoBuilds); err != nil {
		return nil, err
	}
	for i := range kogitoBuilds.Items {
		crs = append(crs, &kogitoBuilds.Items[i])
	}

	return crs, nil
}

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

	"sigs.k8s.io/controller-runtime/pkg/client"

	coreapps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	ispn "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/infinispan/v1"
)

var (
	// infinispanOlmClusterWideInstaller installs Infinispan cluster wide using OLM
	infinispanOlmClusterWideInstaller = OlmClusterWideServiceInstaller{
		SubscriptionName:                   infinispanOperatorSubscriptionName,
		Channel:                            infinispanOperatorSubscriptionChannel,
		Catalog:                            framework.GetCommunityCatalog,
		InstallationTimeoutInMinutes:       10,
		GetAllClusterWideOlmCrsInNamespace: getInfinispanCrsInNamespace,
	}
	// infinispanYamlNamespacedInstaller installs Infinispan namespaced using YAMLs
	infinispanYamlNamespacedInstaller = YamlNamespacedServiceInstaller{
		InstallNamespacedYaml:           installInfinispanUsingYaml,
		WaitForNamespacedServiceRunning: waitForInfinispanUsingYamlRunning,
		GetAllNamespaceYamlCrs:          getInfinispanCrsInNamespace,
		UninstallNamespaceYaml:          uninstallInfinispanUsingYaml,
		NamespacedYamlServiceName:       infinispanOperatorServiceName,
	}

	infinispanOperatorSubscriptionName    = "infinispan"
	infinispanOperatorSubscriptionChannel = "2.3.x"
	infinispanOperatorGitHubBranch        = "2.0.x"
	infinispanOperatorDeployFilesURI      = fmt.Sprintf("https://raw.githubusercontent.com/infinispan/infinispan-operator/%s/deploy/", infinispanOperatorGitHubBranch)
	infinispanOperatorServiceName         = "Infinispan"
)

// GetInfinispanInstaller returns Infinispan installer
func GetInfinispanInstaller() (ServiceInstaller, error) {
	if config.IsInfinispanInstalledByYaml() {
		return &infinispanYamlNamespacedInstaller, nil
	}

	if config.IsInfinispanInstalledByOlm() {
		return &infinispanOlmClusterWideInstaller, nil
	}

	return nil, errors.New("No Infinispan operator installer available for provided configuration")
}

func installInfinispanUsingYaml(namespace string) error {
	framework.GetLogger(namespace).Info("Deploy Infinispan from yaml files", "file uri", infinispanOperatorDeployFilesURI)
	infinispanClusterResourceName := getInfinispanClusterResourceName(namespace)

	if !framework.IsInfinispanAvailable(namespace) {
		if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"crds/infinispan.org_caches_crd.yaml", &apiextensionsv1beta1.CustomResourceDefinition{}, nil); err != nil {
			return err
		}
		if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"crds/infinispan.org_infinispans_crd.yaml", &apiextensionsv1beta1.CustomResourceDefinition{}, nil); err != nil {
			return err
		}
	}

	err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"clusterrole.yaml", &rbac.ClusterRole{}, func(object interface{}) {
		// Prefix name to be unique to allow concurrent installations
		object.(*rbac.ClusterRole).Name = infinispanClusterResourceName
	})
	if err != nil {
		return err
	}

	err = framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"clusterrole_binding.yaml", &rbac.ClusterRoleBinding{}, func(object interface{}) {
		// Prefix name to be unique to allow concurrent installations
		object.(*rbac.ClusterRoleBinding).Name = infinispanClusterResourceName
		// Set proper namespace for binding to service account
		object.(*rbac.ClusterRoleBinding).Subjects[0].Namespace = namespace
	})
	if err != nil {
		return err
	}

	if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"service_account.yaml", &corev1.ServiceAccount{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"role.yaml", &rbac.Role{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"role_binding.yaml", &rbac.RoleBinding{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, infinispanOperatorDeployFilesURI+"operator.yaml", &coreapps.Deployment{}, nil); err != nil {
		return err
	}

	return nil
}

func waitForInfinispanUsingYamlRunning(namespace string) error {
	return framework.WaitForPodsWithLabel(namespace, "name", "infinispan-operator", 1, 3)
}

func uninstallInfinispanUsingYaml(namespace string) error {
	framework.GetLogger(namespace).Info("Uninstalling Infinispan")
	infinispanClusterResourceName := getInfinispanClusterResourceName(namespace)

	var originalError error

	output, err := framework.CreateCommand("oc", "delete", "-f", infinispanOperatorDeployFilesURI+"operator.yaml", "-n", namespace).WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Infinispan operator failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", infinispanOperatorDeployFilesURI+"role_binding.yaml", "-n", namespace).WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Infinispan role binding failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", infinispanOperatorDeployFilesURI+"role.yaml", "-n", namespace).WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Infinispan role failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", infinispanOperatorDeployFilesURI+"service_account.yaml", "-n", namespace).WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Infinispan service account failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	crb, err := framework.GetClusterRoleBinding(infinispanClusterResourceName)
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot retrieve ClusterRoleBinding %s", infinispanClusterResourceName))
		if originalError == nil {
			originalError = err
		}
	} else {
		if err = framework.DeleteObject(crb); err != nil {
			framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot delete ClusterRoleBinding %s", infinispanClusterResourceName))
			if originalError == nil {
				originalError = err
			}
		}
	}

	cr, err := framework.GetClusterRole(infinispanClusterResourceName)
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot retrieve ClusterRole %s", infinispanClusterResourceName))
		if originalError == nil {
			originalError = err
		}
	} else {
		if err = framework.DeleteObject(cr); err != nil {
			framework.GetLogger(namespace).Error(err, fmt.Sprintf("Cannot delete ClusterRole %s", infinispanClusterResourceName))
			if originalError == nil {
				originalError = err
			}
		}
	}

	return originalError
}

func getInfinispanClusterResourceName(namespace string) string {
	return "infinispan-" + namespace
}

func getInfinispanCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	infinispans := &ispn.InfinispanList{}
	if err := framework.GetObjectsInNamespace(namespace, infinispans); err != nil {
		return nil, err
	}
	for i := range infinispans.Items {
		crs = append(crs, &infinispans.Items[i])
	}

	return crs, nil
}

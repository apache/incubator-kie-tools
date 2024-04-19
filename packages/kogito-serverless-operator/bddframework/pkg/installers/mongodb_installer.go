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

	coreapps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	apiextensionsv1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	mongodbv1 "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/mongodb/v1"
)

var (
	// mongoDbYamlNamespacedInstaller installs MongoDB namespaced using YAMLs
	mongoDbYamlNamespacedInstaller = YamlNamespacedServiceInstaller{
		InstallNamespacedYaml:           installMongoDbUsingYaml,
		WaitForNamespacedServiceRunning: waitForMongoDbUsingYamlRunning,
		GetAllNamespaceYamlCrs:          getMongoDbCrsInNamespace,
		UninstallNamespaceYaml:          uninstallMongoDbUsingYaml,
		NamespacedYamlServiceName:       mongoDBOperatorServiceName,
	}

	mongoDBOperatorServiceName    = "Mongo DB"
	mongoDBOperatorVersion        = "v0.7.0"
	mongoDBOperatorDeployFilesURI = "https://raw.githubusercontent.com/mongodb/mongodb-kubernetes-operator/" + mongoDBOperatorVersion + "/config/"
)

// GetMongoDbInstaller returns MongoDB installer
func GetMongoDbInstaller() ServiceInstaller {
	return &mongoDbYamlNamespacedInstaller
}

func installMongoDbUsingYaml(namespace string) error {
	framework.GetLogger(namespace).Info("Deploy MongoDB from yaml files", "file uri", mongoDBOperatorDeployFilesURI)

	if !framework.IsMongoDBAvailable(namespace) {
		if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"crd/bases/mongodbcommunity.mongodb.com_mongodbcommunity.yaml", &apiextensionsv1.CustomResourceDefinition{}, nil); err != nil {
			return err
		}
	}

	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"rbac/service_account.yaml", &corev1.ServiceAccount{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"rbac/role.yaml", &rbac.Role{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"rbac/role_binding.yaml", &rbac.RoleBinding{}, nil); err != nil {
		return err
	}

	// Then deploy operator
	err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"manager/manager.yaml", &coreapps.Deployment{}, func(object interface{}) {
		if framework.IsOpenshift() {
			// See https://github.com/mongodb/mongodb-kubernetes-operator/blob/v0.7.0/deploy/openshift/operator_openshift.yaml
			framework.GetLogger(namespace).Debug("Setup MANAGED_SECURITY_CONTEXT env in MongoDB operator for Openshift")
			object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Env = append(object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Env,
				corev1.EnvVar{
					Name:  "MANAGED_SECURITY_CONTEXT",
					Value: "true",
				})
			labels := map[string]string{}
			object.(*coreapps.Deployment).Namespace = namespace
			object.(*coreapps.Deployment).Name = "mongodb-kubernetes-operator"
			labels["owner"] = namespace
			object.(*coreapps.Deployment).Labels = labels
			//object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Name = "mongodb-kubernetes-operator"
			object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].SecurityContext = nil
			object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Image = "quay.io/mongodb/mongodb-kubernetes-operator:0.7.0"
		}
	})
	if err != nil {
		return err
	}
	return nil
}

func waitForMongoDbUsingYamlRunning(namespace string) error {
	return framework.WaitForMongoDBOperatorRunning(namespace)
}

func uninstallMongoDbUsingYaml(namespace string) error {
	framework.GetMainLogger().Info("Uninstalling Mongo DB")

	var originalError error

	output, err := framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"manager/manager.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB operator failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"rbac/role_binding.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB role binding failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"rbac/role.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB role failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"rbac/service_account.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB service account failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	return nil
}

func getMongoDbCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	mongoDbs := &mongodbv1.MongoDBCommunityList{}
	if err := framework.GetObjectsInNamespace(namespace, mongoDbs); err != nil {
		return nil, err
	}
	for i := range mongoDbs.Items {
		crs = append(crs, &mongoDbs.Items[i])
	}

	return crs, nil
}

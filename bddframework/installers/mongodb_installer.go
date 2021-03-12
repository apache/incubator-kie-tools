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
	"fmt"

	"github.com/kiegroup/kogito-cloud-operator/core/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	mongodbv1 "github.com/mongodb/mongodb-kubernetes-operator/pkg/apis/mongodb/v1"
	coreapps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"
)

var (
	// mongoDbYamlNamespacedInstaller installs MongoDB namespaced using YAMLs
	mongoDbYamlNamespacedInstaller = YamlNamespacedServiceInstaller{
		installNamespacedYaml:           installMongoDbUsingYaml,
		waitForNamespacedServiceRunning: waitForMongoDbUsingYamlRunning,
		getAllNamespaceYamlCrs:          getMongoDbCrsInNamespace,
		uninstallNamespaceYaml:          uninstallMongoDbUsingYaml,
		namespacedYamlServiceName:       mongoDBOperatorServiceName,
	}

	mongoDBOperatorServiceName    = "Mongo DB"
	mongoDBOperatorVersion        = "v0.2.2"
	mongoDBOperatorDeployFilesURI = "https://raw.githubusercontent.com/mongodb/mongodb-kubernetes-operator/" + mongoDBOperatorVersion + "/deploy/"
)

// GetMongoDbInstaller returns MongoDB installer
func GetMongoDbInstaller() ServiceInstaller {
	return &mongoDbYamlNamespacedInstaller
}

func installMongoDbUsingYaml(namespace string) error {
	framework.GetLogger(namespace).Info("Deploy MongoDB from yaml files", "file uri", mongoDBOperatorDeployFilesURI)

	if !framework.IsMongoDBAvailable(namespace) {
		if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"crds/mongodb.com_mongodb_crd.yaml", &apiextensionsv1beta1.CustomResourceDefinition{}, nil); err != nil {
			return err
		}
	}

	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"service_account.yaml", &corev1.ServiceAccount{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"role.yaml", &rbac.Role{}, nil); err != nil {
		return err
	}
	if err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"role_binding.yaml", &rbac.RoleBinding{}, nil); err != nil {
		return err
	}

	// Then deploy operator
	err := framework.LoadResource(namespace, mongoDBOperatorDeployFilesURI+"operator.yaml", &coreapps.Deployment{}, func(object interface{}) {
		if framework.IsOpenshift() {
			framework.GetLogger(namespace).Debug("Setup MANAGED_SECURITY_CONTEXT env in MongoDB operator for Openshift")
			object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Env = append(object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Env,
				corev1.EnvVar{
					Name:  "MANAGED_SECURITY_CONTEXT",
					Value: "true",
				})
		}
	})
	if err != nil {
		return err
	}

	// Set correct file to be deployed
	if framework.IsOpenshift() {
		// Used to give correct access to pvc/secret
		// https://github.com/mongodb/mongodb-kubernetes-operator/issues/212#issuecomment-704744307
		output, err := framework.CreateCommand("oc", "adm", "policy", "add-scc-to-user", "anyuid", "system:serviceaccount:"+namespace+":mongodb-kubernetes-operator").WithLoggerContext(namespace).Sync("add-scc-to-user").Execute()
		if err != nil {
			framework.GetLogger(namespace).Error(err, "Error while trying to set specific rights for MongoDB deployments")
			return err
		}
		framework.GetLogger(namespace).Info(output)
	}

	return nil
}

func waitForMongoDbUsingYamlRunning(namespace string) error {
	return framework.WaitForMongoDBOperatorRunning(namespace)
}

func uninstallMongoDbUsingYaml(namespace string) error {
	framework.GetMainLogger().Info("Uninstalling Mongo DB")

	var originalError error

	output, err := framework.CreateCommand("oc", "adm", "policy", "remove-scc-from-user", "anyuid", "system:serviceaccount:"+namespace+":mongodb-kubernetes-operator").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB operator failed, output: %s", output))
		originalError = err
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"operator.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB operator failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"role_binding.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB role binding failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"role.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB role failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	output, err = framework.CreateCommand("oc", "delete", "-f", mongoDBOperatorDeployFilesURI+"service_account.yaml").WithLoggerContext(namespace).Execute()
	if err != nil {
		framework.GetLogger(namespace).Error(err, fmt.Sprintf("Deleting Mongo DB service account failed, output: %s", output))
		if originalError == nil {
			originalError = err
		}
	}

	return originalError
}

func getMongoDbCrsInNamespace(namespace string) ([]kubernetes.ResourceObject, error) {
	crs := []kubernetes.ResourceObject{}

	mongoDbs := &mongodbv1.MongoDBList{}
	if err := framework.GetObjectsInNamespace(namespace, mongoDbs); err != nil {
		return nil, err
	}
	for i := range mongoDbs.Items {
		crs = append(crs, &mongoDbs.Items[i])
	}

	return crs, nil
}

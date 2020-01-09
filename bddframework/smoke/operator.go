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

package main

import (
	"fmt"
	"time"

	coreapps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	infra "github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/pkg/util"
	"github.com/kiegroup/kogito-cloud-operator/version"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
)

const (
	defaultOperatorImageName = "quay.io/kiegroup/kogito-cloud-operator"
	defaultOperatorDeployURI = "https://raw.githubusercontent.com/kiegroup/kogito-cloud-operator/master/deploy/"

	kogitoCrdGroupName       = "app.kiegroup.org"
	kogitoAppCrdName         = "kogitoapps"
	kogitoInfraCrdName       = "kogitoinfras"
	kogitoDataIndexCrdName   = "kogitodataindices"
	kogitoJobsServiceCrdName = "kogitojobsservices"
)

var (
	defaultOperatorImageTag = version.Version
)

// DeployKogitoOperatorFromYaml Deploy Kogito Operator from yaml files
func DeployKogitoOperatorFromYaml(namespace string) error {
	// Create crds files if needed
	if err := deployCrdIfNeeded(namespace, kogitoAppCrdName); err != nil {
		return err
	}
	if err := deployCrdIfNeeded(namespace, kogitoInfraCrdName); err != nil {
		return err
	}
	if err := deployCrdIfNeeded(namespace, kogitoDataIndexCrdName); err != nil {
		return err
	}
	if err := deployCrdIfNeeded(namespace, kogitoJobsServiceCrdName); err != nil {
		return err
	}

	var deployURI = getOperatorDeployURI()
	GetLogger(namespace).Infof("Deploy Operator from yaml files in %s", deployURI)

	loadResource(namespace, deployURI+"service_account.yaml", &corev1.ServiceAccount{}, nil)
	loadResource(namespace, deployURI+"role.yaml", &rbac.Role{}, nil)
	loadResource(namespace, deployURI+"role_binding.yaml", &rbac.RoleBinding{}, nil)
	loadResource(namespace, deployURI+"operator.yaml", &coreapps.Deployment{}, func(object interface{}) {
		GetLogger(namespace).Debugf("Using operator image %s", getOperatorImageNameAndTag())
		object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Image = getOperatorImageNameAndTag()
	})

	return nil
}

// IsKogitoOperatorRunning returns whether Kogito operator is running
func IsKogitoOperatorRunning(namespace string) (bool, error) {
	exists, err := infra.CheckKogitoOperatorExists(kubeClient, namespace)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}
	return exists, nil
}

// WaitForKogitoOperatorRunning waits for Kogito operator running
func WaitForKogitoOperatorRunning(namespace string) error {
	return waitFor(namespace, "Kogito operator running", time.Minute*2, func() (bool, error) {
		return IsKogitoOperatorRunning(namespace)
	})
}

// InstallCommunityOperator installs an operator from 'community-operators' catalog
func InstallCommunityOperator(namespace, operatorName, channel string) error {
	return InstallOperator(namespace, operatorName, "community-operators", channel)
}

// InstallOperator installs an operator via subscrition
func InstallOperator(namespace, operatorName, operatorSource, channel string) error {
	GetLogger(namespace).Infof("Subscribing to %s operator from source %s on channel %s", operatorName, operatorSource, channel)
	if _, err := CreateOperatorGroupIfNotExists(namespace, namespace); err != nil {
		return err
	}

	if _, err := CreateNamespaceSubscriptionIfNotExist(namespace, operatorName, operatorName, operatorSource, channel); err != nil {
		return err
	}

	return WaitForOperatorRunning(namespace, operatorName+"-operator")
}

// WaitForOperatorRunning waits for an operator to be running
func WaitForOperatorRunning(namespace, operatorName string) error {
	return waitFor(namespace, fmt.Sprintf("%s operator running", operatorName), time.Minute*5, func() (bool, error) {
		return IsOperatorRunning(namespace, operatorName)
	})
}

// IsOperatorRunning checks whether an operator is running
func IsOperatorRunning(namespace, operatorName string) (bool, error) {
	exists, err := infra.CheckOperatorExists(kubeClient, namespace, operatorName)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}
	return exists, nil
}

// CreateOperatorGroupIfNotExists creates an operator group if no exist
func CreateOperatorGroupIfNotExists(namespace, operatorGroupName string) (*olmapiv1.OperatorGroup, error) {
	operatorGroup := &olmapiv1.OperatorGroup{
		ObjectMeta: metav1.ObjectMeta{
			Name:      operatorGroupName,
			Namespace: namespace,
		},
		Spec: olmapiv1.OperatorGroupSpec{
			TargetNamespaces: []string{namespace},
		},
	}
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(operatorGroup); err != nil {
		return nil, fmt.Errorf("Error creating OperatorGroup %s: %v", operatorGroupName, err)
	}
	return operatorGroup, nil
}

// CreateNamespaceSubscriptionIfNotExist create a namespaced subscription if not exists
func CreateNamespaceSubscriptionIfNotExist(namespace string, subscriptionName string, operatorName string, operatorSource string, channel string) (*olmapiv1alpha1.Subscription, error) {
	subscription := &olmapiv1alpha1.Subscription{
		ObjectMeta: metav1.ObjectMeta{
			Name:      subscriptionName,
			Namespace: namespace,
		},
		Spec: &olmapiv1alpha1.SubscriptionSpec{
			Package:                operatorName,
			CatalogSource:          operatorSource,
			CatalogSourceNamespace: "openshift-marketplace",
			Channel:                channel,
		},
	}
	if _, err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(subscription); err != nil {
		return nil, fmt.Errorf("Error creating Subscription %s: %v", subscriptionName, err)
	}

	return subscription, nil
}

func deployCrdIfNeeded(namespace, crdName string) error {
	crdFullName := buildCrdFullName(crdName)
	crdEntity := &apiextensionsv1beta1.CustomResourceDefinition{
		ObjectMeta: metav1.ObjectMeta{
			Name: crdFullName,
		},
	}
	if exists, err := kubernetes.ResourceC(kubeClient).Fetch(crdEntity); err != nil {
		return fmt.Errorf("Error while trying to look for Kogito Operator installation: %v", err)
	} else if !exists {
		crdURI := getOperatorDeployURI() + "crds/" + buildCrdFilename(crdName)
		GetLogger(namespace).Infof("deployCrd %s", crdURI)
		return loadResource("", crdURI, &apiextensionsv1beta1.CustomResourceDefinition{}, nil)
	}

	return nil
}

func buildCrdFullName(crdName string) string {
	return crdName + "." + kogitoCrdGroupName
}

func buildCrdFilename(crdName string) string {
	return kogitoCrdGroupName + "_" + crdName + "_crd.yaml"
}

func getOperatorDeployURI() string {
	return util.GetOSEnv("OPERATOR_DEPLOY_FOLDER", defaultOperatorDeployURI)
}
func getOperatorImageName() string {
	return util.GetOSEnv("OPERATOR_IMAGE_NAME", defaultOperatorImageName)
}
func getOperatorImageTag() string {
	return util.GetOSEnv("OPERATOR_IMAGE_TAG", defaultOperatorImageTag)
}
func getOperatorImageNameAndTag() string {
	return fmt.Sprintf("%s:%s", getOperatorImageName(), getOperatorImageTag())
}

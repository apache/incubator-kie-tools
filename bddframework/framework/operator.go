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

	coreapps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/meta"
	infra "github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/pkg/operator"
	"github.com/kiegroup/kogito-cloud-operator/test/config"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
)

const (
	kogitoOperatorTimeoutInMin     = 5
	kogitoInfinispanDependencyName = "Infinispan"
	kogitoKafkaDependencyName      = "Kafka"
	kogitoKeycloakDependencyName   = "Keycloak"

	// clusterWideSubscriptionLabel label marking cluster wide Subscriptions created by BDD tests
	clusterWideSubscriptionLabel = "kogito-operator-bdd-tests"
)

type dependentOperator struct {
	operatorPackageName string
	timeoutInMin        int
	channel             string
	clusterWide         bool
}

type operatorCatalog struct {
	source       string
	namespace    string
	dependencies map[string]dependentOperator
}

var (
	kogitoOperatorPullImageSecretPrefix = operator.Name + "-dockercfg"

	// KogitoOperatorDependencies contains list of operators to be used together with Kogito operator
	KogitoOperatorDependencies = []string{kogitoInfinispanDependencyName, kogitoKafkaDependencyName, kogitoKeycloakDependencyName}

	commonKogitoOperatorDependencies = map[string]dependentOperator{
		kogitoInfinispanDependencyName: {
			operatorPackageName: "infinispan",
			timeoutInMin:        10,
			channel:             "2.0.x",
			clusterWide:         false,
		},
		kogitoKafkaDependencyName: {
			operatorPackageName: "strimzi-kafka-operator",
			timeoutInMin:        10,
			channel:             "stable",
			clusterWide:         true,
		},
		kogitoKeycloakDependencyName: {
			operatorPackageName: "keycloak-operator",
			timeoutInMin:        10,
			channel:             "alpha",
			clusterWide:         false,
		},
	}

	// CommunityCatalog operator catalog for community
	CommunityCatalog = operatorCatalog{
		source:       "community-operators",
		namespace:    "openshift-marketplace",
		dependencies: commonKogitoOperatorDependencies,
	}
	// OperatorHubCatalog operator catalog of Operator Hub
	OperatorHubCatalog = operatorCatalog{
		source:       "operatorhubio-catalog",
		namespace:    "olm",
		dependencies: commonKogitoOperatorDependencies,
	}
)

// DeployNamespacedKogitoOperatorFromYaml Deploy Kogito Operator watching for single namespace from yaml files, return all objects created for deployment
// Will be deployed in the same namespace as it will watch for
func DeployNamespacedKogitoOperatorFromYaml(deploymentNamespace string) (created []meta.ResourceObject, err error) {
	return deployKogitoOperatorFromYaml(deploymentNamespace, true)
}

// DeployClusterWideKogitoOperatorFromYaml Deploy Kogito Operator watching for all namespaces from yaml files, return all objects created for deployment
func DeployClusterWideKogitoOperatorFromYaml(deploymentNamespace string) (created []meta.ResourceObject, err error) {
	return deployKogitoOperatorFromYaml(deploymentNamespace, false)
}

// Deploy Kogito Operator from yaml files, return all objects created for deployment
func deployKogitoOperatorFromYaml(namespace string, namespaced bool) (created []meta.ResourceObject, err error) {
	var deployURI = config.GetOperatorDeployURI()
	GetLogger(namespace).Infof("Deploy Operator from yaml files in %s", deployURI)

	sa := &corev1.ServiceAccount{}
	if err = loadResource(namespace, deployURI+"service_account.yaml", sa, nil); err != nil {
		return
	}
	created = append(created, sa)

	cr := &rbac.ClusterRole{}
	err = loadResource(namespace, deployURI+"clusterrole.yaml", cr, func(object interface{}) {
		// Object name needs to be unique so we can create independent objects for parallel tests
		object.(*rbac.ClusterRole).SetName(object.(*rbac.ClusterRole).GetName() + "-" + namespace)
	})
	if err != nil {
		return
	}
	created = append(created, cr)

	crb := &rbac.ClusterRoleBinding{}
	err = loadResource(namespace, deployURI+"clusterrole_binding.yaml", crb, func(object interface{}) {
		// Object name needs to be unique so we can create independent objects for parallel tests
		object.(*rbac.ClusterRoleBinding).SetName(object.(*rbac.ClusterRoleBinding).GetName() + "-" + namespace)
		for i := range object.(*rbac.ClusterRoleBinding).Subjects {
			object.(*rbac.ClusterRoleBinding).Subjects[i].Namespace = namespace
		}
		object.(*rbac.ClusterRoleBinding).RoleRef.Name = cr.Name
	})
	if err != nil {
		return
	}
	created = append(created, crb)

	if IsOpenshift() {
		// Wait for docker pulling secret available for kogito-operator serviceaccount
		// This is needed if images are stored into local Openshift registry
		// Note that this is specific to Openshift
		err = WaitForOnOpenshift(namespace, "image pulling secret", 2, func() (bool, error) {
			// unfortunately the SecretList is buggy, so we have to fetch it manually: https://github.com/kubernetes-sigs/controller-runtime/issues/362
			// so use direct command to look for specific secret
			output, err := CreateCommand("oc", "get", "secrets", "-o", "name", "-n", namespace).WithLoggerContext(namespace).Execute()
			if err != nil {
				GetLogger(namespace).Errorf("Error while trying to get secrets: %v", err)
				return false, err
			}
			GetLogger(namespace).Info(output)
			return strings.Contains(output, "secret/"+kogitoOperatorPullImageSecretPrefix), nil
		})

		if err != nil {
			return
		}
	}

	// Then deploy operator
	d := &coreapps.Deployment{}
	err = loadResource(namespace, deployURI+"operator.yaml", d, func(object interface{}) {
		GetLogger(namespace).Debugf("Using operator image %s", getOperatorImageNameAndTag())
		object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0].Image = getOperatorImageNameAndTag()

		if namespaced {
			// Set Kogito operator to watch only namespace where it is installed
			container := object.(*coreapps.Deployment).Spec.Template.Spec.Containers[0]
			for i := range container.Env {
				if container.Env[i].Name == "WATCH_NAMESPACE" {
					container.Env[i].Value = namespace
				}
			}
		}
	})
	if err != nil {
		return
	}
	created = append(created, d)
	return
}

// IsKogitoOperatorRunning returns whether Kogito operator is running
func IsKogitoOperatorRunning(namespace string) (bool, error) {
	exists, err := KogitoOperatorExists(namespace)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}

	return exists, nil
}

// KogitoOperatorExists returns whether Kogito operator exists and is running. If it is existing but not running, it returns true and an error
func KogitoOperatorExists(namespace string) (bool, error) {
	return infra.CheckKogitoOperatorExists(kubeClient, namespace)
}

// WaitForKogitoOperatorRunning waits for Kogito operator running
func WaitForKogitoOperatorRunning(namespace string) error {
	return WaitForOnOpenshift(namespace, "Kogito operator running", kogitoOperatorTimeoutInMin,
		func() (bool, error) {
			running, err := IsKogitoOperatorRunning(namespace)
			if err != nil {
				return false, err
			}

			// If not running, make sure the image pull secret is present in pod
			// If not present, delete the pod to allow its reconstruction with correct pull secret
			// Note that this is specific to Openshift
			if !running && IsOpenshift() {
				podList, err := GetPodsWithLabels(namespace, map[string]string{"name": operator.Name})
				if err != nil {
					GetLogger(namespace).Errorf("Error while trying to retrieve Kogito Operator pods: %v", err)
					return false, nil
				}
				for _, pod := range podList.Items {
					if !CheckPodHasImagePullSecretWithPrefix(&pod, kogitoOperatorPullImageSecretPrefix) {
						// Delete pod as it has been misconfigured (missing pull secret)
						GetLogger(namespace).Info("Kogito Operator pod does not have the image pull secret needed. Deleting it to renew it.")
						err := kubernetes.ResourceC(kubeClient).Delete(&pod)
						if err != nil {
							GetLogger(namespace).Errorf("Error while trying to delete Kogito Operator pod: %v", err)
							return false, nil
						}
					}
				}
			}
			return running, nil
		})
}

// WaitForKogitoOperatorRunningWithDependencies waits for Kogito operator running as well as other dependent operators
func WaitForKogitoOperatorRunningWithDependencies(namespace string) error {
	if err := WaitForKogitoOperatorRunning(namespace); err != nil {
		return err
	}

	for dependentOperator := range CommunityCatalog.dependencies {
		if err := WaitForKogitoOperatorDependencyRunning(namespace, dependentOperator, CommunityCatalog); err != nil {
			return err
		}
	}
	return nil
}

// InstallKogitoOperatorDependency installs dependent operator from parameter
func InstallKogitoOperatorDependency(namespace, dependentOperator string, catalog operatorCatalog) error {
	if operatorInfo, exists := catalog.dependencies[dependentOperator]; exists {
		if operatorInfo.clusterWide {
			if err := InstallClusterWideOperator(namespace, operatorInfo.operatorPackageName, operatorInfo.channel, catalog); err != nil {
				return err
			}
		} else {
			if err := InstallOperator(namespace, operatorInfo.operatorPackageName, operatorInfo.channel, catalog); err != nil {
				return err
			}
		}
	} else {
		return fmt.Errorf("Operator %s not found", dependentOperator)
	}
	return nil
}

// WaitForKogitoOperatorDependencyRunning waits for dependent operator to be running
func WaitForKogitoOperatorDependencyRunning(namespace, dependentOperator string, catalog operatorCatalog) error {
	if operatorInfo, exists := catalog.dependencies[dependentOperator]; exists {
		if operatorInfo.clusterWide {
			if err := WaitForClusterWideOperatorRunning(namespace, operatorInfo.operatorPackageName, catalog, operatorInfo.timeoutInMin); err != nil {
				return err
			}
		} else {
			if err := WaitForOperatorRunning(namespace, operatorInfo.operatorPackageName, catalog, operatorInfo.timeoutInMin); err != nil {
				return err
			}
		}
	} else {
		return fmt.Errorf("Operator %s not found", dependentOperator)
	}
	return nil
}

// InstallOperator installs an operator via subscrition
func InstallOperator(namespace, subscriptionName, channel string, catalog operatorCatalog) error {
	GetLogger(namespace).Infof("Subscribing to %s operator from source %s on channel %s", subscriptionName, catalog.source, channel)
	if _, err := CreateOperatorGroupIfNotExists(namespace, namespace); err != nil {
		return err
	}

	if _, err := CreateNamespacedSubscriptionIfNotExist(namespace, subscriptionName, subscriptionName, catalog, channel); err != nil {
		return err
	}

	return nil
}

// InstallClusterWideOperator installs an operator for all namespaces via subscrition
func InstallClusterWideOperator(namespace, subscriptionName, channel string, catalog operatorCatalog) error {
	olmNamespace := config.GetOlmNamespace()

	GetLogger(namespace).Infof("Subscribing to %s operator from source %s on channel %s in namespace %s", subscriptionName, catalog.source, channel, olmNamespace)
	if _, err := CreateNamespacedSubscriptionIfNotExist(olmNamespace, subscriptionName, subscriptionName, catalog, channel); err != nil {
		return err
	}

	return nil
}

// WaitForOperatorRunning waits for an operator to be running
func WaitForOperatorRunning(namespace, operatorPackageName string, catalog operatorCatalog, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%s operator running", operatorPackageName), timeoutInMin,
		func() (bool, error) {
			return IsOperatorRunning(namespace, operatorPackageName, catalog)
		})
}

// WaitForClusterWideOperatorRunning waits for a cluster wide operator to be running
func WaitForClusterWideOperatorRunning(namespace, operatorPackageName string, catalog operatorCatalog, timeoutInMin int) error {
	olmNamespace := config.GetOlmNamespace()
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%s operator in namespace %s running", operatorPackageName, olmNamespace), timeoutInMin,
		func() (bool, error) {
			return IsOperatorRunning(olmNamespace, operatorPackageName, catalog)
		})
}

// IsOperatorRunning checks whether an operator is running
func IsOperatorRunning(namespace, operatorPackageName string, catalog operatorCatalog) (bool, error) {
	exists, err := infra.CheckOperatorExistsUsingSubscription(kubeClient, namespace, operatorPackageName, catalog.source)
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
	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(operatorGroup); err != nil {
		return nil, fmt.Errorf("Error creating OperatorGroup %s: %v", operatorGroupName, err)
	}
	return operatorGroup, nil
}

// CreateNamespacedSubscriptionIfNotExist create a namespaced subscription if not exists
func CreateNamespacedSubscriptionIfNotExist(namespace string, subscriptionName string, operatorName string, catalog operatorCatalog, channel string) (*olmapiv1alpha1.Subscription, error) {
	subscription := &olmapiv1alpha1.Subscription{
		ObjectMeta: metav1.ObjectMeta{
			Name:      subscriptionName,
			Namespace: namespace,
			Labels:    map[string]string{clusterWideSubscriptionLabel: ""},
		},
		Spec: &olmapiv1alpha1.SubscriptionSpec{
			Package:                operatorName,
			CatalogSource:          catalog.source,
			CatalogSourceNamespace: catalog.namespace,
			Channel:                channel,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(subscription); err != nil {
		return nil, fmt.Errorf("Error creating Subscription %s: %v", subscriptionName, err)
	}

	return subscription, nil
}

// GetClusterWideTestSubscriptions returns cluster wide subscriptions created by BDD tests
func GetClusterWideTestSubscriptions() (*olmapiv1alpha1.SubscriptionList, error) {
	olmNamespace := config.GetOlmNamespace()

	subscriptions := &olmapiv1alpha1.SubscriptionList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespaceAndLabel(olmNamespace, subscriptions, map[string]string{clusterWideSubscriptionLabel: ""}); err != nil {
		return nil, fmt.Errorf("Error retrieving SubscriptionList in namespace %s: %v", olmNamespace, err)
	}

	return subscriptions, nil
}

// DeleteSubscription deletes Subscription and related objects
func DeleteSubscription(subscription *olmapiv1alpha1.Subscription) error {
	installedCsv := subscription.Status.InstalledCSV
	suscriptionNamespace := subscription.Namespace

	// Delete Subscription
	if err := kubernetes.ResourceC(kubeClient).Delete(subscription); err != nil {
		return err
	}

	// Delete related CSV
	csv := &olmapiv1alpha1.ClusterServiceVersion{}
	exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Namespace: suscriptionNamespace, Name: installedCsv}, csv)
	if err != nil {
		return err
	}
	if exists {
		if err := kubernetes.ResourceC(kubeClient).Delete(csv); err != nil {
			return err
		}
	}

	return nil
}

func getOperatorImageNameAndTag() string {
	return fmt.Sprintf("%s:%s", config.GetOperatorImageName(), config.GetOperatorImageTag())
}

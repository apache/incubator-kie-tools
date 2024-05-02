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

package framework

import (
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/meta"

	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/operator"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
)

const (
	kogitoOperatorTimeoutInMin = 5

	// createdByBddLabel label marking resources created by BDD tests
	createdByBddLabel = "kogito-operator-bdd-tests"

	kogitoOperatorDeploymentName = "kogito-operator-controller-manager"

	// kogitoCatalogSourceName name of the CatalogSource containing Kogito bundle for BDD tests
	kogitoCatalogSourceName = "bdd-tests-kogito-catalog"

	// OpenShiftCatalogNamespace is the namespace for clusterwide installations on Openshift
	OpenShiftCatalogNamespace = "openshift-marketplace"
	// KubernetesCatalogNamespace is the namespace for clusterwide installations on Kubernetes
	KubernetesCatalogNamespace = "olm"

	defaultOpenShiftClusterOperatorNamespace  = "openshift-operators"
	defaultKubernetesClusterOperatorNamespace = "operators"
)

// OperatorCatalog OLM operator catalog
type OperatorCatalog struct {
	source    string
	namespace string
}

var (
	kogitoOperatorPullImageSecretPrefix = operator.Name + "-dockercfg"

	// KogitoOperatorMongoDBDependency is the MongoDB identifier for installation
	KogitoOperatorMongoDBDependency = infrastructure.MongoDBKind
	mongoDBOperatorTimeoutInMin     = 10

	kogitoOperatorCatalogSourceTimeoutInMin = 3

	operatorGroupTimeoutInMin = 3
)

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
	GetLogger(namespace).Debug("Checking Operator", "Deployment", kogitoOperatorDeploymentName, "Namespace", namespace)

	operatorDeployment := &v1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      kogitoOperatorDeploymentName,
			Namespace: namespace,
		},
	}
	if exists, err := kubernetes.ResourceC(kubeClient).Fetch(operatorDeployment); err != nil {
		return false, fmt.Errorf("Error while trying to look for Deploment %s: %v ", kogitoOperatorDeploymentName, err)
	} else if !exists {
		return false, nil
	}

	if operatorDeployment.Status.AvailableReplicas == 0 {
		return true, fmt.Errorf("%s Operator seems to be created in the namespace '%s', but there's no available pods replicas deployed ", operator.Name, namespace)
	}

	return true, nil
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
					GetLogger(namespace).Error(err, "Error while trying to retrieve Kogito Operator pods")
					return false, nil
				}
				for _, pod := range podList.Items {
					if !CheckPodHasImagePullSecretWithPrefix(&pod, kogitoOperatorPullImageSecretPrefix) {
						// Delete pod as it has been misconfigured (missing pull secret)
						GetLogger(namespace).Info("Kogito Operator pod does not have the image pull secret needed. Deleting it to renew it.")
						err := kubernetes.ResourceC(kubeClient).Delete(&pod)
						if err != nil {
							GetLogger(namespace).Error(err, "Error while trying to delete Kogito Operator pod")
							return false, nil
						}
					}
				}
			}
			return running, nil
		})
}

// RemoveKogitoOperatorDeployment remove the Kogito operator deployment in the given namespace
func RemoveKogitoOperatorDeployment(namespace string) error {
	GetLogger(namespace).Debug("Removing Operator deployment", "Deployment", kogitoOperatorDeploymentName, "Namespace", namespace)

	operatorDeployment := &v1.Deployment{
		ObjectMeta: metav1.ObjectMeta{
			Name:      kogitoOperatorDeploymentName,
			Namespace: namespace,
		},
	}
	if exists, err := kubernetes.ResourceC(kubeClient).Fetch(operatorDeployment); err != nil {
		return fmt.Errorf("Error while trying to look for Deploment %s: %v ", kogitoOperatorDeploymentName, err)
	} else if exists {
		if err := kubernetes.ResourceC(kubeClient).Delete(operatorDeployment); err != nil {
			return fmt.Errorf("Error while trying to remove Deploment %s: %v ", kogitoOperatorDeploymentName, err)
		}
	} else {
		GetLogger(namespace).Warn("No operator deployment to delete", "Deployment", kogitoOperatorDeploymentName)
	}
	return nil
}

// InstallOperator installs an operator via subscrition
func InstallOperator(namespace, subscriptionName, channel, startingCSV string, catalog OperatorCatalog) error {
	GetLogger(namespace).Info("Subscribing to operator", "subscriptionName", subscriptionName, "catalogSource", catalog.source, "channel", channel)
	if _, err := CreateOperatorGroupIfNotExists(namespace, namespace); err != nil {
		return err
	}

	if err := WaitForOperatorGroup(namespace, namespace); err != nil {
		return err
	}

	if _, err := CreateNamespacedSubscriptionIfNotExist(namespace, subscriptionName, subscriptionName, catalog, channel, startingCSV); err != nil {
		return err
	}

	return nil
}

// InstallClusterWideOperator installs an operator for all namespaces via subscrition
func InstallClusterWideOperator(subscriptionName, channel, startingCSV string, catalog OperatorCatalog) error {
	clusterOperatorNamespace := GetClusterOperatorNamespace()
	GetLogger(clusterOperatorNamespace).Info("Subscribing to operator", "subscriptionName", subscriptionName, "catalogSource", catalog.source, "channel", channel, "namespace", clusterOperatorNamespace)
	if _, err := CreateNamespacedSubscriptionIfNotExist(clusterOperatorNamespace, subscriptionName, subscriptionName, catalog, channel, startingCSV); err != nil {
		return err
	}

	return nil
}

// WaitForOperatorRunning waits for an operator to be running
func WaitForOperatorRunning(namespace, operatorPackageName string, catalog OperatorCatalog, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%s operator running", operatorPackageName), timeoutInMin,
		func() (bool, error) {
			return IsOperatorRunning(namespace, operatorPackageName, catalog)
		},
		func() (bool, error) {
			return SubscriptionResolutionFails(namespace, operatorPackageName, catalog.source)
		})
}

// WaitForClusterWideOperatorRunning waits for a cluster wide operator to be running
func WaitForClusterWideOperatorRunning(operatorPackageName string, catalog OperatorCatalog, timeoutInMin int) error {
	clusterOperatorNamespace := GetClusterOperatorNamespace()
	return WaitForOperatorRunning(clusterOperatorNamespace, operatorPackageName, catalog, timeoutInMin)
}

// IsOperatorRunning checks whether an operator is running
func IsOperatorRunning(namespace, operatorPackageName string, catalog OperatorCatalog) (bool, error) {
	exists, err := OperatorExistsUsingSubscription(namespace, operatorPackageName, catalog.source)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}
	return exists, nil
}

// OperatorExistsUsingSubscription returns whether operator exists and is running. If it is existing but not running, it returns true and an error
// For this check informations from subscription are used.
func OperatorExistsUsingSubscription(namespace, operatorPackageName, operatorSource string) (bool, error) {
	GetLogger(namespace).Debug("Checking Operator", "Subscription", operatorPackageName, "Namespace", namespace)

	subscription, err := getSubscription(kubeClient, namespace, operatorPackageName, operatorSource)
	if err != nil {
		return false, err
	} else if subscription == nil {
		return false, nil
	}
	GetLogger(namespace).Debug("Found", "Subscription", operatorPackageName)

	subscriptionCsv := subscription.Status.CurrentCSV
	if subscriptionCsv == "" {
		// Subscription doesn't contain current CSV yet, operator is still being installed.
		GetLogger(namespace).Debug("Current CSV not found", "Subscription", operatorPackageName)
		return false, nil
	}
	GetLogger(namespace).Debug("Found current CSV in", "Subscription", subscriptionCsv)

	operatorDeployments := &v1.DeploymentList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespaceAndLabel(namespace, operatorDeployments, map[string]string{"olm.owner.kind": "ClusterServiceVersion", "olm.owner": subscriptionCsv}); err != nil {
		return false, fmt.Errorf("Error while trying to fetch DC with label olm.owner: '%s' Operator installation: %s ", subscriptionCsv, err)
	}

	if len(operatorDeployments.Items) == 0 {
		return false, nil
	} else if len(operatorDeployments.Items) == 1 && operatorDeployments.Items[0].Status.AvailableReplicas == 0 {
		return true, fmt.Errorf("Operator based on Subscription '%s' seems to be created in the namespace '%s', but there's no available pods replicas deployed ", operatorPackageName, namespace)
	}
	return true, nil
}

// SubscriptionResolutionFails returns true when Subscription fails to be resolved with error message
func SubscriptionResolutionFails(namespace, operatorPackageName, operatorSource string) (bool, error) {
	GetLogger(namespace).Debug("Checking Subscription", "Subscription", operatorPackageName, "Namespace", namespace)

	subscription, err := getSubscription(kubeClient, namespace, operatorPackageName, operatorSource)
	if err != nil {
		return false, err
	} else if subscription == nil {
		return false, nil
	}
	GetLogger(namespace).Debug("Found", "Subscription", operatorPackageName)

	for _, condition := range subscription.Status.Conditions {
		// TODO: replace with condition.Type == ResolutionFailed when OLM dependency gets updated
		if condition.Reason == "ConstraintsNotSatisfiable" && condition.Status == corev1.ConditionTrue {
			return true, fmt.Errorf("Subscription installation fails: %s", condition.Message)
		}
	}
	return false, nil
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

// WaitForOperatorGroup for an operator group to be available
func WaitForOperatorGroup(namespace, operatorGroupName string) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%s OperatorGroup is ready", operatorGroupName), operatorGroupTimeoutInMin,
		func() (bool, error) {
			return isOperatorGroupReady(namespace, operatorGroupName)
		})
}

func isOperatorGroupReady(namespace, operatorGroupName string) (bool, error) {
	operatorGroup := &olmapiv1.OperatorGroup{
		ObjectMeta: metav1.ObjectMeta{
			Name:      operatorGroupName,
			Namespace: namespace,
		},
	}
	if exists, err := kubernetes.ResourceC(kubeClient).Fetch(operatorGroup); err != nil {
		return false, fmt.Errorf("Error while trying to look for OperatorGroup %s: %v ", operatorGroupName, err)
	} else if !exists {
		return false, nil
	}

	for _, ns := range operatorGroup.Status.Namespaces {
		if ns == namespace {
			return true, nil
		}
	}
	return false, nil
}

// CreateNamespacedSubscriptionIfNotExist create a namespaced subscription if not exists
func CreateNamespacedSubscriptionIfNotExist(namespace string, subscriptionName string, operatorName string, catalog OperatorCatalog, channel, startingCSV string) (*olmapiv1alpha1.Subscription, error) {
	subscription := &olmapiv1alpha1.Subscription{
		ObjectMeta: metav1.ObjectMeta{
			Name:      subscriptionName,
			Namespace: namespace,
			Labels:    map[string]string{createdByBddLabel: ""},
		},
		Spec: &olmapiv1alpha1.SubscriptionSpec{
			Package:                operatorName,
			CatalogSource:          catalog.source,
			CatalogSourceNamespace: catalog.namespace,
			Channel:                channel,
			StartingCSV:            startingCSV,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(subscription); err != nil {
		return nil, fmt.Errorf("Error creating Subscription %s: %v", subscriptionName, err)
	}

	return subscription, nil
}

// GetSubscription returns subscription
func GetSubscription(namespace, operatorPackageName string, catalog OperatorCatalog) (*olmapiv1alpha1.Subscription, error) {
	subscription, err := getSubscription(kubeClient, namespace, operatorPackageName, catalog.source)
	if err != nil {
		return nil, err
	} else if subscription == nil {
		return nil, fmt.Errorf(" Subscription with name %s and operator source %s not found in namespace %s", operatorPackageName, catalog.source, namespace)
	}

	return subscription, nil
}

// GetClusterWideSubscription returns cluster wide subscription
func GetClusterWideSubscription(operatorPackageName string, catalog OperatorCatalog) (*olmapiv1alpha1.Subscription, error) {
	return GetSubscription(GetClusterOperatorNamespace(), operatorPackageName, catalog)
}

// DeleteSubscription deletes Subscription and related objects
func DeleteSubscription(subscription *olmapiv1alpha1.Subscription) error {
	installedCsv := subscription.Status.InstalledCSV
	suscriptionNamespace := subscription.Namespace

	// If created by BDD
	if _, ok := subscription.Labels[createdByBddLabel]; ok {
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
	}

	return nil
}

// WaitForMongoDBOperatorRunning waits for MongoDB operator to be running
func WaitForMongoDBOperatorRunning(namespace string) error {
	return WaitForOnOpenshift(namespace, "MongoDB operator running", mongoDBOperatorTimeoutInMin,
		func() (bool, error) {
			return isMongoDBOperatorRunning(namespace)
		})
}

func isMongoDBOperatorRunning(namespace string) (bool, error) {
	context := operator.Context{
		Client: kubeClient,
		Log:    GetLogger(namespace),
		Scheme: meta.GetRegisteredSchema(),
	}
	mongoDBHandler := infrastructure.NewMongoDBHandler(context)
	exists, err := mongoDBHandler.IsMongoDBOperatorAvailable(namespace)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}

	return exists, nil
}

// CreateKogitoOperatorCatalogSource create a Kogito operator catalog Source
func CreateKogitoOperatorCatalogSource() (*olmapiv1alpha1.CatalogSource, error) {
	catalogNamespace := GetCustomKogitoOperatorCatalog().namespace
	GetLogger(catalogNamespace).Info("Installing custom Kogito operator CatalogSource", "name", kogitoCatalogSourceName, "namespace", catalogNamespace)

	cs := &olmapiv1alpha1.CatalogSource{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetCustomKogitoOperatorCatalog().source,
			Namespace: GetCustomKogitoOperatorCatalog().namespace,
		},
		Spec: olmapiv1alpha1.CatalogSourceSpec{
			SourceType:  olmapiv1alpha1.SourceTypeGrpc,
			Image:       config.GetOperatorCatalogImage(),
			Description: "Catalog containing custom Kogito bundle used for BDD tests",
		},
	}

	if err := kubernetes.ResourceC(kubeClient).CreateIfNotExists(cs); err != nil {
		return nil, fmt.Errorf("Error creating CatalogSource %s: %v", kogitoCatalogSourceName, err)
	}

	return cs, nil
}

// WaitForKogitoOperatorCatalogSourceReady waits for Kogito operator CatalogSource to be ready
func WaitForKogitoOperatorCatalogSourceReady() error {
	return WaitForOnOpenshift(GetCustomKogitoOperatorCatalog().namespace, "Kogito operator CatalogSource is ready", kogitoOperatorCatalogSourceTimeoutInMin,
		func() (bool, error) {
			return isKogitoOperatorCatalogSourceReady()
		})
}

func isKogitoOperatorCatalogSourceReady() (bool, error) {
	cs := &olmapiv1alpha1.CatalogSource{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetCustomKogitoOperatorCatalog().source,
			Namespace: GetCustomKogitoOperatorCatalog().namespace,
		},
	}
	if exists, err := kubernetes.ResourceC(kubeClient).Fetch(cs); err != nil {
		return false, fmt.Errorf("Error while trying to look for CatalogSource %s: %v ", kogitoCatalogSourceName, err)
	} else if !exists {
		return false, nil
	}

	if cs.Status.GRPCConnectionState == nil || cs.Status.GRPCConnectionState.LastObservedState != "READY" {
		return false, nil
	}
	return true, nil
}

// DeleteKogitoOperatorCatalogSource delete a Kogito operator catalog Source
func DeleteKogitoOperatorCatalogSource() error {
	GetLogger(GetCustomKogitoOperatorCatalog().namespace).Info("Deleting custom Kogito operator CatalogSource", "name", kogitoCatalogSourceName, "namespace", GetCustomKogitoOperatorCatalog().namespace)

	cs := &olmapiv1alpha1.CatalogSource{
		ObjectMeta: metav1.ObjectMeta{
			Name:      GetCustomKogitoOperatorCatalog().source,
			Namespace: GetCustomKogitoOperatorCatalog().namespace,
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Delete(cs); err != nil {
		return fmt.Errorf("Error deleting CatalogSource %s: %v", kogitoCatalogSourceName, err)
	}

	return nil
}

// GetClusterOperatorNamespace returns namespace used to deploy cluster wide operators
func GetClusterOperatorNamespace() string {
	if olmNamespace := config.GetOlmNamespace(); len(olmNamespace) > 0 {
		return olmNamespace
	}
	if IsOpenshift() {
		return defaultOpenShiftClusterOperatorNamespace
	}
	return defaultKubernetesClusterOperatorNamespace
}

// GetCommunityCatalog returns OperatorCatalog with community operators
func GetCommunityCatalog() OperatorCatalog {
	if IsOpenshift() {
		return GetOperatorCatalog(OpenShiftCatalogNamespace, "community-operators")
	}
	return GetOperatorCatalog(KubernetesCatalogNamespace, "operatorhubio-catalog")
}

// GetProductCatalog returns OperatorCatalog with red hat operators
func GetProductCatalog() OperatorCatalog {
	return GetOperatorCatalog(OpenShiftCatalogNamespace, "redhat-operators")
}

// GetCustomKogitoOperatorCatalog returns OperatorCatalog containing custom Kogito operator informations
func GetCustomKogitoOperatorCatalog() OperatorCatalog {
	if IsOpenshift() {
		return GetOperatorCatalog(OpenShiftCatalogNamespace, kogitoCatalogSourceName)
	}
	return GetOperatorCatalog(KubernetesCatalogNamespace, kogitoCatalogSourceName)
}

// GetOperatorCatalog creates the operator catalog based given on Source and namespace
func GetOperatorCatalog(namespace, source string) OperatorCatalog {
	return OperatorCatalog{
		source:    source,
		namespace: namespace,
	}
}

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
	"errors"
	"fmt"
	"strings"

	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"

	coreapps "k8s.io/api/apps/v1"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	rbac "k8s.io/api/rbac/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/framework"
	infra "github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/pkg/operator"
	"github.com/kiegroup/kogito-cloud-operator/pkg/version"
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

	kogitoOperatorDeploymentName = "kogito-operator-controller-manager"
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

	// KogitoOperatorMongoDBDependency is the MongoDB identifier for installation
	KogitoOperatorMongoDBDependency = infra.MongoDBKind
	mongoDBOperatorTimeoutInMin     = 10

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
func DeployNamespacedKogitoOperatorFromYaml(deploymentNamespace string) error {
	return errors.New("Kogito operator deployment from namespace is not supported now, needs to be refactored for OLM approach")
}

// DeployClusterWideKogitoOperatorFromYaml Deploy Kogito Operator watching for all namespaces from yaml files, return all objects created for deployment
func DeployClusterWideKogitoOperatorFromYaml(deploymentNamespace string) error {
	yamlContent, err := ReadFromURI(config.GetOperatorYamlURI())
	if err != nil {
		GetLogger(deploymentNamespace).Error(err, "Error while reading kogito-operator.yaml file")
		return err
	}

	yamlContent = strings.ReplaceAll(yamlContent, "quay.io/kiegroup/kogito-cloud-operator:"+version.Version, getOperatorImageNameAndTag())

	tempFilePath, err := CreateTemporaryFile("kogito-operator*.yaml", yamlContent)
	if err != nil {
		GetLogger(deploymentNamespace).Error(err, "Error while storing adjusted YAML content to temporary file")
		return err
	}

	_, err = CreateCommand("oc", "apply", "-f", tempFilePath).WithLoggerContext(deploymentNamespace).Execute()
	if err != nil {
		GetLogger(deploymentNamespace).Error(err, "Error while installing Kogito operator from YAML file")
		return err
	}

	return nil
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
	GetLogger(namespace).Info("Subscribing to operator", "subscriptionName", subscriptionName, "catalogSource", catalog.source, "channel", channel)
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
	GetLogger(namespace).Info("Subscribing to operator", "subscriptionName", subscriptionName, "catalogSource", catalog.source, "channel", channel, "namespace", olmNamespace)
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

	subscription, err := framework.GetSubscription(kubeClient, namespace, operatorPackageName, operatorSource)
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

// DeployMongoDBOperatorFromYaml Deploy Kogito Operator from yaml files
func DeployMongoDBOperatorFromYaml(namespace string) error {
	GetLogger(namespace).Info("Deploy MongoDB from yaml files", "file uri", mongoDBOperatorDeployFilesURI)

	if !infra.IsMongoDBAvailable(kubeClient) {
		if err := loadResource(namespace, mongoDBOperatorDeployFilesURI+"crds/mongodb.com_mongodb_crd.yaml", &apiextensionsv1beta1.CustomResourceDefinition{}, func(object interface{}) {
			// Short fix as 'plural' from mongodb is not mongodbs ...
			// https://github.com/mongodb/mongodb-kubernetes-operator/issues/237
			crdName := object.(*apiextensionsv1beta1.CustomResourceDefinition).Spec.Names.Plural
			if !strings.HasSuffix(crdName, "s") {
				crdName += "s"
				metadataName := fmt.Sprintf("%s.%s", crdName, object.(*apiextensionsv1beta1.CustomResourceDefinition).Spec.Group)

				GetLogger(namespace).Info("MongoDB Crd, changing plural", "from", object.(*apiextensionsv1beta1.CustomResourceDefinition).Spec.Names.Plural, "to", crdName)
				GetLogger(namespace).Info("MongoDB Crd, changing metadata name", "from", object.(*apiextensionsv1beta1.CustomResourceDefinition).ObjectMeta.Name, "to", metadataName)

				object.(*apiextensionsv1beta1.CustomResourceDefinition).Spec.Names.Plural = crdName
				object.(*apiextensionsv1beta1.CustomResourceDefinition).ObjectMeta.Name = metadataName
			}
		}); err != nil {
			return err
		}
	}

	if err := loadResource(namespace, mongoDBOperatorDeployFilesURI+"service_account.yaml", &corev1.ServiceAccount{}, nil); err != nil {
		return err
	}
	if err := loadResource(namespace, mongoDBOperatorDeployFilesURI+"role.yaml", &rbac.Role{}, nil); err != nil {
		return err
	}
	if err := loadResource(namespace, mongoDBOperatorDeployFilesURI+"role_binding.yaml", &rbac.RoleBinding{}, nil); err != nil {
		return err
	}

	// Then deploy operator
	err := loadResource(namespace, mongoDBOperatorDeployFilesURI+"operator.yaml", &coreapps.Deployment{}, func(object interface{}) {
		if IsOpenshift() {
			GetLogger(namespace).Debug("Setup MANAGED_SECURITY_CONTEXT env in MongoDB operator for Openshift")
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
	if IsOpenshift() {
		// Used to give correct access to pvc/secret
		// https://github.com/mongodb/mongodb-kubernetes-operator/issues/212#issuecomment-704744307
		output, err := CreateCommand("oc", "adm", "policy", "add-scc-to-user", "anyuid", "system:serviceaccount:"+namespace+":mongodb-kubernetes-operator").WithLoggerContext(namespace).Sync("add-scc-to-user").Execute()
		if err != nil {
			GetLogger(namespace).Error(err, "Error while trying to set specific rights for MongoDB deployments")
			return err
		}
		GetLogger(namespace).Info(output)
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
	exists, err := infra.IsMongoDBOperatorAvailable(kubeClient, namespace)
	if err != nil {
		if exists {
			return false, nil
		}
		return false, err
	}

	return exists, nil
}

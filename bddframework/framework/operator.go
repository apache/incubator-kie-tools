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

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	infra "github.com/kiegroup/kogito-cloud-operator/pkg/infrastructure"
	"github.com/kiegroup/kogito-cloud-operator/pkg/operator"
	"github.com/kiegroup/kogito-cloud-operator/test/config"

	olmapiv1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1"
	olmapiv1alpha1 "github.com/operator-framework/operator-lifecycle-manager/pkg/api/apis/operators/v1alpha1"
)

const (
	kogitoOperatorTimeoutInMin = 5

	communityCatalog = "community-operators"
)

type dependentOperator struct {
	operatorPackageName string
	timeoutInMin        int
	channel             string
	crdName             string
}

var (
	kogitoOperatorPullImageSecretPrefix = operator.Name + "-dockercfg"

	// KogitoOperatorCommunityDependencies contains list of community operators to be used together with Kogito operator
	KogitoOperatorCommunityDependencies = map[string]dependentOperator{
		"Infinispan": {
			operatorPackageName: "infinispan",
			timeoutInMin:        10,
			channel:             "stable",
			crdName:             "infinispans.infinispan.org",
		},
		"Kafka": {
			operatorPackageName: "strimzi-kafka-operator",
			timeoutInMin:        10,
			channel:             "stable",
			crdName:             "kafkas.kafka.strimzi.io",
		},
		"Keycloak": {
			operatorPackageName: "keycloak-operator",
			timeoutInMin:        10,
			channel:             "alpha",
			crdName:             "keycloaks.keycloak.org",
		},
	}
)

// DeployKogitoOperatorFromYaml Deploy Kogito Operator from yaml files
func DeployKogitoOperatorFromYaml(namespace string) error {
	var deployURI = config.GetOperatorDeployURI()
	GetLogger(namespace).Infof("Deploy Operator from yaml files in %s", deployURI)

	// TODO: error handling, go lint is screaming about this
	loadResource(namespace, deployURI+"service_account.yaml", &corev1.ServiceAccount{}, nil)
	loadResource(namespace, deployURI+"role.yaml", &rbac.Role{}, nil)
	loadResource(namespace, deployURI+"role_binding.yaml", &rbac.RoleBinding{}, nil)

	// Wait for docker pulling secret available for kogito-operator serviceaccount
	// This is needed if images are stored into local Openshift registry
	// Note that this is specific to Openshift
	err := WaitFor(namespace, "image pulling secret", GetOpenshiftDurationFromTimeInMin(2), func() (bool, error) {
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
		return err
	}

	// Then deploy operator
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
	return WaitForOnOpenshift(namespace, "Kogito operator running", kogitoOperatorTimeoutInMin,
		func() (bool, error) {
			running, err := IsKogitoOperatorRunning(namespace)
			if err != nil {
				return false, err
			}

			// If not running, make sure the image pull secret is present in pod
			// If not present, delete the pod to allow its reconstruction with correct pull secret
			// Note that this is specific to Openshift
			if !running {
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

	for dependentOperator := range KogitoOperatorCommunityDependencies {
		if err := WaitForKogitoOperatorDependencyRunning(namespace, dependentOperator); err != nil {
			return err
		}
	}
	return nil
}

// InstallCommunityKogitoOperatorDependency installs dependent operator from parameter
func InstallCommunityKogitoOperatorDependency(namespace, dependentOperator string) error {
	if operatorInfo, exists := KogitoOperatorCommunityDependencies[dependentOperator]; exists {
		if err := InstallCommunityOperator(namespace, operatorInfo.operatorPackageName, operatorInfo.channel); err != nil {
			return err
		}
	} else {
		return fmt.Errorf("Operator %s not found", dependentOperator)
	}
	return nil
}

// WaitForKogitoOperatorDependencyRunning waits for dependent operator to be running
func WaitForKogitoOperatorDependencyRunning(namespace, dependentOperator string) error {
	if operatorInfo, exists := KogitoOperatorCommunityDependencies[dependentOperator]; exists {
		if err := WaitForOperatorRunning(namespace, operatorInfo.operatorPackageName, communityCatalog, operatorInfo.timeoutInMin); err != nil {
			return err
		}
	} else {
		return fmt.Errorf("Operator %s not found", dependentOperator)
	}
	return nil
}

// InstallCommunityOperator installs an operator from 'community-operators' catalog
func InstallCommunityOperator(namespace, subscriptionName, channel string) error {
	return InstallOperator(namespace, subscriptionName, communityCatalog, channel)
}

// InstallOperator installs an operator via subscrition
func InstallOperator(namespace, subscriptionName, operatorSource, channel string) error {
	GetLogger(namespace).Infof("Subscribing to %s operator from source %s on channel %s", subscriptionName, operatorSource, channel)
	if _, err := CreateOperatorGroupIfNotExists(namespace, namespace); err != nil {
		return err
	}

	if _, err := CreateNamespacedSubscriptionIfNotExist(namespace, subscriptionName, subscriptionName, operatorSource, channel); err != nil {
		return err
	}

	return nil
}

// WaitForOperatorRunning waits for an operator to be running
func WaitForOperatorRunning(namespace, operatorPackageName, operatorSource string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("%s operator running", operatorPackageName), timeoutInMin,
		func() (bool, error) {
			return IsOperatorRunning(namespace, operatorPackageName, operatorSource)
		})
}

// IsOperatorRunning checks whether an operator is running
func IsOperatorRunning(namespace, operatorPackageName, operatorSource string) (bool, error) {
	exists, err := infra.CheckOperatorExistsUsingSubscription(kubeClient, namespace, operatorPackageName, operatorSource)
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

// CreateNamespacedSubscriptionIfNotExist create a namespaced subscription if not exists
func CreateNamespacedSubscriptionIfNotExist(namespace string, subscriptionName string, operatorName string, operatorSource string, channel string) (*olmapiv1alpha1.Subscription, error) {
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

func getOperatorImageNameAndTag() string {
	return fmt.Sprintf("%s:%s", config.GetOperatorImageName(), config.GetOperatorImageTag())
}

// IsCommunityOperatorCrdAvailable returns whether the crd is available on cluster
func IsCommunityOperatorCrdAvailable(operatorName string) (bool, error) {
	operator, exists := KogitoOperatorCommunityDependencies[operatorName]
	if !exists {
		return false, fmt.Errorf("Unknown community operator %s", operatorName)
	}
	return IsCrdAvailable(operator.crdName)

}

// WaitForKogitoOperatorCrdAvailable waits for dependent operator main crd to be available
func WaitForKogitoOperatorCrdAvailable(namespace, dependentOperator string) error {
	if operatorInfo, exists := KogitoOperatorCommunityDependencies[dependentOperator]; exists {
		return WaitForOnOpenshift(namespace, fmt.Sprintf("%s operator crd is available", dependentOperator), operatorInfo.timeoutInMin,
			func() (bool, error) {
				return IsCrdAvailable(operatorInfo.crdName)
			})
	}
	return fmt.Errorf("Operator %s not found", dependentOperator)
}

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
	"sync"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/meta"
	apps "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	apiextensionsv1beta1 "k8s.io/apiextensions-apiserver/pkg/apis/apiextensions/v1beta1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"
)

var (
	kubeClient *client.Client
	mux        = &sync.Mutex{}
)

// InitKubeClient initializes the Kubernetes Client
func InitKubeClient() error {
	mux.Lock()
	defer mux.Unlock()
	if kubeClient == nil {
		newClient, err := client.NewClientBuilder().WithDiscoveryClient().WithBuildClient().WithKubernetesExtensionClient().Build()
		if err != nil {
			return fmt.Errorf("Error initializing kube client: %v", err)
		}
		kubeClient = newClient
	}
	return nil
}

// CreateNamespace creates a new namespace
func CreateNamespace(namespace string) error {
	GetLogger(namespace).Infof("Create namespace %s", namespace)
	_, err := kubernetes.NamespaceC(kubeClient).Create(namespace)
	if err != nil {
		return fmt.Errorf("Cannot create namespace %s: %v", namespace, err)
	}
	return nil
}

// DeleteNamespace deletes a namespace
func DeleteNamespace(namespace string) error {
	ns := &corev1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: namespace}}
	GetLogger(namespace).Infof("Delete namespace %s", namespace)
	err := kubernetes.ResourceC(kubeClient).Delete(ns)
	if err != nil {
		return fmt.Errorf("Cannot delete namespace %s: %v", namespace, err)
	}
	return nil
}

// IsNamespace checks wherher a namespace exists
func IsNamespace(namespace string) (bool, error) {
	ns, err := kubernetes.NamespaceC(kubeClient).Fetch(namespace)
	if err != nil {
		return false, fmt.Errorf("Cannot create namespace %s: %v", namespace, err)
	}
	return ns != nil, nil
}

// OperateOnNamespaceIfExists do some operations on the namespace if that one exists
func OperateOnNamespaceIfExists(namespace string, operate func(namespace string) error) error {
	if ok, er := IsNamespace(namespace); er != nil {
		return fmt.Errorf("Error while checking namespace: %v", er)
	} else if ok {
		return operate(namespace)
	}
	return nil
}

// WaitForPodsWithLabel waits for pods with specific label to be available and running
func WaitForPodsWithLabel(namespace, labelName, labelValue string, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods with label name '%s' and value '%s' available and running", labelName, labelValue), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsWithLabels(namespace, map[string]string{labelName: labelValue})
			if err != nil || (len(pods.Items) != numberOfPods) {
				return false, err
			}

			return CheckPodsAreRunning(pods), nil
		})
}

// GetPods retrieves all pods in namespace
func GetPods(namespace string) (*corev1.PodList, error) {
	pods := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespace(namespace, pods); err != nil {
		return nil, err
	}
	return pods, nil
}

// GetPodsByDeploymentConfig retrieves pods with a deploymentconfig label set to <dcName>
func GetPodsByDeploymentConfig(namespace string, dcName string) (*corev1.PodList, error) {
	return GetPodsWithLabels(namespace, map[string]string{"deploymentconfig": dcName})
}

// GetPodsWithLabels retrieves pods based on label name and value
func GetPodsWithLabels(namespace string, labels map[string]string) (*corev1.PodList, error) {
	pods := &corev1.PodList{}
	if err := kubernetes.ResourceC(kubeClient).ListWithNamespaceAndLabel(namespace, pods, labels); err != nil {
		return nil, err
	}
	return pods, nil
}

// CheckPodsAreRunning returns true if all pods are running
func CheckPodsAreRunning(pods *corev1.PodList) bool {
	for _, pod := range pods.Items {
		if !IsPodRunning(&pod) {
			return false
		}
	}
	return true
}

// IsPodRunning returns true if pod is running
func IsPodRunning(pod *corev1.Pod) bool {
	return pod.Status.Phase == corev1.PodRunning
}

// GetDeployment retrieves deployment with specified name in namespace
func GetDeployment(namespace, deploymentName string) (*apps.Deployment, error) {
	deployment := &apps.Deployment{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: deploymentName, Namespace: namespace}, deployment); err != nil {
		return nil, err
	} else if !exists {
		return nil, nil
	}
	return deployment, nil
}

func loadResource(namespace, uri string, resourceRef meta.ResourceObject, beforeCreate func(object interface{})) error {
	GetLogger(namespace).Debugf("loadResource %s", uri)

	data, err := ReadFromURI(uri)
	if err != nil {
		return fmt.Errorf("Unable to read from URI %s: %v", uri, err)
	}

	if err = kubernetes.ResourceC(kubeClient).CreateFromYamlContent(data, namespace, resourceRef, beforeCreate); err != nil {
		return fmt.Errorf("Error while creating resources from file '%s': %v ", uri, err)
	}
	return nil
}

// WaitForAllPodsToContainTextInLog waits for pods of specified deployment config to contain specified text in log
func WaitForAllPodsToContainTextInLog(namespace, dcName, logText string, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods for deployment config '%s' contain text '%s'", dcName, logText), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsByDeploymentConfig(namespace, dcName)
			if err != nil {
				return false, err
			}

			// Container name is equal to deployment config name
			return checkAllPodsContainingTextInLog(namespace, pods, dcName, logText)
		})
}

func checkAllPodsContainingTextInLog(namespace string, pods *corev1.PodList, containerName, text string) (bool, error) {
	for _, pod := range pods.Items {
		containsText, err := isPodContainingTextInLog(namespace, &pod, containerName, text)
		if err != nil || !containsText {
			return false, err
		}
	}
	return true, nil
}

func isPodContainingTextInLog(namespace string, pod *corev1.Pod, containerName, text string) (bool, error) {
	log, err := kubernetes.PodC(kubeClient).GetLogs(namespace, pod.GetName(), containerName)
	return strings.Contains(log, text), err
}

// IsCrdAvailable returns whether the crd is available on cluster
func IsCrdAvailable(crdName string) (bool, error) {
	crdEntity := &apiextensionsv1beta1.CustomResourceDefinition{
		ObjectMeta: metav1.ObjectMeta{
			Name: crdName,
		},
	}
	return kubernetes.ResourceC(kubeClient).Fetch(crdEntity)
}

// CreateSecret creates a new secret
func CreateSecret(namespace, name string, secretContent map[string]string) error {
	GetLogger(namespace).Infof("Create Secret %s", name)

	secret := &corev1.Secret{
		ObjectMeta: metav1.ObjectMeta{
			Name:      name,
			Namespace: namespace,
		},
		Type:       corev1.SecretTypeOpaque,
		StringData: secretContent,
	}

	return kubernetes.ResourceC(kubeClient).Create(secret)
}

// CheckPodHasImagePullSecretWithPrefix checks that a pod has an image pull secret starting with the given prefix
func CheckPodHasImagePullSecretWithPrefix(pod *corev1.Pod, imagePullSecretPrefix string) bool {
	for _, secretRef := range pod.Spec.ImagePullSecrets {
		if strings.HasPrefix(secretRef.Name, imagePullSecretPrefix) {
			return true
		}
	}
	return false
}

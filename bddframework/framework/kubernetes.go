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

// podErrorReasons contains all the reasons to state a pod in error.
var podErrorReasons = [1]string{"ErrImagePull"}

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

// WaitForPodsWithLabel waits for pods with specific label to be available and running
func WaitForPodsWithLabel(namespace, labelName, labelValue string, numberOfPods, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Pods with label name '%s' and value '%s' available and running", labelName, labelValue), timeoutInMin,
		func() (bool, error) {
			pods, err := GetPodsWithLabels(namespace, map[string]string{labelName: labelValue})
			if err != nil || (len(pods.Items) != numberOfPods) {
				return false, err
			}

			return CheckPodsAreReady(pods), nil
		}, CheckPodsWithLabelInError(namespace, labelName, labelValue))
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

// CheckPodsAreReady returns true if all pods are ready
func CheckPodsAreReady(pods *corev1.PodList) bool {
	for _, pod := range pods.Items {
		if !IsPodStatusConditionReady(&pod) {
			return false
		}
	}
	return true
}

// IsPodRunning returns true if pod is running
func IsPodRunning(pod *corev1.Pod) bool {
	return pod.Status.Phase == corev1.PodRunning
}

// IsPodStatusConditionReady returns true if all pod's containers are ready (really running)
func IsPodStatusConditionReady(pod *corev1.Pod) bool {
	for _, condition := range pod.Status.Conditions {
		if condition.Type == corev1.ContainersReady {
			return condition.Status == corev1.ConditionTrue
		}
	}
	return false
}

// WaitForDeploymentRunning waits for a deployment to be running, with a specific number of pod
func WaitForDeploymentRunning(namespace, dName string, podNb int, timeoutInMin int) error {
	return WaitForOnOpenshift(namespace, fmt.Sprintf("Deployment %s running", dName), timeoutInMin,
		func() (bool, error) {
			if dc, err := GetDeployment(namespace, dName); err != nil {
				return false, err
			} else if dc == nil {
				return false, nil
			} else {
				GetLogger(namespace).Debugf("Deployment has %d available replicas\n", dc.Status.AvailableReplicas)
				return dc.Status.AvailableReplicas == int32(podNb), nil
			}
		})
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
		}, CheckPodsByDeploymentConfigInError(namespace, dcName))
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

// CheckPodsByDeploymentConfigInError returns a function that checks the pods error state.
func CheckPodsByDeploymentConfigInError(namespace string, dcName string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsByDeploymentConfig(namespace, dcName)
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods)
	}
}

// CheckPodsWithLabelInError returns a function that checks the pods error state.
func CheckPodsWithLabelInError(namespace, labelName, labelValue string) func() (bool, error) {
	return func() (bool, error) {
		pods, err := GetPodsWithLabels(namespace, map[string]string{labelName: labelValue})
		if err != nil {
			return true, err

		}
		return checkPodsInError(pods)
	}
}

func checkPodsInError(pods *corev1.PodList) (bool, error) {
	for _, pod := range pods.Items {
		if hasErrors, err := isPodInError(&pod); hasErrors {
			return true, err
		}
	}

	return false, nil
}

func isPodInError(pod *corev1.Pod) (bool, error) {
	if IsPodRunning(pod) {
		return false, nil
	}

	for _, status := range pod.Status.ContainerStatuses {
		for _, reason := range podErrorReasons {
			if status.State.Waiting.Reason == reason {
				return true, fmt.Errorf("Error in pod, reason: %s", reason)
			}
		}

	}

	return false, nil
}

func checkPodContainerHasEnvVariableWithValue(pod *corev1.Pod, containerName, envVarName, envVarValue string) bool {
	for _, container := range pod.Spec.Containers {
		if container.Name == containerName {
			for _, env := range container.Env {
				if env.Name == envVarName {
					return env.Value == envVarValue
				}
			}
		}
	}
	return false
}

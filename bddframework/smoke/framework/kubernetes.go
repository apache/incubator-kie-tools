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
	"sync"
	"time"

	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/types"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/meta"
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

// WaitForPods waits for pods with specific label to be available and running
func WaitForPods(namespace, labelName, labelValue string, numberOfPods, timeoutInMin int) error {
	return WaitFor(namespace, fmt.Sprintf("Pods with label name '%s' and value '%s' available and running", labelName, labelValue), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
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

// WaitForStatefulSetRunning waits for a stateful set to be running, with a specific number of pod
func WaitForStatefulSetRunning(namespace, ssName string, podNb int, timeoutInMin int) error {
	return WaitFor(namespace, fmt.Sprintf("StatefulSet %s running", ssName), time.Duration(timeoutInMin)*time.Minute, func() (bool, error) {
		if dc, err := GetStatefulSet(namespace, ssName); err != nil {
			return false, err
		} else if dc == nil {
			return false, nil
		} else {
			GetLogger(namespace).Debugf("StatefulSet %s has %d ready replicas\n", ssName, dc.Status.ReadyReplicas)
			return dc.Status.ReadyReplicas == int32(podNb), nil
		}
	})
}

// GetStatefulSet retrieves a stateful set
func GetStatefulSet(namespace, ssName string) (*appsv1.StatefulSet, error) {
	dc := &appsv1.StatefulSet{}
	if exists, err := kubernetes.ResourceC(kubeClient).FetchWithKey(types.NamespacedName{Name: ssName, Namespace: namespace}, dc); err != nil {
		return nil, fmt.Errorf("Error while trying to look for DeploymentConfig %s: %v ", ssName, err)
	} else if !exists {
		return nil, nil
	}
	return dc, nil
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

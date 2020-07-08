// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	"io/ioutil"
	"os"
	"strings"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	namespaceLogFile = "logs/namespace_history.log"
	fileFlags        = os.O_CREATE | os.O_WRONLY | os.O_APPEND
	permissionMode   = 0666
)

// CreateNamespace creates a new namespace
func CreateNamespace(namespace string) error {
	GetLogger(namespace).Infof("Create namespace %s", namespace)
	_, err := kubernetes.NamespaceC(kubeClient).Create(namespace)
	if err != nil {
		return fmt.Errorf("Cannot create namespace %s: %v", namespace, err)
	}
	onNamespacePostCreated(namespace)
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
	onNamespacePostDeleted(namespace)
	return nil
}

// IsNamespace checks wherher a namespace exists
func IsNamespace(namespace string) (bool, error) {
	ns, err := kubernetes.NamespaceC(kubeClient).Fetch(namespace)
	if err != nil {
		return false, fmt.Errorf("Cannot checking namespace %s: %v", namespace, err)
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
	GetLogger(namespace).Warn("Namespace not found for operation")
	return nil
}

// GetNamespacesInHistory retrieves all the namespaces in the history.
func GetNamespacesInHistory() []string {
	input, err := ioutil.ReadFile(namespaceLogFile)
	if err != nil {
		// file does not exist
		return []string{}
	}

	return strings.Split(string(input), "\n")
}

// ClearNamespaceHistory clears all the namespace history content.
func ClearNamespaceHistory() {
	os.Remove(namespaceLogFile)
}

func onNamespacePostCreated(namespace string) {
	if err := addNamespaceToHistory(namespace); err != nil {
		GetLogger(namespace).Warnf("Error updating namespace history %v", err)
	}
}

func onNamespacePostDeleted(namespace string) {
	if err := removeNamespaceFromHistory(namespace); err != nil {
		GetLogger(namespace).Warnf("Error removing namespace of history %v", err)
	}
}

func addNamespaceToHistory(namespace string) error {
	file, err := os.OpenFile(namespaceLogFile, fileFlags, permissionMode)
	if err != nil {
		return err
	}
	defer file.Close()
	if _, err = file.WriteString(fmt.Sprintf("%s\n", namespace)); err != nil {
		return err
	}

	return nil
}

func removeNamespaceFromHistory(namespace string) error {
	namespaces := GetNamespacesInHistory()
	var newNamespaces []string
	for _, oldNamespace := range namespaces {
		if namespace != oldNamespace {
			newNamespaces = append(newNamespaces, oldNamespace)
		}
	}

	output := strings.Join(newNamespaces, "\n")
	return ioutil.WriteFile(namespaceLogFile, []byte(output), permissionMode)
}

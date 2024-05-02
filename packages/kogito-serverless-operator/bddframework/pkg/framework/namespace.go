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
	"os"
	"strings"

	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"
)

const (
	namespaceLogFile = "logs/namespace_history.log"
)

// CreateNamespace creates a new namespace
func CreateNamespace(namespace string) error {
	GetLogger(namespace).Info("Creating namespace", "namespace", namespace)
	_, err := kubernetes.NamespaceC(kubeClient).Create(namespace)
	if err != nil {
		return fmt.Errorf("Cannot create namespace %s: %v", namespace, err)
	}
	OnNamespacePostCreated(namespace)
	return nil
}

// CreateNamespaceIfNotExists creates a new namespace if not exists, returns true if namespaces already existed
func CreateNamespaceIfNotExists(namespace string) (exists bool, err error) {
	GetLogger(namespace).Info("Creating namespace", "namespace", namespace)
	_, err = kubernetes.NamespaceC(kubeClient).Create(namespace)
	if err != nil {
		if errors.IsAlreadyExists(err) {
			return true, nil
		}
		return false, fmt.Errorf("Cannot create namespace %s: %v", namespace, err)
	}
	OnNamespacePostCreated(namespace)
	return false, nil
}

// DeleteNamespace deletes a namespace
func DeleteNamespace(namespace string) error {
	ns := &corev1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: namespace}}
	GetLogger(namespace).Info("Deleting namespace", "namespace", namespace)
	err := kubernetes.ResourceC(kubeClient).Delete(ns)
	if err != nil {
		return fmt.Errorf("Cannot delete namespace %s: %v", namespace, err)
	}
	OnNamespacePostDeleted(namespace)
	return nil
}

// IsNamespace checks whether a namespace exists
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
	input, err := os.ReadFile(namespaceLogFile)
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

// OnNamespacePostCreated hook when a namespace has been created
func OnNamespacePostCreated(namespace string) {
	if err := addNamespaceToHistory(namespace); err != nil {
		GetLogger(namespace).Warn("Error updating namespace history", "error", err)
	}
}

// OnNamespacePostDeleted hook when a namespace has been deleted
func OnNamespacePostDeleted(namespace string) {
	if err := removeNamespaceFromHistory(namespace); err != nil {
		GetLogger(namespace).Warn("Error removing namespace of history", "error", err)
	}
}

func addNamespaceToHistory(namespace string) error {
	return AddLineToFile(namespace, namespaceLogFile)
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
	return os.WriteFile(namespaceLogFile, []byte(output), permissionMode)
}

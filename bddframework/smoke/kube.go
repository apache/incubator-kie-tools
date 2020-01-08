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
	"sync"

	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/kiegroup/kogito-cloud-operator/pkg/client"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/kubernetes"
	"github.com/kiegroup/kogito-cloud-operator/pkg/client/meta"
)

var (
	kubeClient *client.Client
	mux        = &sync.Mutex{}
)

func initKubeClient() error {
	mux.Lock()
	defer mux.Unlock()
	if kubeClient == nil {
		newClient, err := client.NewClientBuilder().WithDiscoveryClient().WithBuildClient().Build()
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

func loadResource(namespace, uri string, resourceRef meta.ResourceObject, beforeCreate func(object interface{})) error {
	GetLogger(namespace).Debugf("loadResource %s", uri)

	data, err := readFromURI(uri)
	if err != nil {
		return fmt.Errorf("Unable to read from URI %s: %v", uri, err)
	}

	if err = kubernetes.ResourceC(kubeClient).CreateFromYamlContent(data, namespace, resourceRef, beforeCreate); err != nil {
		return fmt.Errorf("Error while creating resources from file '%s': %v ", uri, err)
	}
	return nil
}

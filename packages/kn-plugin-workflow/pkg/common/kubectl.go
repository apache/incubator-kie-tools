/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package common

import (
	"github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/common/k8sclient"
	v1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	"k8s.io/apimachinery/pkg/runtime/schema"
)

type K8sApi interface {

	IsCreateAllowed(resourcePath string, namespace string) (bool, error)
	IsDeleteAllowed(resourcePath string, namespace string) error
	GetCurrentNamespace() (string, error)
	GetNamespace(namespace string) (*corev1.Namespace, error)
	CheckContext() (string, error)
	ExecuteApply(path, namespace string) error
	ExecuteCreate(gvr schema.GroupVersionResource, object *unstructured.Unstructured, namespace string) (*unstructured.Unstructured, error)
	ExecuteDelete(path, namespace string) error
	ExecuteDeleteGVR(gvr schema.GroupVersionResource, name string, namespace string) error
	ExecuteGet(gvr schema.GroupVersionResource, name string, namespace string) (*unstructured.Unstructured, error)
	ExecuteList(gvr schema.GroupVersionResource, namespace string) (*unstructured.UnstructuredList, error)
	CheckCrdExists(path string) error
	GetDeploymentStatus(namespace, deploymentName string) (v1.DeploymentStatus, error)
	PortForward(namespace, serviceName, portFrom, portTo string, onReady func()) error
}

var Current K8sApi = k8sclient.GoAPI{}

func IsCreateAllowed(resourcePath string, namespace string) (bool, error) {
	return Current.IsCreateAllowed(resourcePath, namespace)
}

func IsDeleteAllowed(name string, namespace string) error {
	return Current.IsDeleteAllowed(name, namespace)
}

func CheckContext() (string, error) {
	return Current.GetCurrentNamespace()
}

func GetCurrentNamespace() (string, error) {
	return Current.GetCurrentNamespace()
}

func GetNamespace(namespace string) (*corev1.Namespace, error)  {
	return Current.GetNamespace(namespace)
}

var ExecuteApply = func(path, namespace string) error {
	return Current.ExecuteApply(path, namespace)
}

func ExecuteCreate(gvr schema.GroupVersionResource, object *unstructured.Unstructured, namespace string) (*unstructured.Unstructured, error) {
	return Current.ExecuteCreate(gvr, object, namespace)
}

func ExecuteDelete(path, namespace string) error {
	return Current.ExecuteDelete(path, namespace)
}

func ExecuteDeleteGVR(gvr schema.GroupVersionResource, name string, namespace string) error {
	return Current.ExecuteDeleteGVR(gvr, name, namespace)
}

func ExecuteGet(gvr schema.GroupVersionResource, name string, namespace string) (*unstructured.Unstructured, error) {
	return Current.ExecuteGet(gvr, name, namespace)
}

func ExecuteList(gvr schema.GroupVersionResource, namespace string)(*unstructured.UnstructuredList, error)  {
	return Current.ExecuteList(gvr, namespace)
}

func CheckCrdExists(crd string) error {
	return Current.CheckCrdExists(crd)
}

func GetDeploymentStatus(namespace, deploymentName string) (v1.DeploymentStatus, error) {
	return Current.GetDeploymentStatus(namespace, deploymentName)
}

func PortForward(namespace, deploymentName, portFrom, portTo string, onReady func()) error {
	return Current.PortForward(namespace, deploymentName, portFrom, portTo, onReady)
}

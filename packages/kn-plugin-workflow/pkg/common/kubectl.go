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
)

type K8sApi interface {
	GetNamespace() (string, error)
	CheckContext() (string, error)
	ExecuteApply(crd, namespace string) error
	ExecuteDelete(crd, namespace string) error
	CheckCrdExists(crd string) error
	GetDeploymentStatus(namespace, deploymentName string) (v1.DeploymentStatus, error)
	PortForward(namespace, serviceName, portFrom, portTo string, onReady func()) error
}

var Current K8sApi = k8sclient.GoAPI{}

func CheckContext() (string, error) {
	return Current.GetNamespace()
}

func GetNamespace() (string, error) {
	return Current.GetNamespace()
}

func ExecuteApply(crd, namespace string) error {
	return Current.ExecuteApply(crd, namespace)
}

func ExecuteDelete(crd, namespace string) error {
	return Current.ExecuteDelete(crd, namespace)
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

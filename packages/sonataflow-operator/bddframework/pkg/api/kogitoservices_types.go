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

package api

import (
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
)

// KogitoServiceConditionType is the type of condition
type KogitoServiceConditionType string

const (
	// DeployedConditionType - The KogitoService is deployed
	DeployedConditionType KogitoServiceConditionType = "Deployed"
	// ProvisioningConditionType - The KogitoService is being provisioned
	ProvisioningConditionType KogitoServiceConditionType = "Provisioning"
	// FailedConditionType - The KogitoService is in a failed state
	FailedConditionType KogitoServiceConditionType = "Failed"
)

// KogitoService defines the interface for any Kogito service that the operator can handle, e.g. Data Index, Jobs Service, Runtimes, etc.
type KogitoService interface {
	client.Object
	// GetSpec gets the Kogito Service specification structure.
	GetSpec() KogitoServiceSpecInterface
	// GetStatus gets the Kogito Service Status structure.
	GetStatus() KogitoServiceStatusInterface
}

// KogitoServiceList defines a base interface for Kogito Service list.
type KogitoServiceList interface {
	runtime.Object
	// GetItems get all items
	GetItems() []KogitoService
}

// KogitoServiceSpecInterface defines the interface for the Kogito service specification, it's the basic structure for any Kogito service.
type KogitoServiceSpecInterface interface {
	GetReplicas() *int32
	SetReplicas(replicas int32)
	GetEnvs() []corev1.EnvVar
	SetEnvs(envs []corev1.EnvVar)
	AddEnvironmentVariable(name, value string)
	AddEnvironmentVariableFromSecret(variableName, secretName, secretKey string)
	GetImage() string
	SetImage(image string)
	GetResources() corev1.ResourceRequirements
	SetResources(resources corev1.ResourceRequirements)
	AddResourceRequest(name, value string)
	AddResourceLimit(name, value string)
	GetDeploymentLabels() map[string]string
	SetDeploymentLabels(labels map[string]string)
	AddDeploymentLabel(name, value string)
	GetServiceLabels() map[string]string
	SetServiceLabels(labels map[string]string)
	AddServiceLabel(name, value string)
	GetRuntime() RuntimeType
	IsRouteDisabled() bool
	SetDisableRoute(disableRoute bool)
	IsInsecureImageRegistry() bool
	GetPropertiesConfigMap() string
	GetInfra() []string
	AddInfra(name string)
	GetMonitoring() MonitoringInterface
	SetMonitoring(monitoring MonitoringInterface)
	GetConfig() map[string]string
	GetProbes() KogitoProbeInterface
	SetProbes(probes KogitoProbeInterface)
	GetTrustStoreSecret() string
	SetTrustStoreSecret(trustStore string)
}

// KogitoServiceStatusInterface defines the basic interface for the Kogito Service status.
type KogitoServiceStatusInterface interface {
	GetConditions() *[]metav1.Condition
	SetConditions(conditions *[]metav1.Condition)
	GetDeploymentConditions() []appsv1.DeploymentCondition
	SetDeploymentConditions(deploymentConditions []appsv1.DeploymentCondition)
	GetRouteConditions() *[]metav1.Condition
	SetRouteConditions(conditions *[]metav1.Condition)
	GetImage() string
	SetImage(image string)
	GetExternalURI() string
	SetExternalURI(uri string)
	GetCloudEvents() KogitoCloudEventsStatusInterface
	SetCloudEvents(cloudEvents KogitoCloudEventsStatusInterface)
}

// KogitoCloudEventsStatusInterface ...
type KogitoCloudEventsStatusInterface interface {
	GetConsumes() []KogitoCloudEventInfoInterface
	SetConsumes(consumes []KogitoCloudEventInfoInterface)
	GetProduces() []KogitoCloudEventInfoInterface
	SetProduces(produces []KogitoCloudEventInfoInterface)
}

// KogitoCloudEventInfoInterface ...
type KogitoCloudEventInfoInterface interface {
	GetType() string
	GetSource() string
}

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

package v1beta1

import (
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/api"
)

// KogitoServiceStatus is the basic structure for any Kogito Service status.
type KogitoServiceStatus struct {
	// +listType=atomic
	// History of conditions for the resource
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:io.kubernetes.conditions"
	Conditions *[]metav1.Condition `json:"conditions"`
	// General conditions for the Kogito Service deployment.
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Deployment Conditions"
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:io.kubernetes.conditions"
	DeploymentConditions []appsv1.DeploymentCondition `json:"deploymentConditions,omitempty"`
	// General conditions for the Kogito Service route.
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Route Conditions"
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:io.kubernetes.conditions"
	RouteConditions *[]metav1.Condition `json:"routeConditions,omitempty"`
	// Image is the resolved image for this service.
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Image string `json:"image,omitempty"`
	// URI is where the service is exposed.
	// +operator-sdk:csv:customresourcedefinitions:type=status
	// +operator-sdk:csv:customresourcedefinitions:type=status,xDescriptors="urn:alm:descriptor:org.w3:link"
	ExternalURI string `json:"externalURI,omitempty"`
	// Describes the CloudEvents that this instance can consume or produce
	// +operator-sdk:csv:customresourcedefinitions:type=status
	CloudEvents KogitoCloudEventsStatus `json:"cloudEvents,omitempty"`
}

// GetConditions ...
func (k *KogitoServiceStatus) GetConditions() *[]metav1.Condition {
	return k.Conditions
}

// SetConditions ...
func (k *KogitoServiceStatus) SetConditions(conditions *[]metav1.Condition) {
	k.Conditions = conditions
}

// GetDeploymentConditions gets the deployment conditions for the service.
func (k *KogitoServiceStatus) GetDeploymentConditions() []appsv1.DeploymentCondition {
	return k.DeploymentConditions
}

// SetDeploymentConditions sets the deployment conditions for the service.
func (k *KogitoServiceStatus) SetDeploymentConditions(deploymentConditions []appsv1.DeploymentCondition) {
	k.DeploymentConditions = deploymentConditions
}

// GetRouteConditions gets the deployment conditions for the service.
func (k *KogitoServiceStatus) GetRouteConditions() *[]metav1.Condition {
	return k.RouteConditions
}

// SetRouteConditions sets the deployment conditions for the service.
func (k *KogitoServiceStatus) SetRouteConditions(conditions *[]metav1.Condition) {
	k.RouteConditions = conditions
}

// GetImage ...
func (k *KogitoServiceStatus) GetImage() string { return k.Image }

// SetImage ...
func (k *KogitoServiceStatus) SetImage(image string) { k.Image = image }

// GetExternalURI ...
func (k *KogitoServiceStatus) GetExternalURI() string { return k.ExternalURI }

// SetExternalURI ...
func (k *KogitoServiceStatus) SetExternalURI(uri string) { k.ExternalURI = uri }

// GetCloudEvents ...
func (k *KogitoServiceStatus) GetCloudEvents() api.KogitoCloudEventsStatusInterface {
	return &k.CloudEvents
}

// SetCloudEvents ...
func (k *KogitoServiceStatus) SetCloudEvents(cloudEvents api.KogitoCloudEventsStatusInterface) {
	if newCloudEvents, ok := cloudEvents.(*KogitoCloudEventsStatus); ok {
		k.CloudEvents = *newCloudEvents
	}
}

// KogitoCloudEventsStatus describes the CloudEvents that can be produced or consumed by this Kogito Service instance
type KogitoCloudEventsStatus struct {
	// +optional
	// +listType=atomic
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Consumes []KogitoCloudEventInfo `json:"consumes,omitempty"`
	// +optional
	// +listType=atomic
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Produces []KogitoCloudEventInfo `json:"produces,omitempty"`
}

// GetConsumes ...
func (k *KogitoCloudEventsStatus) GetConsumes() []api.KogitoCloudEventInfoInterface {
	consumes := make([]api.KogitoCloudEventInfoInterface, len(k.Consumes))
	for i, v := range k.Consumes {
		consumes[i] = api.KogitoCloudEventInfoInterface(v)
	}
	return consumes
}

// SetConsumes ...
func (k *KogitoCloudEventsStatus) SetConsumes(consumes []api.KogitoCloudEventInfoInterface) {
	var newConsumes []KogitoCloudEventInfo
	for _, consume := range consumes {
		if newConsume, ok := consume.(KogitoCloudEventInfo); ok {
			newConsumes = append(newConsumes, newConsume)
		}
	}
	k.Consumes = newConsumes
}

// GetProduces ...
func (k *KogitoCloudEventsStatus) GetProduces() []api.KogitoCloudEventInfoInterface {
	produces := make([]api.KogitoCloudEventInfoInterface, len(k.Produces))
	for i, v := range k.Produces {
		produces[i] = api.KogitoCloudEventInfoInterface(v)
	}
	return produces
}

// SetProduces ...
func (k *KogitoCloudEventsStatus) SetProduces(produces []api.KogitoCloudEventInfoInterface) {
	var newProduces []KogitoCloudEventInfo
	for _, produce := range produces {
		if newProduce, ok := produce.(KogitoCloudEventInfo); ok {
			newProduces = append(newProduces, newProduce)
		}
	}
	k.Produces = newProduces
}

// KogitoCloudEventInfo describes the CloudEvent information based on the specification
type KogitoCloudEventInfo struct {
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Type string `json:"type"`
	// +operator-sdk:csv:customresourcedefinitions:type=status
	Source string `json:"source,omitempty"`
}

// GetType ...
func (k KogitoCloudEventInfo) GetType() string {
	return k.Type
}

// GetSource ...
func (k KogitoCloudEventInfo) GetSource() string {
	return k.Source
}

// KogitoServiceSpec is the basic structure for the Kogito Service specification.
type KogitoServiceSpec struct {

	// Number of replicas that the service will have deployed in the cluster.
	//
	// Default value: 1.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Replicas"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:podCount"
	// +kubebuilder:validation:Minimum=0
	Replicas *int32 `json:"replicas,omitempty"`

	// +optional
	// +listType=atomic
	// Environment variables to be added to the runtime container. Keys must be a C_IDENTIFIER.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	Env []corev1.EnvVar `json:"env,omitempty"`

	// +optional
	// Image definition for the service. Example: "quay.io/kiegroup/kogito-service:latest".
	//
	// On OpenShift an ImageStream will be created in the current namespace pointing to the given image.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:text"
	Image string `json:"image,omitempty"`

	// +optional
	// A flag indicating that image streams created by Kogito Operator should be configured to allow pulling from insecure registries.
	// Usable just on OpenShift.
	//
	// Defaults to 'false'.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Insecure Image Registry"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:booleanSwitch"
	InsecureImageRegistry bool `json:"insecureImageRegistry,omitempty"`

	// Defined compute resource requirements for the deployed service.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:resourceRequirements"
	Resources corev1.ResourceRequirements `json:"resources,omitempty"`

	// Additional labels to be added to the Deployment and Pods managed by the operator.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Additional Deployment Labels"
	DeploymentLabels map[string]string `json:"deploymentLabels,omitempty"`

	// Additional labels to be added to the Service managed by the operator.
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Additional Service Labels"
	ServiceLabels map[string]string `json:"serviceLabels,omitempty"`

	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="ConfigMap Properties"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:io.kubernetes:ConfigMap"
	// Custom ConfigMap with application.properties file to be mounted for the Kogito service.
	//
	// The ConfigMap must be created in the same namespace.
	//
	// Use this property if you need custom properties to be mounted before the application deployment.
	//
	// If left empty, one will be created for you. Later it can be updated to add any custom properties to apply to the service.
	PropertiesConfigMap string `json:"propertiesConfigMap,omitempty"`

	// Infra provides list of dependent KogitoInfra objects.
	// +optional
	Infra []string `json:"infra,omitempty"`

	// Create Service monitor instance to connect with Monitoring service
	// +optional
	Monitoring Monitoring `json:"monitoring,omitempty"`

	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Configs"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:text"
	// Application properties that will be set to the service. For example 'MY_VAR: my_value'.
	Config map[string]string `json:"config,omitempty"`

	// Configure liveness, readiness and startup probes for containers
	// +optional
	Probes KogitoProbe `json:"probes,omitempty"`

	// Custom JKS TrustStore that will be used by this service to make calls to TLS endpoints.
	//
	// It's expected that the secret has two keys: `keyStorePassword` containing the password for the KeyStore
	// and `cacerts` containing the binary data of the given KeyStore.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:io.kubernetes:Secret"
	TrustStoreSecret string `json:"trustStoreSecret,omitempty"`

	// A flag indicating that routes are disabled. Usable just on OpenShift.
	//
	// If not provided, defaults to 'false'.
	// +optional
	// +operator-sdk:csv:customresourcedefinitions:type=spec
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="DisableRoute"
	// +operator-sdk:csv:customresourcedefinitions:type=spec,xDescriptors="urn:alm:descriptor:com.tectonic.ui:booleanSwitch"
	DisableRoute bool `json:"disableRoute,omitempty"`
}

// GetReplicas ...
func (k *KogitoServiceSpec) GetReplicas() *int32 { return k.Replicas }

// SetReplicas ...
func (k *KogitoServiceSpec) SetReplicas(replicas int32) { k.Replicas = &replicas }

// GetEnvs ...
func (k *KogitoServiceSpec) GetEnvs() []corev1.EnvVar { return k.Env }

// SetEnvs ...
func (k *KogitoServiceSpec) SetEnvs(envs []corev1.EnvVar) { k.Env = envs }

// GetImage ...
func (k *KogitoServiceSpec) GetImage() string { return k.Image }

// SetImage ...
func (k *KogitoServiceSpec) SetImage(image string) { k.Image = image }

// GetResources ...
func (k *KogitoServiceSpec) GetResources() corev1.ResourceRequirements { return k.Resources }

// SetResources ...
func (k *KogitoServiceSpec) SetResources(resources corev1.ResourceRequirements) {
	k.Resources = resources
}

// AddEnvironmentVariable adds new environment variable to service environment variables.
func (k *KogitoServiceSpec) AddEnvironmentVariable(name, value string) {
	env := corev1.EnvVar{
		Name:  name,
		Value: value,
	}
	k.Env = append(k.Env, env)
}

// AddEnvironmentVariableFromSecret adds a new environment variable from the secret under the key.
func (k *KogitoServiceSpec) AddEnvironmentVariableFromSecret(variableName, secretName, secretKey string) {
	env := corev1.EnvVar{
		Name: variableName,
		ValueFrom: &corev1.EnvVarSource{
			SecretKeyRef: &corev1.SecretKeySelector{
				LocalObjectReference: corev1.LocalObjectReference{
					Name: secretName,
				},
				Key: secretKey,
			},
		},
	}
	k.Env = append(k.Env, env)
}

// AddResourceRequest adds new resource request. Works also on uninitialized Requests field.
func (k *KogitoServiceSpec) AddResourceRequest(name, value string) {
	if k.Resources.Requests == nil {
		k.Resources.Requests = corev1.ResourceList{}
	}

	k.Resources.Requests[corev1.ResourceName(name)] = resource.MustParse(value)
}

// AddResourceLimit adds new resource limit. Works also on uninitialized Limits field.
func (k *KogitoServiceSpec) AddResourceLimit(name, value string) {
	if k.Resources.Limits == nil {
		k.Resources.Limits = corev1.ResourceList{}
	}

	k.Resources.Limits[corev1.ResourceName(name)] = resource.MustParse(value)
}

// GetDeploymentLabels ...
func (k *KogitoServiceSpec) GetDeploymentLabels() map[string]string { return k.DeploymentLabels }

// SetDeploymentLabels ...
func (k *KogitoServiceSpec) SetDeploymentLabels(labels map[string]string) {
	k.DeploymentLabels = labels
}

// AddDeploymentLabel adds new deployment label. Works also on uninitialized DeploymentLabels field.
func (k *KogitoServiceSpec) AddDeploymentLabel(name, value string) {
	if k.DeploymentLabels == nil {
		k.DeploymentLabels = make(map[string]string)
	}

	k.DeploymentLabels[name] = value
}

// GetServiceLabels ...
func (k *KogitoServiceSpec) GetServiceLabels() map[string]string { return k.ServiceLabels }

// SetServiceLabels ...
func (k *KogitoServiceSpec) SetServiceLabels(labels map[string]string) { k.ServiceLabels = labels }

// AddServiceLabel adds new service label. Works also on uninitialized ServiceLabels field.
func (k *KogitoServiceSpec) AddServiceLabel(name, value string) {
	if k.ServiceLabels == nil {
		k.ServiceLabels = make(map[string]string)
	}

	k.ServiceLabels[name] = value
}

// IsInsecureImageRegistry ...
func (k *KogitoServiceSpec) IsInsecureImageRegistry() bool { return k.InsecureImageRegistry }

// GetPropertiesConfigMap ...
func (k *KogitoServiceSpec) GetPropertiesConfigMap() string {
	return k.PropertiesConfigMap
}

// GetInfra ...
func (k *KogitoServiceSpec) GetInfra() []string { return k.Infra }

// AddInfra ...
func (k *KogitoServiceSpec) AddInfra(name string) {
	k.Infra = append(k.Infra, name)
}

// GetMonitoring ...
func (k *KogitoServiceSpec) GetMonitoring() api.MonitoringInterface {
	return &k.Monitoring
}

// SetMonitoring ...
func (k *KogitoServiceSpec) SetMonitoring(monitoring api.MonitoringInterface) {
	if newMonitoring, ok := monitoring.(*Monitoring); ok {
		k.Monitoring = *newMonitoring
	}
}

// GetConfig ...
func (k *KogitoServiceSpec) GetConfig() map[string]string {
	return k.Config
}

// GetProbes ...
func (k *KogitoServiceSpec) GetProbes() api.KogitoProbeInterface {
	return &k.Probes
}

// SetProbes ...
func (k *KogitoServiceSpec) SetProbes(probes api.KogitoProbeInterface) {
	if newProbes, ok := probes.(*KogitoProbe); ok {
		k.Probes = *newProbes
	}
}

// GetTrustStoreSecret ...
func (k *KogitoServiceSpec) GetTrustStoreSecret() string {
	return k.TrustStoreSecret
}

// SetTrustStoreSecret ...
func (k *KogitoServiceSpec) SetTrustStoreSecret(trustStoreSecret string) {
	k.TrustStoreSecret = trustStoreSecret
}

// IsRouteDisabled ...
func (k *KogitoServiceSpec) IsRouteDisabled() bool {
	return k.DisableRoute
}

// SetDisableRoute ...
func (k *KogitoServiceSpec) SetDisableRoute(disableRoute bool) {
	k.DisableRoute = disableRoute
}

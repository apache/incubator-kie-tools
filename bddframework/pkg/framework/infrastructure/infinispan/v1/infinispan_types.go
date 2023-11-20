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

package v1

// IMPORTANT: run "make codegen" or "operator-sdk generate k8s" to regenerate code after modifying this file
// NOTE: json tags are required. Any new fields you add must have json tags for the fields to be serialized.

import (
	"fmt"
	"strings"

	"github.com/RHsyseng/operator-utils/pkg/olm"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// InfinispanSecurity info for the user application connection
type InfinispanSecurity struct {
	// +optional
	Authorization *Authorization `json:"authorization,omitempty"`
	// +optional
	EndpointAuthentication *bool `json:"endpointAuthentication,omitempty"`
	// +optional
	EndpointSecretName string `json:"endpointSecretName,omitempty"`
	// +optional
	EndpointEncryption *EndpointEncryption `json:"endpointEncryption,omitempty"`
}

// Authorization ...
type Authorization struct {
	// +optional
	Enabled bool `json:"enabled,omitempty"`
	// +optional
	Roles []AuthorizationRole `json:"roles,omitempty"`
}

// AuthorizationRole ...
type AuthorizationRole struct {
	Name        string   `json:"name"`
	Permissions []string `json:"permissions"`
}

// CertificateSourceType specifies all the possible sources for the encryption certificate
// +kubebuilder:validation:Enum=Service;service;Secret;secret;None
type CertificateSourceType string

const (
	// CertificateSourceTypeService certificate coming from a cluster service
	CertificateSourceTypeService CertificateSourceType = "Service"
	// CertificateSourceTypeServiceLowCase certificate coming from a cluster service
	CertificateSourceTypeServiceLowCase CertificateSourceType = "service"

	// CertificateSourceTypeSecret certificate coming from a user provided secret
	CertificateSourceTypeSecret CertificateSourceType = "Secret"
	// CertificateSourceTypeSecretLowCase certificate coming from a user provided secret
	CertificateSourceTypeSecretLowCase CertificateSourceType = "secret"

	// CertificateSourceTypeNoneNoEncryption no certificate encryption disabled
	CertificateSourceTypeNoneNoEncryption CertificateSourceType = "None"
)

// ClientCertType specifies a client certificate validation mechanism.
// +kubebuilder:validation:Enum=None;Authenticate;Validate
type ClientCertType string

const (
	// ClientCertNone No client certificates required
	ClientCertNone ClientCertType = "None"
	// ClientCertAuthenticate All client certificates must be in the configured truststore.
	ClientCertAuthenticate ClientCertType = "Authenticate"
	// ClientCertValidate Client certificates are validated against the CA in the truststore. It is not required for all client certificates to be contained in the trustore.
	ClientCertValidate ClientCertType = "Validate"
)

// EndpointEncryption configuration
type EndpointEncryption struct {
	// +optional
	Type CertificateSourceType `json:"type,omitempty"`
	// +optional
	CertServiceName string `json:"certServiceName,omitempty"`
	// +optional
	CertSecretName string `json:"certSecretName,omitempty"`
	// +optional
	ClientCert ClientCertType `json:"clientCert,omitempty"`
	// +optional
	ClientCertSecretName string `json:"clientCertSecretName,omitempty"`
}

// InfinispanServiceContainerSpec resource requirements specific for service
type InfinispanServiceContainerSpec struct {
	// +optional
	Storage *string `json:"storage,omitempty"`
	// +optional
	EphemeralStorage bool `json:"ephemeralStorage,omitempty"`
	// +optional
	StorageClassName string `json:"storageClassName,omitempty"`
}

// +kubebuilder:validation:Enum=DataGrid;Cache

// ServiceType ...
type ServiceType string

const (
	// ServiceTypeCache Deploys Infinispan to act like a cache. This means:
	// Caches are only used for volatile data.
	// No support for data persistence.
	// Cache definitions can still be permanent, but PV size is not configurable.
	// A default cache is created by default,
	// Additional caches can be created, but only as copies of default cache.
	ServiceTypeCache ServiceType = "Cache"

	// ServiceTypeDataGrid Deploys Infinispan to act like a data grid.
	// More flexibility and more configuration options are available:
	// Cross-site replication, store cached data in persistence store...etc.
	ServiceTypeDataGrid ServiceType = "DataGrid"
)

// InfinispanServiceSpec specify configuration for specific service
type InfinispanServiceSpec struct {
	Type ServiceType `json:"type,omitempty"`
	// +optional
	Container *InfinispanServiceContainerSpec `json:"container,omitempty"`
	// +optional
	Sites *InfinispanSitesSpec `json:"sites,omitempty"`
	// +optional
	ReplicationFactor int32 `json:"replicationFactor,omitempty"`
}

// InfinispanContainerSpec specify resource requirements per container
type InfinispanContainerSpec struct {
	// +optional
	ExtraJvmOpts string `json:"extraJvmOpts,omitempty"`
	// +optional
	Memory string `json:"memory,omitempty"`
	// +optional
	CPU string `json:"cpu,omitempty"`
}

// InfinispanSitesLocalSpec ...
type InfinispanSitesLocalSpec struct {
	Name   string              `json:"name"`
	Expose CrossSiteExposeSpec `json:"expose"`
}

// InfinispanSiteLocationSpec ...
type InfinispanSiteLocationSpec struct {
	Name string `json:"name"`
	// +optional
	Namespace string `json:"namespace,omitempty"`
	// +optional
	ClusterName string `json:"clusterName,omitempty"`
	// Deprecated and to be removed on subsequent release. Use .URL with infinispan+xsite schema instead.
	// +optional
	Host *string `json:"host,omitempty"`
	// Deprecated and to be removed on subsequent release. Use .URL with infinispan+xsite schema instead.
	// +optional
	Port *int32 `json:"port,omitempty"`
	// +kubebuilder:validation:Pattern=`(^(kubernetes|minikube|openshift):\/\/(([a-z0-9]|[a-z0-9][a-z0-9\-]*[a-z0-9])\.)*([a-z0-9]|[a-z0-9][a-z0-9\-]*[a-z0-9])*(:[0-9]+)+$)|(^(infinispan\+xsite):\/\/(([a-z0-9]|[a-z0-9][a-z0-9\-]*[a-z0-9])\.)*([a-z0-9]|[a-z0-9][a-z0-9\-]*[a-z0-9])*(:[0-9]+)*$)`
	// +optional
	URL string `json:"url,omitempty"`
	// +optional
	SecretName string `json:"secretName,omitempty"`
}

// InfinispanSitesSpec ...
type InfinispanSitesSpec struct {
	Local     InfinispanSitesLocalSpec     `json:"local"`
	Locations []InfinispanSiteLocationSpec `json:"locations,omitempty"`
}

// LoggingLevelType describe the logging level for selected category
// +kubebuilder:validation:Enum=trace;debug;info;warn;error
type LoggingLevelType string

const (
	// LoggingLevelTrace ...
	LoggingLevelTrace LoggingLevelType = "trace"
	// LoggingLevelDebug ...
	LoggingLevelDebug LoggingLevelType = "debug"
	// LoggingLevelInfo ...
	LoggingLevelInfo LoggingLevelType = "info"
	// LoggingLevelWarn ...
	LoggingLevelWarn LoggingLevelType = "warn"
	// LoggingLevelError ...
	LoggingLevelError LoggingLevelType = "error"
)

// InfinispanLoggingSpec ...
type InfinispanLoggingSpec struct {
	Categories map[string]LoggingLevelType `json:"categories,omitempty"`
}

// ExposeType describe different exposition methods for Infinispan
// +kubebuilder:validation:Enum=NodePort;LoadBalancer;Route
type ExposeType string

const (
	// ExposeTypeNodePort means a service will be exposed on one port of
	// every node, in addition to 'ClusterIP' type.
	ExposeTypeNodePort = ExposeType(corev1.ServiceTypeNodePort)

	// ExposeTypeLoadBalancer means a service will be exposed via an
	// external load balancer (if the cloud provider supports it), in addition
	// to 'NodePort' type.
	ExposeTypeLoadBalancer = ExposeType(corev1.ServiceTypeLoadBalancer)

	// ExposeTypeRoute means the service will be exposed via
	// `Route` on Openshift or via `Ingress` on Kubernetes
	ExposeTypeRoute ExposeType = "Route"
)

// CrossSiteExposeType describe different exposition methods for Infinispan Cross-Site service
// +kubebuilder:validation:Enum=NodePort;LoadBalancer;ClusterIP
type CrossSiteExposeType string

const (
	// CrossSiteExposeTypeNodePort means a service will be exposed on one port of
	// every node, in addition to 'ClusterIP' type.
	CrossSiteExposeTypeNodePort = CrossSiteExposeType(corev1.ServiceTypeNodePort)

	// CrossSiteExposeTypeLoadBalancer means a service will be exposed via an
	// external load balancer (if the cloud provider supports it), in addition
	// to 'NodePort' type.
	CrossSiteExposeTypeLoadBalancer = CrossSiteExposeType(corev1.ServiceTypeLoadBalancer)

	// CrossSiteExposeTypeClusterIP means an internal 'ClusterIP'
	// service will be created without external exposition
	CrossSiteExposeTypeClusterIP = CrossSiteExposeType(corev1.ServiceTypeClusterIP)
)

// ExposeSpec describe how Infinispan will be exposed externally
type ExposeSpec struct {
	// Type specifies different exposition methods for data grid
	Type ExposeType `json:"type"`
	// +optional
	NodePort int32 `json:"nodePort,omitempty"`
	// +optional
	Port int32 `json:"port,omitempty"`
	// +optional
	Host string `json:"host,omitempty"`
	// +optional
	Annotations map[string]string `json:"annotations,omitempty"`
}

// CrossSiteExposeSpec describe how Infinispan Cross-Site service will be exposed externally
type CrossSiteExposeSpec struct {
	// Type specifies different exposition methods for data grid
	Type CrossSiteExposeType `json:"type"`
	// +optional
	NodePort int32 `json:"nodePort,omitempty"`
	// +optional
	Port int32 `json:"port,omitempty"`
	// +optional
	Annotations map[string]string `json:"annotations,omitempty"`
}

// Autoscale describe autoscaling configuration for the cluster
type Autoscale struct {
	MaxReplicas        int32 `json:"maxReplicas"`
	MinReplicas        int32 `json:"minReplicas"`
	MaxMemUsagePercent int   `json:"maxMemUsagePercent"`
	MinMemUsagePercent int   `json:"minMemUsagePercent"`
	// +optional
	Disabled bool `json:"disabled,omitempty"`
}

// InfinispanExternalDependencies describes all the external dependencies
// used by the Infinispan cluster: i.e. lib folder with custom jar, maven artifact, images ...
type InfinispanExternalDependencies struct {
	// Name of the persistent volume claim with custom libraries
	// +optional
	VolumeClaimName string `json:"volumeClaimName,omitempty"`
}

// InfinispanCloudEvents describes how Infinispan is connected with Cloud Event, see Kafka docs for more info
type InfinispanCloudEvents struct {
	// BootstrapServers is comma separated list of boostrap server:port addresses
	BootstrapServers string `json:"bootstrapServers"`
	// Acks configuration for the producer ack-value
	// +optional
	Acks string `json:"acks,omitempty"`
	// CacheEntriesTopic is the name of the topic on which events will be published
	// +optional
	CacheEntriesTopic string `json:"cacheEntriesTopic,omitempty"`
}

// InfinispanSpec defines the desired state of Infinispan
type InfinispanSpec struct {
	Replicas int32 `json:"replicas"`
	// +optional
	Image *string `json:"image,omitempty"`
	// +optional
	Security InfinispanSecurity `json:"security,omitempty"`
	// +optional
	Container InfinispanContainerSpec `json:"container,omitempty"`
	// +optional
	Service InfinispanServiceSpec `json:"service,omitempty"`
	// +optional
	Logging *InfinispanLoggingSpec `json:"logging,omitempty"`
	// +optional
	Expose *ExposeSpec `json:"expose,omitempty"`
	// +optional
	Autoscale *Autoscale `json:"autoscale,omitempty"`
	// +optional
	Affinity *corev1.Affinity `json:"affinity,omitempty"`
	// +optional
	CloudEvents *InfinispanCloudEvents `json:"cloudEvents,omitempty"`
	// External dependencies needed by the Infinispan cluster
	// +optional
	Dependencies *InfinispanExternalDependencies `json:"dependencies,omitempty"`
}

// ConditionType ...
type ConditionType string

const (
	// ConditionPrelimChecksPassed ...
	ConditionPrelimChecksPassed ConditionType = "PreliminaryChecksPassed"
	// ConditionGracefulShutdown ...
	ConditionGracefulShutdown ConditionType = "GracefulShutdown"
	// ConditionStopping ...
	ConditionStopping ConditionType = "Stopping"
	// ConditionUpgrade ...
	ConditionUpgrade ConditionType = "Upgrade"
	// ConditionWellFormed ...
	ConditionWellFormed ConditionType = "WellFormed"
	// ConditionCrossSiteViewFormed ...
	ConditionCrossSiteViewFormed ConditionType = "CrossSiteViewFormed"
)

// InfinispanCondition define a condition of the cluster
type InfinispanCondition struct {
	// Type is the type of the condition.
	Type ConditionType `json:"type"`
	// Status is the status of the condition.
	Status metav1.ConditionStatus `json:"status"`
	// Human-readable message indicating details about last transition.
	// +optional
	Message string `json:"message,omitempty"`
}

// InfinispanStatus defines the observed state of Infinispan
type InfinispanStatus struct {
	// +optional
	Conditions []InfinispanCondition `json:"conditions,omitempty"`
	// +optional
	StatefulSetName string `json:"statefulSetName,omitempty"`
	// +optional
	Security *InfinispanSecurity `json:"security,omitempty"`
	// +optional
	ReplicasWantedAtRestart int32 `json:"replicasWantedAtRestart,omitempty"`
	// +optional
	PodStatus olm.DeploymentStatus `json:"podStatus,omitempty"`
	// +optional
	ConsoleURL *string `json:"consoleUrl,omitempty"`
}

// +genclient
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

// Infinispan is the Schema for the infinispans API
// +k8s:openapi-gen=true
// +kubebuilder:subresource:status
type Infinispan struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   InfinispanSpec   `json:"spec,omitempty"`
	Status InfinispanStatus `json:"status,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

// InfinispanList contains a list of Infinispan
type InfinispanList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Infinispan `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Infinispan{}, &InfinispanList{})
}

// IsWellFormed return true if cluster is well formed
func (ispn *Infinispan) IsWellFormed() bool {
	return ispn.EnsureClusterStability() == nil
}

// EnsureClusterStability ...
func (ispn *Infinispan) EnsureClusterStability() error {
	conditions := map[ConditionType]metav1.ConditionStatus{
		ConditionGracefulShutdown:   metav1.ConditionFalse,
		ConditionPrelimChecksPassed: metav1.ConditionTrue,
		ConditionUpgrade:            metav1.ConditionFalse,
		ConditionStopping:           metav1.ConditionFalse,
		ConditionWellFormed:         metav1.ConditionTrue,
	}
	return ispn.ExpectConditionStatus(conditions)
}

// ExpectConditionStatus ...
func (ispn *Infinispan) ExpectConditionStatus(expected map[ConditionType]metav1.ConditionStatus) error {
	for key, value := range expected {
		c := ispn.GetCondition(key)
		if c.Status != value {
			if c.Message == "" {
				return fmt.Errorf("key '%s' has Status '%s', expected '%s'", key, c.Status, value)
			}
			return fmt.Errorf("key '%s' has Status '%s', expected '%s' Reason '%s", key, c.Status, value, c.Message)
		}
	}
	return nil
}

// GetCondition return the Status of the given condition or nil
// if condition is not present
func (ispn *Infinispan) GetCondition(condition ConditionType) InfinispanCondition {
	for _, c := range ispn.Status.Conditions {
		if c.Type.equals(condition) {
			return c
		}
	}
	// Absence of condition means `False` value
	return InfinispanCondition{Type: condition, Status: metav1.ConditionFalse}
}

// equals compares two ConditionType's case insensitive
func (a ConditionType) equals(b ConditionType) bool {
	return strings.EqualFold(strings.ToLower(string(a)), strings.ToLower(string(b)))
}

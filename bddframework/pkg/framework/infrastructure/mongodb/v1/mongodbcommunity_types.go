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

import (
	appsv1 "k8s.io/api/apps/v1"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
)

// Type ...
type Type string

const (
	// ReplicaSet ...
	ReplicaSet Type = "ReplicaSet"
)

// Phase ...
type Phase string

const (
	// Running ...
	Running Phase = "Running"
	// Failed ...
	Failed Phase = "Failed"
	// Pending ...
	Pending Phase = "Pending"
)

// MongoDBCommunitySpec defines the desired state of MongoDB
type MongoDBCommunitySpec struct {
	// Members is the number of members in the replica set
	// +optional
	Members int `json:"members"`
	// Type defines which type of MongoDB deployment the resource should create
	// +kubebuilder:validation:Enum=ReplicaSet
	Type Type `json:"type"`
	// Version defines which version of MongoDB will be used
	Version string `json:"version"`

	// Arbiters is the number of arbiters (each counted as a member) in the replica set
	// +optional
	Arbiters int `json:"arbiters"`

	// FeatureCompatibilityVersion configures the feature compatibility version that will
	// be set for the deployment
	// +optional
	FeatureCompatibilityVersion string `json:"featureCompatibilityVersion,omitempty"`

	// ReplicaSetHorizons Add this parameter and values if you need your database
	// to be accessed outside of Kubernetes. This setting allows you to
	// provide different DNS settings within the Kubernetes cluster and
	// to the Kubernetes cluster. The Kubernetes Operator uses split horizon
	// DNS for replica set members. This feature allows communication both
	// within the Kubernetes cluster and from outside Kubernetes.
	// +optional
	ReplicaSetHorizons ReplicaSetHorizonConfiguration `json:"replicaSetHorizons,omitempty"`

	// Security configures security features, such as TLS, and authentication settings for a deployment
	// +required
	Security Security `json:"security"`

	// Users specifies the MongoDB users that should be configured in your deployment
	// +required
	Users []MongoDBUser `json:"users"`

	// +optional
	StatefulSetConfiguration StatefulSetConfiguration `json:"statefulSet,omitempty"`

	// AdditionalMongodConfig is additional configuration that can be passed to
	// each data-bearing mongod at runtime. Uses the same structure as the mongod
	// configuration file: https://docs.mongodb.com/manual/reference/configuration-options/
	// +kubebuilder:validation:Type=object
	// +optional
	// +kubebuilder:pruning:PreserveUnknownFields
	// +nullable
	AdditionalMongodConfig MongodConfiguration `json:"additionalMongodConfig,omitempty"`
}

// ReplicaSetHorizonConfiguration holds the split horizon DNS settings for
// replica set members.
type ReplicaSetHorizonConfiguration []map[string]string

// CustomRole defines a custom MongoDB role.
type CustomRole struct {
	// The name of the role.
	Role string `json:"role"`
	// The database of the role.
	DB string `json:"db"`
	// The privileges to grant the role.
	Privileges []Privilege `json:"privileges"`
	// An array of roles from which this role inherits privileges.
	// +optional
	Roles []Role `json:"roles"`
	// The authentication restrictions the server enforces on the role.
	// +optional
	AuthenticationRestrictions []AuthenticationRestriction `json:"authenticationRestrictions,omitempty"`
}

// Privilege defines the actions a role is allowed to perform on a given resource.
type Privilege struct {
	Resource Resource `json:"resource"`
	Actions  []string `json:"actions"`
}

// Resource specifies specifies the resources upon which a privilege permits actions.
// See https://docs.mongodb.com/manual/reference/resource-document for more.
type Resource struct {
	// +optional
	DB *string `json:"db,omitempty"`
	// +optional
	Collection *string `json:"collection,omitempty"`
	// +optional
	Cluster bool `json:"cluster,omitempty"`
	// +optional
	AnyResource bool `json:"anyResource,omitempty"`
}

// AuthenticationRestriction specifies a list of IP addresses and CIDR ranges users
// are allowed to connect to or from.
type AuthenticationRestriction struct {
	ClientSource  []string `json:"clientSource"`
	ServerAddress []string `json:"serverAddress"`
}

// StatefulSetConfiguration holds the optional custom StatefulSet
// that should be merged into the operator created one.
type StatefulSetConfiguration struct {
	// +kubebuilder:pruning:PreserveUnknownFields
	SpecWrapper StatefulSetSpecWrapper `json:"spec"`
}

// StatefulSetSpecWrapper is a wrapper around StatefulSetSpec with a custom implementation
// of MarshalJSON and UnmarshalJSON which delegate to the underlying Spec to avoid CRD pollution.
type StatefulSetSpecWrapper struct {
	Spec appsv1.StatefulSetSpec `json:"-"`
}

// DeepCopy ...
func (m *StatefulSetSpecWrapper) DeepCopy() *StatefulSetSpecWrapper {
	return &StatefulSetSpecWrapper{
		Spec: m.Spec,
	}
}

// MongodConfiguration holds the optional mongod configuration
// that should be merged with the operator created one.
//
// The CRD generator does not support map[string]interface{}
// on the top level and hence we need to work around this with
// a wrapping struct.
type MongodConfiguration struct {
	Object map[string]interface{} `json:"-"`
}

// DeepCopy ...
func (m *MongodConfiguration) DeepCopy() *MongodConfiguration {
	return &MongodConfiguration{
		Object: runtime.DeepCopyJSON(m.Object),
	}
}

// MongoDBUser ...
type MongoDBUser struct {
	// Name is the username of the user
	Name string `json:"name"`

	// DB is the database the user is stored in. Defaults to "admin"
	// +optional
	DB string `json:"db"`

	// PasswordSecretRef is a reference to the secret containing this user's password
	PasswordSecretRef SecretKeyReference `json:"passwordSecretRef"`

	// Roles is an array of roles assigned to this user
	Roles []Role `json:"roles"`

	// ScramCredentialsSecretName appended by string "scram-credentials" is the name of the secret object created by the mongoDB operator for storing SCRAM credentials
	// +kubebuilder:validation:Pattern=^[a-z0-9]([-a-z0-9]*[a-z0-9])?(\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*$
	ScramCredentialsSecretName string `json:"scramCredentialsSecretName"`
}

// SecretKeyReference is a reference to the secret containing the user's password
type SecretKeyReference struct {
	// Name is the name of the secret storing this user's password
	Name string `json:"name"`

	// Key is the key in the secret storing this password. Defaults to "password"
	// +optional
	Key string `json:"key"`
}

// Role is the database role this user should have
type Role struct {
	// DB is the database the role can act on
	DB string `json:"db"`
	// Name is the name of the role
	Name string `json:"name"`
}

// Security ...
type Security struct {
	// +optional
	Authentication Authentication `json:"authentication"`
	// TLS configuration for both client-server and server-server communication
	// +optional
	TLS TLS `json:"tls"`
	// User-specified custom MongoDB roles that should be configured in the deployment.
	// +optional
	Roles []CustomRole `json:"roles,omitempty"`
}

// TLS is the configuration used to set up TLS encryption
type TLS struct {
	Enabled bool `json:"enabled"`

	// Optional configures if TLS should be required or optional for connections
	// +optional
	Optional bool `json:"optional"`

	// CertificateKeySecret is a reference to a Secret containing a private key and certificate to use for TLS.
	// The key and cert are expected to be PEM encoded and available at "tls.key" and "tls.crt".
	// This is the same format used for the standard "kubernetes.io/tls" Secret type, but no specific type is required.
	// +optional
	CertificateKeySecret LocalObjectReference `json:"certificateKeySecretRef"`

	// CaConfigMap is a reference to a ConfigMap containing the certificate for the CA which signed the server certificates
	// The certificate is expected to be available under the key "ca.crt"
	// +optional
	CaConfigMap LocalObjectReference `json:"caConfigMapRef"`
}

// LocalObjectReference is a reference to another Kubernetes object by name.
// TODO: Replace with a type from the K8s API. CoreV1 has an equivalent
//
//	"LocalObjectReference" type but it contains a TODO in its
//	description that we don't want in our CRD.
type LocalObjectReference struct {
	Name string `json:"name"`
}

// Authentication ...
type Authentication struct {
	// Modes is an array specifying which authentication methods should be enabled.
	Modes []AuthMode `json:"modes"`

	// IgnoreUnknownUsers set to true will ensure any users added manually (not through the CRD)
	// will not be removed.

	// TODO: defaults will work once we update to v1 CRD.

	// +optional
	// +kubebuilder:default:=true
	// +nullable
	IgnoreUnknownUsers *bool `json:"ignoreUnknownUsers,omitempty"`
}

// AuthMode ...
// +kubebuilder:validation:Enum=SCRAM;SCRAM-SHA-256;SCRAM-SHA-1
type AuthMode string

// MongoDBCommunityStatus defines the observed state of MongoDB
type MongoDBCommunityStatus struct {
	MongoURI string `json:"mongoUri"`
	Phase    Phase  `json:"phase"`

	CurrentStatefulSetReplicas int `json:"currentStatefulSetReplicas"`
	CurrentMongoDBMembers      int `json:"currentMongoDBMembers"`

	Message string `json:"message,omitempty"`
}

// +kubebuilder:object:root=true
// +kubebuilder:subresource:status

// MongoDBCommunity is the Schema for the mongodbs API
// +kubebuilder:subresource:status
// +kubebuilder:resource:path=mongodbcommunity,scope=Namespaced,shortName=mdbc,singular=mongodbcommunity
// +kubebuilder:printcolumn:name="Phase",type="string",JSONPath=".status.phase",description="Current state of the MongoDB deployment"
// +kubebuilder:printcolumn:name="Version",type="string",JSONPath=".status.version",description="Version of MongoDB server"
type MongoDBCommunity struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   MongoDBCommunitySpec   `json:"spec,omitempty"`
	Status MongoDBCommunityStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// MongoDBCommunityList contains a list of MongoDB
type MongoDBCommunityList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []MongoDBCommunity `json:"items"`
}

func init() {
	SchemeBuilder.Register(&MongoDBCommunity{}, &MongoDBCommunityList{})
}

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

package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

const (
	// UserFinalizer ...
	UserFinalizer = "user.cleanup"
)

var (
	// UserPhaseReconciled ...
	UserPhaseReconciled StatusPhase = "reconciled"
	// UserPhaseFailing ...
	UserPhaseFailing StatusPhase = "failing"
)

// KeycloakUserSpec defines the desired state of KeycloakUser.
// +k8s:openapi-gen=true
type KeycloakUserSpec struct {
	// Selector for looking up KeycloakRealm Custom Resources.
	// +kubebuilder:validation:Required
	RealmSelector *metav1.LabelSelector `json:"realmSelector,omitempty"`
	// Keycloak User REST object.
	// +kubebuilder:validation:Required
	User KeycloakAPIUser `json:"user"`
}

// KeycloakUserStatus defines the observed state of KeycloakUser.
// +k8s:openapi-gen=true
type KeycloakUserStatus struct {
	// Current phase of the operator.
	Phase StatusPhase `json:"phase"`
	// Human-readable message indicating details about current operator phase or error.
	Message string `json:"message"`
}

// KeycloakUser is the Schema for the keycloakusers API.
// +genclient
// +k8s:openapi-gen=true
// +kubebuilder:subresource:status
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
type KeycloakUser struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KeycloakUserSpec   `json:"spec,omitempty"`
	Status KeycloakUserStatus `json:"status,omitempty"`
}

// KeycloakAPIUser ...
type KeycloakAPIUser struct {
	// User ID.
	// +optional
	ID string `json:"id,omitempty"`
	// User Name.
	// +optional
	UserName string `json:"username,omitempty"`
	// First Name.
	// +optional
	FirstName string `json:"firstName,omitempty"`
	// Last Name.
	// +optional
	LastName string `json:"lastName,omitempty"`
	// Email.
	// +optional
	Email string `json:"email,omitempty"`
	// True if email has already been verified.
	// +optional
	EmailVerified bool `json:"emailVerified,omitempty"`
	// User enabled flag.
	// +optional
	Enabled bool `json:"enabled,omitempty"`
	// A set of Realm Roles.
	// +optional
	RealmRoles []string `json:"realmRoles,omitempty"`
	// A set of Client Roles.
	// +optional
	ClientRoles map[string][]string `json:"clientRoles,omitempty"`
	// A set of Required Actions.
	// +optional
	RequiredActions []string `json:"requiredActions,omitempty"`
	// A set of Groups.
	// +optional
	Groups []string `json:"groups,omitempty"`
	// A set of Federated Identities.
	// +optional
	FederatedIdentities []FederatedIdentity `json:"federatedIdentities,omitempty"`
	// A set of Credentials.
	// +optional
	Credentials []KeycloakCredential `json:"credentials,omitempty"`
	// A set of Attributes.
	// +optional
	Attributes map[string][]string `json:"attributes,omitempty"`
}

// KeycloakCredential ...
type KeycloakCredential struct {
	// Credential Type.
	// +optional
	Type string `json:"type,omitempty"`
	// Credential Value.
	// +optional
	Value string `json:"value,omitempty"`
	// True if this credential object is temporary.
	// +optional
	Temporary bool `json:"temporary,omitempty"`
}

// FederatedIdentity ...
type FederatedIdentity struct {
	// Federated Identity Provider.
	// +optional
	IdentityProvider string `json:"identityProvider,omitempty"`
	// Federated Identity User ID.
	// +optional
	UserID string `json:"userId,omitempty"`
	// Federated Identity User Name.
	// +optional
	UserName string `json:"userName,omitempty"`
}

// KeycloakUserList contains a list of KeycloakUser
// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object
type KeycloakUserList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []KeycloakUser `json:"items"`
}

func init() {
	SchemeBuilder.Register(&KeycloakUser{}, &KeycloakUserList{})
}

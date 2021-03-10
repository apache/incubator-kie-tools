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

package infrastructure

import (
	"github.com/keycloak/keycloak-operator/pkg/apis/keycloak/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/core/operator"
)

const (
	// KeycloakKind refers to Keycloak Kind
	KeycloakKind = "Keycloak"
)

var (
	// KeycloakAPIVersion refers to kafka APIVersion
	KeycloakAPIVersion = v1alpha1.SchemeGroupVersion.String()

	keycloakServerGroup = v1alpha1.SchemeGroupVersion.Group
)

// KeycloakHandler ...
type KeycloakHandler interface {
	IsKeycloakAvailable() bool
}

type keycloakHandler struct {
	*operator.Context
}

// NewKeycloakHandler ...
func NewKeycloakHandler(context *operator.Context) KeycloakHandler {
	return &keycloakHandler{
		context,
	}
}

// IsKeycloakAvailable checks if Strimzi CRD is available in the cluster
func (k *keycloakHandler) IsKeycloakAvailable() bool {
	return k.Client.HasServerGroup(keycloakServerGroup)
}

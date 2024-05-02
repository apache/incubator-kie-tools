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
	"encoding/json"
	"fmt"
	"regexp"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/client/kubernetes"

	keycloak "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/keycloak/v1alpha1"
)

const (
	keycloakKey = "keycloak"
)

// DeployKeycloakInstance deploys an instance of Keycloak
func DeployKeycloakInstance(namespace string) error {
	GetLogger(namespace).Info("Creating Keycloak instance.")

	keycloak := &keycloak.Keycloak{
		ObjectMeta: createKeycloakMeta(namespace, keycloakKey),
		Spec: keycloak.KeycloakSpec{
			Instances: 1,
			ExternalAccess: keycloak.KeycloakExternalAccess{
				Enabled: true,
			},
		},
	}

	if err := kubernetes.ResourceC(kubeClient).Create(keycloak); err != nil {
		return fmt.Errorf("Error while creating Keycloak: %v ", err)
	}

	return WaitForPodsWithLabel(namespace, LabelAppKey, keycloakKey, 2, 10)
}

// DeployKeycloakRealm deploys a realm configuration of Keycloak
func DeployKeycloakRealm(namespace, realmName string) error {
	GetLogger(namespace).Info("Creating Keycloak realm", "realmName", realmName)

	realm := &keycloak.KeycloakRealm{
		ObjectMeta: createKeycloakMeta(namespace, realmName),
		Spec: keycloak.KeycloakRealmSpec{
			InstanceSelector: &metav1.LabelSelector{
				MatchLabels: createKeycloakLabel(namespace),
			},
			Realm: &keycloak.KeycloakAPIRealm{
				ID:      realmName,
				Realm:   realmName,
				Enabled: true,
			},
		},
	}

	return kubernetes.ResourceC(kubeClient).Create(realm)
}

// DeployKeycloakClient deploys a client configuration of Keycloak
func DeployKeycloakClient(namespace, clientName string) error {
	GetLogger(namespace).Info("Creating Keycloak client", "clientName", clientName)
	trueValue := true
	client := &keycloak.KeycloakClient{
		ObjectMeta: createKeycloakMeta(namespace, clientName),
		Spec: keycloak.KeycloakClientSpec{
			RealmSelector: &metav1.LabelSelector{
				MatchLabels: createKeycloakLabel(namespace),
			},
			Client: &keycloak.KeycloakAPIClient{
				ID:                        clientName,
				ClientID:                  clientName,
				Name:                      clientName,
				Enabled:                   true,
				StandardFlowEnabled:       true,
				DirectAccessGrantsEnabled: true,
				ServiceAccountsEnabled:    true,
				PublicClient:              true,
				RedirectUris:              []string{"*"},
				Protocol:                  "openid-connect",
				FullScopeAllowed:          &trueValue,
			},
		},
	}

	return kubernetes.ResourceC(kubeClient).Create(client)
}

// DeployKeycloakUser deploys a realm configuration of Keycloak
func DeployKeycloakUser(namespace, userName, password string) error {
	GetLogger(namespace).Info("Creating Keycloak user %s.", "userName", userName)
	user := &keycloak.KeycloakUser{
		ObjectMeta: createKeycloakMeta(namespace, userName),
		Spec: keycloak.KeycloakUserSpec{
			RealmSelector: &metav1.LabelSelector{
				MatchLabels: createKeycloakLabel(namespace),
			},
			User: keycloak.KeycloakAPIUser{
				ID:            userName,
				UserName:      userName,
				Email:         userName + "@a.com",
				EmailVerified: true,
				Enabled:       true,
				Credentials: []keycloak.KeycloakCredential{
					{
						Type:      "password",
						Value:     password,
						Temporary: false,
					},
				},
			},
		},
	}

	return kubernetes.ResourceC(kubeClient).Create(user)
}

// GetAccessTokenFromKeycloak gets the access token for a user
func GetAccessTokenFromKeycloak(namespace, server, userName, password, realm, clientName string) (string, error) {
	path := fmt.Sprintf("auth/realms/%s/protocol/openid-connect/token", realm)
	body := fmt.Sprintf("client_id=%s&username=%s&password=%s&grant_type=password", clientName, userName, password)
	GetLogger(namespace).Info(fmt.Sprintf("Getting access token for '%s' and server '%s/%s'.", body, server, path))

	requestInfo := NewPOSTHTTPRequestInfo(server, path, "x-www-form-urlencoded", body)
	requestInfo.Unsecure = true

	var target map[string]json.RawMessage
	if err := ExecuteHTTPRequestWithUnmarshalledResponse(namespace, requestInfo, &target); err != nil {
		return "", err
	}

	var accessToken string
	if err := json.Unmarshal(target["access_token"], &accessToken); err != nil {
		return "", err
	}

	return accessToken, nil
}

// RetrieveKeycloakEndpointURI retrieves the keycloak endpoint
func RetrieveKeycloakEndpointURI(namespace string) (string, error) {
	uri, err := WaitAndRetrieveEndpointURI(namespace, keycloakKey)
	if err != nil {
		return "", err
	}

	return regexp.MustCompile(":[0-9]+").ReplaceAllString(uri, ""), nil
}

func createKeycloakMeta(namespace, name string) metav1.ObjectMeta {
	return metav1.ObjectMeta{
		Namespace: namespace,
		Name:      name,
		Labels:    createKeycloakLabel(namespace),
	}
}

func createKeycloakLabel(namespace string) map[string]string {
	return map[string]string{"app": "keycloak-in-" + namespace}
}

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

package installers

import (
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	keycloak "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure/keycloak/v1alpha1"
)

var (
	// keycloakOlmNamespacedInstaller installs Keycloak in the namespace using OLM
	keycloakOlmNamespacedInstaller = OlmNamespacedServiceInstaller{
		SubscriptionName:                  "keycloak-operator",
		Channel:                           "fast",
		Catalog:                           framework.GetCommunityCatalog,
		InstallationTimeoutInMinutes:      10,
		GetAllNamespacedOlmCrsInNamespace: getKeycloakCrsInNamespace,
	}
)

// GetKeycloakInstaller returns Keycloak installer
func GetKeycloakInstaller() ServiceInstaller {
	return &keycloakOlmNamespacedInstaller
}

func getKeycloakCrsInNamespace(namespace string) ([]client.Object, error) {
	var crs []client.Object

	keycloaks := &keycloak.KeycloakList{}
	if err := framework.GetObjectsInNamespace(namespace, keycloaks); err != nil {
		return nil, err
	}
	for i := range keycloaks.Items {
		crs = append(crs, &keycloaks.Items[i])
	}

	keycloakClients := &keycloak.KeycloakClientList{}
	if err := framework.GetObjectsInNamespace(namespace, keycloakClients); err != nil {
		return nil, err
	}
	for i := range keycloakClients.Items {
		crs = append(crs, &keycloakClients.Items[i])
	}

	keycloakUsers := &keycloak.KeycloakUserList{}
	if err := framework.GetObjectsInNamespace(namespace, keycloakUsers); err != nil {
		return nil, err
	}
	for i := range keycloakUsers.Items {
		crs = append(crs, &keycloakUsers.Items[i])
	}

	keycloakRealms := &keycloak.KeycloakRealmList{}
	if err := framework.GetObjectsInNamespace(namespace, keycloakRealms); err != nil {
		return nil, err
	}
	for i := range keycloakRealms.Items {
		crs = append(crs, &keycloakRealms.Items[i])
	}

	return crs, nil
}

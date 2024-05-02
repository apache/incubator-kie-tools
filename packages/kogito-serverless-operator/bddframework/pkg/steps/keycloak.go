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

package steps

import (
	"github.com/cucumber/godog"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/installers"
)

func registerKeycloakSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Keycloak Operator is deployed$`, data.keycloakOperatorIsDeployed)
	ctx.Step(`^Keycloak instance is deployed$`, data.keycloakInstanceIsDeployed)
	ctx.Step(`^Keycloak instance with realm "([^"]*)" and client "([^"]*)" is deployed$`, data.keycloakInstanceWithRealmAndClientIsDeployed)
	ctx.Step(`^Keycloak realm "([^"]*)" is deployed$`, data.keycloakRealmIsDeployed)
	ctx.Step(`^Keycloak client "([^"]*)" is deployed$`, data.keycloakClientIsDeployed)
	ctx.Step(`^Keycloak user "([^"]*)" with password "([^"]*)" is deployed$`, data.keycloakUserWithPasswordIsDeployed)

	ctx.Step(`^Stores access token for user "([^"]*)" and password "([^"]*)" on realm "([^"]*)" and client "([^"]*)" into variable "([^"]*)"$`, data.storesAccessTokenForUserAndPasswordOnRealmAndClientIntoVariable)
}

func (data *Data) keycloakOperatorIsDeployed() error {
	return installers.GetKeycloakInstaller().Install(data.Namespace)
}

func (data *Data) keycloakInstanceIsDeployed() error {
	return framework.DeployKeycloakInstance(data.Namespace)
}

func (data *Data) keycloakInstanceWithRealmAndClientIsDeployed(realm, clientName string) error {
	if err := data.keycloakInstanceIsDeployed(); err != nil {
		return err
	}

	if err := data.keycloakRealmIsDeployed(realm); err != nil {
		return err
	}

	return data.keycloakClientIsDeployed(clientName)
}

func (data *Data) keycloakRealmIsDeployed(realm string) error {
	return framework.DeployKeycloakRealm(data.Namespace, realm)
}

func (data *Data) keycloakClientIsDeployed(clientName string) error {
	return framework.DeployKeycloakClient(data.Namespace, clientName)
}

func (data *Data) keycloakUserWithPasswordIsDeployed(userName, password string) error {
	return framework.DeployKeycloakUser(data.Namespace, userName, password)
}

func (data *Data) storesAccessTokenForUserAndPasswordOnRealmAndClientIntoVariable(userName, password, realm, clientName, contextKey string) error {
	uri, err := framework.RetrieveKeycloakEndpointURI(data.Namespace)
	if err != nil {
		return err
	}

	accessToken, err := framework.GetAccessTokenFromKeycloak(data.Namespace, uri, userName, password, realm, clientName)
	if err != nil {
		return err
	}

	data.ScenarioContext[contextKey] = accessToken

	return nil
}

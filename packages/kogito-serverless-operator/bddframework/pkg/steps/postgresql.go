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
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
)

/*
	DataTable for PostgreSQL:
	| username | developer |
	| password | mypass    |
	| database | kogito    |
*/

func registerPostgresqlSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^PostgreSQL instance "([^"]*)" is deployed within (\d+) (?:minute|minutes) with configuration:$`, data.postgresqlInstanceIsDeployedWithConfiguration)

	ctx.Step(`^Scale PostgreSQL instance "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scalePostgresqlInstanceToPodsWithinMinutes)
}

func (data *Data) postgresqlInstanceIsDeployedWithConfiguration(name string, timeOutInMin int, table *godog.Table) error {
	creds := &mappers.PostgresqlCredentialsConfig{}
	if err := mappers.MapPostgresqlCredentialsFromTable(table, creds); err != nil {
		return err
	}

	err := framework.CreatePostgresqlInstance(data.Namespace, name, 1, creds.Username, creds.Password, creds.Database)
	if err != nil {
		return err
	}

	return framework.WaitForPostgresqlInstance(data.Namespace, 1, 3)
}

func (data *Data) scalePostgresqlInstanceToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetPostgresqlReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForPostgresqlInstance(data.Namespace, nbPods, 3)
}

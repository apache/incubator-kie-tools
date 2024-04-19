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
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
)

/*
	DataTable for MongoDB:
	| username | developer |
	| password | mypass    |
	| database | kogito    |
*/

func registerMongoDBSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^MongoDB Operator is deployed$`, data.mongoDbOperatorIsDeployed)
	ctx.Step(`^MongoDB instance "([^"]*)" has (\d+) (?:pod|pods) running within (\d+) (?:minute|minutes)$`, data.mongodbInstanceHasPodsRunningWithinMinutes)
	ctx.Step(`^MongoDB instance "([^"]*)" is deployed with configuration:$`, data.mongodbInstanceIsDeployedWithConfiguration)

	ctx.Step(`^Scale MongoDB instance "([^"]*)" to (\d+) pods within (\d+) minutes$`, data.scaleMongoDBInstanceToPodsWithinMinutes)
}

func (data *Data) mongoDbOperatorIsDeployed() error {
	return installers.GetMongoDbInstaller().Install(data.Namespace)
}

func (data *Data) mongodbInstanceHasPodsRunningWithinMinutes(name string, numberOfPods, timeOutInMin int) error {
	return framework.WaitForPodsWithLabel(data.Namespace, "app", name+"-svc", numberOfPods, timeOutInMin)
}

func (data *Data) mongodbInstanceIsDeployedWithConfiguration(name string, table *godog.Table) error {
	mongoDBSecretName := name + "-secret"
	creds := &mappers.MongoDBCredentialsConfig{}
	if err := mappers.MapMongoDBCredentialsFromTable(table, creds); err != nil {
		return err
	}

	if err := framework.CreateMongoDBSecret(data.Namespace, mongoDBSecretName, creds.Password); err != nil {
		return err
	}

	mongodb := framework.GetMongoDBStub(framework.IsOpenshift(), data.Namespace, name, []framework.MongoDBUserCred{
		{
			Name:         creds.Username,
			AuthDatabase: creds.AuthDatabase,
			SecretName:   mongoDBSecretName,
			Databases:    []string{creds.Database},
		},
	})
	if err := framework.DeployMongoDBInstance(data.Namespace, mongodb); err != nil {
		return err
	}

	return framework.WaitForPodsWithLabel(data.Namespace, "app", name+"-svc", 1, 3)
}

func (data *Data) scaleMongoDBInstanceToPodsWithinMinutes(name string, nbPods, timeoutInMin int) error {
	err := framework.SetMongoDBReplicas(data.Namespace, name, nbPods)
	if err != nil {
		return err
	}
	return framework.WaitForPodsWithLabel(data.Namespace, "app", name+"-svc", nbPods, timeoutInMin)
}

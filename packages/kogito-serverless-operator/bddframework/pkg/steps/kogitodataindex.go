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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/config"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/framework/infrastructure"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/steps/mappers"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
)

/*
	DataTable for Data Index:
	| config          | database       | <Infinispan|MongoDB>      |
	| config          | infra          | <KogitoInfra name>        |
	| runtime-request | cpu/memory     | value                     |
	| runtime-limit   | cpu/memory     | value                     |
	| runtime-env     | varName        | varValue                  |
*/

func registerKogitoDataIndexServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Data Index with (\d+) replicas$`, data.installKogitoDataIndexServiceWithReplicas)
	ctx.Step(`^Install Kogito Data Index with (\d+) replicas with configuration:$`, data.installKogitoDataIndexServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Data Index has (\d+) pods running within (\d+) minutes$`, data.kogitoDataIndexHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoDataIndexServiceWithReplicas(replicas int) error {
	dataIndex := framework.GetKogitoDataIndexResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), &bddtypes.KogitoServiceHolder{KogitoService: dataIndex})
}

func (data *Data) installKogitoDataIndexServiceWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	dataIndex := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoDataIndexResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, dataIndex); err != nil {
		return err
	}
	if dataIndex.DatabaseType == infrastructure.MongoDBKind {
		framework.GetMainLogger().Debug("Setting Data Index MongoDB image")
		dataIndex.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.DataIndexImageType, config.MongoDBPersistenceType), framework.DataIndexMongoDBImageName))
	} else if dataIndex.DatabaseType == "PostgreSQL" {
		framework.GetMainLogger().Debug("Setting Data Index PostgreSQL image")
		dataIndex.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.DataIndexImageType, config.PosgresqlPersistenceType), framework.DataIndexPostgresqlImageName))
	}

	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), dataIndex)
}

func (data *Data) kogitoDataIndexHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoDataIndexService(data.Namespace, podNb, timeoutInMin)
}

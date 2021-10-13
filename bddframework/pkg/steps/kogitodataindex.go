// Copyright 2019 Red Hat, Inc. and/or its affiliates
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

package steps

import (
	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-operator/core/infrastructure"
	"github.com/kiegroup/kogito-operator/core/kogitosupportingservice"
	"github.com/kiegroup/kogito-operator/test/pkg/config"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
	"github.com/kiegroup/kogito-operator/test/pkg/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-operator/test/pkg/types"
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
		dataIndex.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.DataIndexImageType, config.MongoDBPersistenceType), kogitosupportingservice.DataIndexMongoDBImageName))
	} else if dataIndex.DatabaseType == "PostgreSQL" {
		framework.GetMainLogger().Debug("Setting Data Index PostgreSQL image")
		dataIndex.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.DataIndexImageType, config.PosgresqlPersistenceType), kogitosupportingservice.DataIndexPostgresqlImageName))
	}

	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), dataIndex)
}

func (data *Data) kogitoDataIndexHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoDataIndexService(data.Namespace, podNb, timeoutInMin)
}

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
	DataTable for Jobs Service:
	| config           | infra             | <KogitoInfra name>        |
	| runtime-request  | cpu/memory        | value                     |
	| runtime-limit    | cpu/memory        | value                     |
	| runtime-env      | varName           | varValue                  |
*/

func registerKogitoJobsServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Jobs Service with (\d+) replicas$`, data.installKogitoJobsServiceWithReplicas)
	ctx.Step(`^Install Kogito Jobs Service with (\d+) replicas with configuration:$`, data.installKogitoJobsServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Jobs Service has (\d+) pods running within (\d+) minutes$`, data.kogitoJobsServiceHasPodsRunningWithinMinutes)
	ctx.Step(`^Scale Kogito Jobs Service to (\d+) pods within (\d+) minutes$`, data.scaleKogitoJobsServiceToPodsWithinMinutes)
	ctx.Step(`^Kogito Jobs Service log contains text "([^"]*)" within (\d+) minutes$`, data.kogitoJobsServiceLogContainsTextWithinMinutes)
}

func (data *Data) installKogitoJobsServiceWithReplicas(replicas int) error {
	jobsService := framework.GetKogitoJobsServiceResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoJobsService(framework.GetDefaultInstallerType(), &bddtypes.KogitoServiceHolder{KogitoService: jobsService})
}

func (data *Data) installKogitoJobsServiceWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	jobsService := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoJobsServiceResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, jobsService); err != nil {
		return err
	}
	if jobsService.DatabaseType == infrastructure.InfinispanKind {
		framework.GetMainLogger().Debug("Setting Jobs service Infinispan image")
		jobsService.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.JobServiceImageType, config.InfinispanPersistenceType), framework.JobsServiceInfinispanImageName))
	} else if jobsService.DatabaseType == infrastructure.MongoDBKind {
		framework.GetMainLogger().Debug("Setting Jobs service MongoDB image")
		jobsService.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.JobServiceImageType, config.MongoDBPersistenceType), framework.JobsServiceMongoDBImageName))
	} else if jobsService.DatabaseType == "PostgreSQL" {
		framework.GetMainLogger().Debug("Setting Jobs service PostgreSQL image")
		jobsService.KogitoService.GetSpec().SetImage(framework.NewImageOrDefault(config.GetServiceImageTag(config.JobServiceImageType, config.PosgresqlPersistenceType), framework.JobsServicePostgresqlImageName))
	}

	return framework.InstallKogitoJobsService(framework.GetDefaultInstallerType(), jobsService)
}

func (data *Data) kogitoJobsServiceHasPodsRunningWithinMinutes(pods, timeoutInMin int) error {
	return framework.WaitForKogitoJobsService(data.Namespace, pods, timeoutInMin)
}

func (data *Data) scaleKogitoJobsServiceToPodsWithinMinutes(nbPods, timeoutInMin int) error {
	err := framework.SetKogitoJobsServiceReplicas(data.Namespace, int32(nbPods))
	if err != nil {
		return err
	}
	return framework.WaitForKogitoJobsService(data.Namespace, nbPods, timeoutInMin)
}

func (data *Data) kogitoJobsServiceLogContainsTextWithinMinutes(logText string, timeoutInMin int) error {
	return framework.WaitForKogitoJobsServiceLogContainsTextWithinMinutes(data.Namespace, logText, timeoutInMin)
}

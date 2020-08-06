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
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

/*
	DataTable for Jobs Service:
	| config           | enableEvents      | enabled/disabled          |
	| config           | enablePersistence | enabled/disabled          |
	| infinispan       | username          | developer                 |
	| infinispan       | password          | mypass                    |
	| infinispan       | uri               | external-infinispan:11222 |
	| kafka            | externalURI       | kafka-bootstrap:9092      |
	| kafka            | instance          | external-kafka            |
	| runtime-request  | cpu/memory        | value                     |
	| runtime-limit    | cpu/memory        | value                     |
	| runtime-env      | varName           | varValue                  |
*/

func registerKogitoJobsServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Jobs Service with (\d+) replicas$`, data.installKogitoJobsServiceWithReplicas)
	ctx.Step(`^Install Kogito Jobs Service with (\d+) replicas with configuration:$`, data.installKogitoJobsServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Jobs Service has (\d+) pods running within (\d+) minutes$`, data.kogitoJobsServiceHasPodsRunningWithinMinutes)
	ctx.Step(`^Scale Kogito Jobs Service to (\d+) pods within (\d+) minutes$`, data.scaleKogitoJobsServiceToPodsWithinMinutes)
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

	if jobsService.IsInfinispanUsernameSpecified() && framework.GetDefaultInstallerType() == framework.CRInstallerType {
		// If Infinispan authentication is set and CR installer is used, the Secret holding Infinispan credentials needs to be created and passed to Data index CR.
		if err := framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: jobsService.Infinispan.Username, passwordSecretKey: jobsService.Infinispan.Password}); err != nil {
			return err
		}
		jobsService.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.SecretName = kogitoExternalInfinispanSecret
		jobsService.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.UsernameKey = usernameSecretKey
		jobsService.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.PasswordKey = passwordSecretKey
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

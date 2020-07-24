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
	DataTable for Data Index:
	| infinispan      | username    | developer                 |
	| infinispan      | password    | mypass                    |
	| infinispan      | uri         | external-infinispan:11222 |
	| kafka           | externalURI | kafka-bootstrap:9092      |
	| kafka           | instance    | external-kafka            |
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| runtime-env     | varName     | varValue                  |
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

	if dataIndex.IsInfinispanUsernameSpecified() && framework.GetDefaultInstallerType() == framework.CRInstallerType {
		// If Infinispan authentication is set and CR installer is used, the Secret holding Infinispan credentials needs to be created and passed to Data index CR.
		if err := framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: dataIndex.Infinispan.Username, passwordSecretKey: dataIndex.Infinispan.Password}); err != nil {
			return err
		}
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.SecretName = kogitoExternalInfinispanSecret
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.UsernameKey = usernameSecretKey
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.PasswordKey = passwordSecretKey
	}

	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), dataIndex)
}

func (data *Data) kogitoDataIndexHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoDataIndexService(data.Namespace, podNb, timeoutInMin)
}

// Copyright 2020 Red Hat, Inc. and/or its affiliates
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
	DataTable for Trusty:
	| infinispan      | username    | developer                 |
	| infinispan      | password    | mypass                    |
	| infinispan      | uri         | external-infinispan:11222 |
	| kafka           | externalURI | kafka-bootstrap:9092      |
	| kafka           | instance    | external-kafka            |
	| runtime-request | cpu/memory  | value                     |
	| runtime-limit   | cpu/memory  | value                     |
	| runtime-env     | varName     | varValue                  |
*/

func registerKogitoTrustyServiceSteps(ctx *godog.ScenarioContext, data *Data) {
	ctx.Step(`^Install Kogito Trusty with (\d+) replicas$`, data.installKogitoTrustyServiceWithReplicas)
	ctx.Step(`^Install Kogito Trusty with (\d+) replicas with configuration:$`, data.installKogitoTrustyServiceWithReplicasWithConfiguration)
	ctx.Step(`^Kogito Trusty has (\d+) pods running within (\d+) minutes$`, data.kogitoTrustyHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoTrustyServiceWithReplicas(replicas int) error {
	trusty := framework.GetKogitoTrustyResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoTrustyService(data.Namespace, framework.GetDefaultInstallerType(), &bddtypes.KogitoServiceHolder{KogitoService: trusty})
}

func (data *Data) installKogitoTrustyServiceWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	trusty := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoTrustyResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, trusty); err != nil {
		return err
	}

	if trusty.IsInfinispanUsernameSpecified() && framework.GetDefaultInstallerType() == framework.CRInstallerType {
		// If Infinispan authentication is set and CR installer is used, the Secret holding Infinispan credentials needs to be created and passed to Trusty CR.
		if err := framework.CreateSecret(data.Namespace, kogitoExternalInfinispanSecret, map[string]string{usernameSecretKey: trusty.Infinispan.Username, passwordSecretKey: trusty.Infinispan.Password}); err != nil {
			return err
		}
		trusty.KogitoService.(*v1alpha1.KogitoTrusty).Spec.InfinispanProperties.Credentials.SecretName = kogitoExternalInfinispanSecret
		trusty.KogitoService.(*v1alpha1.KogitoTrusty).Spec.InfinispanProperties.Credentials.UsernameKey = usernameSecretKey
		trusty.KogitoService.(*v1alpha1.KogitoTrusty).Spec.InfinispanProperties.Credentials.PasswordKey = passwordSecretKey
	}

	return framework.InstallKogitoTrustyService(data.Namespace, framework.GetDefaultInstallerType(), trusty)
}

func (data *Data) kogitoTrustyHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoTrustyService(data.Namespace, podNb, timeoutInMin)
}

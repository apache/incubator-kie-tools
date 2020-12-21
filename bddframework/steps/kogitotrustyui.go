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
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
	"github.com/kiegroup/kogito-cloud-operator/test/steps/mappers"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

// RegisterCliSteps register all CLI steps existing
func registerKogitoTrustyUISteps(s *godog.ScenarioContext, data *Data) {
	s.Step(`^Install Kogito Trusty UI with (\d+) replicas$`, data.installKogitoTrustyUIWithReplicas)
	s.Step(`^Install Kogito Trusty UI with (\d+) replicas with configuration:$`, data.installKogitoTrustyUIWithReplicasWithConfiguration)
	s.Step(`^Kogito Trusty UI has (\d+) pods running within (\d+) minutes$`, data.kogitoTrustyUIHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoTrustyUIWithReplicas(replicas int) error {
	trustyUI := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoTrustyUIResourceStub(data.Namespace, replicas),
	}

	// Can be removed once https://issues.redhat.com/browse/KOGITO-1141 is implemented
	if !framework.IsOpenshift() {
		if err := addTrustyIngressURIEnvVariable(data.Namespace, trustyUI); err != nil {
			return err
		}
	}

	return framework.InstallKogitoTrustyUI(framework.GetDefaultInstallerType(), trustyUI)
}

func (data *Data) installKogitoTrustyUIWithReplicasWithConfiguration(replicas int, table *godog.Table) error {
	trustyUI := &bddtypes.KogitoServiceHolder{
		KogitoService: framework.GetKogitoTrustyUIResourceStub(data.Namespace, replicas),
	}

	if err := mappers.MapKogitoServiceTable(table, trustyUI); err != nil {
		return err
	}

	// Can be removed once https://issues.redhat.com/browse/KOGITO-1141 is implemented
	if !framework.IsOpenshift() {
		if err := addTrustyIngressURIEnvVariable(data.Namespace, trustyUI); err != nil {
			return err
		}
	}

	return framework.InstallKogitoTrustyUI(framework.GetDefaultInstallerType(), trustyUI)
}

func (data *Data) kogitoTrustyUIHasPodsRunningWithinMinutes(pods, timeoutInMin int) error {
	return framework.WaitForKogitoTrustyUIService(data.Namespace, pods, timeoutInMin)
}

func addTrustyIngressURIEnvVariable(namespace string, trustyUI *bddtypes.KogitoServiceHolder) error {
	trustyURI, err := framework.GetIngressURI(namespace, "trusty")
	if err != nil {
		framework.GetLogger(namespace).Warn("Error while retrieving Ingress route with name trusty, skipping trusty endpoint configuration in Trusty UI: %v", "error", err)
		return nil
	}
	trustyUI.KogitoService.GetSpec().AddEnvironmentVariable("KOGITO_TRUSTY_ENDPOINT", trustyURI)
	return nil
}

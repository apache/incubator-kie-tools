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
	"fmt"

	"github.com/cucumber/godog"
	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

const (
	//DataTable first column
	infinispanKey = "infinispan"

	//DataTable second column
	dataIndexInfinispanUsernameKey = "username"
	dataIndexInfinispanPasswordKey = "password"
	uriKey                         = "uri"
)

func registerKogitoDataIndexServiceSteps(s *godog.Suite, data *Data) {
	s.Step(`^Install Kogito Data Index with (\d+) replicas$`, data.installKogitoDataIndexServiceWithReplicas)
	s.Step(`^Install Kogito Data Index with (\d+) replicas with configuration:$`, data.installKogitoDataIndexServiceWithReplicasWithConfiguration)
	s.Step(`^Kogito Data Index has (\d+) pods running within (\d+) minutes$`, data.kogitoDataIndexHasPodsRunningWithinMinutes)
}

func (data *Data) installKogitoDataIndexServiceWithReplicas(replicas int) error {
	dataIndex := framework.GetKogitoDataIndexResourceStub(data.Namespace, replicas)
	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), &framework.KogitoServiceHolder{KogitoService: dataIndex})
}

func (data *Data) installKogitoDataIndexServiceWithReplicasWithConfiguration(replicas int, table *messages.PickleStepArgument_PickleTable) error {
	dataIndex := &framework.KogitoServiceHolder{
		KogitoService: framework.GetKogitoDataIndexResourceStub(data.Namespace, replicas),
	}

	if err := configureDataIndexFromTable(table, dataIndex); err != nil {
		return err
	}

	if dataIndex.IsInfinispanUsernameSpecified() && framework.GetDefaultInstallerType() == framework.CRInstallerType {
		// If Infinispan authentication is set and CR installer is used, the Secret holding Infinispan credentials needs to be created and passed to Data index CR.
		secretName := "kogito-external-infinispan-secret"
		usernameSecretKey := "user"
		passwordSecretKey := "pass"

		framework.CreateSecret(data.Namespace, secretName, map[string]string{usernameSecretKey: dataIndex.Infinispan.Username, passwordSecretKey: dataIndex.Infinispan.Password})
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.SecretName = secretName
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.UsernameKey = usernameSecretKey
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.Credentials.PasswordKey = passwordSecretKey
	}

	return framework.InstallKogitoDataIndexService(data.Namespace, framework.GetDefaultInstallerType(), dataIndex)
}

func (data *Data) kogitoDataIndexHasPodsRunningWithinMinutes(podNb, timeoutInMin int) error {
	return framework.WaitForKogitoDataIndexService(data.Namespace, podNb, timeoutInMin)
}

// Table parsing

func configureDataIndexFromTable(table *messages.PickleStepArgument_PickleTable, dataIndex *framework.KogitoServiceHolder) error {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 3 {
		return fmt.Errorf("expected table to have exactly three columns")
	}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case infinispanKey:
			parseDataIndexInfinispanRow(row, dataIndex)

		case runtimeEnvKey:
			dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case runtimeRequestKey:
			dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case runtimeLimitKey:
			dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}

	return nil
}

func parseDataIndexInfinispanRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, dataIndex *framework.KogitoServiceHolder) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case dataIndexInfinispanUsernameKey:
		dataIndex.Infinispan.Username = getThirdColumn(row)

	case dataIndexInfinispanPasswordKey:
		dataIndex.Infinispan.Password = getThirdColumn(row)

	case uriKey:
		dataIndex.KogitoService.(*v1alpha1.KogitoDataIndex).Spec.InfinispanProperties.URI = getThirdColumn(row)
	}
}

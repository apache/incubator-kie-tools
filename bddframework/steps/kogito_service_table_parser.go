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
	"fmt"
	"strconv"

	"github.com/cucumber/messages-go/v10"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	kogitoServiceInfinispanKey      = "infinispan"
	kogitoServiceKafkaKey           = "kafka"
	kogitoServiceRuntimeRequestKey  = "runtime-request"
	kogitoServiceRuntimeLimitKey    = "runtime-limit"
	kogitoServiceRuntimeEnvKey      = "runtime-env"
	kogitoServiceServiceLabelKey    = "service-label"
	kogitoServiceDeploymentLabelKey = "deployment-label"
	kogitoServiceConfigKey          = "config"

	// DataTable second column
	kogitoServiceInfinispanUseKogitoInfraKey = "useKogitoInfra"
	kogitoServiceInfinispanUsernameKey       = "username"
	kogitoServiceInfinispanPasswordKey       = "password"
	kogitoServiceInfinispanURIKey            = "uri"
	kogitoServiceKafkaUseKogitoInfraKey      = "useKogitoInfra"
	kogitoServiceKafkaExternalURIKey         = "externalURI"
	kogitoServiceKafkaInstanceKey            = "instance"
	kogitoServiceHTTPPortKey                 = "httpPort"
)

func configureKogitoServiceFromTable(table *messages.PickleStepArgument_PickleTable, kogitoService *framework.KogitoServiceHolder) (err error) {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 3 {
		return fmt.Errorf("expected table to have exactly three columns")
	}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)

		switch firstColumn {
		case kogitoServiceInfinispanKey:
			err = parseKogitoServiceInfinispanRow(row, kogitoService)

		case kogitoServiceKafkaKey:
			err = parseKogitoServiceKafkaRow(row, kogitoService)

		case kogitoServiceServiceLabelKey:
			kogitoService.KogitoService.GetSpec().GetServiceLabels()[getSecondColumn(row)] = getThirdColumn(row)

		case kogitoServiceDeploymentLabelKey:
			kogitoService.KogitoService.GetSpec().GetDeploymentLabels()[getSecondColumn(row)] = getThirdColumn(row)

		case kogitoServiceRuntimeEnvKey:
			kogitoService.KogitoService.GetSpec().AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

		case kogitoServiceRuntimeRequestKey:
			kogitoService.KogitoService.GetSpec().AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

		case kogitoServiceRuntimeLimitKey:
			kogitoService.KogitoService.GetSpec().AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

		case kogitoServiceConfigKey:
			err = parseKogitoServiceConfigRow(row, kogitoService)

		default:
			err = fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}

		if err != nil {
			return
		}
	}

	return
}

func parseKogitoServiceInfinispanRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoService *framework.KogitoServiceHolder) error {
	secondColumn := getSecondColumn(row)

	if infinispanAware, ok := kogitoService.KogitoService.GetSpec().(v1alpha1.InfinispanAware); ok {
		switch secondColumn {
		case kogitoServiceInfinispanUseKogitoInfraKey:
			infinispanAware.GetInfinispanProperties().UseKogitoInfra = framework.MustParseEnabledDisabled(getThirdColumn(row))

		case kogitoServiceInfinispanUsernameKey:
			kogitoService.Infinispan.Username = getThirdColumn(row)

		case kogitoServiceInfinispanPasswordKey:
			kogitoService.Infinispan.Password = getThirdColumn(row)

		case kogitoServiceInfinispanURIKey:
			infinispanAware.GetInfinispanProperties().URI = getThirdColumn(row)
		}
	} else {
		return fmt.Errorf("Kogito service %s doesn't support Infinispan configuration", kogitoService.KogitoService.GetName())
	}
	return nil
}

func parseKogitoServiceKafkaRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoService *framework.KogitoServiceHolder) error {
	secondColumn := getSecondColumn(row)

	if kafkaAware, ok := kogitoService.KogitoService.GetSpec().(v1alpha1.KafkaAware); ok {
		switch secondColumn {
		case kogitoServiceKafkaUseKogitoInfraKey:
			kafkaAware.GetKafkaProperties().UseKogitoInfra = framework.MustParseEnabledDisabled(getThirdColumn(row))

		case kogitoServiceKafkaExternalURIKey:
			kafkaAware.GetKafkaProperties().ExternalURI = getThirdColumn(row)

		case kogitoServiceKafkaInstanceKey:
			kafkaAware.GetKafkaProperties().Instance = getThirdColumn(row)
		}
	} else {
		return fmt.Errorf("Kogito service %s doesn't support Kafka configuration", kogitoService.KogitoService.GetName())
	}
	return nil
}

func parseKogitoServiceConfigRow(row *messages.PickleStepArgument_PickleTable_PickleTableRow, kogitoService *framework.KogitoServiceHolder) error {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoServiceHTTPPortKey:
		httpPort, err := strconv.ParseInt(getThirdColumn(row), 10, 32)
		if err != nil {
			return err
		}

		kogitoService.KogitoService.GetSpec().SetHTTPPort(int32(httpPort))
	}

	return nil
}

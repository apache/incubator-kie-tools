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

package mappers

import (
	"fmt"
	"strconv"

	"github.com/cucumber/godog"
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/types"
	bddtypes "github.com/kiegroup/kogito-cloud-operator/test/types"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	kogitoServiceConfigKey          = "config"
	kogitoServiceRuntimeRequestKey  = "runtime-request"
	kogitoServiceRuntimeLimitKey    = "runtime-limit"
	kogitoServiceRuntimeEnvKey      = "runtime-env"
	kogitoServiceServiceLabelKey    = "service-label"
	kogitoServiceDeploymentLabelKey = "deployment-label"
	kogitoServiceMonitoringKey      = "monitoring"

	// DataTable second column
	kogitoServiceHTTPPortKey         = "httpPort"
	kogitoServiceMonitoringScrapeKey = "scrape"
	kogitoServiceInfraKey            = "infra"
)

// MapKogitoServiceTable maps Cucumber table to KogitoServiceHolder
func MapKogitoServiceTable(table *godog.Table, serviceHolder *types.KogitoServiceHolder) error {
	for _, row := range table.Rows {
		// Try to map configuration row to KogitoServiceHolder
		_, err := mapKogitoServiceTableRow(row, serviceHolder)
		if err != nil {
			return err
		}

	}
	return nil
}

// mapKogitoServiceTableRow maps Cucumber table row to KogitoServiceHolder
func mapKogitoServiceTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := getFirstColumn(row)

	switch firstColumn {
	case kogitoServiceServiceLabelKey:
		kogitoService.KogitoService.GetSpec().AddServiceLabel(getSecondColumn(row), getThirdColumn(row))

	case kogitoServiceDeploymentLabelKey:
		kogitoService.KogitoService.GetSpec().AddDeploymentLabel(getSecondColumn(row), getThirdColumn(row))

	case kogitoServiceRuntimeEnvKey:
		kogitoService.KogitoService.GetSpec().AddEnvironmentVariable(getSecondColumn(row), getThirdColumn(row))

	case kogitoServiceRuntimeRequestKey:
		kogitoService.KogitoService.GetSpec().AddResourceRequest(getSecondColumn(row), getThirdColumn(row))

	case kogitoServiceRuntimeLimitKey:
		kogitoService.KogitoService.GetSpec().AddResourceLimit(getSecondColumn(row), getThirdColumn(row))

	case kogitoServiceConfigKey:
		return mapKogitoServiceConfigTableRow(row, kogitoService)

	case kogitoServiceMonitoringKey:
		return mapKogitoServiceMonitoringTableRow(row, kogitoService)

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}

	return true, nil
}

func mapKogitoServiceConfigTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoServiceHTTPPortKey:
		httpPort, err := strconv.ParseInt(getThirdColumn(row), 10, 32)
		if err != nil {
			return false, err
		}

		kogitoService.KogitoService.GetSpec().SetHTTPPort(int32(httpPort))

	case kogitoServiceInfraKey:
		kogitoService.KogitoService.GetSpec().AddInfra(getThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return true, nil
}

func mapKogitoServiceMonitoringTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	secondColumn := getSecondColumn(row)

	if kogitoRuntime, ok := kogitoService.KogitoService.(*v1alpha1.KogitoRuntime); ok {
		switch secondColumn {
		case kogitoServiceMonitoringScrapeKey:
			kogitoRuntime.Spec.Monitoring.Scrape = MustParseEnabledDisabled(getThirdColumn(row))

		default:
			return false, fmt.Errorf("Unrecognized monitoring configuration option: %s", secondColumn)
		}
	} else {
		return false, fmt.Errorf("Kogito service %s doesn't support monitoring configuration", kogitoService.KogitoService.GetName())
	}

	return true, nil
}

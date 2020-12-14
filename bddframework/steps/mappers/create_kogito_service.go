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

	"github.com/cucumber/godog"
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

	// DataTable second column
	kogitoServiceInfraKey      = "infra"
	kogitoServiceDabaseTypeKey = "database-type"
	kogitoServiceNameTypeKey   = "name"
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

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}

	return true, nil
}

func mapKogitoServiceConfigTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoServiceInfraKey:
		kogitoService.KogitoService.GetSpec().AddInfra(getThirdColumn(row))

	case kogitoServiceDabaseTypeKey:
		kogitoService.DatabaseType = getThirdColumn(row)

	case kogitoServiceNameTypeKey:
		kogitoService.KogitoService.SetName(getThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return true, nil
}

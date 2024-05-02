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

package mappers

import (
	"fmt"

	"github.com/cucumber/messages-go/v16"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
	bddtypes "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/bddframework/pkg/types"
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
func MapKogitoServiceTable(table *messages.PickleTable, serviceHolder *types.KogitoServiceHolder) error {
	for _, row := range table.Rows {
		// Try to map configuration row to KogitoServiceHolder
		_, err := MapKogitoServiceTableRow(row, serviceHolder)
		if err != nil {
			return err
		}

	}
	return nil
}

// MapKogitoServiceTableRow maps Cucumber table row to KogitoServiceHolder
func MapKogitoServiceTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := GetFirstColumn(row)

	switch firstColumn {
	case kogitoServiceServiceLabelKey:
		kogitoService.KogitoService.GetSpec().AddServiceLabel(GetSecondColumn(row), GetThirdColumn(row))

	case kogitoServiceDeploymentLabelKey:
		kogitoService.KogitoService.GetSpec().AddDeploymentLabel(GetSecondColumn(row), GetThirdColumn(row))

	case kogitoServiceRuntimeEnvKey:
		kogitoService.KogitoService.GetSpec().AddEnvironmentVariable(GetSecondColumn(row), GetThirdColumn(row))

	case kogitoServiceRuntimeRequestKey:
		kogitoService.KogitoService.GetSpec().AddResourceRequest(GetSecondColumn(row), GetThirdColumn(row))

	case kogitoServiceRuntimeLimitKey:
		kogitoService.KogitoService.GetSpec().AddResourceLimit(GetSecondColumn(row), GetThirdColumn(row))

	case kogitoServiceConfigKey:
		return mapKogitoServiceConfigTableRow(row, kogitoService)

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}

	return true, nil
}

func mapKogitoServiceConfigTableRow(row *TableRow, kogitoService *bddtypes.KogitoServiceHolder) (mappingFound bool, err error) {
	secondColumn := GetSecondColumn(row)

	switch secondColumn {
	case kogitoServiceInfraKey:
		kogitoService.KogitoService.GetSpec().AddInfra(GetThirdColumn(row))

	case kogitoServiceDabaseTypeKey:
		kogitoService.DatabaseType = GetThirdColumn(row)

	case kogitoServiceNameTypeKey:
		kogitoService.KogitoService.SetName(GetThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return true, nil
}

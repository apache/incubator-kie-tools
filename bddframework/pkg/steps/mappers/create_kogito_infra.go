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
	"github.com/kiegroup/kogito-operator/apis"
	"github.com/kiegroup/kogito-operator/test/pkg/framework"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	kogitoInfraConfigKey = "config"
)

// MapKogitoInfraTable maps Cucumber table to KogitoInfra information
func MapKogitoInfraTable(table *godog.Table, kogitoInfra api.KogitoInfraInterface) error {
	for _, row := range table.Rows {
		// Try to map configuration row to KogitoServiceHolder
		_, err := mapKogitoInfraTableRow(row, kogitoInfra)
		if err != nil {
			return err
		}

	}
	return nil
}

// mapKogitoInfraTableRow maps Cucumber table row to KogitoInfra
func mapKogitoInfraTableRow(row *TableRow, kogitoInfra api.KogitoInfraInterface) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := GetFirstColumn(row)

	switch firstColumn {
	case kogitoInfraConfigKey:
		framework.GetLogger(kogitoInfra.GetNamespace()).Debug("Got config", "config", GetSecondColumn(row))
		appendConfig(kogitoInfra, GetSecondColumn(row), GetThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}

	return true, nil
}

func appendConfig(kogitoInfra api.KogitoInfraInterface, key, value string) {
	infraProps := make(map[string]string)
	infraProps[key] = value
	kogitoInfra.GetSpec().AddInfraProperties(infraProps)
}

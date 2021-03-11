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
	"github.com/kiegroup/kogito-cloud-operator/api/v1beta1"
	"github.com/kiegroup/kogito-cloud-operator/test/framework"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	kogitoInfraConfigKey = "config"
)

// MapKogitoInfraTable maps Cucumber table to KogitoInfra information
func MapKogitoInfraTable(table *godog.Table, kogitoInfra *v1beta1.KogitoInfra) error {
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
func mapKogitoInfraTableRow(row *TableRow, kogitoInfra *v1beta1.KogitoInfra) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := getFirstColumn(row)

	switch firstColumn {
	case kogitoInfraConfigKey:
		framework.GetLogger(kogitoInfra.Namespace).Debug("Got config", "config", getSecondColumn(row))
		appendConfig(kogitoInfra, getSecondColumn(row), getThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}

	return true, nil
}

func appendConfig(kogitoInfra *v1beta1.KogitoInfra, key, value string) {
	if len(kogitoInfra.Spec.InfraProperties) <= 0 {
		kogitoInfra.Spec.InfraProperties = make(map[string]string)
	}
	kogitoInfra.Spec.InfraProperties[key] = value
}

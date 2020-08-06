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
	"github.com/kiegroup/kogito-cloud-operator/pkg/apis/app/v1alpha1"
	"github.com/kiegroup/kogito-cloud-operator/test/types"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	kogitoBuildConfigKey       = "config"
	kogitoBuildWebhookKey      = "webhook"
	kogitoBuildBuildRequestKey = "build-request"
	kogitoBuildBuildLimitKey   = "build-limit"

	// DataTable second column
	kogitoBuildNativeKey = "native"
	kogitoBuildTypeKey   = "type"
	kogitoBuildSecretKey = "secret"
)

// MapKogitoBuildTable maps Cucumber table to KogitoBuildHolder
func MapKogitoBuildTable(table *godog.Table, buildHolder *types.KogitoBuildHolder) error {
	for _, row := range table.Rows {
		// Try to map configuration row to KogitoServiceHolder
		mappingFound, serviceMapErr := mapKogitoServiceTableRow(row, buildHolder.KogitoServiceHolder)
		if !mappingFound {
			// Try to map configuration row to KogitoBuild
			mappingFound, buildMapErr := mapKogitoBuildTableRow(row, buildHolder.KogitoBuild)
			if !mappingFound {
				return fmt.Errorf("Row mapping not found, Kogito service mapping error: %v , Kogito build mapping error: %v", serviceMapErr, buildMapErr)
			}
		}

	}
	return nil
}

// mapKogitoBuildTableRow maps Cucumber table row to KogitoBuild
func mapKogitoBuildTableRow(row *TableRow, kogitoBuild *v1alpha1.KogitoBuild) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := getFirstColumn(row)

	switch firstColumn {
	case kogitoBuildConfigKey:
		return mapKogitoBuildConfigTableRow(row, kogitoBuild)

	case kogitoBuildWebhookKey:
		return mapKogitoBuildWebhookTableRow(row, kogitoBuild)

	case kogitoBuildBuildRequestKey:
		kogitoBuild.Spec.AddResourceRequest(getSecondColumn(row), getThirdColumn(row))
		return true, nil

	case kogitoBuildBuildLimitKey:
		kogitoBuild.Spec.AddResourceLimit(getSecondColumn(row), getThirdColumn(row))
		return true, nil

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}
}

func mapKogitoBuildConfigTableRow(row *TableRow, kogitoBuild *v1alpha1.KogitoBuild) (mappingFound bool, err error) {
	secondColumn := getSecondColumn(row)

	switch secondColumn {
	case kogitoBuildNativeKey:
		kogitoBuild.Spec.Native = MustParseEnabledDisabled(getThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return true, nil
}

func mapKogitoBuildWebhookTableRow(row *TableRow, kogitoBuild *v1alpha1.KogitoBuild) (mappingFound bool, err error) {
	secondColumn := getSecondColumn(row)

	if len(kogitoBuild.Spec.WebHooks) == 0 {
		kogitoBuild.Spec.WebHooks = []v1alpha1.WebhookSecret{{}}
	}

	switch secondColumn {
	case kogitoBuildTypeKey:
		kogitoBuild.Spec.WebHooks[0].Type = v1alpha1.WebhookType(getThirdColumn(row))
	case kogitoBuildSecretKey:
		kogitoBuild.Spec.WebHooks[0].Secret = getThirdColumn(row)

	default:
		return false, fmt.Errorf("Unrecognized webhook configuration option: %s", secondColumn)
	}

	return true, nil
}

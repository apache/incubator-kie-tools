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
	"errors"
	"fmt"

	"github.com/cucumber/messages-go/v16"

	"github.com/kiegroup/kogito-operator/apis/app/v1beta1"
	v1 "github.com/kiegroup/kogito-operator/apis/rhpam/v1"

	api "github.com/kiegroup/kogito-operator/apis"

	"github.com/kiegroup/kogito-operator/test/pkg/types"
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
func MapKogitoBuildTable(table *messages.PickleTable, buildHolder *types.KogitoBuildHolder) error {
	for _, row := range table.Rows {
		// Try to map configuration row to KogitoServiceHolder
		mappingFound, serviceMapErr := MapKogitoServiceTableRow(row, buildHolder.KogitoServiceHolder)
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
func mapKogitoBuildTableRow(row *TableRow, kogitoBuild api.KogitoBuildInterface) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := GetFirstColumn(row)

	switch firstColumn {
	case kogitoBuildConfigKey:
		return mapKogitoBuildConfigTableRow(row, kogitoBuild)

	case kogitoBuildWebhookKey:
		var spec, ok = kogitoBuild.GetSpec().(*v1beta1.KogitoBuildSpec)
		if ok {
			return mapKogitoBuildWebhookTableRow(row, spec)
		}
		var rhpamSpec, rhpamok = kogitoBuild.GetSpec().(*v1.KogitoBuildSpec)
		if rhpamok {
			return mapKogitoBuildWebhookTableRowRhpam(row, rhpamSpec)
		}
		return false, errors.New("unrecognized Kogito Build spec type")

	case kogitoBuildBuildRequestKey:
		kogitoBuild.GetSpec().AddResourceRequest(GetSecondColumn(row), GetThirdColumn(row))
		return true, nil

	case kogitoBuildBuildLimitKey:
		kogitoBuild.GetSpec().AddResourceLimit(GetSecondColumn(row), GetThirdColumn(row))
		return true, nil

	default:
		return false, fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
	}
}

func mapKogitoBuildConfigTableRow(row *TableRow, kogitoBuild api.KogitoBuildInterface) (mappingFound bool, err error) {
	secondColumn := GetSecondColumn(row)

	switch secondColumn {
	case kogitoBuildNativeKey:
		kogitoBuild.GetSpec().SetNative(MustParseEnabledDisabled(GetThirdColumn(row)))

	default:
		return false, fmt.Errorf("Unrecognized config configuration option: %s", secondColumn)
	}

	return true, nil
}

func mapKogitoBuildWebhookTableRow(row *TableRow, spec *v1beta1.KogitoBuildSpec) (mappingFound bool, err error) {
	secondColumn := GetSecondColumn(row)

	if len(spec.WebHooks) == 0 {
		spec.WebHooks = []v1beta1.WebHookSecret{{}}
	}

	switch secondColumn {
	case kogitoBuildTypeKey:
		spec.WebHooks[0].Type = api.WebHookType(GetThirdColumn(row))
	case kogitoBuildSecretKey:
		spec.WebHooks[0].Secret = GetThirdColumn(row)

	default:
		return false, fmt.Errorf("Unrecognized webhook configuration option: %s", secondColumn)
	}

	return true, nil
}

func mapKogitoBuildWebhookTableRowRhpam(row *TableRow, spec *v1.KogitoBuildSpec) (mappingFound bool, err error) {
	secondColumn := GetSecondColumn(row)

	if len(spec.WebHooks) == 0 {
		spec.WebHooks = []v1.WebHookSecret{{}}
	}

	switch secondColumn {
	case kogitoBuildTypeKey:
		spec.WebHooks[0].Type = api.WebHookType(GetThirdColumn(row))
	case kogitoBuildSecretKey:
		spec.WebHooks[0].Secret = GetThirdColumn(row)

	default:
		return false, fmt.Errorf("Unrecognized webhook configuration option: %s", secondColumn)
	}

	return true, nil
}

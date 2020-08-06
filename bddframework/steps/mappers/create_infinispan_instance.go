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
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	infinispanUsernameKey = "username"
	infinispanPasswordKey = "password"
)

// MapInfinispanCredentialsFromTable maps Cucumber table to Infinispan credentials
func MapInfinispanCredentialsFromTable(table *godog.Table) (username, password string, err error) {
	if len(table.Rows) == 0 { // Using default configuration
		return
	}

	if len(table.Rows[0].Cells) != 2 {
		return "", "", fmt.Errorf("expected table to have exactly two columns")
	}

	for _, row := range table.Rows {
		firstColumn := getFirstColumn(row)
		switch firstColumn {
		case infinispanUsernameKey:
			username = getSecondColumn(row)
		case infinispanPasswordKey:
			password = getSecondColumn(row)

		default:
			return "", "", fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}
	return
}

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

	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	buildRequestKey   = "build-request"
	buildLimitKey     = "build-limit"
	runtimeRequestKey = "runtime-request"
	runtimeLimitKey   = "runtime-limit"
)

// MapBuildResourceRequirementsTable maps Cucumber table of build resource requirements
func MapBuildResourceRequirementsTable(table *messages.PickleTable, build *v1.ResourceRequirements) error {
	for _, row := range table.Rows {
		mappingFound, err := mapBuildResourceRequirementsTableRow(row, build)
		if !mappingFound {
			return fmt.Errorf("Row mapping not found, Build resource mapping error: %v", err)
		}

	}
	return nil
}

// mapBuildResourceRequirementsTableRow maps Cucumber table row of build resource requirements
func mapBuildResourceRequirementsTableRow(row *TableRow, build *v1.ResourceRequirements) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := GetFirstColumn(row)
	switch firstColumn {
	case buildRequestKey:
		build.Requests[v1.ResourceName(GetSecondColumn(row))] = resource.MustParse(GetThirdColumn(row))

	case buildLimitKey:
		build.Limits[v1.ResourceName(GetSecondColumn(row))] = resource.MustParse(GetThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized build configuration option: %s", firstColumn)
	}

	return true, nil
}

// MapRuntimeResourceRequirementsTable maps Cucumber table of runtime resource requirements
func MapRuntimeResourceRequirementsTable(table *messages.PickleTable, runtime *v1.ResourceRequirements) error {
	for _, row := range table.Rows {
		mappingFound, err := mapRuntimeResourceRequirementsTableRow(row, runtime)
		if !mappingFound {
			return fmt.Errorf("Row mapping not found, Runtime resource mapping error: %v", err)
		}

	}
	return nil
}

// mapRuntimeResourceRequirementsTableRow maps Cucumber table row of runtime resource requirements
func mapRuntimeResourceRequirementsTableRow(row *TableRow, runtime *v1.ResourceRequirements) (mappingFound bool, err error) {
	if len(row.Cells) != 3 {
		return false, fmt.Errorf("expected table to have exactly three columns")
	}

	firstColumn := GetFirstColumn(row)
	switch firstColumn {
	case runtimeRequestKey:
		runtime.Requests[v1.ResourceName(GetSecondColumn(row))] = resource.MustParse(GetThirdColumn(row))

	case runtimeLimitKey:
		runtime.Limits[v1.ResourceName(GetSecondColumn(row))] = resource.MustParse(GetThirdColumn(row))

	default:
		return false, fmt.Errorf("Unrecognized runtime configuration option: %s", firstColumn)
	}

	return true, nil
}

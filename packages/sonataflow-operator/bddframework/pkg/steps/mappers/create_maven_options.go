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
	"strings"

	"github.com/cucumber/messages-go/v16"
)

const (
	// Maven config first column
	mavenProfileKey = "profile"
	mavenOptionKey  = "option"
	mavenNativeKey  = "native"
)

// MavenCommandConfig contains configuration for Maven Command execution
type MavenCommandConfig struct {
	Profiles []string
	Options  []string
	Native   bool
}

// MapMavenCommandConfigTable maps Cucumber table with Maven options to a slice
func MapMavenCommandConfigTable(table *messages.PickleTable, config *MavenCommandConfig) error {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 2 {
		return fmt.Errorf("expected table to have exactly two columns")
	}

	for _, row := range table.Rows {
		firstColumn := GetFirstColumn(row)
		switch firstColumn {
		case mavenProfileKey:
			config.Profiles = append(config.Profiles, strings.Split(GetSecondColumn(row), ",")...)
		case mavenOptionKey:
			config.Options = append(config.Options, GetSecondColumn(row))
		case mavenNativeKey:
			config.Native = MustParseEnabledDisabled(GetSecondColumn(row))
		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}
	return nil
}

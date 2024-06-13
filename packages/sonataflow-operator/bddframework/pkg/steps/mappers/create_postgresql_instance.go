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
)

// *** Whenever you add new parsing functionality here please add corresponding DataTable example to every file in steps which can use the functionality ***

const (
	// DataTable first column
	posgresqlUsernameKey = "username"
	posgresqlPasswordKey = "password"
	posgresqlDatabaseKey = "database"
)

// PostgresqlCredentialsConfig contains credentials information for PostgreSQL, taken from configuration table
type PostgresqlCredentialsConfig struct {
	Username string
	Password string
	Database string
}

// MapPostgresqlCredentialsFromTable maps Cucumber table to PostgreSQL credentials
func MapPostgresqlCredentialsFromTable(table *messages.PickleTable, creds *PostgresqlCredentialsConfig) error {
	if len(table.Rows) == 0 { // Using default configuration
		return nil
	}

	if len(table.Rows[0].Cells) != 2 {
		return fmt.Errorf("expected table to have exactly two columns")
	}

	for _, row := range table.Rows {
		firstColumn := GetFirstColumn(row)
		switch firstColumn {
		case posgresqlUsernameKey:
			creds.Username = GetSecondColumn(row)
		case posgresqlPasswordKey:
			creds.Password = GetSecondColumn(row)
		case posgresqlDatabaseKey:
			creds.Database = GetSecondColumn(row)

		default:
			return fmt.Errorf("Unrecognized configuration option: %s", firstColumn)
		}
	}
	return nil
}

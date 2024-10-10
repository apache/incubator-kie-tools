// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package persistence

import (
	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

const (
	primaryPostgreSQLJdbc = "jdbc:postgresql://host:port/database?currentSchema=primary-database"

	platformPostgreSQLJdbc = "jdbc:postgresql://host:port/database?currentSchema=platform-database"
	schemaName             = "my-schema"
)

var (
	primaryPostgreSQLSecret = operatorapi.PostgreSQLSecretOptions{
		Name: "primary-secret",
	}
	primaryPostreSQLService = operatorapi.PostgreSQLServiceOptions{
		SQLServiceOptions: &operatorapi.SQLServiceOptions{Name: "primary-service"},
		DatabaseSchema:    "primary-schema",
	}
	plaformPostgreSQLSecret = operatorapi.PostgreSQLSecretOptions{
		Name: "platform-secret",
	}
	platformPostreSQLService = operatorapi.SQLServiceOptions{
		Name: "platform-service",
	}
)

var _ = Describe("RetrievePostgreSQLConfiguration", func() {
	DescribeTable("calculation",
		func(primary *operatorapi.PersistenceOptionsSpec,
			platformPersistence *operatorapi.PlatformPersistenceOptionsSpec,
			schema string,
			expectedConfig *operatorapi.PersistenceOptionsSpec) {
			result := RetrievePostgreSQLConfiguration(primary, platformPersistence, schema)
			Expect(expectedConfig).To(Equal(result))
		},
		Entry("primary is postgresql with JdbcUrl", buildPrimaryIsPostgreSQLWithJdbcUrl(),
			buildPlatformIsPostgreSQLWithJdbcUrl(),
			schemaName,
			buildPrimaryIsPostgreSQLWithJdbcUrl()),
		Entry("primary is postgresql ServiceRef", buildPrimaryIsPostgreSQLWithServiceRef(),
			buildPlatformIsPostgreSQLWithJdbcUrl(),
			schemaName,
			buildPrimaryIsPostgreSQLWithServiceRef()),
		Entry("primary is nil, platform with JdbcUrl",
			nil,
			buildPlatformIsPostgreSQLWithJdbcUrl(),
			schemaName,
			&operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{
					SecretRef: plaformPostgreSQLSecret,
					JdbcUrl:   platformPostgreSQLJdbc,
				},
			}),
		Entry("primary is empty, platform with JdbcUrl",
			&operatorapi.PersistenceOptionsSpec{},
			buildPlatformIsPostgreSQLWithJdbcUrl(),
			schemaName,
			&operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{
					SecretRef: plaformPostgreSQLSecret,
					JdbcUrl:   platformPostgreSQLJdbc,
				},
			}),
		Entry("primary is nil, platform with ServiceRef",
			nil,
			buildPlatformIsPostgreSQLWithServiceRef(),
			schemaName,
			&operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{
					ServiceRef: &operatorapi.PostgreSQLServiceOptions{
						SQLServiceOptions: &platformPostreSQLService,
						DatabaseSchema:    schemaName,
					},
					SecretRef: plaformPostgreSQLSecret,
				},
			}),
		Entry("primary is empty, platform with ServiceRef",
			&operatorapi.PersistenceOptionsSpec{},
			buildPlatformIsPostgreSQLWithServiceRef(),
			schemaName,
			&operatorapi.PersistenceOptionsSpec{
				PostgreSQL: &operatorapi.PersistencePostgreSQL{
					ServiceRef: &operatorapi.PostgreSQLServiceOptions{
						SQLServiceOptions: &platformPostreSQLService,
						DatabaseSchema:    schemaName,
					},
					SecretRef: plaformPostgreSQLSecret,
				},
			}),
	)
})

func buildPrimaryIsPostgreSQLWithJdbcUrl() *operatorapi.PersistenceOptionsSpec {
	return &operatorapi.PersistenceOptionsSpec{
		PostgreSQL: &operatorapi.PersistencePostgreSQL{
			JdbcUrl:   primaryPostgreSQLJdbc,
			SecretRef: primaryPostgreSQLSecret,
		},
	}
}

func buildPrimaryIsPostgreSQLWithServiceRef() *operatorapi.PersistenceOptionsSpec {
	return &operatorapi.PersistenceOptionsSpec{
		PostgreSQL: &operatorapi.PersistencePostgreSQL{
			ServiceRef: &primaryPostreSQLService,
			SecretRef:  primaryPostgreSQLSecret,
		},
	}
}

func buildPlatformIsPostgreSQLWithJdbcUrl() *operatorapi.PlatformPersistenceOptionsSpec {
	return &operatorapi.PlatformPersistenceOptionsSpec{
		PostgreSQL: &operatorapi.PlatformPersistencePostgreSQL{
			JdbcUrl:   platformPostgreSQLJdbc,
			SecretRef: plaformPostgreSQLSecret,
		},
	}
}

func buildPlatformIsPostgreSQLWithServiceRef() *operatorapi.PlatformPersistenceOptionsSpec {
	return &operatorapi.PlatformPersistenceOptionsSpec{
		PostgreSQL: &operatorapi.PlatformPersistencePostgreSQL{
			ServiceRef: &platformPostreSQLService,
			SecretRef:  plaformPostgreSQLSecret,
		},
	}
}

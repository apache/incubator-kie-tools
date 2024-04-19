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

package services

import (
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
)

const (
	defaultSchema = "schema"
)

var _ = Describe("Platform properties", func() {

	var _ = Context("PostgreSQL properties", func() {
		var _ = DescribeTable("Generate a reactive URL", func(spec *operatorapi.PersistencePostgreSQL, expectedReactiveURL string, expectedError bool) {
			res, err := generateReactiveURL(spec, defaultSchema, "default", constants.DefaultDatabaseName, constants.DefaultPostgreSQLPort)
			if expectedError {
				Expect(err).NotTo(BeNil())
			} else {
				Expect(res).To(BeIdenticalTo(expectedReactiveURL))
			}
		},
			Entry("With an invalid URL", generatePostgreSQLOptions(setJDBC("jdbc:\\postgress://url to fail/fail?here&and&here")), "", true),
			Entry("Empty JDBC string in spec", generatePostgreSQLOptions(setServiceName("svcName")), "postgresql://svcName.default:5432/sonataflow?search_path=schema", false),
			Entry("JDBC in spec with duplicated jdbc prefix and no currentSchema in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:jdbc:postgres://host.com:5432/path?k=v#f")), "postgres://host.com:5432/path", false),
			Entry("JDBC in spec with username and password and no currentSchema in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgres://user:pass@host.com:5432/dbName?k=v#f")), "postgres://user:pass@host.com:5432/dbName", false),
			Entry("JDBC in spec without currentSchema in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:5432/sonataflow")), "postgresql://postgres:5432/sonataflow", false),
			Entry("JDBC in spec with duplicated currentSchema in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema&currentSchema=myschema2")), "postgresql://postgres:5432/sonataflow?search_path=myschema", false),
			Entry("JDBC in spec with currentSchema first and search_path later in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema&search_path=myschema2")), "postgresql://postgres:5432/sonataflow?search_path=myschema2", false),
			Entry("JDBC in spec with search_path first and currentSchema later in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema&search_path=myschema2")), "postgresql://postgres:5432/sonataflow?search_path=myschema2", false),
			Entry("JDBC in spec with empty value in currentSchema parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:342/sonataflow?currentSchema")), "postgresql://postgres:342/sonataflow", false),
			Entry("JDBC in spec with currentSchema in URL parameter",
				generatePostgreSQLOptions(setJDBC("jdbc:postgresql://postgres:5432/sonataflow?currentSchema=myschema")), "postgresql://postgres:5432/sonataflow?search_path=myschema", false),
			Entry("With only database service namespace defined",
				generatePostgreSQLOptions(setServiceName("svc"), setServiceNamespace("test")), "postgresql://svc.test:5432/sonataflow?search_path=schema", false),
			Entry("With only database schema defined",
				generatePostgreSQLOptions(setServiceName("svc"), setDatabaseSchemaName("myschema")), "postgresql://svc.default:5432/sonataflow?search_path=myschema", false),
			Entry("With only database port defined",
				generatePostgreSQLOptions(setServiceName("svc"), setDBPort(3432)), "postgresql://svc.default:3432/sonataflow?search_path=schema", false),
			Entry("With only database name defined",
				generatePostgreSQLOptions(setServiceName("svc"), setDatabaseName("foo")), "postgresql://svc.default:5432/foo?search_path=schema", false),
		)
	})
})

type optionFn func(*operatorapi.PersistencePostgreSQL)

func generatePostgreSQLOptions(options ...optionFn) *operatorapi.PersistencePostgreSQL {
	p := &operatorapi.PersistencePostgreSQL{}
	for _, f := range options {
		f(p)
	}
	return p
}

func setJDBC(url string) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		o.JdbcUrl = url
	}
}

func setServiceName(svcName string) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		if o.ServiceRef == nil {
			o.ServiceRef = &operatorapi.PostgreSQLServiceOptions{}
		}
		if o.ServiceRef.SQLServiceOptions == nil {
			o.ServiceRef.SQLServiceOptions = &operatorapi.SQLServiceOptions{}
		}
		o.ServiceRef.Name = svcName
	}
}

func setDatabaseSchemaName(dbSchemaName string) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		if o.ServiceRef == nil {
			o.ServiceRef = &operatorapi.PostgreSQLServiceOptions{}
		}
		o.ServiceRef.DatabaseSchema = dbSchemaName
	}
}

func setDatabaseName(dbName string) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		if o.ServiceRef == nil {
			o.ServiceRef = &operatorapi.PostgreSQLServiceOptions{}
		}
		if o.ServiceRef.SQLServiceOptions == nil {
			o.ServiceRef.SQLServiceOptions = &operatorapi.SQLServiceOptions{}
		}
		o.ServiceRef.DatabaseName = dbName
	}
}

func setServiceNamespace(svcNamespace string) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		if o.ServiceRef == nil {
			o.ServiceRef = &operatorapi.PostgreSQLServiceOptions{}
		}
		if o.ServiceRef.SQLServiceOptions == nil {
			o.ServiceRef.SQLServiceOptions = &operatorapi.SQLServiceOptions{}
		}
		o.ServiceRef.Namespace = svcNamespace
	}
}

func setDBPort(portNumber int) optionFn {
	return func(o *operatorapi.PersistencePostgreSQL) {
		if o.ServiceRef == nil {
			o.ServiceRef = &operatorapi.PostgreSQLServiceOptions{}
		}
		if o.ServiceRef.SQLServiceOptions == nil {
			o.ServiceRef.SQLServiceOptions = &operatorapi.SQLServiceOptions{}
		}
		o.ServiceRef.Port = &portNumber
	}
}

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

package v1alpha08

type DBMigrationStrategyType string

const (
	DBMigrationStrategyService DBMigrationStrategyType = "service"
	DBMigrationStrategyJob     DBMigrationStrategyType = "job"
	DBMigrationStrategyNone    DBMigrationStrategyType = "none"
)

// PlatformPersistenceOptionsSpec configures the DataBase in the platform spec. This specification can
// be used by workflows and platform services when they don't provide one of their own.
// +optional
// +kubebuilder:validation:MaxProperties=1
type PlatformPersistenceOptionsSpec struct {
	// Connect configured services to a postgresql database.
	// +optional
	PostgreSQL *PlatformPersistencePostgreSQL `json:"postgresql,omitempty"`
}

// PlatformPersistencePostgreSQL configure postgresql connection in a platform to be shared
// by platform services and workflows when required.
// +kubebuilder:validation:MinProperties=2
// +kubebuilder:validation:MaxProperties=2
type PlatformPersistencePostgreSQL struct {
	// Secret reference to the database user credentials
	SecretRef PostgreSQLSecretOptions `json:"secretRef"`
	// Service reference to postgresql datasource. Mutually exclusive to jdbcUrl.
	// +optional
	ServiceRef *SQLServiceOptions `json:"serviceRef,omitempty"`
	// PostgreSql JDBC URL. Mutually exclusive to serviceRef.
	// e.g. "jdbc:postgresql://host:port/database?currentSchema=data-index-service"
	// +optional
	JdbcUrl string `json:"jdbcUrl,omitempty"`
}

// PersistenceOptionsSpec configures the DataBase support for both platform services and workflows. For services, it allows
// configuring a generic database connectivity if the service does not come with its own configured. In case of workflows,
// the operator will add the necessary JDBC properties to in the workflow's application.properties so that it can communicate
// with the persistence service based on the spec provided here.
// +optional
// +kubebuilder:validation:MaxProperties=2
type PersistenceOptionsSpec struct {
	// Connect configured services to a postgresql database.
	// +optional
	PostgreSQL *PersistencePostgreSQL `json:"postgresql,omitempty"`

	// DB Migration approach for data-index and jobs-service. Use the following values as described.
	// job: use job based approach provided by the SonataFlow operator.
	// service: service itself shall migrate the db and will not use SonataFlow operator.
	// none: no database migration functionality needed.
	// +optional
	// +kubebuilder:default:=service
	DBMigrationStrategy string `json:"dbMigrationStrategy,omitempty"`
}

// PersistencePostgreSQL configure postgresql connection for service(s).
// +kubebuilder:validation:MinProperties=2
// +kubebuilder:validation:MaxProperties=2
type PersistencePostgreSQL struct {
	// Secret reference to the database user credentials
	SecretRef PostgreSQLSecretOptions `json:"secretRef"`
	// Service reference to postgresql datasource. Mutually exclusive to jdbcUrl.
	// +optional
	ServiceRef *PostgreSQLServiceOptions `json:"serviceRef,omitempty"`
	// PostgreSql JDBC URL. Mutually exclusive to serviceRef.
	// e.g. "jdbc:postgresql://host:port/database?currentSchema=data-index-service"
	// +optional
	JdbcUrl string `json:"jdbcUrl,omitempty"`
}

// PostgreSQLSecretOptions use credential secret for postgresql connection.
type PostgreSQLSecretOptions struct {
	// Name of the postgresql credentials secret.
	Name string `json:"name"`
	// Defaults to POSTGRESQL_USER
	// +optional
	UserKey string `json:"userKey,omitempty"`
	// Defaults to POSTGRESQL_PASSWORD
	// +optional
	PasswordKey string `json:"passwordKey,omitempty"`
}

type SQLServiceOptions struct {
	// Name of the postgresql k8s service.
	Name string `json:"name"`
	// Namespace of the postgresql k8s service. Defaults to the SonataFlowPlatform's local namespace.
	// +optional
	Namespace string `json:"namespace,omitempty"`
	// Port to use when connecting to the postgresql k8s service. Defaults to 5432.
	// +optional
	Port *int `json:"port,omitempty"`
	// Name of postgresql database to be used. Defaults to "sonataflow"
	// +optional
	DatabaseName string `json:"databaseName,omitempty"`
}

// PostgreSQLServiceOptions use k8s service to configure postgresql jdbc url.
type PostgreSQLServiceOptions struct {
	*SQLServiceOptions `json:",inline"`
	// Schema of postgresql database to be used. Defaults to "data-index-service"
	// +optional
	DatabaseSchema string `json:"databaseSchema,omitempty"`
}

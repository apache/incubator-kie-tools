// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package v1alpha08

// ServicesPlatformSpec describes the desired service configuration for "prod" workflows.
type ServicesPlatformSpec struct {
	// Deploys the Data Index service for use by "prod" profile workflows.
	// +optional
	DataIndex *ServiceSpec `json:"dataIndex,omitempty"`
	// Deploys the Job service for use by "prod" profile workflows.
	// +optional
	JobService *ServiceSpec `json:"jobService,omitempty"`
}

// ServiceSpec defines the desired state of a platform service
// +k8s:openapi-gen=true
type ServiceSpec struct {
	// Determines whether "prod" profile workflows should be configured to use this service
	// +optional
	Enabled *bool `json:"enabled,omitempty"`
	// Persists service to a datasource of choice. Ephemeral by default.
	// +optional
	Persistence *PersistenceOptions `json:"persistence,omitempty"`
	// PodTemplate describes the deployment details of this platform service instance.
	//+operator-sdk:csv:customresourcedefinitions:type=spec,displayName="podTemplate"
	PodTemplate PodTemplateSpec `json:"podTemplate,omitempty"`
}

// PersistenceOptions configure the services to persist to a datasource of choice
// +kubebuilder:validation:MaxProperties=1
type PersistenceOptions struct {
	// Connect configured services to a postgresql database.
	// +optional
	PostgreSql *PersistencePostgreSql `json:"postgresql,omitempty"`
}

// PersistencePostgreSql configure postgresql connection for service(s).
// +kubebuilder:validation:MinProperties=2
// +kubebuilder:validation:MaxProperties=2
type PersistencePostgreSql struct {
	// Secret reference to the database user credentials
	SecretRef PostgreSqlSecretOptions `json:"secretRef"`
	// Service reference to postgresql datasource. Mutually exclusive to jdbcUrl.
	// +optional
	ServiceRef *PostgreSqlServiceOptions `json:"serviceRef,omitempty"`
	// PostgreSql JDBC URL. Mutually exclusive to serviceRef.
	// e.g. "jdbc:postgresql://host:port/database?currentSchema=data-index-service"
	// +optional
	JdbcUrl string `json:"jdbcUrl,omitempty"`
}

// PostgreSqlSecretOptions use credential secret for postgresql connection.
type PostgreSqlSecretOptions struct {
	// Name of the postgresql credentials secret.
	Name string `json:"name"`
	// Defaults to POSTGRESQL_USER
	// +optional
	UserKey string `json:"userKey,omitempty"`
	// Defaults to POSTGRESQL_PASSWORD
	// +optional
	PasswordKey string `json:"passwordKey,omitempty"`
}

// PostgreSqlServiceOptions use k8s service to configure postgresql jdbc url.
type PostgreSqlServiceOptions struct {
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
	// Schema of postgresql database to be used. Defaults to "data-index-service"
	// +optional
	DatabaseSchema string `json:"databaseSchema,omitempty"`
}

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
	"fmt"
	"strings"

	"github.com/magiconair/properties"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles"

	corev1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
)

const (
	defaultDatabaseName = "sonataflow"
)

// ConfigurePostgreSQLEnv returns the common env variables required for the DataIndex or JobsService when postresql persistence is used.
func ConfigurePostgreSQLEnv(postgresql *operatorapi.PersistencePostgreSQL, databaseSchema, databaseNamespace string) []corev1.EnvVar {
	dataSourcePort := constants.DefaultPostgreSQLPort
	databaseName := defaultDatabaseName
	dataSourceURL := postgresql.JdbcUrl
	if postgresql.ServiceRef != nil {
		if len(postgresql.ServiceRef.DatabaseSchema) > 0 {
			databaseSchema = postgresql.ServiceRef.DatabaseSchema
		}
		if len(postgresql.ServiceRef.Namespace) > 0 {
			databaseNamespace = postgresql.ServiceRef.Namespace
		}
		if postgresql.ServiceRef.Port != nil {
			dataSourcePort = *postgresql.ServiceRef.Port
		}
		if len(postgresql.ServiceRef.DatabaseName) > 0 {
			databaseName = postgresql.ServiceRef.DatabaseName
		}
		dataSourceURL = fmt.Sprintf("jdbc:postgresql://%s.%s:%d/%s?currentSchema=%s", postgresql.ServiceRef.Name, databaseNamespace, dataSourcePort, databaseName, databaseSchema)
	}
	secretRef := corev1.LocalObjectReference{
		Name: postgresql.SecretRef.Name,
	}
	quarkusDatasourceUsername := "POSTGRESQL_USER"
	if len(postgresql.SecretRef.UserKey) > 0 {
		quarkusDatasourceUsername = postgresql.SecretRef.UserKey
	}
	quarkusDatasourcePassword := "POSTGRESQL_PASSWORD"
	if len(postgresql.SecretRef.PasswordKey) > 0 {
		quarkusDatasourcePassword = postgresql.SecretRef.PasswordKey
	}
	return []corev1.EnvVar{
		{
			Name: "QUARKUS_DATASOURCE_USERNAME",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					Key:                  quarkusDatasourceUsername,
					LocalObjectReference: secretRef,
				},
			},
		},
		{
			Name: "QUARKUS_DATASOURCE_PASSWORD",
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					Key:                  quarkusDatasourcePassword,
					LocalObjectReference: secretRef,
				},
			},
		},
		{
			Name:  "QUARKUS_DATASOURCE_DB_KIND",
			Value: constants.PersistenceTypePostgreSQL.String(),
		},
		{
			Name:  "QUARKUS_DATASOURCE_JDBC_URL",
			Value: dataSourceURL,
		},
		{
			Name:  "KOGITO_PERSISTENCE_TYPE",
			Value: "jdbc",
		},
	}
}

func ConfigurePersistence(serviceContainer *corev1.Container, config *operatorapi.PersistenceOptionsSpec, defaultSchema, namespace string) *corev1.Container {
	if config.PostgreSQL == nil {
		return serviceContainer
	}
	c := serviceContainer.DeepCopy()
	c.Env = append(c.Env, ConfigurePostgreSQLEnv(config.PostgreSQL, defaultSchema, namespace)...)
	return c
}

func RetrieveConfiguration(primary *v1alpha08.PersistenceOptionsSpec, platformPersistence *v1alpha08.PlatformPersistenceOptionsSpec, schema string) *v1alpha08.PersistenceOptionsSpec {
	if primary != nil {
		return primary
	}
	if platformPersistence == nil {
		return nil
	}
	return buildPersistenceOptionsSpec(platformPersistence, schema)
}

// RetrievePostgreSQLConfiguration return the PersistenceOptionsSpec considering that postgresql is the database manager
// to look for. Gives priority to the primary configuration.
func RetrievePostgreSQLConfiguration(primary *v1alpha08.PersistenceOptionsSpec, platformPersistence *v1alpha08.PlatformPersistenceOptionsSpec, schema string) *v1alpha08.PersistenceOptionsSpec {
	if primary != nil && primary.PostgreSQL != nil {
		return primary
	}
	return buildPersistenceOptionsSpec(platformPersistence, schema)
}

func buildPersistenceOptionsSpec(platformPersistence *v1alpha08.PlatformPersistenceOptionsSpec, schema string) *v1alpha08.PersistenceOptionsSpec {
	c := &v1alpha08.PersistenceOptionsSpec{}
	if platformPersistence.PostgreSQL != nil {
		c.PostgreSQL = &v1alpha08.PersistencePostgreSQL{
			SecretRef: platformPersistence.PostgreSQL.SecretRef,
		}
		if platformPersistence.PostgreSQL.ServiceRef != nil {
			c.PostgreSQL.ServiceRef = &v1alpha08.PostgreSQLServiceOptions{
				SQLServiceOptions: platformPersistence.PostgreSQL.ServiceRef,
				DatabaseSchema:    schema,
			}
		} else {
			c.PostgreSQL.JdbcUrl = platformPersistence.PostgreSQL.JdbcUrl
		}
	}
	return c
}

func UsesPostgreSQLPersistence(workflow *operatorapi.SonataFlow, platform *operatorapi.SonataFlowPlatform) bool {
	return (workflow.Spec.Persistence != nil && workflow.Spec.Persistence.PostgreSQL != nil) ||
		(workflow.Spec.Persistence == nil && platform.Spec.Persistence != nil && platform.Spec.Persistence.PostgreSQL != nil)
}

// GetPostgreSQLExtensions returns the Quarkus extensions required for postgresql persistence.
func GetPostgreSQLExtensions() []cfg.GroupArtifactId {
	return cfg.GetCfg().PostgreSQLPersistenceExtensions
}

// GetPostgreSQLWorkflowProperties returns the set of application properties required for postgresql persistence.
// Never nil.
func GetPostgreSQLWorkflowProperties(workflow *operatorapi.SonataFlow) *properties.Properties {
	props := properties.NewProperties()
	if !profiles.IsDevProfile(workflow) && !profiles.IsGitOpsProfile(workflow) {
		// build-time property required by kogito-runtimes to feed flyway build-time settings and package the necessary .sql files.
		props.Set(QuarkusDatasourceDBKind, PostgreSQLDBKind)
		// build-time properties for kogito-runtimes to use jdbc
		props.Set(KogitoPersistenceType, JDBCPersistenceType)
		props.Set(KogitoPersistenceProtoMarshaller, "false")
	}
	return props
}

// GetDBSchemaName Parses jdbc url and returns the schema name
func GetDBSchemaName(persistencePostgreSQL *operatorapi.PersistencePostgreSQL, defaultSchemaName string) string {
	if persistencePostgreSQL != nil && persistencePostgreSQL.ServiceRef != nil && len(persistencePostgreSQL.ServiceRef.DatabaseSchema) > 0 {
		return persistencePostgreSQL.ServiceRef.DatabaseSchema
	}

	if persistencePostgreSQL != nil && len(persistencePostgreSQL.JdbcUrl) > 0 {
		jdbcURL := persistencePostgreSQL.JdbcUrl
		_, a, found := strings.Cut(jdbcURL, "currentSchema=")

		if found {
			if strings.Contains(a, "&") {
				b, _, found := strings.Cut(a, "&")
				if found {
					return b
				}
			} else {
				return a
			}
		}
	}
	return defaultSchemaName
}

func MapToPersistencePostgreSQL(platform *operatorapi.SonataFlowPlatform, defaultSchemaName string) *operatorapi.PersistencePostgreSQL {
	if platform.Spec.Persistence != nil && platform.Spec.Persistence.PostgreSQL != nil {
		persistencePostgreSQL := &operatorapi.PersistencePostgreSQL{}
		persistencePostgreSQL.SecretRef = platform.Spec.Persistence.PostgreSQL.SecretRef

		if len(platform.Spec.Persistence.PostgreSQL.JdbcUrl) > 0 {
			persistencePostgreSQL.JdbcUrl = platform.Spec.Persistence.PostgreSQL.JdbcUrl
		}

		serviceRef := &operatorapi.PostgreSQLServiceOptions{}
		if platform.Spec.Persistence.PostgreSQL.ServiceRef != nil {

			serviceRef.DatabaseSchema = defaultSchemaName
			serviceRef.SQLServiceOptions = &operatorapi.SQLServiceOptions{}

			if len(platform.Spec.Persistence.PostgreSQL.ServiceRef.Name) > 0 {
				serviceRef.SQLServiceOptions.Name = platform.Spec.Persistence.PostgreSQL.ServiceRef.Name
			}

			if len(platform.Spec.Persistence.PostgreSQL.ServiceRef.DatabaseName) > 0 {
				serviceRef.SQLServiceOptions.DatabaseName = platform.Spec.Persistence.PostgreSQL.ServiceRef.DatabaseName
			}

			persistencePostgreSQL.ServiceRef = serviceRef
		}
		return persistencePostgreSQL
	}

	return nil
}

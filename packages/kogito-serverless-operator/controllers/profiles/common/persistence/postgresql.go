// Copyright 2023 Apache Software Foundation (ASF)
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

package persistence

import (
	"fmt"

	corev1 "k8s.io/api/core/v1"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
)

const (
	defaultDatabaseName  = "sonataflow"
	timeoutSeconds       = 3
	failureThreshold     = 5
	initialPeriodSeconds = 15
	initialDelaySeconds  = 10
	successThreshold     = 1

	postgreSQLCPULimit      = "500m"
	postgreSQLMemoryLimit   = "256Mi"
	postgreSQLMemoryRequest = "256Mi"
	postgreSQLCPURequest    = "100m"

	defaultPostgreSQLUsername  = "sonataflow"
	defaultPostgresSQLPassword = "sonataflow"
)

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
		{
			Name:  "KOGITO_PERSISTENCE_PROTO_MARSHALLER",
			Value: "false",
		},
		{
			Name:  "KOGITO_PERSISTENCE_QUERY_TIMEOUT_MILLIS",
			Value: "10000",
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

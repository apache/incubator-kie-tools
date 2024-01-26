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
	"strconv"

	corev1 "k8s.io/api/core/v1"

	operatorapi "github.com/apache/incubator-kie-kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-kogito-serverless-operator/controllers/profiles/common/constants"
)

const (
	defaultSchemaName   = "default"
	defaultDatabaseName = "sonataflow"

	quarkusDatasourceJDBCURL  string = "QUARKUS_DATASOURCE_JDBC_URL"
	quarkusDatasourceDBKind   string = "QUARKUS_DATASOURCE_DB_KIND"
	quarkusDatasourceUsername string = "QUARKUS_DATASOURCE_USERNAME"
	quarkusDatasourcePassword string = "QUARKUS_DATASOURCE_PASSWORD"
)

func ConfigurePostgreSqlEnv(postgresql *operatorapi.PersistencePostgreSql, databaseSchema, databaseNamespace string) []corev1.EnvVar {
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
		dataSourceURL = "jdbc:" + constants.PersistenceTypePostgreSQL + "://" + postgresql.ServiceRef.Name + "." + databaseNamespace + ":" + strconv.Itoa(dataSourcePort) + "/" + databaseName + "?currentSchema=" + databaseSchema
	}
	secretRef := corev1.LocalObjectReference{
		Name: postgresql.SecretRef.Name,
	}
	postgresUsername := "POSTGRESQL_USER"
	if len(postgresql.SecretRef.UserKey) > 0 {
		postgresUsername = postgresql.SecretRef.UserKey
	}
	postgresPassword := "POSTGRESQL_PASSWORD"
	if len(postgresql.SecretRef.PasswordKey) > 0 {
		postgresPassword = postgresql.SecretRef.PasswordKey
	}
	return []corev1.EnvVar{
		{
			Name: quarkusDatasourceUsername,
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					Key:                  postgresUsername,
					LocalObjectReference: secretRef,
				},
			},
		},
		{
			Name: quarkusDatasourcePassword,
			ValueFrom: &corev1.EnvVarSource{
				SecretKeyRef: &corev1.SecretKeySelector{
					Key:                  postgresPassword,
					LocalObjectReference: secretRef,
				},
			},
		},
		{
			Name:  quarkusDatasourceDBKind,
			Value: constants.PersistenceTypePostgreSQL,
		},
		{
			Name:  quarkusDatasourceJDBCURL,
			Value: dataSourceURL,
		},
	}
}

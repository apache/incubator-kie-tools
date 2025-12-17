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

package platform

import (
	"testing"

	"github.com/stretchr/testify/assert"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/persistence"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/test"
)

const (
	DataIndexJdbcUrl         = "jdbc:postgresql://postgres:5432/sonataflow?currentSchema=data-index-service"
	DefaultSchema            = "default-schema"
	DataIndexSchemaName      = "data-index-service"
	SonataFlowPlatformName   = "db-migration-sonataflow-platform"
	DBMigrationStrategyJob   = "job"
	DBSecretKeyRef           = "dbSecretName"
	UserNameKey              = "postgresUserKey"
	PasswordKey              = "postgresPasswordKey"
	DbMigrationJobName       = "sonataflow-db-migrator-job"
	DbMigrationContainerName = "db-migration-container"
)

func getBaseSonataFlowPlatformInReadyPhase(namespace string) *v1alpha08.SonataFlowPlatform {
	return test.GetBaseSonataFlowPlatformInReadyPhase(namespace)
}

func TestDbMigratorJob(t *testing.T) {
	t.Run("verify the sonataflow-platform first", func(t *testing.T) {
		ksp := getBaseSonataFlowPlatformInReadyPhase(t.Name())
		assert.Equal(t, ksp.Name, SonataFlowPlatformName)
		assert.Equal(t, ksp.Spec.Services.DataIndex.Persistence.DBMigrationStrategy, DBMigrationStrategyJob)
	})

	t.Run("verify data-index jdbc url", func(t *testing.T) {
		ksp := getBaseSonataFlowPlatformInReadyPhase(t.Name())
		env := persistence.ConfigurePostgreSQLEnv(ksp.Spec.Services.DataIndex.Persistence.PostgreSQL, "data-index-schema", ksp.Namespace, false)

		jdbcUrl := getJdbcUrl(env)
		assert.Equal(t, jdbcUrl, DataIndexJdbcUrl)
	})

	t.Run("verify new quarkus data source", func(t *testing.T) {
		quarkusDataSource := newQuarkusDataSource(DataIndexJdbcUrl, DBSecretKeyRef, UserNameKey, PasswordKey, DataIndexSchemaName)
		assert.Equal(t, quarkusDataSource.Schema, DataIndexSchemaName)
		assert.Equal(t, quarkusDataSource.JdbcUrl, DataIndexJdbcUrl)
	})

	t.Run("verify new new db migration job config", func(t *testing.T) {
		dbMigrationJobCfg := newDBMigrationJobCfg()
		assert.Equal(t, dbMigrationJobCfg.JobName, DbMigrationJobName)
		assert.Equal(t, dbMigrationJobCfg.ContainerName, DbMigrationContainerName)
	})
}

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
	"context"
	"errors"
	"fmt"
	"strconv"
	"strings"

	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
	"k8s.io/utils/pointer"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/version"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

type QuarkusDataSource struct {
	JdbcUrl  string
	Username string
	Password string
	Schema   string
}

type DBMigratorJob struct {
	MigrateDBDataIndex    bool
	DataIndexDataSource   *QuarkusDataSource
	MigrateDBJobsService  bool
	JobsServiceDataSource *QuarkusDataSource
}

const (
	dbMigrationJobName       = "sonataflow-db-migrator-job"
	dbMigrationContainerName = "db-migration-container"
	dbMigratorToolImage      = "quay.io/rhkp/incubator-kie-kogito-service-db-migration-postgresql:latest"
	dbMigrationCmd           = "./migration.sh"
	dbMigrationJobFailed     = 1
	dbMigrationJobSucceeded  = 1

	migrateDBDataIndex                 = "MIGRATE_DB_DATAINDEX"
	quarkusDataSourceDataIndexJdbcURL  = "QUARKUS_DATASOURCE_DATAINDEX_JDBC_URL"
	quarkusDataSourceDataIndexUserName = "QUARKUS_DATASOURCE_DATAINDEX_USERNAME"
	quarkusDataSourceDataIndexPassword = "QUARKUS_DATASOURCE_DATAINDEX_PASSWORD"
	quarkusFlywayDataIndexSchemas      = "QUARKUS_FLYWAY_DATAINDEX_SCHEMAS"

	migrateDBJobsService                 = "MIGRATE_DB_JOBSSERVICE"
	quarkusDataSourceJobsServiceJdbcURL  = "QUARKUS_DATASOURCE_JOBSSERVICE_JDBC_URL"
	quarkusDataSourceJobsServiceUserName = "QUARKUS_DATASOURCE_JOBSSERVICE_USERNAME"
	quarkusDataSourceJobsServicePassword = "QUARKUS_DATASOURCE_JOBSSERVICE_PASSWORD"
	quarkusFlywayJobsServiceSchemas      = "QUARKUS_FLYWAY_JOBSSERVICE_SCHEMAS"
)

type DBMigrationJobCfg struct {
	JobName       string
	ContainerName string
	ToolImageName string
	MigrationCmd  string
}

func getDBSchemaName(persistencePostgreSQL *operatorapi.PersistencePostgreSQL, defaultSchemaName string) string {
	jdbcURL := persistencePostgreSQL.JdbcUrl

	if len(jdbcURL) == 0 {
		if persistencePostgreSQL.ServiceRef != nil && len(persistencePostgreSQL.ServiceRef.DatabaseSchema) > 0 {
			return persistencePostgreSQL.ServiceRef.DatabaseSchema
		}
	} else {
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

func getQuarkusDataSourceFromPersistence(ctx context.Context, platform *operatorapi.SonataFlowPlatform, persistence *operatorapi.PersistenceOptionsSpec, defaultSchemaName string) *QuarkusDataSource {
	if persistence != nil && persistence.PostgreSQL != nil {
		quarkusDataSource := &QuarkusDataSource{}
		quarkusDataSource.JdbcUrl = persistence.PostgreSQL.JdbcUrl
		quarkusDataSource.Username, _ = services.GetSecretKeyValueString(ctx, persistence.PostgreSQL.SecretRef.Name, persistence.PostgreSQL.SecretRef.UserKey, platform.Namespace)
		quarkusDataSource.Password, _ = services.GetSecretKeyValueString(ctx, persistence.PostgreSQL.SecretRef.Name, persistence.PostgreSQL.SecretRef.PasswordKey, platform.Namespace)
		quarkusDataSource.Schema = getDBSchemaName(persistence.PostgreSQL, defaultSchemaName)
		return quarkusDataSource
	}

	return nil
}

func NewDBMigratorJobData(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, pshDI services.PlatformServiceHandler, pshJS services.PlatformServiceHandler) *DBMigratorJob {

	diJobsBasedDBMigration := false
	jsJobsBasedDBMigration := false

	if pshDI.IsPersistenceEnabledtInSpec() {
		diJobsBasedDBMigration = services.IsJobsBasedDBMigration(platform.Spec.Services.DataIndex.Persistence)
	}
	if pshJS.IsPersistenceEnabledtInSpec() {
		jsJobsBasedDBMigration = services.IsJobsBasedDBMigration(platform.Spec.Services.JobService.Persistence)
	}

	if (pshDI.IsServiceSetInSpec() && diJobsBasedDBMigration) || (pshJS.IsServiceSetInSpec() && jsJobsBasedDBMigration) {
		quarkusDataSourceDataIndex := &QuarkusDataSource{}
		quarkusDataSourceJobService := &QuarkusDataSource{}

		if diJobsBasedDBMigration {
			quarkusDataSourceDataIndex = getQuarkusDataSourceFromPersistence(ctx, platform, platform.Spec.Services.DataIndex.Persistence, "data-index-service")
		}

		if jsJobsBasedDBMigration {
			quarkusDataSourceJobService = getQuarkusDataSourceFromPersistence(ctx, platform, platform.Spec.Services.JobService.Persistence, "jobs-service")
		}

		return &DBMigratorJob{
			MigrateDBDataIndex:    diJobsBasedDBMigration,
			DataIndexDataSource:   quarkusDataSourceDataIndex,
			MigrateDBJobsService:  jsJobsBasedDBMigration,
			JobsServiceDataSource: quarkusDataSourceJobService,
		}
	}
	return nil
}

func newQuarkusDataSource(jdbcURL string, userName string, password string, schema string) *QuarkusDataSource {
	return &QuarkusDataSource{
		JdbcUrl:  jdbcURL,
		Username: userName,
		Password: password,
		Schema:   schema,
	}
}

func (dbmj DBMigratorJob) CreateJobDBMigration(platform *operatorapi.SonataFlowPlatform) *batchv1.Job {

	diQuarkusDataSource := newQuarkusDataSource("", "", "", "")
	jsQuarkusDataSource := newQuarkusDataSource("", "", "", "")

	if dbmj.DataIndexDataSource != nil {
		diQuarkusDataSource.JdbcUrl = dbmj.DataIndexDataSource.JdbcUrl
		diQuarkusDataSource.Username = dbmj.DataIndexDataSource.Username
		diQuarkusDataSource.Password = dbmj.DataIndexDataSource.Password
		diQuarkusDataSource.Schema = dbmj.DataIndexDataSource.Schema
	}

	if dbmj.JobsServiceDataSource != nil {
		jsQuarkusDataSource.JdbcUrl = dbmj.JobsServiceDataSource.JdbcUrl
		jsQuarkusDataSource.Username = dbmj.JobsServiceDataSource.Username
		jsQuarkusDataSource.Password = dbmj.JobsServiceDataSource.Password
		jsQuarkusDataSource.Schema = dbmj.JobsServiceDataSource.Schema
	}

	dbMigrationJobCfg := newDBMigrationJobCfg()
	job := &batchv1.Job{
		ObjectMeta: metav1.ObjectMeta{
			Name:      dbMigrationJobCfg.JobName,
			Namespace: platform.Namespace,
		},
		Spec: batchv1.JobSpec{
			Template: corev1.PodTemplateSpec{
				Spec: corev1.PodSpec{
					Containers: []corev1.Container{
						{
							Name:  dbMigrationJobCfg.ContainerName,
							Image: dbMigrationJobCfg.ToolImageName,
							Env: []corev1.EnvVar{
								{
									Name:  migrateDBDataIndex,
									Value: strconv.FormatBool(dbmj.MigrateDBDataIndex),
								},
								{
									Name:  quarkusDataSourceDataIndexJdbcURL,
									Value: diQuarkusDataSource.JdbcUrl,
								},
								{
									Name:  quarkusDataSourceDataIndexUserName,
									Value: diQuarkusDataSource.Username,
								},
								{
									Name:  quarkusDataSourceDataIndexPassword,
									Value: diQuarkusDataSource.Password,
								},
								{
									Name:  quarkusFlywayDataIndexSchemas,
									Value: diQuarkusDataSource.Schema,
								},
								{
									Name:  migrateDBJobsService,
									Value: strconv.FormatBool(dbmj.MigrateDBJobsService),
								},
								{
									Name:  quarkusDataSourceJobsServiceJdbcURL,
									Value: jsQuarkusDataSource.JdbcUrl,
								},
								{
									Name:  quarkusDataSourceJobsServiceUserName,
									Value: jsQuarkusDataSource.Username,
								},
								{
									Name:  quarkusDataSourceJobsServicePassword,
									Value: jsQuarkusDataSource.Password,
								},
								{
									Name:  quarkusFlywayJobsServiceSchemas,
									Value: jsQuarkusDataSource.Schema,
								},
							},
							Command: []string{
								dbMigrationJobCfg.MigrationCmd,
							},
						},
					},
					RestartPolicy: "Never",
				},
			},
			BackoffLimit: pointer.Int32(0),
		},
	}
	return job
}

// GetDBMigrationJobStatus Returns db migration job status
func (dbmj DBMigratorJob) GetDBMigrationJobStatus(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform) (*batchv1.JobStatus, error) {
	job, err := client.BatchV1().Jobs(platform.Namespace).Get(ctx, dbMigrationJobName, metav1.GetOptions{})
	if err != nil {
		klog.V(log.E).InfoS("Error getting DB migrator job while monitoring completion: ", "error", err)
		return nil, err
	}
	return &job.Status, nil
}

// NewSonataFlowPlatformDBMigrationPhase Returns a new DB migration phase for SonataFlowPlatform
func NewSonataFlowPlatformDBMigrationPhase(status operatorapi.DBMigrationStatus, message string, reason string) *operatorapi.SonataFlowPlatformDBMigrationPhase {
	return &operatorapi.SonataFlowPlatformDBMigrationPhase{
		Status:  status,
		Message: message,
		Reason:  reason,
	}
}

// UpdateSonataFlowPlatformDBMigrationPhase Updates a given SonataFlowPlatformDBMigrationPhase with the supplied values
func UpdateSonataFlowPlatformDBMigrationPhase(dbMigrationStatus *operatorapi.SonataFlowPlatformDBMigrationPhase, status operatorapi.DBMigrationStatus, message string, reason string) *operatorapi.SonataFlowPlatformDBMigrationPhase {
	if dbMigrationStatus != nil {
		dbMigrationStatus.Status = status
		dbMigrationStatus.Message = message
		dbMigrationStatus.Reason = reason
		return dbMigrationStatus
	}
	return nil
}

func getKogitoDBMigratorToolImageName() string {

	imgTag := cfg.GetCfg().KogitoDBMigratorToolImageTag

	if imgTag == "" {
		// returns "docker.io/apache/incubator-kie-kogito-db-migrator-tool:<tag>"
		imgTag = fmt.Sprintf("%s-%s:%s", constants.ImageNamePrefix, constants.KogitoDBMigratorTool, version.GetImageTagVersion())
	}
	return imgTag
}

func newDBMigrationJobCfg() *DBMigrationJobCfg {
	return &DBMigrationJobCfg{
		JobName:       dbMigrationJobName,
		ContainerName: dbMigrationContainerName,
		ToolImageName: getKogitoDBMigratorToolImageName(),
		MigrationCmd:  dbMigrationCmd,
	}
}

// ReconcileDBMigrationJob Check the status of running DB migration job and return status
func (dbmj DBMigratorJob) ReconcileDBMigrationJob(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform) (*batchv1.JobStatus, error) {
	platform.Status.SonataFlowPlatformDBMigrationPhase = NewSonataFlowPlatformDBMigrationPhase(operatorapi.DBMigrationStatusStarted, operatorapi.MessageDBMigrationStatusStarted, operatorapi.ReasonDBMigrationStatusStarted)

	dbMigratorJobStatus, err := dbmj.GetDBMigrationJobStatus(ctx, client, platform)
	if err != nil {
		return nil, err
	}

	klog.V(log.I).InfoS("Db migration job status: ", "active", dbMigratorJobStatus.Active, "ready", dbMigratorJobStatus.Ready, "failed", dbMigratorJobStatus.Failed, "success", dbMigratorJobStatus.Succeeded, "CompletedIndexes", dbMigratorJobStatus.CompletedIndexes, "terminatedPods", dbMigratorJobStatus.UncountedTerminatedPods)

	if dbMigratorJobStatus.Failed == dbMigrationJobFailed {
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusFailed, operatorapi.MessageDBMigrationStatusFailed, operatorapi.ReasonDBMigrationStatusFailed)
		klog.V(log.I).InfoS("DB migration job failed")
		return dbMigratorJobStatus, errors.New("DB migration job failed")
	} else if dbMigratorJobStatus.Succeeded == dbMigrationJobSucceeded {
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusSucceeded, operatorapi.MessageDBMigrationStatusSucceeded, operatorapi.ReasonDBMigrationStatusSucceeded)
		klog.V(log.I).InfoS("DB migration job succeeded")
	} else {
		// DB migration is still running
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusInProgress, operatorapi.MessageDBMigrationStatusInProgress, operatorapi.ReasonDBMigrationStatusInProgress)
	}

	return dbMigratorJobStatus, nil
}

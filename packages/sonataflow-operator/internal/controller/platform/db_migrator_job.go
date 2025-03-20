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

	batchv1 "k8s.io/api/batch/v1"
	corev1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/klog/v2"
	"k8s.io/utils/pointer"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/version"

	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/container-builder/client"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/platform/services"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/persistence"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/log"
)

type QuarkusDataSource struct {
	JdbcUrl           string
	SecretRefName     string
	SecretUserKey     string
	SecretPasswordKey string
	Schema            string
}

type DBMigratorJob struct {
	MigrateDBDataIndex    bool
	DataIndexDataSource   *QuarkusDataSource
	MigrateDBJobsService  bool
	JobsServiceDataSource *QuarkusDataSource
}

type DBMigratorJobStatus struct {
	Name           string
	BatchJobStatus *batchv1.JobStatus
}

const (
	dbMigrationJobName       = "sonataflow-db-migrator-job"
	dbMigrationContainerName = "db-migration-container"
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
}

func getJdbcUrl(env []corev1.EnvVar) string {
	if env != nil {
		for i := 0; i < len(env); i++ {
			if env[i].Name == "QUARKUS_DATASOURCE_JDBC_URL" {
				return env[i].Value
			}
		}
	}
	return ""
}

// getQuarkusDSFromServicePersistence Returns QuarkusDataSource from service level persistence config
func getQuarkusDSFromServicePersistence(platform *operatorapi.SonataFlowPlatform, persistenceOptionsSpec *operatorapi.PersistenceOptionsSpec, defaultSchemaName string) *QuarkusDataSource {
	klog.InfoS("Using service level persistence for PostgreSQL", "defaultSchemaName", defaultSchemaName)
	quarkusDataSource := &QuarkusDataSource{}
	env := persistence.ConfigurePostgreSQLEnv(persistenceOptionsSpec.PostgreSQL, defaultSchemaName, platform.Namespace)
	quarkusDataSource.JdbcUrl = getJdbcUrl(env)
	quarkusDataSource.SecretRefName = persistenceOptionsSpec.PostgreSQL.SecretRef.Name
	quarkusDataSource.SecretUserKey = persistenceOptionsSpec.PostgreSQL.SecretRef.UserKey
	quarkusDataSource.SecretPasswordKey = persistenceOptionsSpec.PostgreSQL.SecretRef.PasswordKey
	quarkusDataSource.Schema = persistence.GetDBSchemaName(persistenceOptionsSpec.PostgreSQL, defaultSchemaName)
	return quarkusDataSource
}

// getQuarkusDSFromPlatformPersistence Returns QuarkusDataSource from platform level persistence config
func getQuarkusDSFromPlatformPersistence(platform *operatorapi.SonataFlowPlatform, defaultSchemaName string) *QuarkusDataSource {
	klog.InfoS("Using platform level persistence for PostgreSQL", "defaultSchemaName", defaultSchemaName)
	quarkusDataSource := &QuarkusDataSource{}
	postgresql := persistence.MapToPersistencePostgreSQL(platform, defaultSchemaName)

	env := persistence.ConfigurePostgreSQLEnv(postgresql, defaultSchemaName, platform.Namespace)
	quarkusDataSource.JdbcUrl = getJdbcUrl(env)
	quarkusDataSource.SecretRefName = platform.Spec.Persistence.PostgreSQL.SecretRef.Name
	quarkusDataSource.SecretUserKey = platform.Spec.Persistence.PostgreSQL.SecretRef.UserKey
	quarkusDataSource.SecretPasswordKey = platform.Spec.Persistence.PostgreSQL.SecretRef.PasswordKey
	quarkusDataSource.Schema = persistence.GetDBSchemaName(postgresql, defaultSchemaName)
	return quarkusDataSource
}

// getQuarkusDataSourceFromPersistence PostgreSQL persistence can be defined at platform level (where both DI and JS will use the same DB defined at platform level) or db can defined at Service level. Service level config will take precedence over platform level config.
func getQuarkusDataSourceFromPersistence(platform *operatorapi.SonataFlowPlatform, persistenceOptionsSpec *operatorapi.PersistenceOptionsSpec, defaultSchemaName string) *QuarkusDataSource {

	if persistenceOptionsSpec != nil && persistenceOptionsSpec.PostgreSQL != nil {
		return getQuarkusDSFromServicePersistence(platform, persistenceOptionsSpec, defaultSchemaName)
	} else if platform != nil && platform.Spec.Persistence != nil && platform.Spec.Persistence.PostgreSQL != nil {
		return getQuarkusDSFromPlatformPersistence(platform, defaultSchemaName)
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
			quarkusDataSourceDataIndex = getQuarkusDataSourceFromPersistence(platform, platform.Spec.Services.DataIndex.Persistence, pshDI.GetServiceName())
		}

		if jsJobsBasedDBMigration {
			quarkusDataSourceJobService = getQuarkusDataSourceFromPersistence(platform, platform.Spec.Services.JobService.Persistence, pshJS.GetServiceName())
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

// IsJobsBasedDBMigration returns whether job based db migration approach is needed?
func IsJobsBasedDBMigration(platform *operatorapi.SonataFlowPlatform, pshDI services.PlatformServiceHandler, pshJS services.PlatformServiceHandler) bool {
	diJobsBasedDBMigration := false
	jsJobsBasedDBMigration := false

	if pshDI.IsPersistenceEnabledtInSpec() {
		diJobsBasedDBMigration = services.IsJobsBasedDBMigration(platform.Spec.Services.DataIndex.Persistence)
	}
	if pshJS.IsPersistenceEnabledtInSpec() {
		jsJobsBasedDBMigration = services.IsJobsBasedDBMigration(platform.Spec.Services.JobService.Persistence)
	}

	return (pshDI.IsServiceSetInSpec() && diJobsBasedDBMigration) || (pshJS.IsServiceSetInSpec() && jsJobsBasedDBMigration)
}

func createOrUpdateDBMigrationJob(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, pshDI services.PlatformServiceHandler, pshJS services.PlatformServiceHandler) (*DBMigratorJob, error) {
	dbMigratorJob := NewDBMigratorJobData(ctx, client, platform, pshDI, pshJS)

	// Invoke DB Migration only if both or either DI/JS services are requested, in addition to DBMigrationStrategyJob
	if dbMigratorJob != nil {
		job := createJobDBMigration(platform, dbMigratorJob)
		klog.V(log.I).InfoS("Starting DB Migration Job: ", "namespace", platform.Namespace, "job", job.Name)
		if err := controllerutil.SetControllerReference(platform, job, client.Scheme()); err != nil {
			return nil, err
		}
		if op, err := controllerutil.CreateOrUpdate(ctx, client, job, func() error {
			return nil
		}); err != nil {
			return dbMigratorJob, err
		} else {
			klog.V(log.I).InfoS("DB Migration Job successfully created on cluster", "operation", op, "namespace", platform.Namespace, "job", job.Name)
		}
	}
	return dbMigratorJob, nil
}

// HandleDBMigrationJob Creates db migration job and executes it on the cluster
func HandleDBMigrationJob(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform, psDI services.PlatformServiceHandler, psJS services.PlatformServiceHandler) (*operatorapi.SonataFlowPlatform, error) {

	dbMigratorJob, err := createOrUpdateDBMigrationJob(ctx, client, platform, psDI, psJS)
	if err != nil {
		return nil, err
	}
	if dbMigratorJob != nil {
		klog.V(log.E).InfoS("Created DB migration job")
		dbMigratorJobStatus, err := dbMigratorJob.ReconcileDBMigrationJob(ctx, client, platform)
		if err != nil {
			return nil, err
		}
		if hasFailed(dbMigratorJobStatus) {
			return nil, errors.New("DB migration job " + dbMigratorJobStatus.Name + " failed in namespace: " + platform.Namespace)
		} else if hasSucceeded(dbMigratorJobStatus) {
			return platform, nil
		} else {
			// DB migration is still running
			return nil, nil
		}
	}

	return platform, nil
}

func newQuarkusDataSource(jdbcURL string, secretRefName string, secretUserKey string, secretPasswordKey string, schema string) *QuarkusDataSource {
	return &QuarkusDataSource{
		JdbcUrl:           jdbcURL,
		SecretRefName:     secretRefName,
		SecretUserKey:     secretUserKey,
		SecretPasswordKey: secretPasswordKey,
		Schema:            schema,
	}
}

func createJobDBMigration(platform *operatorapi.SonataFlowPlatform, dbmj *DBMigratorJob) *batchv1.Job {
	// In DB Migrator Tool, smallrye will throw error for empty string "" while initializing properties.
	// So use an empty space as a default value. Please see more at: https://github.com/eclipse/microprofile-config/issues/671
	nonEmptyValue := " "
	diQuarkusDataSource := newQuarkusDataSource(nonEmptyValue, nonEmptyValue, nonEmptyValue, nonEmptyValue, nonEmptyValue)
	jsQuarkusDataSource := newQuarkusDataSource(nonEmptyValue, nonEmptyValue, nonEmptyValue, nonEmptyValue, nonEmptyValue)

	if dbmj.MigrateDBDataIndex && dbmj.DataIndexDataSource != nil {
		diQuarkusDataSource.JdbcUrl = dbmj.DataIndexDataSource.JdbcUrl
		diQuarkusDataSource.SecretRefName = dbmj.DataIndexDataSource.SecretRefName
		diQuarkusDataSource.SecretUserKey = dbmj.DataIndexDataSource.SecretUserKey
		diQuarkusDataSource.SecretPasswordKey = dbmj.DataIndexDataSource.SecretPasswordKey
		diQuarkusDataSource.Schema = dbmj.DataIndexDataSource.Schema
	}

	if dbmj.MigrateDBJobsService && dbmj.JobsServiceDataSource != nil {
		jsQuarkusDataSource.JdbcUrl = dbmj.JobsServiceDataSource.JdbcUrl
		jsQuarkusDataSource.SecretRefName = dbmj.JobsServiceDataSource.SecretRefName
		jsQuarkusDataSource.SecretUserKey = dbmj.JobsServiceDataSource.SecretUserKey
		jsQuarkusDataSource.SecretPasswordKey = dbmj.JobsServiceDataSource.SecretPasswordKey
		jsQuarkusDataSource.Schema = dbmj.JobsServiceDataSource.Schema
	}

	diDBSecretRef := corev1.LocalObjectReference{
		Name: diQuarkusDataSource.SecretRefName,
	}

	jsDBSecretRef := corev1.LocalObjectReference{
		Name: jsQuarkusDataSource.SecretRefName,
	}

	dbMigrationJobCfg := newDBMigrationJobCfg()

	lbl, _ := getServicesLabelsMap(platform.Name, platform.Namespace, fmt.Sprintf("%s-%s", "sonataflow-db-job", dbMigrationJobCfg.JobName), dbMigrationJobCfg.JobName, fmt.Sprintf("%s-%s", platform.Name, dbMigrationJobCfg.JobName), platform.Name, "sonataflow-operator")

	job := &batchv1.Job{
		ObjectMeta: metav1.ObjectMeta{
			Name:      dbMigrationJobCfg.JobName,
			Namespace: platform.Namespace,
			Labels:    lbl,
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
									Name: quarkusDataSourceDataIndexUserName,
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											Key:                  diQuarkusDataSource.SecretUserKey,
											LocalObjectReference: diDBSecretRef,
										},
									},
								},
								{
									Name: quarkusDataSourceDataIndexPassword,
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											Key:                  diQuarkusDataSource.SecretPasswordKey,
											LocalObjectReference: diDBSecretRef,
										},
									},
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
									Name: quarkusDataSourceJobsServiceUserName,
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											Key:                  jsQuarkusDataSource.SecretUserKey,
											LocalObjectReference: jsDBSecretRef,
										},
									},
								},
								{
									Name: quarkusDataSourceJobsServicePassword,
									ValueFrom: &corev1.EnvVarSource{
										SecretKeyRef: &corev1.SecretKeySelector{
											Key:                  jsQuarkusDataSource.SecretPasswordKey,
											LocalObjectReference: jsDBSecretRef,
										},
									},
								},
								{
									Name:  quarkusFlywayJobsServiceSchemas,
									Value: jsQuarkusDataSource.Schema,
								},
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
func (dbmj DBMigratorJob) GetDBMigrationJobStatus(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform) (*DBMigratorJobStatus, error) {
	job, err := client.BatchV1().Jobs(platform.Namespace).Get(ctx, dbMigrationJobName, metav1.GetOptions{})
	if err != nil {
		klog.V(log.E).InfoS("Error getting DB migrator job while monitoring completion: ", "error", err, "namespace", platform.Namespace, "job", job.Name)
		return nil, err
	}
	return &DBMigratorJobStatus{job.Name, &job.Status}, nil
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

	imgTag := cfg.GetCfg().DbMigratorToolImageTag

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
	}
}

func hasFailed(dbMigratorJobStatus *DBMigratorJobStatus) bool {
	return dbMigratorJobStatus.BatchJobStatus.Failed == dbMigrationJobFailed
}

func hasSucceeded(dbMigratorJobStatus *DBMigratorJobStatus) bool {
	return dbMigratorJobStatus.BatchJobStatus.Succeeded == dbMigrationJobSucceeded
}

// ReconcileDBMigrationJob Check the status of running DB migration job and return status
func (dbmj DBMigratorJob) ReconcileDBMigrationJob(ctx context.Context, client client.Client, platform *operatorapi.SonataFlowPlatform) (*DBMigratorJobStatus, error) {
	platform.Status.SonataFlowPlatformDBMigrationPhase = NewSonataFlowPlatformDBMigrationPhase(operatorapi.DBMigrationStatusStarted, operatorapi.MessageDBMigrationStatusStarted, operatorapi.ReasonDBMigrationStatusStarted)

	dbMigratorJobStatus, err := dbmj.GetDBMigrationJobStatus(ctx, client, platform)
	if err != nil {
		return nil, err
	}

	klog.V(log.I).InfoS("Db migration job status: ", "namespace", platform.Namespace, "job", dbMigratorJobStatus.Name, "active", dbMigratorJobStatus.BatchJobStatus.Active, "ready", dbMigratorJobStatus.BatchJobStatus.Ready, "failed", dbMigratorJobStatus.BatchJobStatus.Failed, "success", dbMigratorJobStatus.BatchJobStatus.Succeeded, "CompletedIndexes", dbMigratorJobStatus.BatchJobStatus.CompletedIndexes, "terminatedPods", dbMigratorJobStatus.BatchJobStatus.UncountedTerminatedPods)

	if hasFailed(dbMigratorJobStatus) {
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusFailed, operatorapi.MessageDBMigrationStatusFailed, operatorapi.ReasonDBMigrationStatusFailed)
		klog.V(log.I).InfoS("DB migration job failed", "namespace", platform.Namespace, "job", dbMigratorJobStatus.Name)
		return dbMigratorJobStatus, errors.New("DB migration job failed. namespace=" + platform.Namespace + " job=" + dbMigratorJobStatus.Name)
	} else if hasSucceeded(dbMigratorJobStatus) {
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusSucceeded, operatorapi.MessageDBMigrationStatusSucceeded, operatorapi.ReasonDBMigrationStatusSucceeded)
		klog.V(log.I).InfoS("DB migration job succeeded", "namespace", platform.Namespace, "job", dbMigratorJobStatus.Name)
	} else {
		// DB migration is still running
		platform.Status.SonataFlowPlatformDBMigrationPhase = UpdateSonataFlowPlatformDBMigrationPhase(platform.Status.SonataFlowPlatformDBMigrationPhase, operatorapi.DBMigrationStatusInProgress, operatorapi.MessageDBMigrationStatusInProgress, operatorapi.ReasonDBMigrationStatusInProgress)
	}

	return dbMigratorJobStatus, nil
}

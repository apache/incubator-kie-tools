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

package constants

const (
	QuarkusHTTP                      = "quarkus-http"
	Post                             = "POST"
	DefaultHTTPProtocol              = "http"
	ConfigMapWorkflowPropsVolumeName = "workflow-properties"

	JobServiceRequestEventsURL                      = "mp.messaging.outgoing.kogito-job-service-job-request-events.url"
	JobServiceRequestEventsConnector                = "mp.messaging.outgoing.kogito-job-service-job-request-events.connector"
	JobServiceRequestEventsMethod                   = "mp.messaging.outgoing.kogito-job-service-job-request-events.method"
	JobServiceStatusChangeEvents                    = "kogito.jobs-service.http.job-status-change-events"
	JobServiceStatusChangeEventsURL                 = "mp.messaging.outgoing.kogito-job-service-job-status-events-http.url"
	JobServiceStatusChangeEventsConnector           = "mp.messaging.outgoing.kogito-job-service-job-status-events-http.connector"
	JobServiceStatusChangeEventsMethod              = "mp.messaging.outgoing.kogito-job-service-job-status-events-http.method"
	JobServiceURLProtocol                           = "http"
	JobServiceDataSourceReactiveURL                 = "quarkus.datasource.reactive.url"
	JobServiceJobEventsPath                         = "/v2/jobs/events"
	JobServiceLeaderCheckExpirationInSeconds        = "kogito.jobs-service.management.leader-check.expiration-in-seconds"
	DefaultJobServiceLeaderCheckExpirationInSeconds = "60"

	KogitoProcessInstancesEventsConnector = "mp.messaging.outgoing.kogito-processinstances-events.connector"
	KogitoProcessInstancesEventsMethod    = "mp.messaging.outgoing.kogito-processinstances-events.method"
	KogitoProcessInstancesEventsURL       = "mp.messaging.outgoing.kogito-processinstances-events.url"
	KogitoProcessInstancesEventsEnabled   = "kogito.events.processinstances.enabled"
	KogitoProcessInstancesEventsPath      = "/processes"
	// KogitoProcessInstancesMultiEventsPath Same value as KogitoProcessInstancesEventsPath intentionally
	KogitoProcessInstancesMultiEventsPath       = "/processes"
	KogitoProcessDefinitionsEventsConnector     = "mp.messaging.outgoing.kogito-processdefinitions-events.connector"
	KogitoProcessDefinitionsEventsMethod        = "mp.messaging.outgoing.kogito-processdefinitions-events.method"
	KogitoProcessDefinitionsEventsURL           = "mp.messaging.outgoing.kogito-processdefinitions-events.url"
	KogitoProcessDefinitionsEventsEnabled       = "kogito.events.processdefinitions.enabled"
	KogitoProcessDefinitionsEventsErrorsEnabled = "kogito.events.processdefinitions.errors.propagate"
	KogitoProcessDefinitionsEventsPath          = "/definitions"
	KogitoUserTasksEventsEnabled                = "kogito.events.usertasks.enabled"
	KogitoJobsPath                              = "/jobs"
	// KogitoDataIndexHealthCheckEnabled configures if a workflow must check for the data index availability as part
	// of its start health check.
	KogitoDataIndexHealthCheckEnabled = "kogito.data-index.health-enabled"
	// KogitoDataIndexURL configures the data index url, this value can be used internally by the workflow.
	KogitoDataIndexURL = "kogito.data-index.url"

	// KogitoJobServiceHealthCheckEnabled configures if a workflow must check for the job service availability as part
	// of its start health check.
	KogitoJobServiceHealthCheckEnabled = "kogito.jobs-service.health-enabled"
	// KogitoJobServiceURL configures the jobs service, this value can be used internally by the workflow.
	KogitoJobServiceURL                            = "kogito.jobs-service.url"
	KogitoServiceURLProperty                       = "kogito.service.url"
	KogitoServiceURLProtocol                       = "http"
	DataIndexKafkaSmallRyeHealthProperty           = `quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`
	JobServiceKafkaSmallRyeHealthProperty          = `quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`
	JobServiceLeaderLivenessSmallRyeHealthProperty = `quarkus.smallrye-health.check."org.kie.kogito.jobs.service.management.JobServiceLeaderLivenessHealthCheck".enabled`
	DataIndexKafkaHealthCheck                      = `quarkus.smallrye-health.check."io.quarkus.kafka.client.health.KafkaHealthCheck".enabled`
	JobServiceKSinkInjectionHealthCheck            = `quarkus.smallrye-health.check."org.kie.kogito.jobs.service.messaging.http.health.knative.KSinkInjectionHealthCheck".enabled`

	DataIndexServiceName = "data-index-service"
	JobServiceName       = "jobs-service"
	ImageNamePrefix      = "docker.io/apache/incubator-kie-kogito"
	DataIndexName        = "data-index"
	KogitoDBMigratorTool = "db-migrator-tool"

	DefaultPostgresServiceName string = "postgresql"
	DefaultDatabaseName        string = "sonataflow"
	DefaultPostgreSQLPort      int    = 5432
)

type PersistenceType string

const (
	PersistenceTypePostgreSQL PersistenceType = "postgresql"
	PersistenceTypeEphemeral  PersistenceType = "ephemeral"
)

func (p PersistenceType) String() string {
	return string(p)
}

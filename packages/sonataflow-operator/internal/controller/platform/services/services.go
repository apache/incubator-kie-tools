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
	"fmt"
	"strconv"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/version"

	appsv1 "k8s.io/api/apps/v1"

	"github.com/imdario/mergo"
	"github.com/magiconair/properties"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/utils/pointer"
	eventingv1 "knative.dev/eventing/pkg/apis/eventing/v1"
	sourcesv1 "knative.dev/eventing/pkg/apis/sources/v1"
	"knative.dev/pkg/apis"
	duckv1 "knative.dev/pkg/apis/duck/v1"
	"knative.dev/pkg/kmeta"
	"knative.dev/pkg/tracker"
	"sigs.k8s.io/controller-runtime/pkg/client"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/knative"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils/kubernetes"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	operatorapi "github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/profiles/common/persistence"
)

const (
	quarkusHibernateORMDatabaseGeneration string = "QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION"
	quarkusFlywayMigrateAtStart           string = "QUARKUS_FLYWAY_MIGRATE_AT_START"
	WaitingKnativeEventing                       = "WaitingKnativeEventing"
)

type PlatformServiceHandler interface {
	// GetContainerName returns the name of the service's container in the deployment.
	GetContainerName() string
	// GetServiceImageName returns the image name of the service's container. It takes in the service and persistence types and returns a string
	// that contains the FQDN of the image, including the tag.
	GetServiceImageName(persistenceName constants.PersistenceType) string
	// GetServiceName returns the name of the kubernetes service prefixed with the platform name
	GetServiceName() string
	// GetServiceCmName returns the name of the configmap associated to the service
	GetServiceCmName() string
	// GetEnvironmentVariables returns the env variables to be injected to the service container
	GetEnvironmentVariables() []corev1.EnvVar
	// GetPodResourceRequirements returns the pod's memory and CPU resource requirements
	// Values for job service taken from
	// https://github.com/parodos-dev/orchestrator-helm-chart/blob/52d09eda56fdbed3060782df29847c97f172600f/charts/orchestrator/values.yaml#L68-L72
	GetPodResourceRequirements() corev1.ResourceRequirements
	// GetReplicaCount Returns the default pod replica count for the given service
	GetReplicaCount() int32
	// GetDeploymentStrategy Returns the deployment strategy for the service
	GetDeploymentStrategy() appsv1.DeploymentStrategy

	// MergeContainerSpec performs a merge with override using the containerSpec argument and the expected values based on the service's pod template specifications. The returning
	// object is the merged result
	MergeContainerSpec(containerSpec *corev1.Container) (*corev1.Container, error)

	// ConfigurePersistence sets the persistence's image and environment values when it is defined in the Persistence field of the service, overriding any existing value.
	ConfigurePersistence(containerSpec *corev1.Container) *corev1.Container

	// MergePodSpec performs a merge with override between the podSpec argument and the expected values based on the service's pod template specification. The returning
	// object is the result of the merge
	MergePodSpec(podSpec corev1.PodSpec) (corev1.PodSpec, error)
	// GenerateServiceProperties returns a property object that contains the application properties required by the service deployment
	GenerateServiceProperties() (*properties.Properties, error)
	// GenerateKnativeResources returns knative resources that bridge between workflow deploys and the service
	GenerateKnativeResources(platform *operatorapi.SonataFlowPlatform, lbl map[string]string) ([]client.Object, *corev1.Event, error)

	// IsServiceSetInSpec returns true if the service is set in the spec.
	IsServiceSetInSpec() bool
	// IsServiceEnabledInSpec returns true if the service is enabled in the spec.
	IsServiceEnabledInSpec() bool
	// IsPersistenceEnabledtInSpec returns true if the service has persistence set in the spec.
	IsPersistenceEnabledtInSpec() bool
	// GetLocalServiceBaseUrl returns the base url of the local service
	GetLocalServiceBaseUrl() string
	// GetServiceBaseUrl returns the base url of the service, based on whether using local or cluster-scoped service.
	GetServiceBaseUrl() string
	// IsServiceEnabled returns true if the service is enabled in either the spec or the status.clusterPlatformRef.
	IsServiceEnabled() bool
	// SetServiceUrlInPlatformStatus sets the service url in the platform's status. if reconciled instance does not have service set in spec AND
	// if cluster referenced platform has said service enabled, use the cluster platform's service
	SetServiceUrlInPlatformStatus(clusterRefPlatform *operatorapi.SonataFlowPlatform)
	// SetServiceUrlInWorkflowStatus sets the service url in a workflow's status.
	SetServiceUrlInWorkflowStatus(workflow *operatorapi.SonataFlow)

	// GetServiceSource returns the source Broker configured for the given service by applying the following precedence rule.
	// The source declared in the given service definition is returned first, if any, otherwise a source declared in the
	// service platform is returned, if any.
	GetServiceSource() *duckv1.Destination

	// Check if K_SINK has injected for Job Service. No Op for Data Index
	CheckKSinkInjected() (bool, error)

	// Returns whether job based, service based or no DB migration is needed
	GetDBMigrationStrategy() operatorapi.DBMigrationStrategyType
}

type DataIndexHandler struct {
	platform *operatorapi.SonataFlowPlatform
}

// GetDBMigrationStrategy returns DB migration approach
func (d *DataIndexHandler) GetDBMigrationStrategy() operatorapi.DBMigrationStrategyType {
	return GetDBMigrationStrategy(d.platform.Spec.Services.DataIndex.Persistence)
}

func NewDataIndexHandler(platform *operatorapi.SonataFlowPlatform) PlatformServiceHandler {
	return &DataIndexHandler{platform: platform}
}

func (d *DataIndexHandler) GetContainerName() string {
	return constants.DataIndexServiceName
}

func (d DataIndexHandler) GetServiceImageName(persistenceType constants.PersistenceType) string {
	if persistenceType == constants.PersistenceTypePostgreSQL && len(cfg.GetCfg().DataIndexPostgreSQLImageTag) > 0 {
		return cfg.GetCfg().DataIndexPostgreSQLImageTag
	}
	if persistenceType == constants.PersistenceTypeEphemeral && len(cfg.GetCfg().DataIndexEphemeralImageTag) > 0 {
		return cfg.GetCfg().DataIndexEphemeralImageTag
	}
	// returns "docker.io/apache/incubator-kie-kogito-data-index-<persistence_layer>:<tag>"
	return fmt.Sprintf("%s-%s-%s:%s", constants.ImageNamePrefix, constants.DataIndexName, persistenceType.String(), version.GetImageTagVersion())
}

func (d *DataIndexHandler) GetServiceName() string {
	return fmt.Sprintf("%s-%s", d.platform.Name, constants.DataIndexServiceName)
}

func (d DataIndexHandler) SetServiceUrlInPlatformStatus(clusterRefPlatform *operatorapi.SonataFlowPlatform) {
	psDI := NewDataIndexHandler(clusterRefPlatform)
	if !isServicesSet(d.platform) && psDI.IsServiceEnabledInSpec() {
		if d.platform.Status.ClusterPlatformRef != nil {
			if d.platform.Status.ClusterPlatformRef.Services == nil {
				d.platform.Status.ClusterPlatformRef.Services = &operatorapi.PlatformServicesStatus{}
			}
			d.platform.Status.ClusterPlatformRef.Services.DataIndexRef = &operatorapi.PlatformServiceRefStatus{
				Url: psDI.GetLocalServiceBaseUrl(),
			}
		}
	}
}

func (d DataIndexHandler) SetServiceUrlInWorkflowStatus(workflow *operatorapi.SonataFlow) {
	if !profiles.IsDevProfile(workflow) && d.IsServiceEnabled() {
		if workflow.Status.Services == nil {
			workflow.Status.Services = &operatorapi.PlatformServicesStatus{}
		}
		workflow.Status.Services.DataIndexRef = &operatorapi.PlatformServiceRefStatus{
			Url: d.GetServiceBaseUrl(),
		}
	}
}

func (d DataIndexHandler) IsServiceSetInSpec() bool {
	return isDataIndexSet(d.platform)
}

func (d *DataIndexHandler) IsServiceEnabledInSpec() bool {
	return isDataIndexEnabled(d.platform)
}

func (d DataIndexHandler) IsPersistenceEnabledtInSpec() bool {
	return d.IsServiceSetInSpec() && d.platform.Spec.Services.DataIndex.Persistence != nil
}

func (d *DataIndexHandler) isServiceEnabledInStatus() bool {
	return d.platform != nil && d.platform.Status.ClusterPlatformRef != nil &&
		d.platform.Status.ClusterPlatformRef.Services != nil && d.platform.Status.ClusterPlatformRef.Services.DataIndexRef != nil &&
		!isServicesSet(d.platform)
}

func (d *DataIndexHandler) IsServiceEnabled() bool {
	return d.IsServiceEnabledInSpec() || d.isServiceEnabledInStatus()
}

func (d *DataIndexHandler) GetServiceBaseUrl() string {
	if d.IsServiceEnabledInSpec() {
		return d.GetLocalServiceBaseUrl()
	}
	if d.isServiceEnabledInStatus() {
		return d.platform.Status.ClusterPlatformRef.Services.DataIndexRef.Url
	}
	return ""
}

func (d *DataIndexHandler) GetLocalServiceBaseUrl() string {
	return GenerateServiceURL(constants.DefaultHTTPProtocol, d.platform.Namespace, d.GetServiceName())
}

func (d *DataIndexHandler) GetEnvironmentVariables() []corev1.EnvVar {
	return []corev1.EnvVar{
		{
			Name:  "KOGITO_DATA_INDEX_QUARKUS_PROFILE",
			Value: "http-events-support",
		},
	}
}

func (d *DataIndexHandler) GetPodResourceRequirements() corev1.ResourceRequirements {
	return corev1.ResourceRequirements{
		Requests: corev1.ResourceList{
			corev1.ResourceCPU:    resource.MustParse("100m"),
			corev1.ResourceMemory: resource.MustParse("1Gi"),
		},
		Limits: corev1.ResourceList{
			corev1.ResourceCPU:    resource.MustParse("200m"),
			corev1.ResourceMemory: resource.MustParse("1Gi"),
		},
	}
}

func (d *DataIndexHandler) MergePodSpec(podSpec corev1.PodSpec) (corev1.PodSpec, error) {
	c := podSpec.DeepCopy()
	err := mergo.Merge(c, d.platform.Spec.Services.DataIndex.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride)
	return *c, err
}

// hasPostgreSQLConfigured returns true when either the SonataFlow Platform PostgreSQL CR's structure or the one in the Data Index service specification is not nil
func (d *DataIndexHandler) hasPostgreSQLConfigured() bool {
	return d.IsServiceSetInSpec() &&
		((d.platform.Spec.Services.DataIndex.Persistence != nil && d.platform.Spec.Services.DataIndex.Persistence.PostgreSQL != nil) ||
			(d.platform.Spec.Persistence != nil && d.platform.Spec.Persistence.PostgreSQL != nil))
}

func GetDBMigrationStrategy(persistence *operatorapi.PersistenceOptionsSpec) operatorapi.DBMigrationStrategyType {
	dbMigrationStrategy := operatorapi.DBMigrationStrategyNone

	if persistence != nil {
		return operatorapi.DBMigrationStrategyType(persistence.DBMigrationStrategy)
	}

	return dbMigrationStrategy
}

func IsServiceBasedDBMigration(persistence *operatorapi.PersistenceOptionsSpec) bool {
	dbMigrationStrategy := GetDBMigrationStrategy(persistence)
	return dbMigrationStrategy == operatorapi.DBMigrationStrategyService
}

func IsJobsBasedDBMigration(persistence *operatorapi.PersistenceOptionsSpec) bool {
	dbMigrationStrategy := GetDBMigrationStrategy(persistence)
	return dbMigrationStrategy == operatorapi.DBMigrationStrategyJob
}

func IsNoDBMigration(persistence *operatorapi.PersistenceOptionsSpec) bool {
	dbMigrationStrategy := GetDBMigrationStrategy(persistence)
	return dbMigrationStrategy == operatorapi.DBMigrationStrategyNone || dbMigrationStrategy == ""
}

func isDBMigrationStrategyService(persistence *v1alpha08.PersistenceOptionsSpec) string {
	dbMigrationStrategyService := "true"
	if persistence != nil {
		dbMigrationStrategyService = strconv.FormatBool(IsServiceBasedDBMigration(persistence))
	}

	return dbMigrationStrategyService
}

func (d *DataIndexHandler) ConfigurePersistence(containerSpec *corev1.Container) *corev1.Container {
	if d.hasPostgreSQLConfigured() {
		p := persistence.RetrievePostgreSQLConfiguration(d.platform.Spec.Services.DataIndex.Persistence, d.platform.Spec.Persistence, d.GetServiceName())
		c := containerSpec.DeepCopy()
		c.Image = d.GetServiceImageName(constants.PersistenceTypePostgreSQL)
		c.Env = append(c.Env, persistence.ConfigurePostgreSQLEnv(p.PostgreSQL, d.GetServiceName(), d.platform.Namespace)...)

		dbMigrationStrategyService := isDBMigrationStrategyService(d.platform.Spec.Services.DataIndex.Persistence)

		// specific to DataIndex
		c.Env = append(c.Env, corev1.EnvVar{Name: quarkusHibernateORMDatabaseGeneration, Value: "update"}, corev1.EnvVar{Name: quarkusFlywayMigrateAtStart, Value: dbMigrationStrategyService})
		return c
	}
	return containerSpec
}

func (d DataIndexHandler) MergeContainerSpec(containerSpec *corev1.Container) (*corev1.Container, error) {
	return mergeContainerSpec(containerSpec, &d.platform.Spec.Services.DataIndex.PodTemplate.Container)
}

func (d *DataIndexHandler) GetReplicaCount() int32 {
	if d.platform.Spec.Services.DataIndex.PodTemplate.Replicas != nil {
		return *d.platform.Spec.Services.DataIndex.PodTemplate.Replicas
	}
	return 1
}

func (d *DataIndexHandler) GetDeploymentStrategy() appsv1.DeploymentStrategy {
	return appsv1.DeploymentStrategy{}
}

func (d *DataIndexHandler) GetServiceCmName() string {
	return fmt.Sprintf("%s-props", d.GetServiceName())
}

func (d *DataIndexHandler) GetServiceSource() *duckv1.Destination {
	if d.platform.Spec.Services.DataIndex.Source != nil {
		return d.platform.Spec.Services.DataIndex.Source
	}
	return GetPlatformBroker(d.platform)
}

func (d *DataIndexHandler) GenerateServiceProperties() (*properties.Properties, error) {
	props := properties.NewProperties()
	props.Set(constants.KogitoServiceURLProperty, d.GetLocalServiceBaseUrl())
	props.Set(constants.DataIndexKafkaHealthCheck, "false")
	return props, nil
}

func (d *DataIndexHandler) CheckKSinkInjected() (bool, error) {
	return true, nil // No op
}

type JobServiceHandler struct {
	platform *operatorapi.SonataFlowPlatform
}

// GetDBMigrationStrategy returns db migration approach otherwise
func (j *JobServiceHandler) GetDBMigrationStrategy() operatorapi.DBMigrationStrategyType {
	return GetDBMigrationStrategy(j.platform.Spec.Services.JobService.Persistence)
}

func NewJobServiceHandler(platform *operatorapi.SonataFlowPlatform) PlatformServiceHandler {
	return &JobServiceHandler{platform: platform}
}

func (j *JobServiceHandler) GetContainerName() string {
	return constants.JobServiceName
}

func (j JobServiceHandler) GetServiceImageName(persistenceType constants.PersistenceType) string {
	if persistenceType == constants.PersistenceTypePostgreSQL && len(cfg.GetCfg().JobsServicePostgreSQLImageTag) > 0 {
		return cfg.GetCfg().JobsServicePostgreSQLImageTag
	}
	if persistenceType == constants.PersistenceTypeEphemeral && len(cfg.GetCfg().JobsServiceEphemeralImageTag) > 0 {
		return cfg.GetCfg().JobsServiceEphemeralImageTag
	}
	// returns "docker.io/apache/incubator-kie-kogito-jobs-service-<persistece_layer>:<tag>"
	return fmt.Sprintf("%s-%s-%s:%s", constants.ImageNamePrefix, constants.JobServiceName, persistenceType.String(), version.GetImageTagVersion())
}

func (j *JobServiceHandler) GetServiceName() string {
	return fmt.Sprintf("%s-%s", j.platform.Name, constants.JobServiceName)
}

func (j *JobServiceHandler) GetServiceCmName() string {
	return fmt.Sprintf("%s-props", j.GetServiceName())
}

func (j JobServiceHandler) SetServiceUrlInPlatformStatus(clusterRefPlatform *operatorapi.SonataFlowPlatform) {
	psJS := NewJobServiceHandler(clusterRefPlatform)
	if !isServicesSet(j.platform) && psJS.IsServiceEnabledInSpec() {
		if j.platform.Status.ClusterPlatformRef != nil {
			if j.platform.Status.ClusterPlatformRef.Services == nil {
				j.platform.Status.ClusterPlatformRef.Services = &operatorapi.PlatformServicesStatus{}
			}
			j.platform.Status.ClusterPlatformRef.Services.JobServiceRef = &operatorapi.PlatformServiceRefStatus{
				Url: psJS.GetLocalServiceBaseUrl(),
			}
		}
	}
}

func (j JobServiceHandler) SetServiceUrlInWorkflowStatus(workflow *operatorapi.SonataFlow) {
	if !profiles.IsDevProfile(workflow) && j.IsServiceEnabled() {
		if workflow.Status.Services == nil {
			workflow.Status.Services = &operatorapi.PlatformServicesStatus{}
		}
		workflow.Status.Services.JobServiceRef = &operatorapi.PlatformServiceRefStatus{
			Url: j.GetServiceBaseUrl(),
		}
	}
}

func (j JobServiceHandler) IsServiceSetInSpec() bool {
	return isJobServiceSet(j.platform)
}

func (j *JobServiceHandler) IsServiceEnabledInSpec() bool {
	return isJobServiceEnabled(j.platform)
}

func (j JobServiceHandler) IsPersistenceEnabledtInSpec() bool {
	return j.IsServiceSetInSpec() && j.platform.Spec.Services.JobService.Persistence != nil
}

func (j *JobServiceHandler) isServiceEnabledInStatus() bool {
	return j.platform != nil && j.platform.Status.ClusterPlatformRef != nil &&
		j.platform.Status.ClusterPlatformRef.Services != nil && j.platform.Status.ClusterPlatformRef.Services.JobServiceRef != nil &&
		!isServicesSet(j.platform)
}

func (j *JobServiceHandler) IsServiceEnabled() bool {
	return j.IsServiceEnabledInSpec() || j.isServiceEnabledInStatus()
}

func (j *JobServiceHandler) GetServiceBaseUrl() string {
	if j.IsServiceEnabledInSpec() {
		return j.GetLocalServiceBaseUrl()
	}
	if j.isServiceEnabledInStatus() {
		return j.platform.Status.ClusterPlatformRef.Services.JobServiceRef.Url
	}
	return ""
}

func (j *JobServiceHandler) GetLocalServiceBaseUrl() string {
	return GenerateServiceURL(constants.DefaultHTTPProtocol, j.platform.Namespace, j.GetServiceName())
}

func (j *JobServiceHandler) GetEnvironmentVariables() []corev1.EnvVar {
	return []corev1.EnvVar{}
}

func (j *JobServiceHandler) GetPodResourceRequirements() corev1.ResourceRequirements {
	return corev1.ResourceRequirements{
		Requests: corev1.ResourceList{
			corev1.ResourceCPU:    resource.MustParse("250m"),
			corev1.ResourceMemory: resource.MustParse("64Mi"),
		},
		Limits: corev1.ResourceList{
			corev1.ResourceCPU:    resource.MustParse("500m"),
			corev1.ResourceMemory: resource.MustParse("1Gi"),
		},
	}
}

func (j *JobServiceHandler) GetReplicaCount() int32 {
	if j.platform.Spec.Services.JobService.PodTemplate.Replicas != nil && *j.platform.Spec.Services.JobService.PodTemplate.Replicas == 0 {
		return 0
	}
	return 1
}

func (j *JobServiceHandler) GetDeploymentStrategy() appsv1.DeploymentStrategy {
	return appsv1.DeploymentStrategy{
		Type:          appsv1.RecreateDeploymentStrategyType,
		RollingUpdate: nil,
	}
}

func (j JobServiceHandler) MergeContainerSpec(containerSpec *corev1.Container) (*corev1.Container, error) {
	return mergeContainerSpec(containerSpec, &j.platform.Spec.Services.JobService.PodTemplate.Container)
}

// hasPostgreSQLConfigured returns true when either the SonataFlow Platform PostgreSQL CR's structure or the one in the Job service specification is not nil
func (j *JobServiceHandler) hasPostgreSQLConfigured() bool {
	return j.IsServiceSetInSpec() &&
		((j.platform.Spec.Services.JobService.Persistence != nil && j.platform.Spec.Services.JobService.Persistence.PostgreSQL != nil) ||
			(j.platform.Spec.Persistence != nil && j.platform.Spec.Persistence.PostgreSQL != nil))
}

func (j *JobServiceHandler) ConfigurePersistence(containerSpec *corev1.Container) *corev1.Container {

	if j.hasPostgreSQLConfigured() {
		c := containerSpec.DeepCopy()
		c.Image = j.GetServiceImageName(constants.PersistenceTypePostgreSQL)
		p := persistence.RetrievePostgreSQLConfiguration(j.platform.Spec.Services.JobService.Persistence, j.platform.Spec.Persistence, j.GetServiceName())
		c.Env = append(c.Env, persistence.ConfigurePostgreSQLEnv(p.PostgreSQL, j.GetServiceName(), j.platform.Namespace)...)

		dbMigrationStrategyService := isDBMigrationStrategyService(j.platform.Spec.Services.JobService.Persistence)

		// Specific to Job Service
		c.Env = append(c.Env, corev1.EnvVar{Name: "QUARKUS_FLYWAY_MIGRATE_AT_START", Value: dbMigrationStrategyService})
		c.Env = append(c.Env, corev1.EnvVar{Name: "KOGITO_JOBS_SERVICE_LOADJOBERRORSTRATEGY", Value: "FAIL_SERVICE"})
		return c
	}
	return containerSpec
}

func (j *JobServiceHandler) MergePodSpec(podSpec corev1.PodSpec) (corev1.PodSpec, error) {
	c := podSpec.DeepCopy()
	err := mergo.Merge(c, j.platform.Spec.Services.JobService.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride)
	return *c, err
}

func (j *JobServiceHandler) GenerateServiceProperties() (*properties.Properties, error) {
	props := properties.NewProperties()
	props.Set(constants.KogitoServiceURLProperty, GenerateServiceURL(constants.KogitoServiceURLProtocol, j.platform.Namespace, j.GetServiceName()))
	props.Set(constants.JobServiceKafkaSmallRyeHealthProperty, "false")
	props.Set(constants.JobServiceLeaderLivenessSmallRyeHealthProperty, "true")
	props.Set(constants.JobServiceLeaderCheckExpirationInSeconds, constants.DefaultJobServiceLeaderCheckExpirationInSeconds)

	if j.GetServiceSource() == nil {
		props.Set(constants.JobServiceKSinkInjectionHealthCheck, "false")
	} else {
		props.Set(constants.JobServiceKSinkInjectionHealthCheck, "true")
	}

	// add data source reactive URL
	if j.hasPostgreSQLConfigured() {
		p := persistence.RetrievePostgreSQLConfiguration(j.platform.Spec.Services.JobService.Persistence, j.platform.Spec.Persistence, j.GetServiceName())
		dataSourceReactiveURL, err := generateReactiveURL(p.PostgreSQL, j.GetServiceName(), j.platform.Namespace, constants.DefaultDatabaseName, constants.DefaultPostgreSQLPort)
		if err != nil {
			return nil, err
		}
		props.Set(constants.JobServiceDataSourceReactiveURL, dataSourceReactiveURL)
	}

	if isDataIndexEnabled(j.platform) {
		props.Set(constants.JobServiceStatusChangeEvents, "true")
		if j.GetServiceSource() == nil {
			di := NewDataIndexHandler(j.platform)
			props.Set(constants.JobServiceStatusChangeEventsURL, di.GetLocalServiceBaseUrl()+"/jobs")
		} else {
			props.Set(constants.JobServiceStatusChangeEventsURL, constants.KnativeInjectedEnvVar)
			props.Set(constants.JobServiceStatusChangeEventsConnector, constants.QuarkusHTTP)
			props.Set(constants.JobServiceStatusChangeEventsMethod, constants.Post)
		}
	}
	props.Sort()
	return props, nil
}

func SetServiceUrlsInWorkflowStatus(pl *operatorapi.SonataFlowPlatform, workflow *operatorapi.SonataFlow) {
	tpsDI := NewDataIndexHandler(pl)
	tpsJS := NewJobServiceHandler(pl)

	workflow.Status.Services = nil
	tpsDI.SetServiceUrlInWorkflowStatus(workflow)
	tpsJS.SetServiceUrlInWorkflowStatus(workflow)
}

func (j *JobServiceHandler) GetServiceSource() *duckv1.Destination {
	if j.platform.Spec.Services.JobService.Source != nil {
		return j.platform.Spec.Services.JobService.Source
	}
	return GetPlatformBroker(j.platform)
}

func (j *JobServiceHandler) GetServiceSink() *duckv1.Destination {
	if j.platform.Spec.Services.JobService.Sink != nil {
		return j.platform.Spec.Services.JobService.Sink
	}
	return GetPlatformBroker(j.platform)
}

func isDataIndexEnabled(platform *operatorapi.SonataFlowPlatform) bool {
	return isDataIndexSet(platform) && platform.Spec.Services.DataIndex.Enabled != nil &&
		*platform.Spec.Services.DataIndex.Enabled
}

func isJobServiceEnabled(platform *operatorapi.SonataFlowPlatform) bool {
	return isJobServiceSet(platform) && platform.Spec.Services.JobService.Enabled != nil &&
		*platform.Spec.Services.JobService.Enabled
}

func isDataIndexSet(platform *operatorapi.SonataFlowPlatform) bool {
	return isServicesSet(platform) && platform.Spec.Services.DataIndex != nil
}

func isJobServiceSet(platform *operatorapi.SonataFlowPlatform) bool {
	return isServicesSet(platform) && platform.Spec.Services.JobService != nil
}

func isServicesSet(platform *operatorapi.SonataFlowPlatform) bool {
	return platform != nil && platform.Spec.Services != nil
}

func GenerateServiceURL(protocol string, namespace string, name string) string {
	var serviceUrl string
	if len(namespace) > 0 {
		serviceUrl = fmt.Sprintf("%s://%s.%s", protocol, name, namespace)
	} else {
		serviceUrl = fmt.Sprintf("%s://%s", protocol, name)
	}
	return serviceUrl
}

// mergeContainerSpec Produces the merging between the operatorapi.ContainerSpec provided in a SonataFlowPlatform
// service, for example, platform.services.jobsService.podTemplate.container, and the destination container for the
// corresponding service deployment. This method consider specific processing like not overriding environment vars
// already configured by the operator in the destination container.
func mergeContainerSpec(dest *corev1.Container, sourceSpec *operatorapi.ContainerSpec) (*corev1.Container, error) {
	result := dest.DeepCopy()
	source := sourceSpec.ToContainer()
	err := mergeContainerPreservingEnvVars(result, &source)
	return result, err
}

// mergeContainerSpecPreservingEnvVars Merges the source container into the dest container by giving priority to the
// env variables already configured in the dest container when both containers have the same variable name.
func mergeContainerPreservingEnvVars(dest *corev1.Container, source *corev1.Container) error {
	currentEnv := dest.Env
	if err := mergo.Merge(dest, source, mergo.WithOverride); err != nil {
		return err
	}
	dest.Env = currentEnv
	for _, envVar := range source.Env {
		kubernetes.AddEnvIfNotPresent(dest, envVar)
	}
	return nil
}

// GetPlatformBroker gets the default broker for the platform.
func GetPlatformBroker(platform *operatorapi.SonataFlowPlatform) *duckv1.Destination {
	if platform != nil && platform.Spec.Eventing != nil && platform.Spec.Eventing.Broker != nil {
		return platform.Spec.Eventing.Broker
	}
	return nil
}

func (d *DataIndexHandler) GetSourceBroker() *duckv1.Destination {
	if d.platform != nil && d.platform.Spec.Services.DataIndex.Source != nil && d.platform.Spec.Services.DataIndex.Source.Ref != nil {
		return d.platform.Spec.Services.DataIndex.Source
	}
	return GetPlatformBroker(d.platform)
}

func (d *DataIndexHandler) newTrigger(labels map[string]string, annotations map[string]string, brokerName, namespace, serviceName, tag, eventType, path string, platform *operatorapi.SonataFlowPlatform) *eventingv1.Trigger {
	return &eventingv1.Trigger{
		ObjectMeta: metav1.ObjectMeta{
			Name:        kmeta.ChildName(fmt.Sprintf("data-index-%s-", tag), string(platform.GetUID())),
			Namespace:   namespace,
			Labels:      labels,
			Annotations: annotations,
		},
		Spec: eventingv1.TriggerSpec{
			Broker: brokerName,
			Filter: &eventingv1.TriggerFilter{
				Attributes: eventingv1.TriggerFilterAttributes{
					"type": eventType,
				},
			},
			Subscriber: duckv1.Destination{
				Ref: &duckv1.KReference{
					Name:       serviceName,
					Namespace:  platform.Namespace,
					APIVersion: "v1",
					Kind:       "Service",
				},
				URI: &apis.URL{
					Path: path,
				},
			},
		},
	}
}
func (d *DataIndexHandler) GenerateKnativeResources(platform *operatorapi.SonataFlowPlatform, lbl map[string]string) ([]client.Object, *corev1.Event, error) {
	broker := d.GetSourceBroker()
	if broker == nil || len(broker.Ref.Name) == 0 {
		return nil, nil, nil // Nothing to do
	}
	brokerName := broker.Ref.Name
	namespace := broker.Ref.Namespace
	if len(namespace) == 0 {
		namespace = platform.Namespace
	}
	var brokerObject *eventingv1.Broker
	var err error
	if brokerObject, err = knative.ValidateBroker(brokerName, namespace); err != nil {
		event := &corev1.Event{
			Type:    corev1.EventTypeWarning,
			Reason:  WaitingKnativeEventing,
			Message: fmt.Sprintf("%s for service: %s", err.Error(), d.GetServiceName()),
		}
		return nil, event, err
	}
	annotations := make(map[string]string)
	managedAnnotations := make(map[string]string)
	addTriggerAnnotations(knative.GetBrokerClass(brokerObject), managedAnnotations)
	serviceName := d.GetServiceName()
	return []client.Object{
		d.newTrigger(lbl, annotations, brokerName, namespace, serviceName, "process-error", "ProcessInstanceErrorDataEvent", constants.KogitoProcessInstancesEventsPath, platform),
		d.newTrigger(lbl, annotations, brokerName, namespace, serviceName, "process-node", "ProcessInstanceNodeDataEvent", constants.KogitoProcessInstancesEventsPath, platform),
		d.newTrigger(lbl, annotations, brokerName, namespace, serviceName, "process-state", "ProcessInstanceStateDataEvent", constants.KogitoProcessInstancesEventsPath, platform),
		d.newTrigger(lbl, annotations, brokerName, namespace, serviceName, "process-variable", "ProcessInstanceVariableDataEvent", constants.KogitoProcessInstancesEventsPath, platform),
		d.newTrigger(lbl, managedAnnotations, brokerName, namespace, serviceName, "process-definition", "ProcessDefinitionEvent", constants.KogitoProcessDefinitionsEventsPath, platform),
		d.newTrigger(lbl, annotations, brokerName, namespace, serviceName, "process-instance-multiple", "MultipleProcessInstanceDataEvent", constants.KogitoProcessInstancesMultiEventsPath, platform),
		d.newTrigger(lbl, managedAnnotations, brokerName, namespace, serviceName, "jobs", "JobEvent", constants.KogitoJobsPath, platform)}, nil, nil
}

func (d JobServiceHandler) GetSourceBroker() *duckv1.Destination {
	if d.platform.Spec.Services.JobService.Source != nil && d.platform.Spec.Services.JobService.Source.Ref != nil {
		return d.platform.Spec.Services.JobService.Source
	}
	return GetPlatformBroker(d.platform)
}

func addTriggerAnnotations(brokerClass string, annotations map[string]string) {
	if knative.IsKafkaBroker(brokerClass) {
		annotations[knative.KafkaKnativeEventingDeliveryOrder] = knative.KafkaKnativeEventingDeliveryOrderOrdered
	}
}

func (d JobServiceHandler) GetSink() *duckv1.Destination {
	if d.platform.Spec.Services.JobService.Sink != nil {
		return d.platform.Spec.Services.JobService.Sink
	}
	return GetPlatformBroker(d.platform)
}

func (j *JobServiceHandler) GenerateKnativeResources(platform *operatorapi.SonataFlowPlatform, lbl map[string]string) ([]client.Object, *corev1.Event, error) {
	broker := j.GetSourceBroker()
	sink := j.GetSink()
	resultObjs := []client.Object{}

	if broker != nil && len(broker.Ref.Name) > 0 {
		brokerName := broker.Ref.Name
		namespace := broker.Ref.Namespace
		if len(namespace) == 0 {
			namespace = platform.Namespace
		}
		var brokerObject *eventingv1.Broker
		var err error
		if brokerObject, err = knative.ValidateBroker(brokerName, namespace); err != nil {
			event := &corev1.Event{
				Type:    corev1.EventTypeWarning,
				Reason:  WaitingKnativeEventing,
				Message: fmt.Sprintf("%s for service: %s", err.Error(), j.GetServiceName()),
			}
			return nil, event, err
		}
		annotations := make(map[string]string)
		addTriggerAnnotations(knative.GetBrokerClass(brokerObject), annotations)
		jobCreateTrigger := &eventingv1.Trigger{
			ObjectMeta: metav1.ObjectMeta{
				Name:        kmeta.ChildName("jobs-service-create-job-", string(platform.GetUID())),
				Namespace:   namespace,
				Labels:      lbl,
				Annotations: annotations,
			},
			Spec: eventingv1.TriggerSpec{
				Broker: brokerName,
				Filter: &eventingv1.TriggerFilter{
					Attributes: eventingv1.TriggerFilterAttributes{
						"type": "job.create",
					},
				},
				Subscriber: duckv1.Destination{
					Ref: &duckv1.KReference{
						Name:       j.GetServiceName(),
						Namespace:  platform.Namespace,
						APIVersion: "v1",
						Kind:       "Service",
					},
					URI: &apis.URL{
						Path: constants.JobServiceJobEventsPath,
					},
				},
			},
		}
		resultObjs = append(resultObjs, jobCreateTrigger)
		jobDeleteTrigger := &eventingv1.Trigger{
			ObjectMeta: metav1.ObjectMeta{
				Name:        kmeta.ChildName("jobs-service-delete-job-", string(platform.GetUID())),
				Namespace:   namespace,
				Labels:      lbl,
				Annotations: annotations,
			},
			Spec: eventingv1.TriggerSpec{
				Broker: brokerName,
				Filter: &eventingv1.TriggerFilter{
					Attributes: eventingv1.TriggerFilterAttributes{
						"type": "job.delete",
					},
				},
				Subscriber: duckv1.Destination{
					Ref: &duckv1.KReference{
						Name:       j.GetServiceName(),
						Namespace:  platform.Namespace,
						APIVersion: "v1",
						Kind:       "Service",
					},
					URI: &apis.URL{
						Path: constants.JobServiceJobEventsPath,
					},
				},
			},
		}
		resultObjs = append(resultObjs, jobDeleteTrigger)
	}
	if sink != nil {
		sinkBinding := &sourcesv1.SinkBinding{
			ObjectMeta: metav1.ObjectMeta{
				Name:      fmt.Sprintf("%s-jobs-service-sb", platform.Name),
				Namespace: platform.Namespace,
				Labels:    lbl,
			},
			Spec: sourcesv1.SinkBindingSpec{
				SourceSpec: duckv1.SourceSpec{
					Sink: *sink,
				},
				BindingSpec: duckv1.BindingSpec{
					Subject: tracker.Reference{
						Name:       j.GetServiceName(),
						Namespace:  platform.Namespace,
						APIVersion: "apps/v1",
						Kind:       "Deployment",
					},
				},
			},
		}
		resultObjs = append(resultObjs, sinkBinding)
	}
	return resultObjs, nil, nil
}

func (j *JobServiceHandler) CheckKSinkInjected() (bool, error) {
	if j.GetSink() != nil { //job services has sink configured
		return knative.CheckKSinkInjected(j.GetServiceName(), j.platform.Namespace)
	}
	return true, nil
}

func IsDataIndexEnabled(plf *operatorapi.SonataFlowPlatform) bool {
	if plf.Spec.Services != nil {
		if plf.Spec.Services.DataIndex != nil {
			return pointer.BoolDeref(plf.Spec.Services.DataIndex.Enabled, false)
		}
		return false
	}
	// Check if DataIndex is enabled in the platform status
	if plf.Status.ClusterPlatformRef != nil && plf.Status.ClusterPlatformRef.Services != nil && plf.Status.ClusterPlatformRef.Services.DataIndexRef != nil && len(plf.Status.ClusterPlatformRef.Services.DataIndexRef.Url) > 0 {
		return true
	}
	return false
}

func IsJobServiceEnabled(plf *operatorapi.SonataFlowPlatform) bool {
	if plf.Spec.Services != nil {
		if plf.Spec.Services.JobService != nil {
			return pointer.BoolDeref(plf.Spec.Services.JobService.Enabled, false)
		}
		return false
	}
	// Check if JobService is enabled in the platform status
	if plf.Status.ClusterPlatformRef != nil && plf.Status.ClusterPlatformRef.Services != nil && plf.Status.ClusterPlatformRef.Services.JobServiceRef != nil && len(plf.Status.ClusterPlatformRef.Services.JobServiceRef.Url) > 0 {
		return true
	}
	return false
}

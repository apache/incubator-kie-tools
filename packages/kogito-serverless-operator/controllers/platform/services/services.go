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

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/cfg"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"

	operatorapi "github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/constants"
	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/controllers/profiles/common/persistence"
	"github.com/magiconair/properties"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/version"
	"github.com/imdario/mergo"
)

const (
	quarkusHibernateORMDatabaseGeneration string = "QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION"
	quarkusFlywayMigrateAtStart           string = "QUARKUS_FLYWAY_MIGRATE_AT_START"
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

	// IsServiceSetInSpec returns true if the service is set in the spec.
	IsServiceSetInSpec() bool
	// IsServiceEnabledInSpec returns true if the service is enabled in the spec.
	IsServiceEnabledInSpec() bool
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
}

type DataIndexHandler struct {
	platform *operatorapi.SonataFlowPlatform
}

func NewDataIndexHandler(platform *operatorapi.SonataFlowPlatform) PlatformServiceHandler {
	return DataIndexHandler{platform: platform}
}

func (d DataIndexHandler) GetContainerName() string {
	return constants.DataIndexServiceName
}

func (d DataIndexHandler) GetServiceImageName(persistenceType constants.PersistenceType) string {
	if persistenceType == constants.PersistenceTypePostgreSQL && len(cfg.GetCfg().DataIndexPostgreSQLImageTag) > 0 {
		return cfg.GetCfg().DataIndexPostgreSQLImageTag
	}
	if persistenceType == constants.PersistenceTypeEphemeral && len(cfg.GetCfg().DataIndexEphemeralImageTag) > 0 {
		return cfg.GetCfg().DataIndexEphemeralImageTag
	}
	var tag = version.GetMajorMinor()
	var suffix = ""
	if version.IsSnapshot() {
		tag = "latest"
		//TODO, remove
		suffix = constants.ImageNameNightlySuffix
	}
	// returns "quay.io/kiegroup/kogito-data-index-<persistence_layer>:<tag>"
	return fmt.Sprintf("%s-%s-%s:%s", constants.ImageNamePrefix, constants.DataIndexName, persistenceType.String()+suffix, tag)
}

func (d DataIndexHandler) GetServiceName() string {
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

func (d DataIndexHandler) IsServiceEnabledInSpec() bool {
	return isDataIndexEnabled(d.platform)
}

func (d DataIndexHandler) isServiceEnabledInStatus() bool {
	return d.platform != nil && d.platform.Status.ClusterPlatformRef != nil &&
		d.platform.Status.ClusterPlatformRef.Services != nil && d.platform.Status.ClusterPlatformRef.Services.DataIndexRef != nil &&
		!isServicesSet(d.platform)
}

func (d DataIndexHandler) IsServiceEnabled() bool {
	return d.IsServiceEnabledInSpec() || d.isServiceEnabledInStatus()
}

func (d DataIndexHandler) GetServiceBaseUrl() string {
	if d.IsServiceEnabledInSpec() {
		return d.GetLocalServiceBaseUrl()
	}
	if d.isServiceEnabledInStatus() {
		return d.platform.Status.ClusterPlatformRef.Services.DataIndexRef.Url
	}
	return ""
}

func (d DataIndexHandler) GetLocalServiceBaseUrl() string {
	return GenerateServiceURL(constants.KogitoServiceURLProtocol, d.platform.Namespace, d.GetServiceName())
}

func (d DataIndexHandler) GetEnvironmentVariables() []corev1.EnvVar {
	return []corev1.EnvVar{
		{
			Name:  "KOGITO_DATA_INDEX_QUARKUS_PROFILE",
			Value: "http-events-support",
		},
		{
			Name:  "QUARKUS_HTTP_CORS",
			Value: "true",
		},
		{
			Name:  "QUARKUS_HTTP_CORS_ORIGINS",
			Value: "/.*/",
		},
	}
}

func (d DataIndexHandler) GetPodResourceRequirements() corev1.ResourceRequirements {
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

func (d DataIndexHandler) MergePodSpec(podSpec corev1.PodSpec) (corev1.PodSpec, error) {
	c := podSpec.DeepCopy()
	err := mergo.Merge(c, d.platform.Spec.Services.DataIndex.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride)
	return *c, err
}

// hasPostgreSQLConfigured returns true when either the SonataFlow Platform PostgreSQL CR's structure or the one in the Data Index service specification is not nil
func (d DataIndexHandler) hasPostgreSQLConfigured() bool {
	return d.IsServiceSetInSpec() &&
		((d.platform.Spec.Services.DataIndex.Persistence != nil && d.platform.Spec.Services.DataIndex.Persistence.PostgreSQL != nil) ||
			(d.platform.Spec.Persistence != nil && d.platform.Spec.Persistence.PostgreSQL != nil))
}

func (d DataIndexHandler) ConfigurePersistence(containerSpec *corev1.Container) *corev1.Container {
	if d.hasPostgreSQLConfigured() {
		p := persistence.RetrieveConfiguration(d.platform.Spec.Services.DataIndex.Persistence, d.platform.Spec.Persistence, d.GetServiceName())
		c := containerSpec.DeepCopy()
		c.Image = d.GetServiceImageName(constants.PersistenceTypePostgreSQL)
		c.Env = append(c.Env, persistence.ConfigurePostgreSQLEnv(p.PostgreSQL, d.GetServiceName(), d.platform.Namespace)...)
		// specific to DataIndex
		c.Env = append(c.Env, corev1.EnvVar{Name: quarkusHibernateORMDatabaseGeneration, Value: "update"}, corev1.EnvVar{Name: quarkusFlywayMigrateAtStart, Value: "true"})
		return c
	}
	return containerSpec
}

func (d DataIndexHandler) MergeContainerSpec(containerSpec *corev1.Container) (*corev1.Container, error) {
	c := containerSpec.DeepCopy()
	err := mergo.Merge(c, d.platform.Spec.Services.DataIndex.PodTemplate.Container.ToContainer(), mergo.WithOverride)
	return c, err
}

func (d DataIndexHandler) GetReplicaCount() int32 {
	if d.platform.Spec.Services.DataIndex.PodTemplate.Replicas != nil {
		return *d.platform.Spec.Services.DataIndex.PodTemplate.Replicas
	}
	return 1
}

func (d DataIndexHandler) GetServiceCmName() string {
	return fmt.Sprintf("%s-props", d.GetServiceName())
}

func (d DataIndexHandler) GenerateServiceProperties() (*properties.Properties, error) {
	props := properties.NewProperties()
	props.Set(constants.KogitoServiceURLProperty, d.GetLocalServiceBaseUrl())
	props.Set(constants.DataIndexKafkaSmallRyeHealthProperty, "false")
	return props, nil
}

type JobServiceHandler struct {
	platform *operatorapi.SonataFlowPlatform
}

func NewJobServiceHandler(platform *operatorapi.SonataFlowPlatform) PlatformServiceHandler {
	return JobServiceHandler{platform: platform}
}

func (j JobServiceHandler) GetContainerName() string {
	return constants.JobServiceName
}

func (j JobServiceHandler) GetServiceImageName(persistenceType constants.PersistenceType) string {
	if persistenceType == constants.PersistenceTypePostgreSQL && len(cfg.GetCfg().JobsServicePostgreSQLImageTag) > 0 {
		return cfg.GetCfg().JobsServicePostgreSQLImageTag
	}
	if persistenceType == constants.PersistenceTypeEphemeral && len(cfg.GetCfg().JobsServiceEphemeralImageTag) > 0 {
		return cfg.GetCfg().JobsServiceEphemeralImageTag
	}
	var tag = version.GetMajorMinor()
	var suffix = ""
	if version.IsSnapshot() {
		tag = "latest"
		//TODO remove
		suffix = constants.ImageNameNightlySuffix
	}
	// returns "quay.io/kiegroup/kogito-jobs-service-<persistece_layer>:<tag>"
	return fmt.Sprintf("%s-%s-%s:%s", constants.ImageNamePrefix, constants.JobServiceName, persistenceType.String()+suffix, tag)
}

func (j JobServiceHandler) GetServiceName() string {
	return fmt.Sprintf("%s-%s", j.platform.Name, constants.JobServiceName)
}

func (j JobServiceHandler) GetServiceCmName() string {
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

func (j JobServiceHandler) IsServiceEnabledInSpec() bool {
	return isJobServiceEnabled(j.platform)
}

func (j JobServiceHandler) isServiceEnabledInStatus() bool {
	return j.platform != nil && j.platform.Status.ClusterPlatformRef != nil &&
		j.platform.Status.ClusterPlatformRef.Services != nil && j.platform.Status.ClusterPlatformRef.Services.JobServiceRef != nil &&
		!isServicesSet(j.platform)
}

func (j JobServiceHandler) IsServiceEnabled() bool {
	return j.IsServiceEnabledInSpec() || j.isServiceEnabledInStatus()
}

func (j JobServiceHandler) GetServiceBaseUrl() string {
	if j.IsServiceEnabledInSpec() {
		return j.GetLocalServiceBaseUrl()
	}
	if j.isServiceEnabledInStatus() {
		return j.platform.Status.ClusterPlatformRef.Services.JobServiceRef.Url
	}
	return ""
}

func (j JobServiceHandler) GetLocalServiceBaseUrl() string {
	return GenerateServiceURL(constants.JobServiceURLProtocol, j.platform.Namespace, j.GetServiceName())
}

func (j JobServiceHandler) GetEnvironmentVariables() []corev1.EnvVar {
	return []corev1.EnvVar{
		{
			Name:  "QUARKUS_HTTP_CORS",
			Value: "true",
		},
		{
			Name:  "QUARKUS_HTTP_CORS_ORIGINS",
			Value: "/.*/",
		},
	}
}

func (j JobServiceHandler) GetPodResourceRequirements() corev1.ResourceRequirements {
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

func (j JobServiceHandler) GetReplicaCount() int32 {
	return 1
}

func (j JobServiceHandler) MergeContainerSpec(containerSpec *corev1.Container) (*corev1.Container, error) {
	c := containerSpec.DeepCopy()
	err := mergo.Merge(c, j.platform.Spec.Services.JobService.PodTemplate.Container.ToContainer(), mergo.WithOverride)
	return c, err
}

// hasPostgreSQLConfigured returns true when either the SonataFlow Platform PostgreSQL CR's structure or the one in the Job service specification is not nil
func (j JobServiceHandler) hasPostgreSQLConfigured() bool {
	return j.IsServiceSetInSpec() &&
		((j.platform.Spec.Services.JobService.Persistence != nil && j.platform.Spec.Services.JobService.Persistence.PostgreSQL != nil) ||
			(j.platform.Spec.Persistence != nil && j.platform.Spec.Persistence.PostgreSQL != nil))
}

func (j JobServiceHandler) ConfigurePersistence(containerSpec *corev1.Container) *corev1.Container {

	if j.hasPostgreSQLConfigured() {
		c := containerSpec.DeepCopy()
		c.Image = j.GetServiceImageName(constants.PersistenceTypePostgreSQL)
		p := persistence.RetrieveConfiguration(j.platform.Spec.Services.JobService.Persistence, j.platform.Spec.Persistence, j.GetServiceName())
		c.Env = append(c.Env, persistence.ConfigurePostgreSQLEnv(p.PostgreSQL, j.GetServiceName(), j.platform.Namespace)...)
		// Specific to Job Service
		c.Env = append(c.Env, corev1.EnvVar{Name: "QUARKUS_FLYWAY_MIGRATE_AT_START", Value: "true"})
		return c
	}
	return containerSpec
}

func (j JobServiceHandler) MergePodSpec(podSpec corev1.PodSpec) (corev1.PodSpec, error) {
	c := podSpec.DeepCopy()
	err := mergo.Merge(c, j.platform.Spec.Services.JobService.PodTemplate.PodSpec.ToPodSpec(), mergo.WithOverride)
	return *c, err
}

func (j JobServiceHandler) GenerateServiceProperties() (*properties.Properties, error) {
	props := properties.NewProperties()
	props.Set(constants.KogitoServiceURLProperty, GenerateServiceURL(constants.KogitoServiceURLProtocol, j.platform.Namespace, j.GetServiceName()))
	props.Set(constants.JobServiceKafkaSmallRyeHealthProperty, "false")
	// add data source reactive URL
	if j.hasPostgreSQLConfigured() {
		p := persistence.RetrieveConfiguration(j.platform.Spec.Services.JobService.Persistence, j.platform.Spec.Persistence, j.GetServiceName())
		dataSourceReactiveURL, err := generateReactiveURL(p.PostgreSQL, j.GetServiceName(), j.platform.Namespace, constants.DefaultDatabaseName, constants.DefaultPostgreSQLPort)
		if err != nil {
			return nil, err
		}
		props.Set(constants.JobServiceDataSourceReactiveURL, dataSourceReactiveURL)
	}

	if isDataIndexEnabled(j.platform) {
		di := NewDataIndexHandler(j.platform)
		props.Set(constants.JobServiceStatusChangeEvents, "true")
		props.Set(constants.JobServiceStatusChangeEventsURL, di.GetLocalServiceBaseUrl()+"/jobs")
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

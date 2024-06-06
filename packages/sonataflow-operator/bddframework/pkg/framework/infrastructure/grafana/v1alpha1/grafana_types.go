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

package v1alpha1

import (
	v12 "github.com/openshift/api/route/v1"
	appsv1 "k8s.io/api/apps/v1"
	v1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/resource"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// StatusPhase ...
type StatusPhase string

var (
	// NoPhase ...
	NoPhase StatusPhase
	// PhaseReconciling ...
	PhaseReconciling StatusPhase = "reconciling"
	// PhaseFailing ...
	PhaseFailing StatusPhase = "failing"
)

// GrafanaSpec defines the desired state of Grafana
type GrafanaSpec struct {
	Config                     GrafanaConfig            `json:"config"`
	Containers                 []v1.Container           `json:"containers,omitempty"`
	DashboardLabelSelector     []*metav1.LabelSelector  `json:"dashboardLabelSelector,omitempty"`
	Ingress                    *GrafanaIngress          `json:"ingress,omitempty"`
	InitResources              *v1.ResourceRequirements `json:"initResources,omitempty"`
	Secrets                    []string                 `json:"secrets,omitempty"`
	ConfigMaps                 []string                 `json:"configMaps,omitempty"`
	Service                    *GrafanaService          `json:"service,omitempty"`
	Deployment                 *GrafanaDeployment       `json:"deployment,omitempty"`
	Resources                  *v1.ResourceRequirements `json:"resources,omitempty"`
	ServiceAccount             *GrafanaServiceAccount   `json:"serviceAccount,omitempty"`
	Client                     *GrafanaClient           `json:"client,omitempty"`
	DashboardNamespaceSelector *metav1.LabelSelector    `json:"dashboardNamespaceSelector,omitempty"`
	DataStorage                *GrafanaDataStorage      `json:"dataStorage,omitempty"`
	Jsonnet                    *JsonnetConfig           `json:"jsonnet,omitempty"`
	BaseImage                  string                   `json:"baseImage,omitempty"`
	InitImage                  string                   `json:"initImage,omitempty"`
	LivenessProbeSpec          *LivenessProbeSpec       `json:"livenessProbeSpec,omitempty"`
	ReadinessProbeSpec         *ReadinessProbeSpec      `json:"readinessProbeSpec,omitempty"`
}

// ReadinessProbeSpec ...
type ReadinessProbeSpec struct {
	InitialDelaySeconds int32 `json:"initialDelaySeconds,omitempty"`
	TimeOutSeconds      int32 `json:"timeoutSeconds,omitempty"`
	PeriodSeconds       int32 `json:"periodSeconds,omitempty"`
	SuccessThreshold    int32 `json:"successThreshold,omitempty"`
	FailureThreshold    int32 `json:"failureThreshold,omitempty"`
}

// LivenessProbeSpec ...
type LivenessProbeSpec struct {
	InitialDelaySeconds int32 `json:"initialDelaySeconds,omitempty"`
	TimeOutSeconds      int32 `json:"timeoutSeconds,omitempty"`
	PeriodSeconds       int32 `json:"periodSeconds,omitempty"`
	SuccessThreshold    int32 `json:"successThreshold,omitempty"`
	FailureThreshold    int32 `json:"failureThreshold,omitempty"`
}

// JsonnetConfig ...
type JsonnetConfig struct {
	LibraryLabelSelector *metav1.LabelSelector `json:"libraryLabelSelector,omitempty"`
}

// GrafanaClient API client settings
type GrafanaClient struct {
	TimeoutSeconds *int `json:"timeout,omitempty"`
	PreferService  bool `json:"preferService"`
}

// GrafanaService provides a means to configure the service
type GrafanaService struct {
	Name        string            `json:"name,omitempty"`
	Annotations map[string]string `json:"annotations,omitempty"`
	Labels      map[string]string `json:"labels,omitempty"`
	Type        v1.ServiceType    `json:"type,omitempty"`
	Ports       []v1.ServicePort  `json:"ports,omitempty"`
	ClusterIP   string            `json:"clusterIP,omitempty"`
}

// GrafanaDataStorage provides a means to configure the grafana data storage
type GrafanaDataStorage struct {
	Annotations map[string]string               `json:"annotations,omitempty"`
	Labels      map[string]string               `json:"labels,omitempty"`
	AccessModes []v1.PersistentVolumeAccessMode `json:"accessModes,omitempty"`
	Size        resource.Quantity               `json:"size,omitempty"`
	Class       string                          `json:"class,omitempty"`
}

// GrafanaServiceAccount ...
type GrafanaServiceAccount struct {
	Skip             *bool                     `json:"skip,omitempty"`
	Annotations      map[string]string         `json:"annotations,omitempty"`
	Labels           map[string]string         `json:"labels,omitempty"`
	ImagePullSecrets []v1.LocalObjectReference `json:"imagePullSecrets,omitempty"`
}

// GrafanaDeployment provides a means to configure the deployment
type GrafanaDeployment struct {
	Annotations                   map[string]string          `json:"annotations,omitempty"`
	Labels                        map[string]string          `json:"labels,omitempty"`
	Replicas                      *int32                     `json:"replicas,omitempty"`
	NodeSelector                  map[string]string          `json:"nodeSelector,omitempty"`
	Tolerations                   []v1.Toleration            `json:"tolerations,omitempty"`
	Affinity                      *v1.Affinity               `json:"affinity,omitempty"`
	SecurityContext               *v1.PodSecurityContext     `json:"securityContext,omitempty"`
	ContainerSecurityContext      *v1.SecurityContext        `json:"containerSecurityContext,omitempty"`
	TerminationGracePeriodSeconds *int64                     `json:"terminationGracePeriodSeconds,omitempty"`
	EnvFrom                       []v1.EnvFromSource         `json:"envFrom,omitempty"`
	Env                           []v1.EnvVar                `json:"env,omitempty"`
	SkipCreateAdminAccount        *bool                      `json:"skipCreateAdminAccount,omitempty"`
	PriorityClassName             string                     `json:"priorityClassName,omitempty"`
	HostNetwork                   *bool                      `json:"hostNetwork,omitempty"`
	ExtraVolumes                  []v1.Volume                `json:"extraVolumes,omitempty"`
	ExtraVolumeMounts             []v1.VolumeMount           `json:"extraVolumeMounts,omitempty"`
	Strategy                      *appsv1.DeploymentStrategy `json:"strategy,omitempty"`
	HTTPProxy                     *GrafanaHTTPProxy          `json:"httpProxy,omitempty"`
}

// GrafanaHTTPProxy provides a means to configure the Grafana deployment
// to use a HTTP(S) proxy when making requests and resolving plugins.
type GrafanaHTTPProxy struct {
	Enabled bool   `json:"enabled"`
	URL     string `json:"url,omitempty"`
}

// GrafanaIngress provides a means to configure the ingress created
type GrafanaIngress struct {
	Annotations      map[string]string      `json:"annotations,omitempty"`
	Hostname         string                 `json:"hostname,omitempty"`
	Labels           map[string]string      `json:"labels,omitempty"`
	Path             string                 `json:"path,omitempty"`
	Enabled          bool                   `json:"enabled,omitempty"`
	TLSEnabled       bool                   `json:"tlsEnabled,omitempty"`
	TLSSecretName    string                 `json:"tlsSecretName,omitempty"`
	TargetPort       string                 `json:"targetPort,omitempty"`
	Termination      v12.TLSTerminationType `json:"termination,omitempty"`
	IngressClassName string                 `json:"ingressClassName,omitempty"`
	PathType         string                 `json:"pathType,omitempty"`
}

// GrafanaConfig is the configuration for grafana
type GrafanaConfig struct {
	Paths                         *GrafanaConfigPaths                         `json:"paths,omitempty" ini:"paths,omitempty"`
	Server                        *GrafanaConfigServer                        `json:"server,omitempty" ini:"server,omitempty"`
	Database                      *GrafanaConfigDatabase                      `json:"database,omitempty" ini:"database,omitempty"`
	RemoteCache                   *GrafanaConfigRemoteCache                   `json:"remote_cache,omitempty" ini:"remote_cache,omitempty"`
	Security                      *GrafanaConfigSecurity                      `json:"security,omitempty" ini:"security,omitempty"`
	Users                         *GrafanaConfigUsers                         `json:"users,omitempty" ini:"users,omitempty"`
	Auth                          *GrafanaConfigAuth                          `json:"auth,omitempty" ini:"auth,omitempty"`
	AuthBasic                     *GrafanaConfigAuthBasic                     `json:"auth.basic,omitempty" ini:"auth.basic,omitempty"`
	AuthAnonymous                 *GrafanaConfigAuthAnonymous                 `json:"auth.anonymous,omitempty" ini:"auth.anonymous,omitempty"`
	AuthAzureAD                   *GrafanaConfigAuthAzureAD                   `json:"auth.azuread,omitempty" ini:"auth.azuread,omitempty"`
	AuthGoogle                    *GrafanaConfigAuthGoogle                    `json:"auth.google,omitempty" ini:"auth.google,omitempty"`
	AuthGithub                    *GrafanaConfigAuthGithub                    `json:"auth.github,omitempty" ini:"auth.github,omitempty"`
	AuthGitlab                    *GrafanaConfigAuthGitlab                    `json:"auth.gitlab,omitempty" ini:"auth.gitlab,omitempty"`
	AuthGenericOauth              *GrafanaConfigAuthGenericOauth              `json:"auth.generic_oauth,omitempty" ini:"auth.generic_oauth,omitempty"`
	AuthOkta                      *GrafanaConfigAuthOkta                      `json:"auth.okta,omitempty" ini:"auth.okta,omitempty"`
	AuthLdap                      *GrafanaConfigAuthLdap                      `json:"auth.ldap,omitempty" ini:"auth.ldap,omitempty"`
	AuthProxy                     *GrafanaConfigAuthProxy                     `json:"auth.proxy,omitempty" ini:"auth.proxy,omitempty"`
	AuthSaml                      *GrafanaConfigAuthSaml                      `json:"auth.saml,omitempty" ini:"auth.saml,omitempty"`
	DataProxy                     *GrafanaConfigDataProxy                     `json:"dataproxy,omitempty" ini:"dataproxy,omitempty"`
	Analytics                     *GrafanaConfigAnalytics                     `json:"analytics,omitempty" ini:"analytics,omitempty"`
	Dashboards                    *GrafanaConfigDashboards                    `json:"dashboards,omitempty" ini:"dashboards,omitempty"`
	SMTP                          *GrafanaConfigSMTP                          `json:"smtp,omitempty" ini:"smtp,omitempty"`
	Log                           *GrafanaConfigLog                           `json:"log,omitempty" ini:"log,omitempty"`
	LogConsole                    *GrafanaConfigLogConsole                    `json:"log.console,omitempty" ini:"log.console,omitempty"`
	LogFrontend                   *GrafanaConfigLogFrontend                   `json:"log.frontend,omitempty" ini:"log.frontend,omitempty"`
	Metrics                       *GrafanaConfigMetrics                       `json:"metrics,omitempty" ini:"metrics,omitempty"`
	MetricsGraphite               *GrafanaConfigMetricsGraphite               `json:"metrics.graphite,omitempty" ini:"metrics.graphite,omitempty"`
	Snapshots                     *GrafanaConfigSnapshots                     `json:"snapshots,omitempty" ini:"snapshots,omitempty"`
	ExternalImageStorage          *GrafanaConfigExternalImageStorage          `json:"external_image_storage,omitempty" ini:"external_image_storage,omitempty"`
	ExternalImageStorageS3        *GrafanaConfigExternalImageStorageS3        `json:"external_image_storage.s3,omitempty" ini:"external_image_storage.s3,omitempty"`
	ExternalImageStorageWebdav    *GrafanaConfigExternalImageStorageWebdav    `json:"external_image_storage.webdav,omitempty" ini:"external_image_storage.webdav,omitempty"`
	ExternalImageStorageGcs       *GrafanaConfigExternalImageStorageGcs       `json:"external_image_storage.gcs,omitempty" ini:"external_image_storage.gcs,omitempty"`
	ExternalImageStorageAzureBlob *GrafanaConfigExternalImageStorageAzureBlob `json:"external_image_storage.azure_blob,omitempty" ini:"external_image_storage.azure_blob,omitempty"`
	Alerting                      *GrafanaConfigAlerting                      `json:"alerting,omitempty" ini:"alerting,omitempty"`
	Panels                        *GrafanaConfigPanels                        `json:"panels,omitempty" ini:"panels,omitempty"`
	Plugins                       *GrafanaConfigPlugins                       `json:"plugins,omitempty" ini:"plugins,omitempty"`
	Rendering                     *GrafanaConfigRendering                     `json:"rendering,omitempty" ini:"rendering,omitempty"`
	FeatureToggles                *GrafanaConfigFeatureToggles                `json:"feature_toggles,omitempty" ini:"feature_toggles,omitempty"`
}

// GrafanaConfigPaths ...
type GrafanaConfigPaths struct {
	TempDataLifetime string `json:"temp_data_lifetime,omitempty" ini:"temp_data_lifetime,omitempty"`
}

// GrafanaConfigServer ...
type GrafanaConfigServer struct {
	HTTPAddr         string `json:"http_addr,omitempty" ini:"http_addr,omitempty"`
	HTTPPort         string `json:"http_port,omitempty" ini:"http_port,omitempty"`
	Protocol         string `json:"protocol,omitempty" ini:"protocol,omitempty"`
	Socket           string `json:"socket,omitempty" ini:"socket,omitempty"`
	Domain           string `json:"domain,omitempty" ini:"domain,omitempty"`
	EnforceDomain    *bool  `json:"enforce_domain,omitempty" ini:"enforce_domain"`
	RootURL          string `json:"root_url,omitempty" ini:"root_url,omitempty"`
	ServeFromSubPath *bool  `json:"serve_from_sub_path,omitempty" ini:"serve_from_sub_path"`
	StaticRootPath   string `json:"static_root_path,omitempty" ini:"static_root_path,omitempty"`
	EnableGzip       *bool  `json:"enable_gzip,omitempty" ini:"enable_gzip"`
	CertFile         string `json:"cert_file,omitempty" ini:"cert_file,omitempty"`
	CertKey          string `json:"cert_key,omitempty" ini:"cert_key,omitempty"`
	RouterLogging    *bool  `json:"router_logging,omitempty" ini:"router_logging"`
}

// GrafanaConfigDatabase ...
type GrafanaConfigDatabase struct {
	URL             string `json:"url,omitempty" ini:"url,omitempty"`
	Type            string `json:"type,omitempty" ini:"type,omitempty"`
	Path            string `json:"path,omitempty" ini:"path,omitempty"`
	Host            string `json:"host,omitempty" ini:"host,omitempty"`
	Name            string `json:"name,omitempty" ini:"name,omitempty"`
	User            string `json:"user,omitempty" ini:"user,omitempty"`
	Password        string `json:"password,omitempty" ini:"password,omitempty"`
	SslMode         string `json:"ssl_mode,omitempty" ini:"ssl_mode,omitempty"`
	CaCertPath      string `json:"ca_cert_path,omitempty" ini:"ca_cert_path,omitempty"`
	ClientKeyPath   string `json:"client_key_path,omitempty" ini:"client_key_path,omitempty"`
	ClientCertPath  string `json:"client_cert_path,omitempty" ini:"client_cert_path,omitempty"`
	ServerCertName  string `json:"server_cert_name,omitempty" ini:"server_cert_name,omitempty"`
	MaxIdleConn     *int   `json:"max_idle_conn,omitempty" ini:"max_idle_conn,omitempty"`
	MaxOpenConn     *int   `json:"max_open_conn,omitempty" ini:"max_open_conn,omitempty"`
	ConnMaxLifetime *int   `json:"conn_max_lifetime,omitempty" ini:"conn_max_lifetime,omitempty"`
	LogQueries      *bool  `json:"log_queries,omitempty" ini:"log_queries"`
	CacheMode       string `json:"cache_mode,omitempty" ini:"cache_mode,omitempty"`
}

// GrafanaConfigRemoteCache ...
type GrafanaConfigRemoteCache struct {
	Type    string `json:"type,omitempty" ini:"type,omitempty"`
	ConnStr string `json:"connstr,omitempty" ini:"connstr,omitempty"`
}

// GrafanaConfigSecurity ...
type GrafanaConfigSecurity struct {
	AdminUser                            string `json:"admin_user,omitempty" ini:"admin_user,omitempty"`
	AdminPassword                        string `json:"admin_password,omitempty" ini:"admin_password,omitempty"`
	LoginRememberDays                    *int   `json:"login_remember_days,omitempty" ini:"login_remember_days,omitempty"`
	SecretKey                            string `json:"secret_key,omitempty" ini:"secret_key,omitempty"`
	DisableGravatar                      *bool  `json:"disable_gravatar,omitempty" ini:"disable_gravatar"`
	DataSourceProxyWhitelist             string `json:"data_source_proxy_whitelist,omitempty" ini:"data_source_proxy_whitelist,omitempty"`
	CookieSecure                         *bool  `json:"cookie_secure,omitempty" ini:"cookie_secure"`
	CookieSamesite                       string `json:"cookie_samesite,omitempty" ini:"cookie_samesite,omitempty"`
	AllowEmbedding                       *bool  `json:"allow_embedding,omitempty" ini:"allow_embedding"`
	StrictTransportSecurity              *bool  `json:"strict_transport_security,omitempty" ini:"strict_transport_security"`
	StrictTransportSecurityMaxAgeSeconds *int   `json:"strict_transport_security_max_age_seconds,omitempty" ini:"strict_transport_security_max_age_seconds,omitempty"`
	StrictTransportSecurityPreload       *bool  `json:"strict_transport_security_preload,omitempty" ini:"strict_transport_security_preload"`
	StrictTransportSecuritySubdomains    *bool  `json:"strict_transport_security_subdomains,omitempty" ini:"strict_transport_security_subdomains"`
	XContentTypeOptions                  *bool  `json:"x_content_type_options,omitempty" ini:"x_content_type_options"`
	XXssProtection                       *bool  `json:"x_xss_protection,omitempty" ini:"x_xss_protection"`
}

// GrafanaConfigUsers ...
type GrafanaConfigUsers struct {
	AllowSignUp       *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	AllowOrgCreate    *bool  `json:"allow_org_create,omitempty" ini:"allow_org_create"`
	AutoAssignOrg     *bool  `json:"auto_assign_org,omitempty" ini:"auto_assign_org"`
	AutoAssignOrgID   string `json:"auto_assign_org_id,omitempty" ini:"auto_assign_org_id,omitempty"`
	AutoAssignOrgRole string `json:"auto_assign_org_role,omitempty" ini:"auto_assign_org_role,omitempty"`
	ViewersCanEdit    *bool  `json:"viewers_can_edit,omitempty" ini:"viewers_can_edit"`
	EditorsCanAdmin   *bool  `json:"editors_can_admin,omitempty" ini:"editors_can_admin"`
	LoginHint         string `json:"login_hint,omitempty" ini:"login_hint,omitempty"`
	PasswordHint      string `json:"password_hint,omitempty" ini:"password_hint,omitempty"`
	DefaultTheme      string `json:"default_theme,omitempty" ini:"default_theme,omitempty"`
}

// GrafanaConfigAuth ...
type GrafanaConfigAuth struct {
	LoginCookieName                      string `json:"login_cookie_name,omitempty" ini:"login_cookie_name,omitempty"`
	LoginMaximumInactiveLifetimeDays     *int   `json:"login_maximum_inactive_lifetime_days,omitempty" ini:"login_maximum_inactive_lifetime_days,omitempty"`
	LoginMaximumInactiveLifetimeDuration string `json:"login_maximum_inactive_lifetime_duration,omitempty" ini:"login_maximum_inactive_lifetime_duration,omitempty"`
	LoginMaximumLifetimeDays             *int   `json:"login_maximum_lifetime_days,omitempty" ini:"login_maximum_lifetime_days,omitempty"`
	LoginMaximumLifetimeDuration         string `json:"login_maximum_lifetime_duration,omitempty" ini:"login_maximum_lifetime_duration,omitempty"`
	TokenRotationIntervalMinutes         *int   `json:"token_rotation_interval_minutes,omitempty" ini:"token_rotation_interval_minutes,omitempty"`
	DisableLoginForm                     *bool  `json:"disable_login_form,omitempty" ini:"disable_login_form"`
	DisableSignoutMenu                   *bool  `json:"disable_signout_menu,omitempty" ini:"disable_signout_menu"`
	SigV4AuthEnabled                     *bool  `json:"sigv4_auth_enabled,omitempty" ini:"sigv4_auth_enabled"`
	SignoutRedirectURL                   string `json:"signout_redirect_url,omitempty" ini:"signout_redirect_url,omitempty"`
	OauthAutoLogin                       *bool  `json:"oauth_auto_login,omitempty" ini:"oauth_auto_login"`
}

// GrafanaConfigAuthBasic ...
type GrafanaConfigAuthBasic struct {
	Enabled *bool `json:"enabled,omitempty" ini:"enabled"`
}

// GrafanaConfigAuthAnonymous ...
type GrafanaConfigAuthAnonymous struct {
	Enabled *bool  `json:"enabled,omitempty" ini:"enabled"`
	OrgName string `json:"org_name,omitempty" ini:"org_name,omitempty"`
	OrgRole string `json:"org_role,omitempty" ini:"org_role,omitempty"`
}

// GrafanaConfigAuthSaml ...
type GrafanaConfigAuthSaml struct {
	Enabled                  *bool  `json:"enabled,omitempty" ini:"enabled"`
	SingleLogout             *bool  `json:"single_logout,omitempty" ini:"single_logout,omitempty"`
	AllowIdpInitiated        *bool  `json:"allow_idp_initiated,omitempty" ini:"allow_idp_initiated,omitempty"`
	CertificatePath          string `json:"certificate_path,omitempty" ini:"certificate_path"`
	KeyPath                  string `json:"private_key_path,omitempty" ini:"private_key_path"`
	SignatureAlgorithm       string `json:"signature_algorithm,omitempty" ini:"signature_algorithm,omitempty"`
	IdpURL                   string `json:"idp_metadata_url,omitempty" ini:"idp_metadata_url"`
	MaxIssueDelay            string `json:"max_issue_delay,omitempty" ini:"max_issue_delay,omitempty"`
	MetadataValidDuration    string `json:"metadata_valid_duration,omitempty" ini:"metadata_valid_duration,omitempty"`
	RelayState               string `json:"relay_state,omitempty" ini:"relay_state,omitempty"`
	AssertionAttributeName   string `json:"assertion_attribute_name,omitempty" ini:"assertion_attribute_name,omitempty"`
	AssertionAttributeLogin  string `json:"assertion_attribute_login,omitempty" ini:"assertion_attribute_login,omitempty"`
	AssertionAttributeEmail  string `json:"assertion_attribute_email,omitempty" ini:"assertion_attribute_email,omitempty"`
	AssertionAttributeGroups string `json:"assertion_attribute_groups,omitempty" ini:"assertion_attribute_groups,omitempty"`
	AssertionAttributeRole   string `json:"assertion_attribute_role,omitempty" ini:"assertion_attribute_role,omitempty"`
	AssertionAttributeOrg    string `json:"assertion_attribute_org,omitempty" ini:"assertion_attribute_org,omitempty"`
	AllowedOrganizations     string `json:"allowed_organizations,omitempty" ini:"allowed_organizations,omitempty"`
	OrgMapping               string `json:"org_mapping,omitempty" ini:"org_mapping,omitempty"`
	RoleValuesEditor         string `json:"role_values_editor,omitempty" ini:"role_values_editor,omitempty"`
	RoleValuesAdmin          string `json:"role_values_admin,omitempty" ini:"role_values_admin,omitempty"`
	RoleValuesGrafanaAdmin   string `json:"role_values_grafana_admin,omitempty" ini:"role_values_grafana_admin,omitempty"`
}

// GrafanaConfigAuthAzureAD ...
type GrafanaConfigAuthAzureAD struct {
	Enabled        *bool  `json:"enabled,omitempty" ini:"enabled"`
	AllowSignUp    *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ClientID       string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret   string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes         string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL        string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL       string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	AllowedDomains string `json:"allowed_domains,omitempty" ini:"allowed_domains,omitempty"`
	AllowedGroups  string `json:"allowed_groups,omitempty" ini:"allowed_groups,omitempty"`
}

// GrafanaConfigAuthGoogle ...
type GrafanaConfigAuthGoogle struct {
	Enabled        *bool  `json:"enabled,omitempty" ini:"enabled"`
	ClientID       string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret   string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes         string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL        string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL       string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	AllowedDomains string `json:"allowed_domains,omitempty" ini:"allowed_domains,omitempty"`
	AllowSignUp    *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
}

// GrafanaConfigAuthGithub ...
type GrafanaConfigAuthGithub struct {
	Enabled              *bool  `json:"enabled,omitempty" ini:"enabled"`
	AllowSignUp          *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ClientID             string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret         string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes               string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL              string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL             string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	APIURL               string `json:"api_url,omitempty" ini:"api_url,omitempty"`
	TeamIds              string `json:"team_ids,omitempty" ini:"team_ids,omitempty"`
	AllowedOrganizations string `json:"allowed_organizations,omitempty" ini:"allowed_organizations,omitempty"`
}

// GrafanaConfigAuthGitlab ...
type GrafanaConfigAuthGitlab struct {
	Enabled       *bool  `json:"enabled,omitempty" ini:"enabled"`
	AllowSignUp   *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ClientID      string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret  string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes        string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL       string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL      string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	APIURL        string `json:"api_url,omitempty" ini:"api_url,omitempty"`
	AllowedGroups string `json:"allowed_groups,omitempty" ini:"allowed_groups,omitempty"`
}

// GrafanaConfigAuthGenericOauth ...
type GrafanaConfigAuthGenericOauth struct {
	Enabled               *bool  `json:"enabled,omitempty" ini:"enabled"`
	AllowSignUp           *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ClientID              string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret          string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes                string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL               string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL              string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	APIURL                string `json:"api_url,omitempty" ini:"api_url,omitempty"`
	AllowedDomains        string `json:"allowed_domains,omitempty" ini:"allowed_domains,omitempty"`
	RoleAttributePath     string `json:"role_attribute_path,omitempty" ini:"role_attribute_path,omitempty"`
	RoleAttributeStrict   *bool  `json:"role_attribute_strict,omitempty" ini:"role_attribute_strict,omitempty"`
	EmailAttributePath    string `json:"email_attribute_path,omitempty" ini:"email_attribute_path,omitempty"`
	TLSSkipVerifyInsecure *bool  `json:"tls_skip_verify_insecure,omitempty" ini:"tls_skip_verify_insecure,omitempty"`
	TLSClientCert         string `json:"tls_client_cert,omitempty" ini:"tls_client_cert,omitempty"`
	TLSClientKey          string `json:"tls_client_key,omitempty" ini:"tls_client_key,omitempty"`
	TLSClientCa           string `json:"tls_client_ca,omitempty" ini:"tls_auth_ca,omitempty"`
}

// GrafanaConfigAuthOkta ...
type GrafanaConfigAuthOkta struct {
	Enabled             *bool  `json:"enabled,omitempty" ini:"enabled"`
	Name                string `json:"name,omitempty" ini:"name,omitempty"`
	AllowSignUp         *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ClientID            string `json:"client_id,omitempty" ini:"client_id,omitempty"`
	ClientSecret        string `json:"client_secret,omitempty" ini:"client_secret,omitempty"`
	Scopes              string `json:"scopes,omitempty" ini:"scopes,omitempty"`
	AuthURL             string `json:"auth_url,omitempty" ini:"auth_url,omitempty"`
	TokenURL            string `json:"token_url,omitempty" ini:"token_url,omitempty"`
	APIURL              string `json:"api_url,omitempty" ini:"api_url,omitempty"`
	AllowedDomains      string `json:"allowed_domains,omitempty" ini:"allowed_domains,omitempty"`
	AllowedGroups       string `json:"allowed_groups,omitempty" ini:"allowed_groups,omitempty"`
	RoleAttributePath   string `json:"role_attribute_path,omitempty" ini:"role_attribute_path,omitempty"`
	RoleAttributeStrict *bool  `json:"role_attribute_strict,omitempty" ini:"role_attribute_strict,omitempty"`
}

// GrafanaConfigAuthLdap ...
type GrafanaConfigAuthLdap struct {
	Enabled     *bool  `json:"enabled,omitempty" ini:"enabled"`
	AllowSignUp *bool  `json:"allow_sign_up,omitempty" ini:"allow_sign_up"`
	ConfigFile  string `json:"config_file,omitempty" ini:"config_file,omitempty"`
}

// GrafanaConfigAuthProxy ...
type GrafanaConfigAuthProxy struct {
	Enabled          *bool  `json:"enabled,omitempty" ini:"enabled"`
	HeaderName       string `json:"header_name,omitempty" ini:"header_name,omitempty"`
	HeaderProperty   string `json:"header_property,omitempty" ini:"header_property,omitempty"`
	AutoSignUp       *bool  `json:"auto_sign_up,omitempty" ini:"auto_sign_up"`
	LdapSyncTTL      string `json:"ldap_sync_ttl,omitempty" ini:"ldap_sync_ttl,omitempty"`
	Whitelist        string `json:"whitelist,omitempty" ini:"whitelist,omitempty"`
	Headers          string `json:"headers,omitempty" ini:"headers,omitempty"`
	EnableLoginToken *bool  `json:"enable_login_token,omitempty" ini:"enable_login_token"`
}

// GrafanaConfigDataProxy ...
type GrafanaConfigDataProxy struct {
	Logging        *bool `json:"logging,omitempty" ini:"logging"`
	Timeout        *int  `json:"timeout,omitempty" ini:"timeout,omitempty"`
	SendUserHeader *bool `json:"send_user_header,omitempty" ini:"send_user_header,omitempty"`
}

// GrafanaConfigAnalytics ...
type GrafanaConfigAnalytics struct {
	ReportingEnabled    *bool  `json:"reporting_enabled,omitempty" ini:"reporting_enabled"`
	GoogleAnalyticsUaID string `json:"google_analytics_ua_id,omitempty" ini:"google_analytics_ua_id,omitempty"`
	CheckForUpdates     *bool  `json:"check_for_updates,omitempty" ini:"check_for_updates"`
}

// GrafanaConfigDashboards ...
type GrafanaConfigDashboards struct {
	VersionsToKeep *int `json:"versions_to_keep,omitempty" ini:"versions_to_keep,omitempty"`
}

// GrafanaConfigSMTP ...
type GrafanaConfigSMTP struct {
	Enabled      *bool  `json:"enabled,omitempty" ini:"enabled"`
	Host         string `json:"host,omitempty" ini:"host,omitempty"`
	User         string `json:"user,omitempty" ini:"user,omitempty"`
	Password     string `json:"password,omitempty" ini:"password,omitempty"`
	CertFile     string `json:"cert_file,omitempty" ini:"cert_file,omitempty"`
	KeyFile      string `json:"key_file,omitempty" ini:"key_file,omitempty"`
	SkipVerify   *bool  `json:"skip_verify,omitempty" ini:"skip_verify"`
	FromAddress  string `json:"from_address,omitempty" ini:"from_address,omitempty"`
	FromName     string `json:"from_name,omitempty" ini:"from_name,omitempty"`
	EhloIdentity string `json:"ehlo_identity,omitempty" ini:"ehlo_identity,omitempty"`
}

// GrafanaConfigLog ...
type GrafanaConfigLog struct {
	Mode    string `json:"mode,omitempty" ini:"mode,omitempty"`
	Level   string `json:"level,omitempty" ini:"level,omitempty"`
	Filters string `json:"filters,omitempty" ini:"filters,omitempty"`
}

// GrafanaConfigLogFrontend ...
type GrafanaConfigLogFrontend struct {
	Enabled                           *bool  `json:"enabled,omitempty" ini:"enabled,omitempty"`
	SentryDsn                         string `json:"sentry_dsn,omitempty" ini:"sentry_dsn,omitempty"`
	CustomEndpoint                    string `json:"custom_endpoint,omitempty" ini:"custom_endpoint,omitempty"`
	SampleRate                        string `json:"sample_rate,omitempty" ini:"sample_rate,omitempty"`
	LogEndpointRequestsPerSecondLimit *int   `json:"log_endpoint_requests_per_second_limit,omitempty" ini:"log_endpoint_requests_per_second_limit,omitempty"`
	LogEndpointBurstLimit             *int   `json:"log_endpoint_burst_limit,omitempty" ini:"log_endpoint_burst_limit,omitempty"`
}

// GrafanaConfigLogConsole ...
type GrafanaConfigLogConsole struct {
	Level  string `json:"level,omitempty" ini:"level,omitempty"`
	Format string `json:"format,omitempty" ini:"format,omitempty"`
}

// GrafanaConfigMetrics ...
type GrafanaConfigMetrics struct {
	Enabled           *bool  `json:"enabled,omitempty" ini:"enabled"`
	BasicAuthUsername string `json:"basic_auth_username,omitempty" ini:"basic_auth_username,omitempty"`
	BasicAuthPassword string `json:"basic_auth_password,omitempty" ini:"basic_auth_password,omitempty"`
	IntervalSeconds   *int   `json:"interval_seconds,omitempty" ini:"interval_seconds,omitempty"`
}

// GrafanaConfigMetricsGraphite ...
type GrafanaConfigMetricsGraphite struct {
	Address string `json:"address,omitempty" ini:"address,omitempty"`
	Prefix  string `json:"prefix,omitempty" ini:"prefix,omitempty"`
}

// GrafanaConfigSnapshots ...
type GrafanaConfigSnapshots struct {
	ExternalEnabled       *bool  `json:"external_enabled,omitempty" ini:"external_enabled"`
	ExternalSnapshotURL   string `json:"external_snapshot_url,omitempty" ini:"external_snapshot_url,omitempty"`
	ExternalSnapshotName  string `json:"external_snapshot_name,omitempty" ini:"external_snapshot_name,omitempty"`
	SnapshotRemoveExpired *bool  `json:"snapshot_remove_expired,omitempty" ini:"snapshot_remove_expired"`
}

// GrafanaConfigExternalImageStorage ...
type GrafanaConfigExternalImageStorage struct {
	Provider string `json:"provider,omitempty" ini:"provider,omitempty"`
}

// GrafanaConfigExternalImageStorageS3 ...
type GrafanaConfigExternalImageStorageS3 struct {
	Bucket    string `json:"bucket,omitempty" ini:"bucket,omitempty"`
	Region    string `json:"region,omitempty" ini:"region,omitempty"`
	Path      string `json:"path,omitempty" ini:"path,omitempty"`
	BucketURL string `json:"bucket_url,omitempty" ini:"bucket_url,omitempty"`
	AccessKey string `json:"access_key,omitempty" ini:"access_key,omitempty"`
	SecretKey string `json:"secret_key,omitempty" ini:"secret_key,omitempty"`
}

// GrafanaConfigExternalImageStorageWebdav ...
type GrafanaConfigExternalImageStorageWebdav struct {
	URL       string `json:"url,omitempty" ini:"url,omitempty"`
	PublicURL string `json:"public_url,omitempty" ini:"public_url,omitempty"`
	Username  string `json:"username,omitempty" ini:"username,omitempty"`
	Password  string `json:"password,omitempty" ini:"password,omitempty"`
}

// GrafanaConfigExternalImageStorageGcs ...
type GrafanaConfigExternalImageStorageGcs struct {
	KeyFile string `json:"key_file,omitempty" ini:"key_file,omitempty"`
	Bucket  string `json:"bucket,omitempty" ini:"bucket,omitempty"`
	Path    string `json:"path,omitempty" ini:"path,omitempty"`
}

// GrafanaConfigExternalImageStorageAzureBlob ...
type GrafanaConfigExternalImageStorageAzureBlob struct {
	AccountName   string `json:"account_name,omitempty" ini:"account_name,omitempty"`
	AccountKey    string `json:"account_key,omitempty" ini:"account_key,omitempty"`
	ContainerName string `json:"container_name,omitempty" ini:"container_name,omitempty"`
}

// GrafanaConfigAlerting ...
type GrafanaConfigAlerting struct {
	Enabled                    *bool  `json:"enabled,omitempty" ini:"enabled"`
	ExecuteAlerts              *bool  `json:"execute_alerts,omitempty" ini:"execute_alerts"`
	ErrorOrTimeout             string `json:"error_or_timeout,omitempty" ini:"error_or_timeout,omitempty"`
	NodataOrNullvalues         string `json:"nodata_or_nullvalues,omitempty" ini:"nodata_or_nullvalues,omitempty"`
	ConcurrentRenderLimit      *int   `json:"concurrent_render_limit,omitempty" ini:"concurrent_render_limit,omitempty"`
	EvaluationTimeoutSeconds   *int   `json:"evaluation_timeout_seconds,omitempty" ini:"evaluation_timeout_seconds,omitempty"`
	NotificationTimeoutSeconds *int   `json:"notification_timeout_seconds,omitempty" ini:"notification_timeout_seconds,omitempty"`
	MaxAttempts                *int   `json:"max_attempts,omitempty" ini:"max_attempts,omitempty"`
}

// GrafanaConfigPanels ...
type GrafanaConfigPanels struct {
	DisableSanitizeHTML *bool `json:"disable_sanitize_html,omitempty" ini:"disable_sanitize_html"`
}

// GrafanaConfigPlugins ...
type GrafanaConfigPlugins struct {
	EnableAlpha *bool `json:"enable_alpha,omitempty" ini:"enable_alpha"`
}

// GrafanaConfigRendering ...
type GrafanaConfigRendering struct {
	ServerURL                    string `json:"server_url,omitempty" ini:"server_url"`
	CallbackURL                  string `json:"callback_url,omitempty" ini:"callback_url"`
	ConcurrentRenderRequestLimit *int   `json:"concurrent_render_request_limit,omitempty" ini:"concurrent_render_request_limit,omitempty"`
}

// GrafanaConfigFeatureToggles ...
type GrafanaConfigFeatureToggles struct {
	Enable string `json:"enable,omitempty" ini:"enable,omitempty"`
}

// GrafanaStatus defines the observed state of Grafana
type GrafanaStatus struct {
	Phase               StatusPhase            `json:"phase,omitempty"`
	PreviousServiceName string                 `json:"previousServiceName,omitempty"`
	Message             string                 `json:"message,omitempty"`
	InstalledDashboards []*GrafanaDashboardRef `json:"dashboards,omitempty"`
	InstalledPlugins    PluginList             `json:"installedPlugins,omitempty"`
	FailedPlugins       PluginList             `json:"failedPlugins,omitempty"`
}

// PluginList ...
type PluginList []GrafanaPlugin

// GrafanaPlugin contains information about a single plugin
// +k8s:openapi-gen=true
type GrafanaPlugin struct {
	// +kubebuilder:validation:Required
	Name string `json:"name"`
	// +kubebuilder:validation:Required
	Version string `json:"version"`
}

// Grafana is the Schema for the grafanas API
// +kubebuilder:object:root=true
// +kubebuilder:subresource:status
type Grafana struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   GrafanaSpec   `json:"spec,omitempty"`
	Status GrafanaStatus `json:"status,omitempty"`
}

// GrafanaList contains a list of Grafana
// +kubebuilder:object:root=true
type GrafanaList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Grafana `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Grafana{}, &GrafanaList{})
}

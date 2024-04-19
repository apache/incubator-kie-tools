/*
Copyright 2021.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package v1alpha1

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// EDIT THIS FILE!  THIS IS SCAFFOLDING FOR YOU TO OWN!
// NOTE: json tags are required.  Any new fields you add must have json tags for the fields to be serialized.

// GrafanaDataSourceSpec defines the desired state of GrafanaDataSource
type GrafanaDataSourceSpec struct {
	// INSERT ADDITIONAL SPEC FIELDS - desired state of cluster
	// Important: Run "make" to regenerate code after modifying this file

	Datasources []GrafanaDataSourceFields `json:"datasources"`
	Name        string                    `json:"name"`
}

// GrafanaDataSourceStatus defines the observed state of GrafanaDatasource
type GrafanaDataSourceStatus struct {
	Phase   StatusPhase `json:"phase"`
	Message string      `json:"message"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

// +kubebuilder:object:root=true
// +kubebuilder:subresource:status

// GrafanaDataSource is the Schema for the grafanadatasources API
type GrafanaDataSource struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   GrafanaDataSourceSpec   `json:"spec,omitempty"`
	Status GrafanaDataSourceStatus `json:"status,omitempty"`
}

// +k8s:deepcopy-gen:interfaces=k8s.io/apimachinery/pkg/runtime.Object

// GrafanaDataSourceList contains a list of GrafanaDataSource
// +kubebuilder:object:root=true
type GrafanaDataSourceList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []GrafanaDataSource `json:"items"`
}

// GrafanaDataSourceFields ...
type GrafanaDataSourceFields struct {
	Name              string                          `json:"name"`
	Type              string                          `json:"type"`
	UID               string                          `json:"uid,omitempty"`
	Access            string                          `json:"access"`
	OrgID             int                             `json:"orgId,omitempty"`
	URL               string                          `json:"url"`
	Password          string                          `json:"password,omitempty"`
	User              string                          `json:"user,omitempty"`
	Database          string                          `json:"database,omitempty"`
	BasicAuth         bool                            `json:"basicAuth,omitempty"`
	BasicAuthUser     string                          `json:"basicAuthUser,omitempty"`
	BasicAuthPassword string                          `json:"basicAuthPassword,omitempty"`
	WithCredentials   bool                            `json:"withCredentials,omitempty"`
	IsDefault         bool                            `json:"isDefault,omitempty"`
	JSONData          GrafanaDataSourceJSONData       `json:"jsonData,omitempty"`
	SecureJSONData    GrafanaDataSourceSecureJSONData `json:"secureJsonData,omitempty"`
	Version           int                             `json:"version,omitempty"`
	Editable          bool                            `json:"editable,omitempty"`
}

// GrafanaDataSourceJSONData The most common json options
// See https://grafana.com/docs/administration/provisioning/#datasources
type GrafanaDataSourceJSONData struct {
	OauthPassThru           bool   `json:"oauthPassThru,omitempty"`
	TLSAuth                 bool   `json:"tlsAuth,omitempty"`
	TLSAuthWithCACert       bool   `json:"tlsAuthWithCACert,omitempty"`
	TLSSkipVerify           bool   `json:"tlsSkipVerify,omitempty"`
	GraphiteVersion         string `json:"graphiteVersion,omitempty"`
	TimeInterval            string `json:"timeInterval,omitempty"`
	EsVersion               int    `json:"esVersion,omitempty"`
	TimeField               string `json:"timeField,omitempty"`
	Interval                string `json:"interval,omitempty"`
	LogMessageField         string `json:"logMessageField,omitempty"`
	LogLevelField           string `json:"logLevelField,omitempty"`
	AuthType                string `json:"authType,omitempty"`
	AssumeRoleArn           string `json:"assumeRoleArn,omitempty"`
	DefaultRegion           string `json:"defaultRegion,omitempty"`
	CustomMetricsNamespaces string `json:"customMetricsNamespaces,omitempty"`
	TsdbVersion             string `json:"tsdbVersion,omitempty"`
	TsdbResolution          string `json:"tsdbResolution,omitempty"`
	Sslmode                 string `json:"sslmode,omitempty"`
	Encrypt                 string `json:"encrypt,omitempty"`
	PostgresVersion         int    `json:"postgresVersion,omitempty"`
	Timescaledb             bool   `json:"timescaledb,omitempty"`
	MaxOpenConns            int    `json:"maxOpenConns,omitempty"`
	MaxIdleConns            int    `json:"maxIdleConns,omitempty"`
	ConnMaxLifetime         int    `json:"connMaxLifetime,omitempty"`
	//  Useful fields for clickhouse datasource
	//  See https://github.com/Vertamedia/clickhouse-grafana/tree/master/dist/README.md#configure-the-datasource-with-provisioning
	//  See https://github.com/Vertamedia/clickhouse-grafana/tree/master/src/datasource.ts#L44
	AddCorsHeader               bool   `json:"addCorsHeader,omitempty"`
	DefaultDatabase             string `json:"defaultDatabase,omitempty"`
	UsePOST                     bool   `json:"usePOST,omitempty"`
	UseYandexCloudAuthorization bool   `json:"useYandexCloudAuthorization,omitempty"`
	XHeaderUser                 string `json:"xHeaderUser,omitempty"`
	XHeaderKey                  string `json:"xHeaderKey,omitempty"`
	// Custom HTTP headers for datasources
	// See https://grafana.com/docs/grafana/latest/administration/provisioning/#datasources
	HTTPHeaderName1 string `json:"httpHeaderName1,omitempty"`
	HTTPHeaderName2 string `json:"httpHeaderName2,omitempty"`
	HTTPHeaderName3 string `json:"httpHeaderName3,omitempty"`
	HTTPHeaderName4 string `json:"httpHeaderName4,omitempty"`
	HTTPHeaderName5 string `json:"httpHeaderName5,omitempty"`
	HTTPHeaderName6 string `json:"httpHeaderName6,omitempty"`
	HTTPHeaderName7 string `json:"httpHeaderName7,omitempty"`
	HTTPHeaderName8 string `json:"httpHeaderName8,omitempty"`
	HTTPHeaderName9 string `json:"httpHeaderName9,omitempty"`
	// Fields for Stackdriver data sources
	TokenURI           string `json:"tokenUri,omitempty"`
	ClientEmail        string `json:"clientEmail,omitempty"`
	AuthenticationType string `json:"authenticationType,omitempty"`
	DefaultProject     string `json:"defaultProject,omitempty"`
	// Fields for Azure data sources
	AppInsightsAppID             string `json:"appInsightsAppId,omitempty"`
	AzureLogAnalyticsSameAs      string `json:"azureLogAnalyticsSameAs,omitempty"`
	ClientID                     string `json:"clientId,omitempty"`
	CloudName                    string `json:"cloudName,omitempty"`
	LogAnalyticsDefaultWorkspace string `json:"logAnalyticsDefaultWorkspace,omitempty"`
	LogAnalyticsClientID         string `json:"logAnalyticsClientId,omitempty"`
	LogAnalyticsSubscriptionID   string `json:"logAnalyticsSubscriptionId,omitempty"`
	LogAnalyticsTenantID         string `json:"logAnalyticsTenantId,omitempty"`
	SubscriptionID               string `json:"subscriptionId,omitempty"`
	TenantID                     string `json:"tenantId,omitempty"`
	// Fields for InfluxDB data sources
	HTTPMode      string `json:"httpMode,omitempty"`
	Version       string `json:"version,omitempty"`
	Organization  string `json:"organization,omitempty"`
	DefaultBucket string `json:"defaultBucket,omitempty"`
	// Fields for Loki data sources
	MaxLines      int                                  `json:"maxLines,omitempty"`
	DerivedFields []GrafanaDataSourceJSONDerivedFields `json:"derivedFields,omitempty"`
	// Fields for Prometheus data sources
	CustomQueryParameters string `json:"customQueryParameters,omitempty"`
	HTTPMethod            string `json:"httpMethod,omitempty"`
}

// GrafanaDataSourceJSONDerivedFields ...
type GrafanaDataSourceJSONDerivedFields struct {
	DatasourceUID string `json:"datasourceUid,omitempty"`
	MatcherRegex  string `json:"matcherRegex,omitempty"`
	Name          string `json:"name,omitempty"`
	URL           string `json:"url,omitempty"`
}

// GrafanaDataSourceSecureJSONData The most common secure json options
// See https://grafana.com/docs/administration/provisioning/#datasources
type GrafanaDataSourceSecureJSONData struct {
	TLSCaCert         string `json:"tlsCACert,omitempty"`
	TLSClientCert     string `json:"tlsClientCert,omitempty"`
	TLSClientKey      string `json:"tlsClientKey,omitempty"`
	Password          string `json:"password,omitempty"`
	BasicAuthPassword string `json:"basicAuthPassword,omitempty"`
	AccessKey         string `json:"accessKey,omitempty"`
	SecretKey         string `json:"secretKey,omitempty"`
	// Custom HTTP headers for datasources
	// See https://grafana.com/docs/grafana/latest/administration/provisioning/#datasources
	HTTPHeaderValue1 string `json:"httpHeaderValue1,omitempty"`
	HTTPHeaderValue2 string `json:"httpHeaderValue2,omitempty"`
	HTTPHeaderValue3 string `json:"httpHeaderValue3,omitempty"`
	HTTPHeaderValue4 string `json:"httpHeaderValue4,omitempty"`
	HTTPHeaderValue5 string `json:"httpHeaderValue5,omitempty"`
	HTTPHeaderValue6 string `json:"httpHeaderValue6,omitempty"`
	HTTPHeaderValue7 string `json:"httpHeaderValue7,omitempty"`
	HTTPHeaderValue8 string `json:"httpHeaderValue8,omitempty"`
	HTTPHeaderValue9 string `json:"httpHeaderValue9,omitempty"`
	// Fields for Stackdriver data sources
	PrivateKey string `json:"privateKey,omitempty"`
	// Fields for Azure data sources
	ClientSecret             string `json:"clientSecret,omitempty"`
	AppInsightsAPIKey        string `json:"appInsightsApiKey,omitempty"`
	LogAnalyticsClientSecret string `json:"logAnalyticsClientSecret,omitempty"`
	// Fields for InfluxDB data sources
	Token string `json:"token,omitempty"`
}

func init() {
	SchemeBuilder.Register(&GrafanaDataSource{}, &GrafanaDataSourceList{})
}

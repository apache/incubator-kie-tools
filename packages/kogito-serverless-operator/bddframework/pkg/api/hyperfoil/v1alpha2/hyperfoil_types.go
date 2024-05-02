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

package v1alpha2

import (
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// HyperfoilSpec Configures Hyperfoil Controller and related resources.
type HyperfoilSpec struct {
	// Controller image. If 'version' is defined, too, the tag is replaced (or appended). Defaults to 'quay.io/hyperfoil/hyperfoil'
	Image string `json:"image,omitempty"`
	// Tag for controller image. Defaults to version matching the operator version.
	Version string `json:"version,omitempty"`
	// Specification of the exposed route.
	Route RouteSpec `json:"route,omitempty"`
	// Authentication/authorization settings.
	Auth AuthSpec `json:"auth,omitempty"`
	// Name of the config map and optionally its entry (separated by '/': e.g myconfigmap/log4j2-superverbose.xml) storing Log4j2 configuration file. By default the Controller uses its embedded configuration.
	Log string `json:"log,omitempty"`
	// Deploy timeout for agents, in milliseconds.
	AgentDeployTimeout int `json:"agentDeployTimeout,omitempty"`
	// If this is set the controller does not start benchmark run right away after hitting
	// /benchmark/my-benchmark/start ; instead it responds with status 301 and header Location
	// set to concatenation of this string and 'BENCHMARK=my-benchmark&RUN_ID=xxxx'.
	// CLI interprets that response as a request to hit CI instance on this URL, assuming that
	// CI will trigger a new job that will eventually call /benchmark/my-benchmark/start?runId=xxxx
	// with header 'x-trigger-job'. This is useful if the the CI has to synchronize Hyperfoil
	// to other benchmarks that don't use this controller instance.
	TriggerURL string `json:"triggerUrl,omitempty"`
	// Names of config maps and optionally keys (separated by '/') holding hooks that run before the run starts.
	PreHooks []string `json:"preHooks,omitempty"`
	// Names of config maps and optionally keys (separated by '/') holding hooks that run after the run finishes.
	PostHooks []string `json:"postHooks,omitempty"`
	// Name of the PVC hyperfoil should mount for its workdir.
	PersistentVolumeClaim string `json:"persistentVolumeClaim,omitempty"`
	// List of secrets in this namespace; each entry from those secrets will be mapped
	// as environment variable, using the key as variable name.
	SecretEnvVars []string `json:"secretEnvVars,omitempty"`
}

// RouteSpec defines the route for external access.
type RouteSpec struct {
	// Host for the route leading to Controller REST endpoint. Example: hyperfoil.apps.cloud.example.com
	Host string `json:"host,omitempty"`
	// Either 'http' (for plain-text routes - not recommended), 'edge', 'reencrypt' or 'passthrough'
	Type string `json:"type,omitempty"`
	// Optional for edge and reencrypt routes, required for passthrough; Name of the secret hosting `tls.crt`, `tls.key` and optionally `ca.crt`
	TLS string `json:"tls,omitempty"`
}

// AuthSpec defines authentication/authorization settings.
type AuthSpec struct {
	// Optional; Name of secret used for basic authentication. Must contain key 'password'.
	Secret string `json:"secret,omitempty"`
}

// HyperfoilStatus defines the observed state of Hyperfoil
type HyperfoilStatus struct {
	// "One of: 'Ready', 'Pending' or 'Error'"
	Status string `json:"status,omitempty"`
	// RFC 3339 date and time of the last update.
	LastUpdate metav1.Time `json:"lastUpdate,omitempty"`
	// Human readable explanation for the status.
	Reason string `json:"reason,omitempty"`
}

//+kubebuilder:object:root=true
//+kubebuilder:resource:categories=all;hyperfoil,shortName=hf
//+kubebuilder:subresource:status
//+kubebuilder:printcolumn:name="Version",type=string,JSONPath=`.spec.version`
//+kubebuilder:printcolumn:name="Route",type=string,JSONPath=`.spec.route.host`
//+kubebuilder:printcolumn:name="PVC",type=string,JSONPath=`.spec.persistentVolumeClaim`
//+kubebuilder:printcolumn:name="Status",type=string,JSONPath=`.status.status`

// Hyperfoil is the Schema for the hyperfoils API
type Hyperfoil struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   HyperfoilSpec   `json:"spec,omitempty"`
	Status HyperfoilStatus `json:"status,omitempty"`
}

//+kubebuilder:object:root=true

// HyperfoilList contains a list of Hyperfoil
type HyperfoilList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Hyperfoil `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Hyperfoil{}, &HyperfoilList{})
}

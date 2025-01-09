// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package v1alpha08

import v1 "k8s.io/api/core/v1"

// PropertyPlatformSpec defines the struct for global managed properties in the SonataFlowPlatform.
// These properties are ignored in the SonataFlowClusterPlatform since a source of a property (PropertyVarSource) can only be local.
type PropertyPlatformSpec struct {
	// Properties that will be added to the SonataFlow managed configMaps in the current context.
	// +optional
	// +patchMergeKey=name
	// +patchStrategy=merge
	Flow []PropertyVar `json:"flow,omitempty" patchStrategy:"merge" patchMergeKey:"name"`
}

// PropertyVar is the entry for a property set derived from the Kubernetes API EnvVar.
// Note that the name doesn't have to match C_IDENTIFIER.
type PropertyVar struct {
	// The property name
	Name string `json:"name"`

	// Optional: no more than one of the following may be specified.

	// Defaults to "".
	// +optional
	Value string `json:"value,omitempty"`
	// Source for the property's value. Cannot be used if value is not empty.
	// +optional
	ValueFrom *PropertyVarSource `json:"valueFrom,omitempty"`
}

// PropertyVarSource is the definition of a property source derived from the Kubernetes API EnvVarSource.
type PropertyVarSource struct {
	// Selects a key of a ConfigMap.
	// +optional
	ConfigMapKeyRef *v1.ConfigMapKeySelector `json:"configMapKeyRef,omitempty"`
	// Selects a key of a secret in the flow's namespace
	// +optional
	SecretKeyRef *v1.SecretKeySelector `json:"secretKeyRef,omitempty"`
}

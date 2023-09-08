// Copyright 2021 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package v1

import "github.com/kiegroup/kogito-operator/apis"

// WebHookSecret Secret to use for a given webHook.
// +k8s:openapi-gen=true
type WebHookSecret struct {
	// WebHook type, either GitHub or Generic.
	// +kubebuilder:validation:Enum=GitHub;Generic
	Type api.WebHookType `json:"type,omitempty"`
	// Secret value for webHook
	Secret string `json:"secret,omitempty"`
}

// GetType ...
func (w WebHookSecret) GetType() api.WebHookType {
	return w.Type
}

// GetSecret ...
func (w WebHookSecret) GetSecret() string {
	return w.Secret
}

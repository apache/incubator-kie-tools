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

// GitSource Git coordinates to locate the source code to build.
// +k8s:openapi-gen=true
// +operator-sdk:csv:customresourcedefinitions:displayName="Kogito Git Source"
type GitSource struct {
	// Git URI for the s2i source.
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Git URI"
	URI string `json:"uri"`
	// Branch to use in the Git repository.
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Git Reference"
	Reference string `json:"reference,omitempty"`
	// Context/subdirectory where the code is located, relative to the repo root.
	// +operator-sdk:csv:customresourcedefinitions:type=spec,displayName="Git Context"
	ContextDir string `json:"contextDir,omitempty"`
}

// GetURI ...
func (g *GitSource) GetURI() string {
	return g.URI
}

// SetURI ...
func (g *GitSource) SetURI(uri string) {
	g.URI = uri
}

// GetReference ...
func (g *GitSource) GetReference() string {
	return g.Reference
}

// SetReference ...
func (g *GitSource) SetReference(reference string) {
	g.Reference = reference
}

// GetContextDir ...
func (g *GitSource) GetContextDir() string {
	return g.ContextDir
}

// SetContextDir ...
func (g *GitSource) SetContextDir(context string) {
	g.ContextDir = context
}

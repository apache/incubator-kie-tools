// Copyright 2023 Red Hat, Inc. and/or its affiliates
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package metadata

const (
	Domain                      = "sw.kogito.kie.org"
	Key                         = Domain + "/key"
	Name                        = Domain + "/name"
	Description                 = Domain + "/description"
	Annotations                 = Domain + "/annotations"
	DataInputSchema             = Domain + "/dataInputSchema"
	ExpressionLang              = Domain + "/expressionLang"
	Metadata                    = Domain + "/metadata"
	Version                     = Domain + "/version"
	Label                       = Domain + "/label"
	Profile                     = Domain + "/profile"
	SecondaryPlatformAnnotation = Domain + "/secondary.platform"
	OperatorIDAnnotation        = Domain + "/operator.id"
	// TODO: is this the right value?
	ServiceType = Domain + "/name"
)

const (
	// DefaultExpressionLang is the default serverless workflow specification language
	DefaultExpressionLang = "jq"
	// SpecVersion is the current CNCF Serverless Workflow version supported by the operator
	SpecVersion = "v0.8"
)

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

package metadata

const (
	Domain                      = "sonataflow.org"
	Key                         = Domain + "/key"
	Name                        = Domain + "/name"
	Description                 = Domain + "/description"
	ExpressionLang              = Domain + "/expressionLang"
	Version                     = Domain + "/version"
	Label                       = Domain + "/label"
	Profile                     = Domain + "/profile"
	SecondaryPlatformAnnotation = Domain + "/secondary.platform"
	OperatorIDAnnotation        = Domain + "/operator.id"
	RestartedAt                 = Domain + "/restartedAt"
	Checksum                    = Domain + "/checksum-config"
)

const (
	// DefaultExpressionLang is the default serverless workflow specification language
	DefaultExpressionLang = "jq"
	// SpecVersion is the current CNCF Serverless Workflow version supported by the operator
	SpecVersion = "0.8"
)

type ProfileType string

func (p ProfileType) String() string {
	return string(p)
}

const (
	DevProfile  ProfileType = "dev"
	ProdProfile ProfileType = "prod"
)

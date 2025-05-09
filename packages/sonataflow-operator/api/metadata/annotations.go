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
	KubernetesDomain            = "app.kubernetes.io"
	KubernetesLabelName         = KubernetesDomain + "/name"
	KubernetesLabelVersion      = KubernetesDomain + "/version"
	KubernetesLabelInstance     = KubernetesDomain + "/instance"
	KubernetesLabelPartOf       = KubernetesDomain + "/part-of"
	KubernetesLabelManagedBy    = KubernetesDomain + "/managed-by"
	KubernetesLabelComponent    = KubernetesDomain + "/component"
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

type QuarkusProfileType string

func (p QuarkusProfileType) String() string {
	return string(p)
}

const (
	// QuarkusDevProfile the profile used by quarkus in devmode
	QuarkusDevProfile QuarkusProfileType = "dev"
	// QuarkusProdProfile the profile used by quarkus in an immutable image
	QuarkusProdProfile QuarkusProfileType = "prod"
)

type ProfileType string

func (p ProfileType) String() string {
	return string(p)
}

const (
	// DevProfile deploys a mutable workflow that can be changed based on .spec.flow definitions CR change.
	DevProfile ProfileType = "dev"
	// Deprecated: use PreviewProfile.
	ProdProfile ProfileType = "prod"
	// PreviewProfile is the default profile if none is set.
	// The operator will use the platform to do a minimal image build for users to preview an immutable app deployed in the cluster.
	// Not suitable for production use cases since the managed build has configuration and resources limitations.
	PreviewProfile ProfileType = "preview"
	// GitOpsProfile signs the operator that the application image is built externally, skipping any internal managed build.
	// Ideally used in production use cases
	GitOpsProfile ProfileType = "gitops"
)

const (
	DefaultProfile = PreviewProfile
)

// deprecated prod profile is deprecate and not supported, use preview profile
var supportedProfiles = map[ProfileType]ProfileType{DevProfile: DevProfile, PreviewProfile: PreviewProfile, GitOpsProfile: GitOpsProfile}

func GetProfileOrDefault(annotation map[string]string) ProfileType {
	if annotation == nil {
		return DefaultProfile
	}
	if profile, ok := supportedProfiles[ProfileType(annotation[Profile])]; !ok {
		return DefaultProfile
	} else {
		return profile
	}
}

func (p ProfileType) isValidProfile() bool {
	_, ok := supportedProfiles[p]
	return ok
}

func IsDevProfile(annotation map[string]string) bool {
	if annotation == nil {
		return false
	}
	if len(annotation[Profile]) == 0 {
		return false
	}
	return ProfileType(annotation[Profile]) == DevProfile
}

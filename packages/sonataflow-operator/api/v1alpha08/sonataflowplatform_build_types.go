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

package v1alpha08

import (
	"strconv"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

// Describes the general build specification for this platform. Specific for build scenarios.
type BuildPlatformSpec struct {
	// Describes a build template for building workflows. Base for the internal SonataFlowBuild resource.
	Template BuildTemplate `json:"template,omitempty"`
	// Describes the platform configuration for building workflows.
	Config BuildPlatformConfig `json:"config,omitempty"`
}

// Describes the configuration for building in the given platform
type BuildPlatformConfig struct {
	// a base image that can be used as base layer for all images.
	// It can be useful if you want to provide some custom base image with further utility software
	BaseImage string `json:"baseImage,omitempty"`
	// how much time to wait before time out the build process
	Timeout *metav1.Duration `json:"timeout,omitempty"`
	// BuildStrategy to use to build workflows in the platform.
	// Usually, the operator elect the strategy based on the platform.
	// Note that this field might be read only in certain scenarios.
	BuildStrategy BuildStrategy `json:"strategy,omitempty"`
	// BuildStrategyOptions additional options to add to the build strategy.
	// See https://sonataflow.org/serverlessworkflow/main/cloud/operator/build-and-deploy-workflows.html
	BuildStrategyOptions map[string]string `json:"strategyOptions,omitempty"`
	// Registry the registry where to publish the built image
	Registry RegistrySpec `json:"registry,omitempty"`
}

// GetTimeout returns the specified duration or a default one
func (b *BuildPlatformConfig) GetTimeout() metav1.Duration {
	if b.Timeout == nil {
		return metav1.Duration{}
	}
	return *b.Timeout
}

// IsStrategyOptionEnabled return whether the BuildStrategyOptions is enabled or not
func (b *BuildPlatformConfig) IsStrategyOptionEnabled(option string) bool {
	if enabled, ok := b.BuildStrategyOptions[option]; ok {
		res, err := strconv.ParseBool(enabled)
		if err != nil {
			return false
		}
		return res
	}
	return false
}

func (b *BuildPlatformConfig) IsStrategyOptionEmpty(option string) bool {
	if v, ok := b.BuildStrategyOptions[option]; ok {
		return len(v) == 0
	}
	return false
}

// RegistrySpec provides the configuration for the container registry
type RegistrySpec struct {
	// if the container registry is insecure (ie, http only)
	Insecure bool `json:"insecure,omitempty"`
	// the URI to access
	Address string `json:"address,omitempty"`
	// the secret where credentials are stored
	Secret string `json:"secret,omitempty"`
	// the configmap which stores the Certificate Authority
	CA string `json:"ca,omitempty"`
	// the registry organization
	Organization string `json:"organization,omitempty"`
}

type BuildStrategy string

const (
	// OperatorBuildStrategy uses the operator builder to perform the workflow build
	// E.g. on Minikube or Kubernetes the container-builder strategies
	OperatorBuildStrategy BuildStrategy = "operator"
	// PlatformBuildStrategy uses the cluster to perform the build.
	// E.g. on OpenShift, BuildConfig.
	PlatformBuildStrategy BuildStrategy = "platform"

	// In the future we can have "custom" which will delegate the build to an external actor provided by the administrator
	// See https://issues.redhat.com/browse/KOGITO-9084
)

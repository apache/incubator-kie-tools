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

package api

import (
	"strconv"

	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
)

type PlatformContainerBuild struct {
	ObjectReference `json:"meta,omitempty"`
	Spec            PlatformContainerBuildSpec `json:"spec,omitempty"`
}

type PlatformContainerBuildSpec struct {
	// the strategy to adopt for building a base image
	BuildStrategy ContainerBuildStrategy `json:"buildStrategy,omitempty"`
	// the strategy to adopt for publishing a base image
	PublishStrategy PlatformContainerBuildPublishStrategy `json:"publishStrategy,omitempty"`
	// a base image that can be used as base layer for all images.
	// It can be useful if you want to provide some custom base image with further utility software
	BaseImage string `json:"baseImage,omitempty"`
	// the image registry used to push/pull built images
	Registry ContainerRegistrySpec `json:"registry,omitempty"`
	// how much time to wait before time out the build process
	Timeout *metav1.Duration `json:"timeout,omitempty"`
	//
	BuildStrategyOptions map[string]string `json:"BuildStrategyOptions,omitempty"`
}

// PlatformContainerBuildPublishStrategy defines the strategy used to package and publish an Integration base image
type PlatformContainerBuildPublishStrategy string

const (
	// PlatformBuildPublishStrategyKaniko uses Kaniko project (https://github.com/GoogleContainerTools/kaniko)
	// in order to push the incremental images to the image repository. It can be used with `pod` ContainerBuildStrategy.
	PlatformBuildPublishStrategyKaniko PlatformContainerBuildPublishStrategy = "Kaniko"
)

// IsOptionEnabled return whether if the BuildStrategyOptions is enabled or not
func (b *PlatformContainerBuildSpec) IsOptionEnabled(option string) bool {
	//Key defined in builder/kaniko.go
	if enabled, ok := b.BuildStrategyOptions[option]; ok {
		res, err := strconv.ParseBool(enabled)
		if err != nil {
			return false
		}
		return res
	}
	return false
}

// GetTimeout returns the specified duration or a default one
func (b *PlatformContainerBuildSpec) GetTimeout() metav1.Duration {
	if b.Timeout == nil {
		return metav1.Duration{}
	}
	return *b.Timeout
}

/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package api

import metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

type PlatformBuild struct {
	ObjectReference `json:"meta,omitempty"`
	Spec            PlatformBuildSpec `json:"spec,omitempty"`
}

type PlatformBuildSpec struct {
	// the strategy to adopt for building an Integration base image
	BuildStrategy BuildStrategy `json:"buildStrategy,omitempty"`
	// the strategy to adopt for publishing an Integration base image
	PublishStrategy PlatformBuildPublishStrategy `json:"publishStrategy,omitempty"`
	// a base image that can be used as base layer for all images.
	// It can be useful if you want to provide some custom base image with further utility software
	BaseImage string `json:"baseImage,omitempty"`
	// the image registry used to push/pull built images
	Registry RegistrySpec `json:"registry,omitempty"`
	// how much time to wait before time out the build process
	Timeout *metav1.Duration `json:"timeout,omitempty"`
	//
	PublishStrategyOptions map[string]string `json:"PublishStrategyOptions,omitempty"`
}

// PlatformBuildPublishStrategy defines the strategy used to package and publish an Integration base image
type PlatformBuildPublishStrategy string

const (
	// PlatformBuildPublishStrategyKaniko uses Kaniko project (https://github.com/GoogleContainerTools/kaniko)
	// in order to push the incremental images to the image repository. It can be used with `pod` BuildStrategy.
	PlatformBuildPublishStrategyKaniko PlatformBuildPublishStrategy = "Kaniko"
)

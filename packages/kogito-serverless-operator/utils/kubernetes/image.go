// Copyright 2024 Apache Software Foundation (ASF)
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

package kubernetes

import (
	"strings"

	corev1 "k8s.io/api/core/v1"
)

// GetImagePullPolicy gets the default corev1.PullPolicy depending on the image tag specified.
// It follows the conventions of docker client and OpenShift. If no tag specified, it assumes latest.
// Returns PullAlways if latest tag, empty otherwise to let the cluster figure it out.
// See: https://kubernetes.io/docs/concepts/containers/images/#updating-images
func GetImagePullPolicy(imageTag string) corev1.PullPolicy {
	if len(imageTag) == 0 {
		return ""
	}
	idx := strings.LastIndex(imageTag, ":")
	if idx < 0 {
		return corev1.PullAlways
	}
	if GetImageTag(imageTag) == "latest" {
		return corev1.PullAlways
	}
	return ""
}

// GetImageTag gets the tag after `:` in an image tag or empty if not found.
func GetImageTag(imageTag string) string {
	if len(imageTag) == 0 {
		return ""
	}
	idx := strings.LastIndex(imageTag, ":")
	if idx < 0 {
		return ""
	}
	return imageTag[idx+1:]
}

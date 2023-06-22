// Copyright 2023 Red Hat, Inc. and/or its affiliates
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

package workflowdef

import (
	"github.com/kiegroup/kogito-serverless-operator/api/metadata"
	"github.com/kiegroup/kogito-serverless-operator/api/v1alpha08"
	"github.com/kiegroup/kogito-serverless-operator/version"
)

const (
	latestImageTag              = "latest"
	nightlySuffix               = "nightly"
	DefaultWorkflowDevModeImage = "quay.io/kiegroup/kogito-swf-devmode"
	DefaultWorkflowBuilderImage = "quay.io/kiegroup/kogito-swf-builder"
)

// GetWorkflowAppImageNameTag retrieve the tag for the image based on the Workflow based annotation, <workflowid>:latest otherwise
func GetWorkflowAppImageNameTag(w *v1alpha08.SonataFlow) string {
	v := w.Annotations[metadata.Version]
	if v != "" {
		return w.Name + ":" + v
	}
	return w.Name + ":" + latestImageTag
}

func GetDefaultWorkflowDevModeImageTag() string {
	return GetDefaultImageTag(DefaultWorkflowDevModeImage)
}

func GetDefaultWorkflowBuilderImageTag() string {
	return GetDefaultImageTag(DefaultWorkflowBuilderImage)
}

func GetDefaultImageTag(imgTag string) string {
	if version.IsSnapshot() {
		imgTag += "-" + nightlySuffix
	}
	imgTag += ":"
	if version.IsLatestVersion() {
		imgTag += latestImageTag
	} else {
		imgTag += version.GetMajorMinor()
	}
	return imgTag
}

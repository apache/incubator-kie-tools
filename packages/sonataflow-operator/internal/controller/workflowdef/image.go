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

package workflowdef

import (
	"fmt"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/version"

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/api/v1alpha08"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/internal/controller/cfg"
	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/utils"
)

const (
	defaultWorkflowDevModeImage = "docker.io/apache/incubator-kie-sonataflow-devmode"
	defaultWorkflowBuilderImage = "docker.io/apache/incubator-kie-sonataflow-builder"
)

// GetWorkflowAppImageNameTag returns the image name with tag to use for the image to be produced for a given workflow.
// Before, we generated the tags based on the workflow version annotation, however this produced the following undesired
// effects. Empirically, it was detected that, if we deploy a workflow several times, for instance, the workflow is deleted
// for a modification, and then deployed again. When the build cycle is produced, etc., if the workflow version
// remains the same, e.g. 1.0.0, the bits for the new image are not written in the respective registry (because an image
// with the given tag already exists), and thus, when the workflow executes the old bits are executed.
// To avoid this, the workflow version must be changed, for example to 2.0.0, and thus the subsequent image will have
// a different tag, and the expected bits will be stored at the registry and finally executed.
// This workflow version bump must be produced by the users, but we don't have control over this.
// So by now, considering that the operator images build is oriented to "dev" and "preview" scenarios, and
// not for "production" scenarios, we decided to use "latest" as the tag. In that way, we ensure that the last image
// produced bits will be used to execute a given workflow.
func GetWorkflowAppImageNameTag(w *v1alpha08.SonataFlow) string {
	return w.Name + ":" + utils.LatestImageTag
}

func GetDefaultWorkflowDevModeImageTag() string {
	if len(cfg.GetCfg().SonataFlowDevModeImageTag) > 0 {
		return cfg.GetCfg().SonataFlowDevModeImageTag
	}
	return GetDefaultImageTag(defaultWorkflowDevModeImage)
}

func GetDefaultWorkflowBuilderImageTag() string {
	if len(cfg.GetCfg().SonataFlowBaseBuilderImageTag) > 0 {
		return cfg.GetCfg().SonataFlowBaseBuilderImageTag
	}
	return GetDefaultImageTag(defaultWorkflowBuilderImage)
}

func GetDefaultImageTag(imgTag string) string {
	return fmt.Sprintf("%s:%s", imgTag, version.GetImageTagVersion())
}

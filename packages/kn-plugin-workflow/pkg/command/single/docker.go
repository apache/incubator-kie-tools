/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package single

import (
	"fmt"

	"github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/common"
)

func getDockerBuildArgs(cfg BuildCmdConfig, imageRegistry string, imageRepository string, imageName string, imageTag string) []string {
	dockerBuildArgs := []string{
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_WORKFLOW_FILE, common.WORKFLOW_SW_JSON),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_EXTENSIONS, cfg.Extesions),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_WORKFLOW_NAME, cfg.ImageName),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_REGISTRY, imageRegistry),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_GROUP, imageRepository),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_NAME, imageName),
		fmt.Sprintf("--build-arg %s=%s", common.DOCKER_BUILD_ARG_CONTAINER_IMAGE_TAG, imageTag),
	}

	return dockerBuildArgs
}

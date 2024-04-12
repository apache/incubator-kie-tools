# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#  http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

.DEFAULT_GOAL 				:= build

# Image build envs
CEKIT_BUILD_OPTIONS 		?=
CEKIT_CMD 					:= cekit -v ${CEKIT_BUILD_OPTIONS}
BUILD_ENGINE 				?= docker
BUILD_ENGINE_TLS_OPTIONS 	?=

# Resolving the current image version from the kogito.project.versions module. Cekit will use it to tag the image.
CURRENT_IMAGE_VERSION 		:= $(shell python scripts/retrieve_version.py)

# SWF Image creation envs
SWF_IMAGE_NAME				?= # Image name
SWF_IMAGE_REGISTRY 			?= 'quay.io'
SWF_IMAGE_REGISTRY_ACCOUNT 	?= 'kie-tools'
SWF_IMAGE_TAG 				?= $(CURRENT_IMAGE_VERSION) # Setting a default value if SWF_IMAGE_TAG env is not present
SWF_FULL_IMAGE_NAME 		:= $(SWF_IMAGE_REGISTRY)/$(SWF_IMAGE_REGISTRY_ACCOUNT)/$(SWF_IMAGE_NAME)
SWF_IMAGE_FILENAME			:= ${SWF_IMAGE_NAME}-image.yaml

# Check if there are Quarkus and Kogito version envs
_check_versions:
ifdef QUARKUS_PLATFORM_VERSION
 VERSIONS_MANAGER_ARGS := --quarkus-version ${QUARKUS_PLATFORM_VERSION}
endif
ifdef KOGITO_VERSION
 VERSIONS_MANAGER_ARGS :=  ${VERSIONS_MANAGER_ARGS} --kogito-version ${KOGITO_VERSION}
endif

# Upgrade Quarkus & Kogito versions in the images and modules
_run_version_manager:
ifdef VERSIONS_MANAGER_ARGS
	python scripts/versions_manager.py ${VERSIONS_MANAGER_ARGS}
endif

fix_platform_versions: _check_versions _run_version_manager

# Building the SWF image with Cekit
_cekit_build:
	${CEKIT_CMD} --descriptor ${SWF_IMAGE_FILENAME} build ${CEKIT_BUILD_OPTIONS} ${BUILD_ENGINE}

# Tagging the generated image if SWF_IMAGE_TAG doesn't match the CURRENT_IMAGE_VERSION
_tag_image:
 ifneq ($(SWF_IMAGE_TAG), $(CURRENT_IMAGE_VERSION))
	${BUILD_ENGINE} tag ${SWF_FULL_IMAGE_NAME}:${CURRENT_IMAGE_VERSION} ${SWF_FULL_IMAGE_NAME}:${SWF_IMAGE_TAG}
 endif

.PHONY build: fix_platform_versions _cekit_build _tag_image
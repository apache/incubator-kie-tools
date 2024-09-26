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
CEKIT_CMD 					:= cekit ${CEKIT_BUILD_OPTIONS}
BUILD_ENGINE 				?= docker
BUILD_ENGINE_OPTIONS 	 	?=

# SWF Image creation envs
KOGITO_IMAGE_NAME				?= # Image name
KOGITO_IMAGE_REGISTRY 			?= 'docker.io'
KOGITO_IMAGE_REGISTRY_ACCOUNT 	?= 'apache'
KOGITO_IMAGE_TAG 				?= $(shell pnpm build-env root.streamName) # Setting a default value if KOGITO_IMAGE_TAG env is not present
KOGITO_FULL_IMAGE_NAME 			:= $(KOGITO_IMAGE_REGISTRY)/$(KOGITO_IMAGE_REGISTRY_ACCOUNT)/$(KOGITO_IMAGE_NAME)
KOGITO_IMAGE_FILENAME			:= ${KOGITO_IMAGE_NAME}-image.yaml

# Services Applications Image Build
KOGITO_APPS_TARGET_URI 		?= 'https://github.com/apache/incubator-kie-kogito-apps.git'

_check_kogito_image_name:
ifndef KOGITO_IMAGE_NAME
	$(error Cannot build image, please provide a valid image name using the KOGITO_IMAGE_NAME env)
endif

# Check if there are Quarkus and Kogito version envs
_check_versions:
ifndef QUARKUS_PLATFORM_VERSION
	$(error Cannot build image, please provide a valid Quarkus version using the QUARKUS_PLATFORM_VERSION env)
endif
ifndef KOGITO_VERSION
	$(error Cannot build image, please provide a valid Kogito version using the KOGITO_VERSION env)
endif

# Upgrade Quarkus & Kogito versions in the images and modules
_run_version_manager:
ifneq ($(SONATAFLOW_QUARKUS_DEVUI_VERSION),)
	python3 scripts/versions_manager.py --quarkus-version ${QUARKUS_PLATFORM_VERSION} --kogito-version ${KOGITO_VERSION} --sonataflow-quarkus-devui-version ${SONATAFLOW_QUARKUS_DEVUI_VERSION}
else
	python3 scripts/versions_manager.py --quarkus-version ${QUARKUS_PLATFORM_VERSION} --kogito-version ${KOGITO_VERSION}
endif

_fix_platform_versions: _check_versions _run_version_manager

# Building the SWF image with Cekit
_cekit_build:
	${CEKIT_CMD} --descriptor ${KOGITO_IMAGE_FILENAME} build ${CEKIT_BUILD_OPTIONS} ${BUILD_ENGINE} ${BUILD_ENGINE_OPTIONS} --tag ${KOGITO_FULL_IMAGE_NAME}:${KOGITO_IMAGE_TAG}

_create_e2e_dir:
	rm -rf ../dist-tests-e2e
	mkdir ../dist-tests-e2e

# Pull kogito-apps repo and build the target kogito-apps depending on the `KOGITO_IMAGE_NAME`s
# Required for kogito-apps images only
.PHONY build-kogito-app: _build_kogito_app
_build_kogito_app:
	scripts/build-kogito-apps-components.sh ${KOGITO_IMAGE_NAME} ${KOGITO_VERSION} ${KOGITO_APPS_TARGET_URI};

# Trigger the image tests
.PHONY test-image: _create_e2e_dir _check_kogito_image_name _test_image
_test_image:
	tests/shell/run.sh ${KOGITO_IMAGE_NAME} ${KOGITO_FULL_IMAGE_NAME}:${KOGITO_IMAGE_TAG}
	${CEKIT_CMD} --descriptor ${KOGITO_IMAGE_FILENAME} test behave

.PHONY build: _check_kogito_image_name _fix_platform_versions _cekit_build

# run bat tests locally
bats:
	@./scripts/run-bats.sh

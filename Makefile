#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#

IMAGE_VERSION := $(shell python scripts/retrieve_version.py)
SHORTENED_LATEST_VERSION := $(shell echo $(IMAGE_VERSION) | awk -F. '{print $$1"."$$2}')
KOGITO_APPS_TARGET_BRANCH ?= main
KOGITO_APPS_TARGET_URI ?= https://github.com/apache/incubator-kie-kogito-apps.git
BUILD_ENGINE ?= docker
BUILD_ENGINE_TLS_OPTIONS ?= ''
.DEFAULT_GOAL := build
CEKIT_CMD := cekit -v ${cekit_option}
NATIVE := true

clone-repos:
# if the ignore_test env is not defined or false, proceed with the tests, as first step prepare the examples to be used
ifneq ($(ignore_test),true)
ifneq ($(ignore_test_prepare),true)
	cd tests/test-apps && export CONTAINER_ENGINE=$(BUILD_ENGINE) && bash clone-repo.sh $(NATIVE) $(image_name)
	cd ../..
endif
endif

.PHONY: list
list:
	@python scripts/list-images.py $(arg)

.PHONY: display-image-version
display-image-version:
	@echo $(IMAGE_VERSION)

# Build all images
.PHONY: build
# start to build the images
build: clone-repos _build

_build:
	@for f in $(shell make list); do make build-image image_name=$${f}; done


.PHONY: build-image
image_name=
build-image: clone-repos _build-image

_build-image:
ifneq ($(ignore_build),true)
	scripts/build-kogito-apps-components.sh ${image_name} ${KOGITO_APPS_TARGET_BRANCH} ${KOGITO_APPS_TARGET_URI};
	${CEKIT_CMD} --descriptor ${image_name}-image.yaml build ${build_options} ${BUILD_ENGINE}
endif
# tag with shortened version
ifneq ($(ignore_tag),true)
    ifneq ($(findstring rc,$(IMAGE_VERSION)),rc)
	    ${BUILD_ENGINE} tag docker.io/apache/incubator-kie-${image_name}:${IMAGE_VERSION} docker.io/apache/incubator-kie-${image_name}:${SHORTENED_LATEST_VERSION}
    endif
endif
# if ignore_test is set to true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} --descriptor ${image_name}-image.yaml test behave ${test_options}
	tests/shell/run.sh ${image_name} "docker.io/apache/incubator-kie-${image_name}:${SHORTENED_LATEST_VERSION}"
endif


# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push

_push:
	@for f in $(shell make list); do make push-image image_name=$${f}; done

.PHONY: push-image
image_name=
push-image:
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push docker.io/apache/incubator-kie-${image_name}:${IMAGE_VERSION}
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push docker.io/apache/incubator-kie-${image_name}:latest
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	@echo "${SHORTENED_LATEST_VERSION} will be pushed"
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push docker.io/apache/incubator-kie-${image_name}:${SHORTENED_LATEST_VERSION}
endif


# push staging images to quay.io, done before release, this requires permissions under kiegroup organization
# to force updating an existing tag instead create a new one, use `$ make push-staging override=-o`
.PHONY: push-staging
push-staging: build _push-staging
_push-staging:
	python scripts/push-staging.py ${override}


# push to local registry, useful to push the built images to local registry
# requires parameter: REGISTRY: my-custom-registry:[port]
# requires pre built images, if no images, run make build first
# the shortened version will be used so operator can fetch it from the local namespace.
# use the NS env to set the current namespace, if no set openshift will be used
# example:  make push-local-registry REGISTRY=docker-registry-default.apps.spolti.cloud NS=spolti-1
.PHONY: push-local-registry
push-local-registry:
	/bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}

# run bat tests locally
.PHONY: bats
bats:
	./scripts/run-bats.sh

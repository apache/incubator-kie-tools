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
	@for f in $(shell make list); do make build-image image_name=$${iname}; done


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
	    ${BUILD_ENGINE} tag quay.io/kiegroup/${image_name}:${IMAGE_VERSION} quay.io/kiegroup/${image_name}:${SHORTENED_LATEST_VERSION}
    endif
endif
# if ignore_test is set to true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} --descriptor ${image_name}-image.yaml test behave ${test_options}
	tests/shell/run.sh ${image_name} "quay.io/kiegroup/${image_name}:${SHORTENED_LATEST_VERSION}"
endif

# Build all images
.PHONY: build-prod
# start to build the images
build-prod:
	@for iname in $(shell make list arg=--prod); do make build-prod-image image_name=$${iname}; done


.PHONY: build-prod-image
image_name=
build-prod-image: clone-repos _build-prod-image

_build-prod-image:
ifneq ($(ignore_build),true)
	scripts/build-kogito-apps-components.sh ${image_name} ${KOGITO_APPS_TARGET_BRANCH} ${KOGITO_APPS_TARGET_URI};
	scripts/build-product-image.sh "build" $(image_name) ${build_options} ${BUILD_ENGINE}
endif
# if ignore_test is set to true, ignore the tests
ifneq ($(ignore_test),true)
	scripts/build-product-image.sh "test" $(image_name) ${test_options}
endif

# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push

_push:
	@for f in $(shell make list); do make push-image image_name=$${f}; done

.PHONY: push-image
image_name=
push-image:
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push quay.io/kiegroup/${image_name}:${IMAGE_VERSION}
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push quay.io/kiegroup/${image_name}:latest
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	@echo "${SHORTENED_LATEST_VERSION} will be pushed"
	${BUILD_ENGINE} ${BUILD_ENGINE_TLS_OPTIONS} push quay.io/kiegroup/${image_name}:${SHORTENED_LATEST_VERSION}
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

.PHONY: container-build-osbs
prod_component=
container-build-osbs:
	echo "calling RHPAM container-build-osbs......................................"
	$(CEKIT_CMD) --descriptor $(prod_component).yaml --redhat build ${build_options} osbs --assume-yes ${osbs_options}
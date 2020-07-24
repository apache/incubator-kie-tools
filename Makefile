IMAGE_VERSION := $(shell cat image.yaml | egrep ^version  | cut -d"\"" -f2)
SHORTENED_LATEST_VERSION := $(shell echo $(IMAGE_VERSION) | awk -F. '{print $$1"."$$2}')
BUILD_ENGINE := docker
.DEFAULT_GOAL := build
CEKIT_CMD := cekit -v ${cekit_option}

# Build all images
.PHONY: build
# start to build the images
build: clone-repos kogito-quarkus-ubi8 kogito-quarkus-jvm-ubi8 kogito-quarkus-ubi8-s2i kogito-springboot-ubi8 kogito-springboot-ubi8-s2i kogito-data-index kogito-trusty kogito-explainability kogito-jobs-service kogito-management-console

clone-repos:
# if the NO_TEST env defined, proceed with the tests, as first step prepare the repo to be used
ifneq ($(ignore_test),true)
	cd tests/test-apps && sh clone-repo.sh
endif

# build the quay.io/kiegroup/kogito-quarkus-ubi8 image
kogito-quarkus-ubi8:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-quarkus-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-quarkus-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)),rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-quarkus-ubi8:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-ubi8:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-quarkus-jvm-ubi8 image
kogito-quarkus-jvm-ubi8:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-quarkus-jvm-overrides.yaml ${BUILD_ENGINE}
endif
# if no NO_TEST env defined, test the image
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-quarkus-jvm-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)),rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-quarkus-jvm-ubi8:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-jvm-ubi8:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-quarkus-ubi8-s2i image
kogito-quarkus-ubi8-s2i:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-quarkus-s2i-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-quarkus-s2i-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)),rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-springboot-ubi8 image
kogito-springboot-ubi8:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-springboot-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-springboot-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)),rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-ubi8:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-springboot-ubi8-s2i image
kogito-springboot-ubi8-s2i:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-springboot-s2i-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-springboot-s2i-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-ubi8-s2i:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-data-index image
kogito-data-index:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-data-index-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-data-index-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-data-index:${IMAGE_VERSION} quay.io/kiegroup/kogito-data-index:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-trusty image
kogito-trusty:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-trusty-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-trusty-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-trusty:${IMAGE_VERSION} quay.io/kiegroup/kogito-trusty:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-explainability image
kogito-explainability:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-explainability-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-explainability-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-explainability:${IMAGE_VERSION} quay.io/kiegroup/kogito-explainability:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-jobs-service image
kogito-jobs-service:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-jobs-service-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-jobs-service-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-jobs-service:${IMAGE_VERSION} quay.io/kiegroup/kogito-jobs-service:${SHORTENED_LATEST_VERSION}
endif

# build the quay.io/kiegroup/kogito-management-console image
kogito-management-console:
ifneq ($(ignore_build),true)
	${CEKIT_CMD} build --overrides-file kogito-management-console-overrides.yaml ${BUILD_ENGINE}
endif
# if ignore_test is set tu true, ignore the tests
ifneq ($(ignore_test),true)
	${CEKIT_CMD} test --overrides-file kogito-management-console-overrides.yaml behave
endif
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	${BUILD_ENGINE} tag quay.io/kiegroup/kogito-management-console:${IMAGE_VERSION} quay.io/kiegroup/kogito-management-console:${SHORTENED_LATEST_VERSION}
endif


# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push
_push:
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:latest
	docker push quay.io/kiegroup/kogito-quarkus-jvm-ubi8:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest
	docker push quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8:latest
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest
	docker push quay.io/kiegroup/kogito-data-index:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-data-index:latest
	docker push quay.io/kiegroup/kogito-trusty:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-trusty:latest
	docker push quay.io/kiegroup/kogito-explainability:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-explainability:latest
	docker push quay.io/kiegroup/kogito-jobs-service:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-jobs-service:latest
	docker push quay.io/kiegroup/kogito-management-console:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-management-console:latest
ifneq ($(findstring rc,$(IMAGE_VERSION)), rc)
	@echo "${SHORTENED_LATEST_VERSION} will be pushed"
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-jvm-ubi8:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-data-index:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-trusty:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-explainability:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-jobs-service:${SHORTENED_LATEST_VERSION}
	docker push quay.io/kiegroup/kogito-management-console:${SHORTENED_LATEST_VERSION}
endif


# push staging images to quay.io, done before release, this requires permissions under kiegroup organization
.PHONY: push-staging
push-staging: build _push-staging
_push-staging:
	python3 scripts/push-staging.py


# push to local registry, useful to push the built images to local registry
# requires parameter: REGISTRY: my-custom-registry:[port]
# requires pre built images, if no images, run make build first
# the shortened version will be used so operator can fetch it from the local namespace.
# use the NS env to set the current namespace, if no set openshift will be used
# example:  make push-local-registry REGISTRY=docker-registry-default.apps.spolti.cloud NS=spolti-1
.PHONY: push-local-registry
push-local-registry:
	/bin/sh scripts/push-local-registry.sh ${REGISTRY} ${SHORTENED_LATEST_VERSION} ${NS}


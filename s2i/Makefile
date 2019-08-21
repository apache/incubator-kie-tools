IMAGE_VERSION := $(shell cat image.yaml | egrep ^version  | cut -d"\"" -f2)
BUILD_ENGINE := docker
.DEFAULT_GOAL := build

# Build all images
.PHONY: build
build: kogito-quarkus-ubi8 kogito-quarkus-ubi8-s2i kogito-springboot-ubi8 kogito-springboot-ubi8-s2i

kogito-quarkus-ubi8:
	cekit -v build --overrides-file kogito-quarkus-ubi8-overrides.yaml ${BUILD_ENGINE}

kogito-quarkus-ubi8-s2i:
	cekit -v build --overrides-file kogito-quarkus-ubi8-s2i-overrides.yaml ${BUILD_ENGINE}

kogito-springboot-ubi8:
	cekit -v build --overrides-file kogito-springboot-ubi8-overrides.yaml ${BUILD_ENGINE}

kogito-springboot-ubi8-s2i:
	cekit -v build --overrides-file kogito-springboot-ubi8-s2i-overrides.yaml ${BUILD_ENGINE}


# Build and test all images
.PHONY: test
test:
	cekit -v build --overrides-file kogito-quarkus-ubi8-overrides.yaml --overrides 'name: kogito-quarkus-ubi8-candidate' docker --no-squash
	modules/kogito-quarkus-ubi8/test/run
	cekit -v build --overrides-file kogito-quarkus-ubi8-s2i-overrides.yaml --overrides 'name: kogito-quarkus-ubi8-s2i-candidate' docker --no-squash
	modules/kogito-quarkus-ubi8-s2i/test/run
	cekit -v build --overrides-file kogito-springboot-ubi8-overrides.yaml --overrides 'name: kogito-springboot-ubi8-candidate' docker --no-squash
	modules/kogito-springboot-ubi8/test/run
	cekit -v build --overrides-file kogito-springboot-ubi8-s2i-overrides.yaml --overrides 'name: kogito-springboot-ubi8-s2i-candidate' docker --no-squash
	modules/kogito-springboot-ubi8-s2i-/test/run


# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push
_push:
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:$(IMAGE_VERSION)
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:latest
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest
	docker push quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8:latest
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest


# push staging images to quay.io, done before release, this requires permissions under kiegroup organization
.PHONY: push-staging
push-staging: build _push-staging
_push-staging:
	docker tag quay.io/kiegroup/kogito-quarkus-ubi8:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-ubi8:${IMAGE_VERSION}-rc1
	docker push quay.io/kiegroup/kogito-quarkus-ubi8:${IMAGE_VERSION}-rc1

	docker tag quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION}-rc1
	docker push quay.io/kiegroup/kogito-quarkus-ubi8-s2i:${IMAGE_VERSION}-rc1

	docker tag quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION}-rc1
	docker push quay.io/kiegroup/kogito-springboot-ubi8:${IMAGE_VERSION}-rc1

	docker tag quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION}-rc1
	docker push quay.io/kiegroup/kogito-springboot-ubi8-s2i:${IMAGE_VERSION}-rc1
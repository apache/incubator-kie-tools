IMAGE_VERSION := $(shell cat image.yaml | egrep ^version  | cut -d"\"" -f2)
BUILD_ENGINE := docker
.DEFAULT_GOAL := build

# Build all images
.PHONY: build
build: kogito-quarkus-centos kogito-quarkus-centos-s2i kogito-springboot-centos kogito-springboot-centos-s2i

kogito-quarkus-centos:
	cekit -v build --overrides-file kogito-quarkus-centos-overrides.yaml ${BUILD_ENGINE}

kogito-quarkus-centos-s2i:
	cekit -v build --overrides-file kogito-quarkus-centos-s2i-overrides.yaml ${BUILD_ENGINE}

kogito-springboot-centos:
	cekit -v build --overrides-file kogito-springboot-centos-overrides.yaml ${BUILD_ENGINE}

kogito-springboot-centos-s2i:
	cekit -v build --overrides-file kogito-springboot-centos-s2i-overrides.yaml ${BUILD_ENGINE}


# Build and test all images
.PHONY: test
test:
	cekit -v build --overrides-file kogito-quarkus-centos-overrides.yaml --overrides 'name: kogito-quarkus-centos-candidate' docker --no-squash
	modules/kogito-quarkus-centos/test/run
	cekit -v build --overrides-file kogito-quarkus-centos-s2i-overrides.yaml --overrides 'name: kogito-quarkus-centos-s2i-candidate' docker --no-squash
	modules/kogito-quarkus-centos-s2i/test/run
	cekit -v build --overrides-file kogito-springboot-centos-overrides.yaml --overrides 'name: kogito-springboot-centos-candidate' docker --no-squash
	modules/kogito-springboot-centos/test/run
	cekit -v build --overrides-file kogito-springboot-centos-s2i-overrides.yaml --overrides 'name: kogito-springboot-centos-s2i-candidate' docker --no-squash
	modules/kogito-springboot-centos-s2i-/test/run


# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push
_push:
	docker push quay.io/kiegroup/kogito-quarkus-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-centos:latest
	docker push quay.io/kiegroup/kogito-quarkus-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-centos-s2i:latest
	docker push quay.io/kiegroup/kogito-springboot-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-centos:latest
	docker push quay.io/kiegroup/kogito-springboot-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-centos-s2i:latest

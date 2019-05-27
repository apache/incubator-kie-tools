IMAGE_VERSION := $(shell cat image.yaml | egrep ^version  | cut -d"\"" -f2)

.DEFAULT_GOAL := build

# Build all images
.PHONY: build
build: kogito-quarkus-centos kogito-quarkus-centos-s2i kogito-springboot-centos kogito-springboot-centos-s2i

kogito-quarkus-centos:
	cekit build -v --overrides-file kogito-quarkus-centos-overrides.yaml
	docker-squash quay.io/kiegroup/kogito-quarkus-centos:${IMAGE_VERSION} --tag=quay.io/kiegroup/kogito-quarkus-centos:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kogito-quarkus-centos:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-centos:latest

kogito-quarkus-centos-s2i:
	cekit build -v --overrides-file kogito-quarkus-centos-s2i-overrides.yaml
	docker-squash quay.io/kiegroup/kogito-quarkus-centos-s2i:${IMAGE_VERSION} --tag=quay.io/kiegroup/kogito-quarkus-centos-s2i:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kogito-quarkus-centos-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-quarkus-centos-s2i:latest

kogito-springboot-centos:
	cekit build -v --overrides-file kogito-springboot-centos-overrides.yaml
	docker-squash quay.io/kiegroup/kogito-springboot-centos:${IMAGE_VERSION} --tag=quay.io/kiegroup/kogito-springboot-centos:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kogito-springboot-centos:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-centos:latest

kogito-springboot-centos-s2i:
	cekit build -v --overrides-file kogito-springboot-centos-s2i-overrides.yaml
	docker-squash quay.io/kiegroup/kogito-springboot-centos-s2i:${IMAGE_VERSION} --tag=quay.io/kiegroup/kogito-springboot-centos-s2i:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kogito-springboot-centos-s2i:${IMAGE_VERSION} quay.io/kiegroup/kogito-springboot-centos-s2i:latest

.PHONY: build-cekit
build-cekit3:
	cekit -v build --overrides-file kogito-quarkus-centos-overrides.yaml docker
	cekit -v build --overrides-file kogito-quarkus-centos-s2i-overrides.yaml docker
	cekit -v build --overrides-file kogito-springboot-centos-overrides.yaml docker
	cekit -v build --overrides-file kogito-springboot-centos-s2i-overrides.yaml docker


# Build and test all images
.PHONY: test
test:
	cekit build -v --overrides-file kogito-quarkus-centos-overrides.yaml
	modules/kogito-quarkus-centos/test/run

	cekit build -v --overrides-file kogito-quarkus-centos-s2i-overrides.yaml
	modules/kogito-quarkus-centos-s2i/test/run

	cekit build -v --overrides-file kogito-springboot-centos-overrides.yaml
	modules/kogito-springboot-centos/test/run

	cekit build -v --overrides-file kogito-springboot-centos-s2i-overrides.yaml
	modules/kogito-springboot-centos-s2i-/test/run

test-cekit3:
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

push-cekit3: build-cekit3 _push

_push:
	docker push quay.io/kiegroup/kogito-quarkus-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-centos:latest
	docker push quay.io/kiegroup/kogito-quarkus-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-quarkus-centos-s2i:latest
	docker push quay.io/kiegroup/kogito-springboot-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-centos:latest
	docker push quay.io/kiegroup/kogito-springboot-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kogito-springboot-centos-s2i:latest

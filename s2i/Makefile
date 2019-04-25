IMAGE_VERSION := $(shell cat image.yaml | egrep ^version  | cut -d"\"" -f2)

.DEFAULT_GOAL := build

# Build all images
.PHONY: build
build: kaas-quarkus-centos kaas-quarkus-centos-s2i kaas-springboot-centos kaas-springboot-centos-s2i

kaas-quarkus-centos:
	cekit build -v --overrides-file kaas-quarkus-centos-overrides.yaml
	docker-squash quay.io/kiegroup/kaas-quarkus-centos:${IMAGE_VERSION} --tag=quay.io/kiegroup/kaas-quarkus-centos:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kaas-quarkus-centos:${IMAGE_VERSION} quay.io/kiegroup/kaas-quarkus-centos:latest

kaas-quarkus-centos-s2i:
	cekit build -v --overrides-file kaas-quarkus-centos-s2i-overrides.yaml
	docker-squash quay.io/kiegroup/kaas-quarkus-centos-s2i:${IMAGE_VERSION} --tag=quay.io/kiegroup/kaas-quarkus-centos-s2i:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kaas-quarkus-centos-s2i:${IMAGE_VERSION} quay.io/kiegroup/kaas-quarkus-centos-s2i:latest

kaas-springboot-centos:
	cekit build -v --overrides-file kaas-springboot-centos-overrides.yaml
	docker-squash quay.io/kiegroup/kaas-springboot-centos:${IMAGE_VERSION} --tag=quay.io/kiegroup/kaas-springboot-centos:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kaas-springboot-centos:${IMAGE_VERSION} quay.io/kiegroup/kaas-springboot-centos:latest

kaas-springboot-centos-s2i:
	cekit build -v --overrides-file kaas-springboot-centos-s2i-overrides.yaml
	docker-squash quay.io/kiegroup/kaas-springboot-centos-s2i:${IMAGE_VERSION} --tag=quay.io/kiegroup/kaas-springboot-centos-s2i:${IMAGE_VERSION}
	docker tag quay.io/kiegroup/kaas-springboot-centos-s2i:${IMAGE_VERSION} quay.io/kiegroup/kaas-springboot-centos-s2i:latest

.PHONY: build-cekit
build-cekit3:
	cekit -v build --overrides-file kaas-quarkus-centos-overrides.yaml docker
	cekit -v build --overrides-file kaas-quarkus-centos-s2i-overrides.yaml docker
	cekit -v build --overrides-file kaas-springboot-centos-overrides.yaml docker
	cekit -v build --overrides-file kaas-springboot-centos-s2i-overrides.yaml docker


# Build and test all images
.PHONY: test
test:
	cekit build -v --overrides-file kaas-quarkus-centos-overrides.yaml
	modules/kaas-quarkus-centos/test/run

	cekit build -v --overrides-file kaas-quarkus-centos-s2i-overrides.yaml
	modules/kaas-quarkus-centos-s2i/test/run

	cekit build -v --overrides-file kaas-springboot-centos-overrides.yaml
	modules/kaas-springboot-centos/test/run

	cekit build -v --overrides-file kaas-springboot-centos-s2i-overrides.yaml
	modules/kaas-springboot-centos-s2i-/test/run

test-cekit3:
	cekit -v build --overrides-file kaas-quarkus-centos-overrides.yaml --overrides 'name: kaas-quarkus-centos-candidate' docker --no-squash
	modules/kaas-quarkus-centos/test/run
	cekit -v build --overrides-file kaas-quarkus-centos-s2i-overrides.yaml --overrides 'name: kaas-quarkus-centos-s2i-candidate' docker --no-squash
	modules/kaas-quarkus-centos-s2i/test/run
	cekit -v build --overrides-file kaas-springboot-centos-overrides.yaml --overrides 'name: kaas-springboot-centos-candidate' docker --no-squash
	modules/kaas-springboot-centos/test/run
	cekit -v build --overrides-file kaas-springboot-centos-s2i-overrides.yaml --overrides 'name: kaas-springboot-centos-s2i-candidate' docker --no-squash
	modules/kaas-springboot-centos-s2i-/test/run

# push images to quay.io, this requires permissions under kiegroup organization
.PHONY: push
push: build _push

cekit3: build-cekit3 _push

_push:
	docker push quay.io/kiegroup/kaas-quarkus-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kaas-quarkus-centos:latest
	docker push quay.io/kiegroup/kaas-quarkus-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kaas-quarkus-centos-s2i:latest
	docker push quay.io/kiegroup/kaas-springboot-centos:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kaas-springboot-centos:latest
	docker push quay.io/kiegroup/kaas-springboot-centos-s2i:${IMAGE_VERSION}
	docker push quay.io/kiegroup/kaas-springboot-centos-s2i:latest
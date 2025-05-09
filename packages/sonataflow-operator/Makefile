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

# VERSION defines the project version for the bundle.
# Update this value when you upgrade the version of your project.
# To re-generate a bundle for another specific version without changing the standard setup, you can:
# - use the VERSION as arg of the bundle target (e.g make bundle VERSION=0.0.2)
# - use environment variables to overwrite this value (e.g export VERSION=0.0.2)
VERSION ?= $(shell pnpm build-env sonataFlowOperator.version)
IMAGE_TAG ?= $(shell pnpm build-env sonataFlowOperator.buildTag)

# CHANNELS define the bundle channels used in the bundle.
# Add a new line here if you would like to change its default config. (E.g CHANNELS = "candidate,fast,stable")
# To re-generate a bundle for other specific channels without changing the standard setup, you can:
# - use the CHANNELS as arg of the bundle target (e.g make bundle CHANNELS=candidate,fast,stable)
# - use environment variables to overwrite this value (e.g export CHANNELS="candidate,fast,stable")
ifneq ($(origin CHANNELS), undefined)
BUNDLE_CHANNELS := --channels=$(CHANNELS)
endif

# DEFAULT_CHANNEL defines the default channel used in the bundle.
# Add a new line here if you would like to change its default config. (E.g DEFAULT_CHANNEL = "stable")
# To re-generate a bundle for any other default channel without changing the default setup, you can:
# - use the DEFAULT_CHANNEL as arg of the bundle target (e.g make bundle DEFAULT_CHANNEL=stable)
# - use environment variables to overwrite this value (e.g export DEFAULT_CHANNEL="stable")
ifneq ($(origin DEFAULT_CHANNEL), undefined)
BUNDLE_DEFAULT_CHANNEL := --default-channel=$(DEFAULT_CHANNEL)
endif
BUNDLE_METADATA_OPTS ?= $(BUNDLE_CHANNELS) $(BUNDLE_DEFAULT_CHANNEL)

# IMAGE_TAG_BASE defines the image namespace and part of the image name for remote images.
# This variable is used to construct full image tags for bundle and catalog images.
#
# For example, running 'make bundle-build bundle-push catalog-build catalog-push' will build and push both
# apache/sonataflow-operator-bundle:$VERSION and apache/sonataflow-operator-catalog:$VERSION.
IMAGE_TAG_BASE ?= $(shell pnpm build-env sonataFlowOperator.registry)/$(shell pnpm build-env sonataFlowOperator.account)/$(shell pnpm build-env sonataFlowOperator.name)

# BUNDLE_IMG defines the image:tag used for the bundle.
# You can use it as an arg. (E.g make bundle-build BUNDLE_IMG=<some-registry>/<project-name-bundle>:<tag>)
BUNDLE_IMG ?= $(IMAGE_TAG_BASE)-bundle:v$(IMAGE_TAG)

# BUNDLE_GEN_FLAGS are the flags passed to the operator-sdk generate bundle command
# TODO: review this flag once we upgrade https://github.com/operator-framework/operator-sdk/issues/4992 (https://issues.redhat.com/browse/KOGITO-9428)
# TODO: It is preventing us from adding new annotations to bundle/metadata/annotations.yaml
BUNDLE_GEN_FLAGS ?= -q --overwrite=false --version $(VERSION) $(BUNDLE_METADATA_OPTS)

# Container runtime engine used for building the images
BUILDER ?= docker

# USE_IMAGE_DIGESTS defines if images are resolved via tags or digests
# You can enable this value if you would like to use SHA Based Digests
# To enable set flag to true
IMG_TAG_SEP = :
USE_IMAGE_DIGESTS ?= false
ifeq ($(USE_IMAGE_DIGESTS), true)
	BUNDLE_GEN_FLAGS += --use-image-digests
	IMG_TAG_SEP = @
endif

# Image URL to use all building/pushing image targets
IMG ?= $(IMAGE_TAG_BASE)$(IMG_TAG_SEP)$(IMAGE_TAG)
# ENVTEST_K8S_VERSION refers to the version of kubebuilder assets to be downloaded by envtest binary.
ENVTEST_K8S_VERSION = 1.26

OPERATOR_SDK_VERSION ?= 1.35.0

# Get the currently used golang install path (in GOPATH/bin, unless GOBIN is set)
ifeq (,$(shell go env GOBIN))
GOBIN=$(shell go env GOPATH)/bin
else
GOBIN=$(shell go env GOBIN)
endif

# Setting SHELL to bash allows bash commands to be executed by recipes.
# This is a requirement for 'setup-envtest.sh' in the test target.
# Options are set to exit when a recipe line exits non-zero or a piped command fails.
SHELL = /usr/bin/env bash -o pipefail
.SHELLFLAGS = -ec

.PHONY: all
all: build

##@ General

# The help target prints out all targets with their descriptions organized
# beneath their categories. The categories are represented by '##@' and the
# target descriptions by '##'. The awk commands is responsible for reading the
# entire set of makefiles included in this invocation, looking for lines of the
# file as xyz: ## something, and then pretty-format the target and help. Then,
# if there's a line with ##@ something, that gets pretty-printed as a category.
# More info on the usage of ANSI control characters for terminal formatting:
# https://en.wikipedia.org/wiki/ANSI_escape_code#SGR_parameters
# More info on the awk command:
# http://linuxcommand.org/lc3_adv_awk.php

.PHONY: help
help: ## Display this help.
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n"} /^[a-zA-Z_0-9-]+:.*?##/ { printf "  \033[36m%-15s\033[0m %s\n", $$1, $$2 } /^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } ' $(MAKEFILE_LIST)

##@ Development

.PHONY: manifests
manifests: generate ## Generate WebhookConfiguration, ClusterRole and CustomResourceDefinition objects.
	@echo "📄 Generating WebhookConfiguration, ClusterRole, and CRD objects..."
	@$(CONTROLLER_GEN) rbac:roleName=manager-role crd:allowDangerousTypes=true webhook paths="./api/..." paths="./internal/controller/..." output:crd:artifacts:config=config/crd/bases

.PHONY: generate
generate: controller-gen ## Generate code containing DeepCopy, DeepCopyInto, and DeepCopyObject method implementations.
	@echo "🔄 Generating DeepCopy methods for APIs..."
	@$(CONTROLLER_GEN) object:headerFile="hack/boilerplate.go.txt" paths="./api/..." paths="./container-builder/api/..." > /dev/null 2>&1

.PHONY: fmt
fmt: ## Run go fmt against code.
	@echo "🧹 Running go fmt and goimports..."
	@./hack/goimports.sh > /dev/null 2>&1
	@go work sync > /dev/null 2>&1
	@go mod tidy > /dev/null 2>&1
	@go fmt ./... > /dev/null 2>&1

.PHONY: vet
vet: ## Run go vet against code.
	@echo "🔍 Running go vet..."
	@go vet ./...

.PHONY: test
test: manifests generate test-api ## Run tests.
	@$(MAKE) addheaders
	@$(MAKE) vet
	@$(MAKE) fmt
	@echo "🔍 Running controller tests..."
	go test $(shell go list ./... | grep -v /test/) -coverprofile cover.out
	@echo "✅  Tests completed successfully. Coverage report generated: cover.out."

.PHONY: test-api
test-api:
	@echo "🔄 Running API tests..."
	@cd api && make test > /dev/null 2>&1
	@echo "✅  API tests completed successfully."


.PHONY: lint
lint: golangci-lint ## Run golangci-lint linter
	$(GOLANGCI_LINT) run

.PHONY: lint-fix
lint-fix: golangci-lint ## Run golangci-lint linter and perform fixes
	$(GOLANGCI_LINT) run --fix

######
# Test proxy commands

TEST_DIR=testbdd

.PHONY: run-tests
run-tests: generate-all
	@(cd $(TEST_DIR) && $(MAKE) $@)

.PHONY: run-smoke-tests
run-smoke-tests: generate-all
	@(cd $(TEST_DIR) && $(MAKE) $@)

.PHONY: test-container-builder
test-container-builder:
	cd container-builder && make test

.PHONY: test-workflowproj
test-workflowproj:
	cd workflowproj && make test

##@ Build

.PHONY: build
build: ## Build manager binary.
	CGO_ENABLED=0 go build -trimpath -ldflags=-buildid= -o bin/manager cmd/main.go

.PHONY: build-4-debug
build-4-debug: generate ## Build manager binary with debug options.
	go build -gcflags="all=-N -l" -o bin/manager cmd/main.go

.PHONY: run
run: manifests generate ## Run a controller from your host.
	go run ./cmd/main.go -v=2 -controller-cfg-path=$(CURDIR)/config/manager/controllers_cfg.yaml

.PHONY: debug
debug: build-4-debug ## Run a controller from your host from binary
	./bin/manager -v=2 -controller-cfg-path=$(CURDIR)/config/manager/controllers_cfg.yaml

# This is currently done directly into the CI
# PLATFORMS defines the target platforms for the manager image be build to provide support to multiple
# architectures. (i.e. make docker-buildx IMG=myregistry/mypoperator:0.0.1). To use this option you need to:
# - able to use docker buildx . More info: https://docs.docker.com/build/buildx/
# - have enable BuildKit, More info: https://docs.docker.com/develop/develop-images/build_enhancements/
# - be able to push the image for your registry (i.e. if you do not inform a valid value via IMG=<myregistry/image:<tag>> than the export will fail)
# To properly provided solutions that supports more than one platform you should use this option.
PLATFORMS ?= linux/arm64,linux/amd64,linux/s390x,linux/ppc64le
.PHONY: docker-buildx
docker-buildx: generate ## Build and push docker image for the manager for cross-platform support
	# copy existing Dockerfile and insert --platform=${BUILDPLATFORM} into Dockerfile.cross, and preserve the original Dockerfile
	sed -e '1 s/\(^FROM\)/FROM --platform=\$$\{BUILDPLATFORM\}/; t' -e ' 1,// s//FROM --platform=\$$\{BUILDPLATFORM\}/' Dockerfile > Dockerfile.cross
	- docker buildx create --name project-v3-builder
	docker buildx use project-v3-builder
	- docker buildx build --build-arg SOURCE_DATE_EPOCH=$(shell git log -1 --pretty=%ct) --push . --platform=$(PLATFORMS) --tag ${IMG} -f Dockerfile.cross
	- docker buildx rm project-v3-builder
	rm Dockerfile.cross

.PHONY: container-build
container-build: ## Build the container image
	cekit -v --descriptor images/manager.yaml build ${build_options} $(BUILDER) --build-arg SOURCE_DATE_EPOCH="$(shell git log -1 --pretty=%ct)"
ifneq ($(ignore_tag),true)
	$(BUILDER) tag sonataflow-operator:latest ${IMG}
endif

.PHONY: container-push
container-push: ## Push the container image
	$(BUILDER) push ${CONTAINER_PUSH_PARAMS} ${IMG}

##@ Deployment

ifndef ignore-not-found
  ignore-not-found = false
endif

.PHONY: install
install: manifests kustomize ## Install CRDs into the K8s cluster specified in ~/.kube/config.
	$(KUSTOMIZE) build config/crd | kubectl create -f -

.PHONY: uninstall
uninstall: manifests kustomize ## Uninstall CRDs from the K8s cluster specified in ~/.kube/config. Call with ignore-not-found=true to ignore resource not found errors during deletion.
	$(KUSTOMIZE) build config/crd | kubectl delete --ignore-not-found=$(ignore-not-found) -f -

.PHONY: deploy
deploy: manifests kustomize ## Deploy controller to the K8s cluster specified in ~/.kube/config.
	cd config/manager && $(KUSTOMIZE) edit set image controller=${IMG}
	$(KUSTOMIZE) build config/default | kubectl create -f -

.PHONY: generate-deploy
generate-deploy: manifests kustomize ## Deploy controller to the K8s cluster specified in ~/.kube/config.
	@echo "🚀 Updating controller image to ${IMG}..."
	@cd config/manager && $(KUSTOMIZE) edit set image controller=${IMG} > /dev/null 2>&1
	@echo "📄 Building deployment YAML..."
	@$(KUSTOMIZE) build config/default > operator.yaml


.PHONY: undeploy
undeploy: ## Undeploy controller from the K8s cluster specified in ~/.kube/config. Call with ignore-not-found=true to ignore resource not found errors during deletion.
	$(KUSTOMIZE) build config/default | kubectl delete --ignore-not-found=$(ignore-not-found) -f -

##@ Build Dependencies

## Location to install dependencies to
LOCALBIN ?= $(shell pwd)/bin
$(LOCALBIN):
	mkdir -p $(LOCALBIN)

## Tool Binaries
KUBECTL ?= kubectl
KUSTOMIZE ?= $(LOCALBIN)/kustomize-$(KUSTOMIZE_VERSION)
CONTROLLER_GEN ?= $(LOCALBIN)/controller-gen
ENVTEST ?= $(LOCALBIN)/setup-envtest-$(ENVTEST_VERSION)
GOLANGCI_LINT = $(LOCALBIN)/golangci-lint-$(GOLANGCI_LINT_VERSION)

## Tool Versions
KUSTOMIZE_VERSION ?= v5.4.1
CONTROLLER_TOOLS_VERSION ?= v0.16.4
ENVTEST_VERSION ?= release-0.18
GOLANGCI_LINT_VERSION ?= v1.57.2

KIND_VERSION ?= v0.20.0
KNATIVE_VERSION ?= v1.13.2
TIMEOUT_SECS ?= 180s
PROMETHEUS_VERSION ?= v0.70.0
GRAFANA_VERSION ?= v5.13.0

KNATIVE_SERVING_PREFIX ?= "https://github.com/knative/serving/releases/download/knative-$(KNATIVE_VERSION)"
KNATIVE_EVENTING_PREFIX ?= "https://github.com/knative/eventing/releases/download/knative-$(KNATIVE_VERSION)"
KUSTOMIZE_INSTALL_SCRIPT ?= "https://raw.githubusercontent.com/kubernetes-sigs/kustomize/master/hack/install_kustomize.sh"

.PHONY: kustomize
kustomize: $(KUSTOMIZE) ## Download kustomize locally if necessary.
$(KUSTOMIZE): $(LOCALBIN)
	$(call go-install-tool,$(KUSTOMIZE),sigs.k8s.io/kustomize/kustomize/v5,$(KUSTOMIZE_VERSION))

.PHONY: controller-gen
controller-gen: $(CONTROLLER_GEN) ## Download controller-gen locally if necessary.
	@echo "⬇️  Ensuring controller-gen is installed..."
	@test -s $(CONTROLLER_GEN) || (GOBIN=$(LOCALBIN) go install sigs.k8s.io/controller-tools/cmd/controller-gen@$(CONTROLLER_TOOLS_VERSION) > /dev/null 2>&1 && echo "✅  controller-gen installed successfully!")

$(CONTROLLER_GEN):
	@mkdir -p $(LOCALBIN) # Ensure LOCALBIN exists

.PHONY: envtest
envtest: $(ENVTEST) ## Download setup-envtest locally if necessary.
$(ENVTEST): $(LOCALBIN)
	$(call go-install-tool,$(ENVTEST),sigs.k8s.io/controller-runtime/tools/setup-envtest,$(ENVTEST_VERSION))

.PHONY: golangci-lint
golangci-lint: $(GOLANGCI_LINT) ## Download golangci-lint locally if necessary.
$(GOLANGCI_LINT): $(LOCALBIN)
	$(call go-install-tool,$(GOLANGCI_LINT),github.com/golangci/golangci-lint/cmd/golangci-lint,${GOLANGCI_LINT_VERSION})

# go-install-tool will 'go install' any package with custom target and name of binary, if it doesn't exist
# $1 - target path with name of binary (ideally with version)
# $2 - package url which can be installed
# $3 - specific version of package
define go-install-tool
@[ -f $(1) ] || { \
set -e; \
package=$(2)@$(3) ;\
echo "Downloading $${package}" ;\
GOBIN=$(LOCALBIN) go install $${package} ;\
mv "$$(echo "$(1)" | sed "s/-$(3)$$//")" $(1) ;\
}
endef


.PHONY: bundle
PACKAGE_NAME = "sonataflow-operator"
bundle: kustomize install-operator-sdk ## Generate bundle manifests and metadata, then validate generated files.
	@echo "📦 Generating bundle manifests and metadata..."
	@operator-sdk generate kustomize manifests --package=$(PACKAGE_NAME) -q > /dev/null 2>&1
	@echo "🔧 Setting controller image in Kustomize..."
	@cd config/manager && $(KUSTOMIZE) edit set image controller=$(IMG) > /dev/null 2>&1
	@echo "🔨 Building Kustomize and generating bundle..."
	@$(KUSTOMIZE) build config/manifests | operator-sdk generate bundle $(BUNDLE_GEN_FLAGS) --package=$(PACKAGE_NAME) > /dev/null 2>&1
	@echo "🛠️  Validating generated bundle..."
	@operator-sdk bundle validate ./bundle > /dev/null 2>&1

.PHONY: bundle-build
BUNDLE_DESCRIPTOR = "images/bundle.yaml"
bundle-build: ## Build the bundle image
	cekit -v --descriptor $(BUNDLE_DESCRIPTOR) build ${build_options} $(BUILDER) --no-squash --platform=linux/amd64 --build-arg SOURCE_DATE_EPOCH="$(shell git log -1 --pretty=%ct)"
ifneq ($(ignore_tag),true)
	$(BUILDER) tag sonataflow-operator-bundle:latest $(BUNDLE_IMG)
endif

.PHONY: bundle-push
bundle-push: ## Push the bundle image.
	$(MAKE) container-push IMG=$(BUNDLE_IMG)

.PHONY: opm
OPM = ./bin/opm
opm: ## Download opm locally if necessary.
ifeq (,$(wildcard $(OPM)))
ifeq (,$(shell which opm 2>/dev/null))
	@{ \
	set -e ;\
	mkdir -p $(dir $(OPM)) ;\
	OS=$(shell go env GOOS) && ARCH=$(shell go env GOARCH) && \
	curl -sSLo $(OPM) https://github.com/operator-framework/operator-registry/releases/download/v$(OPERATOR_SDK_VERSION)/$${OS}-$${ARCH}-opm ;\
	chmod +x $(OPM) ;\
	}
else
OPM = $(shell which opm)
endif
endif

# A comma-separated list of bundle images (e.g. make catalog-build BUNDLE_IMGS=example.com/operator-bundle:v0.1.0,example.com/operator-bundle:v0.2.0).
# These images MUST exist in a registry and be pull-able.
BUNDLE_IMGS ?= $(BUNDLE_IMG)

# The image tag given to the resulting catalog image (e.g. make catalog-build CATALOG_IMG=example.com/operator-catalog:v0.2.0).
CATALOG_IMG ?= $(IMAGE_TAG_BASE)-catalog:v$(IMAGE_TAG)

# Set CATALOG_BASE_IMG to an existing catalog image tag to add $BUNDLE_IMGS to that image.
ifneq ($(origin CATALOG_BASE_IMG), undefined)
FROM_INDEX_OPT := --from-index $(CATALOG_BASE_IMG)
endif

PLATFORM ?= linux/amd64

# Build a catalog image by adding bundle images to an empty catalog using the operator package manager tool, 'opm'.
# This recipe invokes 'opm' in 'semver' bundle add mode. For more information on add modes, see:
# https://github.com/operator-framework/community-operators/blob/7f1438c/docs/packaging-operator.md#updating-your-existing-operator
.PHONY: catalog-build
catalog-build: opm ## Build a catalog image.
	$(OPM) index add --container-tool $(BUILDER) --mode semver --tag $(CATALOG_IMG) --bundles $(BUNDLE_IMGS) $(FROM_INDEX_OPT) --generate -d ./index.Dockerfile
	$(BUILDER) build --platform $(PLATFORM) -f ./index.Dockerfile -t $(CATALOG_IMG) .
	rm ./index.Dockerfile

# Push the catalog image.
.PHONY: catalog-push
catalog-push: ## Push a catalog image.
	$(MAKE) container-push IMG=$(CATALOG_IMG)

.PHONY: clean
clean:
	rm -rf bin/

.PHONY: bump-version
new_version = ""
bump-version:
	./hack/bump-version.sh $(new_version)

.PHONY: install-operator-sdk
install-operator-sdk:
	@echo "📦 Installing Operator SDK..."
	@./hack/install-operator-sdk.sh > /dev/null 2>&1


.PHONY: addheaders
addheaders:
	@echo "📝 Adding headers to files..."
	@./hack/addheaders.sh > /dev/null 2>&1

.PHONY: generate-all
generate-all: generate generate-deploy bundle
	@$(MAKE) addheaders
	@$(MAKE) vet
	@$(MAKE) fmt

.PHONY: test-e2e # You will need to have a Minikube/Kind cluster up and running to run this target, and run container-builder before the test
label = "flows-ephemeral" # possible values are flows-ephemeral, flows-persistence, flows-monitoring, platform, cluster
test-e2e:
ifeq ($(label), cluster)
	@echo "🌐 Running e2e tests for cluster..."
	go test ./test/e2e/e2e_suite_test.go ./test/e2e/helpers.go ./test/e2e/clusterplatform_test.go \
	-v -ginkgo.v -ginkgo.no-color -ginkgo.github-output -ginkgo.label-filter=$(label) \
	-ginkgo.junit-report=./e2e-test-report-clusterplatform_test.xml -timeout 60m KUSTOMIZE=$(KUSTOMIZE);
else ifeq ($(label), platform)
	@echo "📦 Running e2e tests for platform..."
	go test ./test/e2e/e2e_suite_test.go ./test/e2e/helpers.go ./test/e2e/platform_test.go \
	-v -ginkgo.v -ginkgo.no-color -ginkgo.github-output -ginkgo.label-filter=$(label) \
	-ginkgo.junit-report=./e2e-test-report-platform_test.xml -timeout 60m KUSTOMIZE=$(KUSTOMIZE);
else ifeq ($(label), flows-ephemeral)
	@echo "🔄 Running e2e tests for flows-ephemeral..."
	go test ./test/e2e/e2e_suite_test.go ./test/e2e/helpers.go ./test/e2e/workflow_test.go \
	-v -ginkgo.v -ginkgo.no-color -ginkgo.github-output -ginkgo.label-filter=$(label) \
	-ginkgo.junit-report=./e2e-test-report-workflow_test.xml -timeout 60m KUSTOMIZE=$(KUSTOMIZE);
else ifeq ($(label), flows-persistence)
	@echo "🔁 Running e2e tests for flows-persistence..."
	go test ./test/e2e/e2e_suite_test.go ./test/e2e/helpers.go ./test/e2e/workflow_test.go \
	-v -ginkgo.v -ginkgo.no-color -ginkgo.github-output -ginkgo.label-filter=$(label) \
	-ginkgo.junit-report=./e2e-test-report-workflow_test.xml -timeout 60m KUSTOMIZE=$(KUSTOMIZE);
else ifeq ($(label), flows-monitoring)
	@echo "🔁 Running e2e tests for flows-monitoring..."
	go test ./test/e2e/e2e_suite_test.go ./test/e2e/helpers.go ./test/e2e/workflow_test.go \
	-v -ginkgo.v -ginkgo.no-color -ginkgo.github-output -ginkgo.label-filter=$(label) \
	-ginkgo.junit-report=./e2e-test-report-workflow_test.xml -timeout 60m KUSTOMIZE=$(KUSTOMIZE);
else
	@echo "❌  Invalid label. Please use one of: cluster, platform, flows-ephemeral, flows-persistence, flows-monitoring"
endif

.PHONY: full-test-e2e
full-test-e2e: create-cluster load-docker-image deploy deploy-knative deploy-prometheus
	sleep 30
	kubectl wait pod -A -l app.kubernetes.io/name=sonataflow-operator --for condition=Ready --timeout 120s
	@$(MAKE) test-e2e label=platform
	@$(MAKE) test-e2e label=cluster
	@$(MAKE) test-e2e label=flows-monitoring
	@$(MAKE) test-e2e label=flows-persistence
	@$(MAKE) test-e2e label=flows-ephemeral

.PHONY: before-pr
before-pr: generate-all test ## Run generate-all before executing tests.
	@echo "✅  Your working branch is done."

.PHONY: load-docker-image
load-docker-image: install-kind
	kind load docker-image $(IMG)
	kind load docker-image $(shell pnpm build-env sonataFlowOperator.sonataflowBuilderImage)
	kind load docker-image $(shell pnpm build-env sonataFlowOperator.sonataflowDevModeImage)

.PHONY: install-kind
install-kind:
	command -v kind >/dev/null || go install sigs.k8s.io/kind@$(KIND_VERSION)

.PHONY: create-cluster
create-cluster: install-kind
	kind get clusters | grep kind >/dev/null || ./hack/create-kind-cluster-with-registry.sh $(BUILDER)

.PHONY: deploy-knative
deploy-knative:
	kubectl apply -f https://github.com/knative/operator/releases/download/knative-$(KNATIVE_VERSION)/operator.yaml
	kubectl wait  --for=condition=Available=True deploy/knative-operator -n default --timeout=$(TIMEOUT_SECS)
	kubectl apply -f ./test/testdata/knative_serving_eventing.yaml
	kubectl wait  --for=condition=Ready=True KnativeServing/knative-serving -n knative-serving --timeout=$(TIMEOUT_SECS)
	kubectl wait  --for=condition=Ready=True KnativeEventing/knative-eventing -n knative-eventing --timeout=$(TIMEOUT_SECS)
	
.PHONY: deploy-prometheus
deploy-prometheus: create-cluster
	kubectl create -f https://github.com/prometheus-operator/prometheus-operator/releases/download/$(PROMETHEUS_VERSION)/bundle.yaml
	kubectl wait  --for=condition=Available=True deploy/prometheus-operator -n default --timeout=$(TIMEOUT_SECS)
	kubectl apply -f ./test/testdata/prometheus.yaml -n default
	kubectl wait  --for=condition=Available=True prometheus/prometheus -n default --timeout=$(TIMEOUT_SECS)

.PHONY: deploy-grafana
deploy-grafana: create-cluster
	kubectl create -f https://github.com/grafana/grafana-operator/releases/download/$(GRAFANA_VERSION)/kustomize-cluster_scoped.yaml
	kubectl wait  --for=condition=Available=True deploy/grafana-operator-controller-manager -n grafana --timeout=$(TIMEOUT_SECS)

.PHONY: delete-cluster
delete-cluster: install-kind
	kind delete cluster && $(BUILDER) rm -f kind-registry

# Updates the manager_env_patch.yaml file with the images used by the operator.
# These params come from the package.json file processing the env vars at ./env/index.js
.PHONY: update-patch
update-patch:
	@echo "🔧 Updating Kustomize patch file..."
	$(eval PIN_TOOL ?= $$(shell build-env sonataFlowOperator.pinImageSHABundleTool)) # set to docker, podman, or skopeo if needed
	$(eval PARAMS := \
		RELATED_IMAGE_JOBS_SERVICE_POSTGRESQL=$$(shell build-env sonataFlowOperator.kogitoJobsServicePostgresqlImage) \
		RELATED_IMAGE_JOBS_SERVICE_EPHEMERAL=$$(shell build-env sonataFlowOperator.kogitoJobsServiceEphemeralImage) \
		RELATED_IMAGE_DATA_INDEX_POSTGRESQL=$$(shell build-env sonataFlowOperator.kogitoDataIndexPostgresqlImage) \
		RELATED_IMAGE_DATA_INDEX_EPHEMERAL=$$(shell build-env sonataFlowOperator.kogitoDataIndexEphemeralImage) \
		RELATED_IMAGE_BASE_BUILDER=$$(shell build-env sonataFlowOperator.sonataflowBuilderImage) \
		RELATED_IMAGE_DEVMODE=$$(shell build-env sonataFlowOperator.sonataflowDevModeImage) \
		RELATED_IMAGE_DB_MIGRATOR_TOOL=$$(shell build-env sonataFlowOperator.kogitoDBMigratorToolImage))
	@if [ -z "$(strip $(PARAMS))" ]; then \
		echo "⚠️  No variables resolved. Skipping updates."; \
	else \
		echo "✅ Resolved:"; echo "$(PARAMS)" | tr ' ' '\n'; \
		python ./hack/update_patch_env.py "$(PIN_TOOL)" $(PARAMS); \
		echo "✅ Patch updated!"; \
	fi

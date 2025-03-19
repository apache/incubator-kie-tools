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

BIN              := kn-workflow
BIN_DARWIN_AMD64 ?= $(BIN)-darwin-amd64
BIN_DARWIN_ARM64 ?= $(BIN)-darwin-arm64
BIN_LINUX        ?= $(BIN)-linux-amd64
BIN_WINDOWS      ?= $(BIN)-windows-amd64.exe

BIN_PATH         := ./dist
TEST_PATH        := ./dist-tests
MAIN_PATH        := cmd/main.go

METADATA_PATH                 := github.com/apache/incubator-kie-tools/packages/kn-plugin-workflow/pkg/metadata
SET_QUARKUS_PLATFORM_GROUP_ID := $(METADATA_PATH).QuarkusPlatformGroupId=$(QUARKUS_PLATFORM_GROUP_ID)
SET_QUARKUS_VERSION           := $(METADATA_PATH).QuarkusVersion=$(QUARKUS_VERSION)
SET_DEV_MODE_IMAGE            := $(METADATA_PATH).DevModeImage=$(DEV_MODE_IMAGE_URL)
SET_VERSION                   := $(METADATA_PATH).PluginVersion=$(PLUGIN_VERSION)
SET_KOGITO_VERSION            := $(METADATA_PATH).KogitoVersion=$(KOGITO_VERSION)
LDFLAGS                       := "-X $(SET_QUARKUS_PLATFORM_GROUP_ID) -X $(SET_QUARKUS_VERSION) -X $(SET_VERSION) -X $(SET_DEV_MODE_IMAGE) -X $(SET_KOGITO_VERSION)"

KIND_VERSION ?= v0.20.0
OLM_VERSION = v0.31.0

ARCH := $(shell uname -m)
ifeq ($(ARCH),arm64)
	GOARCH = arm64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_ARM64)
else
	GOARCH = amd64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_AMD64)
endif

build-all: build-linux-amd64 build-darwin-amd64 build-darwin-arm64 build-win32-amd64

build-darwin:
	CGO_ENABLED=0 GOOS=darwin GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_BUILD_DARWIN) $(MAIN_PATH)

build-darwin-amd64:
	CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_DARWIN_AMD64) $(MAIN_PATH)

build-darwin-arm64:
	CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_DARWIN_ARM64) $(MAIN_PATH)

build-linux-amd64:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_LINUX) $(MAIN_PATH)

build-win32-amd64:
	CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_WINDOWS) $(MAIN_PATH)

clean:
	go clean
	rm -rf $(BIN_PATH) $(TEST_PATH)

.PHONY: test-e2e
test-e2e:
	@$(MAKE) install-kind
	@$(MAKE) create-cluster
	@$(MAKE) install-operator-framework
	@$(MAKE) go-test-e2e
	@$(MAKE) go-test-e2e-report


.PHONY: install-kind
install-kind:
	command -v kind >/dev/null || go install sigs.k8s.io/kind@$(KIND_VERSION)

.PHONY: create-cluster
create-cluster: install-kind
	kind create cluster

.PHONY: install-operator-framework
install-operator-framework:
	curl -sL https://github.com/operator-framework/operator-lifecycle-manager/releases/download/$(OLM_VERSION)/install.sh | bash -s $(OLM_VERSION)

.PHONY: go-test-e2e
go-test-e2e:
	rm -rf dist-tests-e2e
	mkdir dist-tests-e2e
	go test -v ./e2e-tests/... -tags e2e_tests -timeout 20m 2>&1 | tee ./dist-tests-e2e/go-test-output-e2e.txt

.PHONY: go-test-e2e-report
go-test-e2e-report:
	go run github.com/jstemmer/go-junit-report/v2 \
	  -set-exit-code \
	  -in ./dist-tests-e2e/go-test-output-e2e.txt \
	  -out ./dist-tests-e2e/junit-report-it.xml
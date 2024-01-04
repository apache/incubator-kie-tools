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
SET_DEV_MODE_IMAGE            := $(METADATA_PATH).DevModeImage=$(DEV_MODE_IMAGE)
SET_VERSION                   := $(METADATA_PATH).PluginVersion=$(PLUGIN_VERSION)
SET_KOGITO_VERSION            := $(METADATA_PATH).KogitoVersion=$(KOGITO_VERSION)
LDFLAGS                       := "-X $(SET_QUARKUS_PLATFORM_GROUP_ID) -X $(SET_QUARKUS_VERSION) -X $(SET_VERSION) -X $(SET_DEV_MODE_IMAGE) -X $(SET_KOGITO_VERSION)"

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
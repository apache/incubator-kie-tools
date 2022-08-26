#
# Copyright 2022 Red Hat, Inc. and/or its affiliates.
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

BIN              := kn-workflow
BIN_DARWIN_AMD64 ?= $(BIN)-darwin-amd64
BIN_DARWIN_ARM64 ?= $(BIN)-darwin-arm64
BIN_LINUX        ?= $(BIN)-linux-amd64
BIN_WINDOWS      ?= $(BIN)-windows-amd64.exe

SET_QUARKUS_PLATFORM_GROUP_ID := github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata.QuarkusPlatformGroupId=$(QUARKUS_PLATFORM_GROUP_ID)
SET_QUARKUS_VERSION           := github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata.QuarkusVersion=$(QUARKUS_VERSION)
SET_VERSION                   := github.com/kiegroup/kie-tools/packages/kn-plugin-workflow/pkg/metadata.PluginVersion=$(PLUGIN_VERSION)
LDFLAGS                       := "-X $(SET_QUARKUS_PLATFORM_GROUP_ID) -X $(SET_QUARKUS_VERSION) -X $(SET_VERSION)"

ARCH := $(shell uname -m)
ifeq ($(ARCH),arm64)
	GOARCH = arm64
else
	GOARCH = amd64
endif

build-all: build-linux build-darwin-amd64 build-darwin-arm64 build-win32

build-darwin:
	CGO_ENABLED=0 GOOS=darwin GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_DARWIN_AMD64) cmd/main.go

build-darwin-amd64:
	CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_DARWIN_AMD64) cmd/main.go

build-darwin-arm64:
	CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_DARWIN_ARM64) cmd/main.go

build-linux:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_LINUX) cmd/main.go

build-win32:
	CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_WINDOWS) cmd/main.go

clean:
	go clean
	rm -rf $(BINARY_PATH) 
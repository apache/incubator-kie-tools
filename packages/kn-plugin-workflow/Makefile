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

BIN 		 := kn-workflow
BIN_DARWIN   ?= $(BIN)-darwin-amd64
BIN_LINUX  	 ?= $(BIN)-linux-amd64
BIN_WINDOWS	 ?= $(BIN)-windows-amd64.exe

QUARKUS_VERSION := $(shell pnpm build-env knPluginWorkflow.quarkusVersion)
KOGITO_VERSION := $(shell pnpm build-env knPluginWorkflow.kogitoVersion)
VERSION := $(shell pnpm build-env knPluginWorkflow.version)

SET_QUARKUS_VERSION := main.quarkusVersion=$(QUARKUS_VERSION)
SET_KOGITO_VERSION := main.kogitoVersion=$(KOGITO_VERSION)
SET_CLI_VERISON := main.cliVersion=$(CLI_VERSION)
LDFLAGS := "-X $(SET_QUARKUS_VERSION) -X $(SET_KOGITO_VERSION) -X $(SET_CLI_VERISON)"

build-all: build-linux build-darwin build-win32

build-linux:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_LINUX) cmd/main.go

build-darwin:
	CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_DARWIN) cmd/main.go

build-win32:
	CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BIN_WINDOWS) cmd/main.go

clean:
	go clean
	rm -rf $(BINARY_PATH) 
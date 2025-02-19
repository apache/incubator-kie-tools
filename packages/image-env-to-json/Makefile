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

SHELL := sh

ifeq ($(OS),Windows_NT)
    SHELL := powershell.exe
    .SHELLFLAGS := -NoProfile -ExecutionPolicy Bypass -Command
endif

BIN              := image-env-to-json
BIN_DARWIN_AMD   ?= $(BIN)-darwin-amd64
BIN_DARWIN_ARM   ?= $(BIN)-darwin-arm64
BIN_LINUX_AMD    ?= $(BIN)-linux-amd64
BIN_LINUX_ARM    ?= $(BIN)-linux-arm64
BIN_WINDOWS_AMD  ?= $(BIN)-windows-amd64.exe
BIN_WINDOWS_ARM  ?= $(BIN)-windows-arm64.exe

BIN_PATH         := dist
MAIN_PATH        := main.go

METADATA_PATH                 := github.com/apache/incubator-kie-tools/packages/image-env-to-json/internal
SET_VERSION                   := $(METADATA_PATH).Version=$(IMAGE_ENV_TO_JSON_VERSION)
LDFLAGS                       := "-X $(SET_VERSION)"

ARCH := $(shell uname -m)
ifeq ($(ARCH),arm64)
	GOARCH = arm64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_ARM)
	BIN_BUILD_LINUX=$(BIN_LINUX_ARM)
	BIN_BUILD_WINDOWS=$(BIN_WINDOWS_ARM)
else
	GOARCH = amd64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_AMD)
	BIN_BUILD_LINUX=$(BIN_LINUX_AMD)
	BIN_BUILD_WINDOWS=$(BIN_WINDOWS_AMD)
endif

build-all: build-darwin-amd64 build-darwin-arm64 build-linux-amd64 build-linux-arm64 build-win-amd64 build-win-arm64

build-darwin:
	cross-env CGO_ENABLED=0 GOOS=darwin GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_BUILD_DARWIN) $(MAIN_PATH)

build-darwin-amd64:
	cross-env CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_DARWIN_AMD) $(MAIN_PATH)

build-darwin-arm64:
	cross-env CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_DARWIN_ARM) $(MAIN_PATH)

build-linux:
	cross-env CGO_ENABLED=0 GOOS=linux GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_BUILD_LINUX) $(MAIN_PATH)

build-linux-amd64:
	cross-env CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_LINUX_AMD) $(MAIN_PATH)

build-linux-arm64:
	cross-env CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_LINUX_ARM) $(MAIN_PATH)

build-win:
	cross-env CGO_ENABLED=0 GOOS=windows GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_BUILD_WINDOWS) $(MAIN_PATH)

build-win-amd64:
	cross-env CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_WINDOWS_AMD) $(MAIN_PATH)

build-win-arm64:
	cross-env CGO_ENABLED=0 GOOS=windows GOARCH=arm64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_WINDOWS_ARM) $(MAIN_PATH)

clean:
	go clean
	rm -rf $(BIN_PATH) $(TEST_PATH)
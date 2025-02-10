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

BIN              := image-env-to-json
BIN_LINUX_AMD    ?= $(BIN)-linux-amd64
BIN_LINUX_ARM    ?= $(BIN)-linux-arm64

BIN_PATH         := ./dist
TEST_PATH        := ./dist-tests
MAIN_PATH        := ./main.go

METADATA_PATH                 := github.com/apache/incubator-kie-tools/packages/image-env-to-json/internal
SET_VERSION                   := $(METADATA_PATH).Version=$(PLUGIN_VERSION)
LDFLAGS                       := "-X $(SET_VERSION)"

ARCH := $(shell uname -m)
ifeq ($(ARCH),arm64)
	GOARCH = arm64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_ARM64)
else
	GOARCH = amd64
	BIN_BUILD_DARWIN=$(BIN_DARWIN_AMD64)
endif

build-all: build-linux-amd64 build-linux-arm64

build-linux-amd64:
	CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_LINUX_AMD) $(MAIN_PATH)

build-linux-arm64:
	CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -ldflags $(LDFLAGS) -o $(BIN_PATH)/$(BIN_LINUX_ARM) $(MAIN_PATH)

clean:
	go clean
	rm -rf $(BIN_PATH) $(TEST_PATH)
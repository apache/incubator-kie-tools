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

.PHONY: all
all: build

##@ Development
.PHONY: fmt
fmt: ## Run go fmt against code.
	go fmt ./...

.PHONY: vet
vet: ## Run go vet against code.
	go vet ./...

.PHONY: test
test: fmt vet ## Run tests.
	go test ./... -coverprofile cover.out

##@ Build

.PHONY: build
build: fmt vet ## Build manager binary.
	go build -o bin/builder main.go

.PHONY: run
run: fmt vet ## Run a controller from your host.
	go run ./main.go

##@ Build Dependencies

.PHONY: clean
clean:
	rm -rf bin/

.PHONY: docker-integration-test
docker-integration-test: ## Test docker integration tests
	go test ./... -tags integration_docker

.PHONY: kaniko-docker-integration-test
kaniko-docker-integration-test: ## Test kaniko integration docker tests
	go test ./... -tags integration_kaniko_docker

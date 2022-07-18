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

BINARY_NAME=kn-workflow

QUARKUS_VERSION := $(shell pnpm build-env knPluginWorkflow.quarkusVersion)
KOGITO_VERSION := $(shell pnpm build-env knPluginWorkflow.kogitoVersion)
CLI_VERSION := $(shell pnpm build-env global.version)

SET_QUARKUS_VERSION := github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/cmd.quarkusVersion=$(QUARKUS_VERSION)
SET_KOGITO_VERSION := github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/cmd.kogitoVersion=$(KOGITO_VERSION)
SET_CLI_VERISON := github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/cmd.cliVersion=$(CLI_VERSION)
LDFLAGS := "-X $(SET_QUARKUS_VERSION) -X $(SET_KOGITO_VERSION) -X $(SET_CLI_VERISON)"

build-linux:
	GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BINARY_NAME)-linux-amd64 cmd/main.go

build-darwin:
	GO111MODULE=on GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BINARY_NAME)-darwin-amd64 cmd/main.go

build-win32:
	GO111MODULE=on GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o ./dist/$(BINARY_NAME)-windows-amd64.exe cmd/main.go

clean:
	go clean
	rm -rf $(BINARY_PATH) 
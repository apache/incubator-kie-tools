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

METADATA_PATH                                               := github.com/apache/incubator-kie-tools/examples/kie-sandbox-commit-message-validation-service/pkg/metadata
SET_COMMIT_MESSAGE_VALIDATION_SERVICE_PORT                  := $(METADATA_PATH).Port=$(shell build-env kieSandboxCommitMessageValidationServiceExample.port)
SET_COMMIT_MESSAGE_VALIDATION_SERVICE_ENABLED_VALIDATORS    := $(METADATA_PATH).Validators=$(shell build-env kieSandboxCommitMessageValidationServiceExample.validators)
LDFLAGS                                                     := "-X $(SET_COMMIT_MESSAGE_VALIDATION_SERVICE_PORT) -X $(SET_COMMIT_MESSAGE_VALIDATION_SERVICE_ENABLED_VALIDATORS)"

ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
    GOARCH = amd64
else
    GOARCH = arm64
endif

darwin:
    GOOS=darwin GOARCH=$(GOARCH) CGO_ENABLED=1 go build -ldflags $(LDFLAGS) -o dist/darwin/commit-message-validation-service main.go

darwin-amd64:
    GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/darwin/commit-message-validation-service main.go

darwin-arm64:
    GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/darwin/commit-message-validation-service main.go

linux:
    GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/linux/commit-message-validation-service main.go

win32:
    GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/win32/commit-message-validation-service.exe main.go
        
start:
    go run -ldflags $(LDFLAGS) main.go
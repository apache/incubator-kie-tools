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

LDFLAGS := "-w"
DDUS_VERSION?=0.0.0

build-all: linux-amd64 linux-arm64 darwin-amd64 darwin-arm64 win32-amd64 checksum

linux-amd64:
	cross-env CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-linux-amd64 main.go
	tar -zcvf dist/dev-deployment-upload-service-linux-amd64-$(DDUS_VERSION).tar.gz -C dist dev-deployment-upload-service-linux-amd64

linux-arm64:
	cross-env CGO_ENABLED=0 GOOS=linux GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-linux-arm64 main.go
	tar -zcvf dist/dev-deployment-upload-service-linux-arm64-$(DDUS_VERSION).tar.gz -C dist dev-deployment-upload-service-linux-arm64

darwin-amd64:
	cross-env CGO_ENABLED=0 GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-darwin-amd64 main.go
	tar -zcvf dist/dev-deployment-upload-service-darwin-amd64-$(DDUS_VERSION).tar.gz -C dist dev-deployment-upload-service-darwin-amd64

darwin-arm64:
	cross-env CGO_ENABLED=0 GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-darwin-arm64 main.go
	tar -zcvf dist/dev-deployment-upload-service-darwin-arm64-$(DDUS_VERSION).tar.gz -C dist dev-deployment-upload-service-darwin-arm64

win32-amd64:
	cross-env CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-windows-amd64.exe main.go
	tar -zcvf dist/dev-deployment-upload-service-windows-amd64-$(DDUS_VERSION).tar.gz -C dist dev-deployment-upload-service-windows-amd64.exe

checksum:
ifeq ($(OS),Windows_NT)
	powershell -NoProfile -ExecutionPolicy Unrestricted -Command "./scripts/generateChecksumsWindows.ps1"
else
	for f in $$(find dist/*.tar.gz -type f 2>/dev/null) ; do \
		shasum -a 256 "$${f}" | awk '{print $$1}' > "$${f}.sha256" ; \
	done
endif

start:
	go run -ldflags $(LDFLAGS) main.go
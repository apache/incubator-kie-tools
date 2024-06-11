METADATA_PATH                                     					:= github.com/apache/incubator-kie-tools/examples/commit-message-validation-service/pkg/metadata
SET_COMMIT_MESSAGE_VALIDATION_SERVICE_PORT                 	:= $(METADATA_PATH).Port=$(shell build-env commitMessageValidationService.port)
SET_COMMIT_MESSAGE_VALIDATION_SERVICE_ENABLED_VALIDATORS   	:= $(METADATA_PATH).Validators=$(shell build-env commitMessageValidationService.validators)
LDFLAGS                                           					:= "-X $(SET_COMMIT_MESSAGE_VALIDATION_SERVICE_PORT) -X $(SET_COMMIT_MESSAGE_VALIDATION_SERVICE_ENABLED_VALIDATORS)"

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
METADATA_PATH                 := github.com/kiegroup/kie-tools/packages/extended-services/pkg/metadata
SET_EXTENDED_SERVICES_VERSION := $(METADATA_PATH).Version=$(shell pnpm build-env extendedServices.version)
SET_EXTENDED_SERVICES_IP      := $(METADATA_PATH).IP=$(shell build-env extendedServices.serverIp)
SET_EXTENDED_SERVICES_PORT    := $(METADATA_PATH).Port=$(shell build-env extendedServices.serverPort)
SET_EXTENDED_SERVICES_MODELER := $(METADATA_PATH).Modeler=$(shell build-env extendedServices.modeler)
LDFLAGS                       := "-X $(SET_EXTENDED_SERVICES_VERSION) -X $(SET_EXTENDED_SERVICES_IP) -X $(SET_EXTENDED_SERVICES_PORT) -X $(SET_EXTENDED_SERVICES_MODELER)"

ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
	GOARCH = amd64
else
	GOARCH = arm64
endif

darwin:
	GOOS=darwin GOARCH=$(GOARCH) go build -ldflags $(LDFLAGS) -o dist/darwin/kie_sandbox_extended_services main.go

darwin-amd64:
	GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/darwin/kie_sandbox_extended_services main.go

darwin-arm64:
	GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/darwin/kie_sandbox_extended_services main.go

linux:
	GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/linux/kie_sandbox_extended_services main.go

linux-headless:
	GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -tags headless -o dist/linux/kie_sandbox_extended_services_headless main.go

win32:
	GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/win32/kie_sandbox_extended_services.exe main.go
	
start:
	go run -ldflags $(LDFLAGS) main.go
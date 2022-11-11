ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
	GOARCH = amd64
else
	GOARCH = arm64
endif

darwin:
	GOOS=darwin GOARCH=$(GOARCH) go build -o dist/darwin/kie_sandbox_extended_services main.go

darwin-amd64:
	GOOS=darwin GOARCH=amd64 go build -o dist/darwin/kie_sandbox_extended_services main.go

darwin-arm64:
	GOOS=darwin GOARCH=arm64 go build -o dist/darwin/kie_sandbox_extended_services main.go

linux:
	GOOS=linux GOARCH=amd64 go build -o dist/linux/kie_sandbox_extended_services main.go

linux-headless:
	GOOS=linux GOARCH=amd64 go build -tags headless -o dist/linux/kie_sandbox_extended_services_headless main.go

win32:
	GOOS=windows GOARCH=amd64 go build -o dist/win32/kie_sandbox_extended_services.exe main.go
	
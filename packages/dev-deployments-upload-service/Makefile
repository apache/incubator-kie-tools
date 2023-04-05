LDFLAGS := ""

ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
	GOARCH = amd64
else
	GOARCH = arm64
endif

darwin:
	GOOS=darwin GOARCH=$(GOARCH) CGO_ENABLED=1 go build -ldflags $(LDFLAGS) -o dist/darwin/dev-deployments-upload-service main.go

darwin-amd64:
	GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/darwin/dev-deployments-upload-service main.go

darwin-arm64:
	GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/darwin/dev-deployments-upload-service main.go

linux:
	GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/linux/dev-deployments-upload-service main.go

win32:
	GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/win32/dev-deployments-upload-service.exe main.go
	
start:
	go run -ldflags $(LDFLAGS) main.go
LDFLAGS := ""

ARCH := $(shell uname -m)
ifeq ($(ARCH),x86_64)
	GOARCH = amd64
else
	GOARCH = arm64
endif

darwin:
	cross-env GOOS=darwin GOARCH=$(GOARCH) CGO_ENABLED=1 go build -ldflags $(LDFLAGS) -o dist/darwin/dev-deployments-upload-service main.go

darwin-amd64:
	cross-env GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/darwin/amd64/dev-deployments-upload-service main.go

darwin-arm64:
	cross-env GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/darwin/arm64/dev-deployments-upload-service main.go

linux:
	cross-env GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/linux/dev-deployments-upload-service main.go

win32:
	cross-env GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/win32/dev-deployments-upload-service.exe main.go
	
start:
	go run -ldflags $(LDFLAGS) main.go
LDFLAGS := "-w"

darwin-amd64:
	cross-env GOOS=darwin GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-darwin-amd64 main.go
	tar -zcvf dist/dev-deployment-upload-service-darwin-amd64.tar.gz -C dist dev-deployment-upload-service-darwin-amd64

darwin-arm64:
	cross-env GOOS=darwin GOARCH=arm64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-darwin-arm64 main.go
	tar -zcvf dist/dev-deployment-upload-service-darwin-arm64.tar.gz -C dist dev-deployment-upload-service-darwin-arm64

linux-amd64:
	cross-env GOOS=linux GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-linux-amd64 main.go
	tar -zcvf dist/dev-deployment-upload-service-linux-amd64.tar.gz -C dist dev-deployment-upload-service-linux-amd64

windows-amd64:
	cross-env GOOS=windows GOARCH=amd64 go build -ldflags $(LDFLAGS) -o dist/dev-deployment-upload-service-windows-amd64.exe main.go
	tar -zcvf dist/dev-deployment-upload-service-windows-amd64.tar.gz -C dist dev-deployment-upload-service-windows-amd64.exe

checksum:
ifeq ($(OS),Windows_NT)
	echo "Nothing to do"
else
	for f in $$(find dist/*.tar.gz -type f 2>/dev/null) ; do \
		shasum -a 256 "$${f}" | sed 's/  .*\//  /' > "$${f}.sha256sum" ; \
		shasum -a 256 "$${f}" | awk '{print $$1}' > "$${f}.sha256" ; \
	done
endif
	
start:
	go run -ldflags $(LDFLAGS) main.go
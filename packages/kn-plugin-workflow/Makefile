all:
	GO111MODULE=on GOOS=windows GOARCH=amd64 go build -ldflags "-X 'github.com/kiegroup/kie-tools/kn-plugin-workflow/pkg/common.QUARKUS_VERSION=$(build-env knPluginWorkflow.quarkusVersion)'" -o dist/kn-workflow-windows-amd64.exe cmd/main.go

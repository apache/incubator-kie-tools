.PHONY: all
all: build

##@ Development
.PHONY: fmt
fmt: ## Run go fmt against code.
	go fmt ./...

.PHONY: vet
vet: ## Run go vet against code.
	go vet ./...

.PHONY: test
test: fmt vet ## Run tests.
	go test ./... -coverprofile cover.out

##@ Build

.PHONY: build
build: fmt vet ## Build manager binary.
	go build -o bin/builder main.go

.PHONY: run
run: fmt vet ## Run a controller from your host.
	go run ./main.go

##@ Build Dependencies

.PHONY: clean
clean:
	rm -rf bin/

.PHONY: docker-integration-test
docker-integration-test: ## Test docker integration tests
	go test ./... -tags integration_docker -v

.PHONY: kaniko-docker-integration-test
kaniko-docker-integration-test: ## Test kaniko integration docker tests
	go test ./... -tags integration_kaniko_docker

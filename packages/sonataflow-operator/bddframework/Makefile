.PHONY: all
all: test

##@ Development

.PHONY: fmt
fmt: ## Run go fmt against code.
	go mod tidy
	go fmt ./...

.PHONY: vet
vet: ## Run go vet against code.
	go vet ./...

.PHONY: test
test: fmt vet ## Run tests.
	go test ./... -v

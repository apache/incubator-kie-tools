TEST_PARAMS=$(foreach V,$(.VARIABLES),$(if $(filter command line, $(origin $V)),--$(V) "$($(V))"))

.PHONY: fmt
fmt: ## Run go fmt against code.
	go mod tidy
	go fmt ./...

.PHONY: vet
vet: ## Run go vet against code.
	go vet ./...

.PHONY: update-modules
update-modules: fmt vet

.PHONY: run-tests
run-tests: update-modules
	../hack/run-tests.sh $(TEST_PARAMS)

.PHONY: run-smoke-tests
run-smoke-tests: update-modules
	$(MAKE) run-tests smoke=true

.PHONY: clean-logs
clean-logs:
	rm -rf logs/


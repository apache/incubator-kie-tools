TEST_PARAMS=$(foreach V,$(.VARIABLES),$(if $(filter command line, $(origin $V)),--$V $($V)))

update-modules:
	go mod tidy

.PHONY: run-tests
run-tests: update-modules
	../hack/run-tests.sh $(shell echo $(TEST_PARAMS) | xargs)

.PHONY: build-examples-images
build-examples-images: update-modules
	make run-tests test_main_dir=./examples feature=../features cr_deployment_only=true

.PHONY: run-smoke-tests
run-smoke-tests: update-modules
	make run-tests smoke=true

.PHONY: build-smoke-examples-images
build-smoke-examples-images: update-modules
	make build-examples-images smoke=true

.PHONY: run-performance-tests
run-performance-tests: update-modules
	make run-tests performance=true

.PHONY: build-performance-examples-images
build-performance-examples-images: update-modules
	make build-examples-images performance=true
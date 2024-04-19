.PHONY: test
test:
	go test $(shell go list ./... | grep -v /test/) -coverprofile cover.out

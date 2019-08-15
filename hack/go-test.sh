#!/bin/sh

if [[ -z ${CI} ]]; then
    ./hack/go-vet.sh
    ./hack/go-fmt.sh
    ./hack/go-lint.sh
fi
go test ./pkg/... ./cmd/... -count=1

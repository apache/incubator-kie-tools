#!/bin/sh

. ./hack/go-mod-env.sh

if [[ -z ${CI} ]]; then
    ./hack/go-vet.sh
    ./hack/go-fmt.sh
    ./hack/go-lint.sh
fi
setGoModEnv
go test -mod=vendor ./pkg/... ./cmd/... -count=1

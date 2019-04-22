#!/bin/sh

if [[ -z ${CI} ]]; then
    ./hack/go-dep.sh
    operator-sdk generate k8s
fi
go vet ./...
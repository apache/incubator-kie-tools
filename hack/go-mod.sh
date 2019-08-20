#!/bin/sh

. ./hack/go-mod-env.sh

echo Resetting vendor directory

setGoModEnv

go mod tidy
go mod vendor

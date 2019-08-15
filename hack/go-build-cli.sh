#!/bin/sh

CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -v -a -o build/_output/bin/kogito github.com/kiegroup/kogito-cloud-operator/cmd/kogito
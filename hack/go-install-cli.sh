#!/bin/sh
cd cmd/kogito || exit
CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go install
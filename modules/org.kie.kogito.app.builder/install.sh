#!/bin/sh
set -e

cd /workspace
CGO_ENABLED=0 GO111MODULE=on go build -a -o manager main.go;
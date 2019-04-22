#!/bin/sh

DEP_VERSION=v0.5.1

if [[ -z ${CI} ]]; then
    dep ensure -v
else
    if ! command -v dep 2> /dev/null; then
        curl -L -s https://github.com/golang/dep/releases/download/${DEP_VERSION}/dep-linux-amd64 -o /go/bin/dep
        chmod +x /go/bin/dep
    fi
    dep check
fi
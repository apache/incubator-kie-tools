#!/bin/sh

gofmt -s -l -w cmd/ pkg/ version/

if [[ -n ${CI} ]]; then
    git diff --exit-code
fi

#!/bin/sh
set -e

cd $REMOTE_SOURCE_DIR/app
source $CACHITO_ENV_FILE && go build -a -o manager main.go
mkdir /workspace && cp $REMOTE_SOURCE_DIR/app/manager /workspace && mkdir /workspace/resources && cp -rf $REMOTE_SOURCE_DIR/app/resources/* /workspace/resources
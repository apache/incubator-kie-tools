#!/usr/bin/env bash

# Post Hook script that should be called by Kogito Service Kubernetes Pod upon initialization
# Updates the Kogito Service protobuf configMap with the persistence files located at ${KOGITO_HOME}/data/protobufs

source $KOGITO_HOME/launch/kogito-persistence.sh

update_configmap
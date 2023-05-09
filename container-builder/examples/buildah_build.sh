#!/bin/sh
buildah bud --ulimit nofile=262144:262144 -t localhost:5000/kiegroup/buildah-bash:latest ./../examples/dockerfiles/Kogito.dockerfile
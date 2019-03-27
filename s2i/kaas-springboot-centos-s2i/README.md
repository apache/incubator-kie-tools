# S2I image to build builder image based on Centos 7

This S2I is considered image builder that is equipped with build tools

- OpenJDK 1.8
- Apache Maven

So it is equipped with all required tools to build a runnable fat jar of
KaaS (Knowledge as a Service) type of projects.

## Build the image

To be able to build the image a docker should be installed.

Setup environment

`eval $(minishift docker-env)`

Login to docker registry

`docker login -u developer -p $(oc whoami -t) $(minishift openshift registry)`

Built the image

`make`

(this will run a docker build and produce a image builder)

Tag the built image

`docker tag {IMAGE_ID} $(minishift openshift registry)/{PROJECT}/kaas-springboot-centos-s2i`

Modify accordingly
- IMAGE_ID - use the hash from the image produced by the make command (`docker images` to view images)
- PROJECT - OpenShift project that the image should be pushed to

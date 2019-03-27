# S2I image to build runtime image based on Centos 7

This S2I is considered a runtime image builder as it relies on another image
(built with `kaas-quarkus-centos-s2i`) that actually produced the binaries to be executed
(the native image based on Quarkus runtime). So this one will only get the binaries
and place into the container.

With that it produces lightweight and compact application container image.

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

`docker tag {IMAGE_ID} $(minishift openshift registry)/{PROJECT}/kaas-quarkus-centos`

Modify accordingly
- IMAGE_ID - use the hash from the image produced by the make command (`docker images` to view images)
- PROJECT - OpenShift project that the image should be pushed to

# S2I image to build runtime image based on Centos 7

This S2I is considered a runtime image builder as it relies on another image
(built with `kaas-springboot-centos-s2i`) that actually produced the binaries to be executed
(the fat jar based on SpringBoot 2.1.x runtime). So this one will only get the binaries
and place into the container.

On top of the OS it has JRE installed (OpenJDK 1.8) to be able to execute fat jar.

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

`docker tag {IMAGE_ID} $(minishift openshift registry)/{PROJECT}/kaas-springboot-centos`

Modify accordingly
- IMAGE_ID - use the hash from the image produced by the make command (`docker images` to view images)
- PROJECT - OpenShift project that the image should be pushed to

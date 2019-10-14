# Kogito S2I images

To be able to produce efficient container images for Kogito there are following sets
of builder images to perform the build and to run the result binaries.

Images are grouped by the runtime that will run the binaries and the OS level


## Quarkus

### kogito-quarkus-ubi8-s2i

Builder image that is responsible for building the project
with Apache Maven and generate native image using GraalVM/SubstrateVM

Image location: quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest

For more details have a look at [README.md](modules/kogito-quarkus-ubi8-s2i/README.md)

### kogito-quarkus-ubi8

Runtime image that is responsible for just running the binaries taken from the
builder image. That approach is giving small and compact image that does not
not carry on any of the build tools or artefacts (like local maven repository).

Image location: quay.io/kiegroup/kogito-quarkus-ubi8:latest

For more details have a look at [README.md](modules/kogito-quarkus-ubi8/README.md)

## SpringBoot

### kogito-springboot-ubi8-s2i

Builder image that is responsible for building the project
with Apache Maven and generate fat jar.

Image location: quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest

For more details have a look at [README.md](modules/kogito-springboot-ubi8-s2i/README.md)

### kogito-springboot-ubi8

Runtime image that is responsible for just running the fat jar taken from the
builder image. It has JRE installed on the container to allow java executable.
That approach is giving small and compact image that does not
not carry on any of the build tools or artefacts (like local maven repository).

Image location: quay.io/kiegroup/kogito-springboot-ubi8:latest

For more details have a look at [README.md](modules/kogito-springboot-ubi8/README.md)

## Data Index Service

Runtime image that is responsibile for just running the [Data Index Service](https://github.com/kiegroup/kogito-runtimes/wiki/Data-Index-Service). Solely used by the [Kogito Operator](https://github.com/kiegroup/kogito-cloud-operator) to deploy data index capabilities to Kogito Services.

# Supported source structure

Images can be build based on two types of source structure

- assets only
- kjar maven project

## Assets only

This source structure assumes there is no maven project
but only business assets stored either directly in the top folder or grouped into directories.

Business assets are:

- process definition - bpmn2
- rule definition - drl
- decision definition - dmn
- etc

Upon build these assets will be copied to generated kjar maven project and build with maven to produce the runnable binary.

## Kjar maven project

This source structure is expected to be valid kjar project equipped with runtime information - either Quarkus or Spring Boot.

Best way is to use maven archetypes to generate project structure (same archetypes are used in cases assets only are used as source).

[Kogito Quarkus archetype](https://github.com/kiegroup/kogito-runtimes/tree/master/archetypes/kogito-quarkus-archetype)

[Kogito SpringBoot archetype](https://github.com/kiegroup/kogito-runtimes/tree/master/archetypes/kogito-springboot-archetype)

# Example usage

## Build the application

Once the images are built and imported into registry (docker hub or internal OpenShift registry)
new applications can be build and deployed with this few steps

`oc new-build quay.io/kiegroup/kogito-quarkus-ubi8-s2i~https://github.com/user/project --name=builder-app-name`

Modify accordingly following
- repository is the docker repository the images are available in (could be OpenShift project or docker hub user)
- user is user name in github
- project is project in github
- builder-app-name is name of the resulting build image that will be referenced in next step to build the runtime image

## Build runtime image of the application

Once the build is finished, create another build config to produce runtime image

`oc new-build --name app-name --source-image=builder-app-name --source-image-path=/home/kogito/bin:. --image-stream=kogito-quarkus-ubi8`

Modify accordingly following
- app-name is the final name of the image that will be deployed
- builder-app-name is the name of the built image from first step

This build is very fast and should produce rather small image (below 100mb).

## Create new application from the runtime image

Create new application based on runtime image

`oc new-app app-name:latest`

Modify accordingly following
- app-name is the final name of the image that will be deployed

## Make the application available to users

Expose the application via route

`oc expose svc/app-name`

Modify accordingly following
- app-name is the name of the application that was deployed

# Subsequent builds of the application

Once the build configs are in place there is only one command that is needed to produce
new application image (the runtime one)

`oc start-build builder-app-name`

Modify accordingly following
- builder-app-name is name of the resulting build image that was built in the first step.

This will get the latest source and run the builder image first. Once the build
is completed it will automatically trigger building of the runtime image and next
automatic deployment of the runtime image.

## Improve speed of the builds

Time needed to build the application is rather long, mainly due to
maven downloading dependencies for the build which usually takes several minutes.

That can be improved by using incremental builds

`oc start-build builder-app-name --incremental=true`

Modify accordingly following
- builder-app-name is name of the resulting build image that was built in the first step.

what this does ... essentially reuses previously build image and takes advantage of
already downloaded artifacts. By that improving overall build time significantly.

You can also use a maven mirror, if available on your internal network, just set the
MAVEN_MIRROR_URL environment variable when starting a new build.

## Building the images locally:

CeKit3 is required to build the images, how to install: https://docs.cekit.io/en/latest/handbook/installation/instructions.html

To build all images:
```bash
$ make
```

To build a single image, use `make build image-name`, example:
```bash
$ make kogito-quarkus-ubi8
```

Testing the images (WIP):
```bash
$ make test
```

And finally, to push the images for the quay.io/kiegroup registry, this step requires permission under the kiegroup
organization on quay.io.:
```bash
 make push
 ```

##### Pushing the built images to a local OCP registry:
To be able to build the image a docker should be installed.

Setup environment

```bash
$ eval $(minishift docker-env)
 ```

Login to docker registry

```bash
$ docker login -u developer -p $(oc whoami -t) $(minishift openshift registry)
```

(this will run a docker build and produce a image builder)

Tag the built image, example
```bash
$ docker tag {IMAGE_ID} $(minishift openshift registry)/{PROJECT}/kogito-quarkus-ubi8
```

Modify accordingly
 - IMAGE_ID - use the hash from the image produced by the make command (`docker images` to view images)
    * Note that, you can also use the image name:tag.
 - PROJECT - OpenShift project that the image should be pushed to

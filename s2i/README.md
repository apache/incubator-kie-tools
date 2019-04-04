# Submarine S2I images

To be able to produce efficient container images for Submarine there are following sets
of builder images to perform the build and to run the result binaries.

Images are grouped by the runtime that will run the binaries and the OS level

## Centos 7

## Quarkus

### kaas-quarkus-centos-s2i

Builder image that is responsible for building the project
with Apache Maven and generate native image using GraalVM/SubstrateVM

For more details have a look at [README.md](kaas-quarkus-centos-s2i/README.md)

### kaas-quarkus-centos

Runtime image that is responsible for just running the binaries taken from the
builder image. That approach is giving small and compact image that does not
not carry on any of the build tools or artefacts (like local maven repository).

For more details have a look at [README.md](kaas-quarkus-centos/README.md)

## SpringBoot

### kaas-springboot-centos-s2i

Builder image that is responsible for building the project
with Apache Maven and generate fat jar.

For more details have a look at [README.md](kaas-springboot-centos-s2i/README.md)

### kaas-springboot-centos

Runtime image that is responsible for just running the fat jar taken from the
builder image. It has JRE installed on the container to allow java executable.
That approach is giving small and compact image that does not
not carry on any of the build tools or artefacts (like local maven repository).

For more details have a look at [README.md](kaas-springboot-centos/README.md)

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

[KaaS Quarkus archetype](https://github.com/kiegroup/submarine-runtimes/tree/master/archetypes/kaas-quarkus-archetype)

[KaaS SpringBoot archetype](https://github.com/kiegroup/submarine-runtimes/tree/master/archetypes/kaas-springboot-archetype)

# Example usage

## Build the application

Once the images are built and imported into registry (docker hub or internal OpenShift registry)
new applications can be build and deployed with this few steps

`oc new-build repository/kaas-quarkus-centos-s2i~https://github.com/user/project --name=builder-app-name`

Modify accordingly following
- repository is the docker repository the images are available in (could be OpenShift project or docker hub user)
- user is user name in github
- project is project in github
- builder-app-name is name of the resulting build image that will be referenced in next step to build the runtime image

## Build runtime image of the application

Once the build is finished, create another build config to produce runtime image

`oc new-build --name app-name --source-image=builder-app-name --source-image-path=/home/submarine/bin:. --image-stream=kaas-quarkus-centos`

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
already downloaded artefacts. By that improving overall build time significantly.

<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

Kogito
------

**Kogito** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

<p align="center"><img width=55% height=55% src="docsimg/kogito.png"></p>

[![GitHub Stars](https://img.shields.io/github/stars/apache/incubator-kie-kogito-images.svg)](https://github.com/apache/incubator-kie-kogito-images/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/apache/incubator-kie-kogito-images.svg)](https://github.com/apache/incubator-kie-kogito-images/network/members)
[![Pull Requests](https://img.shields.io/github/issues-pr/apache/incubator-kie-kogito-images.svg?style=flat-square)](https://github.com/apache/incubator-kie-kogito-images/pulls)
[![Contributors](https://img.shields.io/github/contributors/apache/incubator-kie-kogito-images.svg?style=flat-square)](https://github.com/apache/incubator-kie-kogito-images/graphs/contributors)
[![License](https://img.shields.io/github/license/apache/incubator-kie-kogito-images.svg)](https://github.com/apache/incubator-kie-kogito-images/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)


# Kogito Container Images

To be able to efficiently execute Kogito services on the Cloud there's a need to have Container Images so it can be played
smoothly on any Kubernetes cluster. There are a few sets images which are divided in three different groups which are 
the components, the builder images and the runtime images.


Table of Contents
=================

- [Kogito Container Images](#kogito-container-images)
- [Table of Contents](#table-of-contents)
  - [Kogito Images Requirements](#kogito-images-requirements)
  - [Kogito Images JVM Memory Management](#kogito-images-jvm-memory-management)
  - [Kogito Component Images](#kogito-component-images)
    - [Kogito Data Index Component Images](#kogito-data-index-component-images)
    - [Kogito Jobs Service Component Images](#kogito-jobs-service-component-images)
      - [Jobs Services All-in-one](#jobs-services-all-in-one)
    - [Kogito JIT Runner Component Image](#kogito-jit-runner-component-image)
  - [Contributing to Kogito Images repository](#contributing-to-kogito-images-repository)
    - [Building Images](#building-images)
      - [Image Modules](#image-modules)
    - [Testing Images](#testing-images)
      - [Behave tests](#behave-tests)
        - [Running Behave tests](#running-behave-tests)
        - [Writing Behave tests](#writing-behave-tests)
      - [Bats tests](#bats-tests)
        - [Running Bats tests](#running-bats-tests)
        - [Writing Bats tests](#writing-bats-tests)
    - [Reporting new issues](#reporting-new-issues)

         
## Kogito Images Requirements 

To interact with Kogito images, you would need to install the needed dependencies so that the images can be built and tested.

* Mandatory dependencies:
    * Moby Engine or Docker CE
        * Podman can be use to build the images, but at this moment CeKit does not support it, so images build with podman
            cannot be tested with CeKit.
    * [CeKit 4.8.0+](https://docs.cekit.io/en/latest/): 
        * CeKit also has its own dependencies:
            * python packages: docker, docker-squash, odcs-client. 
            * All of those can be handled with pip, including CeKit.
            * if any dependency is missing CeKit will tell which one.
    * [Bats](https://github.com/sstephenson/bats) 
    * Java 17 or higher
    * Maven 3.9.3 or higher
            
* Optional dependencies:
    * [source-to-image](https://github.com/openshift/source-to-image)
        * used to perform local s2i images using some of the [builder images](#builder-images)
    * [GraalVM 23+](https://github.com/graalvm/mandrel/releases) Java 17 or higher
        * Useful to test Kogito apps on native mode before create a Container image with it.
    * [OpenShift Cli](https://docs.openshift.com/container-platform/4.3/cli_reference/openshift_cli/getting-started-cli.html)
        

## Kogito Images JVM Memory Management

All the Kogito Container Images contains a base module that will calculate the JVM max (Xmx) and min (Xms) values based
on the container memory limits. To auto tune it, you can use the following environment variables to instruct the scripts
what value the min and max should have:

- JAVA_MAX_MEM_RATIO: Is used when no `-Xmx` option is given in **JAVA_OPTIONS**. This is used to calculate a default 
  maximal heap memory based on a containers restriction. If used in a container without any memory constraints for the
  container then this option has no effect. If there is a memory constraint then `-Xmx` is set to a ratio of the 
  container available memory as set here. The default is `50` which means 50% of the available memory is used as an 
  upper boundary. You can skip this mechanism by setting this value to `0` in which case no `-Xmx` option is added. 

- JAVA_INITIAL_MEM_RATIO: Is used when no `-Xms` option is given in **JAVA_OPTIONS**. This is used to calculate a 
  default initial heap memory based on the maximum heap memory. If used in a container without any memory constraints 
  for the container then this option has no effect. If there is a memory constraint then `-Xms` is set to a ratio 
  of the `-Xmx` memory as set here. The default is `25` which means 25% of the `-Xmx` is used as the initial heap size. 
  You can skip this mechanism by setting this value to `0` in which case no `-Xms` option is added.

For a complete list ov environment variables that can be used to configure the JVM, please check the [dynamic resources](modules/kogito-dynamic-resources/module.yaml) module      
  
When performing Quarkus native builds, by default, it will rely on the cgroups memory report to determine the amount of memory
that will be used by the builder container. On OpenShift or k8s, it can be defined by setting the memory limit.
The build process will use 80% of the total memory reported by cgroups. For backwards compatibility, the env
`LIMIT_MEMORY` will be respected, but it is recommended unset it and let the memory be calculated automatic based
on the available memory, it can be used in specific scenarios, like a CI test where it does not run on OpenShift cluster.

#### SonataFlow Builder Image usage

##### Using as a builder

The main purpose of this image is to be used within the Kogito Serverless Operator as a builder image, below you can find
an example on how to use it:

```bash
FROM quay.io/kiegroup/kogito-swf-builder:latest AS builder

# Copy all files from current directory to the builder context
COPY * ./resources/

# Build app with given resources
RUN "${KOGITO_HOME}"/launch/build-app.sh './resources'
#=============================
# Runtime Run
CMD /usr/bin/java -jar target/quarkus-app/quarkus-run.jar
#=============================
```

##### Using for application development

If you run the image, it will start an empty [Kogito Serverless Workflow](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/index.html) application with [Quarkus Devmode](https://quarkus.io/guides/maven-tooling#dev-mode). This allows you to develop and to run quick tests locally without having to setup Maven or Java on your machine. You can have your workflows in your local file system mounted in the image so that you can see test the application live.

To run the image for testing your local workflow files, run:

```shell
docker run -it --rm -p 8080:8080 -v <local_workflow_path>:/home/kogito/serverless-workflow-project/src/main/resources/workflows quay.io/kiegroup/kogito-swf-builder:latest
```

Replace `<local_workflow_path>` with your local filesystem containing your workflow files. You can test with the [example application](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/getting-started/create-your-first-workflow-service.html#proc-creating-workflow).

After the image bootstrap, you can access [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui) and test the workflow application right away!

##### Using the SonataFlow Builder Image nightly image

The nightly builder image has been built and optimized with an internal nightly build of the Quarkus Platform.  
There are 2 environment variables that should not be changed when using it:

- QUARKUS_PLATFORM_VERSION = kogito-${KOGITO_VERSION}
- MAVEN_REPO_URL = https://repository.jboss.org/nexus/content/repositories/kogito-internal-repository/

That way, no new artifacts will be downloaded and you can directly use it.

## Kogito Component Images

The Kogito Component Images can be considered as lightweight images that will complement the Kogito core engine 
by providing extra capabilities, like managing the processes on a web UI or providing persistence layer to the Kogito applications.
Today we have the following Kogito Component Images:

* [quay.io/kiegroup/kogito-data-index-ephemeral](https://quay.io/kiegroup/kogito-data-index-ephemeral)
* [quay.io/kiegroup/kogito-data-index-postgresql](https://quay.io/kiegroup/kogito-data-index-postgresql)
* [quay.io/kiegroup/kogito-jobs-service-ephemeral](https://quay.io/kiegroup/kogito-jobs-service-ephemeral) 
* [quay.io/kiegroup/kogito-jobs-service-postgresql](https://quay.io/kiegroup/kogito-jobs-service-postgresql)
* [quay.io/kiegroup/kogito-jobs-service-allinone](https://quay.io/kiegroup/kogito-jobs-service-allinone)
* [quay.io/kiegroup/kogito-jit-runner](https://quay.io/kiegroup/kogito-jit-runner)


### Kogito Data Index Component Images

The Data Index Service aims at capturing and indexing data produced by one more Kogito runtime services. 
For more information please visit this (link)(https://docs.jboss.org/kogito/release/latest/html_single/#proc-kogito-travel-agency-enable-data-index_kogito-deploying-on-openshift). 
The Data Index Service depends on a PostgreSQL instance.
The Persistence service can be switched by using its corresponding image

- Ephemeral PostgreSQL: quay.io/kiegroup/kogito-data-index-ephemeral
  [image.yaml](kogito-data-index-ephemeral-image.yaml)
- PostgreSQL: quay.io/kiegroup/kogito-data-index-postgresql
  [image.yaml](kogito-data-index-postgresql-image.yaml)

Basic usage with Ephemeral PostgreSQL:
```bash
$ docker run -it quay.io/kiegroup/kogito-data-index-ephemeral:latest
```

Basic usage with PostgreSQL:
```bash
$ docker run -it --env QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://localhost:5432/quarkus"  \
    --env QUARKUS_DATASOURCE_USERNAME="kogito" \
    --env QUARKUS_DATASOURCE_PASSWORD="secret" \
    quay.io/kiegroup/kogito-data-index-postgresql:latest
```

To enable debug just use this env while running this image:

```bash
$ docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-data-index-postgresql:latest
```
You should notice a few debug messages present in the system output.


The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Data Index Service 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Jobs Service Component Images

The Kogito Jobs Service is a dedicated lightweight service responsible for scheduling jobs that aim at firing at a given time. 
It does not execute the job itself, but it triggers a callback that could be an HTTP request on a given endpoint specified 
on the job request, or any other callback that could be supported by the service. 
For more information please visit this [link](https://github.com/apache/incubator-kie-kogito-runtimes/wiki/Job-Service).

Today, the Jobs service contains four images:

- [ephemeral](kogito-jobs-service-ephemeral-image.yaml)
- [postgresql](kogito-jobs-service-postgresql-image.yaml)
- [all-in-one](kogito-jobs-service-allinone-image.yaml)

Basic usage:

```bash
$ docker run -it quay.io/kiegroup/kogito-jobs-service-ephemeral:latest
```

To enable debug on the Jobs Service images, set the ` SCRIPT_DEBUG` to `true`, example: 

```bash
docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-jobs-service-postgresql:latest
```

You should notice a few debug messages being printed in the system output.

The ephemeral image does not have external dependencies like a backend persistence provider, it uses in-memory persistence
while working with Jobs Services `postgresql` variant, it will need to have a PostgreSQL server previously running.

#### Jobs Services All-in-one 

The Jobs Services All in One image provides the option to run any supported variant that we have at disposal, which are:

- PostgreSQL
- Ephemeral (default if no variant is specified)

There are 3 exposed environment variables that can be used to configure the behaviour, which are:

- SCRIPT_DEBUG: enable debug level of the image and its operations
- ENABLE_EVENTS: enable the events add-on
- JOBS_SERVICE_PERSISTENCE: select which persistence variant to use

Note: As the Jobs Services are built on top of Quarkus, we can also set any configuration supported by Quarkus
using either environment variables or system properties.

Using environment variables:
```bash
podman run -it -e VARIABLE_NAME=value quay.io/kiegroup/kogito-jobs-service-allinone:latest
```

Using system properties:
```bash
podman run -it -e JAVA_OPTIONS='-Dmy.sys.prop1=value1 -Dmy.sys.prop2=value2' \
  quay.io/kiegroup/kogito-jobs-service-allinone:latest
```

For convenience there are `container-compose` files that can be used to start the Jobs Service with the desired
persistence variant, to use execute the following command:

```bash
podman-compose -f contrib/jobs-service/container-compose-<variant>.yaml up
```

The above command will spinup the Jobs-service so you can connect your application.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Jobs Service
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications

### Kogito JIT Runner Component Image

The Kogito JIT Runner provides a tool that allows you to submit a DMN model and evaluate it on the fly with a simple HTTP request. You can find more details on JIT [here](https://github.com/apache/incubator-kie-kogito-apps/tree/main/jitexecutor).

Basic usage:

```bash
$ docker run -it quay.io/kiegroup/kogito-jit-runner:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-jit-runner:latest
```
You should notice a few debug messages being printed in the system output. You can then visit `localhost:8080/index.html` to test the service.

To know what configurations this image accepts please take a look [here](kogito-jit-runner-image.yaml) on the **envs** section.

## Contributing to Kogito Images repository

Before proceeding please make sure you have checked the [requirements](#kogito-images-requirements).

### Building Images

To build the images for local testing there is a [Makefile](./Makefile) which will do all the hard work for you.
With this Makefile you can:

- Build and test all images with only one command:
     ```bash
     $ make
     ```
     If there's no need to run the tests just set the *ignore_test* env to true, e.g.:
     ```bash
     $ make ignore_test=true
     ```

- Test all images with only one command, no build triggered, set the *ignore_build* env to true, e.g.:
     ```bash
     $ make ignore_build=true
     ```
 
- Build images individually, by default it will build and test each image
     ```bash
     $ make build-image image_name=kogito-data-index-ephemeral
     $ make build-image image_name=kogito-data-index-postgresql
     $ make build-image image_name=kogito-jobs-service-ephemeral
     $ make build-image image_name=kogito-jobs-service-postgresql
     $ make build-image image_name=kogito-jobs-service-allinone
     $ make build-image image_name=kogito-jit-runner
     ```
  
     We can ignore the build or the tests while interacting with a specific image as well, to build only:
     ```bash
     $ make ignore_test=true image_name={image_name}

     ```
     
     Or to test only:
     ```bash
     $ make ignore_build=true image_name={image_name}
     ```      
     
- Build and Push the Images to quay or a repo for you preference, for this you need to edit the Makefile accordingly: 
      ```bash
      $ make push
      ```
      It will create 3 tags:
        - X.Y
        - X.Y.z
        - latest
        
      to push a single image:
      ```bash
      $ make push-image image_name={image_name}
      ```     
        
- Push staging images (release candidates, a.k.a rcX tags), the following command will build and push RC images to quay. 
      ```bash
      $ make push-staging
      ```
      To override an existing tag use:
      ```bash
      $ make push-staging override=-o
      ```
      It uses the [push-staging.py](scripts/push-staging.py) script to handle the images.

- Push images to a local registry for testing 
      ```bash
      $ make push-local-registry REGISTRY=docker-registry-default.apps.spolti.cloud NS=spolti-1
      ```
      It uses the [push-local-registry.sh](scripts/push-local-registry.sh) script properly tag the images and push to the
      desired registry.

- You can also add `cekit_option` to the make command, which will be appended to the Cekit command. Default is `cekit -v`.

#### Image Modules

CeKit can use modules to better separate concerns and reuse these modules on different images. 
On the Kogito Images we have several CeKit modules that are used during builds. 
To better understand the CeKit Modules, please visit this [link](https://docs.cekit.io/en/latest/handbook/modules).

Below you can find all modules used to build the Kogito Images

- [kogito-data-index-common](modules/kogito-data-index-common): Data Index common module.
- [kogito-data-index-ephemeral](modules/kogito-data-index-ephemeral): Installs and Configure the ephemeral PostgreSQL data-index jar inside the image.
- [kogito-data-index-postgresql](modules/kogito-data-index-postgresql): Installs and Configure the PostgreSQL data-index jar inside the image.
- [kogito-jobs-service-common](modules/kogito-jobs-service-common): Job service common module
- [kogito-jobs-service-ephemeral](modules/kogito-jobs-service-ephemeral): Installs and Configure the in-memory jobs-service jar inside the image
- [kogito-jobs-service-postgresql](modules/kogito-jobs-service-postgresql): Installs and Configure the postgresql jobs-service jar inside the image
- [kogito-jobs-service-allinone](modules/kogito-jobs-service-all-in-one): Provides the runner script that supports all jobs-service flavors
- [kogito-launch-scripts](modules/kogito-launch-scripts): Main script for all images, it contains the startup script for Kogito Images
- [kogito-logging](modules/kogito-logging): Provides common logging functions.
- [kogito-jit-runner](modules/kogito-jit-runner): Installs and Configure the jit-runner jar inside the image
- [kogito-maven](modules/kogito-maven): Provides custom configuration script.


For each image, we use a specific *-image.yaml file.
Please inspect the image files to learn which modules are being installed on each image:

- [quay.io/kiegroup/kogito-data-index-ephemeral](kogito-data-index-ephemeral-image.yaml)
- [quay.io/kiegroup/kogito-data-index-postgresql](kogito-data-index-postgresql-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-ephemeral](kogito-jobs-service-ephemeral-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-postgresql](kogito-jobs-service-postgresql-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-allinone](kogito-jobs-service-allinone-image.yaml)
- [quay.io/kiegroup/kogito-jit-runner](kogito-jit-runner-image.yaml)

### Testing Images

There is two kind of tests, **behave** and **bats** tests.

#### Behave tests

For more information about behave tests please refer this [link](https://docs.cekit.io/en/latest/handbook/testing/behave.html)

##### Running Behave tests

To run all behave tests:

```bash
make test
```


CeKit also allows you to run a specific test.
See [Writing Behave Tests](#writing-behave-tests).

Example:
```bash
make build-image image_name=kogito-swf-builder test_options=--wip
```

Or by name:
```bash
make build-image image_name=kogito-swf-builder test_options=--name <Test Scenario Name>
```

You can also add `cekit_option` to the make command, which will be appended to the Cekit command. Default is `cekit -v`.

##### Writing Behave tests


With the Cekit extension of behave we can run, practically, any kind of test on the containers, even source to image tests.
There are a few options that you can use to define what action and what kind of validations/verifications your test must do.
The behave test structure looks like:

```bash
Feature my cool feature
    Scenario test my cool feature - it should print Hello and World on logs
  	  Given/when image is built/container is ready
	  Then container log should contain Hello
	  And container log should contain World
```

One feature can have as many scenarios as you want.
But one Scenario can have one action defined by the keywords given or when, the most common options for this are:

 - **Given s2i build {app_git_repo}**
 - **When container is ready**
 - **When container is started with env** \
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| variable&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| value |\
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| JBPM_LOOP_LEVEL_DISABLED&nbsp;&nbsp;&nbsp;| true &nbsp;&nbsp;| \
 In this test, we can specify any valid environment variable or a set of them.
 - **When container is started with args**: Most useful when you want to pass some docker argument, i.e. memory limit for the container.

The **Then** clause is used to do your validations, test something, look for a keyword in the logs, etc. 
If you need to validate more than one thing you can add a new line with the **And** keyword, like this example:

```bash
Scenario test my cool feature - it should print Hello and World on logs
  	  Given/when image is built/container is ready
	  Then container log should contain Hello
	   And container log should contain World
	   And container log should not contain World!!
	   And file /opt/eap/standalone/deployments/bar.jar should not exist
```

The most common sentences are:

 - **Then/And file {file} should exist**
 - **Then/And file {file} should not exist**
 - **Then/And s2i build log should not contain {string}**
 - **Then/And run {bash command} in container and check its output for {command_output}**
 - **Then/And container log should contain {string}**
 - **Then/And container log should not contain {string}**


CeKit allow us to use tags, it is very useful to segregate tests, if we want to run only the tests for the 
given image, we need to annotate the test specific or the entire feature with the image name, for example, 
we have the common tests that needs to run against almost all images, instead to add the same tests for every 
image feature, we create a common feature and annotate it with the images we want that
specific test or feature to run, an example can be found on [this common test](tests/features/common.feature)
For example, suppose you are working on a new feature and add tests to cover your changes. You don't want to run all existing tests, 
this can be easily done by adding the **@wip** tag on the behave test that you are creating.

All images have already test feature files. If a new image is being created, a new feature file will need to be created
and the very first line of this file would need to contain a tag with the image name.

For example, if we are creating a new image called quay.io/kiegroup/kogito-moon-service, we would have a feature called
**kogito-moon-service.feature** under the **tests/features** directory and this file will look like with the following
example:

```text
@quay.io/kiegroup/kogito-data-index-postgresql
Feature: Kogito-data-index-postgresql feature.
    ...
    Scenarios......
```

For a complete list of all available sentences, please refer the CeKit source code:
https://github.com/cekit/behave-test-steps/tree/v1/steps


#### Bats tests

What is Bats tests ? 
From Google: Bats is a TAP-compliant testing framework for Bash.
It provides a simple way to verify that the UNIX programs you write behave as expected.  
A Bats test file is a Bash script with special syntax for defining test cases.  
Under the hood, each test case is just a function with a description.

##### Running Bats tests

To run the bats tests, we need to specify which module and test we want to run.  
As an example, let's execute the tests from the [kogito-s2i-core](modules/kogito-s2i-core) module:

```bash
 $ bats modules/kogito-s2i-core/tests/bats/s2i-core.bats 
 ✓ test manage_incremental_builds
 ✓ test assemble_runtime no binaries
 ✓ test runtime_assemble
 ✓ test runtime_assemble with binary builds
 ✓ test runtime_assemble with binary builds entire target!
 ✓ test copy_kogito_app default java build no jar file present
 ✓ test copy_kogito_app default java build jar file present
 ✓ test copy_kogito_app default quarkus java build no jar file present
 ✓ test copy_kogito_app default quarkus java build uberJar runner file present
 ✓ test copy_kogito_app default quarkus native builds file present
 ✓ build_kogito_app only checks if it will generate the project in case there's no pom.xml
 ✓ build_kogito_app only checks if it will a build will be triggered if a pom is found

16 tests, 0 failures
```

##### Writing Bats tests

The best way to start to interact with Bats tests is to take a look on its [documentation](https://github.com/sstephenson/bats) 
and after use the existing ones as example.

[Here](modules/kogito-jobs-service-common/tests/bats) you can find a basic example about how our Bats tests
are structured.


### Reporting new issues

For the Kogito Images, we use the [Jira issue tracker](https://issues.redhat.com/projects/KOGITO) under the **KOGITO** project.
And to specify that the issue is specific to the Kogito images, there is a component called **Image** that 
should be added for any issue related to this repository.

When submitting the Pull Request with the fix for the reported issue, and for a better readability, we use the following pattern:


- Pull Requests targeting only main branch:  
```text
[KOGITO-XXXX] - Description of the Issue
```

- But if the Pull Request also needs to be part of a different branch/version and is cherry picked from main: 
```text
Master PR:
[main][KOGITO-XXXX] - Description of the Issue

0.9.x PR cherry picker from main:
[0.9.x][KOGITO-XXXX] - Description of the Issue
```
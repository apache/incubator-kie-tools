Kogito
------

**Kogito** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

<p align="center"><img width=55% height=55% src="docsimg/kogito.png"></p>

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kogito-images.svg)](https://github.com/kiegroup/kogito-images/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kogito-images.svg)](https://github.com/kiegroup/kogito-images/network/members)
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kogito-images.svg?style=flat-square)](https://github.com/kiegroup/kogito-images/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kogito-images.svg?style=flat-square)](https://github.com/kiegroup/kogito-images/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kogito-images.svg)](https://github.com/kiegroup/kogito-images/blob/master/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)


# Kogito Container Images

To be able to efficiently execute Kogito services on the Cloud there's a need to have Container Images so it can played
smoothly on any Kubernetes cluster. There are a few sets images which are divided in three different groups which are 
the components, the builder images and the runtime images.


Table of Contents
=================

- [Kogito Container Images](#kogito-container-images)
- [Table of Contents](#table-of-contents)
  - [Kogito Images Requirements](#kogito-images-requirements)
  - [Kogito Runtime and Builder Images](#kogito-runtime-and-builder-images)
    - [Kogito Builder Images](#kogito-builder-images)
      - [Kogito Quarkus Builder Image](#kogito-quarkus-builder-image)
        - [Kogito Quarkus Builder Image usage](#kogito-quarkus-builder-image-usage)
        - [Kogito Quarkus Builder Image example](#kogito-quarkus-builder-image-example)
      - [Kogito Spring Boot Builder Image](#kogito-spring-boot-builder-image)
        - [Kogito Spring Boot Builder Image usage](#kogito-spring-boot-builder-image-usage)
        - [Kogito Spring Boot Builder Image example](#kogito-spring-boot-builder-image-example)
      - [Improving Build Time](#improving-build-time)
        - [Using incremental builds](#using-incremental-builds)
        - [Using a Maven mirror](#using-a-maven-mirror)
    - [Kogito Runtime Images](#kogito-runtime-images)
      - [Binary Builds](#binary-builds)
        - [KJAR Maven Project](#kjar-maven-project)
        - [Assets only](#assets-only)
      - [Kogito Quarkus JVM Runtime Image](#kogito-quarkus-jvm-runtime-image)
        - [Kogito Quarkus JVM Runtime Image usage](#kogito-quarkus-jvm-runtime-image-usage)
        - [Kogito Quarkus JVM Runtime Image examples](#kogito-quarkus-jvm-runtime-image-examples)
      - [Kogito Quarkus Native Runtime Image](#kogito-quarkus-native-runtime-image)
        - [Kogito Quarkus Native Runtime Image usage](#kogito-quarkus-native-runtime-image-usage)
        - [Kogito Quarkus Native Runtime Image example](#kogito-quarkus-native-runtime-image-example)
      - [Kogito Spring Boot Runtime Image](#kogito-spring-boot-runtime-image)
        - [Kogito Spring Boot Runtime Image usage](#kogito-spring-boot-runtime-image-usage)
        - [Kogito Spring Boot Runtime Image example](#kogito-spring-boot-runtime-image-example)
  - [Kogito Component Images](#kogito-component-images)
    - [Kogito Data Index Component Image](#kogito-data-index-component-image)
    - [Kogito Explainability Component Image](#kogito-explainability-component-image)
    - [Kogito Jobs Service Component Image](#kogito-jobs-service-component-image)
    - [Kogito Management Console Component Image](#kogito-management-console-component-image)
  - [Using Kogito Images to Deploy Apps on OpenShift](#using-kogito-images-to-deploy-apps-on-openshift)
    - [Using released images](#using-released-images)
    - [Pushing the built images to a local OCP registry:](#pushing-the-built-images-to-a-local-ocp-registry)
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
            [Image Modules](#image-modules)
        * [Testing Images](#testing-images)
            * [Behave tests](#behave-tests)
                * [Running Behave tests](#running-behave-tests)
                * [Writing Behave tests](#writing-behave-tests)
            * [Bats tests](#bats-tests)
                * [Running Bats tests](#running-bats-tests)
                * [Writing Bats tests](#writing-bats-tests)
        * [Reporting new issues](#reporting-new-issues)

         
## Kogito Images Requirements 

To interact with Kogito images, you would need to install the needed dependencies so that the images can be built and tested.

* Mandatory dependencies:
    * Moby Engine or Docker CE
        * Podman can be use to build the images, but at this moment CeKit does not support it, so images build with podman
            cannot be tested with CeKit.
    * [CeKit 3.6.0+](https://docs.cekit.io/en/latest/): 
        * CeKit also has its own dependencies:
            * python packages: docker, docker-squash, odcs-client. 
            * All of those can be handled with pip, including CeKit.
            * if any dependency is missing CeKit will tell which one.
    * [Bats](https://github.com/sstephenson/bats) 
    * Java 11 or higher
    * Maven 3.6.2 or higher
            
* Optional dependencies:
    * [source-to-image](https://github.com/openshift/source-to-image)
        * used to perform local s2i images using some of the [builder images](#builder-images)
    * [GraalVM 19.3.1](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-19.3.1) Java 11 or higher
        * Useful to test Kogito apps on native mode before create a Container image with it.
    * [OpenShift Cli](https://docs.openshift.com/container-platform/4.3/cli_reference/openshift_cli/getting-started-cli.html)
        
        
## Kogito Runtime and Builder Images

Today, the Kogito images are divided basically in 2 vectors, when we talk about images that would be used to assemble 
or run Kogito applications: Runtime image and Builder image. 
Those are described bellow.


### Kogito Builder Images

The Kogito Builder Images are responsible for building the project with Apache Maven and generate the binary that will 
be used by the Kogito Runtime images to run the Kogito application.

The current available Kogito Builder images are:

* [quay.io/kiegroup/kogito-quarkus-ubi8-s2i](https://quay.io/kiegroup/kogito-quarkus-ubi8-s2i)
* [quay.io/kiegroup/kogito-springboot-ubi8-s2i](https://quay.io/kiegroup/kogito-springboot-ubi8-s2i)

The Kogito Quarkus Builder Image allows you to create native image using GraalVM which allows you to have
lightweight and fast applications ready to run in the Cloud.


#### Kogito Quarkus Builder Image

The Kogito Quarkus Builder Image is equipped with the following components:

 * GraalVM 19.3.1-java11
 * OpenJDK 11.0.6
 * Maven 3.6.2
 
For more information about what is installed on this image take a look [here](kogito-quarkus-s2i-overrides.yaml) in the
**modules.install** section. 

##### Kogito Quarkus Builder Image usage

This image contains a helper option to better understand how to it:

```bash
$ docker run -it quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest /home/kogito/kogito-app-launch.sh -h
```

By default, a normal java build will be performed. To perform a native build just set the **NATIVE**
build environment variable to **true**.

See the next topic for an example.
 

##### Kogito Quarkus Builder Image example

In this example, let's use a simple application available in the [Kogito Examples](https://github.com/kiegroup/kogito-examples)
repository: the *rules-quarkus-helloworld* example, with native compilation disabled.


```bash
$ s2i build https://github.com/kiegroup/kogito-examples.git \
    --ref master \
    --context-dir rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest \
    rules-example:1.0
...
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  08:37 s
[INFO] Finished at: 2020-04-06T19:13:42Z
[INFO] ------------------------------------------------------------------------
---> Build finished, installing application from path /tmp/src
---> Installing jar file
'target/rules-quarkus-helloworld-8.0.0-SNAPSHOT-runner.jar' -> '/home/kogito/bin/rules-quarkus-helloworld-8.0.0-SNAPSHOT-runner.jar'
---> Copying application libraries
---> [s2i-core] Copy image metadata file...
'/tmp/src/target/image_metadata.json' -> '/tmp/.s2i/image_metadata.json'
'/tmp/src/target/image_metadata.json' -> '/tmp/src/.s2i/image_metadata.json'
'/tmp/src/target/image_metadata.json' -> '/home/kogito/bin/image_metadata.json'
INFO ---> [persistence] Copying persistence files...
INFO ---> [persistence] Skip copying files, persistence directory does not exist...
Build completed successfully
```

After the image is built, let's test it:

```bash
$ docker run -it -p 8080:8080 rules-example:1.0
 --/ __ \/ / / / _ | / _ \/ //_/ / / / __/ 
 -/ /_/ / /_/ / __ |/ , _/ ,< / /_/ /\ \   
--\___\_\____/_/ |_/_/|_/_/|_|\____/___/   
2020-04-08 18:59:57,753 INFO  [io.quarkus] (main) rules-quarkus-helloworld 8.0.0-SNAPSHOT (powered by Quarkus 1.3.0.Final) started in 0.839s. Listening on: http://0.0.0.0:8080
2020-04-08 18:59:57,755 INFO  [io.quarkus] (main) Profile prod activated. 
2020-04-08 18:59:57,756 INFO  [io.quarkus] (main) Installed features: [cdi, kogito, resteasy, resteasy-jackson, resteasy-jsonb]
```

In a different shell, try the following command:
```bash
$ curl -H "Content-Type: application/json" -X POST -d '{"strings":["hello"]}' http://localhost:8080/hello

# the service will return `["hello", "world"]`
```


#### Kogito Spring Boot Builder Image

The Kogito Spring Boot Builder Image is equipped with the following components:

 * OpenJDK 11.0.6
 * Maven 3.6.2
 
For more information about what is installed on this image take a look [here](kogito-springboot-s2i-overrides.yaml) 
in the **modules.install** section. 

##### Kogito Spring Boot Builder Image usage

This image contains a helper option to better understand how to it:

```bash
$ docker run -it quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest /home/kogito/kogito-app-launch.sh -h
```

##### Kogito Spring Boot Builder Image example

In this example, let's use a simple application available in the [Kogito Examples](https://github.com/kiegroup/kogito-examples)
repository: the *process-springboot-example*.

```bash
$ s2i build https://github.com/kiegroup/kogito-examples.git \
    --ref master \
    --context-dir \
    process-springboot-example \
    quay.io/kiegroup/kogito-springboot-ubi8-s2i:latest \
    springboot-example:1.0
```

After the image is built, let's test it:

```bash
$ docker run -it -p 8080:8080 springboot-example:1.0
```

In a different shell, try the following commands:
```bash
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" \
     -X POST http://localhost:8080/orders

{"id":"10de03c0-828f-4f2e-bb3f-68c3ddfea7ec","approver":"john","order":{"orderNumber":"12345","shipped":false,"total":0.4231905542160477}}✔

$ curl -X GET http://localhost:8080/orders
$ curl -X DELETE http://localhost:8080/orders/10de03c0-828f-4f2e-bb3f-68c3ddfea7ec
```

#### Improving Build Time

The time needed to build the application is rather long. This is mainly due to maven downloading all dependencies, which takes several minutes.


##### Using incremental builds

If you are planning to build many times the same application, you can use the incremental builds which will improve drastically the build time. 
Let's start 2 builds with the incremental option enabled and compare the time spent to build each one:

```bash
# First incremental build
$ time s2i build https://github.com/kiegroup/kogito-examples.git \
    --ref master \
    --context-dir rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest \
    rules-example-incremental:1.0 \
    --incremental \    
    --env NATIVE=false
...
real	13m49.819s
user	0m1.768s
sys	0m1.429s
```

And now, let's run it again.

```bash
# Second incremental build
$ time s2i build https://github.com/kiegroup/kogito-examples.git \
    --ref master \
    --context-dir rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest \
    rules-example-incremental:1.0 \
    --incremental \
    --env NATIVE=false
...
real	0m57.582s
user	0m1.628s
sys	0m1.123s
```

In the second try, you can see the artifacts getting unpacked and reused from the previous build. 
Now, pay also attention to the time spent to build it. There is a big difference. 
In fact, an incremental build reuses the previously built image and it takes advantage of already downloaded artifacts. 
This can significantly improve the build time.

##### Using a Maven mirror

Another option is to use a Maven Mirror.  
This can be used together with incremental builds to speed up the build even more.  
To turn it possible we just need to set the **MAVEN_MIRROR_URL** environment variable when starting a new build, see the example below:


```bash
# Third incremental build, with Maven mirror option
$ time s2i build https://github.com/kiegroup/kogito-examples.git \
    --ref master \
    --context-dir rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-quarkus-ubi8-s2i:latest \
    rules-example-incremental-1 \
    --incremental \
    --env NATIVE=false \
    --env MAVEN_MIRROR_URL=http://nexus.apps.local.cloud/nexus/content/groups/public
...
real	0m49.658s
user	0m0.968s
sys	0m0.539s
```

Here you can see that the build time has again been reduced. 
If the maven mirror already have all the dependencies there, the build time can be even faster.  
Also, Maven generates lots of transfer logs for downloading/uploading of maven dependencies. By default, these logs are disabled. To view these logs we need to set env variable **MAVEN_DOWNLOAD_OUTPUT** to true. 

If a custom Maven Repository is required, the S2i images also supports it.  
In case the **MAVEN_REPO_URL** environment variable is provided a new Repository and Plugin Repository will be added to the internal `settings.xml` file.  
If no repo-id is provided using the **MAVEN_REPO_ID** environment variable, a generated one will be used.  
There is also the possibility to provide more than one custom Repository. In this case, we need to provide the repo **prefix** using the **MAVEN_REPOS** environment variable.  
Example, if we want to add two new repositories, the following environment variables is needed:

```bash
MAVEN_REPOS="CENTRAL,COMPANY"
CENTRAL_MAVEN_REPO_URL="http://central.severinolabs.com/group/public"
CENTRAL_MAVEN_REPO_ID="my_cool_id_central"
COMPANY_MAVEN_REPO_URL="http://company.severinolabs.com/group/public"
COMPANY_MAVEN_REPO_ID="my_cool_id_company"
``` 

### Kogito Runtime Images

The Kogito Runtime Images have 2 behaviors:
* Run the artifacts built by the Kogito Builder Images
* Run your pre-built local artifacts, via what we call a _Binary Build_. 
For non-native built applications, they have a JRE installed to allow to execute Java applications.

With this approach, we can have smaller and more compact images that do not include any of the build tools or artifacts (like the local maven repository for example).

Today we have the following Kogito Runtime Images:

* [quay.io/kiegroup/kogito-quarkus-jvm-ubi8](https://quay.io/kiegroup/kogito-quarkus-jvm-ubi8)
* [quay.io/kiegroup/kogito-quarkus-ubi8](http://quay.io/kiegroup/kogito-quarkus-ubi8)
* [quay.io/kiegroup/kogito-springboot-ubi8](http://quay.io/kiegroup/kogito-springboot-ubi8)

#### Binary Builds

A Binary Build allows you to quickly copy the built locally artifacts into the target *Kogito Runtime Image*, 
saving the time needed to build the final image using the method with Kogito Builder Images.
Below are the supported source structure:

- KJAR Maven Project
- Assets only

Both methods are described below.

##### KJAR Maven Project

[KJAR](https://developers.redhat.com/blog/2018/03/14/what-is-a-kjar/) stands for Knowledge jar which is a custom JAR file 
that contains Business Process or Rules and all needed dependencies and files to execute it on the target runtime, 
either Quarkus or Spring Boot.

If you don't have a already existing project, the best way to create a new one is to use Kogito Maven Archetypes 
to generate project structure. 
The available archetypes are:

- [Kogito Quarkus Archetype](https://github.com/kiegroup/kogito-runtimes/tree/master/archetypes/kogito-quarkus-archetype)
- [Kogito Spring Boot Archetype](https://github.com/kiegroup/kogito-runtimes/tree/master/archetypes/kogito-springboot-archetype)

Note that, when building Quarkus based application that is **not** a *UberJAR* we also need to copy the **lib** directory 
located inside the *target* directory.  
Examples on how to use this feature can be found in the next topics.


##### Assets only

This source structure assumes that there is no maven project but only business assets, stored either directly in the top folder 
or grouped into directories.

Types of Business assets can be:

- Business Process definition - bpmn2 or just bpmn files
- Business Rule definition - drl files
- Business Decision definition - dmn files

Upon build, these assets will be copied to a generated maven project and built with Maven to produce a runnable binary. Default value of group id is "com.company", artifact id is "project" and version is "1.0-SNAPSHOT". To provide custom value we need to set the **PROJECT_GROUP_ID**, **PROJECT_ARTIFACT_ID** and **PROJECT_VERSION**.

#### Kogito Quarkus JVM Runtime Image

This Kogito Runtime Image contains only the needed files to execute a pre built Kogito application and a JRE.


##### Kogito Quarkus JVM Runtime Image usage

This image contains a helper option to better understand how to it:

```bash
docker run -it quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest /home/kogito/kogito-app-launch.sh -h
```


##### Kogito Quarkus JVM Runtime Image examples

In the next few lines let's take a look on how this image can be used to receive an already built UberJAR. 
To configure Quarkus to generate a UberJAR please follow the instructions described [here](https://quarkus.io/guides/maven-tooling#configuration-reference)

For this example let's use the [process-quarkus-example](https://github.com/kiegroup/kogito-examples/tree/stable/process-quarkus-example).
Once you have checked out the example on your local machine follow the steps below:

**Example with UberJAR**
```bash
# build the example using uberjar reference
$ mvn clean package -Dquarkus.package.uber-jar
# inspect and run the generated uberjar, for instructions on how to use this example see its README file.
$ java -jar target/jbpm-quarkus-example-8.0.0-SNAPSHOT-runner.jar 

# performing a source to image build to copy the artifacts to the runtime image
$ s2i build target/ quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest process-quarkus-example

# run the generated image
$ docker run -p 8080:8080 -it process-quarkus-example

# On another shell do a simple post request 
curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

#repare the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```


**Example with non UberJAR**
For non uberjar the process is the same, but you only need to remove the property from Quarkus configuration to not generate uberjar.
```bash
$ mvn clean package
```

Note that this time there is a *lib* folder into the **target** directory. The s2i build will take care of copying it to the correct place. 
Just perform a build:

```bash
$ s2i build target/ quay.io/kiegroup/kogito-quarkus-jvm-ubi8:latest process-quarkus-example-non-uberjar
$ docker run -p 8080:8080 -it process-quarkus-example-non-uberjar

# On another shell do a simple post request 
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

#repare the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```


#### Kogito Quarkus Native Runtime Image

This Kogito Runtime Image contains only the needed files to execute a pre built Kogito application.


##### Kogito Quarkus Native Runtime Image usage

This image contains a helper option to better understand how to it:

```bash
docker run -it quay.io/kiegroup/kogito-quarkus-ubi8:latest /home/kogito/kogito-app-launch.sh -h
```

##### Kogito Quarkus Native Runtime Image example

For this example let's use the same than the previous one (process-quarkus-example). 
But this time, let's perform a native build:

```bash
$ mvn clean package -Pnative
```

A binary has been generated into the **target directory**.
Let's use this binary to perform the source-to-image build:

```bash
s2i build target/ quay.io/kiegroup/kogito-quarkus-ubi8:latest binary-test-example
-----> [s2i-core] Running runtime assemble script
-----> Binary build enabled, artifacts were uploaded directly to the image build
-----> Found binary file, native build.
-----> Cleaning up unneeded jar files
...
---> Installing application binaries
'./process-quarkus-example-8.0.0-SNAPSHOT-runner' -> '/home/kogito/bin/process-quarkus-example-8.0.0-SNAPSHOT-runner'
...

# run the output image
$ docker run -it -p 8080:8080 binary-test-example-3

# on another terminal, interact with the kogito service
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

#repare the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```


#### Kogito Spring Boot Runtime Image

This Kogito Runtime Image contains only the needed files to execute a pre built Kogito application and a JRE.


##### Kogito Spring Boot Runtime Image usage

This image contains a helper option to better understand how to it:

```bash
docker run -it quay.io/kiegroup/kogito-springboot-ubi8:latest /home/kogito/kogito-app-launch.sh -h
```

##### Kogito Spring Boot Runtime Image example

Let's try, here, the *process-springboot-example*: 

```bash
$ mvn clean package
```

A uberjar file has been generated into the **target directory**. 
Let's use this uberjar to perform the build:

```bash
$ s2i build target/ quay.io/kiegroup/kogito-springboot-ubi8:latest spring-binary-example
-----> [s2i-core] Running runtime assemble script
-----> Binary build enabled, artifacts were uploaded directly to the image build
-----> Cleaning up unneeded jar files
removed 'process-springboot-example-tests.jar'
removed 'process-springboot-example-sources.jar'
removed 'process-springboot-example-test-sources.jar'
-----> Copying uploaded files to /home/kogito
---> [s2i-core] Copy image metadata file...
'/tmp/src/./image_metadata.json' -> '/tmp/.s2i/image_metadata.json'
'/tmp/src/./image_metadata.json' -> '/tmp/src/.s2i/image_metadata.json'
'/tmp/src/./image_metadata.json' -> '/home/kogito/bin/image_metadata.json'
---> Installing application binaries
'./process-springboot-example.jar' -> '/home/kogito/bin/process-springboot-example.jar'
...

# run the output image
$ docker run -it -p 8080:8080 spring-binary-example

# on another terminal, interact with the kogito service
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

#repare the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```


## Kogito Component Images

The Kogito Component Images can be considered as lightweight images that will complement the Kogito core engine 
by providing extra capabilities, like managing the processes on a web UI or providing persistence layer to the Kogito applications.
Today we have 3 Kogito Component Images:

* [quay.io/kiegroup/kogito-data-index](https://quay.io/kiegroup/kogito-data-index)
* [quay.io/kiegroup/kogito-explainability](https://quay.io/kiegroup/kogito-explainability)
* [quay.io/kiegroup/kogito-jobs-service](htps://quay.io/kiegroup/kogito-jobs-service)
* [quay.io/kiegroup/kogito-management-console](https://quay.io/kiegroup/kogito-management-console)


### Kogito Data Index Component Image

The Data Index Service aims at capturing and indexing data produced by one more Kogito runtime services. 
For more information please visit this (link)(https://docs.jboss.org/kogito/release/latest/html_single/#proc_kogito-travel-agency-enable-data-index). 
The Data Index Service depends on a running Infinispan Server.


Basic usage
```bash
$ docker run -it --env QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=my-infinispan-server:11222 quay.io/kiegroup/kogito-data-index:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true --env QUARKUS_INFINISPAN_CLIENT_SERVER_LIST=my-infinispan-server:11222 quay.io/kiegroup/kogito-data-index:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-data-index-overrides.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/kiegroup/kogito-cloud-operator) can be used to deploy the Kogito Data Index Service 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Explainability Component Image

The Explainability Service aims to provide explainability on the decisions that have been taken by kogito runtime applications. 

Basic usage
```bash
$ docker run -it quay.io/kiegroup/kogito-explainability:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-explainability:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-explainability-overrides.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/kiegroup/kogito-cloud-operator) can be used to deploy the Kogito Explainability Service 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Jobs Service Component Image

The Kogito Jobs Service is a dedicated lightweight service responsible for scheduling jobs that aim at firing at a given time. 
It does not execute the job itself but it triggers a callback that could be an HTTP request on a given endpoint specified 
on the job request, or any other callback that could be supported by the service. 
For more information please visit this (link)[https://github.com/kiegroup/kogito-runtimes/wiki/Job-Service]


Basic usage:

```bash
$ docker run -it quay.io/kiegroup/kogito-jobs-service:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-jobs-service:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-jobs-service-overrides.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/kiegroup/kogito-cloud-operator) can be used to deploy the Kogito Jobs Service
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications


### Kogito Management Console Component Image

The Kogito Management Console allows you to have a intuitive way to manage your Kogito processes in execution.
It depends on the Kogito Data Index Service on which the Console will connect to so it can be able to manage it.
Keep in mind that when using the [Process Instance Management Add-on](https://docs.jboss.org/kogito/release/latest/html_single/#con_bpmn-process-instance-management_kogito-developing-process-services) 
it will enable your Kogito service be manageable through REST API.


To work correctly, the Kogito Management Console needs the Kogito Data Index Service url. If not provided, it will try to connect to the default one (http://localhost:8180).

Basic usage:

```bash
$ docker run -it --env KOGITO_DATAINDEX_HTTP_URL=data-index-service-url:9090 quay.io/kiegroup/kogito-management-console:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true --env KOGITO_DATAINDEX_HTTP_URL=data-index-service-url:9090 quay.io/kiegroup/kogito-management-console:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-management-console-overrides.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/kiegroup/kogito-cloud-operator) can be used to deploy the Kogito Management Console 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.


## Using Kogito Images to Deploy Apps on OpenShift

Once the images are built and imported into a registry (quay.io or any other registry), new applications can be built and deployed within a few steps.


### Using released images

As a first step, we need to make the Kogito Images available as Image Streams in OpenShift. If you have `cluster-admin` 
rights you can deploy it into the **openshift** namespace, otherwise, deploy it into the namespace where you have permissions. 
To install the image stream use this imagestream file: [kogito-imagestream.yaml](https://raw.githubusercontent.com/kiegroup/kogito-images/0.9.0/kogito-imagestream.yaml).
It points to the latest released version.

Let's use the *rules-quarkus-helloworld* from [Kogito Examples](https://github.com/kiegroup/kogito-examples).

```bash
# creating a new namespace
$ oc new-project rules-quarkus-helloworld
Now using project "rules-quarkus-helloworld" on server "https://ocp.lab.cloud:8443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-25-centos7~https://github.com/sclorg/ruby-ex.git

to build a new example application in Ruby.

# installing the imagestream on the current namespace
$ oc create -f https://raw.githubusercontent.com/kiegroup/kogito-images/0.9.0/kogito-imagestream.yaml
imagestream.image.openshift.io/kogito-quarkus-ubi8 created
imagestream.image.openshift.io/kogito-quarkus-jvm-ubi8 created
imagestream.image.openshift.io/kogito-quarkus-ubi8-s2i created
imagestream.image.openshift.io/kogito-springboot-ubi8 created
imagestream.image.openshift.io/kogito-springboot-ubi8-s2i created
imagestream.image.openshift.io/kogito-data-index created
imagestream.image.openshift.io/kogito-jobs-service created
imagestream.image.openshift.io/kogito-management-console created

# performing a new build
$ oc new-build --name=rules-quarkus-helloworld-builder --image-stream=kogito-quarkus-ubi8-s2i:0.9.0 \ 
    https://github.com/kiegroup/kogito-examples.git#master --context-dir=rules-quarkus-helloworld \
    --strategy=source --env NATIVE=false 
--> Found image 8c9d756 (5 days old) in image stream "rules-quarkus-helloworld/kogito-quarkus-ubi8-s2i" under tag "0.9.0" for "kogito-quarkus-ubi8-s2i:0.9.0"

    Kogito based on Quarkus 
    ----------------------- 
    Platform for building Kogito based on Quarkus

    Tags: builder, kogito, quarkus

    * The source repository appears to match: jee
    * A source build using source code from https://github.com/kiegroup/kogito-examples.git#master will be created
      * The resulting image will be pushed to image stream tag "rules-quarkus-helloworld-builder:latest"
      * Use 'start-build' to trigger a new build

--> Creating resources with label build=drools-helloworld-builder ...
    imagestreamtag.image.openshift.io "rules-quarkus-helloworld-builder:latest" created
    buildconfig.build.openshift.io "rules-quarkus-helloworld-builder" created
--> Success
```

The build has started, you can check the logs with the following command:

```bash
$ oc logs -f bc/rules-quarkus-helloworld-builder
```

Once the build is finished, you can now create a new build to copy the generated artifact from the source to image build
to the Kogito Runtime Image. To do this, execute the following command:

```bash
$ oc new-build --name=rules-quarkus-helloworld-service --source-image=rules-quarkus-helloworld-builder \
  --source-image-path=/home/kogito/bin:. --image-stream=kogito-quarkus-jvm-ubi8:0.9.0
--> Found image 1608e71 (6 days old) in image stream "rules-quarkus-helloworld/kogito-quarkus-jvm-ubi8" under tag "0.9.0" for "kogito-quarkus-jvm-ubi8:0.9.0"

    Kogito based on Quarkus JVM image 
    --------------------------------- 
    Runtime image for Kogito based on Quarkus JVM image

    Tags: builder, runtime, kogito, quarkus, jvm

    * A source build using <unknown> will be created
      * The resulting image will be pushed to image stream tag "rules-quarkus-helloworld-service:latest"
      * Use 'start-build' to trigger a new build

--> Creating resources with label build=drools-helloworld-service ...
    imagestream.image.openshift.io "rules-quarkus-helloworld-service" created
    buildconfig.build.openshift.io "rules-quarkus-helloworld-service" created
--> Success
```

Follow the logs with the following command:
```bash
$ oc logs -f bc/rules-quarkus-helloworld-service
```

Once the build gets finished, you need to create an application and use the service image created with the latest command.

```bash
$ oc new-app rules-quarkus-helloworld-service:latest
  --> Found image 664b295 (3 minutes old) in image stream "rules-quarkus-helloworld/rules-quarkus-helloworld-service" under tag "latest" for "rules-quarkus-helloworld-service:latest"
  
      temp.builder.openshift.io/rules-quarkus-helloworld/rules-quarkus-helloworld-service-1:e8062a99 
      --------------------------------------------------------------------------------------- 
      Runtime image for Kogito based on Quarkus JVM image
  
      Tags: builder, runtime, kogito, quarkus, jvm
  
      * This image will be deployed in deployment config "rules-quarkus-helloworld-service"
      * Port 8080/tcp will be load balanced by service "rules-quarkus-helloworld-service"
        * Other containers can access this service through the hostname "rules-quarkus-helloworld-service"
  
  --> Creating resources ...
      deploymentconfig.apps.openshift.io "rules-quarkus-helloworld-service" created
      service "rules-quarkus-helloworld-service" created
  --> Success
      Application is not exposed. You can expose services to the outside world by executing one or more of the commands below:
       'oc expose svc/rules-quarkus-helloworld-service' 
      Run 'oc status' to view your app.
```

As described in the command output, to be able to access the application, we need to expose it to the external world. 
For that, just execute the command listed in the output above, e.g.:

```bash
$ oc expose svc/rules-quarkus-helloworld-service
```

To see the route name, just execute the following command:

```bash
$ oc get routes
NAME                               HOST/PORT                                                                     PATH      SERVICES                           PORT       TERMINATION   WILDCARD
rules-quarkus-helloworld-service   rules-quarkus-helloworld-service-rules-quarkus-helloworld.apps.lab.cloud                rules-quarkus-helloworld-service   8080-tcp                 None
```

Now, with the service address in hand we can test our service:

```bash
$ curl -H "Content-Type: application/json" -X POST -d '{"strings":["hello"]}' \
     http://rules-quarkus-helloworld-service-rules-quarkus-helloworld.apps.lab.cloud/hello
```

As output you should see the following response:

```json
["hello","world"]
```


For more complex deployment, please use the [Kogito Cloud Operator](https://github.com/kiegroup/kogito-cloud-operator)



### Pushing the built images to a local OCP registry:

To be able to build the image it should be installed and available on OpenShift before it can be used.

Suppose we have built the Quarkus s2i Image with the following command:

```bash
$ make kogito-quarkus-ubi8-s2i 
```

We'll have as output the following image:

```bash
quay.io/kiegroup/kogito-quarkus-ubi8-s2i:X.X.X
```

Then we need to tag the image properly. 
Suppose your local registry is openshift.local.registry:8443, you should do:

```bash
$ docker tag quay.io/kiegroup/kogito-quarkus-ubi8-s2i:X.X.X \
    openshift.local.registry:8443/{NAMESPACE}/kogito-quarkus-ubi8-s2i:X.X.X
```

Where the namespace is the place where you want the image to be available for usage. 
Once the image is properly tagged, log in to the registry and push the new image:

```bash
$ docker login -u <USERNAME> -p <PASSWORD>  openshift.local.registry:8443
$ docker push  openshift.local.registry:8443/{NAMESPACE}/kogito-quarkus-ubi8-s2i:X.X.X
```

To deploy and test the new image, follow the same steps as described [here](#using-released-images)


## Contributing to Kogito Images repository

Before proceed please make sure you have checked the [requirements](#kogito-images-requirements).


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
     $ make kogito-quarkus-ubi8
     $ make kogito-quarkus-jvm-ubi8
     $ make kogito-quarkus-ubi8-s2i
     $ make kogito-springboot-ubi8 
     $ make kogito-springboot-ubi8-s2i
     $ make kogito-data-index
     $ make kogito-explainability
     $ make kogito-jobs-service 
     $ make kogito-management-console
     ```
  
     We can ignore the build or the tests while interacting with a specific image as well, to build only:
     ```bash
     $ make ignore_test=true {image_name}

     ```
     
     Or to test only:
     ```bash
     $ make ignore_build=true {image_name}
     ```      
     
- Build and Push the Images to quay or a repo for you preference, for this you need to edit the Makefile accordingly: 
      ```bash
      $ make push
      ```
      It will create 3 tags:
        - X.Y
        - X.Y.z
        - latest
        
- Push staging images (release candidates, a.k.a rcX tags), the following command will build and push RC images to quay. 
      ```bash
      $ make push-staging
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

- [kogito-data-index](modules/kogito-data-index): Installs and Configure the data-index jar inside the image.
- [kogito-explainability](modules/kogito-explainability): Installs and Configure the explainability jar inside the image.
- [kogito-epel](modules/kogito-epel): Configures the epel repository on the target image.
- [kogito-graalvm-installer](modules/kogito-graalvm-installer): Installs the GraalVM on the target Image.
- [kogito-graalvm-scripts](modules/kogito-graalvm-scripts): Configures the GraalVM on the target image and provides custom configuration script. 
- [kogito-image-dependencies](modules/kogito-image-dependencies): Installs rpm packages on the target image. Contains common dependencies for Kogito Images.
- [kogito-infinispan-properties](modules/kogito-infinispan-properties): Provides Infinispan custom script to configure Infinispan properties during image startup.
- [kogito-jobs-service](modules/kogito-jobs-service): Installs and Configure the jobs-service jar inside the image
- [kogito-jq](modules/kogito-jq): Provides jq binary.
- [kogito-kubernetes-client](modules/kogito-kubernetes-client): Provides a simple wrapper to interact with Kubernetes API.
- [kogito-launch-scripts](modules/kogito-launch-scripts): Main script for all images, it contains the startup script for Kogito Images
- [kogito-logging](modules/kogito-logging): Provides common logging functions.
- [kogito-management-console](modules/kogito-management-console): Installs and Configure the management-console jar inside the image
- [kogito-maven](modules/kogito-maven): Installs and configure Maven on the S2I images, also provides custom configuration script.
- [kogito-openjdk](modules/kogito-openjdk): Provides OpenJDK and JRE.
- [kogito-persistence](modules/kogito-persistence): Provides the needed configuration scripts to properly configure the Kogito Services in the target image.
- [kogito-quarkus](modules/kogito-quarkus): Main module for the quay.io/kiegroup/kogito-quarkus-ubi8 image.
- [kogito-quarkus-jvm](modules/kogito-quarkus-jvm): Main module for the quay.io/kiegroup/kogito-quarkus-jvm-ubi8 image.
- [kogito-quarkus-s2i](modules/kogito-quarkus-s2i): Main module for the quay.io/kiegroup/kogito-quarkus-ubi8-s2i image.
- [kogito-s2i-core](modules/kogito-s2i-core): Provides the source-to-image needed scripts and configurations.
- [kogito-springboot](modules/kogito-springboot): Main module for the quay.io/kiegroup/kogito-springboot-ubi8 image.
- [kogito-springboot-s2i](modules/kogito-springbot-s2i): Main module for the quay.io/kiegroup/kogito-springboot-ubi8-s2i image.


For each image, we use a specific *-overrides.yaml file which will specific the modules needed.
Please inspect the images overrides files to learn which modules are being installed:

- [quay.io/kiegroup/kogito-data-index](kogito-data-index-overrides.yaml)
- [quay.io/kiegroup/kogito-explainability](kogito-explainability-overrides.yaml)
- [quay.io/kiegroup/kogito-jobs-service](kogito-jobs-service-overrides.yaml)
- [quay.io/kiegroup/kogito-management-console](kogito-management-console-overrides.yaml)
- [quay.io/kiegroup/kogito-quarkus-jvm-ubi8](kogito-quarkus-jvm-overrides.yaml)
- [quay.io/kiegroup/kogito-quarkus-ubi8](kogito-quarkus-overrides.yaml)
- [quay.io/kiegroup/kogito-quarkus-ubi8-s2i](kogito-quarkus-s2i-overrides.yaml)
- [quay.io/kiegroup/kogito-springboot-ubi8](kogito-springboot-overrides.yaml)
- [quay.io/kiegroup/kogito-springboot-ubi8-s2i](kogito-springboot-s2i-overrides.yaml)


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

The **Then** clause is used to do your validations, test something, looking for a keyword in the logs, etc. 
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
specific test or feature to run, a example can be found on [this common test](tests/features/common.feature)
For example, suppose you are working on a new feature and add tests to cover your changes. You don't want to run all existing tests, 
this can be easily done by adding the **@wip** tag on the behave test that you are creating.

All images have already test feature files. If a new image is being created, a new feature file will need to be created
and the very first line of this file would need to contain a tag with the image name.

For example, if we are creating a new image called quay.io/kiegroup/kogito-moon-service, we would have a feature called
**kogito-moon-service.feature** under the **tests/features** directory and this file will looks like with the following
example:

```text
@quay.io/kiegroup/kogito-data-index
Feature: Kogito-data-index feature.
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
 ✓ test assemble_runtime with binaries binaries
 ✓ test assemble_runtime with binaries binaries and metadata
 ✓ test runtime_assemble
 ✓ test runtime_assemble with binary builds
 ✓ test runtime_assemble with binary builds entire target!
 ✓ test handle_image_metadata_json no metadata
 ✓ test handle_image_metadata_json with metadata
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

The best way to start to interact with Bats tests is take a look on its [documentation](https://github.com/sstephenson/bats) 
and after use the existing ones as example.

[Here](modules/kogito-jobs-service/tests/bats) you can find a basic example about how our Bats tests
are structured.


### Reporting new issues

For the Kogito Images, we use the [Jira issue tracker](https://issues.redhat.com/projects/KOGITO) under the **KOGITO** project.
And to specify that the issue is specific to the Kogito images, there is a component called **Image** that 
should be added for any issue related to this repository.

When submitting the Pull Request with the fix for the reported issue, and for a better readability, we use the following pattern:


- Pull Requests targeting only master branch:  
```text
[KOGITO-XXXX] - Description of the Issue
```

- But if the Pull Request also needs to be part of a different branch/version and is cherry picked from master: 
```text
Master PR:
[master][KOGITO-XXXX] - Description of the Issue

0.9.x PR cherry picker from master:
[0.9.x][KOGITO-XXXX] - Description of the Issue
```

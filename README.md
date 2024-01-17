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
  - [Kogito Runtime and Builder Images](#kogito-runtime-and-builder-images)
    - [Kogito Builder Images](#kogito-builder-images)
      - [Kogito SWF (Serverless Workflow) Builder Image usage](#kogito-swf-serverless-workflow-builder-image-usage)
        - [Using as a builder](#using-as-a-builder)
        - [Using for application development](#using-for-application-development)
        - [Using the Kogito SWF Builder Image nightly image](#using-the-kogito-swf-builder-image-nightly-image)
      - [Kogito s2i Builder Image usage](#kogito-s2i-builder-image-usage)
      - [Kogito s2i Builder Image example](#kogito-s2i-builder-image-example)
        - [S2i Builder Image Example with Quarkus](#s2i-builder-image-example-with-quarkus)
        - [S2i Builder Image Example with Springboot](#s2i-builder-image-example-with-springboot)
      - [Improving Build Time](#improving-build-time)
        - [Using incremental builds](#using-incremental-builds)
        - [Using a Maven mirror](#using-a-maven-mirror)
    - [Kogito Runtime Images](#kogito-runtime-images)
      - [Binary Builds](#binary-builds)
        - [KJAR Maven Project](#kjar-maven-project)
        - [Assets only](#assets-only)
      - [Kogito Runtime JVM Image](#kogito-runtime-jvm-image)
        - [Kogito Runtime JVM Image usage](#kogito-runtime-jvm-image-usage)
        - [Kogito Runtime JVM Image examples](#kogito-runtime-jvm-image-examples)
      - [Kogito Runtime Native Image](#kogito-runtime-native-image)
        - [Kogito Runtime Native Image Usage](#kogito-runtime-native-image-usage)
        - [Kogito Runtime Native Image Example](#kogito-runtime-native-image-example)
  - [Kogito Component Images](#kogito-component-images)
    - [Kogito Data Index Component Images](#kogito-data-index-component-images)
    - [Kogito Explainability Component Image](#kogito-explainability-component-image)
    - [Kogito Trusty Component Image](#kogito-trusty-component-image)
    - [Kogito Jobs Service Component Images](#kogito-jobs-service-component-images)
      - [Jobs Services All-in-one](#jobs-services-all-in-one)
    - [Kogito Management Console Component Image](#kogito-management-console-component-image)
    - [Kogito Task Console Component Image](#kogito-task-console-component-image)
    - [Kogito Trusty UI Component Image](#kogito-trusty-ui-component-image)
    - [Kogito JIT Runner Component Image](#kogito-jit-runner-component-image)
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


## Kogito Runtime and Builder Images

Today, the Kogito images are divided basically in 2 vectors, when we talk about images that would be used to assemble 
or run Kogito applications: Runtime image and Builder image. 
Those are described bellow.


### Kogito Builder Images

The Kogito Builder Images are responsible for building the project with Apache Maven and generating the binary that will 
be used by the Kogito Runtime images to run the Kogito application.

There are three builder images available:


* [quay.io/kiegroup/kogito-base-builder](https://quay.io/kiegroup/kogito-base-builder)  
The Kogito base Builder Image is equipped with the following components:
  * OpenJDK 17.x
  * Maven 3.9.3

* [quay.io/kiegroup/kogito-swf-builder](https://quay.io/kiegroup/kogito-swf-builder)  
  The Kogito SWF Builder Image extends the kogito-base-builder is equipped with the following components for faster builds:
    * Quarkus dependencies
    * kogito-quarkus-serverless-workflow extension dependencies
    * kogito-addons-quarkus-knative-eventing extension dependencies

* [quay.io/kiegroup/kogito-s2i-builder](https://quay.io/kiegroup/kogito-s2i-builder)

Former name: `quay.io/kiegroup/kogito-builder`  
The Kogito s2i builder image supports building applications based on Spring Boot and Quarkus. To define your runtime, specify the `RUNTIME_TYPE` environment variable. If var is not defined, it defaults to `quarkus`.
When `RUNTIME_TYPE` quarkus is chosen, the Builder Image allows you to create a native image using GraalVM, which allows you to have lightweight and fast applications ready to run in the Cloud.

The Kogito s2i Builder Image is equipped with the following components:

 * GraalVM 23+
 * OpenJDK 17+
 * Maven 3.9.3
 
For more information about what is installed on this image, take a look [here](kogito-s2i-builder-image.yaml) in the
**modules.install** section. 


#### Kogito SWF (Serverless Workflow) Builder Image usage

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

##### Using the Kogito SWF Builder Image nightly image

The nightly builder image has been built and optimized with an internal nightly build of the Quarkus Platform.  
There are 2 environment variables that should not be changed when using it:

- QUARKUS_PLATFORM_VERSION = kogito-${KOGITO_VERSION}
- MAVEN_REPO_URL = https://repository.jboss.org/nexus/content/repositories/kogito-internal-repository/

That way, no new artifacts will be downloaded and you can directly use it.

#### Kogito s2i Builder Image usage

This image contains a helper option to better understand how to use it:

```bash
$ docker run -it quay.io/kiegroup/kogito-s2i-builder:latest /home/kogito/kogito-app-launch.sh -h
```

By default, quarkus is selected as runtime, and a normal java build will be performed. To perform a native build, just set the **NATIVE** build environment variable to **true**.

See the next topic for an example.
 

#### Kogito s2i Builder Image example

##### S2i Builder Image Example with Quarkus
In this example, let's use a simple application based on Quarkus that is available in the [Kogito Examples](https://github.com/apache/incubator-kie-kogito-examples)
repository: the *rules-quarkus-helloworld* example, with native compilation disabled.


```bash
$ s2i build https://github.com/apache/incubator-kie-kogito-examples.git \
    --ref main \
    -e RUNTIME_TYPE=quarkus \
    --context-dir kogito-quarkus-examples/rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-s2i-builder:latest \
    rules-example:1.0
...
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  08:37 s
[INFO] Finished at: 2020-04-06T19:13:42Z
[INFO] ------------------------------------------------------------------------
---> Build finished, installing application from path /tmp/src
---> Installing jar file
'target/rules-quarkus-helloworld-runner.jar' -> '/home/kogito/bin/rules-quarkus-helloworld-runner.jar'
---> Copying application libraries
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



##### S2i Builder Image Example with Springboot
In this example, let's use a simple application based on Spring Boot that is available in the [Kogito Examples](https://github.com/apache/incubator-kie-kogito-examples)
repository: the *process-springboot-example*.

```bash
$ s2i build https://github.com/apache/incubator-kie-kogito-examples.git \
    --ref main \
    --context-dir kogito-springboot-examples/process-springboot-example \
    -e RUNTIME_TYPE=springboot \
    quay.io/kiegroup/kogito-s2i-builder:latest \
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
$ time s2i build https://github.com/apache/incubator-kie-kogito-examples.git \
    --ref main \
    -e RUNTIME_TYPE=quarkus
    --context-dir kogito-quarkus-examples/rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-s2i-builder:latest \
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
$ time s2i build https://github.com/apache/incubator-kie-kogito-examples.git \
    --ref main \
    -e RUNTIME_TYPE=quarkus
    --context-dir kogito-quarkus-examples/rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-s2i-builder:latest \
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
To make it possible we just need to set the **MAVEN_MIRROR_URL** environment variable when starting a new build, see the example below:


```bash
# Third incremental build, with Maven mirror option
$ time s2i build https://github.com/apache/incubator-kie-kogito-examples.git \
    --ref main \
    -e RUNTIME_TYPE=quarkus
    --context-dir kogito-quarkus-examples/rules-quarkus-helloworld \
    quay.io/kiegroup/kogito-s2i-builder:latest \
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
If the maven mirror already has all the dependencies there, the build time can be even faster.  
Also, Maven generates lots of transfer logs for downloading/uploading of maven dependencies. By default, these logs are 
disabled. To view these logs we need to set env variable **MAVEN_DOWNLOAD_OUTPUT** to true. 

If a custom Maven Repository is required, the S2i images also support it.  
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

* [quay.io/kiegroup/kogito-runtime-jvm](https://quay.io/kiegroup/kogito-runtime-jvm)
* [quay.io/kiegroup/kogito-runtime-native](http://quay.io/kiegroup/kogito-runtime-native)

#### Binary Builds

A Binary Build allows you to quickly copy the built locally artifacts into the target *Kogito Runtime Image*, 
saving the time needed to build the final image using the method with Kogito Builder Images.
Below are the supported source structure:

- KJAR Maven Project
- Assets only

Both methods are described below.

##### KJAR Maven Project

[KieJAR](https://developers.redhat.com/blog/2018/03/14/what-is-a-kjar/) stands for Knowledge Is Everything jar which is a custom JAR file 
that contains Business Process or Rules and all needed dependencies and files to execute it on the target runtime, 
either Quarkus or Spring Boot.

If you don't have an already existing project, the best way to create a new one is to use Kogito Maven Archetypes 
to generate project structure. 
The available archetypes are:

- [Kogito Quarkus Archetype](https://github.com/apache/incubator-kie-kogito-runtimes/tree/main/archetypes/kogito-quarkus-archetype)
- [Kogito Spring Boot Archetype](https://github.com/apache/incubator-kie-kogito-runtimes/tree/main/archetypes/kogito-springboot-archetype)

Note that, when building Quarkus based application that is **not** an *UberJAR* we also need to copy the **lib** directory 
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

#### Kogito Runtime JVM Image

This Kogito Runtime Image contains only the needed files to execute a pre built Kogito application and a JRE.
The Image can run an application based on Quarkus or Springboot. Users can define `RUNTIME_TYPE` environment variable to switch between the two.


##### Kogito Runtime JVM Image usage

This image contains a helper option to better understand how to use it:

```bash
docker run -it quay.io/kiegroup/kogito-runtime-jvm:latest /home/kogito/kogito-app-launch.sh -h
```


##### Kogito Runtime JVM Image examples

In the next few lines let's take a look on how this image can be used to receive an already built UberJAR. 
To configure Quarkus to generate an UberJAR please follow the instructions described [here](https://quarkus.io/guides/maven-tooling#configuration-reference)

For this example let's use the [process-quarkus-example](https://github.com/apache/incubator-kie-kogito-examples/tree/stable/kogito-quarkus-examples/process-quarkus-example).
Once you have checked out the example on your local machine follow the steps below:

**Example with UberJAR**
```bash
# build the example using uberjar reference
$ mvn clean package -Dquarkus.package.uber-jar
# inspect and run the generated uberjar, for instructions on how to use this example see its README file.
$ java -jar target/jbpm-quarkus-example-runner.jar 

# performing a source to image build to copy the artifacts to the runtime image
$ s2i build target/ -e RUNTIME_TYPE=quarkus quay.io/kiegroup/kogito-runtime-jvm:latest process-quarkus-example

# run the generated image
$ docker run -p 8080:8080 -it process-quarkus-example

# On another shell do a simple post request 
curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

# notice the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```


**Example with non UberJAR**
For non uberjar the process is the same, but you only need to remove the property from Quarkus configuration to not generate uberjar.
```bash
$ mvn clean package
```

Note that this time there is a *lib* folder in the **target** directory. The s2i build will take care of copying it to the correct place. 
Just perform a build:

```bash
$ s2i build target/ quay.io/kiegroup/kogito-runtime-jvm:latest process-quarkus-example-non-uberjar
$ docker run -p 8080:8080 -it process-quarkus-example-non-uberjar

# On another shell do a simple post request 
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

# notice the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```
**Runtime Image example with springboot**
Let's try, here, the *process-springboot-example*: 

```bash
$ mvn clean package
```

An uberjar file has been generated into the **target directory**. 
Let's use this uberjar to perform the build:

```bash
$ s2i build target/ -e RUNTIME_TYPE=springboot quay.io/kiegroup/kogito-runtime-jvm:latest spring-binary-example
-----> [s2i-core] Running runtime assemble script
-----> Binary build enabled, artifacts were uploaded directly to the image build
-----> Cleaning up unneeded jar files
removed 'process-springboot-example-tests.jar'
removed 'process-springboot-example-sources.jar'
removed 'process-springboot-example-test-sources.jar'
-----> Copying uploaded files to /home/kogito
---> Installing application binaries
'./process-springboot-example.jar' -> '/home/kogito/bin/process-springboot-example.jar'
...

# run the output image
$ docker run -it -p 8080:8080 spring-binary-example

# on another terminal, interact with the kogito service
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

# notice the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```

#### Kogito Runtime Native Image

This Kogito Runtime Image contains only the needed files to execute a pre built Kogito application.


##### Kogito Runtime Native Image Usage

This image contains a helper option to better understand how to it:

```bash
docker run -it quay.io/kiegroup/kogito-runtime-native:latest /home/kogito/kogito-app-launch.sh -h
```

##### Kogito Runtime Native Image Example

For this example, let's use the same as the previous one (process-quarkus-example). 
But this time, let's perform a native build:

```bash
$ mvn clean package -Dnative
```

A binary has been generated into the **target directory**.
Let's use this binary to perform the source-to-image build:

```bash
s2i build target/ -e RUNTIME_TYPE=quarkus quay.io/kiegroup/kogito-runtime-native:latest binary-test-example
-----> [s2i-core] Running runtime assemble script
-----> Binary build enabled, artifacts were uploaded directly to the image build
-----> Found binary file, native build.
-----> Cleaning up unneeded jar files
...
---> Installing application binaries
'./process-quarkus-example-runner' -> '/home/kogito/bin/process-quarkus-example-runner'
...

# run the output image
$ docker run -it -p 8080:8080 binary-test-example

# on another terminal, interact with the kogito service
$ curl -d '{"approver" : "john", "order" : {"orderNumber" : "12345", "shipped" : false}}' -H "Content-Type: application/json" -X POST http://localhost:8080/orders

# notice the container logs the following message:
Order has been created Order[12345] with assigned approver JOHN
```

## Kogito Component Images

The Kogito Component Images can be considered as lightweight images that will complement the Kogito core engine 
by providing extra capabilities, like managing the processes on a web UI or providing persistence layer to the Kogito applications.
Today we have 16 Kogito Component Images:

* [quay.io/kiegroup/kogito-data-index-infinispan](https://quay.io/kiegroup/kogito-data-index-infinispan)
* [quay.io/kiegroup/kogito-data-index-ephemeral](https://quay.io/kiegroup/kogito-data-index-ephemeral)
* [quay.io/kiegroup/kogito-data-index-mongodb](https://quay.io/kiegroup/kogito-data-index-mongodb)
* [quay.io/kiegroup/kogito-data-index-postgresql](https://quay.io/kiegroup/kogito-data-index-postgresql)
* [quay.io/kiegroup/kogito-trusty-infinispan](https://quay.io/kiegroup/kogito-trusty-infinispan)
* [quay.io/kiegroup/kogito-trusty-redis](https://quay.io/kiegroup/kogito-trusty-redis)
* [quay.io/kiegroup/kogito-trusty-postgresql](https://quay.io/kiegroup/kogito-trusty-postgresql)
* [quay.io/kiegroup/kogito-explainability](https://quay.io/kiegroup/kogito-explainability)
* [quay.io/kiegroup/kogito-jobs-service-ephemeral](https://quay.io/kiegroup/kogito-jobs-service-ephemeral) 
* [quay.io/kiegroup/kogito-jobs-service-infinispan](https://quay.io/kiegroup/kogito-jobs-service-infinispan)
* [quay.io/kiegroup/kogito-jobs-service-mongodb](https://quay.io/kiegroup/kogito-jobs-service-mongodb)
* [quay.io/kiegroup/kogito-jobs-service-postgresql](https://quay.io/kiegroup/kogito-jobs-service-postgresql)
* [quay.io/kiegroup/kogito-jobs-service-allinone](https://quay.io/kiegroup/kogito-jobs-service-allinone)
* [quay.io/kiegroup/kogito-management-console](https://quay.io/kiegroup/kogito-management-console)
* [quay.io/kiegroup/kogito-task-console](https://quay.io/kiegroup/kogito-task-console)
* [quay.io/kiegroup/kogito-trusty-ui](https://quay.io/kiegroup/kogito-trusty-ui)
* [quay.io/kiegroup/kogito-jit-runner](https://quay.io/kiegroup/kogito-jit-runner)


### Kogito Data Index Component Images

The Data Index Service aims at capturing and indexing data produced by one more Kogito runtime services. 
For more information please visit this (link)(https://docs.jboss.org/kogito/release/latest/html_single/#proc-kogito-travel-agency-enable-data-index_kogito-deploying-on-openshift). 
The Data Index Service depends on a running Infinispan, MongoDB or PostgreSQL.
The Persistence service can be switched by using its corresponding image

- Infinispan: quay.io/kiegroup/kogito-data-index-infinispan
  [image.yaml](kogito-data-index-infinispan-image.yaml)
- Ephemeral PostgreSQL: quay.io/kiegroup/kogito-data-index-ephemeral
  [image.yaml](kogito-data-index-ephemeral-image.yaml)
- Mongodb: quay.io/kiegroup/kogito-data-index-mongodb 
  [image.yaml](kogito-data-index-mongodb-image.yaml)
- Oracle: quay.io/kiegroup/kogito-data-index-oracle
  [image.yaml](kogito-data-index-oracle-image.yaml)
- PostgreSQL: quay.io/kiegroup/kogito-data-index-postgresql
  [image.yaml](kogito-data-index-postgresql-image.yaml)


Basic usage with Infinispan:
```bash
$ docker run -it --env QUARKUS_INFINISPAN_CLIENT_HOSTS=my-infinispan-server:11222 quay.io/kiegroup/kogito-data-index-infinispan:latest
```

Basic usage with Ephemeral PostgreSQL:
```bash
$ docker run -it quay.io/kiegroup/kogito-data-index-ephemeral:latest
```

Basic usage with Mongodb:
```bash
$ docker run -it --env QUARKUS_MONGODB_CONNECTION_STRING=mongodb://localhost:27017 quay.io/kiegroup/kogito-data-index-mongodb:latest
```

Basic usage with Oracle:
```bash
$ docker run -it --env QUARKUS_DATASOURCE_JDBC_URL="jdbc:oracle:thin://localhost:1521/kogito"  \
    --env QUARKUS_DATASOURCE_USERNAME="kogito" \
    --env QUARKUS_DATASOURCE_PASSWORD="secret" \
    quay.io/kiegroup/kogito-data-index-oracle:latest
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
$ docker run -it --env SCRIPT_DEBUG=true --env QUARKUS_INFINISPAN_CLIENT_HOSTS=my-infinispan-server:11222 quay.io/kiegroup/kogito-data-index-infinispan:latest
```
You should notice a few debug messages present in the system output.


The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Data Index Service 
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

To know what configurations this image accepts please take a look [here](kogito-explainability-image.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Explainability Service 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.


### Kogito Trusty Component Image

The Trusty Service aims at collecting tracing information by one or more Kogito runtime services and provides analytical capabilities on top of the collected data. 
The Trusty Service depends on a running Infinispan, Redis Server or PostgreSQL RDBMS.
The Trusty service can be switched by using its corresponding image

- Infinispan: quay.io/kiegroup/kogito-trusty-infinispan
    [image.yaml](kogito-trusty-infinispan-image.yaml)
- Redis: quay.io/kiegroup/kogito-trusty-redis
    [image.yaml](kogito-trusty-redis-image.yaml)
- PostgreSQL: quay.io/kiegroup/kogito-trusty-postgresql
    [image.yaml](kogito-trusty-postgresql-image.yaml)

Basic usage with Infinispan:
```bash
$ docker run -it --env QUARKUS_INFINISPAN_CLIENT_HOSTS=my-infinispan-server:11222 quay.io/kiegroup/kogito-trusty-infinispan:latest
```

Basic usage with Redis:
```bash
$ docker run -it --env KOGITO_PERSISTENCE_REDIS_URL=redis://localhost:6379 quay.io/kiegroup/kogito-trusty-redis:latest
```

Basic usage with PostgreSQL:
```bash
$ docker run -it --env QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql://localhost:5432/quarkus"  \
    --env QUARKUS_DATASOURCE_USERNAME="kogito" \
    --env QUARKUS_DATASOURCE_PASSWORD="secret" \
    quay.io/kiegroup/kogito-trusty-postgresql:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true --env QUARKUS_INFINISPAN_CLIENT_HOSTS=my-infinispan-server:11222 quay.io/kiegroup/kogito-trusty:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-trusty-image.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Trusty Service 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Jobs Service Component Images

The Kogito Jobs Service is a dedicated lightweight service responsible for scheduling jobs that aim at firing at a given time. 
It does not execute the job itself, but it triggers a callback that could be an HTTP request on a given endpoint specified 
on the job request, or any other callback that could be supported by the service. 
For more information please visit this [link](https://github.com/apache/incubator-kie-kogito-runtimes/wiki/Job-Service).

Today, the Jobs service contains four images:

- [ephemeral](kogito-jobs-service-ephemeral-image.yaml)
- [infinispan](kogito-jobs-service-infinispan-image.yaml)
- [mongodb](kogito-jobs-service-mongodb-image.yaml)
- [postgresql](kogito-jobs-service-postgresql-image.yaml)
- [all-in-one](kogito-jobs-service-allinone-image.yaml)

Basic usage:

```bash
$ docker run -it quay.io/kiegroup/kogito-jobs-service-ephemeral:latest
```

To enable debug on the Jobs Service images, set the ` SCRIPT_DEBUG` to `true`, example: 

```bash
docker run -it --env SCRIPT_DEBUG=true quay.io/kiegroup/kogito-jobs-service-infinispan:latest
```

You should notice a few debug messages being printed in the system output.

The ephemeral image does not have external dependencies like a backend persistence provider, it uses in-memory persistence
while working with Jobs Services `allinone`, `infinispan`, `mongodb` and `postgresql` variants, it will need to 
have an Infinispan, MongoDB or PostgreSQL server, respectively, previously running.

#### Jobs Services All-in-one 

The Jobs Services All in One image provides the option to run any supported variant that we have at disposal, which are:

- PostgreSQL
- Infinispan
- MongoDB
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

To know what configurations this image accepts please take a look [here](kogito-management-console-image.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Management Console 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Task Console Component Image

The Kogito Task Console allows you to have an intuitive way to work with User Tasks in Kogito processes.
It depends on the Kogito Data Index Service on which the Console will connect to, so it can be able to manage it.

To work correctly, the Kogito Task Console needs the Kogito Data Index Service url. If not provided, it will try to connect to the default one (http://localhost:8180).

Basic usage:

```bash
$ docker run -it --env KOGITO_DATAINDEX_HTTP_URL=data-index-service-url:9090 quay.io/kiegroup/kogito-task-console:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true --env KOGITO_DATAINDEX_HTTP_URL=data-index-service-url:9090 quay.io/kiegroup/kogito-task-console:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-task-console-image.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Task Console
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

### Kogito Trusty UI Component Image

The Kogito Trusty UI provides an audit tool that allows you to retrieve and inspect the decisions that have been taken by Kogito Runtime Services.
It depends on the Kogito Trusty Service on which the Trusty UI will connect to so it can be able to retrieve the information to display.

To work correctly, the Kogito Trusty UI needs the Kogito Trusty Service url. If not provided, it will try to connect to the default one (http://localhost:8180).

Basic usage:

```bash
$ docker run -it --env KOGITO_TRUSTY_ENDPOINT=trusty-service-url:9090 quay.io/kiegroup/kogito-trusty-ui:latest
```

To enable debug just use this env while running this image:

```bash
docker run -it --env SCRIPT_DEBUG=true --env KOGITO_TRUSTY_ENDPOINT=trusty-service-url:9090 quay.io/kiegroup/kogito-trusty-ui:latest
```
You should notice a few debug messages being printed in the system output.

To know what configurations this image accepts please take a look [here](kogito-trusty-ui-image.yaml) on the **envs** section.

The [Kogito Operator](https://github.com/apache/incubator-kie-kogito-operator) can be used to deploy the Kogito Trusty UI 
to your Kogito infrastructure on a Kubernetes cluster and provide its capabilities to your Kogito applications.

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

## Using Kogito Images to Deploy Apps on OpenShift

Once the images are built and imported into a registry (quay.io or any other registry), new applications can be built and deployed within a few steps.


### Using released images

As a first step, we need to make the Kogito Images available as Image Streams in OpenShift. If you have `cluster-admin` 
rights you can deploy it into the **openshift** namespace, otherwise, deploy it into the namespace where you have permissions. 
To install the image stream use this imagestream file: [kogito-imagestream.yaml](https://raw.githubusercontent.com/apache/incubator-kie-kogito-images/main/kogito-imagestream.yaml).
It points to the latest released version.

Let's use the *kogito-quarkus-examples/rules-quarkus-helloworld* from [Kogito Examples](https://github.com/apache/incubator-kie-kogito-examples).

```bash
# creating a new namespace
$ oc new-project rules-quarkus-helloworld
Now using project "rules-quarkus-helloworld" on server "https://ocp.lab.cloud:8443".

You can add applications to this project with the 'new-app' command. For example, try:

    oc new-app centos/ruby-25-centos7~https://github.com/sclorg/ruby-ex.git

to build a new example application in Ruby.

# installing the imagestream on the current namespace
$ oc create -f https://raw.githubusercontent.com/apache/incubator-kie-kogito-images/0.16.0/kogito-imagestream.yaml
imagestream.image.openshift.io/kogito-runtime-native created
imagestream.image.openshift.io/kogito-runtime-jvm created
imagestream.image.openshift.io/kogito-s2i-builder created
imagestream.image.openshift.io/kogito-data-index-infinispan created
imagestream.image.openshift.io/kogito-data-index-ephemeral created
imagestream.image.openshift.io/kogito-data-index-mongodb created
imagestream.image.openshift.io/kogito-data-index-postgresql created
imagestream.image.openshift.io/kogito-trusty-infinispan created
imagestream.image.openshift.io/kogito-trusty-redis created
imagestream.image.openshift.io/kogito-trusty-postgresql created
imagestream.image.openshift.io/kogito-jobs-service-ephemeral created
imagestream.image.openshift.io/kogito-jobs-service-infinispan created
imagestream.image.openshift.io/kogito-jobs-service-mongodb created
imagestream.image.openshift.io/kogito-jobs-service-postgresql created
imagestream.image.openshift.io/kogito-jobs-service-allinone created
imagestream.image.openshift.io/kogito-management-console created

# performing a new build
$ oc new-build --name=rules-quarkus-helloworld-builder --image-stream=kogito-s2i-builder:latest \ 
    https://github.com/apache/incubator-kie-kogito-examples.git#main --context-dir=kogito-quarkus-examples/rules-quarkus-helloworld \
    --strategy=source --env NATIVE=false 
--> Found image 8c9d756 (5 days old) in image stream "rules-quarkus-helloworld/kogito-s2i-builder" under tag "latest" for "kogito-s2i-builder:latest"

    Kogito based on Quarkus 
    ----------------------- 
    Platform for building Kogito based on Quarkus

    Tags: builder, kogito, quarkus

    * The source repository appears to match: jee
    * A source build using source code from https://github.com/apache/incubator-kie-kogito-examples.git#main will be created
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
  --source-image-path=/home/kogito/bin:. --image-stream=kogito-runtime-jvm:latest
--> Found image 1608e71 (6 days old) in image stream "rules-quarkus-helloworld/kogito-runtime-jvm" under tag "latest" for "kogito-runtime-jvm:latest"

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

As output, you should see the following response:

```json
["hello","world"]
```


For more complex deployment, please use the [Kogito Cloud Operator](https://github.com/apache/incubator-kie-kogito-operator)



### Pushing the built images to a local OCP registry:

To be able to build the image it should be installed and available on OpenShift before it can be used.

Suppose we have built the kogito-s2i-builder with the following command:

```bash
$ make build-image image_name=kogito-s2i-builder
```

We'll have as output the following image:

```bash
quay.io/kiegroup/kogito-s2i-builder:X.X.X
```

Then we need to tag the image properly. 
Suppose your local registry is openshift.local.registry:8443, you should do:

```bash
$ docker tag quay.io/kiegroup/kogito-s2i-builder:X.X.X \
    openshift.local.registry:8443/{NAMESPACE}/kogito-s2i-builder:X.X.X
```

Where the namespace is the place where you want the image to be available for usage. 
Once the image is properly tagged, log in to the registry and push the new image:

```bash
$ docker login -u <USERNAME> -p <PASSWORD>  openshift.local.registry:8443
$ docker push  openshift.local.registry:8443/{NAMESPACE}/kogito-s2i-builder:X.X.X
```

To deploy and test the new image, follow the same steps as described [here](#using-released-images)


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
     $ make build-image image_name=kogito-s2i-builder
     $ make build-image image_name=kogito-runtime-jvm-ubi8
     $ make build-image image_name=kogito-runtime-native
     $ make build-image image_name=kogito-data-index-infinispan
     $ make build-image image_name=kogito-data-index-ephemeral
     $ make build-image image_name=kogito-data-index-mongodb
     $ make build-image image_name=kogito-data-index-oracle
     $ make build-image image_name=kogito-data-index-postgresql
     $ make build-image image_name=kogito-trusty-infinispan
     $ make build-image image_name=kogito-trusty-redis
     $ make build-image image_name=kogito-trusty-postgresql
     $ make build-image image_name=kogito-explainability
     $ make build-image image_name=kogito-jobs-service-ephemeral
     $ make build-image image_name=kogito-jobs-service-infinispan
     $ make build-image image_name=kogito-jobs-service-mongodb
     $ make build-image image_name=kogito-jobs-service-postgresql
     $ make build-image image_name=kogito-jobs-service-allinone
     $ make build-image image_name=kogito-management-console
     $ make build-image image_name=kogito-trusty-ui
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
- [kogito-data-index-infinispan](modules/kogito-data-index-infinispan): Installs and Configure the infinispan data-index jar inside the image.
- [kogito-data-index-ephemeral](modules/kogito-data-index-ephemeral): Installs and Configure the ephemeral PostgreSQL data-index jar inside the image.
- [kogito-data-index-mongodb](modules/kogito-data-index-mongodb): Installs and Configure the mongodb data-index jar inside the image.
- [kogito-data-index-oracle](modules/kogito-data-index-oracle): Installs and Configure the Oracle data-index jar inside the image.
- [kogito-data-index-postgresql](modules/kogito-data-index-postgresql): Installs and Configure the PostgreSQL data-index jar inside the image.
- [kogito-trusty-infinispan](modules/kogito-trusty-infinispan): Installs and Configure the infinispan trusty jar inside the image.
- [kogito-trusty-redis](modules/kogito-trusty-redis): Installs and Configure the redis trusty jar inside the image.
- [kogito-trusty-postgresql](modules/kogito-trusty-postgresql): Installs and Configure the PostgreSQL trusty jar inside the image.
- [kogito-explainability](modules/kogito-explainability): Installs and Configure the explainability jar inside the image.
- [kogito-epel](modules/kogito-epel): Configures the epel repository on the target image.
- [kogito-graalvm-installer](modules/kogito-graalvm-installer): Installs the GraalVM on the target Image.
- [kogito-graalvm-scripts](modules/kogito-graalvm-scripts): Configures the GraalVM on the target image and provides custom configuration script. 
- [kogito-image-dependencies](modules/kogito-image-dependencies): Installs rpm packages on the target image. Contains common dependencies for Kogito Images.
- [kogito-jobs-service-common](modules/kogito-jobs-service-common): Job service common module
- [kogito-jobs-service-ephemeral](modules/kogito-jobs-service-ephemeral): Installs and Configure the in-memory jobs-service jar inside the image
- [kogito-jobs-service-infinispan](modules/kogito-jobs-service-infinispan): Installs and Configure the infinispan jobs-service jar inside the image
- [kogito-jobs-service-mongodb](modules/kogito-jobs-service-mongodb): Installs and Configure the mongodb jobs-service jar inside the image
- [kogito-jobs-service-postgresql](modules/kogito-jobs-service-postgresql): Installs and Configure the postgresql jobs-service jar inside the image
- [kogito-jobs-service-allinone](modules/kogito-jobs-service-all-in-one): Provides the runner script that supports all jobs-service flavors
- [kogito-kubernetes-client](modules/kogito-kubernetes-client): Provides a simple wrapper to interact with Kubernetes API.
- [kogito-launch-scripts](modules/kogito-launch-scripts): Main script for all images, it contains the startup script for Kogito Images
- [kogito-logging](modules/kogito-logging): Provides common logging functions.
- [kogito-management-console](modules/kogito-management-console): Installs and Configure the management-console jar inside the image
- [kogito-trusty-ui](modules/kogito-trusty-ui): Installs and Configure the trusty-ui jar inside the image
- [kogito-jit-runner](modules/kogito-jit-runner): Installs and Configure the jit-runner jar inside the image
- [kogito-maven](modules/kogito-maven): Installs and configure Maven on the S2I images, also provides custom configuration script.
- [kogito-openjdk](modules/kogito-openjdk): Provides OpenJDK and JRE.
- [kogito-persistence](modules/kogito-persistence): Provides the needed configuration scripts to properly configure the Kogito Services in the target image.
- [kogito-runtime-native](modules/kogito-runtime-native): Main module for the quay.io/kiegroup/kogito-runtime-native image.
- [kogito-runtime-jvm](modules/kogito-runtime-jvm): Main module for the quay.io/kiegroup/kogito-runtime-jvm image.
- [kogito-s2i-builder](modules/kogito-s2i-builder): Main module for the quay.io/kiegroup/kogito-s2i-builder image.
- [kogito-s2i-core](modules/kogito-s2i-core): Provides the source-to-image needed scripts and configurations.


For each image, we use a specific *-image.yaml file.
Please inspect the image files to learn which modules are being installed on each image:

- [quay.io/kiegroup/kogito-data-index-infinispan](kogito-data-index-infinispan-image.yaml)
- [quay.io/kiegroup/kogito-data-index-ephemeral](kogito-data-index-ephemeral-image.yaml)
- [quay.io/kiegroup/kogito-data-index-mongodb](kogito-data-index-mongodb-image.yaml)
- [quay.io/kiegroup/kogito-data-index-oracle](kogito-data-index-oracle-image.yaml)
- [quay.io/kiegroup/kogito-data-index-postgresql](kogito-data-index-postgresql-image.yaml)
- [quay.io/kiegroup/kogito-trusty-infinispan](kogito-trusty-infinispan-image.yaml)
- [quay.io/kiegroup/kogito-trusty-redis](kogito-trusty-redis-image.yaml)
- [quay.io/kiegroup/kogito-trusty-postgresql](kogito-trusty-postgresql-image.yaml)
- [quay.io/kiegroup/kogito-explainability](kogito-explainability-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-ephemeral](kogito-jobs-service-ephemeral-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-infinispan](kogito-jobs-service-infinispan-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-mongodb](kogito-jobs-service-mongodb-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-postgresql](kogito-jobs-service-postgresql-image.yaml)
- [quay.io/kiegroup/kogito-jobs-service-allinone](kogito-jobs-service-allinone-image.yaml) 
- [quay.io/kiegroup/kogito-management-console](kogito-management-console-image.yaml)
- [quay.io/kiegroup/kogito-trusty-ui](kogito-trusty-ui-image.yaml)
- [quay.io/kiegroup/kogito-jit-runner](kogito-jit-runner-image.yaml)
- [quay.io/kiegroup/kogito-runtime-jvm](kogito-runtime-jvm-image.yaml)
- [quay.io/kiegroup/kogito-runtime-native](kogito-runtime-native-image.yaml)
- [quay.io/kiegroup/kogito-s2i-builder](kogito-s2i-builder-image.yaml)


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
make build-image image_name=kogito-s2i-builder test_options=--wip
```

Or by name:
```bash
make build-image image_name=kogito-s2i-builder test_options=--name <Test Scenario Name>
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
@quay.io/kiegroup/kogito-data-index-infinispan
Feature: Kogito-data-index-infinispan feature.
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
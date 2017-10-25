# Guvnor ALA (Application Lifecycle Automation)

(Guvnor ALA currently works with Docker Tools in Mac OSX, but Docker for Mac (native) is not supported yet, due the lack of support in docker-java. If you have any other docker related issue feel free to report it)

Guvnor ALA provides a simple API to automate all the stages of our applications lifecycle. From a high level point of view Guvnor ALA provides only a thin layer to integrate several mechanisms to manage source code, projects and building  and provisioning mechanisms. Inside Guvnor ALA you will find 5 building blocks.
- **Source** Allow us to get code locally from different sources, list all our sources, add new sources, etc. A Git implementation is provided.
- **Build** API & Maven build provider implementation provided
- **Runtime** create new runtime in different providers: AppServers, Docker, Kubernetes, OpenShift & control these runtimes
- **Pipeline** a way to control and chain the previous elements to we can move from source to runtime in just one service call
- **Registry** a way to keep track where our projects, providers, runtimes and pipelines are

On top of these building blocks you will find the Service Layer that allows you to interact with each step separately or with the pipelines to chain different steps together.

#Source
This module allows us to get code from external repositories so we can build it locally. The main idea behind these services are to provide us with a flexible way to manage our source repositories and enable us to get that code locally so it can be built.
A Git Implementation is provided as the base for other implementations. While we provide GIT out of the box, you can implement any other mechanisms following the same APIs defined in the guvnor-ala-spi module.

#Build
This block will be in charge of taking a project path and building the appropriate binaries for runtime. A Maven implementation is provided, but again you can provide your own build mechanisms for other technologies by implementing the interface defined in the guvnor-ala-spi module.


#Runtime
In order to provision runtimes there are 3 main concepts to understand:
 - **Provider Type** : it represents a registered provider type into the system. This could be Docker, OpenShift, Any Servlet container or Application Server
 - **Provider** : it represent an actual instance of a provider. The provider object represent and contains the information to interact with a provider, so we can start provisioning things inside it. For Docker, this relates to a specific Docker Deamon that we can contact, or an instance of an application server hosted remotely.
 - **Runtime** : a running container/application that we have provisioned and we can check the status or execute operations.

The project is divided into an SPI module whcih contains these concepts plus a set of providers that automatically register to the service via classpath resolution.

The current providers implementations are:
- **Docker** (guvnor-ala-docker-provider)
- **Openshift** (guvnor-ala-openshift-provider)
- **Wildfly** (guvnor-ala-wildfly-provider)

Once we have our new provisioned runtimes we can manage their state and control them remotely.

By using the provided APIs we will end up creating runtimes, no matter the provider that we choose to use.
These container instances should provide a way for the end users to execute operations and get information about state:

# Pipeline
An API to define a set of Stages that can be chained to achieve different outputs. So for example you will be able to
get sources from a remote repository and build those sources. Or get the generated binaries and create new runtimes into different providers.
The Pipeline API is simple to use and it provides an advance VariableInterpolation mechanism to resolve variable expressions that can be generated dynamically by one stage and used by another.

Here is simple example for creating a complete pipeline that will pick up the source code, built it using Maven, Create a Docker Image and then start the image:

```
// First we define the stages and their configurations
List<Config> configs = new ArrayList<>();
        configs.add( new GitConfigImpl() );
        configs.add( new MavenProjectConfigImpl() );
        configs.add( new MavenBuildConfigImpl() );
        configs.add( new DockerBuildConfigImpl() );
        configs.add( new MavenBuildExecConfigImpl() );
        configs.add( new DockerProviderConfigImpl() );
        configs.add( new ContextAwareDockerProvisioningConfig() );
        configs.add( new ContextAwareDockerRuntimeExecConfig() );

// Then we create the pipeline by using the Pipeline Services to chain these configuration together.

pipelineService.newPipeline( new PipelineConfigImpl( "mypipe", configs ) );

// Then we can execute the Pipeline by providing an input map with the initial parameters:

Input input = new Input();
input.put( "repo-name", "drools-workshop" );
input.put( "branch", "master" );
input.put( "out-dir", tempPath.getAbsolutePath() );
input.put( "origin", "https://github.com/salaboy/drools-workshop" );
input.put( "project-dir", "drools-webapp-example" );

pipelineService.runPipeline( "mypipe", input );

```
This pipeline will pick the "https://github.com/salaboy/drools-workshop", clone it into a temporal directory, built it using Maven (which will also generate a Docker Image) and then it will create a new Runtime by creating a Docker Container with the provided image and starting it so it is ready to use.

Pipelines also provide a local fluent API for easy creation:

```
final Pipeline pipe = PipelineFactory
               .startFrom( sourceConfig )
               .andThen( projectConfig )
               .andThen( buildConfig )
               .andThen( dockerBuildConfig )
               .andThen( buildExec )
               .andThen( providerConfig )
               .andThen( runtimeConfig )
               .andThen( runtimeExec ).buildAs( "my pipe" );
```               

# Registry
The registry module provides a place to store (in a distributed way) the information related with Repositories, Projects, Providers, Runtimes, Pipelines and builds. The registry is separated into different registries for different concepts:
- Sources Registry
- Build Registry
- Runtime Registry
- Pipelines Registry

The Registry project should provide a distributed implementation that allows multiple Service layers to access the same data.


# Services

A service layer is provided using JAX-RS services, so remote clients can register their provider instances and remotely provision new runtimes.

Multiple instances of the services can be started in different nodes and by using a distributed Registry module, they should be able to share the information about ProviderTypes, Providers and Runtimes.

There are currently 4 projects related with the services:
- **guvnor-ala-services-api**: it contains all the services APIs definitions.
- **guvnor-ala-services-rest**: it contains the implementation for the rest services.
- **guvnor-ala-services-backend**: it contains the logic of the services so they can be reused in a CDI environment, where local interaction against the services are needed.
- **guvnor-ala-distribution**: it generates a Wildfly Swarm Fat-JAR that can be independently started to get a new Node of the Guvnor ALA Services. These services can be started with: java -jar guvnor-ala-distribution-7.0.0-SNAPSHOT-swarm.jar

Current URLs:

- **Runtime**
 - GET /api/providerstypes (Get All Provider Types)
 - GET /api/providers (Get All Providers)
 - GET /api/runtimes (Get All Runtimes)

 - POST /api/providers (New Provider)
 - POST /api/runtimes (New Runtime)
 - POST /api/runtimes/{id}/start (Start Runtime)
 - POST /api/runtimes/{id}/stop (Stop Runtime)
 - DELETE /api/runtimes/{id} (Remove Runtime)

- **Pipelines**
 - GET /api/pipelines (Get All Pipelines)
 - POST /api/pipelines (New Pipeline)
 - POST /api/pipelines/{id}/run (Run Pipeline)

# Getting Started
You can clone the repository and build all the projects using: mvn clean install (note that you need to have Docker installed so all the Docker related test work. Alternatively you can compile the projects and skip the test by adding -DskipTests to the command)

In order to start the services you can do:
```
cd guvnor-ala-distribution/target/
java -jar guvnor-ala-distribution-7.0.0-SNAPSHOT-swarm.jar
```

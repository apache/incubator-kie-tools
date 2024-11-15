# Container Builder

This is an internal build system implementation inspired by [Camel-K Builder package](https://github.com/apache/camel-k/tree/main/pkg/builder) to build Kogito services in a Kubernetes clusters.

It supports [Kaniko](https://github.com/GoogleContainerTools/kaniko/blob/main/docs/tutorial.md) as the builder implementation.

## Requirements

To run it on minikube, you can do:

- Install minikube locally
- Enable the internal registry via `minikube addons enable registry`
- Run with `go run main.go`

Please note that the [`main.go`](main.go) file is a usage example. _Don't use it in production level code_.

## History

Since Camel-K already does a pretty good job building Camel applications in any Kubernetes environments and has a quite similar use case to Kogito, it makes sense looking at their tech.

## How does it work?

Camel-K has basically two phases of their build, which are "Project Assemble" and "Image Build".
In this first phase, Camel-K reads the Route configuration, assemble the Maven project and run the project build.
Then it takes the Java application and build into an image.

Camel-K has this concept of "Environment Platform" that is based on the type of cluster in use, so it can pick the right build feature.
For example, source to image on OpenShift clusters or Kaniko on Kubernetes.

Kogito might use this first phase to assemble a specific application based on the sources pushed to the cluster.
For simplicity, we skipped this first phase and assembled the project on a "builder image" for Serverless Workflow projects.
You can see a [draft for this image here](https://github.com/kiegroup/kogito-images/pull/1322).

This base builder image does the project assembling "ahead of time", so there's no need to run this phase like Camel-K does.
Kogito won't need this level of customization of a project, but there are use cases which could benefit from it such as using MongoDB as the persistence layer rather than Postgres.

This package performs the Camel-K builder just partially, but using their interfaces and structures to have some sort of compatibility.
Ideally, this project will evolve to a shared builder that can be reused by the tools from Camel-K and Kogito.

The package is not a Kubernetes Operator, but rather a set of packages that could be embedded on an operator running in a cluster or a CLI running locally.

The concept behind it is really simple. It abstracts the build and delegates internally based on the `PlatformBuild` information.
The builder chosen by the environment will run and the final image pushed to the elected registry.

This initial work supports Kaniko running on Minikube. Has potential to work on Kubernetes with an external registry such as Quay or Dockerhub.

## The Next Steps

This package can evolve to do more and abstract the build stage for a shareable use among Kaniko and Camel-K.

In a nutshell, a few EPICs:

- Run tests on different environments to validate Kaniko on OpenShift, KIND, and Kubernetes.
- Implement other build implementations such as [Spectrum](https://github.com/container-tools/spectrum), local Podman/Docker run, and [Source to Image](https://github.com/openshift/source-to-image).
- Implement the first stage: "Project Assemble" instead of relying on a pre-built, pre-configured image.
- Review the API interfaces and types to make sure that aligns with the build abstraction and can cover the majority of use cases.

Keep in mind that the end goal is to use this package anywhere you can run a Go application.

As we evolve, evaluate the package with Camel-K team to make sure that it can fit their use case the same way it does today with their embedded package.
That might require some work to remove the relation of the integration concept from the build tasks.

## Docker Registry configuration

If you want to connect on a remote Docker registry we must set the following environment variables:

- **DOCKER_HOST**: sets the url to the docker server.
- **DOCKER_API_VERSION**: sets the version of the API to reach, leave empty for latest.
- **DOCKER_CERT_PATH**: loads the TLS certificates from.
- **DOCKER_TLS_VERIFY**: enables or disable TLS verification, off by default.

otherwise a local docker registry will be used if nothing is present

## Podman Registry configuration

To connect on a remote Podman registry we can use one of the following uri connections:

- tcp://localhost:<port>
- unix:///run/podman/podman.sock
- ssh://<user>@<host>[:port]/run/podman/podman.sock?secure=True

To connect with a remote server we must set as environment variables the follows:
Envs

- CONTAINER_HOST
- CONTAINER_SSHKEY
- CONTAINER_PASSPHRASE

Otherwise, for local connection will be used the env var

- XDG_RUNTIME_DIR

with ROOTLESS access
unix://run/user/1000/podman/podman.sock

Note start the podman rootless socket with:
`systemctl --user start podman.socket`

To start podman root mode
`systemctl start podman.socket`

Problems on test with SELinux
`sudo setenforce Permissive`

Development debug
`podman --log-level=debug system service -t 0`

`journalctl --user --no-pager -u podman.socket`

Problems on mixing sysregistry v1/v2 is not supported
add on /etc/containers/registries.conf
`[[registry]]
insecure = true
location = "localhost:5000"`

NOTE on Registry container
TO enable the images deletion you need to set the following environment variable:

```bash
REGISTRY_STORAGE_DELETE_ENABLED=true
```

otherwise it will return an HTTP 405 error (Not Allowed).

## Kaniko Vanilla

Kaniko Vanilla is our API to run a Kaniko build outside Kubernetes Cluster
when it is needed to measure the time of a dockerfile to correctly improve the operations.

To run Kaniko locally first we need to start a local registry:

```sh
docker run -d -p 5000:5000 --name registry registry:latest
```

then after replaced <user> with your current user and <projectpath> with your current project path
run a build with

```sh
docker run  \
        --net=host \
        -v /<projectpath>/examples:/workspace \
        -v /home/<user>/.docker/config.json:/root/.docker/config.json \
        -e DOCKER_CONFIG=/root/.docker \
        gcr.io/kaniko-project/executor:latest \
        -f /workspace/dockerfiles/Kogito.dockerfile \
        -d localhost:5000/kaniko-test/kaniko-dockerfile_test_swf \
        --force \
        -c /workspace \
        --verbosity debug
```

to see the image in the container registry open your browser at the address:

```sh
http://localhost:5000/v2/_catalog
```

# Smoke tests

Smoke tests are using [Cucumber](https://cucumber.io/) and [Gherkin syntax](https://cucumber.io/docs/gherkin).

It is using the [Godog](https://github.com/DATA-DOG/godog) framework.

### Configuration

You can add some environment variables which will be taken into account by the tests:

* **OPERATOR_DEPLOY_FOLDER**

  URI where you can find operator deployment yaml files and crds
  Default: "../../deploy/" (as tests are run into "test/smoke" folder)

* **OPERATOR_CLI_PATH**

    URI where you can find the built cli binary
    Default: "../../build/_output/bin/kogito" (as tests are run into "test/smoke" folder)

* **OPERATOR_IMAGE_NAME** 

  Default: quay.io/kiegroup/kogito-cloud-operator

* **OPERATOR_IMAGE_TAG**

  Default to current version
  
* **MAVEN_MIRROR_URL**

  Url for Maven repository to be used in builds

* **KOGITO_BUILD_IMAGE_STREAM_TAG**

  Image tag to be used for building the application (e.g. `quay.io/mynamespace/myimage:tag`)
  Default to operator current version and Kiegroup images

* **KOGITO_BUILD_IMAGE_VERSION**

  Image version to be used for the Image Streams created internally by the operator
  Default to operator current version

* **LOCAL_TESTS**

  If you are launching the tests in local mode. It will use your 'USERNAME' to setup the namespace's name

* **KOGITO_EXAMPLES_REPOSITORY_URI**

    kogito-examples URI. Default to https://github.com/kiegroup/kogito-examples.

* **KOGITO_EXAMPLES_REPOSITORY_REF**

    If you want to use a specific tag or branch.

### Run

* Install godog: https://github.com/DATA-DOG/godog
* Go to `./go`
* Authenticate to OCP cluster (need clusteradmin to install crds if not available)
* export MAVEN_MIRROR_URL if needed => `export MAVEN_MIRROR_URL=<uri>`
* export LOCAL_TESTS="true"
* Run `godog`

#### Run specific feature

`godog {PATH_TO_FEATURE}`

#### Run in concurrent mode

`godog -c {NUMBER_OF_CONCURRENT} -f progress`

**NOTE:** Format progress is the only one supported in concurrent mode.

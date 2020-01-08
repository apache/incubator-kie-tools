# Smoke tests

Smoke tests are using [Cucumber](https://cucumber.io/) and [Gherkin syntax](https://cucumber.io/docs/gherkin).

Ti is using the [Godog](https://github.com/DATA-DOG/godog) framework.

### Configuration

You can add some environment variables which will be taken into account by the tests:

* **OPERATOR_DEPLOY_FOLDER**

  URI where you can find operator deployment yaml files and crds
  Default: "https://raw.githubusercontent.com/kiegroup/kogito-cloud-operator/master/deploy/"

* **OPERATOR_IMAGE_NAME** 

  Default: quay.io/kiegroup/kogito-cloud-operator

* **OPERATOR_IMAGE_TAG**

  Default to current version
  
* **MAVEN_MIRROR_URL**

  Url for Maven repository to be used in builds

* **KOGITO_BUILD_IMAGE_STREAM_[TAG|NAME|NAMESPACE]**

  Image stream tag / name / namespace to be used for building the application
  Default to current version
  You can also define 'KOGITO_BUILD_S2I_IMAGE_STREAM_*' and 'KOGITO_BUILD_RUNTIME_IMAGE_STREAM_*' separately

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

# Tests

Tests are using [Cucumber](https://cucumber.io/) and [Gherkin syntax](https://cucumber.io/docs/gherkin).

It is using the [Godog](https://github.com/cucumber/godog) framework.

## Run tests

* Authenticate to OCP cluster (need clusteradmin to install crds if not available)
* Run `../hack/run-tests.sh`

### Run specific feature

`../hack/run-tests.sh --feature {FEATURE}`

Example:

`../hack/run-tests.sh --feature "features/install_kogitoinfra.feature`

### Run using your own Kogito Operator repository

`../hack/run-tests.sh --operator_image quay.io/<namespace>/kogito-cloud-operator --operator_tag <version>`

Useful if you built your own image into your repository.

### Run in concurrent mode

`../hack/run-tests.sh --concurrent {NB_OF_CONCURRENT_TESTS}`

### Other options

Check the usage of the tests script:

`../hack/run-tests.sh -h`

## Development

### Useful Extensions for VS Code

- [Cucumber (Gherkin)](https://marketplace.visualstudio.com/items?itemName=alexkrechik.cucumberautocomplete))
- [Go](https://github.com/microsoft/vscode-go) extension
- [Go Documentation](https://github.com/msyrus/vscode-go-doc)


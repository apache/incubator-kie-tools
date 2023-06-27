# BDD tests

Tests in this module are a rewrite and enhancement of the end-to-end tests available in the `/test` directory.
They use [Godog](https://github.com/cucumber/godog) framework which is the official [Cucumber](https://cucumber.io/) BDD framework for Go and use [Gherkin](https://cucumber.io/docs/gherkin/) for writing test scenarios.

Tests also make use of the [kogito-operator](https://github.com/kiegroup/kogito-operator/tree/main/test) framework for Kubernetes which simplifies communication with a Kubernetes cluster.

## Run tests

Tests currently support Minikube with OpenShift support coming.

### Prerequisites
Specific prerequisites for running tests:
* `oc` installed
* `~/.kube/config` targeting the Minikube cluster

### Run devMode tests
`go test -v --tests.cr_deployment_only=true --tests.dev.local_cluster=true --tests.show_scenarios=true --godog.tags="devMode" --tests.operator_yaml_uri=../operator.yaml --tests.operator_image_tag=quay.io/kiegroup/kogito-serverless-operator-nightly:1.40.x-2023-07-10 --tests.dev.namespace_name=my-namespace`

If you want to have a more readable format, you can specify the `--godog.format=pretty` parameter. You can also specify your own operator image. Namespace is always created automatically, however, you can provide its name as in the command above, otherwise it will be automatically generated.

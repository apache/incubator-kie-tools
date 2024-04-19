# BDD tests

Tests in this module are a rewrite and enhancement of the end-to-end tests available in the `/test` directory.
They use [Godog](https://github.com/cucumber/godog) framework which is the official [Cucumber](https://cucumber.io/) BDD framework for Go and use [Gherkin](https://cucumber.io/docs/gherkin/) for writing test scenarios.

Tests also make use of the [BDD Framework](https://github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/tree/main/bddframework) for Kubernetes which simplifies communication with a Kubernetes cluster. This framework is an extract of the original framework available at [github.com/kiegroup/kogito-operator](https://github.com/kiegroup/kogito-operator/tree/main/test).

## Run tests

**Prerequisites:**

- `oc` installed
- Minikube or OpenShift running
- `~/.kube/config` targeting the cluster

Command to run tests has the following format:

```bash
$ make run-tests [key=value]*
```

You can set these optional keys:

<!--- tests configuration -->

- `feature` is a specific feature you want to run.  
  If you define a relative path, this has to be based on the "test" folder as the run is happening there.
  _Default are all enabled features from 'test/features' folder_  
  Example: feature=features/operator/deploy_quarkus_service.feature
- `tags` to run only specific scenarios. It is using tags filtering.  
  _Scenarios with '@disabled' tag are always ignored._  
  Expression can be:

  - "@wip": run all scenarios with wip tag
  - "~@wip": exclude all scenarios with wip tag
  - "@wip && ~@new": run wip scenarios, but exclude new
  - "@wip,@undone": run wip or undone scenarios

  Complete list of supported tags and descriptions can be found in [List of test tags](#list-of-test-tags)

- `concurrent` is the number of concurrent tests to be run.  
  _Default is 1._
- `timeout` sets the timeout in minutes for the overall run.
  _Default is 240 minutes._
- `debug` to be set to true to activate debug mode.
  _Default is false._
- `load_factor` sets the tests load factor. Useful for the tests to take into account that the cluster can be overloaded, for example for the calculation of timeouts.  
  _Default is 1._
- `ci` to be set if running tests with CI. Give CI name.
- `cr_deployment_only` to be set if you don't have a CLI built. Default will deploy applications via the CLI.
- `load_default_config` sets to true if you want to directly use the default test config (from test/.default_config)
- `format` sets the Godog output format, possible values are 'pretty' or 'junit'. _Default is junit._
- `container_engine` engine used to interact with images and local containers.
  _Default is docker._
- `domain_suffix` domain suffix used for exposed services. Ignored when running tests on Openshift.
- `http_retry_nb` sets the retry number for all HTTP calls in case it fails (and response code != 500).
  _Default is 3._
- `olm_namespace` Set the namespace which is used for cluster scope operators. Default is 'openshift-operators'.
<!--- operator information -->
- `operator_image_tag` is the Operator image full name.
- `operator_installation_source` Defines what source is used to install the SonataFlow operator. Available options are `olm` and `yaml`.
  _Default is yaml_.
- `operator_catalog_image` Specifies catalog image containing SonataFlow operator bundle. Needs to be specified when `operator_installation_source` is set to `olm`.
- `use_product_operator` Set true if you want to run tests using product operator.
<!--- files/binaries -->
- `operator_yaml_uri` Url or Path to operator.yaml file.
_Default is ../operator.yaml_.
<!--- development options -->
- `show_scenarios` sets to true to display scenarios which will be executed.  
  _Default is false._
- `show_steps` sets to true to display scenarios and their steps which will be executed.  
  _Default is false._
- `dry_run` sets to true to execute a dry run of the tests, disable crds updates and display the scenarios which will be executed.  
  _Default is false._
- `keep_namespace` sets to true to not delete namespace(s) after scenario run (WARNING: can be resources consuming ...).  
  _Default is false._
- `namespace_name` to specify name of the namespace which will be used for scenario execution (intended for development purposes).
- `local_cluster` to be set to true if running tests using a local cluster.
  _Default is false._
- `local_execution` to be set to true if running tests in local using either a local or remote cluster.
  _Default is false._

Logs will be shown on the Terminal.

To save the test output in a local file for future reference, run the following command:

```bash
make run-tests 2>&1 | tee log.out
```

#### Running BDD tests with the current branch

```
$ make
$ make container-build
$ podman tag quay.io/kiegroup/kogito-serverless-operator-nightly:latest quay.io/{USERNAME}/kogito-serverless-operator-nightly:latest
$ podman push quay.io/{USERNAME}/kogito-serverless-operator-nightly:latest
$ make run-tests cr_deployment_only=true local_cluster=true operator_image_tag=quay.io/{USERNAME}/kogito-serverless-operator-nightly:latest
```

**NOTE:** Replace {USERNAME} with the username/group you want to push to. Podman needs to be logged in to quay.io and be able to push to your username/group. If you want to use docker, just append `BUILDER=docker` to the `make container-build` command.

#### Running smoke tests

The BDD tests do provide some smoke tests for a quick feedback on basic functionality:

```bash
$ make run-smoke-tests [key=value]*
```

It will run only tests tagged with `@smoke`.
All options from BDD tests do also apply here.

#### Running devMode tests

```bash
make run-tests cr_deployment_only=true local_cluster=true show_scenarios=true tags=devMode  namespace_name=my-namespace operator_image_tag=quay.io/kiegroup/kogito-serverless-operator-nightly:latest
```

If you want to have a more readable format, you can specify the `format=pretty` parameter. You can also specify your own operator image. Namespace is always created automatically, however, you can provide its name as in the command above, otherwise it will be automatically generated.

#### List of test tags

| Tag name  | Tag meaning                                               |
| --------- | --------------------------------------------------------- |
| @smoke    | Smoke tests verifying basic functionality                 |
| @disabled | Disabled tests, usually with comment describing reasons   |
|           |                                                           |
| @devMode  | Tests verifying dev mode profile of the deployed workflow |

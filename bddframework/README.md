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

`../hack/run-tests.sh --operator_image quay.io/<namespace>/kogito-operator --operator_tag <version>`

Useful if you built your own image into your repository.

### Run in concurrent mode

`../hack/run-tests.sh --concurrent {NB_OF_CONCURRENT_TESTS}`

### Run Kogito Runtime tests

The Kogito Runtime tests have been disabled because it cannot be integrated to either the CI pipelines yet. 
In order to run these tests locally, it requires:
- an Openshift or Kubernetes cluster
- an Image registry (quay.io as an example)

We first need to enable the feature by removing the "@disabled" annotation in "features/deploy_kogito_runtime.feature".
Then, we need to login to the image registry. See example using quay.io:

```
docker login -u <your_username> -p <your_password> quay.io
```

And finally, we run these tests by:

```
../hack/run-tests.sh --feature "features/deploy_kogito_runtime.feature" --runtime_application_image_namespace <namespace> --runtime_application_image_registry <registry>
```

> Note that if we use quay.io, the repositories must be public **before** running the tests. Concretely, the repositories that will be used are:
> - https://quay.io/<namespace\>/process-springboot-example
> - https://quay.io/<namespace\>/process-quarkus-example

### Other options

Check the usage of the tests script:

`../hack/run-tests.sh -h`

## Development

### Writing new BDD tests

New tests should be added as new scenarios in `features` folder.  
Existing steps for the scenarios can be found into the different `steps/*.go` files.  
If you need a new step for your scenario, you will need to add it to one of those files (or create a new one) and implement the functionality.

#### New example to handle

As KogitoRuntime is only taking care of getting a specific kogito image and run it in the cluster, the corresponding image needs to be existing into the runtime application registry.  
In case you need to add a new Kogito example, which has not been handled yet into the different existing scenarios, you will need to add a new feature/scenario into the `scripts/examples` folder, so the image can be built by the pipeline and pushed to the runtime application registry.

### Useful Extensions for VS Code

- [Cucumber (Gherkin)](https://marketplace.visualstudio.com/items?itemName=alexkrechik.cucumberautocomplete))
- [Go](https://github.com/microsoft/vscode-go) extension
- [Go Documentation](https://github.com/msyrus/vscode-go-doc)

### How to Debug BDD tests

- Install Delve go module:

```sh
go install -v github.com/go-delve/delve/cmd/dlv@latest
```

- Start the Delve process to launch our tests:

```sh
dlv test --headless --listen=:2345 --log --api-version=2 -- -godog.tags="" features/my_feature.feature
```

The program will wait until you launch the Debug mode from your favourite IDE (next step).

> It must run in the /test folder.
> Any arguments must be passed after the "--" token, for example: "-- -godog.tags="" -tests.services-image-version=0.9.0-rc2"  

**TIP:** If you are (likely) running BDD tests using `make` and you are not sure how are these parameters transformed
to `go test` parameters, you may try running the `make` command with `dry_run=true` which will print the `go test` command
along with its parameters. Then you can just copy parameters starting with the first `--godog` parameter, e.g. you will remove
`DEBUG=false go test ./hack/../test -v -timeout "240m"` from the original command. Then you add all these parameters after the `--` token as stated above.

#### Attaching a debugger from VS Code

- Run the command (press *F1*), type "Go: Install/Update Tools", select dlv, press Ok to install/update delve.
- Run the command (press *F1*), type "Debug: Open launch.json" with *attach* mode:

```json
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Launch",
            "type": "go",
            "request": "attach",
            "remotePath": "${workspaceFolder}",
            "mode": "remote",
            "port": 2345,
            "host": "127.0.0.1"
        }
    ]
}
```

- Launch Debug from VS Code

Launch debug session by selecting Run/Start Debugging from top menu.

Further information [here](https://github.com/golang/vscode-go/blob/master/docs/debugging.md).

#### Attaching a debugger from GoLand/IntelliJ IDEA

- Create a remote debugger configuration as stated in the [Remote Debug Kogito Operator using Intellij IDEA](../README.md#remote-debug-kogito-operator-using-intellij-idea) section of the Operator README file.
- Launch the debugger configuration to connect to the waiting BDD tests. The tests should continue and after a while hit your breakpoint.

### Prune namespaces

In case you stopped a test execution, the test framework won't delete the namespaces in the cluster. So to avoid waste of resources in the cluster, you need to manually delete these namespaces. In order to ease this, now we have a namespace history log and an utility to do this by running:

```sh
cd test
go run scripts/prune_namespaces.go
``` 
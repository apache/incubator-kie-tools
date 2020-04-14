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

### How to Debug in VS Code

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

| If you didnt already have a launch.json file, this will create one with the below default configuration which can be used to debug the current package.

- Install Delve go module:

```sh
go get -v github.com/go-delve/delve/cmd/dlv
```

- Start the Delve process to launch our tests:

```sh
dlv test --headless --listen=:2345 --log --api-version=2 -- -godog.tags="" features/my_feature.feature
```

The program will wait until you launch the Debug mode from VS Code (next step).

| It must run in the /test folder
| Any arguments must be passed after the "--" token, for example: "-- -godog.tags="" -tests.services-image-version=0.9.0-rc2"

- Launch Debug from VS Code

Launch debug session by selecting Run/Start Debugging from top menu.

Further information [here](https://github.com/Microsoft/vscode-go/wiki/Debugging-Go-code-using-VS-Code).

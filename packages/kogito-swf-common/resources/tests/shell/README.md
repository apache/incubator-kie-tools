# Tests in Shell

## Running Tests With JBang

- Install JBang
- Install VSCode Red Hat's Java plugin
- Install VSCode JBang plugin

You can then edit the files in `kogito-swf-builder` and `kogito-swf-devmode` with intellisense.

The `run.sh` should be used to run the tests since it must set a few env vars. To run from your terminal, try:

```shell
tests/shell/run.sh kogito-swf-devmode quay.io/kiegroup/kogito-swf-devmode:999-SNAPSHOT
```

The first argument is the test case to run and the second, the image.

Under the hood, it uses [Junit's Console Launcher](https://junit.org/junit5/docs/current/user-guide/#running-tests-console-launcher) tool to run the tests from the command line.

Update this file with new findings, and don't remove the `.vscode` folder. It's useful to run JBang from the IDE.

<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

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

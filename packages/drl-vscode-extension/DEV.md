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

## Build

This package depends on the server uber-JAR produced by `@kie-tools/drools-lsp`, so build it through the workspace filter that includes dependencies:

```sh
pnpm -F drl-vscode-extension... build:dev
```

`build:prod` additionally packages the `.vsix` into `dist/`.

## Test

The E2E tests download VS Code via `@vscode/test-electron` and exercise the LSP features against the fixture project in `src/testFixture/`. The server JAR must have been built first (see Build above — the `...` filter suffix builds `@kie-tools/drools-lsp` too). Then:

```sh
pnpm -F drl-vscode-extension test
```

The `pretest` script rebuilds the extension bundle, compiles the test fixture, and compiles the test sources, so tests always run against the current sources.

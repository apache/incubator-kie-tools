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

## Base64 PNG Editor - Visual Studio Code Extension

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples%e2%80%8a-%e2%80%8ahow-to-create-a-vs-code-extension-for-the-custom-editor.html) a step-by-step tutorial of how this VS Code Extension was built.

This package is the VS Code Extension, which runs the Base64 PNG Editor.

### Pre requisites

This example requires the VS Code version 1.46 or later.

### Running

Install all dependencies and build the project on the **root** folder of this repository

```shell script
pnpm init
pnpm build:prod
```

- Debug mode

```shell script
// open this package with visual studio code
code .
// press F5 and wait to start
```

- Installing VSIX

The `pnpm build:prod` generate a vsix file on the `/dist` folder. Open your VS Code, and install it.

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

## DMN Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit DMN and SceSim files.

> NOTE: This extension provides two DMN Editors. The legacy one will be removed in future versions. See the details of the new DMN Editor [here](https://github.com/apache/incubator-kie-issues/issues/439).

## Features

- Create and edit DMN 1.2, 1.3, 1.4, and 1.5 (`.dmn`) files.
- Create and edit SceSim (`.scesim`) files with the Test Scenario Editor.
- Native keyboard shortcuts (Press `shift+/` to display available combinations).
- Export diagram to SVG (use the SVG icon on the top-right corner).

### Editing a new DMN file

![alt](./gifs/dmn.gif?raw=true)

### Editing a SceSim file

DMN files must be inside a `src/` folder on your Workspace to be visible on the Test Scenario Editor.

![alt](./gifs/scesim.gif?raw=true)

### Settings

| Setting                          | Description                                              | Default value                        |
| -------------------------------- | -------------------------------------------------------- | ------------------------------------ |
| `kogito.dmn.runOnSave`           | Execute a command on each save operation of the DMN file | _empty_                              |
| `kogito.dmn.svgFilenameTemplate` | Filename template to be used when generating SVG files   | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.dmn.svgFilePath`         | Where to save generated SVG files                        | `${fileDirname}`                     |

The `kogito.dmn.svgFilenameTemplate` and `kogito.dmn.svgFilePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |

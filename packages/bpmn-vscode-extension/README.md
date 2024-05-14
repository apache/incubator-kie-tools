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

## BPMN Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit BPMN and BPMN2 files.

## Features

- Create and edit BPMN (`.bpmn`) and BPMN2 (`.bpmn2`) files.
- Native keyboard shortcuts (Press `shift+/` to display available combinations).
- Export diagram to SVG (use the SVG icon on the top-right corner).

### Editing a new BPMN file

![alt](./gifs/bpmn.gif?raw=true)

### Settings

| Setting                           | Description                                               | Default value                        |
| --------------------------------- | --------------------------------------------------------- | ------------------------------------ |
| `kogito.bpmn.runOnSave`           | Execute a command on each save operation of the BPMN file | _empty_                              |
| `kogito.bpmn.svgFilenameTemplate` | Filename template to be used when generating SVG files    | `${fileBasenameNoExtension}-svg.svg` |
| `kogito.bpmn.svgFilePath`         | Where to save generated SVG files                         | `${fileDirname}`                     |

The `kogito.bpmn.svgFilenameTemplate` and `kogito.bpmn.svgFilePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |

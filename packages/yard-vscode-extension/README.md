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

## Kogito yard Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit yard (Yet Another Rule Definition) files (\*.yard.yaml, \*.yard.yml).

## Features

- Create and edit yard definition files

### Settings

| Setting                                                         | Description                                                                                                                                                                                                               | Default value                                                                    |
| --------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `kogito.yard.runOnSave`                                         | Execute a command on each save operation of the yard file                                                                                                                                                                 | _empty_                                                                          |
| `kogito.yard.automaticallyOpenDiagramEditorAlongsideTextEditor` | When opening yard files, decide whether or not to open the Diagram Editor alongside the text editor. Regardless of the configured option, you can always open the yard Diagram Editor using the 'Open as Diagram' button. | `Ask next time` (possible: `Open automatically`, `Do not open`, `Ask next time`) |

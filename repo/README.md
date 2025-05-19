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

This directory contains files related to the `kie-tools` monorepo itself.

#### Manual

The manual for the `kie-tools` repository. See [MANUAL.md](./MANUAL.md)

#### Repo dependencies version

The related `build-dependencies-version.json` contains a centralized view of all required tools to correctly
build the project. CI\CD tasks should rely on this file only, avoiding to hard-coding these version or store
them in another place. Be aware that any key addition, change or removal can have an impact on CI\CD process
that rely on this file. Any tool version update must be updated on this file as well, to guarantee the same
condition in all building processes.

#### Packages dependency graph

![This repository's packages dependency graph](https://g.gravizo.com/source/svg?https%3A%2F%2Fraw.githubusercontent.com%2Fapache%2Fincubator-kie-tools%2Fmain%2Frepo%2Fgraph.dot)

Nodes:

- `Blue`: Packages that are published to the NPM registry.
- `Purple`: Core packages that are published to the NPM registry.
- `Dotted black`: Packages that are deployed to other mediums.
- `Dotted orange`: Examples packages.

Edges:

- `Dotted black`: devDependency.
- `Solid black`: dependency.

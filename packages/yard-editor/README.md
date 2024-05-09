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

## YARD editor

###### An editor to manage YARD (Yet Another Rule Definition) assets, part of serverless logic for Red Hat OpenShift

### Description

This module contains the core implementation of the **yard editor**, which allows the user
to create and edit **yard** definition files _(\*.yard.yaml, \*.yard.yml)_.

### Structure

The module structure is composed of:

- **dev-webapp**: This represents a showcase of the editor, useful for testing and demo purposes.
  Here, the editor is wrapped in a static HTML page with a dedicated menu. The menu is necessary to
  simulate functionality implemented by channel which integrate the editor _(eg. Undo / Redo / Validation)_
- **src**: The project source code
- **test**: The project tests assets

### Usage

To build the module: `pnpm build:prod`

To build the module in dev mode (no tests): `pnpm build:dev`

To run the development webapp: `pnpm start`

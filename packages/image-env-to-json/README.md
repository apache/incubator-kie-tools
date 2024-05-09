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

# image-env-to-json

This package contains a CLI tool to convert environment variables to a JSON file.

It is designed mainly to be used by container images.

## Build

Run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/image-env-to-json...
```

The output artifacts will be a JS version and a standalone executable in the `packages/image-env-to-json/dist` directory.

## Usage

```
$ image-env-to-json [options]

Options:
  -V, --version                output the version number
  -d, --directory <directory>  directory to create or update an existing env.json file
  -n, --names <names...>       environment variable names to look for
  -h, --help                   display help for command
```

## Example

Suppose the host environment contains the following environment variables:

```
ENV_A=value_a
ENV_B=value_b
```

When running:

```bash
$ image-env-to-json -d /my/directory -n ENV_A ENV_B ENV_C
```

The following JSON content will be written to `/my/directory/env.json`:

```json
{
  "ENV_A": "value_a",
  "ENV_B": "value_b"
}
```

**Note**: Only existing environment variables will be written to the file.

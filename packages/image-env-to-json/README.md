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

It is intended for use within a Linux container.

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
  -v, --version                output the version number
  -d, --directory <directory>  directory to create or update an existing env.json file
  --json-schema <directory>    directory of the JSON Schema to be used during the `env.json` creation/update
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
$ image-env-to-json --directory /path/to/env/json/dir --json-schema /path/to/schema.json
```

The following JSON content will be written to `/path/to/env/json/dir/env.json`:

```json
{
  "ENV_A": "value_a",
  "ENV_B": "value_b"
}
```

**Note**: Only existing environment variables will be written to the file.

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html

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

## Form Code Generator Bootstrap4 Theme

This package is the Bootstrap4 theme for the [form-code-generator](../form-code-generator/README.md) package.

## How it works?

This package generates the form code of a [Uniforms](https://uniforms.tools/) form using the Bootstrap4 style. [Uniforms](https://uniforms.tools/) is a library that autogenerates forms based on schemas and supports multiple themes. This package makes its own theme, describing the code that each field should have. After it, the form is rendered using `ReactDOMServer.renderToString` getting the form code.

## Usage

You can consume this package in two ways:

1. The `dist/theme.ts` file exports the `bootstrap4FormCodeGeneratorTheme` which is a theme for the `form-code-generator` package. This theme implements the `FormCodeGeneratorTheme` interface, and its `generate` function uses JSON Schemas to generate the form code.

2. You can create your own theme, and implement the `FormCodeGeneratorTheme` interface, and make the `generate` fucntion use another type of schema that is supported by [Uniforms](https://uniforms.tools/). To do so, you must use the `dist/uniforms/renderForm` function, which will receive a [Uniforms Bridge](https://uniforms.tools/docs/api-bridges/) and some parameters:

```ts
{
  id: string;             // The form id
  sanitizedId: string;    // The form id, any # is replaced by _
  disabled?: boolean;     // Enable/disable form (read only)
  placeholder?: boolean;  // Enable/disable placeholders
  schema: Bridge;         // A Uniforms Bridge instance
}
```

## Build

In order to build the library you must run the following command in the root folder of the repository:

```shell script
pnpm -F @kie-tools/form-code-generator-bootstrap4-theme... build:prod
```

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

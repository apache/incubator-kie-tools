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

## Form Code Generator

This library is used to generate the form code based on a schema. It uses different themes, such as the [Bootstrap4](../form-code-generator-bootstrap4-theme/README.md) and [PatternFly](../form-code-generator-patternfly-theme/README.md) or any theme that implements the [FormCodeGeneratorTheme](./src/types.ts) interface.

## How it works?

This library provides types and interfaces to be used by themes. The `generateFormCode` function receives a list of schemas where the theme will be applied. The generated code or error are returned to the caller.

## Usage

To use it, call the `generateFormCode` function passing your theme to its arguments:

```ts
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode"
import { FormCodeGeneratorTheme } from "@kie-tools/form-code-generator/dist/types"

const formCode = generateFormCode({
  formSchemas: [
    {
      formSchema: {
        name: "my form name",   // Form name
        schema: {},             // My form schema. The theme determines which kind of schema will be supported.
      },
    },
  ],
  formCodeGeneratorTheme: {
    generate: ({ name, schema }) => { ... };
  },
});

formsCode[0];                 // FormAsset | FormGenerationError
```

Example using the [PatternFly](../form-code-generator-patternfly-theme/README.md) theme:

```ts
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { patternflyFormCodeGeneratorTheme } from "@kie-tools/form-code-generator-patternfly-theme/dist/theme";

const formsCode = generateFormCode({
  formSchemas: [
    {
      formSchema: {
        name: "my patternfly form", // Form name
        schema: {}, // My form JSON Schema.
      },
    },
  ],
  formCodeGeneratorTheme: patternflyFormCodeGeneratorTheme,
});

formsCode[0]; // FormAsset | FormGenerationError
```

## Build

In order to build the library you must run the following command in the root folder of the repository:

```shell script
pnpm -F @kie-tools/form-code-generator... build:prod
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

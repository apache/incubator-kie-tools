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

## jBPM Form Code Generator

This package has two jBPM themes for the [form-code-generator](../form-code-generator/README.md) library. Both themes are extensions of the [Bootstrap4 theme](../form-code-generator-bootstrap4-theme/README.md) and [PatternFly theme](../form-code-generator-patternfly-theme/README.md), resulting in the jBPM Bootstrap4 theme and jBPM PatternFly theme respectively.

## Usage

To use it, pass the jBPM theme to the `generateFormCode` function:

```ts
import { generateFormCode } from "@kie-tools/form-code-generator/dist/generateFormCode";
import { jbpmPatternflyFormCodeGeneratorTheme } from "@kie-tools/jbpm-form-code-generator-themes/dist/jbpmPatternflyFormCodeGeneratorTheme";

const jbpmFormsCode = generateFormCode({
  formCodeGeneratorTheme: jbpmPatternflyFormCodeGeneratorTheme,
  formSchemas: [
    {
      name: "<name>",
      schema: {}, // Your JSON Schema
    },
  ],
});
```

The `jbpmFormsCode` will give you the following object:

```ts
[{
  formAsset: JbpmFormAssets | undefined
  formError: FormCodeGenerationError | undefined
}]
```

`JbpmFormAssets` is a object with the following properties:

```ts
{
  name: string,                             // The form id
  nameWithoutInvalidTsVarChars: string,     // The same value as "id" but any "#" occorrence is replaced by "_"
  fileName: string,                         // The form name
  fileNameWithoutInvalidVarChars: string,      // The same value as "assetName" but any "#" occorrence is replaced by "_"
  fileExt: string,                          // The file extension of the code
  content: string,                          // The unescaped form code
  config: {
    schema: string,                         // The stringifyied JSON Schema
    resources: {
      styles: {},                           // Any style that need to be loaded
      scripts: {},                          // Any script that need to be loaded
    },
  },
}
```

`FormCodeGenerationError` is a object with the following properties:

```ts
{
  error: Error; // The error object that was thrown during the form generation
}
```

## Build

In order to build the library you must run the following command in the root folder of the repository:

```shell script
pnpm -F @kie-tools/jbpm-form-code-generator-themes... build:prod
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

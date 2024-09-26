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

## jBPM Form Generator

This is a utility library to help generating forms in differents formats for BPMN processes and User Tasks in your Kogito projects.

### How does it work?

When building a Kogito project, Kogito generates JSON Schemas to represent the data models for both Processes and User Tasks.

This tool locates those JSON Schemas in the project and taking advantage of the [Uniforms](https://uniforms.tools) APIs, processes them and generate static forms.

### Form Types

There are two types of form supported with differnt styling and output format:

- **Patternfly**: generates a React (`.tsx`) forms using Patternfly 4 components. Implementation can be found in `@kie-tools/uniforms-patternfly-codegen`

- **Bootstrap 4**: generates a HTML (`.html`) forms using Bootstrap 4 styling. Implementation can be found in `@kie-tools/uniforms-bootstrap4-codegen`

Each generated form consist in two files, the source code (`.tsx` or `.html`) and a companion `.config` file that defines the external resources (`css` / `js`) the form may need.

## Build

In order to build the library you must run the following command in the root folder of the repository:

```shell script
pnpm -F @kie-tools/jbpm-form-generator... build:prod
```

After the command, you can import the `generateForms` function into your codebase. It requires two arguments

```tsx
generateForms({ type, formSchemas }: Args): (FormAsset | FormGenerationError)[]
```

The `type` argument defines which kind of form will be generated. Currently, we have native support for `"bootstrap"` and `"patternfly"` but more can be added.

The `formSchemas` argument is a list of JSON Schemas which will be used to generate the form code.

`generateForms` will return a list of form assets, which contain the `form` and `config` file content.

## Adding custom themes

To add custom themes use the `registerFormGeneratorType` method, which requires a class that implements the following interface:

```tsx
export interface FormGenerator {
  type: string;
  generate: (formSchema: FormSchema) => FormAsset | FormGenerationError;
}
```

## Using the Custom Forms with Runtime Tools Quarkus Extension

If your project is a Quarkus based Kogito project, you can use and test them by using the **Runtime Tools Quarkus Extension**.

To do so, just add the following dependency in your project `pom.xml`:

```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>runtime-tools-quarkus-extension</artifactId>
  <version>${version}</version>
</dependency>
```

And start the project in Dev mode with the command:

```shell script
mvn clean quarkus:dev
```

For more information on how to setup the **Runtime Tools Quarkus Extension** in your project look at the oficial Kogito [documentation](https://docs.kogito.kie.org/latest/html_single/#con-runtime-tools-dev-ui_kogito-developing-process-services).

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

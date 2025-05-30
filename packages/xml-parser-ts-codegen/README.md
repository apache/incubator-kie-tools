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

# @kie-tools/xml-parser-ts-codegen

This package provides a Node.js script that generates TypeScript types and metadata from XSD file(s) to parse/build XML strings from/to JSON using `@kie-tools/xml-parser-ts`. It doesn't implement the entire XSD specification, so there might be bugs when attempting to use it with an arbitrary XSD file.

For more information about `@kie-tools/xml-parser-ts`, go to [../packages/xml-parser-ts](../xml-parser-ts/README.md)

It was created to provide a way to marshall/unmarshall BPMN, DMN, Test Scenario, and PMML files, so it is only tested against those formats XSDs.

> NOTE: This package is probably not suited for your use-case, as it is incomplete and was not tested with a large number of XSD files, but it should work for simple XSDs. If you want to use it, please go ahead! Let us know if you tried and it worked! Feel free to contribute too with issues and PRs too.

> NOTE: Since this package itself uses `@kie-tools/xml-parser-ts` to parse XSD files, in theory, it's possible to use this package to generate types and metadata for XSDs themselves. It was not done yet, so I'm pretty sure it wouldn't work without a few modifications, but it should be possible. Feel free to try it and contribute with a PR.

### Features:

1. Supports substitution groups.
1. Supports recursive types.
1. Supports anonymous element types (max depth: 2)
1. Supports `<xsd:import>` and `<xsd:include>` tags.
1. Supports multiple namespaces.
1. The generated types support extensions on types that contain `<xsd:any>` and `<xsd:anyAttribute>`.

### Usage:

`npx @kie-tools/xml-parser-ts-codegen relative/path/to/your-xsd-file.xsd rootElementName`

Types and metadata will be written to the same folder of `your-xsd-file.xsd`, at the `ts-gen` directory. Two files are going to be generated:

1. types.ts
   - Contains the types representing the JSON obtained from parsing an XML document.
   - Extensible types are interfaces, so you can extend them with `declare module ...`
   - Hierarchies are flattened, to make it simple to extend the exact type wanted.
2. meta.ts
   - `root`: Root element type and name
   - `ns`: Bi-directional Namespace mapping created from the XSD file(s).
   - `meta`: Type information to be used in runtime by `@kie-tools/xml-parser-ts`. It's main use is to determine whetever an element/attribute is a string/boolean/float/integer or an array, so the XMLs are parsed and built consistently.

### Future:

- Generate JSON Schemas to validate the parsed JSON file with relevant information present on the original XSDs.
- Resolve substitutionGroups with union types instead of optional properties.
- Add `<xsd:annotation>` information to the generated TypeScript types.

PRs are welcome!

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

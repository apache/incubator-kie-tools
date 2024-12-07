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

# Example :: Micro-frontends Multiplying Architecture :: Base64 PNG Editor VS Code Extension

This package provides a VS Code Extension containing a Base64 PNG Editor, which allows editing `.base64png` files.

To make it easier to create `.base64png` files and exporting them to other formats, this extension also provides two commands:

1. `Base64 PNG: New from .png`
   - Creates a new `.base64png` file from a PNG. You can right-click a PNG file to run this command.
1. `Base64 PNG: Save as SVG`
   - When a `.base64png` is open, you can click the little SVG icon at the top-right corner of the Editor, or run it from the command palette. A new SVG file will be created at the same directory as the `.base64png` file.

### Building the dependencies

Before running this example, building its dependencies is required. Run the following command on a Terminal:

```shell script
KIE_TOOLS_BUILD__buildExamples=true pnpm -F kie-tools-examples-micro-frontends-multiplying-architecture-base64png-editor-vscode-extension^... build:dev
```

### Building

```shell script
KIE_TOOLS_BUILD__buildExamples=true pnpm build:prod
```

A `.vsix` file will be on the `dist` folder. Drag it to the Extensions panel on VS Code to install it.

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

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

# VS Code Java Code Completion Extension Plugin

## Installation

This project extends the JDT.LS by providing an extension point for `org.eclipse.jdt.ls.core.delegateCommandHandler`.

This project is built using [Eclipse Tycho](https://www.eclipse.org/tycho/) and requires at least [maven 3.0](http://maven.apache.org/download.html) to be built via CLI.

Simply run :

    mvn install

The first run will take quite a while since maven will download all the required dependencies in order to build everything.

## Usage

Once compiled you need to copy the generated JAR in a folder inside the extension, and you need to configure that location path in _contributes_ section in package.json

```json
"contributes": {
    "javaExtensions": [
      "./dist/server/vscode-java-code-completion-extension-plugin-core.jar"
    ],
    ...
}
```

and you also need `redhat.java` as extension dependency:

```json
 "extensionDependencies": [
    "redhat.java"
  ]
```

Once done that, the Language Server will automatically recognize the new plugin.

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

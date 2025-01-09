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

## Apache KIE™ Serverless Workflow Editor

![vs-code-support](https://img.shields.io/badge/Visual%20Studio%20Code-1.67.0+-blue.svg)
![github-ci](https://github.com/apache/incubator-kie-tools/actions/workflows/ci_build.yml/badge.svg)

Create and edit Serverless Workflow definition files (\*.sw.json, \*.sw.yaml, \*.sw.yml).

## Features

- Create and edit CNCF Serverless Workflow v0.8 definition files
- Real-time diagram preview rendering
- Editor code-lens to assist the authoring of your workflow
- Contextual auto-complete
- Open API auto-completion to your functions
- Integration with Red Hat Hybrid Console Authentication
- Red Hat Service Catalog Integration
- Automatically export diagram to SVG (see below for Settings)

#### Editing a new Serverless Workflow file

![alt](./gifs/sw.gif?raw=true)

### Settings

| Setting                                                        | Description                                                                                                                                                                                                                                             | Default value                                                                    |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `kogito.swf.runOnSave`                                         | Execute a command on each save operation of the SW file                                                                                                                                                                                                 | `extension.kogito.swf.silentlyGenerateSvg`                                       |
| `kogito.swf.svgFilenameTemplate`                               | Filename template to be used when generating SVG files                                                                                                                                                                                                  | `${fileBasenameNoExtension}.svg`                                                 |
| `kogito.swf.svgFilePath`                                       | Where to save generated SVG files                                                                                                                                                                                                                       | `${fileDirname}`                                                                 |
| `kogito.swf.specsStoragePath`                                  | Directory where spec files are stored (defaults to a 'specs' directory in the same path as the Serverless Workflow file).                                                                                                                               | `${fileDirname}/specs`                                                           |
| `kogito.swf.serviceRegistries`                                 | List of Service Registries to fetch artifacts that improve the functions autocompletion mechanism.                                                                                                                                                      | `(empty)`                                                                        |
| `kogito.swf.shouldReferenceServiceRegistryFunctionsWithUrls`   | When adding a function coming from a Service Registry, use its URL to reference it, instead of downloading the file.                                                                                                                                    | `false`                                                                          |
| `kogito.swf.automaticallyOpenDiagramEditorAlongsideTextEditor` | When opening Serverless Workflow files, decide whether or not to open the Diagram Editor alongside the text editor. Regardless of the configured option, you can always open the Serverless Workflow Diagram Editor using the 'Open as Diagram' button. | `Ask next time` (possible: `Open automatically`, `Do not open`, `Ask next time`) |

The `kogito.swf.svgFilenameTemplate`, `kogito.swf.svgFilePath`, and `kogito.swf.specsStoragePath` settings accept the following variables as tokens:

| Variable                       | Example                                   |
| ------------------------------ | ----------------------------------------- |
| **${workspaceFolder}**         | `/home/your-username/your-project`        |
| **${fileDirname}**             | `/home/your-username/your-project/folder` |
| **${fileExtname}**             | `.ext`                                    |
| **${fileBasename}**            | `file.ext`                                |
| **${fileBasenameNoExtension}** | `file`                                    |

The Service Registries configured in the `kogito.swf.serviceRegistries` setting expects a value following the following schema:

```json
{
  "type": "object",
  "properties": {
    "registries": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "name": { "type": "string" },
          "url": {
            "type": "string",
            "format": "uri",
            "pattern": "^https?://?[-A-Za-z0-9+&@#/%?=_!:.]+[-A-Za-z0-9+&@#/%=~_|]"
          },
          "authProvider": {
            "type": "string",
            "enum": ["none", "red-hat-account"],
            "default": "none"
          }
        },
        "required": ["name", "url", "authProvider"]
      }
    }
  }
}
```

### Thank you

Some Open Source projects were vital for the development of this extension, and we would like to thank all of them, with highlights to:

- [CNCF Serverless Workflow SDK Typescript](https://github.com/serverlessworkflow/sdk-typescript): for workflow parsing;
- [Mermaid](https://mermaid-js.github.io/): for workflow rendering and visualization;
- [Apicurio](https://www.apicur.io/): for our service catalog integration;

You can also checkout the [CNCF Serverless Workflow VS Code extension](https://marketplace.visualstudio.com/items?itemName=serverlessworkflow.serverless-workflow-vscode-extension) at VS Code Marketplace.

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

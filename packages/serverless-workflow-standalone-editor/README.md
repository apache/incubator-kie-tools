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

## Serverless Workflow Standalone Editors

### Description

This library provides standalone Serverless Workflow Editor (one all-in-one JavaScript file) that can be embedded into any web application.

A comprehensive API is also provided for setup and interaction with the Editor.

### Installation

- To add it to your `package.json` file:

  - `npm install @kie-tools/serverless-workflow-standalone-editor`

- To import SWF Editor library:

  - `import * as SwfEditor from "@kie-tools/serverless-workflow-standalone-editor/dist/swf"`

### Usage

Here is an example on how to open the Serverless Workflow Standalone Editor using the provided API:

```
const editor = SwfEditor.open({
      container: document.getElementById("swf-editor-container"),
      initialContent: Promise.resolve(""),
      readOnly: false,
      languageType: "json",
      swfPreviewOptions: { editorMode: "diagram", defaultWidth: "100%" }
    });
```

Available parameters:

- `container`: HTML element in which the Editor will be appended to.
- `initialContent`: Promise to a workflow content. Can be empty. Examples:
  - `Promise.resolve("")`
  - `Promise.resolve("<SWF_CONTENT_DIRECTLY_HERE>")`
- `readOnly` (optional, defaults to `false`): Use `false` to allow content edition, and `true` for read-only mode, in which the Editor will not allow changes.
- `languageType` (required, defaults to `json`): Use `json`/`yaml` to render text editor with serverless workflow content in json/yaml format and stunner editor to display diagram.
- `swfPreviewOptions` (optional, defaults to `{ editorMode: "full", defaultWidth: "50%" }`): You can use this option to change the `editorMode` (optional, defaults to `full`) and `defaultWidth` (optional, defaults to `50%`). If you want to render only the diagram editors into the web application and hide the text editor, you can use this parameter and set the value `diagram`. To render the text editor and hide the diagram editor, you can use set the value `text`. To render both `diagram` and `text` together, you can set the value `full`.

The returned object will contain the methods needed to manipulate the Editor:

- `getContent(): Promise<string>`: Returns a Promise containing the Editor content.
- `setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>`: Sets the content of the Editor. The returning Promise will be rejected if setting the content fails.
- `getPreview(): Promise<string>`: Returns a Promise containing the SVG string of the current diagram.
- `undo(): void`: Undo the last change in the Editor. This will also fire the subscribed callbacks of content changes.
- `redo(): void`: Redo the last undone change in the Editor. This will also fire the subscribed callbacks of content changes.
- `validate(): Promise<Notification[]>`: Validates the serverless workflow json/yaml content based on its schemas and it also includes custom validations specific to serverless workflow rules.
- `setTheme(theme: EditorTheme): Promise<void>`: This sets theme to the editors on the web application.

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

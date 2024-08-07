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

## DMN Standalone Editor

### Description

This library provides standalone DMN Editor (one all-in-one JavaScript file) that can be embedded into any web application.

A comprehensive API is also provided for setup and interaction with the Editor.

### Installation

- To add it to your `package.json` file:

  - `npm install @kie-tools/dmn-editor-standalone`

- To import the library:

  - `import * as DmnEditor from "@kie-tools/dmn-editor-standalone/dist"`

### Usage

Here is an example on how to open the DMN Editor:

```
const editor = DmnEditor.open({
  container: document.getElementById("dmn-editor-container"),
  initialContent: Promise.resolve(""),
  initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "model.dmn",
  readOnly: false,
  resources: new Map([
    [
      "MyIncludedModel.dmn",
      {
        contentType: "text",
        content: Promise.resolve("")
      }
    ]
  ])
});
```

Available parameters:

- `container`: HTML element in which the Editor will be appended to.
- `initialContent`: Promise to a DMN model content. Can be empty. Examples:
  - `Promise.resolve("")`
  - `Promise.resolve("<DIAGRAM_CONTENT_DIRECTLY_HERE>")`
  - `fetch("MyDmnModel.dmn").then(content => content.text())`
- `initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot`: The POSIX path relative to the workspace root (`/` by default), including the file name. The path is useful to locate the file in the workspace, espcially in relation to other resources (defined by the `resources` parameters). Examples:
  - `model.dmn`
  - `myPath/model.dmn`
  - `parent/child/filename.dmn`
- `readOnly` (optional, defaults to `false`): Use `false` to allow content edition, and `true` for read-only mode, in which the Editor will not allow changes.
- `origin` (optional, defaults to `*` when accessing the application with the `file` protocol, `window.location.origin` otherwise): If for some reason your application needs to change this parameter, you can use it.
- `onError` (optional, defaults to `() => {}`): If there's an error opening the Editor, this function will be called.

* `resources` (optional, defaults to `[]`): Map of resources that will be provided for the Editor. This can be used, for instance, to provide included models for the DMN Editor. Each entry in the map has the resource POSIX path (similar to the `initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot` parameter) as its key and an object containing the `content-type` (`text` or `binary`) and the resource `content` (Promise similar to the `initialContent` parameter) as its value. Resources located in a parent directory (in relation to the current content path, defined by the `initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot`) won't be listed to be used as an Included Model.

The returned object will contain the methods needed to manipulate the Editor:

- `getContent(): Promise<string>`: Returns a Promise containing the Editor content.
- `setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string): Promise<void>`: Sets the content of the Editor. The returning Promise will be rejected if setting the content fails.
- `getPreview(): Promise<string>`: Returns a Promise containing the SVG string of the current diagram.
- `subscribeToContentChanges(callback: (isDirty: boolean) => void): (isDirty: boolean) => void`: Setup a callback to be called on every content change in the Editor. Returns the same callback to be used for unsubscription.
- `unsubscribeToContentChanges(callback: (isDirty: boolean) => void): void`: Unsubscribes the passed callback from content changes.
- `markAsSaved(): void`: Resets the Editor state, signalizing that its content is saved. This will also fire the subscribed callbacks of content changes.
- `undo(): void`: Undo the last change in the Editor. This will also fire the subscribed callbacks of content changes.
- `redo(): void`: Redo the last undone change in the Editor. This will also fire the subscribed callbacks of content changes.
- `close(): void`: Closes the Editor.
- `envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>`: Advanced Editor API. See more details in [MessageBusClientApi](https://github.com/apache/incubator-kie-tools/blob/main/packages/envelope-bus/src/api/index.ts#L43-L56) and [KogitoEditorEnvelopeApi](https://github.com/apache/incubator-kie-tools/blob/main/packages/editor/src/api/KogitoEditorEnvelopeApi.ts#L34-L41).

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

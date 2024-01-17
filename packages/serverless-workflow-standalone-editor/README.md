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

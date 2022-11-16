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

Here is an example on how to open the Serverless Workflow standalone Editor using the provided API:

```
const editor = SwfEditor.open({
      container: document.getElementById("swf-editor-container"),
      initialContent: Promise.resolve(""),
      readOnly: false,
      languageType: "json",
      isDiagramOnly: true,
    });
```

Available parameters:

- `container`: HTML element in which the Editor will be appended to.
- `initialContent`: Promise to a Swf content. Can be empty. Examples:
  - `Promise.resolve("")`
  - `Promise.resolve("<SWF_CONTENT_DIRECTLY_HERE>")`
- `readOnly` (optional, defaults to `false`): Use `false` to allow content edition, and `true` for read-only mode, in which the Editor will not allow changes.
- `languageType` (required, defaults to `json`): Use `json` to render text editor with serverless workflow content in json format and stunner editor to display diagram. Use `yaml` to render text editor with serverless workflow content in yaml format and mermaid editor to display diagram.
- `isDiagramOnly` (optional, defaults to `false`): If you want to render only the diagram editors into the web application and hide the text editor, you can use this parameter and set the value `true`.

The returned object will contain the methods needed to manipulate the Editor:

- `getContent(): Promise<string>`: Returns a Promise containing the Editor content.
- `setContent(path: string, content: string): Promise<void>`: Sets the content of the Editor. The returning Promise will be rejected if setting the content fails.
- `getPreview(): Promise<string>`: Returns a Promise containing the SVG string of the current diagram.
- `undo(): void`: Undo the last change in the Editor. This will also fire the subscribed callbacks of content changes.
- `redo(): void`: Redo the last undone change in the Editor. This will also fire the subscribed callbacks of content changes.
- `validate(): Promise<Notification[]>`: Validates the serverless workflow json/yaml content based on its schemas and it also includes custom validations specific to serverless workflow rules.
- `setTheme(theme: EditorTheme): Promise<void>`: This sets theme to the editors on the web application.
- `getElementPosition(selector: string): Promise<Rect>`: Provides an alternative for extending the standard query selector when the element lives inside a canvas or even a video component. The `selector` parameter must follow the format of "\<PROVIDER\>:::\<SELECT\>“, e.g. “Canvas:::MySquare” or “Video:::PresenterHand”. Returns a `Rect` representing the element position.

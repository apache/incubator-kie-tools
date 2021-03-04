BPMN and DMN Standalone Editors
--

### Description

This library provides standalone DMN and BPMN Editors (one all-in-one JavaScript file each) that can be embedded into any web application.

A comprehensive API is also provided for setup and interaction with the Editor.

### Installation

* To add it to your `package.json` file:

  * `npm install @kogito-tooling/kie-editors-standalone`

* To import each Editor library:

  * `import * as DmnEditor from "@kogito-tooling/kie-editors-standalone/dist/dmn"`

  * `import * as BpmnEditor from "@kogito-tooling/kie-editors-standalone/dist/bpmn"`

### Usage

The API is the same for both editors. Here is an example on how to open the DMN Editor:

```
const editor = DmnEditor.open({
  container: document.getElementById("dmn-editor-container"),
  initialContent: Promise.resolve(""),
  readOnly: false,
  origin: "",
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

Parameters description:
* `container`: HTML element in which the Editor will be appended to.
* `initialContent`: Promise to a DMN model content. Can be empty. Examples:
  * `Promise.resolve("")`
  * `Promise.resolve("<DIAGRAM_CONTENT_DIRECTLY_HERE>")`
  * `fetch("MyDmnModel.dmn").then(content => content.text())`
* `readOnly` (optional, defaults to `false`): Use `false` to allow content edition, and `true` for read-only mode, in which the Editor will not allow changes. WARNING: Currently only the DMN Editor supports read-only mode.
* `origin` (optional, defaults to `window.location.origin`): If for some reason your application needs to change this parameter, you can use it.
* `onError` (optional, defaults to `() => {}`): If there's an error opening the Editor, this function will be called.
* `resources` (optional, defaults to `[]`): Map of resources that will be provided for the Editor. This can be used, for instance, to provide included models for the DMN Editor or Work Item Definitions for the BPMN Editor. Each entry in the map has the resource name as its key and an object containing the `content-type` (`text` or `binary`) and the resource `content` (Promise similar to the `initialContent` parameter) as its value.

The returned object will contain the methods needed to manipulate the Editor:
* `getContent(): Promise<string>`: Returns a Promise containing the Editor content.
* `setContent(content: string): Promise<void>`: Sets the content of the Editor. The returning Promise will be rejected if setting the content fails.
* `getPreview(): Promise<string>`: Returns a Promise containing the SVG string of the current diagram.
* `subscribeToContentChanges(callback: (isDirty: boolean) => void): (isDirty: boolean) => void`: Setup a callback to be called on every content change in the Editor. Returns the same callback to be used for unsubscription.
* `unsubscribeToContentChanges(callback: (isDirty: boolean) => void): void`: Unsubscribes the passed callback from content changes.
* `markAsSaved(): void`: Resets the Editor state, signalizing that its content is saved. This will also fire the subscribed callbacks of content changes.
* `undo(): void`: Undo the last change in the Editor. This will also fire the subscribed callbacks of content changes.
* `redo(): void`:  Redo the last undone change in the Editor. This will also fire the subscribed callbacks of content changes.
* `close(): void`: Closes the Editor.
* `getElementPosition(selector: string): Promise<Rect>`: Provides an alternative for extending the standard query selector when the element lives inside a canvas or even a video component. The `selector` parameter must follow the format of “<PROVIDER>:::<SELECT>“, e.g. “Canvas:::MySquare” or “Video:::PresenterHand”. Returns a `Rect` representing the element position.
* `envelopeApi: MessageBusClientApi<KogitoEditorEnvelopeApi>`: Advanced Editor API. See more details in [MessageBusClientApi](https://github.com/kiegroup/kogito-tooling/blob/master/packages/envelope-bus/src/api/index.ts#L43-L56) and [KogitoEditorEnvelopeApi](https://github.com/kiegroup/kogito-tooling/blob/master/packages/editor/src/api/KogitoEditorEnvelopeApi.ts#L34-L41).

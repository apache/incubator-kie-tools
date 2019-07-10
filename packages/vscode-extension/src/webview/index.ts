import * as MicroEditorEnvelope from "appformer-js-microeditor-envelope";

declare global {
  export const acquireVsCodeApi: any;
}

MicroEditorEnvelope.init({
  container: document.getElementById("envelope-app")!,
  busApi: acquireVsCodeApi(),
  clientSideOnly: true
});

import { GwtEditor } from "./GwtEditor";
import { Resource } from "appformer-js-microeditor-router";

declare global {
  //Exposed API of AppFormerGwt
  interface Window {
    gwtEditorBeans: Map<string, { get(): GwtEditor }>;
    appFormerGwtFinishedLoading: () => any;
    erraiBusApplicationRoot: string;
    erraiBusRemoteCommunicationEnabled: boolean;
  }
}

export class AppFormerGwtApi {
  public setErraiDomain(backendDomain: string): void {
    window.erraiBusApplicationRoot = backendDomain;
  }

  public onFinishedLoading(callback: () => Promise<any>) {
    window.appFormerGwtFinishedLoading = callback;
  }

  public getEditor(editorId: string) {
    const gwtEditor = window.gwtEditorBeans.get(editorId);
    if (!gwtEditor) {
      throw new Error(`GwtEditor with id '${editorId}' was not found`);
    }

    return gwtEditor.get();
  }

  public setClientSideOnly(clientSideOnly: boolean) {
    window.erraiBusRemoteCommunicationEnabled = !clientSideOnly;
  }
}

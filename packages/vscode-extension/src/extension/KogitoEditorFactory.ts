import * as vscode from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditor } from "./KogitoEditor";
import { LocalRouter } from "./LocalRouter";

export class KogitoEditorFactory {
  private readonly context: vscode.ExtensionContext;
  private readonly editorStore: KogitoEditorStore;
  private readonly router: LocalRouter;

  constructor(context: vscode.ExtensionContext, router: LocalRouter, editorStore: KogitoEditorStore) {
    this.context = context;
    this.editorStore = editorStore;
    this.router = router;
  }

  public openNew(path: string) {
    if (path.length <= 0) {
      throw new Error("parameter 'path' cannot be empty");
    }

    const panel = this.openNewPanel(path);
    const editor = new KogitoEditor(path, panel, this.context, this.router, this.editorStore);
    this.editorStore.addAsActive(editor);
    editor.setupEnvelopeBus();
    editor.setupPanelActiveStatusChange();
    editor.setupPanelOnDidDispose();
    editor.setupWebviewContent();
    return editor;
  }

  private openNewPanel(path: string) {
    const panelTitle = path.split("/").pop()! + " 🦉";

    //this will open a panel on vscode's UI.
    return vscode.window.createWebviewPanel(
      "kogito-editor",
      panelTitle,
      { viewColumn: vscode.ViewColumn.Active, preserveFocus: true },
      { enableCommandUris: true, enableScripts: true, retainContextWhenHidden: true }
    );
  }
}

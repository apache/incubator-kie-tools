import * as vscode from "vscode";
import { KogitoEditorsExtension } from "./KogitoEditorsExtension";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { LocalRouter } from "./LocalRouter";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const router = new LocalRouter(context);
  const editorStore = new KogitoEditorStore();
  const editorFactory = new KogitoEditorFactory(context, router, editorStore);
  const extension = new KogitoEditorsExtension(context, editorStore, editorFactory);

  extension.startReplacingTextEditorsByKogitoEditorsAsTheyOpenIfLanguageIsSupported();
  extension.registerCustomSaveCommand();
  extension.registerCustomSaveAllCommand();

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  //FIXME: For some reason, this method is not being called :(
  console.info("Extension is deactivating");
}

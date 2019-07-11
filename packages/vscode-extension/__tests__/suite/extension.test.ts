import * as vscode from "vscode";
import * as assert from "assert";
import * as __path from "path";
import { afterEach } from "mocha";

const workspace = __path.resolve(__dirname, "../../test-workspace");

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

function waitForActiveTextEditorChanges(length: number) {
  const openedEditors: string[] = [];

  return new Promise(resolve => {
    const disposable = vscode.window.onDidChangeActiveTextEditor((textEditor?: vscode.TextEditor) => {
      if (textEditor) {
        openedEditors.push(textEditor.document.fileName);
      } else {
        openedEditors.push("");
      }

      if (openedEditors.length === length) {
        resolve(openedEditors);
        disposable.dispose();
      }
    });
  });
}

function open(txtFile: string) {
  vscode.commands.executeCommand("vscode.open", vscode.Uri.file(txtFile));
}

suite("vscode extension :: integration tests", () => {
  afterEach(async () => {
    await vscode.commands.executeCommand("workbench.action.closeAllEditors");
  });

  test("open dmn file", async () => {
    const textEditorOpenedAndClosed = waitForActiveTextEditorChanges(2);

    const dmnFile = `${workspace}/demo.dmn`;
    open(dmnFile);

    assert.deepStrictEqual([dmnFile, ""], await textEditorOpenedAndClosed);

    await delay(1000);
  });

  test("open text file", async () => {
    const textEditorOpened = waitForActiveTextEditorChanges(1);

    const txtFile = `${workspace}/example.txt`;
    open(txtFile);

    assert.deepStrictEqual([txtFile], await textEditorOpened);
    console.info("editor opened correctly");
  });
});

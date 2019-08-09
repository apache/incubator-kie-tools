/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    const expt  = await textEditorOpenedAndClosed;

    assert.deepStrictEqual([dmnFile, ""], expt);

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

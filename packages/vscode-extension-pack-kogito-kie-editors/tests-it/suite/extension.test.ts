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

const NONE = "";

const testWorkspace = __path.resolve(__dirname, "../../test-workspace");

const delay = (ms: number) => {
  return new Promise(res => setTimeout(res, ms));
};

function editorStackWithLength(length: number) {
  const openedEditors: string[] = [];

  return new Promise(resolve => {
    const disposable = vscode.window.onDidChangeActiveTextEditor((textEditor?: vscode.TextEditor) => {
      if (textEditor) {
        openedEditors.push(textEditor.document.fileName);
      } else {
        openedEditors.push(NONE);
      }

      if (openedEditors.length === length) {
        resolve(openedEditors);
        disposable.dispose();
      }
    });
  });
}

function open(path: string) {
  return vscode.commands.executeCommand("vscode.open", vscode.Uri.file(path));
}

function openForTheFirstTime(path: string) {
  return open(path).then(() => delay(500));
}

suite("vscode extension :: integration tests", () => {
  afterEach(async () => {
    await vscode.commands.executeCommand("workbench.action.closeAllEditors");
  });

  test("open text file", async () => {
    const editorStack = editorStackWithLength(1);

    const txtFile = `${testWorkspace}/example.txt`;
    await open(txtFile);

    assert.deepStrictEqual([txtFile], await editorStack);
    assert.strictEqual(vscode.window.activeTextEditor.document.uri.path, txtFile);
    assert.strictEqual(vscode.window.visibleTextEditors.length, 1);
  });

  test("open bpmn editor", async () => {
    const editorStack = editorStackWithLength(2);

    const bpmnFile = `${testWorkspace}/demo.bpmn`;
    await openForTheFirstTime(bpmnFile);

    assert.deepStrictEqual([bpmnFile, NONE], await editorStack);
    assert.strictEqual(vscode.window.activeTextEditor, undefined);
    assert.strictEqual(vscode.window.visibleTextEditors.length, 0);
  });

  test("open dmn editor", async () => {
    const editorStack = editorStackWithLength(2);

    const dmnFile = `${testWorkspace}/demo.dmn`;
    await openForTheFirstTime(dmnFile);

    assert.deepStrictEqual([dmnFile, NONE], await editorStack);
    assert.strictEqual(vscode.window.activeTextEditor, undefined);
    assert.strictEqual(vscode.window.visibleTextEditors.length, 0);
  });

  test("reopen a custom editor", async () => {
    const editorStack = editorStackWithLength(6);

    const bpmnFile = `${testWorkspace}/demo.bpmn`;
    const txtFile = `${testWorkspace}/example.txt`;

    await openForTheFirstTime(bpmnFile);
    await open(txtFile);
    await open(bpmnFile);

    assert.deepStrictEqual([bpmnFile, NONE, txtFile, NONE, bpmnFile, NONE], await editorStack);
    assert.strictEqual(vscode.window.activeTextEditor, undefined);
    assert.strictEqual(vscode.window.visibleTextEditors.length, 0);
  });
});

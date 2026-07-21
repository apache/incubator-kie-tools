/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as vscode from "vscode";
import * as assert from "assert";
import { getDocUri, activate } from "./helper";

suite("Completion tests", () => {
  const docUri = getDocUri("empty.drl");

  test("Completes 'package' at the beginning", async () => {
    await testCompletion(docUri, new vscode.Position(0, 0), {
      items: [{ label: "package", kind: vscode.CompletionItemKind.Keyword }],
    });
  }).timeout(30000); // increase timeout because helper.activate waits for server startup
});

suite("Class completion tests", () => {
  const docUri = getDocUri("sample.drl");

  test("Suggests class names in pattern position", async () => {
    await activate(docUri);

    // "$p :" in the "greeting" rule — caret after the colon (line 31, 0-indexed)
    const position = new vscode.Position(31, 7);

    // Class index is built asynchronously after initialize; retry until ready
    let classLabels: string[] = [];
    for (let attempt = 0; attempt < 30; attempt++) {
      const completionList = (await vscode.commands.executeCommand(
        "vscode.executeCompletionItemProvider",
        docUri,
        position
      )) as vscode.CompletionList;

      classLabels = completionList.items
        .filter((item) => item.kind === vscode.CompletionItemKind.Class)
        .map((item) => item.label as string);

      if (classLabels.includes("Person")) {
        break;
      }
      await new Promise((resolve) => setTimeout(resolve, 1000));
    }

    assert.ok(classLabels.includes("Person"), "Should suggest Person class");
    assert.ok(classLabels.includes("Address"), "Should suggest Address class");

    // Re-query to check sortText on the final result
    const actualCompletionList = (await vscode.commands.executeCommand(
      "vscode.executeCompletionItemProvider",
      docUri,
      position
    )) as vscode.CompletionList;

    // Person is imported, so should have sortText starting with "0_"
    const personItem = actualCompletionList.items.find(
      (item) => item.label === "Person" && item.kind === vscode.CompletionItemKind.Class
    );
    assert.ok(personItem, "Person completion item should exist");
    assert.ok(personItem!.sortText?.startsWith("0_"), "Imported Person should be ranked first");

    // Address is not imported, so should have sortText starting with "1_"
    const addressItem = actualCompletionList.items.find(
      (item) => item.label === "Address" && item.kind === vscode.CompletionItemKind.Class
    );
    assert.ok(addressItem, "Address completion item should exist");
    assert.ok(addressItem!.sortText?.startsWith("1_"), "Unimported Address should be ranked after imported classes");
  }).timeout(60000); // classpath resolution via mvn adds time
});

async function testCompletion(
  docUri: vscode.Uri,
  position: vscode.Position,
  expectedCompletionList: vscode.CompletionList
) {
  await activate(docUri);

  // Executing the command `vscode.executeCompletionItemProvider` to simulate triggering completion
  const actualCompletionList = (await vscode.commands.executeCommand(
    "vscode.executeCompletionItemProvider",
    docUri,
    position
  )) as vscode.CompletionList;

  assert.ok(actualCompletionList.items.length >= expectedCompletionList.items.length);

  expectedCompletionList.items.forEach((expectedItem) => {
    const actualItem = actualCompletionList.items.find((item) => item.label === expectedItem.label);
    assert(actualItem);
    assert.strictEqual(actualItem.kind, expectedItem.kind);
  });
}

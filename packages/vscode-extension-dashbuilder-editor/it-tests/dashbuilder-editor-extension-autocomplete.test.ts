/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

require("./dashbuilder-editor-extension-smoke.test");

import * as path from "path";
import * as fs from "fs";
import { expect } from "chai";
import { Key } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import DashbuilderTextEditorTestHelper from "./helpers/dashbuilder/DashbuilderTextEditorTestHelper";

describe("Dashbuilder editor - autocomplete function tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "autocomplete-test");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER);
  });

  beforeEach(async function () {
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(35000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it("Completes empty dashbuilder yaml file with first dashboard example", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("empty_file_example_autocomplete.dash.yaml");
    const dashbuilderTextEditor = new DashbuilderTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await dashbuilderTextEditor.getDashbuilderTextEditor();

    // open content assist
    await textEditor.toggleContentAssist(true);

    // first dashboard example is expected to be the first option in the content assist
    textEditor.typeText(Key.ENTER);

    // check that the example from content assist is the same as expected example
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(
      path.resolve(TEST_PROJECT_FOLDER, "empty_file_example_autocomplete.dash.yaml.result"),
      "utf-8"
    );
    expect(editorContent).equals(expectedContent);
  });

  it("Check autocomplete feature suggestions for dashbuilder yaml file", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("file_for_autocompletion.dash.yaml");
    const dashbuilderTextEditor = new DashbuilderTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await dashbuilderTextEditor.getDashbuilderTextEditor();

    await textEditor.moveCursor(18, 14);
    await textEditor.typeText(Key.ENTER);
    const content = await textEditor.toggleContentAssist(true);

    // check content assist suggestions
    const suggestedItems = await content?.getItems();
    const suggestedItemsToString = await Promise.all(suggestedItems?.map(async (i) => await i.getText()) ?? []);
    expect(suggestedItemsToString).to.have.length(12);
    expect(suggestedItemsToString).to.contain.members(["date", "label", "number", "text"]);
    expect(suggestedItemsToString).to.contain.members(["date", "Date", "DATE"]);
  });
});

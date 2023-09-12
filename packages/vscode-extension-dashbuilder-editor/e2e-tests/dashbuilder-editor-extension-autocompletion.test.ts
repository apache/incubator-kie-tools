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

require("./dashbuilder-editor-extension-smoke.test");

import * as path from "path";
import * as fs from "fs";
import { expect } from "chai";
import { Key } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import DashbuilderTextEditorTestHelper from "./helpers/dashbuilder/DashbuilderTextEditorTestHelper";

describe("Dashbuilder editor - autocompletion tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("e2e-tests-tmp", "resources", "autocompletion");

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

  it("Completes dashbuilder yaml file from an empty file", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("empty_file_autocompletion.dash.yaml");
    const dashbuilderTextEditor = new DashbuilderTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await dashbuilderTextEditor.getDashbuilderTextEditor();

    // first dashboard example is expected to be the first option in the content assist
    await textEditor.toggleContentAssist(true);
    textEditor.typeText(Key.ENTER);

    // check that the final editor content is the same as expected result
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(
      path.resolve(TEST_PROJECT_FOLDER, "empty_file_autocompletion.dash.yaml.result"),
      "utf-8"
    );
    expect(editorContent).equals(expectedContent);
  });

  it("Checks dashbuilder yaml provides correct autocompletion", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("autocompletion.dash.yaml");
    const dashbuilderTextEditor = new DashbuilderTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await dashbuilderTextEditor.getDashbuilderTextEditor();

    await textEditor.moveCursor(18, 14);
    await textEditor.typeText(Key.ENTER);

    // check available content assist parameters
    const content = await textEditor.toggleContentAssist(true);
    const items = await content?.getItems();
    const itemNames = await Promise.all(items?.map(async (i) => await i.getText()) ?? []);
    expect(itemNames).to.have.length(4);
    expect(itemNames).to.contain.members(["LABEL", "NUMBER", "DATE", "TEXT"]);
  });
});

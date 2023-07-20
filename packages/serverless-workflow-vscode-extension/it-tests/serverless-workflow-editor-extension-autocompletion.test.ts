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

require("./serverless-workflow-editor-extension-smoke.test");

import * as path from "path";
import * as fs from "fs";
import { expect } from "chai";
import { Key, TextEditor } from "vscode-extension-tester";
import { VSCodeTestHelper, sleep } from "@kie-tools/vscode-extension-common-test-helpers";
import SwfEditorTestHelper from "./helpers/swf/SwfEditorTestHelper";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - autocompletion tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "autocompletion");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

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
    this.timeout(15000);
    await testHelper.takeScreenshotOnTestFailure(this, DIST_IT_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  describe("JSON files", () => {
    it("Completes serverless workflow with function and state autocompletion", async function () {
      this.timeout(50000);

      const editorWebviews = await testHelper.openFileFromSidebar("autocompletion.sw.json");
      const swfEditor = new SwfEditorTestHelper(editorWebviews[1]);
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      await textEditor.moveCursor(7, 26);
      await textEditor.typeText(Key.ENTER);

      // check available content assist parameters
      const content = await textEditor.toggleContentAssist(true);
      const items = await content?.getItems();
      const itemNames = await Promise.all(items?.map(async (i) => await i.getText()) ?? []);
      expect(itemNames).to.have.all.members([
        "auth",
        "dataInputSchema",
        "errors",
        "events",
        "functions",
        "retries",
        "start",
        "states",
        "timeouts",
      ]);
      await textEditor.toggleContentAssist(false);

      // add function from specs directory
      await selectFromContentAssist(textEditor, "functions");
      await textEditor.typeText(": ");
      await selectFromContentAssist(textEditor, "[]");
      await selectFromContentAssist(textEditor, "specs»api.yaml#testFuncId");

      // add test state
      await textEditor.moveCursor(17, 38);
      await textEditor.typeText(Key.ENTER);
      await selectFromContentAssist(textEditor, "states");
      await selectFromContentAssist(textEditor, "{}");
      await textEditor.typeText(Key.ENTER);
      await textEditor.typeText(
        '"name": "testState",\n' + '"type": "operation",\n' + '"actions": [{"functionRef": }],\n' + '"end": true'
      );

      // complete the state with refName
      await textEditor.moveCursor(21, 33);
      await selectFromContentAssist(textEditor, "{}");
      await textEditor.typeText(Key.ENTER);
      await selectFromContentAssist(textEditor, "refName");
      await selectFromContentAssist(textEditor, '"testFuncId"');

      // check there are 2 nodes: testState, end
      const nodeIds = await swfEditor.getAllNodeIds();
      expect(nodeIds.length).equal(2);

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "autocompletion.sw.json.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });

    it("Completes serverless workflow from an empty file", async function () {
      this.timeout(50000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyfile_autocompletion.sw.json");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await selectFromContentAssist(textEditor, "Serverless Workflow Example");

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "emptyfile_autocompletion.sw.json.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });
  });

  describe("YAML files", () => {
    it("Completes serverless workflow with function and state autocompletion", async function () {
      this.timeout(50000);

      const editorWebviews = await testHelper.openFileFromSidebar("autocompletion.sw.yaml");
      const swfEditor = new SwfEditorTestHelper(editorWebviews[1]);
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      await textEditor.moveCursor(9, 17);
      await textEditor.typeText(Key.ENTER);

      // check available content assist parameters
      const content = await textEditor.toggleContentAssist(true);
      const items = await content?.getItems();
      const itemNames = await Promise.all(items?.map(async (i) => await i.getText()) ?? []);
      expect(itemNames).to.have.all.members([
        "auth",
        "dataInputSchema",
        "errors",
        "events",
        "functions",
        "retries",
        "states",
        "timeouts",
      ]);
      await textEditor.toggleContentAssist(false);

      // add function from specs directory
      await selectFromContentAssist(textEditor, "functions");
      await textEditor.typeText(":\n  - ");
      await selectFromContentAssist(textEditor, "specs»api.yaml#testFuncId");

      // add test state
      await textEditor.moveCursor(19, 19);
      await textEditor.typeText(Key.ENTER);
      await selectFromContentAssist(textEditor, "states");
      await textEditor.typeText(`name: testState
  type: operation
actions:
  - functionRef: `);

      // complete the state with refName
      await textEditor.moveCursor(24, 21);
      await textEditor.typeText(Key.ENTER);
      await textEditor.typeText(Key.TAB);
      await textEditor.typeText(Key.TAB);
      await selectFromContentAssist(textEditor, "refName");
      await selectFromContentAssist(textEditor, "testFuncId");
      await textEditor.typeText(Key.ENTER);
      await textEditor.typeText(Key.BACK_SPACE);
      await textEditor.typeText(Key.BACK_SPACE);
      await textEditor.typeText(Key.BACK_SPACE);
      await textEditor.typeText("end: true");

      // check there are 3 nodes: start, testState, end
      const nodes = await swfEditor.getAllNodeIds();
      expect(nodes.length).equal(3);

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "autocompletion.sw.yaml.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });

    it("Completes serverless workflow from an empty file and create Serverless Workflow Example", async function () {
      this.timeout(50000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyfile_autocompletion.sw.yaml");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await selectFromContentAssist(textEditor, "Serverless Workflow Example");

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "emptyfile_autocompletion.sw.yaml.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });
    it("Completes serverless workflow from an empty file and create Empty Serverless Workflow", async function () {
      this.timeout(50000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyworkflow_autocompletion.sw.json");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await selectFromContentAssist(textEditor, "Empty Serverless Workflow");

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "emptyworkflow_autocompletion.sw.json.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });
  });

  async function selectFromContentAssist(textEditor: TextEditor, value: string): Promise<void> {
    const contentAssist = await textEditor.toggleContentAssist(true);
    try {
      const item = await contentAssist?.getItem(value);
      await sleep(1000);
      expect(await item?.getLabel()).contain(value);
      await item?.click();
    } catch (e) {
      throw new Error(
        `The ContentAssist menu is not available or it was not possible to select the element '${value}'!`
      );
    }
  }
});

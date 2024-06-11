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

require("./serverless-workflow-editor-extension-smoke.test");

import * as path from "path";
import * as fs from "fs";
import { expect } from "chai";
import { Key } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import SwfEditorTestHelper from "./helpers/swf/SwfEditorTestHelper";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - autocompletion tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("e2e-tests-tmp", "resources", "autocompletion");
  const DIST_E2E_TESTS_FOLDER: string = path.resolve("dist-e2e-tests");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER);
  });

  beforeEach(async function () {
    this.timeout(60000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(60000);
    await testHelper.takeScreenshotOnTestFailure(this, DIST_E2E_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  describe("JSON files", () => {
    it("Completes serverless workflow with function and state autocompletion", async function () {
      this.timeout(80000);

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
      await textEditor.selectFromContentAssist("functions");
      await textEditor.typeText(": ");
      await textEditor.selectFromContentAssist("[]");
      await textEditor.selectFromContentAssist("specs»api.yaml#testFuncId");

      // add test state
      await textEditor.moveCursor(17, 38);
      await textEditor.typeText(Key.ENTER);
      await textEditor.selectFromContentAssist("states");
      await textEditor.selectFromContentAssist("{}");
      await textEditor.typeText(Key.ENTER);
      await textEditor.typeText(
        '"name": "testState",\n' + '"type": "operation",\n' + '"actions": [{"functionRef": }],\n' + '"end": true'
      );

      // complete the state with refName
      await textEditor.moveCursor(21, 33);
      await textEditor.selectFromContentAssist("{}");
      await textEditor.typeText(Key.ENTER);
      await textEditor.selectFromContentAssist("refName");
      await textEditor.selectFromContentAssist('"testFuncId"');

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
      this.timeout(80000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyfile_autocompletion.sw.json");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await textEditor.selectFromContentAssist("Serverless Workflow Example");

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
      this.timeout(80000);

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
      await textEditor.selectFromContentAssist("functions");
      await textEditor.typeText(":\n  - ");
      await textEditor.selectFromContentAssist("specs»api.yaml#testFuncId");

      // add test state
      await textEditor.moveCursor(19, 19);
      await textEditor.typeText(Key.ENTER);
      await textEditor.selectFromContentAssist("states");
      await textEditor.typeText(`name: testState
  type: operation
actions:
  - functionRef: `);

      // complete the state with refName
      await textEditor.moveCursor(24, 21);
      await textEditor.typeText(Key.ENTER);
      await textEditor.typeText(Key.TAB);
      await textEditor.typeText(Key.TAB);
      await textEditor.selectFromContentAssist("refName");
      await textEditor.selectFromContentAssist("testFuncId");
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
      this.timeout(80000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyfile_autocompletion.sw.yaml");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await textEditor.selectFromContentAssist("Serverless Workflow Example");

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "emptyfile_autocompletion.sw.yaml.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });
    it("Completes serverless workflow from an empty file and create Empty Serverless Workflow", async function () {
      this.timeout(80000);

      const editorWebviews = await testHelper.openFileFromSidebar("emptyworkflow_autocompletion.sw.json");
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await swfTextEditor.getSwfTextEditor();

      // select the autocompletion
      await textEditor.selectFromContentAssist("Empty Serverless Workflow");

      // check the final editor content is the same as expected result
      const editorContent = await textEditor.getText();
      const expectedContent = fs.readFileSync(
        path.resolve(TEST_PROJECT_FOLDER, "emptyworkflow_autocompletion.sw.json.result"),
        "utf-8"
      );
      expect(editorContent).equal(expectedContent);
    });
  });
});

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

import { expect } from "chai";
import * as path from "path";
import SwfEditorTestHelper from "./helpers/swf/SwfEditorTestHelper";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";

describe("Serverless workflow editor - Diagram navigation tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "diagram-navigation");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(30000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER);
  });

  beforeEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.takeScreenshotOnTestFailure(this, DIST_IT_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  //TOD - fix test environment
  it.skip("Select states", async function () {
    this.timeout(30000);

    const WORKFLOW_NAME = "applicant-request-decision.sw.json";

    try {
      const editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME);
      const swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
      const swfEditor = new SwfEditorTestHelper(editorWebViews[1]);

      const nodeIds = await swfEditor.getAllNodeIds();
      expect(nodeIds.length).equal(6);

      // Select CheckApplication node
      await swfEditor.selectNode(nodeIds[1]);

      const textEditor = await swfTextEditor.getSwfTextEditor();
      let lineNumber = (await textEditor.getCoordinates())[0];
      let columnNumber = (await textEditor.getCoordinates())[1];

      expect(lineNumber).equal(16);
      expect(columnNumber).equal(7);

      // Select StartApplication node
      await swfEditor.selectNode(nodeIds[2]);

      lineNumber = (await textEditor.getCoordinates())[0];
      columnNumber = (await textEditor.getCoordinates())[1];

      expect(lineNumber).equal(33);
      expect(columnNumber).equal(7);
    } catch (error) {
      console.error("Select states: " + error);
    }
  });
});

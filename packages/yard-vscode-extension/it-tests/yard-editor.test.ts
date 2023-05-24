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

import * as path from "path";
import { assert } from "chai";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import YardEditorTestHelper from "./helpers/yard/YardEditorTestHelper";
import YardTextEditorTestHelper from "./helpers/yard/YardTextEditorTestHelper";

describe("yard editor - integration tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");
  const EMPTY_YARD_YAML = "empty.yard.yaml";
  const EMPTY_YARD_YML = "empty.yard.yml";

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

  [EMPTY_YARD_YAML, EMPTY_YARD_YML].forEach(function (fileName) {
    it("Opens " + fileName + " and loads two editor groups (text editor and a web view)", async function () {
      this.timeout(30000);
      const editorWebviews = await testHelper.openFileFromSidebar(fileName);

      const yardTextEditor = new YardTextEditorTestHelper(editorWebviews[0]);
      const textEditor = await yardTextEditor.getYardTextEditor();
      const yardWebView = new YardEditorTestHelper(editorWebviews[1]);

      assert.isFalse(await textEditor.isDirty());
      assert.equal("\n", await textEditor.getText());

      const yardUITab = await yardWebView.getYardTabElements();
      assert.isNotNull(yardUITab);
      assert.equal(3, yardUITab.length);
    });
  });
});

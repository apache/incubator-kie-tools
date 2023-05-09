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

require("./extension-editors-smoke.test");

import { InputBox, SideBarView, TextEditor, WebView } from "vscode-extension-tester";
import * as path from "path";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import ScesimEditorTestHelper from "./helpers/ScesimEditorTestHelper";
import { assert } from "chai";

/**
 * SCESIM editor vscode integration test suite, add any acceptance tests,
 * freature verificaition, bug reproducers here.
 *
 * For scenarios with other editor consider adding it to a specific
 * file for the integration e.g. "extensions-editors-dmn-scesim.test.ts"
 */
describe("KIE Editors Integration Test Suite - SCESIM Editor", () => {
  const RESOURCES: string = path.resolve("it-tests-tmp", "resources");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");
  const DEMO_DMN: string = "demo.dmn";
  const DEMO_DMN_SCESIM: string = "demo-dmn.scesim";

  let testHelper: VSCodeTestHelper;
  let webview: WebView;
  let folderView: SideBarView;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    folderView = await testHelper.openFolder(RESOURCES);
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
    await webview.switchBack();
  });

  // The test is broken. It is necessary to fix it. See https://issues.redhat.com/browse/KOGITO-9131 for more information.
  /**
   * As the opened scesim file is empty, a prompt to specify file under test should be shown
   */
  it.skip("Opens demo-dmn.scesim file in SCESIM Editor", async function () {
    this.timeout(20000);

    const editorWebviews = await testHelper.openFileFromSidebar(DEMO_DMN_SCESIM);
    webview = editorWebviews[0];
    const scesimEditorTester = new ScesimEditorTestHelper(webview);
    await scesimEditorTester.switchToEditorFrame();

    await scesimEditorTester.specifyDmnOnLandingPage(DEMO_DMN);

    await scesimEditorTester.switchBack();

    // save file so we can check the plain text source
    await testHelper.executeCommandFromPrompt("File: Save");

    // check plain text source starts with <?xml?> prolog
    await testHelper.executeCommandFromPrompt("View: Reopen Editor With...");
    const input = await InputBox.create();
    await input.selectQuickPick("Text Editor");

    const xmlProlog = '<?xml version="1.0" encoding="UTF-8"?>';
    const plainText = new TextEditor();
    assert.equal(await plainText.getTextAtLine(1), xmlProlog, "First line should be an <?xml?> prolog");
    assert.notEqual(await plainText.getTextAtLine(2), xmlProlog, "<?xml?> prolog should be there just once");
  });
});

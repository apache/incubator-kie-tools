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
import { expect } from "chai";
import { Key } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - expression tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "expression");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(30000);
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

  it("Checks expression autocompletion in JSON serverless workflow file", async function () {
    this.timeout(50000);

    await testExpressionsOnLocation("expression.sw.json", 11, 31);
  });

  it("Checks expression autocompletion in YAML serverless workflow file", async function () {
    this.timeout(50000);

    await testExpressionsOnLocation("expression.sw.yaml", 9, 27);
  });

  async function testExpressionsOnLocation(fileName: string, line: number, column: number): Promise<void> {
    const editorWebviews = await testHelper.openFileFromSidebar(fileName);
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    await textEditor.moveCursor(line, column);
    const contentAssist = await textEditor.toggleContentAssist(true);

    // check jq expression autocompletion
    var contentAssistItems = await contentAssist?.getItems();
    const jqExpressionNames = await Promise.all(contentAssistItems?.map(async (i) => await i.getLabel()) ?? []);
    expect(jqExpressionNames).to.contain.members(["add", "all", "all(condition)", "all(generator; condition)", "any"]);

    // check properties autocompletion from dataInputSchema and functions
    await textEditor.typeText(".");
    contentAssistItems = await contentAssist?.getItems();
    const propsNames = await Promise.all(contentAssistItems?.map(async (i) => await i.getLabel()) ?? []);
    expect(propsNames).to.have.all.members(["aProp", "bProp", "cProp", "testObject", "xProp", "yProp"]);

    // check expression functions autocompletion
    await textEditor.typeText(Key.BACK_SPACE);
    await textEditor.typeText("fn:");
    contentAssistItems = await contentAssist?.getItems();
    const functionNames = await Promise.all(contentAssistItems?.map(async (i) => await i.getLabel()) ?? []);
    expect(functionNames).to.have.all.members(["expressionFunc"]);
  }
});

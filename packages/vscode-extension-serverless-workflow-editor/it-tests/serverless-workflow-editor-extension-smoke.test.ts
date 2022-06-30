/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import VSCodeTestHelper, { sleep } from "./helpers/VSCodeTestHelper";
import SwfEditorTestHelper from "./helpers/swf/SwfEditorTestHelper";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - smoke integration tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "greeting-flow");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER, "greeting-flow");
  });

  beforeEach(async function () {
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it("Opens greetings.sw.json and loads two editor groups", async function () {
    this.timeout(30000);
    const editorWebviews = await testHelper.openFileFromSidebar("greetings.sw.json", "src/main/resources");

    const swfEditor = new SwfEditorTestHelper(editorWebviews[1]);
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);

    // find elements, this asserts they exist
    await swfEditor.getSvgElement();
    await swfEditor.getMermaidDivElement();

    // expect 7 states /w end and start
    const stateElements = await swfEditor.getAllStateNodes();
    assert.equal(stateElements.length, 7);
  });
});

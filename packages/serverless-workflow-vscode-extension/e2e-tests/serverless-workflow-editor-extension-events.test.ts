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
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - events tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("e2e-tests-tmp", "resources", "functions-events");
  const DIST_E2E_TESTS_FOLDER: string = path.resolve("dist-e2e-tests");

  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(30000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER);
  });

  beforeEach(async function () {
    this.timeout(30000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(30000);
    await testHelper.takeScreenshotOnTestFailure(this, DIST_E2E_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it("Checks events are loaded from asyncapi files into JSON serverless workflow file", async function () {
    this.timeout(50000);

    const editorWebViews = await testHelper.openFileFromSidebar("event.sw.json");
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    await textEditor.moveCursor(13, 14);
    await textEditor.typeText(Key.ENTER);

    // check content assist contains events from asyncapi files
    await textEditor.typeText("s");
    const contentAssist = await textEditor.toggleContentAssist(true);
    const items = await contentAssist?.getItems();
    const eventNames = await Promise.all(items?.map(async (i) => await i.getLabel()) ?? []);
    expect(eventNames).to.have.all.members([
      "specs»asyncapi.json#publishJsonOperation",
      "specs»asyncapi.json#subscribeJsonOperation",
      "specs»asyncapi.yaml#publishYamlOperation",
      "specs»asyncapi.yaml#subscribeYamlOperation",
    ]);

    // add asyncapi event from yaml specification
    await textEditor.selectFromContentAssist("specs»asyncapi.yaml#publishYamlOperation");

    // check the final editor content is the same as expected result
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, "event.sw.json.result"), "utf-8");
    expect(editorContent).equal(expectedContent);
  });

  it("Checks events are loaded from asyncapi files into YAML serverless workflow file", async function () {
    this.timeout(50000);

    const editorWebViews = await testHelper.openFileFromSidebar("event.sw.yaml");
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    await textEditor.moveCursor(10, 4);
    await textEditor.typeText(" ");

    // check content assist contains events from asyncapi files
    await textEditor.typeText("s");
    const contentAssist = await textEditor.toggleContentAssist(true);
    const items = await contentAssist?.getItems();
    const eventNames = await Promise.all(items?.map(async (i) => await i.getLabel()) ?? []);
    expect(eventNames).to.have.all.members([
      "specs»asyncapi.json#publishJsonOperation",
      "specs»asyncapi.json#subscribeJsonOperation",
      "specs»asyncapi.yaml#publishYamlOperation",
      "specs»asyncapi.yaml#subscribeYamlOperation",
    ]);

    // add asyncapi event from json specification
    await textEditor.selectFromContentAssist("specs»asyncapi.json#subscribeJsonOperation");

    // check the final editor content is the same as expected result
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, "event.sw.yaml.result"), "utf-8");
    expect(editorContent).equal(expectedContent);
  });
});

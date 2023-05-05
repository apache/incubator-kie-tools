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
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - functions tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "functions");
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

  it("Checks functions are loaded from specs and routes directories into JSON serverless workflow file", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("function.sw.json");
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    await textEditor.moveCursor(13, 17);
    await textEditor.typeText(Key.ENTER);

    // check content assist contains functions from specs directory
    await textEditor.typeText("s");
    const contentAssist = await textEditor.toggleContentAssist(true);
    const items = await contentAssist?.getItems();
    const functionNames = await Promise.all(items?.map(async (i) => await i.getLabel()) ?? []);
    expect(functionNames).to.have.all.members([
      "specs»asyncapi.json#publishJsonOperation",
      "specs»asyncapi.json#subscribeJsonOperation",
      "specs»asyncapi.yaml#publishYamlOperation",
      "specs»asyncapi.yaml#subscribeYamlOperation",
      "specs»openapi.json#testGetJsonFunc",
      "specs»openapi.json#testPutJsonFunc",
      "specs»openapi.yaml#testGetYamlFunc",
      "specs»openapi.yaml#testPutYamlFunc",
    ]);

    // add asyncapi function from yaml specification
    await selectFromContentAssist(textEditor, "specs»asyncapi.yaml#publishYamlOperation");

    await textEditor.moveCursor(18, 6);
    await textEditor.typeText("," + Key.ENTER);

    // add openapi function from json specification
    await textEditor.typeText("s");
    await selectFromContentAssist(textEditor, "specs»openapi.json#testPutJsonFunc");

    await textEditor.moveCursor(23, 6);
    await textEditor.typeText("," + Key.ENTER);

    // check content assist contains functions from routes directory
    await textEditor.typeText("r");
    const contentAssistRoutes = await textEditor.toggleContentAssist(true);
    const routeItems = await contentAssistRoutes?.getItems();
    const routeNames = await Promise.all(routeItems?.map(async (i) => await i.getLabel()) ?? []);
    expect(routeNames).to.have.all.members([
      "routes»camel.json#camel:direct:fromJson",
      "routes»camel.json#camel:direct:routeJson",
      "routes»camel.yaml#camel:direct:fromYaml",
      "routes»camel.yaml#camel:direct:routeYaml",
    ]);

    // add camel function from json specification
    await selectFromContentAssist(textEditor, "routes»camel.json#camel:direct:routeJson");

    // check the final editor content is the same as expected result
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, "function.sw.json.result"), "utf-8");
    expect(editorContent).equal(expectedContent);
  });

  it("Checks functions are loaded from specs and routes directories into YAML serverless workflow file", async function () {
    this.timeout(50000);

    const editorWebviews = await testHelper.openFileFromSidebar("function.sw.yaml");
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    await textEditor.moveCursor(10, 4);
    await textEditor.typeText(" ");

    // check content assist contains functions from specs directory
    await textEditor.typeText("s");
    const contentAssist = await textEditor.toggleContentAssist(true);
    const items = await contentAssist?.getItems();
    const functionNames = await Promise.all(items?.map(async (i) => await i.getLabel()) ?? []);
    expect(functionNames).to.contain.members([
      "specs»asyncapi.json#publishJsonOperation",
      "specs»asyncapi.json#subscribeJsonOperation",
      "specs»asyncapi.yaml#publishYamlOperation",
      "specs»asyncapi.yaml#subscribeYamlOperation",
      "specs»openapi.json#testGetJsonFunc",
      "specs»openapi.json#testPutJsonFunc",
      "specs»openapi.yaml#testGetYamlFunc",
      "specs»openapi.yaml#testPutYamlFunc",
    ]);

    // add asyncapi function from json specification
    await selectFromContentAssist(textEditor, "specs»asyncapi.json#subscribeJsonOperation");

    await textEditor.moveCursor(12, 19);
    await textEditor.typeText(Key.ENTER + Key.BACK_SPACE + "- ");

    // add openapi function from yaml specification
    await textEditor.typeText("s");
    await selectFromContentAssist(textEditor, "specs»openapi.yaml#testGetYamlFunc");

    await textEditor.moveCursor(15, 15);
    await textEditor.typeText(Key.ENTER + Key.BACK_SPACE + "- ");

    // check content assist contains functions from routes directory
    await textEditor.typeText("r");
    const contentAssistRoutes = await textEditor.toggleContentAssist(true);
    const routeItems = await contentAssistRoutes?.getItems();
    const routeNames = await Promise.all(routeItems?.map(async (i) => await i.getLabel()) ?? []);
    expect(routeNames).to.have.all.members([
      "routes»camel.json#camel:direct:fromJson",
      "routes»camel.json#camel:direct:routeJson",
      "routes»camel.yaml#camel:direct:fromYaml",
      "routes»camel.yaml#camel:direct:routeYaml",
    ]);

    // add camel function from yaml specification
    await selectFromContentAssist(textEditor, "routes»camel.yaml#camel:direct:fromYaml");

    // check the final editor content is the same as expected result
    const editorContent = await textEditor.getText();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, "function.sw.yaml.result"), "utf-8");
    expect(editorContent).equal(expectedContent);
  });

  async function selectFromContentAssist(textEditor: TextEditor, value: string): Promise<void> {
    const contentAssist = await textEditor.toggleContentAssist(true);
    const item = await contentAssist?.getItem(value);
    await sleep(500);
    expect(await item?.getLabel()).contain(value);
    await item?.click();
  }
});

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

import { expect } from "chai";
import * as path from "path";
import * as fs from "fs";
import SwfEditorTestHelper from "./helpers/swf/SwfEditorTestHelper";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";
import VSCodeTestHelper, { sleep } from "./helpers/VSCodeTestHelper";
import { join } from "path";

// The following test is failing in github CI. See https://issues.redhat.com/browse/KOGITO-8952.
describe("Serverless workflow editor - Basic operations tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "basic-operations");
  
  let testHelper: VSCodeTestHelper;

  before(async function () {
    this.timeout(30000);
    testHelper = new VSCodeTestHelper();
    await testHelper.openFolder(TEST_PROJECT_FOLDER, "basic-operations");
  });

  beforeEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it("Opens, edits and saves the *.sw.json file", async function () {
    this.timeout(30000);

    const WORKFLOW_NAME = "greet.sw.json";

    let editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME);
    let swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    let swfEditor = new SwfEditorTestHelper(editorWebViews[1]);

    await testHelper.saveScreenshot("openAndSave_1");
    expect((await swfEditor.getAllNodeIds()).length).equal(6);

    let textEditor = await swfTextEditor.getSwfTextEditor();
    await testHelper.saveScreenshot("openAndSave_2");

    const greetInGermanStateString =
      "\n" +
      "{\n" +
      '"name": "GreetInGerman",\n' +
      '"type": "inject",\n' +
      '"data": {\n' +
      '"greeting": "Hallo vom JSON-Workflow, "\n' +
      "},\n" +
      '"transition": "GreetPerson"\n' +
      "},";

    const germanConditionString =
      ",\n" + "{\n" + '"condition": "${ .language == \\"German\\"}",\n' + '"transition": "GreetInGerman"\n' + "}";

    await textEditor.typeTextAt(47, 7, greetInGermanStateString);
    await textEditor.typeTextAt(26, 10, germanConditionString);
    await testHelper.saveScreenshot("openAndSave_3");

    expect((await swfEditor.getAllNodeIds()).length).equal(7);
    await testHelper.saveScreenshot("openAndSave_4");

    if (await textEditor.isDirty()) {
      await textEditor.save();
      console.log("Saving the changes.");
    } else {
      console.log("The editor doesn't have unsaved changes.");
    }
    await testHelper.saveScreenshot("openAndSave_5");

    await testHelper.closeAllEditors();
    await testHelper.saveScreenshot("openAndSave_6");

    editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME);
    await testHelper.saveScreenshot("openAndSave_7");
    swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    await testHelper.saveScreenshot("openAndSave_8");
    swfEditor = new SwfEditorTestHelper(editorWebViews[1]);
    await testHelper.saveScreenshot("openAndSave_7");

    textEditor = await swfTextEditor.getSwfTextEditor();
    await testHelper.saveScreenshot("openAndSave_8");

    const editorTextTrimmedLines = (await textEditor.getText())
      .split(/\n/)
      .map((row) => row.trim())
      .join("\n");
    await testHelper.saveScreenshot("openAndSave_9");

    expect(editorTextTrimmedLines).to.have.string(greetInGermanStateString);
    expect(editorTextTrimmedLines).to.have.string(germanConditionString);
    await testHelper.saveScreenshot("openAndSave_9");

    expect((await swfEditor.getAllNodeIds()).length).equal(7);
  });

  //The following test is skipped because of bug: https://issues.redhat.com/browse/KOGITO-8384
  it("Renames *.sw.json file while editor is open", async function () {
    this.timeout(30000);

    const WORKFLOW_NAME = "hello-world.sw.json";
    const RENAMED_WORKFLOW_NAME = "hello-world-renamed.sw.json";

    let editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME);
    let swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    let swfEditor = new SwfEditorTestHelper(editorWebViews[1]);
    await testHelper.saveScreenshot("renameFileWithEdOpen_1");

    let textEditor = await swfTextEditor.getSwfTextEditor();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, WORKFLOW_NAME), "utf-8");
    await testHelper.saveScreenshot("renameFileWithEdOpen_2");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
    await testHelper.saveScreenshot("renameFileWithEdOpen_3");

    await testHelper.renameFile(WORKFLOW_NAME, RENAMED_WORKFLOW_NAME);
    await testHelper.saveScreenshot("renameFileWithEdOpen_4");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
    await testHelper.saveScreenshot("renameFileWithEdOpen_5");

    await testHelper.closeAllEditors();
    await testHelper.saveScreenshot("renameFileWithEdOpen_6");

    editorWebViews = await testHelper.openFileFromSidebar(RENAMED_WORKFLOW_NAME);
    await testHelper.saveScreenshot("renameFileWithEdOpen_7");
    swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    await testHelper.saveScreenshot("renameFileWithEdOpen_8");
    swfEditor = new SwfEditorTestHelper(editorWebViews[1]);
    await testHelper.saveScreenshot("renameFileWithEdOpen_9");

    textEditor = await swfTextEditor.getSwfTextEditor();
    await testHelper.saveScreenshot("renameFileWithEdOpen_10");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
  });
});

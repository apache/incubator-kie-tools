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
  const SCREENSHOTS_DIR: string = path.resolve("screenshots");
  const pngPath = (fileName: string)=>join(SCREENSHOTS_DIR, fileName + ".png");


  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "basic-operations");
  if (!fs.existsSync(SCREENSHOTS_DIR)) {
    fs.mkdirSync(SCREENSHOTS_DIR);
  }
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

    fs.writeFileSync(pngPath("openAndSave_1"), await testHelper.takeScreenshot(), "base64");
    expect((await swfEditor.getAllNodeIds()).length).equal(6);

    let textEditor = await swfTextEditor.getSwfTextEditor();
    fs.writeFileSync(pngPath("openAndSave_2"), await testHelper.takeScreenshot(), "base64");

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
    fs.writeFileSync(pngPath("openAndSave_3"), await testHelper.takeScreenshot(), "base64");

    expect((await swfEditor.getAllNodeIds()).length).equal(7);
    fs.writeFileSync(pngPath("openAndSave_4"), await testHelper.takeScreenshot(), "base64");

    if (await textEditor.isDirty()) {
      await textEditor.save();
      console.log("Saving the changes.");
    } else {
      console.log("The editor doesn't have unsaved changes.");
    }
    fs.writeFileSync(pngPath("openAndSave_5"), await testHelper.takeScreenshot(), "base64");

    await testHelper.closeAllEditors();
    fs.writeFileSync(pngPath("openAndSave_6"), await testHelper.takeScreenshot(), "base64");

    editorWebViews = await testHelper.openFileFromSidebar(WORKFLOW_NAME);
    swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    swfEditor = new SwfEditorTestHelper(editorWebViews[1]);
    fs.writeFileSync(pngPath("openAndSave_7"), await testHelper.takeScreenshot(), "base64");

    textEditor = await swfTextEditor.getSwfTextEditor();
    fs.writeFileSync(pngPath("openAndSave_8"), await testHelper.takeScreenshot(), "base64");

    const editorTextTrimmedLines = (await textEditor.getText())
      .split(/\n/)
      .map((row) => row.trim())
      .join("\n");
    fs.writeFileSync(pngPath("openAndSave_9"), await testHelper.takeScreenshot(), "base64");

    expect(editorTextTrimmedLines).to.have.string(greetInGermanStateString);
    expect(editorTextTrimmedLines).to.have.string(germanConditionString);
    fs.writeFileSync(pngPath("openAndSave_10"), await testHelper.takeScreenshot(), "base64");

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
    fs.writeFileSync(pngPath("renameFileWithEdOpen_1"), await testHelper.takeScreenshot(), "base64");

    let textEditor = await swfTextEditor.getSwfTextEditor();
    const expectedContent = fs.readFileSync(path.resolve(TEST_PROJECT_FOLDER, WORKFLOW_NAME), "utf-8");
    fs.writeFileSync(pngPath("renameFileWithEdOpen_2"), await testHelper.takeScreenshot(), "base64");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
    fs.writeFileSync(pngPath("renameFileWithEdOpen_3"), await testHelper.takeScreenshot(), "base64");

    await testHelper.renameFile(WORKFLOW_NAME, RENAMED_WORKFLOW_NAME);
    fs.writeFileSync(pngPath("renameFileWithEdOpen_4"), await testHelper.takeScreenshot(), "base64");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
    fs.writeFileSync(pngPath("renameFileWithEdOpen_5"), await testHelper.takeScreenshot(), "base64");

    await testHelper.closeAllEditors();
    fs.writeFileSync(pngPath("renameFileWithEdOpen_6"), await testHelper.takeScreenshot(), "base64");

    editorWebViews = await testHelper.openFileFromSidebar(RENAMED_WORKFLOW_NAME);
    swfTextEditor = new SwfTextEditorTestHelper(editorWebViews[0]);
    swfEditor = new SwfEditorTestHelper(editorWebViews[1]);
    fs.writeFileSync(pngPath("renameFileWithEdOpen_7"), await testHelper.takeScreenshot(), "base64");

    textEditor = await swfTextEditor.getSwfTextEditor();
    fs.writeFileSync(pngPath("renameFileWithEdOpen_8"), await testHelper.takeScreenshot(), "base64");

    expect(await textEditor.getText()).equal(expectedContent);
    expect((await swfEditor.getAllNodeIds()).length).equal(3);
  });
});
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
import { expect } from "chai";
import { By, VSBrowser, WebDriver } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import DashbuilderEditorTestHelper from "./helpers/dashbuilder/DashbuilderEditorTestHelper";

// TODO Fix tests and re-enable them
describe.skip("Dashbuilder editor - smoke integration tests", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "smoke-test");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

  let testHelper: VSCodeTestHelper;
  let browser: VSBrowser;
  let webdriver: WebDriver;

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

  it("Opens simple-dashbuilder-example.dash.yml and checks its content is rendered", async function () {
    this.timeout(30000);
    const editorWebviews = await testHelper.openFileFromSidebar("simple-dashbuilder-example.dash.yml");

    const dashbuilderEditor = new DashbuilderEditorTestHelper(editorWebviews[1]);
    await dashbuilderEditor.switchToEditorFrame();

    const renderedContent = await dashbuilderEditor.getDashbuilderRenderedContent();
    expect(await renderedContent.isDisplayed()).is.true;

    const renderedContentText = await renderedContent.getText();
    expect(renderedContentText).contains("Welcome to Dashbuilder!");
    expect(renderedContentText).contains("Section Product Quantity");

    dashbuilderEditor.switchBack();
  });

  it("Opens empty-dashbuilder-examples.dash.yaml and check Dashbuilder empty view", async function () {
    this.timeout(30000);
    const editorWebviews = await testHelper.openFileFromSidebar("empty-dashbuilder-example.dash.yaml");

    const dashbuilderEditor = new DashbuilderEditorTestHelper(editorWebviews[1]);
    await dashbuilderEditor.switchToEditorFrame();

    const emptyContentView = await dashbuilderEditor.getEmptyContentView();
    const subtitleWebElement = await emptyContentView.findElement(By.xpath("//p[@id='subTitleParagraph']"));
    const emptyViewSubtitle = await subtitleWebElement.getText();
    expect(emptyViewSubtitle).contains("No content to display");

    dashbuilderEditor.switchBack();
  });
});

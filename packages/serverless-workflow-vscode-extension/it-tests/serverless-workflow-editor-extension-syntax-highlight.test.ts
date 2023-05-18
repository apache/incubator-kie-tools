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
import { By, WebDriver, WebElement } from "vscode-extension-tester";
import { VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - syntax highlighting test", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("it-tests-tmp", "resources", "syntax-highlight");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");

  let testHelper: VSCodeTestHelper;
  let driver: WebDriver;

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

  it("Checks syntax highlighting of *.sw.json files in serverless workflow editor", async function () {
    this.timeout(30000);

    const editorWebviews = await testHelper.openFileFromSidebar("syntax-highlight-hello-world.sw.json");
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    driver = textEditor.getDriver();

    // get color codes of json properties
    const property1WebElement = await getWebElementByDisplayedText("specVersion");
    const property2WebElement = await getWebElementByDisplayedText("states");
    const property1Color = await resolveWebElementColor(property1WebElement);
    const property2Color = await resolveWebElementColor(property2WebElement);

    // get color codes of json string values
    const stringValue1WebElement = await getWebElementByDisplayedText("helloworld");
    const stringValue2WebElement = await getWebElementByDisplayedText("HelloWorldWorkflow");
    const stringValue1Color = await resolveWebElementColor(stringValue1WebElement);
    const stringValue2Color = await resolveWebElementColor(stringValue2WebElement);

    // get color code of json boolean value
    const booleanValueWebElement = await getWebElementByDisplayedText("true");
    const booleanValueColor = await resolveWebElementColor(booleanValueWebElement);

    // check that properties have the same color
    expect(property1Color, "All properties should be the same color").equal(property2Color);

    // check that string values have the same color
    expect(stringValue1Color, "All string values should be the same color").equal(stringValue2Color);

    // check that properties, string values and boolean have different color
    const allTypesColors = [property1Color, stringValue1Color, booleanValueColor];
    const uniqueColors = Array.from(new Set(allTypesColors));
    expect(allTypesColors.length, "Colors of properties, string values and boolean values should differ").equal(
      uniqueColors.length
    );
  });

  async function getWebElementByDisplayedText(displayedText: string): Promise<WebElement> {
    return await driver.findElement(By.xpath("//*[text()[contains(.,'" + displayedText + "')]]"));
  }

  async function resolveWebElementColor(webElement: WebElement): Promise<string> {
    return await webElement.getAttribute("class");
  }
});

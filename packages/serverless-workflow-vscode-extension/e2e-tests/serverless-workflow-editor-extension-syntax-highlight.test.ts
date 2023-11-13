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
import { expect } from "chai";
import { By, WebDriver, WebElement } from "vscode-extension-tester";
import { VSCodeTestHelper, isWorkflowJSONFile } from "@kie-tools/vscode-extension-common-test-helpers";
import SwfTextEditorTestHelper from "./helpers/swf/SwfTextEditorTestHelper";

describe("Serverless workflow editor - syntax highlighting test", () => {
  const TEST_PROJECT_FOLDER: string = path.resolve("e2e-tests-tmp", "resources", "syntax-highlight");
  const DIST_E2E_TESTS_FOLDER: string = path.resolve("dist-e2e-tests");

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
    await testHelper.takeScreenshotOnTestFailure(this, DIST_E2E_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  it("Checks syntax highlighting of *.sw.json files in serverless workflow editor", async function () {
    this.timeout(30000);
    await testSyntaxHighlighting("syntax-highlight-hello-world.sw.json");
  });

  it("Checks syntax highlighting of *.sw.yaml files in serverless workflow editor", async function () {
    this.timeout(30000);
    await testSyntaxHighlighting("syntax-highlight-hello-world.sw.yaml");
  });

  async function testSyntaxHighlighting(workflowName: string): Promise<void> {
    const editorWebviews = await testHelper.openFileFromSidebar(workflowName);
    const swfTextEditor = new SwfTextEditorTestHelper(editorWebviews[0]);
    const textEditor = await swfTextEditor.getSwfTextEditor();

    driver = textEditor.getDriver();

    // get color codes of workflow properties
    const property1WebElement = await getWebElementByDisplayedText("specVersion");
    const property2WebElement = await getWebElementByDisplayedText("states");
    const property1Color = await resolveWebElementColor(property1WebElement);
    const property2Color = await resolveWebElementColor(property2WebElement);

    // get color codes of workflow string values
    const stringValue1WebElement = await getWebElementByDisplayedText("helloworld");
    const stringValue2WebElement = await getWebElementByDisplayedText("HelloWorldWorkflow");
    const stringValue1Color = await resolveWebElementColor(stringValue1WebElement);
    const stringValue2Color = await resolveWebElementColor(stringValue2WebElement);

    // get color code of workflow boolean value
    const booleanValueWebElement = await getWebElementByDisplayedText("true");
    const booleanValueColor = await resolveWebElementColor(booleanValueWebElement);

    // check that properties have the same color
    expect(property1Color, "All properties should be the same color").equal(property2Color);

    // check that string values have the same color
    expect(stringValue1Color, "All string values should be the same color").equal(stringValue2Color);

    // check elements types color differences
    // YAML unique types
    const allTypesColors = [property1Color, stringValue1Color];
    let assertionMessage = "Colors of properties and string values should differ";

    if (isWorkflowJSONFile(workflowName)) {
      // JSON unique types
      allTypesColors.push(booleanValueColor);
      assertionMessage = "Colors of properties, string values and boolean values should differ";
    }
    const uniqueColors = Array.from(new Set(allTypesColors));

    expect(allTypesColors.length, assertionMessage).equal(uniqueColors.length);
  }

  async function getWebElementByDisplayedText(displayedText: string): Promise<WebElement> {
    return await driver.findElement(
      By.xpath("//div[@class='editor-container']//*[text()[contains(.,'" + displayedText + "')]]")
    );
  }

  async function resolveWebElementColor(webElement: WebElement): Promise<string> {
    return await webElement.getAttribute("class");
  }
});

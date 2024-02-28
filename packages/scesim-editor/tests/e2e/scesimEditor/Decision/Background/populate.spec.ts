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

import { test, expect } from "../../../__fixtures__/base";

test.describe("Populate Decision Background table", () => {
  test("should correctly populate a background table", async ({ stories, page, resizing }) => {
    await stories.openBackgroundTableDecision();
    await page.getByLabel("Background").getByTestId("monaco-container").click();
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > .data-cell > .editable-cell"
      )
      .press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foo}");
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > thead > tr:nth-child(3) > .\\31 \\|1"
      )
      .click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(2) > .editable-cell > .feel-input > div"
      )
      .click();
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(2) > .editable-cell"
      )
      .press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("[foo]");
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(3) > .editable-cell > .feel-input > div"
      )
      .click();
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(3) > .editable-cell"
      )
      .press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"foo"');
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(4) > .editable-cell > .feel-input > div"
      )
      .click();
    await page
      .locator(
        ".kie-scesim-editor--background-table-container > .test-scenario-table > .expression-container > .expression-container-box > .standalone-bee-table > .boxed-expression-provider > .table-component > table > tbody > .\\30  > td:nth-child(4) > .editable-cell"
      )
      .press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(",./123981275980172957129517");
    await page.locator("td:nth-child(5) > .editable-cell > .feel-input > div").click();
    await page.locator("td:nth-child(5) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foo}{foo}f");
    await page.locator("td:nth-child(6) > .editable-cell > .feel-input > div").click();
    await page.locator("td:nth-child(6) > .editable-cell").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("=1-205=-1205=-0125-0215215");
    await resizing.reset(page.getByRole("columnheader", { name: "INSTANCE-1 (<Undefined>)" }));
    await expect(page.getByLabel("Background").getByTestId("standalone-bee-table")).toHaveScreenshot(
      "background-table-decision.png"
    );
  });
});

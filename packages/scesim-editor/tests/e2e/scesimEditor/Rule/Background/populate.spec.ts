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

test.describe("Populate Rule Background table", () => {
  test("should correctly populate a background table", async ({ stories, page, resizing }) => {
    await stories.openBackgroundTableRule();
    await page.getByLabel("Background").getByTestId("monaco-container").click();
    await page.getByLabel("Background").getByTestId("monaco-container").press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foo}");
    await page.getByRole("columnheader", { name: "PROPERTY (<Undefined>)" }).click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").locator("path").click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(1).click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(1).press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("[foo]");
    await page.getByLabel("Background").getByTestId("monaco-container").nth(2).click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(2).press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill('"foo"');
    await page.getByLabel("Background").getByTestId("monaco-container").nth(3).click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(3).press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(",./123981275980172957129517");
    await page.getByLabel("Background").getByTestId("monaco-container").nth(4).click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(4).press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("{foo}{foo}f");
    await page.getByLabel("Background").getByTestId("monaco-container").nth(5).click();
    await page.getByLabel("Background").getByTestId("monaco-container").nth(5).press("Enter");
    await page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill("=1-205=-1205=-0125-0215215");
    await resizing.reset(page.getByRole("columnheader", { name: "INSTANCE-1 (<Undefined>)" }));
    await expect(page.getByLabel("Background")).toHaveScreenshot("background-table-rule.png");
  });
});

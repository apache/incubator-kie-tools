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

import { expect, test } from "../../__fixtures__/base";

test.describe("Keyboard", () => {
  test.describe("Navigation", () => {
    test("should correctly navigate", async () => {
      // enter, shift+enter, tab, shift+tab, escape
      // currently not working in playwright
    });
  });

  test.describe("Arrow Key Navigation", () => {
    test("should correctly navigate the page using arrow keys", async ({ stories, page }) => {
      await stories.openTestScenarioTableDecision();
      await page.getByRole("cell", { name: "1" }).click();
      await page.getByRole("cell", { name: "1" }).locator("div").nth(1).click();
      await page.getByRole("cell", { name: "1" }).locator("div").nth(1).click();
      await page.getByRole("cell", { name: "1" }).locator("div").nth(1).click();
      await page.getByRole("row", { name: "1", exact: true }).click();
      await page.getByRole("row", { name: "1", exact: true }).press("ArrowRight");
      await page.getByRole("row", { name: "1", exact: true }).press("ArrowRight");
      await page.getByRole("row", { name: "1", exact: true }).press("ArrowDown");
      await page.getByRole("row", { name: "2", exact: true }).press("ArrowLeft");
      await page.getByRole("row", { name: "1", exact: true }).press("ArrowDown");
      await page.getByRole("row", { name: "2", exact: true }).press("ArrowLeft");
      await page.getByRole("row", { name: "3", exact: true }).press("ArrowUp");
      await page.getByRole("row", { name: "2", exact: true }).press("ArrowRight");
      await page.getByRole("row", { name: "2", exact: true }).press("ArrowDown");
      await page.getByRole("row", { name: "3", exact: true }).press("ArrowDown");
      await page.getByRole("row", { name: "4", exact: true }).press("Enter");
      await page
        .getByLabel("Editor content;Press Alt+F1 for Accessibility Options.")
        .fill("This text should go in the 4th row, 2nd column");
      await expect(page.getByLabel("Test Scenario")).toHaveScreenshot("navigation-arrow-screenshot.png");
    });
  });
});

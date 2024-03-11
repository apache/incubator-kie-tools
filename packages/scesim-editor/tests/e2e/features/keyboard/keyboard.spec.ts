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
import { AddRowPosition } from "../../__fixtures__/scesimEditor";

test.describe("Keyboard", () => {
  test.describe("Keyboard-Shortcuts Navigation", () => {
    test("should correctly navigate the page using enter, shift+enter, tab, shift+tab, escape", async ({
      stories,
      page,
      scesimEditor,
    }) => {
      await stories.openTestScenarioTableRule();
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await page.getByRole("row", { name: "1", exact: true }).click();
      await page.getByRole("row", { name: "1", exact: true }).press("Tab");
      await page.getByRole("row", { name: "1", exact: true }).press("Tab");
      await page.getByRole("row", { name: "1", exact: true }).press("Enter");
      await page.getByRole("row", { name: "1", exact: true }).press("Enter");
      await page.getByRole("row", { name: "2", exact: true }).press("Shift+Tab");
      await page.getByRole("row", { name: "1", exact: true }).press("Enter");
      await page.getByRole("row", { name: "1", exact: true }).press("Enter");
      await page.getByRole("row", { name: "2", exact: true }).press("Shift+Tab");
      await page.getByRole("row", { name: "3", exact: true }).press("Shift+Enter");
      await page.getByRole("row", { name: "3", exact: true }).press("Shift+Enter");
      await page.getByRole("row", { name: "2", exact: true }).press("Tab");
      await page.getByRole("row", { name: "2", exact: true }).press("Enter");
      await page.getByRole("row", { name: "2", exact: true }).press("Enter");
      await page.getByRole("row", { name: "3", exact: true }).press("Enter");
      await page.getByRole("row", { name: "3", exact: true }).press("Enter");
      await page.getByRole("row", { name: "4", exact: true }).press("Enter");
      await expect(page.getByLabel("Test Scenario")).toHaveScreenshot("navigation-screenshot.png");
      await page.getByRole("row", { name: "4", exact: true }).press("Escape");
      await expect(page.getByLabel("Test Scenario")).toHaveScreenshot(
        "navigation-keyboard-shortcut-escaped-screenshot.png"
      );
    });
  });

  test.describe("Arrow Key Navigation", () => {
    test("should correctly navigate the page using arrow keys", async ({ stories, page, cells, scesimEditor }) => {
      await stories.openTestScenarioTableDecision();
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await scesimEditor.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await page.getByRole("row", { name: "1", exact: true }).click();
      await cells.navigateRight(page.getByRole("row", { name: "1", exact: true }));
      await cells.navigateRight(
        page.getByRole("row", { name: "1", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await cells.navigateDown(
        page.getByRole("row", { name: "1", exact: true }).getByTestId("monaco-container").nth(2)
      );
      await cells.navigateLeft(
        page.getByRole("row", { name: "2", exact: true }).getByTestId("monaco-container").nth(2)
      );
      await cells.navigateDown(
        page.getByRole("row", { name: "2", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await cells.navigateLeft(
        page.getByRole("row", { name: "3", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await cells.navigateUp(page.getByRole("row", { name: "3", exact: true }));
      await cells.navigateRight(
        page.getByRole("row", { name: "2", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await cells.navigateDown(
        page.getByRole("row", { name: "3", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await cells.navigateDown(
        page.getByRole("row", { name: "4", exact: true }).getByTestId("monaco-container").nth(1)
      );
      await expect(page.getByLabel("Test Scenario")).toHaveScreenshot("navigation-screenshot.png");
    });
  });
});

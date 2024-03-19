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
import { AssetType } from "../../__fixtures__/editor";
import { AddRowPosition, Type } from "../../__fixtures__/table";

test.describe("Keyboard", () => {
  test.describe("Keyboard-Shortcuts Navigation", () => {
    test("should correctly navigate the page using keyboard shortcuts", async ({
      editor,
      table,
      testScenarioTable,
    }) => {
      await editor.createTestScenario(AssetType.RULE);
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.selectCell({ rowNumber: "1", columnNumber: 0 });
      await table.navigateRight({ rowNumber: "1", columnNumber: 0, type: Type.KEYBOARD_SHORTCUT });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-right.png", {
        maxDiffPixels: 1000,
      });
      await table.navigateDown({ rowNumber: "1", columnNumber: 1, type: Type.KEYBOARD_SHORTCUT });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-down.png", { maxDiffPixels: 1000 });
      await table.navigateLeft({ rowNumber: "2", columnNumber: 1, type: Type.KEYBOARD_SHORTCUT });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-left.png", { maxDiffPixels: 1000 });
      await table.navigateUp({ rowNumber: "2", columnNumber: 0, type: Type.KEYBOARD_SHORTCUT });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-up.png", { maxDiffPixels: 1000 });
      await table.deselectCell({ rowNumber: "2", columnNumber: 0 });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-escaped-screenshot.png");
    });
  });

  test.describe("Arrow Key Navigation", () => {
    test("should correctly navigate the page using arrow keys", async ({ editor, table, testScenarioTable }) => {
      await editor.createTestScenario(AssetType.DECISION);
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.addRow({ targetCell: "1", position: AddRowPosition.BELOW });
      await table.selectCell({ rowNumber: "1", columnNumber: 0 });
      await table.navigateRight({ rowNumber: "1", columnNumber: 0, type: Type.ARROW });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-right.png", {
        maxDiffPixels: 1000,
      });
      await table.navigateDown({ rowNumber: "1", columnNumber: 1, type: Type.ARROW });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-down.png", { maxDiffPixels: 1000 });
      await table.navigateLeft({ rowNumber: "2", columnNumber: 1, type: Type.ARROW });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-left.png", { maxDiffPixels: 1000 });
      await table.navigateUp({ rowNumber: "2", columnNumber: 0, type: Type.ARROW });
      await expect(testScenarioTable.get()).toHaveScreenshot("navigation-screenshot-up.png", { maxDiffPixels: 1000 });
    });
  });
});

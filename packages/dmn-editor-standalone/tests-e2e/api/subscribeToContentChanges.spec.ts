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

import { test, expect } from "../__fixtures__/base";
import { DefaultNodeName } from "../__fixtures__/editor";

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("subscribeToContentChanges and unsubscribeToContentChanges", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should count edits via subscribeToContentChanges", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      const inputSelector = editorIFrame.getByTitle("Input Data", { exact: true });
      const editorDiagram = editor.getEditorDiagram();

      // Add 4 Input Data nodes and check if edit count increases to 4
      for (let i = 0; i < 4; i++) {
        await inputSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 100 } });
        await editor.resetFocus();
        await expect(
          (await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.INPUT_DATA}"]`).all()).length
        ).toBe(i + 1);
        await expect(await page.locator("#edit-counter")).toHaveText((i + 1).toString());
      }
    });

    test("should stop counting edits after unsubscribeToContentChanges", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      const inputSelector = editorIFrame.getByTitle("Input Data", { exact: true });
      const editorDiagram = editor.getEditorDiagram();

      // Add 4 Input Data nodes and check if edit count increases to 4
      for (let i = 0; i < 4; i++) {
        await inputSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 100 } });
        await editor.resetFocus();
      }
      await expect(
        (await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.INPUT_DATA}"]`).all()).length
      ).toBe(4);

      await expect(await page.locator("#edit-counter")).toHaveText("4");

      await editor.unsubscribeToContentChanges();

      // Add 2 more Input Data nodes and check if edit count remains the same
      for (let i = 0; i < 2; i++) {
        await inputSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 300 } });
        await editor.resetFocus();
      }
      await expect(
        (await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.INPUT_DATA}"]`).all()).length
      ).toBe(6);

      await expect(await page.locator("#edit-counter")).toHaveText("4");
    });
  });
});

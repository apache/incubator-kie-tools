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

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("markAsSaved", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should reset edit count edits when markAsSaved is called", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      const inputSelector = editorIFrame.getByTitle("Input Data", { exact: true });
      const editorDiagram = editor.getEditorDiagram();

      // Add 4 Input Data nodes and check if dirty count increases to 4
      for (let i = 0; i < 4; i++) {
        await inputSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 100 } });
        await editor.resetFocus();
      }
      await expect(await page.locator("#edit-counter")).toHaveText("4");
      await expect(await page.locator("#is-dirty")).toHaveText("true");

      await editor.markAsSaved();

      // Marking as saved generates a new content change event, causing the counter to increase.
      await expect(await page.locator("#edit-counter")).toHaveText("5");
      await expect(await page.locator("#is-dirty")).toHaveText("false");
    });
  });
});

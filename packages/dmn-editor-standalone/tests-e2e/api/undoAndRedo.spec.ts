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
  test.describe("undo and redo", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should undo and redo edits when undo or redo are called", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      const decisionSelector = editorIFrame.getByTitle("Decision", { exact: true });
      const editorDiagram = editor.getEditorDiagram();

      // Add 4 Decision nodes
      for (let i = 0; i < 4; i++) {
        await decisionSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 100 } });
        await editor.resetFocus();
      }

      await expect(await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.DECISION}"]`)).toHaveCount(4);

      // The checks for the edit counter are here to make sure that we wait for the debounce time.
      // Each action on the editor (such as adding a node) has a debounce time to count as an edit,
      // meaning that dragging 4 new Decision nodes in quick succession may be counted as 1 or 2 edits
      // instead of 4.
      // If we wait for the content changes event (via subscribeToContentChanges) to have the edit counter
      // updated, that means that the debounce time has already passed and we can undo/redo single edits.
      // Edit counts are incremental in this example (regardless of undo/redo), that's why it is 4 after
      // adding 4 nodes and 5 after undoing one of them.
      await expect(await page.locator("#edit-counter")).toHaveText("4");

      await editor.undo();

      await expect(await page.locator("#edit-counter")).toHaveText("5");

      await expect(await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.DECISION}"]`)).toHaveCount(3);

      await editor.undo();
      await editor.undo();
      await editor.undo();

      await expect(await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.DECISION}"]`)).toHaveCount(0);
      await expect(await page.locator("#edit-counter")).toHaveText("8");

      // Should show initial modal for an empty DMN
      await expect(editor.getEditorIframe().getByText("This DMN's Diagram is empty")).toBeAttached();

      await editor.redo();
      await editor.redo();

      await expect(await editorIFrame.locator(`div[data-nodelabel="${DefaultNodeName.DECISION}"]`)).toHaveCount(2);
      await expect(await page.locator("#edit-counter")).toHaveText("10");
    });
  });
});

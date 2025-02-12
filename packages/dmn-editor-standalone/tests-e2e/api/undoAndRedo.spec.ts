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
import { NodeType } from "../__fixtures__/nodes";

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("undo and redo", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
      test.slow();
    });

    // Obs.: Draging a new node then renaming it counts as 2 edits, that's why we need to undo/redo twice for each node.
    test("should undo and redo edits when undo or redo are called", async ({ page, editor, palette, nodes }) => {
      // The checks for the edit counter are here to make sure that we wait for the debounce time.
      // Each action on the editor (such as adding a node or renaming) has a debounce time to count as an edit,
      // meaning that dragging and renaming 4 new Decision nodes in quick succession may be counted as
      // less than 8 edits.
      // If we wait for the content changes event (via subscribeToContentChanges) to have the edit counter
      // updated, that means that the debounce time has already passed and we can undo/redo single edits.
      // Edit counts are incremental in this example (regardless of undo/redo), that's why it is 8 after
      // adding and ranaming 4 nodes and 10 after undoing one of them.
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input-A",
      });
      await expect(await editor.getEditCount()).toHaveText("2");
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 300, y: 100 },
        thenRenameTo: "Input-B",
      });
      await expect(await editor.getEditCount()).toHaveText("4");
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 500, y: 100 },
        thenRenameTo: "Input-C",
      });
      await expect(await editor.getEditCount()).toHaveText("6");
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 700, y: 100 },
        thenRenameTo: "Input-D",
      });
      await expect(await editor.getEditCount()).toHaveText("8");
      await expect(nodes.get({ name: "Input-A" })).toBeAttached();
      await expect(nodes.get({ name: "Input-B" })).toBeAttached();
      await expect(nodes.get({ name: "Input-C" })).toBeAttached();
      await expect(nodes.get({ name: "Input-D" })).toBeAttached();

      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("9");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("10");

      await expect(nodes.get({ name: "Input-A" })).toBeAttached();
      await expect(nodes.get({ name: "Input-B" })).toBeAttached();
      await expect(nodes.get({ name: "Input-C" })).toBeAttached();
      await expect(nodes.get({ name: "Input-D" })).not.toBeAttached();

      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("11");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("12");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("13");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("14");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("15");
      await editor.undo();
      await expect(await editor.getEditCount()).toHaveText("16");

      await expect(nodes.get({ name: "Input-A" })).not.toBeAttached();
      await expect(nodes.get({ name: "Input-B" })).not.toBeAttached();
      await expect(nodes.get({ name: "Input-C" })).not.toBeAttached();
      await expect(nodes.get({ name: "Input-D" })).not.toBeAttached();

      // Should show initial modal for an empty DMN
      await expect(editor.get().getByRole("heading", { name: "This DMN's Diagram is empty" })).toBeAttached();

      await editor.redo();
      await expect(await editor.getEditCount()).toHaveText("17");
      await editor.redo();
      await expect(await editor.getEditCount()).toHaveText("18");
      await editor.redo();
      await expect(await editor.getEditCount()).toHaveText("19");
      await editor.redo();
      await expect(await editor.getEditCount()).toHaveText("20");

      await expect(nodes.get({ name: "Input-A" })).toBeAttached();
      await expect(nodes.get({ name: "Input-B" })).toBeAttached();
      await expect(nodes.get({ name: "Input-C" })).not.toBeAttached();
      await expect(nodes.get({ name: "Input-D" })).not.toBeAttached();
    });
  });
});

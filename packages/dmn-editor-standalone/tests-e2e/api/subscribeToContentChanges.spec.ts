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
  test.describe("subscribeToContentChanges and unsubscribeToContentChanges", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
      test.slow();
    });

    // Obs.: Draging a new node then renaming it counts as 2 edits
    test("should count edits via subscribeToContentChanges", async ({ editor, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input-A",
      });
      await expect(nodes.get({ name: "Input-A" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("2");

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 300, y: 100 },
        thenRenameTo: "Input-B",
      });
      await expect(nodes.get({ name: "Input-B" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("4");

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 500, y: 100 },
        thenRenameTo: "Input-C",
      });
      await expect(nodes.get({ name: "Input-C" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("6");

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 700, y: 100 },
        thenRenameTo: "Input-D",
      });
      await expect(nodes.get({ name: "Input-D" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("8");
    });

    test("should stop counting edits after unsubscribeToContentChanges", async ({ editor, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input-A",
      });
      await expect(nodes.get({ name: "Input-A" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("2");

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 300, y: 100 },
        thenRenameTo: "Input-B",
      });
      await expect(nodes.get({ name: "Input-B" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("4");

      await editor.unsubscribeToContentChanges();

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 500, y: 100 },
        thenRenameTo: "Input-C",
        dontWaitForEditCount: true,
      });
      await expect(nodes.get({ name: "Input-C" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("4");

      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 700, y: 100 },
        thenRenameTo: "Input-D",
        dontWaitForEditCount: true,
      });
      await expect(nodes.get({ name: "Input-D" })).toBeAttached();
      await expect(await editor.getEditCount()).toHaveText("4");
    });
  });
});

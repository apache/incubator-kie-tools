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

import { test, expect } from "../../__fixtures__/base";
import { DefaultNodeName, NodeType } from "../../__fixtures__/nodes";

test.beforeEach(async ({ editor }) => {
  await editor.open();
});

test.describe("Keyboard Shortcuts", () => {
  test("Cancel action - Escape", async ({ palette, page, diagram }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 100 } });
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 200, y: 200 } });
    await diagram.select({ startPosition: { x: 50, y: 50 }, endPosition: { x: 500, y: 500 } });
    await expect(diagram.get()).toHaveScreenshot("selected-multiple-nodes.png");
    await page.keyboard.press("Escape");
    await expect(diagram.get()).toHaveScreenshot("cancel-action-with-keyboard-shortcut.png");
  });

  test("Create group - G", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 100 } });
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 200, y: 200 } });
    await diagram.select({ startPosition: { x: 50, y: 50 }, endPosition: { x: 500, y: 500 } });
    await page.keyboard.press("G");
    await expect(diagram.get()).toHaveScreenshot("created-group-with-keyboard-shortcut.png");
  });

  test("Delete node - Backspace", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-delete.png");
    await page.keyboard.press("Backspace");
    await expect(diagram.get()).toHaveScreenshot("deleted-decision-node-using-backspace.png");
  });

  test("Delete node - Delete", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await expect(nodes.get({ name: DefaultNodeName.DECISION })).toBeAttached();
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-delete.png");
    await page.keyboard.press("Delete");
    await expect(diagram.get()).toHaveScreenshot("deleted-decision-node-delete.png");
  });

  test("Focus on selection - B", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 100 } });
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 200, y: 200 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-focus.png");
    await page.keyboard.press("B");
    await expect(diagram.get()).toHaveScreenshot("focused-on-selection-using-shortcut.png");
  });

  test("Hide from DRD - X", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-hide.png");
    await page.keyboard.press("X");
    await expect(diagram.get()).toHaveScreenshot("hide-decision-node-from-drd.png");
  });

  test("Reset position to origin - Space", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 10 } });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 110 } });
    await diagram.resetFocus();
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 210 } });
    await diagram.zoomIn({ clicks: 1 });
    await expect(diagram.get()).toHaveScreenshot("zoomed-in-diagram.png");
    await page.keyboard.press("Space");
    await expect(diagram.get()).toHaveScreenshot("reset-position-to-origin.png");
  });

  test("Select/Deselect all - A", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 100 } });
    await palette.dragNewNode({ type: NodeType.INPUT_DATA, targetPosition: { x: 200, y: 200 } });
    await page.keyboard.press("A");
    await expect(diagram.get()).toHaveScreenshot("selected-all-nodes.png");
    await page.keyboard.press("A");
    await expect(diagram.get()).toHaveScreenshot("unselected-all-nodes.png");
  });

  test("Zoom - Hold Control Or Meta", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 200, y: 100 } });
    await expect(diagram.get()).toHaveScreenshot("added-decision-node-to-zoom.png");
    await page.keyboard.down("ControlOrMeta");
    await page.mouse.wheel(0, 100);
    await page.keyboard.up("ControlOrMeta");
    await expect(diagram.get()).toHaveScreenshot("zoomed-drd-using-shortcut.png");
  });

  test("Cut node - Control Or Meta + X", async ({ palette, nodes, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-cut.png");
    await page.keyboard.press("ControlOrMeta+X");
    await expect(diagram.get()).toHaveScreenshot("decision-node-cut-from-drd-using-cmd-key.png");
  });

  test("Copy/Paste node using shotcuts", async ({ palette, nodes, diagram, browserName, page }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await nodes.select({ name: DefaultNodeName.DECISION });
    await expect(diagram.get()).toHaveScreenshot("selected-decision-node-to-copy.png");
    await page.keyboard.press("ControlOrMeta+C");
    await page.keyboard.press("ControlOrMeta+V");
    await nodes.select({ name: DefaultNodeName.DECISION });
    await expect(diagram.get()).toHaveScreenshot("changed-decision-position.png");
  });

  test("Horizontal navigation - Hold Shift", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await expect(diagram.get()).toHaveScreenshot("added-decision-node-to-scroll-horizontal.png");
    await page.keyboard.down("Shift");
    await page.mouse.wheel(200, 0);
    await page.keyboard.up("Shift");
    await expect(diagram.get()).toHaveScreenshot("scrolled-horizontal-using-shotcut.png");
  });

  test("Undo/Redo actions using shortcuts", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await palette.dragNewNode({
      type: NodeType.DECISION,
      targetPosition: { x: 300, y: 300 },
      thenRenameTo: "Second Decision",
    });
    await expect(diagram.get()).toHaveScreenshot("added-decision-for-undo-redo-action.png");
    await page.keyboard.press("ControlOrMeta+Z");
    await expect(diagram.get()).toHaveScreenshot("undo-decision-rename-using-shoftcut.png");
    await page.keyboard.press("ControlOrMeta+Shift+Z");
    await expect(diagram.get()).toHaveScreenshot("redo-decision-rename-using-shoftcut.png");
  });

  test("Toggle properties panel - I", async ({ palette, diagram, page }) => {
    await palette.dragNewNode({ type: NodeType.DECISION, targetPosition: { x: 100, y: 100 } });
    await page.keyboard.press("I");
    await expect(diagram.get()).toHaveScreenshot("show-properties-using-shoftcut.png");
    await page.keyboard.press("I");
    await expect(diagram.get()).toHaveScreenshot("hide-properties-using-shoftcut.png");
  });
});

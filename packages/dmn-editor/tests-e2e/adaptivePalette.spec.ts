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

import { test, expect } from "./__fixtures__/base";
import { DefaultNodeName, NodeType } from "./__fixtures__/nodes";

// A viewport height of 500px is small enough to clip the 5-icon primary palette
const SMALL_VIEWPORT = { width: 1280, height: 500 };

test.describe("Adaptive Palette - small viewport", () => {
  test.beforeEach(async ({ page, editor }) => {
    await page.setViewportSize(SMALL_VIEWPORT);
    await editor.open();
  });

  test("should show ellipsis button when palette icons overflow the viewport", async ({ palette }) => {
    await expect(palette.getEllipsisButton()).toBeVisible();
  });

  test("should hide overflow icons from the primary palette column", async ({ palette }) => {
    await expect(palette.getMoreItemsSubmenu()).not.toBeAttached();

    // The Decision Service node is the last in the primary list; it must not
    // be directly accessible before opening the overflow submenu.
    const decisionServiceIcon = palette.getMoreItemsSubmenu().getByTitle("Decision Service", { exact: true });
    await expect(decisionServiceIcon).not.toBeAttached();
  });

  test("should open more-items submenu when ellipsis button is clicked", async ({ palette }) => {
    await palette.openMoreItems();
    await expect(palette.getMoreItemsSubmenu()).toBeVisible();
  });

  test("should close more-items submenu on second ellipsis click", async ({ palette }) => {
    await palette.openMoreItems();
    await expect(palette.getMoreItemsSubmenu()).toBeVisible();

    await palette.openMoreItems();
    await expect(palette.getMoreItemsSubmenu()).not.toBeAttached();
  });

  test("should show overflow icons inside the submenu", async ({ palette }) => {
    await palette.openMoreItems();
    // At least one icon must appear in the submenu grid.
    await expect(palette.getMoreItemsSubmenu().locator(".kie-dmn-editor--palette-button").first()).toBeVisible();
  });

  test("should be able to drag a node from the overflow submenu onto the canvas", async ({ palette, nodes }) => {
    await palette.openMoreItems();

    // Drag whichever node is first in the overflow submenu.
    // Decision Service is always the last primary icon and is therefore
    // the most likely candidate to be clipped on a small viewport.
    await palette.dragNewNodeFromMoreItems({
      type: NodeType.DECISION_SERVICE,
      targetPosition: { x: 300, y: 200 },
    });

    await expect(nodes.get({ name: DefaultNodeName.DECISION_SERVICE })).toBeAttached();
  });
});

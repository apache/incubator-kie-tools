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

test.beforeEach(async ({ editor, nodes }) => {
  await editor.openLoanPreQualification();
  await expect(nodes.get({ name: "Loan Pre-Qualification" })).toBeVisible();
  await nodes.select({ name: "Loan Pre-Qualification" });
});

test.describe("Move selection - keyboard shortcut", () => {
  test("Move selection up", async ({ diagram, page }) => {
    await page.keyboard.press("ArrowUp");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-up.png");
  });

  test("Move selection down", async ({ diagram, page }) => {
    await page.keyboard.press("ArrowDown");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-down.png");
  });

  test("Move selection left", async ({ diagram, page }) => {
    await page.keyboard.press("ArrowLeft");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-left.png");
  });

  test("Move selection right", async ({ diagram, page }) => {
    await page.keyboard.press("ArrowRight");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-right.png");
  });

  test("Move selection up a big distance", async ({ diagram, page }) => {
    await page.keyboard.down("Shift");
    await page.keyboard.press("ArrowUp");
    await page.keyboard.up("Shift");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-up-big-distance.png");
  });

  test("Move selection down a big distance", async ({ diagram, page }) => {
    await page.keyboard.down("Shift");
    await page.keyboard.press("ArrowDown");
    await page.keyboard.up("Shift");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-down-big-distance.png");
  });

  test("Move selection left a big distance", async ({ diagram, page }) => {
    await page.keyboard.down("Shift");
    await page.keyboard.press("ArrowLeft");
    await page.keyboard.up("Shift");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-left-big-distance.png");
  });

  test("Move selection right a big distance", async ({ diagram, page }) => {
    await page.keyboard.down("Shift");
    await page.keyboard.press("ArrowRight");
    await page.keyboard.up("Shift");
    await expect(diagram.get()).toHaveScreenshot("selected-node-moved-right-big-distance.png");
  });
});

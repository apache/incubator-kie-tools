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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Boxed List context menu", () => {
  test.describe("Rows control", () => {
    test.beforeEach(async ({ stories, page, monaco, boxedExpressionEditor }) => {
      await stories.openBoxedList();
      await boxedExpressionEditor.selectBoxedLiteral();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    });

    test("shouldn't render column context menu", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "ROWS" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");
    });

    test("shouldn't render row context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "ROWS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");

      await page.getByTestId("monaco-container").click({ button: "right" });
      await expect(page.getByRole("heading", { name: "ROWS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");
    });

    test("should open row context menu and insert row above", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });

    test("should open row context menu and insert row below", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(0)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open row context menu and insert multiples rows above", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "4" })).toContainText("test");
    });

    test("should open row context menu and insert multiples rows below", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open row context menu and delete row", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(0)).toContainText("test");
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({
    stories,
    page,
    monaco,
  }) => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/421");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/421",
    });

    await stories.openRelation();
    await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    await page.getByTestId("monaco-container").click({ button: "right" });
    await page.getByRole("menuitem", { name: "Insert", exact: true }).first().click();
    await page.getByRole("cell", { name: "1" }).click({ button: "right" });
    await expect(page.getByRole("heading", { name: "ROWS" })).toBeAttached();
    await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories, page, boxedExpressionEditor, monaco }) => {
      await stories.openBoxedList();
      await boxedExpressionEditor.selectBoxedLiteral();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    });

    test.describe("Add rows", () => {
      test("should add row above by positioning mouse on the index cell upper section", async ({ page }) => {
        await page.getByRole("cell", { name: "1" }).hover({ position: { x: 0, y: 0 } });
        await page.getByRole("cell", { name: "1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "2" })).toContainText("test");
      });

      test("should add row below by positioning mouse on the index cell lower section", async ({ page }) => {
        await page.getByRole("cell", { name: "1" }).hover();
        await page.getByRole("cell", { name: "1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "1" }).nth(0)).toContainText("test");
        await expect(page.getByRole("row", { name: "2" })).toBeAttached();
      });
    });
  });
});

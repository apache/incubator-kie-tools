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

test.describe("Boxed Invocation context menu", () => {
  test.describe("Parameters control", () => {
    test.beforeEach(async ({ stories, page, monaco, boxedExpressionEditor }) => {
      await stories.openBoxedInvocation();
      await boxedExpressionEditor.selectBoxedLiteral();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    });

    test("should't render selection context menu", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "PARAMETERS" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).not.toBeAttached();
    });

    test("shouldn't render parameters context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "PARAMETERS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");

      await page.getByTestId("monaco-container").click({ button: "right" });
      await expect(page.getByRole("heading", { name: "PARAMETERS" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");
    });

    test("should open parameters context menu and insert parameters above", async ({ page }) => {
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "p-" }).nth(1)).toContainText("test");
    });

    test("should open parameters context menu and insert parameters below", async ({ page }) => {
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "p-" }).nth(0)).toContainText("test");
    });

    test("should open parameters context menu and insert multiples parameters above", async ({ page }) => {
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(4);
      await expect(page.getByRole("row", { name: "p-" }).nth(3)).toContainText("test");
    });

    test("should open parameters context menu and insert multiples parameters below", async ({ page }) => {
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "p-" }).nth(0)).toContainText("test");
    });

    test("should open parameters context menu and delete row", async ({ page }) => {
      await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "p-" }).nth(1)).toContainText("test");
      await page.getByRole("cell", { name: "p-2" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "p-" })).toHaveCount(1);
      await expect(page.getByRole("row", { name: "p-" }).nth(0)).toContainText("test");
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
    await page.getByRole("cell", { name: "p-1" }).click({ button: "right" });
    await expect(page.getByRole("heading", { name: "PARAMETERS" })).toBeAttached();
    await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories, page, boxedExpressionEditor, monaco }) => {
      await stories.openBoxedInvocation();
      await boxedExpressionEditor.selectBoxedLiteral();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    });

    test.describe("Add parameters", () => {
      test("should add parameters above by positioning mouse on the index cell upper section", async ({ page }) => {
        await page.getByRole("cell", { name: "p-1" }).hover({ position: { x: 0, y: 0 } });
        await page.getByRole("cell", { name: "p-1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
        await expect(page.getByRole("row", { name: "p-" }).nth(1)).toContainText("test");
      });

      test("should add parameters below by positioning mouse on the index cell lower section", async ({ page }) => {
        await page.getByRole("cell", { name: "p-1" }).hover();
        await page.getByRole("cell", { name: "p-1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "p-" })).toHaveCount(2);
        await expect(page.getByRole("row", { name: "p-" }).nth(0)).toContainText("test");
      });
    });
  });
});

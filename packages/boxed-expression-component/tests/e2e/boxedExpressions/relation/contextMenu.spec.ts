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

test.describe("Relation context menu", () => {
  test.describe("Rows control", () => {
    test.beforeEach(async ({ stories, page, monaco }) => {
      await stories.openRelation();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
    });

    test("shouldn't render column context menu", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await expect(page.getByRole("heading", { name: "ROWS" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
    });

    test("should open row context menu and insert row above", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });

    test("should open row context menu and insert row below", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open row context menu and insert multiples rows above", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.AFFECTED_BY,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "4" })).toContainText("test");
    });

    test("should open row context menu and insert multiples rows below", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.AFFECTED_BY,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("should open row context menu and delete row", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
    });

    test("should open row context menu and duplicate row", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Duplicate" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });
  });

  test.describe("Columns controls", () => {
    test.beforeEach(async ({ stories, page, monaco }) => {
      await stories.openRelation();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
      await page.getByRole("columnheader", { name: "column-1 (<Undefined>)" }).click({
        button: "right",
      });
    });

    test("shouldn't render row context menu", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await expect(page.getByRole("heading", { name: "COLUMNS" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "ROWS" })).not.toBeAttached();
    });

    test("should open column context menu and insert column right", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert right" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
    });

    test("should open column context menu and insert column left", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert left" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");
    });

    test("should open column context menu and insert multiples columns on right", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.AFFECTED_BY,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(5);
    });

    test("should open column context menu and insert multiples columns on left", async ({ page }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/420");
      test.info().annotations.push({
        type: TestAnnotations.AFFECTED_BY,
        description: "https://github.com/apache/incubator-kie-issues/issues/420",
      });

      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Left").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(3);
    });

    test("should open column context menu and delete column", async ({ page }) => {
      await page.getByRole("menuitem", { name: "Insert left" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");

      await page.getByTestId("expression-container").getByText("column-2").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(2);
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
    test.beforeEach(async ({ stories, page, monaco }) => {
      await stories.openRelation();
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
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await expect(page.getByRole("row", { name: "2" })).toBeAttached();
      });
    });

    test.describe("Add columns", () => {
      test("should add column left by positioning mouse on the header cell left section", async ({ page }) => {
        await page.getByRole("columnheader", { name: "column-1 (<Undefined>)" }).hover({ position: { x: 0, y: 0 } });
        await page.getByRole("row", { name: "column-1 (<Undefined>)" }).locator("svg").click();
        await expect(page.getByRole("columnheader", { name: "column-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(2)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(3);
      });

      test("should add column right by positioning mouse on the header cell right section", async ({ page }) => {
        await page.getByRole("columnheader", { name: "column-1 (<Undefined>)" }).hover();
        await page.getByRole("row", { name: "column-1 (<Undefined>)" }).locator("svg").click();
        await expect(page.getByRole("columnheader", { name: "column-2 (<Undefined>)" })).toBeAttached();
        await expect(page.getByRole("cell").nth(1)).toContainText("test");
        await expect(page.getByRole("cell")).toHaveCount(3);
      });
    });
  });
});

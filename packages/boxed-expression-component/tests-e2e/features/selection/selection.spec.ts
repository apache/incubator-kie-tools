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

import { expect, test } from "../../__fixtures__/base";

test.describe("Selection", () => {
  test.describe("Cell navigation", () => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/543");
    test("should correctly navigate", async () => {
      // enter, shift+enter, tab, shift+tab, escape
    });
  });

  test.describe("Expression navigation", () => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/543");
    test("should correctly navigatee", async () => {
      // enter, shift+enter, tab, shift+tab, escape
    });
  });

  test.describe("Cell selection", () => {
    test("should select multiple cells and write on them", async ({ stories, page }) => {
      await stories.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).dragTo(page.getByTestId("monaco-container").nth(10));

      // Write on cells
      for (let i = 0; i < 4; i++) {
        await page.keyboard.type(`"test${i}"`);
        await page.keyboard.press("Space");
        await page.keyboard.press("Tab");
      }

      await expect(page.getByRole("cell", { name: "test0" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test1" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test2" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test3" })).toBeAttached();
    });

    test("should select multiple cells and write on more of them", async ({ stories, page }) => {
      await stories.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).dragTo(page.getByTestId("monaco-container").nth(10));

      // Write on cells
      for (let i = 0; i < 9; i++) {
        await page.keyboard.type(`"test${i}"`);
        await page.keyboard.press("Space");
        await page.keyboard.press("Tab");
      }

      await expect(page.getByRole("cell", { name: "test8" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test5" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test6" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test7" })).toBeAttached();
    });

    test("should select multiple cells and copy/paste", async ({ stories, page, clipboard, browserName }) => {
      test.skip(
        browserName === "webkit",
        "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
      );

      await stories.openRelation("people");
      await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(5));
      await clipboard.copy();
      await page.keyboard.press("Delete");

      // Check if cells are empty
      for (let i = 1; i < 3; i++) {
        await expect(page.getByRole("row", { name: `${i}`, exact: true })).toContainText("");
      }

      await stories.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).click();
      await clipboard.paste();
      await expect(page.getByRole("cell", { name: "Luiz" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "30" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Brazil" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Tiago" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "29" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "USA" })).toBeAttached();
    });

    test("should select multiple cells, cut and paste on another expression", async ({
      stories,
      page,
      clipboard,
      browserName,
    }) => {
      test.skip(
        browserName === "webkit",
        "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
      );

      await stories.openRelation("people");
      await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(5));
      await clipboard.cut();

      // Check if cells are empty
      for (let i = 1; i < 3; i++) {
        await expect(page.getByRole("row", { name: `${i}`, exact: true })).toContainText("");
      }

      await stories.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).click();
      await clipboard.paste();
      await expect(page.getByRole("cell", { name: "Luiz" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "30" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Brazil" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Tiago" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "29" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "USA" })).toBeAttached();
    });
  });

  test.describe("Filter Expression", () => {
    test("should correctly copy and paste a filter expression from context menu", async ({
      bee,
      browserName,
      context,
      stories,
    }) => {
      test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/1394");
      test.skip(
        browserName === "webkit",
        "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
      );

      await context.grantPermissions(["clipboard-read", "clipboard-write"]);

      await stories.openBoxedFilter("rebooked-flights");
      await bee.expression.header.copy();
      await bee.expression.header.reset();
      await bee.selectExpressionMenu.selectContext();
      await bee.expression.asContext().entry(0).expression.contextMenu.open();
      await bee.expression.asContext().entry(0).expression.contextMenu.option("Paste").click();

      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-copied-and-pasted-as-nested.png");
    });
  });
});

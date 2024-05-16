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

import _ from "lodash";
import { test, expect } from "../../__fixtures__/base";

test.describe("Populate Boxed Filter", () => {
  test("should rename a filter", async ({ page, stories }) => {
    await stories.openBoxedFilter("base");

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Filter Expression Name");
    await page.keyboard.press("Enter");

    await expect(page.getByRole("columnheader", { name: "Filter Expression Name (<Undefined>)" })).toBeVisible();
  });

  test("should change a filter data type", async ({ page, stories }) => {
    await stories.openBoxedFilter("base");

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await expect(page.getByRole("columnheader", { name: "Expression Name (boolean)" })).toBeVisible();
  });

  test("should correctly create a Rebooked Flights filter", async ({ stories, page, boxedExpressionEditor }) => {
    await stories.openBoxedFilter("base");
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Rebooked Flights");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "Any" }).click();
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.fillFilter({
      collectionIn: "Passengers",
      collectionMatch: "item.Flight Number = Flight.Flight Number",
    });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-rebooked-flights.png");
  });

  test("should correctly create a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("base");

    await boxedExpressionEditor.resetFilter();
    await boxedExpressionEditor.selectBoxedContext();
    await boxedExpressionEditor.selectBoxedFilter(page.getByText("Select expression").first());

    await boxedExpressionEditor.fillFilter({
      collectionIn: "collection in expression",
      collectionMatch: "collection match expression",
    });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should correctly create a filter using list boxed expression", async ({
    boxedExpressionEditor,
    page,
    stories,
  }) => {
    await stories.openBoxedFilter("base");
    await page.getByText("=").first().click({ button: "right" });
    await page.getByRole("menuitem").getByText("Reset").click();
    await boxedExpressionEditor.selectBoxedList(page.getByText("Select expression").first());

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-boxed-list.png");
  });

  test("should correctly reset a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("nested");

    await boxedExpressionEditor.resetFilter();

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-reset.png");
  });

  test("should correctly copy and paste a filter - context menu", async ({
    boxedExpressionEditor,
    browserName,
    context,
    page,
    stories,
  }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    await context.grantPermissions(["clipboard-read", "clipboard-write"]);

    await stories.openBoxedFilter("rebooked-flights");
    await boxedExpressionEditor.copyFilter();
    await boxedExpressionEditor.resetFilter();
    await boxedExpressionEditor.selectBoxedContext(page.getByText("Select expression").first());
    await boxedExpressionEditor.pasteToUndefinedCell(page.getByText("Select expression").first());

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-copied-and-pasted-as-nested.png");
  });
});

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

test.describe("Create Boxed Filter", () => {
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

  test("should correctly reset a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("nested");

    await boxedExpressionEditor.resetFilter(page.locator("[data-ouia-component-id=expression-row-0]"));

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-reset.png");
  });

  test("should correctly create a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("base");

    await boxedExpressionEditor.resetFilter();
    await boxedExpressionEditor.selectBoxedContext();

    // Prepare empty Filter 'in' and 'match' cells
    await boxedExpressionEditor.selectBoxedFilter(page.getByText("Select expression").first());
    await boxedExpressionEditor.selectBoxedLiteral(page.getByText("Select expression").first());
    await boxedExpressionEditor.selectBoxedLiteral(page.getByText("Select expression").first());

    await boxedExpressionEditor.fillFilter({
      collectionIn: ["collection in expression"],
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
    await boxedExpressionEditor.selectBoxedList(page.getByText("Select expression").first());
    // 'in'
    await boxedExpressionEditor.selectBoxedLiteral(page.getByText("Select expression").first());
    await page.getByText("1").first().click({ button: "right" });
    await page.getByRole("menuitem").getByText("Insert below").click();
    await boxedExpressionEditor.selectBoxedLiteral(page.getByText("Select expression").first());
    // 'match'
    await boxedExpressionEditor.selectBoxedLiteral(page.getByText("Select expression").first());

    await boxedExpressionEditor.fillFilter({
      collectionIn: ["Passenger One", "Passenger Two"],
      collectionMatch: "item.Flight Number = Flight.Flight Number",
    });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-boxed-list.png");
  });
});

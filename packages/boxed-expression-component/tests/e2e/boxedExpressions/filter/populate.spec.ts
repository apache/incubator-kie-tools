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

    await page.getByTestId("kie-tools--boxed-expression-component---filter-collection-in").dblclick();
    await page.keyboard.type("Passengers");
    await page.keyboard.press("Enter");

    await page.getByTestId("kie-tools--boxed-expression-component---filter-collection-match").dblclick();
    await page.keyboard.type("item.Flight Number = Flight.Flight Number");
    await page.keyboard.press("Escape"); // monaco suggestion is shown
    await page.keyboard.press("Enter");

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-rebooked-flights.png");
  });

  test("should correctly create a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("base");

    await page.getByRole("button").getByText("Filter").click();
    await page.getByRole("menuitem").getByText("Reset").click();
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem").getByText("Context").click();
    await page.getByText("Select expression").first().click();
    await page.getByRole("menuitem").getByText("Filter").click();

    await page.getByTestId("kie-tools--boxed-expression-component---filter-collection-in").dblclick();
    await page.keyboard.type("collection in expression");
    await page.keyboard.press("Escape"); // monaco suggestion is shown
    await page.keyboard.press("Enter");

    await page.getByTestId("kie-tools--boxed-expression-component---filter-collection-match").dblclick();
    await page.keyboard.type("collection match expression");
    await page.keyboard.press("Escape"); // monaco suggestion is shown
    await page.keyboard.press("Enter");

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should correctly resize a filter", async ({ boxedExpressionEditor, page, resizing, stories }) => {
    await stories.openBoxedFilter("base");

    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }),
      { x: 0, y: 0 },
      { x: 80, y: 0 }
    );

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-resized.png");
  });

  test("should correctly resize a nested filter", async ({ boxedExpressionEditor, page, resizing, stories }) => {
    await stories.openBoxedFilter("nested");

    await resizing.resizeCell(
      page.getByTestId("kie-tools--boxed-expression-component---filter-collection-match"),
      { x: 0, y: 0 },
      { x: 80, y: 0 }
    );

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-resized.png");
  });

  test("should correctly create a filter using list boxed expression", async ({
    boxedExpressionEditor,
    page,
    stories,
  }) => {
    await stories.openBoxedFilter("base");
    await page.getByText("=").first().click({ button: "right" });
    await page.getByRole("menuitem").getByText("Reset").click();
    await page.getByText("Select expression").first().click();
    await page.getByRole("menuitem").getByText("List").click();

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-boxed-list.png");
  });

  test("should correctly reset a nested filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("nested");

    await page.getByRole("button").getByText("Filter").click();
    await page.getByRole("menuitem").getByText("Reset").click();

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested-reset.png");
  });

  test.skip("should correctly paste a filter", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedFilter("rebooked-flights");
    await page.getByRole("button").getByText("Filter").click();
    await page.getByRole("menuitem").getByText("Copy").click();
    await page.getByRole("button").getByText("Filter").click();
    await page.getByRole("menuitem").getByText("Reset").click();
    await page.getByText("Select expression").first().click();
    await page.getByRole("menuitem").getByText("Context").click();
    await page.getByText("Select expression").first().click();
    await page.getByRole("menuitem").getByText("Paste").click();

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-copied-and-pasted-as-nested.png");
  });
});

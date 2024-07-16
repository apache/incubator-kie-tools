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

  test("should correctly reset a nested filter", async ({ bee, stories }) => {
    await stories.openBoxedFilter("nested");

    await bee.expression.asContext().entry(0).expression.header.reset();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested-reset.png");
  });

  test("should correctly create a nested filter", async ({ bee, stories }) => {
    await bee.goto();

    await bee.selectExpressionMenu.selectContext();

    // Prepare empty Filter 'in' and 'match' cells
    await bee.expression.asContext().entry(0).selectExpressionMenu.selectFilter();
    await bee.expression.asContext().entry(0).expression.asFilter().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asFilter().match.selectExpressionMenu.selectLiteral();
    await bee.expression
      .asContext()
      .entry(0)
      .expression.asFilter()
      .in.expression.asLiteral()
      .fill("collection in expression");
    await bee.expression
      .asContext()
      .entry(0)
      .expression.asFilter()
      .match.expression.asLiteral()
      .fill("collection match expression");

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should correctly create a filter using list boxed expression", async ({ bee, stories }) => {
    await stories.openBoxedFilter("base");
    await bee.expression.asFilter().in.selectExpressionMenu.selectList();
    await bee.expression.asFilter().in.expression.asList().addEntryAtTop();
    await bee.expression.asFilter().in.expression.asList().row(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().in.expression.asList().row(1).selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().in.expression.asList().row(0).expression.asLiteral().fill("Passenger One");
    await bee.expression.asFilter().in.expression.asList().row(1).expression.asLiteral().fill("Passenger Two");

    await bee.expression.asFilter().match.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().match.expression.asLiteral().fill("item.Flight Number = Flight.Flight Number");

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested-boxed-list.png");
  });
});

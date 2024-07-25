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
import { TestAnnotations } from "../../../../playwright-base/annotations";

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
    const contextExpression = bee.expression.asContext();

    // Prepare empty Filter 'in' and 'match' cells
    await contextExpression.entry(0).selectExpressionMenu.selectFilter();
    await contextExpression.entry(0).expression.asFilter().in.selectExpressionMenu.selectLiteral();
    await contextExpression.entry(0).expression.asFilter().match.selectExpressionMenu.selectLiteral();
    await contextExpression.entry(0).expression.asFilter().in.expression.asLiteral().fill("collection in expression");
    await contextExpression
      .entry(0)
      .expression.asFilter()
      .match.expression.asLiteral()
      .fill("collection match expression");

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should correctly create a filter using list boxed expression", async ({ bee, stories }) => {
    await stories.openBoxedFilter("base");
    const filterExpression = bee.expression.asFilter();
    await filterExpression.in.selectExpressionMenu.selectList();
    await filterExpression.in.expression.asList().addEntryAtTop();
    await filterExpression.in.expression.asList().row(0).selectExpressionMenu.selectLiteral();
    await filterExpression.in.expression.asList().row(1).selectExpressionMenu.selectLiteral();
    await filterExpression.in.expression.asList().row(0).expression.asLiteral().fill("Passenger One");
    await filterExpression.in.expression.asList().row(1).expression.asLiteral().fill("Passenger Two");
    await filterExpression.match.selectExpressionMenu.selectLiteral();
    await filterExpression.match.expression.asLiteral().fill("item.Flight Number = Flight.Flight Number");

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested-boxed-list.png");
  });

  test("should keep IDs after resetting entries", async ({ bee, jsonModel, stories }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1182",
    });

    await stories.openBoxedFilter("base");

    const filterExpression = bee.expression.asFilter();
    await filterExpression.in.selectExpressionMenu.selectLiteral();
    await filterExpression.match.selectExpressionMenu.selectLiteral();
    await filterExpression.in.expression.asLiteral().fill("test");
    await filterExpression.match.expression.asLiteral().fill("test");
    await filterExpression.in.contextMenu.open();
    await filterExpression.in.contextMenu.option("Reset").nth(0).click();
    await filterExpression.match.contextMenu.open();
    await filterExpression.match.contextMenu.option("Reset").nth(0).click();

    expect((await jsonModel.getFilterExpression()).in).not.toBeUndefined();
    expect((await jsonModel.getFilterExpression()).match).not.toBeUndefined();
    expect((await jsonModel.getFilterExpression()).in["@_id"]).not.toBeUndefined();
    expect((await jsonModel.getFilterExpression()).match["@_id"]).not.toBeUndefined();
  });
});

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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";
import { CloseOption } from "../../api/nameAndDataTypeCell";

test.describe("Create Boxed Filter", () => {
  test("should rename a filter", async ({ bee, page, stories }) => {
    await stories.openBoxedFilter("base");

    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression
      .asFilter()
      .expressionHeaderCell.setName({ name: "Filter Expression Name", close: CloseOption.PRESS_ENTER });

    await expect(page.getByRole("columnheader", { name: "Filter Expression Name (<Undefined>)" })).toBeVisible();
  });

  test("should change a filter data type", async ({ bee, page, stories }) => {
    await stories.openBoxedFilter("base");

    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression
      .asFilter()
      .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

    await expect(page.getByRole("columnheader", { name: "Expression Name (boolean)" })).toBeVisible();
  });

  test("should correctly reset a nested filter", async ({ bee, stories }) => {
    await stories.openBoxedFilter("nested");

    await bee.expression.asContext().entry(0).expression.header.reset();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested-reset.png");
  });

  test("should correctly create a nested filter", async ({ bee }) => {
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

  test("should have IDs after resetting 'in' and 'match' and setting it again", async ({ bee, jsonModel, stories }) => {
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

  test.describe("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell", () => {
    // We're testing the expressions known to be wider than the default Filter Expression width, not all expressions
    test.beforeEach(async ({ stories, browserName, clipboard }) => {
      await stories.openBoxedFilter("base");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Context Expression", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectContext();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-context.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Decision Table", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectDecisionTable();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-decision-table.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - List", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectList();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-list.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Invocation", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectInvocation();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-invocation.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Function", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectFunction();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-function.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Conditional", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectConditional();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-conditional.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - For", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectFor();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-for.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Every", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectEvery();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-every.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Some", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectSome();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-some.png");
    });

    test("should correctly fill the 'in' cell when the 'match' cell is larger than the 'in' cell - Filter", async ({
      bee,
    }) => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1147",
      });
      const filterExpression = bee.expression.asFilter();
      await filterExpression.match.selectExpressionMenu.selectFilter();
      // We're hovering the 'in' to make the width difference to the 'match' more clear
      await filterExpression.in.hover();
      await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-aligned-match-filter.png");
    });
  });
});

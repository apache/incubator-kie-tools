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
import { CloseOption } from "../../api/nameAndDataTypeCell";

test.describe("Populate Boxed Conditional", () => {
  test("should rename a Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression
      .asConditional()
      .expressionHeaderCell.setName({ name: "Conditional Expression Name", close: CloseOption.PRESS_ENTER });

    expect(
      await bee.expression.asConditional().expressionHeaderCell.content.locator(".header-cell-info").textContent()
    ).toEqual("Conditional Expression Name(<Undefined>)");
  });

  test("should change a Conditional data type", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression
      .asConditional()
      .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

    await expect(await bee.expression.asConditional().expressionHeaderCell.getPopoverMenu()).not.toBeAttached();

    expect(await bee.expression.asConditional().expressionHeaderCell.content.textContent()).toEqual(
      "Expression Name(boolean)"
    );
  });

  test("should create the Monthly Fee Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    // HEADER
    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression.asConditional().expressionHeaderCell.setName({ name: "MonthlyFee" });
    await bee.expression
      .asConditional()
      .expressionHeaderCell.setDataType({ dataType: "number", close: CloseOption.PRESS_ENTER });

    // IF
    await bee.expression.asConditional().if.expression.asLiteral().fill('ProdctType = "STANDARD LOAN"');

    // THEN
    await bee.expression.asConditional().then.expression.asLiteral().fill("20");

    // ELSE
    await bee.expression.asConditional().else.expression.asLiteral().equalsSignContextMenu.open();
    await bee.expression
      .asConditional()
      .else.expression.asLiteral()
      .equalsSignContextMenu.option("Reset")
      .nth(0)
      .click();
    await bee.expression.asConditional().else.selectExpressionMenu.selectConditional();
    await bee.expression.asConditional().else.expression.asConditional().if.selectExpressionMenu.selectLiteral();
    await bee.expression
      .asConditional()
      .else.expression.asConditional()
      .if.expression.asLiteral()
      .fill('ProdctType = "SPECIAL OFFERING"');
    await bee.expression.asConditional().else.expression.asConditional().then.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().else.expression.asConditional().then.expression.asLiteral().fill("25");
    await bee.expression.asConditional().else.expression.asConditional().else.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().else.expression.asConditional().else.expression.asLiteral().fill("null");

    await expect(bee.getContainer()).toHaveScreenshot("boxed-conditional-monthly-fee.png");
  });

  test("should create a nested Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.header.reset();
    await bee.selectExpressionMenu.selectContext();
    await bee.expression.asContext().entry(0).selectExpressionMenu.selectConditional();

    await bee.expression.asContext().entry(0).expression.asConditional().if.selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asConditional().if.expression.asLiteral().fill("1 > 0");

    await bee.expression.asContext().entry(0).expression.asConditional().then.selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asConditional().then.expression.asLiteral().fill("always");

    await bee.expression.asContext().entry(0).expression.asConditional().else.selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asConditional().else.expression.asLiteral().fill("never");

    await expect(bee.getContainer()).toHaveScreenshot("nested-boxed-conditional.png");
  });

  test("should combine other expressions with the Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.asConditional().if.expression.asLiteral().equalsSignContextMenu.open();
    await bee.expression.asConditional().if.expression.asLiteral().equalsSignContextMenu.option("Reset").nth(0).click();
    await bee.expression.asConditional().if.selectExpressionMenu.selectContext();

    await bee.expression.asConditional().then.expression.asLiteral().equalsSignContextMenu.open();
    await bee.expression
      .asConditional()
      .then.expression.asLiteral()
      .equalsSignContextMenu.option("Reset")
      .nth(0)
      .click();
    await bee.expression.asConditional().then.selectExpressionMenu.selectDecisionTable();

    await bee.expression.asConditional().else.expression.asLiteral().equalsSignContextMenu.open();
    await bee.expression
      .asConditional()
      .else.expression.asLiteral()
      .equalsSignContextMenu.option("Reset")
      .nth(0)
      .click();
    await bee.expression.asConditional().else.selectExpressionMenu.selectSome();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-conditional-with-nested-other-expressions.png");
  });

  test("should reset a Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.header.reset();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-conditional--reset-state.png");
  });

  test("should reset a nested Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional("nested");

    await bee.expression.asContext().entry(0).expression.header.reset();

    await expect(bee.getContainer()).toHaveScreenshot("nested-boxed-conditional--reset-state.png");
  });

  test("should copy and paste Conditional", async ({ bee, browserName, context, stories }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    await context.grantPermissions(["clipboard-read", "clipboard-write"]);

    await stories.openBoxedConditional();

    // Copy the root of the expression
    await bee.expression.header.copy();

    // Paste it recursively as 'if' expression
    await bee.expression.asConditional().if.expression.asLiteral().equalsSignContextMenu.open();
    await bee.expression.asConditional().if.expression.asLiteral().equalsSignContextMenu.option("Paste").nth(0).click();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-conditional-copy-and-paste-recursively.png");
  });
});

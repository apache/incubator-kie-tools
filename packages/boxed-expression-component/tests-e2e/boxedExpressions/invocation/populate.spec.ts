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
import { CloseOption } from "../../api/nameAndDataTypeCell";

test.describe("Populate Boxed Invocation", () => {
  test("should correctly create pre-bureau-affordability boxed invocation", async ({
    stories,
    page,
    bee,
    resizing,
    monaco,
  }) => {
    await stories.openBoxedInvocation();

    await bee.expression.asInvocation().expressionHeaderCell.open();
    await bee.expression.asInvocation().expressionHeaderCell.setName({ name: "Installment calculation" });
    await bee.expression
      .asInvocation()
      .expressionHeaderCell.setDataType({ dataType: "number", close: CloseOption.PRESS_ENTER });

    await bee.expression.asInvocation().addParameterBelowOfEntryAtIndex(0);
    await bee.expression.asInvocation().addParameterBelowOfEntryAtIndex(0);
    await bee.expression.asInvocation().addParameterBelowOfEntryAtIndex(0);
    await bee.expression.asInvocation().addParameterBelowOfEntryAtIndex(0);

    await bee.expression.asInvocation().parameter(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(0).descriptionCell.open();
    await bee.expression.asInvocation().parameter(0).descriptionCell.setName({ name: "Monthly income" });
    await bee.expression.asInvocation().parameter(0).descriptionCell.setDataType({ dataType: "number", commit: true });

    await bee.expression.asInvocation().parameter(1).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(1).descriptionCell.open();
    await bee.expression.asInvocation().parameter(1).descriptionCell.setName({ name: "Monthly repayments" });
    await bee.expression.asInvocation().parameter(1).descriptionCell.setDataType({ dataType: "number", commit: true });

    await bee.expression.asInvocation().parameter(2).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(2).descriptionCell.open();
    await bee.expression.asInvocation().parameter(2).descriptionCell.setName({ name: "Monthly expenses" });
    await bee.expression.asInvocation().parameter(2).descriptionCell.setDataType({ dataType: "number", commit: true });

    await bee.expression.asInvocation().parameter(3).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(3).descriptionCell.open();
    await bee.expression.asInvocation().parameter(3).descriptionCell.setName({ name: "Risk category" });
    await bee.expression.asInvocation().parameter(3).descriptionCell.setDataType({ dataType: "number", commit: true });

    await bee.expression.asInvocation().parameter(4).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(4).descriptionCell.open();
    await bee.expression.asInvocation().parameter(4).descriptionCell.setName({ name: "Required monthly installment" });
    await bee.expression.asInvocation().parameter(4).descriptionCell.setDataType({ dataType: "number", commit: true });

    await resizing.reset(page.getByRole("cell", { name: "Required monthly installment" }));

    await bee.expression.asInvocation().invokedFunctionNameCell.click();
    await page.keyboard.type(`Affordability calculation`);
    await page.keyboard.press("Enter");

    await monaco.fill({ monacoParentLocator: page, nth: 0, content: "Aplicant data.Monthly.Income" });
    await monaco.fill({ monacoParentLocator: page, nth: 1, content: "Aplicant data.Monthly.Repayments" });
    await monaco.fill({ monacoParentLocator: page, nth: 2, content: "Aplicant data.Monthly.Expenses" });
    await monaco.fill({ monacoParentLocator: page, nth: 3, content: "Pre-bureau risk category" });
    await monaco.fill({ monacoParentLocator: page, nth: 4, content: "Required monthly installment" });

    await resizing.resizeCell(
      page.getByRole("cell", { name: "= Aplicant data.Monthly.Income Aplicant data.Monthly.Income" }),
      { x: 0, y: 0 },
      { x: 100, y: 0 }
    );

    await expect(bee.getContainer()).toHaveScreenshot("boxed-invocation-affordability-calculation.png");
  });
});

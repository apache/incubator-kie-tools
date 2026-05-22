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

test.describe("Populate Boxed Function", () => {
  test("should correctly populate boxed function", async ({ stories, page, bee, resizing, monaco }) => {
    await stories.openBoxedFunction();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Affordability calculation");

    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await bee.expression.asFunction().addParameter({ name: "Monthly Income", dataType: "number" });
    await bee.expression.asFunction().addParameter({ name: "Monthly Repayments", dataType: "number" });
    await bee.expression.asFunction().addParameter({ name: "Monthly Expenses", dataType: "number" });
    await bee.expression.asFunction().addParameter({ name: "Risk Category", dataType: "string" });
    await bee.expression.asFunction().addParameter({ name: "Required Monthly Income", dataType: "number" });

    await bee.expression.asFunction().entry(0).selectExpressionMenu.selectContext();
    await bee.expression.asFunction().entry(0).expression.asContext().addEntryBelowOfEntryAtIndex(0);
    await bee.expression.asFunction().entry(0).expression.asContext().entry(0).variable.open();
    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(0)
      .variable.setName({ name: "Disposable income" });
    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(0)
      .variable.setDataType({ dataType: "number", commit: true });

    await bee.expression.asFunction().entry(0).expression.asContext().entry(1).variable.open();
    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(1)
      .variable.setName({ name: "Credit contingency factor" });
    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(1)
      .variable.setDataType({ dataType: "number", commit: true });

    await resizing.reset(page.getByRole("cell", { name: "Credit contingency factor (number)", exact: true }));

    await bee.expression.asFunction().entry(0).expression.asContext().entry(0).selectExpressionMenu.selectLiteral();
    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(1)
      .selectExpressionMenu.selectDecisionTable();
    await bee.expression.asFunction().entry(0).expression.asContext().result.selectExpressionMenu.selectLiteral();

    await monaco.fill({
      monacoParentLocator: page.getByRole("cell", { name: "=" }).nth(1),
      content: "Monthly Income - (Monthly Repayments + Monthly Expenses)",
    });

    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(1)
      .expression.asDecisionTable()
      .addRowAtBottomOfIndex(1);

    await bee.expression
      .asFunction()
      .entry(0)
      .expression.asContext()
      .entry(1)
      .expression.asDecisionTable()
      .addRowAtBottomOfIndex(1);

    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click();
    await monaco.fill({
      monacoParentLocator: page.getByTestId("kie-tools--bee--expression-popover-menu"),
      content: "Risk category",
      submit: true,
    });

    await bee.expression.asDecisionTable().fill({
      startAtCell: 1,
      tableData: [
        [`"High", "Decline"`, "0.6"],
        [`"Medium"`, "0.7"],
        [`"Low", "Very Low"`, "0.8"],
      ],
    });

    await monaco.fill({
      monacoParentLocator: page.getByRole("cell", { name: "=" }).nth(2),
      content: `if Disposable Income * Credit Contigency Factor > Required Monthly Installment
then true
else false`,
    });

    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Affordability calculation (boolean)" }),
      { x: 0, y: 0 },
      { x: 320, y: 0 }
    );

    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/536",
    });
    // Can't resize this column as it's named as "Expression Name"
    // await resizing.reset(
    //   page.getByRole("columnheader", { name: "Credit contingency factor (number)" }).nth(1)
    // );

    await expect(bee.getContainer()).toHaveScreenshot("boxed-function-affordability-calculation.png");
  });
});

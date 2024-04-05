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
  test("should correctly populate boxed function", async ({
    stories,
    page,
    boxedExpressionEditor,
    resizing,
    monaco,
  }) => {
    await stories.openBoxedFunction();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Affordability calculation");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await page.getByText("Edit parameters").click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter name").nth(0).click();
    await page.getByPlaceholder("Parameter name").nth(0).fill("Monthly Income");
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();

    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter name").nth(1).click();
    await page.getByPlaceholder("Parameter name").nth(1).fill("Monthly Repayments");
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();

    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter name").nth(2).click();
    await page.getByPlaceholder("Parameter name").nth(2).fill("Monthly Expenses");
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();

    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter name").nth(3).click();
    await page.getByPlaceholder("Parameter name").nth(3).fill("Risk Category");
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();

    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter name").nth(4).click();
    await page.getByPlaceholder("Parameter name").nth(4).fill("Required Monthly Income");
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Escape");

    await boxedExpressionEditor.selectBoxedContext(page.getByRole("row", { name: "1" }));
    await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)", exact: true }).hover();
    await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)", exact: true }).locator("svg").click();

    await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)", exact: true }).click();
    await page.getByPlaceholder("Expression Name").fill("Disposable income");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await page.getByRole("cell", { name: "ContextEntry-2 (<Undefined>)", exact: true }).click();
    await page.getByPlaceholder("Expression Name").fill("Credit contingency factor");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await resizing.reset(page.getByRole("cell", { name: "Credit contingency factor (number)", exact: true }));

    await boxedExpressionEditor.selectBoxedLiteral(
      page.getByRole("cell", { name: "Select expression", exact: true }).nth(0)
    );
    await boxedExpressionEditor.selectDecisionTable(
      page.getByRole("cell", { name: "Select expression", exact: true }).nth(0)
    );
    await boxedExpressionEditor.selectBoxedLiteral(
      page.getByRole("cell", { name: "Select expression", exact: true }).nth(0)
    );

    await monaco.fill({
      monacoParentLocator: page.getByRole("cell", { name: "=" }).nth(1),
      content: "Monthly Income - (Monthly Repayments + Monthly Expenses)",
    });

    await page.getByRole("cell", { name: "1", exact: true }).nth(1).hover();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();
    await page.getByRole("cell", { name: "1", exact: true }).locator("svg").click();

    await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Risk category");
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.fillDecisionTable({
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

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-function-affordability-calculation.png");
  });
});

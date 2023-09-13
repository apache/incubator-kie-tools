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

test.describe("Populate Boxed Invocation", () => {
  test("should correctly create pre-bureau-affordability boxed invocation", async ({
    stories,
    page,
    boxedExpressionEditor,
    resizing,
    monaco,
  }) => {
    await stories.openBoxedInvocation();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Installment calculation");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).hover();
    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).locator("svg").click();
    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).locator("svg").click();
    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).locator("svg").click();
    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).locator("svg").click();

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "p-1" }));
    await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Monthly income");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "p-5" }));
    await page.getByRole("cell", { name: "p-5 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Monthly repayments");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "p-4" }));
    await page.getByRole("cell", { name: "p-4 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Monthly expenses");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "p-3" }));
    await page.getByRole("cell", { name: "p-3 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Risk category");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "p-2" }));
    await page.getByRole("cell", { name: "p-2 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Required monthly installment");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await resizing.reset(page.getByRole("cell", { name: "Required monthly installment" }));

    await page.getByText("FUNCTION").click();
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

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot(
      "boxed-invocation-affordability-calculation.png"
    );
  });
});

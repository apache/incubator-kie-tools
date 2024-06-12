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

test.describe("Populate Boxed Conditional", () => {
  test("should rename a Conditional", async ({ page, stories }) => {
    await stories.openBoxedConditional();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Conditional Expression Name");
    await page.keyboard.press("Enter");

    await expect(page.getByRole("columnheader", { name: "Conditional Expression Name (<Undefined>)" })).toBeVisible();
  });

  test("should change a Conditional data type", async ({ page, stories }) => {
    await stories.openBoxedConditional();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await expect(page.getByRole("columnheader", { name: "Expression Name (boolean)" })).toBeVisible();
  });

  test("should create the Monthly Fee Conditional", async ({ stories }) => {
    await stories.openBoxedConditional();
  });

  test("should create a nested Conditional", async ({ boxedExpressionEditor, page, stories }) => {
    await stories.openBoxedConditional();

    await boxedExpressionEditor.resetConditional();
    await boxedExpressionEditor.selectBoxedContext();
    await boxedExpressionEditor.selectBoxedConditional(page.getByText("Select expression").first());

    await boxedExpressionEditor.fillConditional({
      from: page.getByTestId("kie-tools--boxed-expression-component---conditional"),
      ifExpr: "1 > 0",
      thenExpr: "always",
      elseExpr: "never",
    });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should combine other expressions with the Conditional", async ({ stories }) => {
    await stories.openBoxedConditional();
  });

  test("should reset a Conditional", async ({ boxedExpressionEditor, stories }) => {
    await stories.openBoxedConditional();

    await boxedExpressionEditor.resetConditional();
  });

  test("should reset a nested Conditional", async ({ boxedExpressionEditor, stories }) => {
    await stories.openBoxedConditional("nested");

    await boxedExpressionEditor.resetConditional();
  });

  test("should copy and paste Conditional", async ({ browserName, context, stories }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    await context.grantPermissions(["clipboard-read", "clipboard-write"]);

    await stories.openBoxedConditional();
  });
});

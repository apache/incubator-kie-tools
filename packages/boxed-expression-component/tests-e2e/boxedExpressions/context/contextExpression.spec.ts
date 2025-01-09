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

test.describe("Create Boxed Context", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedContext();
  });

  test("should render expression correctly", async ({ bee, page }) => {
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "<result>" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(2);
    await expect(page.getByRole("columnheader")).toHaveCount(1);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(bee.getContainer()).toHaveScreenshot("boxed-context.png");
  });

  test("should have IDs after resetting entries and setting it again", async ({ bee, jsonModel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1182",
    });

    const contextExpression = bee.expression.asContext();
    await contextExpression.result.selectExpressionMenu.selectLiteral();
    await contextExpression.result.expression.asLiteral().fill("test");
    await contextExpression.result.contextMenu.open();
    await contextExpression.result.contextMenu.option("Reset").nth(0).click();
    await contextExpression.result.selectExpressionMenu.selectLiteral();
    await contextExpression.result.expression.asLiteral().fill("test2");

    await contextExpression.entry(0).selectExpressionMenu.selectLiteral();
    await contextExpression.entry(0).expression.asLiteral().fill("test");
    await contextExpression.entry(0).variable.contextMenu.open();
    await contextExpression.entry(0).variable.contextMenu.option("Reset").nth(0).click();
    await contextExpression.entry(0).selectExpressionMenu.selectLiteral();
    await contextExpression.entry(0).expression.asLiteral().fill("test2");

    expect((await jsonModel.getContextExpression()).contextEntry).not.toBeUndefined();
    expect((await jsonModel.getContextExpression()).contextEntry?.[0]["@_id"]).not.toBeUndefined();
    expect((await jsonModel.getContextExpression()).contextEntry?.[1]["@_id"]).not.toBeUndefined();
  });
});

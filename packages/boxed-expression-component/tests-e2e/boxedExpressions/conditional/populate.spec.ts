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
  test("should rename a Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression
      .asConditional()
      .expressionHeaderCell.setName({ name: "Conditional Expression Name", close: true });

    expect(await bee.expression.asConditional().expressionHeaderCell.content.textContent()).toEqual(
      "Conditional Expression Name(<Undefined>)"
    );
  });

  test("should change a Conditional data type", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression.asConditional().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asConditional().expressionHeaderCell.content.textContent()).toEqual(
      "Expression Name(boolean)"
    );
  });

  test("should create the Monthly Fee Conditional", async ({ stories }) => {
    await stories.openBoxedConditional();
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

    await expect(bee.getContainer()).toHaveScreenshot("boxed-filter-nested.png");
  });

  test("should combine other expressions with the Conditional", async ({ stories }) => {
    await stories.openBoxedConditional();
  });

  test("should reset a Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional();

    await bee.expression.header.reset();
  });

  test("should reset a nested Conditional", async ({ bee, stories }) => {
    await stories.openBoxedConditional("nested");

    await bee.expression.asContext().entry(0).expression.header.reset();
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

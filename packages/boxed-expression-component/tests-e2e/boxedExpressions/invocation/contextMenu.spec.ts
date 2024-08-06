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

test.describe("Boxed Invocation context menu", () => {
  test.describe("Parameters control", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedInvocation();
    });

    // There is no reason for not to render the "selection" here.
    test("should render selection context menu", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await expect(invocationExpression.parameter(0).descriptionCell.contextMenu.heading("PARAMETERS")).toBeAttached();
      await expect(invocationExpression.parameter(0).descriptionCell.contextMenu.heading("SELECTION")).toBeAttached();
    });

    test("shouldn't render parameters context menu", async ({ page, bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.expressionHeaderCell.contextMenu.open();

      await expect(invocationExpression.expressionHeaderCell.contextMenu.heading("PARAMETERS")).not.toBeAttached();
      await expect(invocationExpression.expressionHeaderCell.contextMenu.heading("SELECTION")).toBeAttached();
      await expect(invocationExpression.expressionHeaderCell.contextMenu.heading("COLUMNS")).not.toBeAttached();

      await page.keyboard.press("Escape");

      await bee.expression.asInvocation().parameter(0).expression.contextMenu.open();

      await expect(
        bee.expression.asInvocation().parameter(0).expression.contextMenu.heading("PARAMETERS")
      ).toBeAttached();
      await expect(
        bee.expression.asInvocation().parameter(0).expression.contextMenu.heading("SELECTION")
      ).toBeAttached();
      await expect(
        bee.expression.asInvocation().parameter(0).expression.contextMenu.heading("COLUMNS")
      ).not.toBeAttached();
      await page.keyboard.press("Escape");
    });

    test("should open parameters context menu and insert parameters above", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Insert above").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-2");
      await expect(invocationExpression.parameter(1).descriptionCell.content).toContainText("p-1");
    });

    test("should open parameters context menu and insert parameters below", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Insert below").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-1");
      await expect(invocationExpression.parameter(1).descriptionCell.content).toContainText("p-2");
    });

    test("should open parameters context menu and insert multiples parameters above", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Insert").click();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.button("plus").click();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.button("Insert").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-4");
      await expect(invocationExpression.parameter(1).descriptionCell.content).toContainText("p-3");
      await expect(invocationExpression.parameter(2).descriptionCell.content).toContainText("p-2");
      await expect(invocationExpression.parameter(3).descriptionCell.content).toContainText("p-1");
    });

    test("should open parameters context menu and insert multiples parameters below", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Insert").click();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.button("plus").click();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.radio("Below").click();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.button("Insert").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-1");
      await expect(invocationExpression.parameter(1).descriptionCell.content).toContainText("p-4");
      await expect(invocationExpression.parameter(2).descriptionCell.content).toContainText("p-3");
      await expect(invocationExpression.parameter(3).descriptionCell.content).toContainText("p-2");
    });

    test("should open parameters context menu and delete row", async ({ bee }) => {
      const invocationExpression = bee.expression.asInvocation();

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Insert above").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-2");
      await expect(invocationExpression.parameter(1).descriptionCell.content).toContainText("p-1");

      await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
      await invocationExpression.parameter(0).descriptionCell.contextMenu.option("Delete").click();

      await expect(invocationExpression.parameter(0).descriptionCell.content).toContainText("p-1");

      expect(await invocationExpression.parametersCount()).toEqual(1);
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({ stories, bee }) => {
    await stories.openBoxedInvocation();

    const invocationExpression = bee.expression.asInvocation();

    await invocationExpression.parameter(0).expression.contextMenu.open();
    await invocationExpression.parameter(0).expression.contextMenu.option("Insert").click();
    await invocationExpression.parameter(0).descriptionCell.contextMenu.open();
    await expect(invocationExpression.parameter(0).descriptionCell.contextMenu.heading("PARAMETERS")).toBeAttached();
    await expect(invocationExpression.parameter(0).descriptionCell.contextMenu.heading("SELECTION")).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedInvocation();
    });

    test.describe("Add parameters", () => {
      test("should add parameters above by positioning mouse on the index cell upper section", async ({ bee }) => {
        const invocationExpression = bee.expression.asInvocation();

        await invocationExpression.parameter(0).selectExpressionMenu.selectLiteral();
        await invocationExpression.parameter(0).expression.asLiteral().fill("test");
        await invocationExpression.addParameterAboveOfEntryAtIndex(0);

        await expect(invocationExpression.parameter(1).expression.asLiteral().content).toContainText("test");
      });

      test("should add parameters below by positioning mouse on the index cell lower section", async ({ bee }) => {
        const invocationExpression = bee.expression.asInvocation();

        await invocationExpression.parameter(0).selectExpressionMenu.selectLiteral();
        await invocationExpression.parameter(0).expression.asLiteral().fill("test");
        await invocationExpression.addParameterBelowOfEntryAtIndex(0);

        await expect(invocationExpression.parameter(0).expression.asLiteral().content).toContainText("test");
      });
    });
  });
});

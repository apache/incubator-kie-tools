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

test.describe("Boxed Context context menu", () => {
  test.describe("context entry control", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedContext();
    });

    test("shouldn't render selection context menu", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).selectExpressionMenu.selectLiteral();
      await contextExpression.entry(0).expression.asLiteral().fill("test");
      await contextExpression.entry(0).variable.contextMenu.open();

      await expect(contextExpression.entry(0).variable.contextMenu.heading("CONTEXT ENTRY")).toBeAttached();
      await expect(contextExpression.entry(0).variable.contextMenu.heading("SELECTION")).toBeAttached();
    });

    test("shouldn't render context entry context menu", async ({ page, bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).selectExpressionMenu.selectLiteral();
      await contextExpression.expressionHeaderCell.contextMenu.open();

      await expect(contextExpression.expressionHeaderCell.contextMenu.heading("CONTEXT ENTRY")).not.toBeAttached();
      await expect(contextExpression.expressionHeaderCell.contextMenu.heading("SELECTION")).toBeAttached();
      await expect(contextExpression.expressionHeaderCell.contextMenu.heading("COLUMNS")).not.toBeAttached();

      await page.keyboard.press("Escape");

      await contextExpression.entry(0).expression.asLiteral().cell.contextMenu.open();

      await expect(
        contextExpression.entry(0).expression.asLiteral().cell.contextMenu.heading("CONTEXT ENTRY")
      ).not.toBeAttached();
      await expect(
        contextExpression.entry(0).expression.asLiteral().cell.contextMenu.heading("SELECTION")
      ).toBeAttached();
      await expect(
        contextExpression.entry(0).expression.asLiteral().cell.contextMenu.heading("COLUMNS")
      ).not.toBeAttached();
    });

    test("should open context entry context menu and insert context entry above", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).variable.contextMenu.open();
      await contextExpression.entry(0).variable.contextMenu.option("Insert above").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-2");
      await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-1");
    });

    test("should open context entry context menu and insert context entry below", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await bee.expression.asContext().entry(0).variable.contextMenu.open();
      await bee.expression.asContext().entry(0).variable.contextMenu.option("Insert below").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-1");
      await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-2");
    });

    test("should open context entry context menu and insert multiples context entry above", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).variable.contextMenu.open();
      await contextExpression.entry(0).variable.contextMenu.option("Insert").click();
      await contextExpression.entry(0).variable.contextMenu.button("plus").click();
      await contextExpression.entry(0).variable.contextMenu.button("Insert").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-4");
      await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-3");
      await expect(contextExpression.entry(2).variable.content).toContainText("ContextEntry-2");
      await expect(contextExpression.entry(3).variable.content).toContainText("ContextEntry-1");
    });

    test("should open context entry context menu and insert multiples context entry below", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).variable.contextMenu.open();
      await contextExpression.entry(0).variable.contextMenu.option("Insert").click();
      await contextExpression.entry(0).variable.contextMenu.button("plus").click();
      await contextExpression.entry(0).variable.contextMenu.radio("Below").click();
      await contextExpression.entry(0).variable.contextMenu.button("Insert").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-1");
      await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-4");
      await expect(contextExpression.entry(2).variable.content).toContainText("ContextEntry-3");
      await expect(contextExpression.entry(3).variable.content).toContainText("ContextEntry-2");
    });

    test("should open context entry context menu and delete row", async ({ bee }) => {
      const contextExpression = bee.expression.asContext();

      await contextExpression.entry(0).variable.contextMenu.open();
      await contextExpression.entry(0).variable.contextMenu.option("Insert above").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-2");
      await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-1");

      await contextExpression.entry(0).variable.contextMenu.open();
      await contextExpression.entry(0).variable.contextMenu.option("Delete").click();

      await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-1");

      expect(await contextExpression.entriesCount()).toEqual(1);
    });
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedContext();
    });

    test.describe("Add context entry", () => {
      test("should add context entry above by positioning mouse on the index cell upper section", async ({ bee }) => {
        const contextExpression = bee.expression.asContext();
        await contextExpression.addEntryAboveOfEntryAtIndex(0);

        expect(await contextExpression.entriesCount()).toEqual(2);
        await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-1");
        await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-2");
      });

      test("should add context entry below by positioning mouse on the index cell lower section", async ({ bee }) => {
        const contextExpression = bee.expression.asContext();
        await contextExpression.addEntryBelowOfEntryAtIndex(0);

        expect(await contextExpression.entriesCount()).toEqual(2);

        await expect(contextExpression.entry(0).variable.content).toContainText("ContextEntry-1");
        await expect(contextExpression.entry(1).variable.content).toContainText("ContextEntry-2");
      });
    });
  });
});

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

test.describe("Readonly", () => {
  test("Literal expression", async ({ stories, bee, page }) => {
    await stories.openBoxedLiteral("readonly");
    const literalExpression = bee.expression.asLiteral();

    expect(await literalExpression.canFill()).toBeFalsy();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");

    await page.keyboard.press("Escape");
    await literalExpression.expressionHeaderCell.open();
    await expect(await literalExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Relation expression", async ({ stories, bee, page }) => {
    await stories.openRelation("readonly");
    const relationExpression = bee.expression.asRelation();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await relationExpression.expressionHeaderCell.open();
    await expect(await relationExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();

    // Can add rows?
    await relationExpression.cellAt({ column: 0, row: 1 }).content.hover();
    await expect(relationExpression.cellAt({ column: 0, row: 1 }).content.locator("svg")).not.toBeAttached();

    // Can add columns?
    await relationExpression.getColumnHeaderAtIndex(0).hover();
    await expect(relationExpression.getColumnHeaderAtIndex(0).locator("svg")).not.toBeAttached();
  });

  test("Context expression", async ({ stories, bee, page }) => {
    await stories.openBoxedContext("readonly");
    const contextExpression = bee.expression.asContext();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await contextExpression.expressionHeaderCell.open();
    await expect(await contextExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Decision Table expression", async ({ stories, bee, page }) => {
    await stories.openDecisionTable("readonly");
    const decisionTableExpression = bee.expression.asDecisionTable();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await decisionTableExpression.expressionHeaderCell.open();
    await expect(await decisionTableExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("List expression", async ({ stories, bee, page }) => {
    await stories.openBoxedList("readonly");
    const listExpression = bee.expression.asList();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await listExpression.expressionHeaderCell.open();
    await expect(await listExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Invocation expression", async ({ stories, bee, page }) => {
    await stories.openBoxedInvocation("readonly");
    const invocationExpression = bee.expression.asInvocation();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await invocationExpression.expressionHeaderCell.open();
    await expect(await invocationExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Conditional expression", async ({ stories, bee, page }) => {
    await stories.openBoxedConditional("readonly");
    const conditionalExpression = bee.expression.asConditional();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await conditionalExpression.expressionHeaderCell.open();
    await expect(await conditionalExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("For expression", async ({ stories, bee, page }) => {
    await stories.openBoxedFor("readonly");
    const forExpression = bee.expression.asFor();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await forExpression.expressionHeaderCell.open();
    await expect(await forExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Every expression", async ({ stories, bee, page }) => {
    await stories.openBoxedEvery("readonly");
    const everyExpression = bee.expression.asEvery();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await everyExpression.expressionHeaderCell.open();
    await expect(await everyExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Some expression", async ({ stories, bee, page }) => {
    await stories.openBoxedSome("readonly");
    const someExpression = bee.expression.asSome();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await someExpression.expressionHeaderCell.open();
    await expect(await someExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });

  test("Filter expression", async ({ stories, bee, page }) => {
    await stories.openBoxedFilter("readonly");
    const filterExpression = bee.expression.asFilter();

    await bee.expression.header.get().click();
    await expect(await bee.expression.header.availableOptions()).toHaveCount(1);
    await expect(await bee.expression.header.availableOptions()).toContainText("Copy");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Reset");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Cut");
    await expect(await bee.expression.header.availableOptions()).not.toContainText("Paste");
    await page.keyboard.press("Escape");
    await filterExpression.expressionHeaderCell.open();
    await expect(await filterExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();
  });
});

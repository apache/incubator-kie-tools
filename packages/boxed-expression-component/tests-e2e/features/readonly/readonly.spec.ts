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

test.describe("Read Only Mode", () => {
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

    // Can change name/data type?
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

    // Can change name/data type?
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

    // Can change name/data type?
    await contextExpression.expressionHeaderCell.open();
    await expect(await contextExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();

    // Can add entry?
    await contextExpression.entry(0).variable.content.hover();
    await expect(contextExpression.entry(0).variable.content.locator("svg")).not.toBeAttached();

    // Can change entry name/datatype?
    await contextExpression.entry(0).variable.open();
    await expect(await contextExpression.entry(0).getPopoverMenu()).not.toBeAttached();

    // Can change entry expression?
    expect(await contextExpression.entry(0).expression.asLiteral().canFill()).toBeFalsy();

    // Can reset, cut, paste in entry expression?
    await contextExpression.entry(0).expression.contextMenu.open();
    await expect(await contextExpression.entry(0).expression.contextMenu.availableOptions()).toHaveCount(1);
    await expect(await contextExpression.entry(0).expression.contextMenu.availableOptions()).toContainText("Copy");
    await expect(await contextExpression.entry(0).expression.contextMenu.availableOptions()).not.toContainText("Reset");
    await expect(await contextExpression.entry(0).expression.contextMenu.availableOptions()).not.toContainText("Cut");
    await expect(await contextExpression.entry(0).expression.contextMenu.availableOptions()).not.toContainText("Paste");
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

    // Hit table menu
    await decisionTableExpression.hitTableSelector.cell.click();
    await expect(await decisionTableExpression.hitTableSelector.menu.availableOptions()).not.toBeAttached();

    // Can add input?
    await decisionTableExpression.inputHeaderAt(0).content.hover();
    await expect(decisionTableExpression.inputHeaderAt(0).content.locator("svg")).not.toBeAttached();

    // Can add output?
    await decisionTableExpression.outputHeaderAt(0).content.hover();
    await expect(decisionTableExpression.outputHeaderAt(0).content.locator("svg")).not.toBeAttached();

    // Can add annotation?
    await decisionTableExpression.annotationHeaderAt(0).content.hover();
    await expect(decisionTableExpression.annotationHeaderAt(0).content.locator("svg")).not.toBeAttached();

    // Can change input?
    await decisionTableExpression.inputHeaderAt(0).open();
    await expect(await decisionTableExpression.inputHeaderAt(0).getPopoverMenu()).not.toBeAttached();

    // Can change output?
    await decisionTableExpression.outputHeaderAt(0).open();
    await expect(await decisionTableExpression.outputHeaderAt(0).getPopoverMenu()).not.toBeAttached();

    // Can change name/data type?
    await decisionTableExpression.expressionHeaderCell.open();
    await expect(await decisionTableExpression.expressionHeaderCell.getPopoverMenu()).not.toBeAttached();

    // Can add rows?
    await decisionTableExpression.cellAt({ row: 1, column: 1 }).content.hover();
    await expect(decisionTableExpression.cellAt({ row: 1, column: 1 }).content.locator("svg")).not.toBeAttached();
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

    // Can add rows?
    await listExpression.row(0).cell.content.hover();
    await expect(listExpression.row(0).cell.content.locator("svg")).not.toBeAttached();

    // Can change list expression?
    expect(await listExpression.row(0).expression.asLiteral().canFill()).toBeFalsy();
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

    // Can add rows?
    await invocationExpression.parameter(0).descriptionCell.content.hover();
    await expect(invocationExpression.parameter(0).descriptionCell.content.locator("svg")).not.toBeAttached();

    // Can change invocation name?
    await invocationExpression.invokedFunctionNameCell.click();
    await expect(invocationExpression.invokedFunctionNameCell.getByRole("textbox")).not.toBeAttached();

    // Can change list expression?
    expect(await invocationExpression.parameter(0).expression.asLiteral().canFill()).toBeFalsy();

    // Can reset, cut, paste in entry expression?
    await invocationExpression.parameter(0).expression.contextMenu.open();
    await expect(await invocationExpression.parameter(0).expression.contextMenu.availableOptions()).toHaveCount(1);
    await expect(await invocationExpression.parameter(0).expression.contextMenu.availableOptions()).toContainText(
      "Copy"
    );
    await expect(await invocationExpression.parameter(0).expression.contextMenu.availableOptions()).not.toContainText(
      "Reset"
    );
    await expect(await invocationExpression.parameter(0).expression.contextMenu.availableOptions()).not.toContainText(
      "Cut"
    );
    await expect(await invocationExpression.parameter(0).expression.contextMenu.availableOptions()).not.toContainText(
      "Paste"
    );
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

    expect(await conditionalExpression.if.expression.asLiteral().canFill()).toBeFalsy();
    expect(await conditionalExpression.then.expression.asLiteral().canFill()).toBeFalsy();
    expect(await conditionalExpression.else.expression.asLiteral().canFill()).toBeFalsy();
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

    await forExpression.variable.content.click();
    await expect(forExpression.variable.content.getByRole("textbox")).not.toBeAttached();
    expect(await forExpression.in.expression.asLiteral().canFill()).toBeFalsy();
    expect(await forExpression.return.expression.asLiteral().canFill()).toBeFalsy();
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

    await everyExpression.variable.content.click();
    await expect(everyExpression.variable.content.getByRole("textbox")).not.toBeAttached();
    expect(await everyExpression.in.expression.asLiteral().canFill()).toBeFalsy();
    expect(await everyExpression.satisfies.expression.asLiteral().canFill()).toBeFalsy();
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

    await someExpression.variable.content.click();
    await expect(someExpression.variable.content.getByRole("textbox")).not.toBeAttached();
    expect(await someExpression.in.expression.asLiteral().canFill()).toBeFalsy();
    expect(await someExpression.satisfies.expression.asLiteral().canFill()).toBeFalsy();
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

    expect(await filterExpression.in.expression.asLiteral().canFill()).toBeFalsy();
    expect(await filterExpression.match.expression.asLiteral().canFill()).toBeFalsy();
  });
});

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

import { expect, test } from "../__fixtures__/base";

test.describe("Copy, Cut and Paste expressions", () => {
  test.beforeEach(async ({ bee, clipboard }, testInfo) => {
    test.skip(
      testInfo.project.name === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );
    test.skip(testInfo.project.name === "Google Chrome", "https://github.com/apache/incubator-kie-issues/issues/1873");

    // This is required to enable Playwright to access browser Clipboard
    clipboard.use();

    await bee.goto();
  });

  test("should copy and paste top-level - Literal Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.asLiteral().fill("something else");
    await expect(bee.expression.asLiteral().content).toContainText("something else");
    await expect(bee.expression.asLiteral().content).not.toContainText("ORIGINAL");
    await bee.expression.header.paste();
    await expect(bee.expression.asLiteral().content).toContainText("ORIGINAL");
    await expect(bee.expression.asLiteral().content).not.toContainText("something else");
  });

  test("should cut and paste top-level - Literal Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should copy and paste top-level - Relation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectRelation();
    await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("something else");
    await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("something else");
    await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).not.toContainText("ORIGINAL");
    await bee.expression.header.paste();
    await expect(
      bee.expression.asRelation().cellAt({
        row: 1,
        column: 1,
      }).content
    ).not.toContainText("something else");
    await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("ORIGINAL");
  });

  test("should cut and paste top-level - Relation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectRelation();
    await bee.expression.asRelation().cellAt({ row: 1, column: 1 }).fill("ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asRelation().cellAt({ row: 1, column: 1 }).content).toContainText("ORIGINAL");
  });

  test("should copy and paste top-level - Context Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectContext();
    await bee.expression.asContext().entry(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asLiteral().fill("ORIGINAL");
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).toContainText("ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.asContext().entry(0).expression.asLiteral().fill("test");
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).toContainText("test");
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).not.toContainText("ORIGINAL");
    await bee.expression.header.paste();
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).not.toContainText("test");
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should cut and paste top-level - Context Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectContext();
    await bee.expression.asContext().entry(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asContext().entry(0).expression.asLiteral().fill("ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asContext().entry(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should copy and paste top-level - Decision Table", async ({ bee }) => {
    await bee.selectExpressionMenu.selectDecisionTable();
    await bee.expression.asDecisionTable().addInputAtStart();
    await bee.expression.asDecisionTable().addInputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).fill("1-1");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).fill("4-1");
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).fill("1-7");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 7 }).fill("4-7");
    await bee.expression.header.copy();
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).fill("top left");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).fill("bottom left");
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).fill("top right");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 7 }).fill("bottom right");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).content).toContainText("top left");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).content).toContainText("bottom left");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).content).toContainText("top right");
    await expect(
      bee.expression.asDecisionTable().cellAt({
        row: 4,
        column: 7,
      }).content
    ).toContainText("bottom right");
    await bee.expression.header.paste();
    await expect(
      bee.expression.asDecisionTable().cellAt({
        row: 1,
        column: 1,
      }).content
    ).not.toContainText("top left");
    await expect(
      bee.expression.asDecisionTable().cellAt({
        row: 4,
        column: 1,
      }).content
    ).not.toContainText("bottom left");
    await expect(
      bee.expression.asDecisionTable().cellAt({
        row: 1,
        column: 7,
      }).content
    ).not.toContainText("top right");
    await expect(
      bee.expression.asDecisionTable().cellAt({
        row: 4,
        column: 7,
      }).content
    ).not.toContainText("bottom right");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).content).toContainText("1-1");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).content).toContainText("4-1");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).content).toContainText("1-7");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 7 }).content).toContainText("4-7");
  });

  test("should cut and paste top-level - Decision Table", async ({ bee }) => {
    await bee.selectExpressionMenu.selectDecisionTable();
    await bee.expression.asDecisionTable().addInputAtStart();
    await bee.expression.asDecisionTable().addInputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().addRowAtTop();
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).fill("1-1");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).fill("4-1");
    await bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).fill("1-7");
    await bee.expression.asDecisionTable().cellAt({ row: 4, column: 7 }).fill("4-7");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 1 }).content).toContainText("1-1");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 1 }).content).toContainText("4-1");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 1, column: 7 }).content).toContainText("1-7");
    await expect(bee.expression.asDecisionTable().cellAt({ row: 4, column: 7 }).content).toContainText("4-7");
  });

  test("should copy and paste top-level - List Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectList();
    await bee.expression.asList().row(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asList().row(0).expression.asLiteral().fill("ORIGINAL");
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.asList().row(0).expression.asLiteral().fill("test");
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("test");
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).not.toContainText("ORIGINAL");
    await bee.expression.header.paste();
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).not.toContainText("test");
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should cut and paste top-level - List Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectList();
    await bee.expression.asList().row(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asList().row(0).expression.asLiteral().fill("ORIGINAL");
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asList().row(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should copy and paste top-level - Invocation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectInvocation();
    await bee.expression.asInvocation().parameter(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(0).expression.asLiteral().fill("ORIGINAL");
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).toContainText("ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.asInvocation().parameter(0).expression.asLiteral().fill("test");
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).toContainText("test");
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).not.toContainText(
      "ORIGINAL"
    );
    await bee.expression.header.paste();
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).not.toContainText("test");
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should cut and paste top-level - Invocation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectInvocation();
    await bee.expression.asInvocation().parameter(0).selectExpressionMenu.selectLiteral();
    await bee.expression.asInvocation().parameter(0).expression.asLiteral().fill("ORIGINAL");
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).toContainText("ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asInvocation().parameter(0).expression.asLiteral().content).toContainText("ORIGINAL");
  });

  test("should copy and paste top-level - Conditional Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectConditional();
    await bee.expression.asConditional().if.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().then.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().else.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().if.expression.asLiteral().fill("if ORIGINAL");
    await bee.expression.asConditional().then.expression.asLiteral().fill("then ORIGINAL");
    await bee.expression.asConditional().else.expression.asLiteral().fill("else ORIGINAL");
    await expect(bee.expression.asConditional().if.expression.asLiteral().content).toContainText("if ORIGINAL");
    await expect(bee.expression.asConditional().then.expression.asLiteral().content).toContainText("then ORIGINAL");
    await expect(bee.expression.asConditional().else.expression.asLiteral().content).toContainText("else ORIGINAL");
    await bee.expression.header.copy();
    await bee.expression.header.reset();
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("old");
    await expect(bee.expression.asLiteral().content).toContainText("old");
    await bee.expression.header.paste();
    await expect(bee.expression.asConditional().if.expression.asLiteral().content).toContainText("if ORIGINAL");
    await expect(bee.expression.asConditional().then.expression.asLiteral().content).toContainText("then ORIGINAL");
    await expect(bee.expression.asConditional().else.expression.asLiteral().content).toContainText("else ORIGINAL");
  });

  test("should cut and paste top-level - Conditional Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectConditional();
    await bee.expression.asConditional().if.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().then.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().else.selectExpressionMenu.selectLiteral();
    await bee.expression.asConditional().if.expression.asLiteral().fill("if ORIGINAL");
    await bee.expression.asConditional().then.expression.asLiteral().fill("then ORIGINAL");
    await bee.expression.asConditional().else.expression.asLiteral().fill("else ORIGINAL");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asConditional().if.expression.asLiteral().content).toContainText("if ORIGINAL");
    await expect(bee.expression.asConditional().then.expression.asLiteral().content).toContainText("then ORIGINAL");
    await expect(bee.expression.asConditional().else.expression.asLiteral().content).toContainText("else ORIGINAL");
  });

  test("should copy and paste top-level - For Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFor();
    const forExpression = bee.expression.asFor();
    await forExpression.in.selectExpressionMenu.selectLiteral();
    await forExpression.return.selectExpressionMenu.selectLiteral();
    const inExpression = forExpression.in.expression.asLiteral();
    const returnExpression = forExpression.return.expression.asLiteral();
    await forExpression.variable.fill("my variable");
    await inExpression.fill("ORIGINAL1");
    await returnExpression.fill("ORIGINAL2");
    await bee.expression.header.copy();
    await bee.expression.header.reset();
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("not");
    await expect(bee.expression.asLiteral().content).toContainText("not");
    await bee.expression.header.paste();
    await expect(inExpression.content).toContainText("ORIGINAL1");
    await expect(returnExpression.content).toContainText("ORIGINAL2");
    await expect(forExpression.variable.content).toContainText("my variable");
  });

  test("should cut and paste top-level - For Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFor();
    await bee.expression.asFor().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asFor().return.selectExpressionMenu.selectLiteral();
    await bee.expression.asFor().variable.fill("my variable");
    await bee.expression.asFor().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asFor().return.expression.asLiteral().fill("ORIGINAL2");

    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();

    await expect(bee.expression.asFor().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asFor().return.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asFor().variable.content).toContainText("my variable");
  });

  test("should copy and paste top-level - Every Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectEvery();
    await bee.expression.asEvery().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asEvery().satisfies.selectExpressionMenu.selectLiteral();
    await bee.expression.asEvery().variable.fill("my variable");
    await bee.expression.asEvery().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asEvery().satisfies.expression.asLiteral().fill("ORIGINAL2");
    await bee.expression.header.copy();
    await bee.expression.header.reset();

    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("not");
    await expect(bee.expression.asLiteral().content).toContainText("not");
    await bee.expression.header.paste();

    await expect(bee.expression.asEvery().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asEvery().satisfies.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asEvery().variable.content).toContainText("my variable");
  });

  test("should cut and paste top-level - Every Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectEvery();
    await bee.expression.asEvery().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asEvery().satisfies.selectExpressionMenu.selectLiteral();
    await bee.expression.asEvery().variable.fill("my variable");
    await bee.expression.asEvery().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asEvery().satisfies.expression.asLiteral().fill("ORIGINAL2");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asEvery().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asEvery().satisfies.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asEvery().variable.content).toContainText("my variable");
  });

  test("should copy and paste top-level - Some Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectSome();
    await bee.expression.asSome().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asSome().satisfies.selectExpressionMenu.selectLiteral();
    await bee.expression.asSome().variable.fill("my variable");
    await bee.expression.asSome().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asSome().satisfies.expression.asLiteral().fill("ORIGINAL2");

    await bee.expression.header.copy();
    await bee.expression.header.reset();
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("not");
    await expect(bee.expression.asLiteral().content).toContainText("not");
    await bee.expression.header.paste();

    await expect(bee.expression.asSome().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asSome().satisfies.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asSome().variable.content).toContainText("my variable");
  });

  test("should cut and paste top-level - Some Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectSome();
    await bee.expression.asSome().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asSome().satisfies.selectExpressionMenu.selectLiteral();
    await bee.expression.asSome().variable.fill("my variable");
    await bee.expression.asSome().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asSome().satisfies.expression.asLiteral().fill("ORIGINAL2");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asSome().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asSome().satisfies.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asSome().variable.content).toContainText("my variable");
  });

  test("should copy and paste top-level - Filter Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFilter();
    await bee.expression.asFilter().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().match.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asFilter().match.expression.asLiteral().fill("ORIGINAL2");
    await expect(bee.expression.asFilter().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asFilter().match.expression.asLiteral().content).toContainText("ORIGINAL2");
    await bee.expression.header.copy();
    await bee.expression.asFilter().in.expression.asLiteral().fill("new1");
    await bee.expression.asFilter().match.expression.asLiteral().fill("new2");
    await expect(bee.expression.asFilter().in.expression.asLiteral().content).toContainText("new1");
    await expect(bee.expression.asFilter().match.expression.asLiteral().content).toContainText("new2");
    await bee.expression.header.paste();
    await expect(bee.expression.asFilter().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asFilter().match.expression.asLiteral().content).toContainText("ORIGINAL2");
    await expect(bee.expression.asFilter().in.expression.asLiteral().content).not.toContainText("new1");
    await expect(bee.expression.asFilter().match.expression.asLiteral().content).not.toContainText("new2");
  });

  test("should cut and paste top-level - Filter Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFilter();
    await bee.expression.asFilter().in.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().match.selectExpressionMenu.selectLiteral();
    await bee.expression.asFilter().in.expression.asLiteral().fill("ORIGINAL1");
    await bee.expression.asFilter().match.expression.asLiteral().fill("ORIGINAL2");
    await bee.expression.header.cut();
    expect(await bee.expression.isEmpty()).toBeTruthy();
    await bee.selectExpressionMenu.paste();
    expect(await bee.expression.isEmpty()).toBeFalsy();
    await expect(bee.expression.asFilter().in.expression.asLiteral().content).toContainText("ORIGINAL1");
    await expect(bee.expression.asFilter().match.expression.asLiteral().content).toContainText("ORIGINAL2");
  });
});

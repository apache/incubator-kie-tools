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

import { beforeEach } from "node:test";
import { expect, test } from "../__fixtures__/base";
import { CloseOption } from "../api/nameAndDataTypeCell";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Change Boxed Expression Name and Data Type", () => {
  test.beforeEach(async ({ bee }) => {
    await bee.goto();
  });

  test.describe("Change Boxed Expression Name and Data Type - Pressing Enter", () => {
    test.describe("Change Boxed Expression name - Pressing Enter", () => {
      test("should change name - Pressing Enter - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Decision Table - one output column", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Decision Table - multiple output columns", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Pressing Enter - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });
    });

    test.describe("Change Boxed Expression Data Type - Pressing Enter", () => {
      test("should change Data Type - Pressing Enter - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Decision Table - one output column", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Decision Table - multiple output columns", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Pressing Enter - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("boolean");
      });
    });

    test.describe("Change Boxed Expression name and Data Type - Pressing Enter", () => {
      test("should change name and Data Type - Pressing Enter - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Decision Table - one output column", async ({
        bee,
      }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Decision Table - multiple output columns", async ({
        bee,
      }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Pressing Enter - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.PRESS_ENTER });
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.PRESS_ENTER });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("boolean");
      });
    });
  });

  test.describe("Change Boxed Expression Name and Data Type - Clicking Outside", () => {
    beforeEach(() => {
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/apache/incubator-kie-issues/issues/1961",
      });
    });

    test.describe("Change Boxed Expression name - Clicking Outside", () => {
      test("should change name - Clicking Outside - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Decision Table - one output column", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Decision Table - multiple output columns", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });

      test("should change name - Clicking Outside - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("<Undefined>");
      });
    });

    test.describe("Change Boxed Expression Data Type - Clicking Outside", () => {
      test("should change Data Type - Clicking Outside - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Decision Table - one output column", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Decision Table - multiple output columns", async ({ bee }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change Data Type - Clicking Outside - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("Expression Name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("boolean");
      });
    });

    test.describe("Change Boxed Expression name and Data Type - Clicking Outside", () => {
      test("should change name and Data Type - Clicking Outside - Literal Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectLiteral();
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asLiteral().expressionHeaderCell.open();
        await bee.expression
          .asLiteral()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Relation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectRelation();
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asRelation().expressionHeaderCell.open();
        await bee.expression
          .asRelation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Context Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectContext();
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asContext().expressionHeaderCell.open();
        await bee.expression
          .asContext()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Decision Table - one output column", async ({
        bee,
      }) => {
        await bee.selectExpressionMenu.selectDecisionTable();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Decision Table - multiple output columns", async ({
        bee,
      }) => {
        await bee.selectExpressionMenu.selectDecisionTable();
        await bee.expression.asDecisionTable().addOutputAtStart();
        await bee.expression.asDecisionTable().addOutputAtStart();

        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asDecisionTable().expressionHeaderCell.open();
        await bee.expression
          .asDecisionTable()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - List Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectList();
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asList().expressionHeaderCell.open();
        await bee.expression
          .asList()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Invocation Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectInvocation();
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asInvocation().expressionHeaderCell.open();
        await bee.expression
          .asInvocation()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Conditional Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectConditional();
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asConditional().expressionHeaderCell.open();
        await bee.expression
          .asConditional()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - For Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFor();
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asFor().expressionHeaderCell.open();
        await bee.expression
          .asFor()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Every Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectEvery();
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asEvery().expressionHeaderCell.open();
        await bee.expression
          .asEvery()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Some Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectSome();
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asSome().expressionHeaderCell.open();
        await bee.expression
          .asSome()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("boolean");
      });

      test("should change name and Data Type - Clicking Outside - Filter Expression", async ({ bee }) => {
        await bee.selectExpressionMenu.selectFilter();
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setName({ name: "test name", close: CloseOption.CLICK_OUTSIDE });
        await bee.expression.asFilter().expressionHeaderCell.open();
        await bee.expression
          .asFilter()
          .expressionHeaderCell.setDataType({ dataType: "boolean", close: CloseOption.CLICK_OUTSIDE });

        expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("test name");
        expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("boolean");
      });
    });
  });
});

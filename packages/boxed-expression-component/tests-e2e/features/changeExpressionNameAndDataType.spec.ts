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

test.describe("Change Expression Name and Data Type", () => {
  test.beforeEach(async ({ bee, browserName, clipboard }) => {
    await bee.goto();
  });

  test("should change name and Data Type - Literal Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().nameAndDataTypeCell.open();
    await bee.expression.asLiteral().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asLiteral().nameAndDataTypeCell.open();
    await bee.expression.asLiteral().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asLiteral().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asLiteral().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asLiteral().nameAndDataTypeCell.open();
    await bee.expression.asLiteral().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asLiteral().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asLiteral().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asLiteral().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Relation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectRelation();
    await bee.expression.asRelation().nameAndDataTypeCell.open();
    await bee.expression.asRelation().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asRelation().nameAndDataTypeCell.open();
    await bee.expression.asRelation().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asRelation().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asRelation().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asRelation().nameAndDataTypeCell.open();
    await bee.expression.asRelation().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asRelation().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asRelation().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asRelation().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Context Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectContext();
    await bee.expression.asContext().expressionHeaderCell.open();
    await bee.expression.asContext().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asContext().expressionHeaderCell.open();
    await bee.expression.asContext().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asContext().expressionHeaderCell.open();
    await bee.expression.asContext().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asContext().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asContext().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asContext().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Decision Table", async ({ bee }) => {
    await bee.selectExpressionMenu.selectDecisionTable();

    await bee.expression.asDecisionTable().nameAndDataTypeCell.open();
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asDecisionTable().nameAndDataTypeCell.open();
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asDecisionTable().nameAndDataTypeCell.open();
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getDataType()).toContain("string");

    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();

    await bee.expression.asDecisionTable().nameAndDataTypeCell.open();
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setName({ name: "nice name", close: false });
    await bee.expression.asDecisionTable().nameAndDataTypeCell.setDataType({ dataType: "number", close: true });

    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getName()).toEqual("nice name");
    expect(await bee.expression.asDecisionTable().nameAndDataTypeCell.getDataType()).toContain("number");
  });

  test("should change name and Data Type - List Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectList();
    await bee.expression.asList().nameAndDataTypeCell.open();
    await bee.expression.asList().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asList().nameAndDataTypeCell.open();
    await bee.expression.asList().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asList().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asList().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asList().nameAndDataTypeCell.open();
    await bee.expression.asList().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asList().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asList().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asList().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Invocation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectInvocation();
    await bee.expression.asInvocation().nameAndDataTypeCell.open();
    await bee.expression.asInvocation().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asInvocation().nameAndDataTypeCell.open();
    await bee.expression.asInvocation().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asInvocation().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asInvocation().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asInvocation().nameAndDataTypeCell.open();
    await bee.expression.asInvocation().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asInvocation().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asInvocation().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asInvocation().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Conditional Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectConditional();
    await bee.expression.asConditional().nameAndDataTypeCell.open();
    await bee.expression.asConditional().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asConditional().nameAndDataTypeCell.open();
    await bee.expression.asConditional().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asConditional().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asConditional().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asConditional().nameAndDataTypeCell.open();
    await bee.expression.asConditional().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asConditional().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asConditional().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asConditional().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - For Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFor();
    await bee.expression.asFor().nameAndDataTypeCell.open();
    await bee.expression.asFor().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asFor().nameAndDataTypeCell.open();
    await bee.expression.asFor().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asFor().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asFor().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asFor().nameAndDataTypeCell.open();
    await bee.expression.asFor().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asFor().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asFor().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asFor().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Every Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectEvery();
    await bee.expression.asEvery().nameAndDataTypeCell.open();
    await bee.expression.asEvery().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asEvery().nameAndDataTypeCell.open();
    await bee.expression.asEvery().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asEvery().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asEvery().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asEvery().nameAndDataTypeCell.open();
    await bee.expression.asEvery().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asEvery().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asEvery().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asEvery().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Some Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectSome();
    await bee.expression.asSome().nameAndDataTypeCell.open();
    await bee.expression.asSome().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asSome().nameAndDataTypeCell.open();
    await bee.expression.asSome().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asSome().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asSome().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asSome().nameAndDataTypeCell.open();
    await bee.expression.asSome().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asSome().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asSome().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asSome().nameAndDataTypeCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Filter Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFilter();
    await bee.expression.asFilter().nameAndDataTypeCell.open();
    await bee.expression.asFilter().nameAndDataTypeCell.setName({ name: "test name", close: true });
    await bee.expression.asFilter().nameAndDataTypeCell.open();
    await bee.expression.asFilter().nameAndDataTypeCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asFilter().nameAndDataTypeCell.getName()).toEqual("test name");
    expect(await bee.expression.asFilter().nameAndDataTypeCell.getDataType()).toContain("boolean");

    await bee.expression.asFilter().nameAndDataTypeCell.open();
    await bee.expression.asFilter().nameAndDataTypeCell.setName({ name: "another one", close: false });
    await bee.expression.asFilter().nameAndDataTypeCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asFilter().nameAndDataTypeCell.getName()).toEqual("another one");
    expect(await bee.expression.asFilter().nameAndDataTypeCell.getDataType()).toContain("string");
  });
});

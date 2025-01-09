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
  test.beforeEach(async ({ bee }) => {
    await bee.goto();
  });

  test("should change name and Data Type - Literal Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().expressionHeaderCell.open();
    await bee.expression.asLiteral().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asLiteral().expressionHeaderCell.open();
    await bee.expression.asLiteral().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asLiteral().expressionHeaderCell.open();
    await bee.expression.asLiteral().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asLiteral().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asLiteral().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asLiteral().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Relation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectRelation();
    await bee.expression.asRelation().expressionHeaderCell.open();
    await bee.expression.asRelation().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asRelation().expressionHeaderCell.open();
    await bee.expression.asRelation().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asRelation().expressionHeaderCell.open();
    await bee.expression.asRelation().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asRelation().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asRelation().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asRelation().expressionHeaderCell.getDataType()).toContain("string");
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

    await bee.expression.asDecisionTable().expressionHeaderCell.open();
    await bee.expression.asDecisionTable().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asDecisionTable().expressionHeaderCell.open();
    await bee.expression.asDecisionTable().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asDecisionTable().expressionHeaderCell.open();
    await bee.expression.asDecisionTable().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asDecisionTable().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("string");

    await bee.expression.asDecisionTable().addOutputAtStart();
    await bee.expression.asDecisionTable().addOutputAtStart();

    await bee.expression.asDecisionTable().expressionHeaderCell.open();
    await bee.expression.asDecisionTable().expressionHeaderCell.setName({ name: "nice name", close: false });
    await bee.expression.asDecisionTable().expressionHeaderCell.setDataType({ dataType: "number", close: true });

    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getName()).toEqual("nice name");
    expect(await bee.expression.asDecisionTable().expressionHeaderCell.getDataType()).toContain("number");
  });

  test("should change name and Data Type - List Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectList();
    await bee.expression.asList().expressionHeaderCell.open();
    await bee.expression.asList().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asList().expressionHeaderCell.open();
    await bee.expression.asList().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asList().expressionHeaderCell.open();
    await bee.expression.asList().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asList().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asList().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asList().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Invocation Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectInvocation();
    await bee.expression.asInvocation().expressionHeaderCell.open();
    await bee.expression.asInvocation().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asInvocation().expressionHeaderCell.open();
    await bee.expression.asInvocation().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asInvocation().expressionHeaderCell.open();
    await bee.expression.asInvocation().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asInvocation().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Conditional Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectConditional();
    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression.asConditional().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression.asConditional().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asConditional().expressionHeaderCell.open();
    await bee.expression.asConditional().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asConditional().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asConditional().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asConditional().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - For Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFor();
    await bee.expression.asFor().expressionHeaderCell.open();
    await bee.expression.asFor().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asFor().expressionHeaderCell.open();
    await bee.expression.asFor().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asFor().expressionHeaderCell.open();
    await bee.expression.asFor().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asFor().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asFor().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asFor().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Every Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectEvery();
    await bee.expression.asEvery().expressionHeaderCell.open();
    await bee.expression.asEvery().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asEvery().expressionHeaderCell.open();
    await bee.expression.asEvery().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asEvery().expressionHeaderCell.open();
    await bee.expression.asEvery().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asEvery().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asEvery().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asEvery().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Some Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectSome();
    await bee.expression.asSome().expressionHeaderCell.open();
    await bee.expression.asSome().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asSome().expressionHeaderCell.open();
    await bee.expression.asSome().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asSome().expressionHeaderCell.open();
    await bee.expression.asSome().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asSome().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asSome().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asSome().expressionHeaderCell.getDataType()).toContain("string");
  });

  test("should change name and Data Type - Filter Expression", async ({ bee }) => {
    await bee.selectExpressionMenu.selectFilter();
    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression.asFilter().expressionHeaderCell.setName({ name: "test name", close: true });
    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression.asFilter().expressionHeaderCell.setDataType({ dataType: "boolean", close: true });

    expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("test name");
    expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("boolean");

    await bee.expression.asFilter().expressionHeaderCell.open();
    await bee.expression.asFilter().expressionHeaderCell.setName({ name: "another one", close: false });
    await bee.expression.asFilter().expressionHeaderCell.setDataType({ dataType: "string", close: true });

    expect(await bee.expression.asFilter().expressionHeaderCell.getName()).toEqual("another one");
    expect(await bee.expression.asFilter().expressionHeaderCell.getDataType()).toContain("string");
  });
});

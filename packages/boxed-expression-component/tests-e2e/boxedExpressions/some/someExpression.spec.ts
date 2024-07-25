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
import { TestAnnotations } from "../../../../playwright-base/annotations";

test.describe("Create Boxed Some", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedSome("base");
  });
  test("should keep IDs after resetting entries", async ({ bee, jsonModel, page }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1182",
    });

    const someExpression = bee.expression.asSome();
    await someExpression.in.selectExpressionMenu.selectLiteral();
    await someExpression.satisfies.selectExpressionMenu.selectLiteral();
    await someExpression.in.expression.asLiteral().fill("test");
    await someExpression.satisfies.expression.asLiteral().fill("test");
    await someExpression.in.contextMenu.open();
    await someExpression.in.contextMenu.option("Reset").nth(0).click();
    await someExpression.satisfies.contextMenu.open();
    await someExpression.satisfies.contextMenu.option("Reset").nth(0).click();

    expect((await jsonModel.getSomeExpression()).in).not.toBeUndefined();
    expect((await jsonModel.getSomeExpression()).satisfies).not.toBeUndefined();
    expect((await jsonModel.getSomeExpression()).in["@_id"]).not.toBeUndefined();
    expect((await jsonModel.getSomeExpression()).satisfies["@_id"]).not.toBeUndefined();
  });
});

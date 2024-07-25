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

test.describe("Create Boxed Every", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedEvery("base");
  });
  test("should keep IDs after resetting entries", async ({ bee, jsonModel, page }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1182",
    });

    const everyExpression = bee.expression.asEvery();
    await everyExpression.in.selectExpressionMenu.selectLiteral();
    await everyExpression.satisfies.selectExpressionMenu.selectLiteral();
    await everyExpression.in.expression.asLiteral().fill("test");
    await everyExpression.satisfies.expression.asLiteral().fill("test");
    await everyExpression.in.contextMenu.open();
    await everyExpression.in.contextMenu.option("Reset").nth(0).click();
    await everyExpression.satisfies.contextMenu.open();
    await everyExpression.satisfies.contextMenu.option("Reset").nth(0).click();

    expect((await jsonModel.getEveryExpression()).in).not.toBeUndefined();
    expect((await jsonModel.getEveryExpression()).satisfies).not.toBeUndefined();
    expect((await jsonModel.getEveryExpression()).in["@_id"]).not.toBeUndefined();
    expect((await jsonModel.getEveryExpression()).satisfies["@_id"]).not.toBeUndefined();
  });
});

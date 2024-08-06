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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Create Boxed For", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedFor();
  });
  test("should have IDs after resetting 'in' and 'return' and setting it again", async ({ bee, jsonModel }) => {
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/apache/incubator-kie-issues/issues/1182",
    });

    const forExpression = bee.expression.asFor();
    await forExpression.in.selectExpressionMenu.selectLiteral();
    await forExpression.return.selectExpressionMenu.selectLiteral();
    await forExpression.in.expression.asLiteral().fill("test");
    await forExpression.return.expression.asLiteral().fill("test");
    await forExpression.in.contextMenu.open();
    await forExpression.in.contextMenu.option("Reset").nth(0).click();
    await forExpression.return.contextMenu.open();
    await forExpression.return.contextMenu.option("Reset").nth(0).click();

    expect((await jsonModel.getForExpression()).in).not.toBeUndefined();
    expect((await jsonModel.getForExpression()).return).not.toBeUndefined();
    expect((await jsonModel.getForExpression()).in["@_id"]).not.toBeUndefined();
    expect((await jsonModel.getForExpression()).return["@_id"]).not.toBeUndefined();
  });
});

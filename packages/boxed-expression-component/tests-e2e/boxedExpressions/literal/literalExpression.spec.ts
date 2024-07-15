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

test.describe("Create Boxed Literal", () => {
  test("should render expression correctly", async ({ bee, stories, page }) => {
    await stories.openBoxedLiteral();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell")).toHaveCount(1);
    await expect(bee.getContainer()).toHaveScreenshot("boxed-literal.png");
  });
});

test.describe("Boxed Literal", () => {
  test.beforeEach(async ({ bee }) => {
    await bee.goto();
  });
  test("should fill and change content", async ({ bee }) => {
    await bee.selectExpressionMenu.selectLiteral();
    await bee.expression.asLiteral().fill("test content");
    await expect(bee.expression.asLiteral().content).toContainText("test content");
    await bee.expression.asLiteral().fill("");
    await expect(bee.expression.asLiteral().content).not.toContainText("test content");
  });
});

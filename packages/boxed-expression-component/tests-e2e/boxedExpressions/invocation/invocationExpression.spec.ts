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

test.describe("Create Boxed Invocation", () => {
  test("should render expression correctly", async ({ bee, stories, page }) => {
    await stories.openBoxedInvocation();
    expect(await bee.expression.asInvocation().expressionHeaderCell.getName()).toEqual("Expression Name");
    expect(await bee.expression.asInvocation().expressionHeaderCell.getDataType()).toEqual("(<Undefined>)");
    await expect(bee.expression.asInvocation().invokedFunctionNameCell).toBeAttached();
    await expect(page.getByRole("cell", { name: "p-1" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(bee.getContainer()).toHaveScreenshot("boxed-invocation.png");
  });

  test("should commit invoked function by cell click", async ({ bee, stories, page }) => {
    await stories.openBoxedInvocation();

    // click to start editing and type new value
    await bee.expression.asInvocation().invokedFunctionNameCell.click();
    await page.keyboard.type("Changed Invoked Function Name");

    // commit a change by a click to another cell
    await bee.expression.asInvocation().parameter(0).descriptionCell.select();

    await expect(bee.getContainer()).toHaveScreenshot("boxed-invocation-commit-function-by-cell-click.png");
  });
});

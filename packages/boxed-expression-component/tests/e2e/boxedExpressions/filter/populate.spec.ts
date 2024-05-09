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

test.describe("Populate Boxed Filter", () => {
  test("should rename a filter", async ({ stories }) => {
    await stories.openBoxedFilter("base");
  });

  test("should change a filter data type", async ({ stories }) => {
    await stories.openBoxedFilter("base");
  });

  test("should correctly create a Rebooked Flights filter", async ({
    stories,
    page,
    boxedExpressionEditor,
    resizing,
    monaco,
  }) => {
    await stories.openBoxedFilter("base");
    // await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    // await page.getByPlaceholder("Expression Name").fill("Can drive?");
    // await page.getByLabel("<Undefined>").click();
    // await page.getByRole("option", { name: "boolean" }).click();
    // await page.keyboard.press("Enter");

    // await monaco.fill({ monacoParentLocator: page, nth: 0, content: "Age >= 18 then true else false" });
    // await resizing.resizeCell(
    //   page.getByRole("columnheader", { name: "Can drive? (boolean)" }),
    //   { x: 0, y: 0 },
    //   { x: 80, y: 0 }
    // );

    // await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-literal-can-drive.png");
  });

  test("should correctly create a nested filter", async ({ stories }) => {
    await stories.openBoxedFilter("nested");
  });

  test("should correctly resize a filter", async ({ stories }) => {
    await stories.openBoxedFilter("base");
  });

  test("should correctly resize a nested filter", async ({ stories }) => {
    await stories.openBoxedFilter("nested");
  });

  test("should correctly create a filter child expressions", async ({ stories }) => {
    await stories.openBoxedFilter("nested");
  });

  test("should correctly reset a filter", async ({ stories }) => {
    await stories.openBoxedFilter("base");
  });

  test("should correctly reset a nested filter", async ({ stories }) => {
    await stories.openBoxedFilter("nested");
  });

  test("should correctly paste a filter", async ({ stories }) => {
    await stories.openBoxedFilter("base");
  });
});

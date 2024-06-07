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

import { test, expect } from "../__fixtures__/base";

test.describe("Boxed expression header menu popover", () => {
  test("should open context menu and change name and type", async ({ stories, page }) => {
    await stories.openBoxedLiteral();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expect(page.getByTestId("expression-container")).toBeAttached();

    await page.getByPlaceholder("Expression Name").fill("Test");
    await expect(page.getByPlaceholder("Expression Name")).toHaveValue("Test");

    await page.getByLabel("<Undefined>").click();
    await page.mouse.wheel(200, 0);
    await page.getByRole("option", { name: "years and months duration" }).click();
    await expect(page.getByLabel("years and months duration")).toBeAttached();
    await page.keyboard.press("Enter");

    await expect(page.getByRole("columnheader", { name: "Test (years and months duration)" })).toBeAttached();
  });

  test("should open context menu and cancel value change of name and type", async ({ stories, page }) => {
    await stories.openBoxedLiteral();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expect(page.getByTestId("expression-container")).toBeAttached();

    await page.getByPlaceholder("Expression Name").fill("Test");
    await expect(page.getByPlaceholder("Expression Name")).toHaveValue("Test");

    await page.getByLabel("<Undefined>").click();
    await page.mouse.wheel(200, 0);
    await page.getByRole("option", { name: "years and months duration" }).click();
    await expect(page.getByLabel("years and months duration")).toBeAttached();
    await page.keyboard.press("Escape");

    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
  });
});

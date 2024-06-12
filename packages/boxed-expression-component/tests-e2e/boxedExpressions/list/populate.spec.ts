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

test.describe("Populate Boxed List", () => {
  test("should correctly create an age group boxed list", async ({ stories, boxedExpressionEditor, page, monaco }) => {
    await stories.openBoxedList();
    await page.getByRole("cell", { name: "1" }).hover();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "1 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "2 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "3 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "4 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "5 " }));

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Age groups");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await monaco.fill({ monacoParentLocator: page, nth: 0, content: "<18" });
    await monaco.fill({ monacoParentLocator: page, nth: 1, content: "[18..30)" });
    await monaco.fill({ monacoParentLocator: page, nth: 2, content: "[30..50)" });
    await monaco.fill({ monacoParentLocator: page, nth: 3, content: "[50..70)" });
    await monaco.fill({ monacoParentLocator: page, nth: 4, content: ">=70" });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-list-age-group.png");
  });
});

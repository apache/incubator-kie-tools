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
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Populate Relation", () => {
  test("should correctly create a people relation", async ({ stories, page, boxedExpressionEditor, resizing }) => {
    await stories.openRelation();

    await page.getByRole("columnheader", { name: "column-1 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Name");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();
    await page.keyboard.press("Enter");
    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Name (string)" }),
      { x: 0, y: 0 },
      { x: 40, y: 0 }
    );
    await page.getByRole("columnheader", { name: "Name (string)" }).hover();
    await page.getByRole("row", { name: "Name (string)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "column-2 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Age");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");
    await page.getByRole("columnheader", { name: "Age (number)" }).hover();
    await page.getByRole("row", { name: "Age (number)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "column-3 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Country");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();
    await page.keyboard.press("Enter");
    await page.getByRole("columnheader", { name: "Country (string)" }).hover();
    await page.getByRole("row", { name: "Country (string)" }).locator("svg").click();

    await page.getByRole("columnheader", { name: "column-4 (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Married");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    test.info().annotations.push({
      type: TestAnnotations.WORKAROUND_DUE_TO,
      description: "https://github.com/apache/incubator-kie-issues/issues/541",
    });
    // Plus sign triggers flaky tests.
    await page.getByRole("columnheader", { name: "Name (string)" }).hover();
    await page.getByRole("columnheader", { name: "Age (number)" }).hover();
    await page.getByRole("columnheader", { name: "Country (string)" }).hover();
    await page.getByRole("columnheader", { name: "Married (boolean)" }).hover();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("People");
    await page.keyboard.press("Enter");

    await page.getByRole("cell", { name: "1" }).hover();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();

    await boxedExpressionEditor.fillRelation({
      startAtCell: 0,
      relationData: [
        [`"John Doe"`, "30", `"US"`, `"S"`],
        [`"Richard roe"`, "54", `"Canada"`, `"M"`],
        [`"Jane Doe"`, "23", `"England"`, `"M"`],
      ],
    });

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-people.png");
  });
});

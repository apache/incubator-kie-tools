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

test.describe("Create Boxed Function", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedFunction();
  });

  test("should render FEEL function expression correctly", async ({ boxedExpressionEditor, page }) => {
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "F", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Edit parameters" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-feel-function.png");
  });

  test("should render Java function expression correctly", async ({ boxedExpressionEditor, page }) => {
    await page.getByRole("columnheader", { name: "F", exact: true }).click();
    await page.getByRole("menuitem", { name: "Java" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-java-function.png");

    await expect(page.getByRole("columnheader", { name: "J", exact: true })).toBeAttached();
    // the "onmouseenter" events triggers if the mouse was outside of the element before it appears on screen.
    await page.getByRole("columnheader", { name: "J", exact: true }).hover();
    await page.getByText("Class name(string)").hover();
    await expect(page.getByRole("cell", { name: "Class name (string)" }).locator("svg")).toBeAttached();
    await page.getByText("Method signature(string)").hover();
    await expect(page.getByRole("cell", { name: "Method signature (string)" }).locator("svg")).toBeAttached();
    await expect(page.getByRole("row", { name: "1 Class name (string)" })).toBeAttached();
    await expect(page.getByRole("row", { name: "2 Method signature (string)" })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(6);
  });

  test("should render PMML function expression correctly", async ({ boxedExpressionEditor, page }) => {
    await page.getByRole("columnheader", { name: "F", exact: true }).click();
    await page.getByRole("menuitem", { name: "PMML" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-pmml-function.png");

    await expect(page.getByRole("columnheader", { name: "P", exact: true })).toBeAttached();
    await expect(page.getByText("Document(string)")).toBeAttached();
    await expect(page.getByText("Model(string)")).toBeAttached();

    await expect(page.getByRole("row", { name: "2 Model (string) Options menu" }).locator("div").nth(3)).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("pmml-document").click();
    await expect(page.getByLabel("document")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("pmml-model").click();
    await expect(page.getByLabel("model")).toBeAttached();

    await page.getByLabel("document").click();
    await page.getByTestId("pmml-mining pmml").click();
    await expect(page.getByLabel("mining pmml")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("pmml-MiningModelSum").click();
    await expect(page.getByLabel("MiningModelSum")).toBeAttached();

    await page.getByLabel("mining pmml").click();
    await page.getByTestId("pmml-regression pmml").click();
    await expect(page.getByLabel("regression pmml")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("pmml-RegressionLinear").click();
    await expect(page.getByLabel("RegressionLinear")).toBeAttached();

    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(6);
  });

  test.describe("Change function type", () => {
    test.beforeEach(async ({ page }) => {
      await page.getByRole("columnheader", { name: "F", exact: true }).click();
    });

    const functioTypes = [
      ["FEEL", "F"],
      ["Java", "J"],
      ["PMML", "P"],
    ];

    for (const [functionName, abreviation] of functioTypes) {
      test(`should change function type to ${functionName}`, async ({ page }) => {
        await page.getByRole("menuitem", { name: functionName }).hover();
        await expect(page.getByLabel(`${functionName}-help`)).toBeAttached();
        await page.getByRole("menuitem", { name: functionName }).click();
        await expect(page.getByRole("columnheader", { name: abreviation, exact: true })).toBeAttached();
      });
    }
  });

  test("should edit function parameters", async ({ page }) => {
    await page.getByRole("columnheader", { name: "Edit parameters" }).click();
    await expect(page.getByRole("button", { name: "Add parameter" })).toBeAttached();

    await page.getByRole("button", { name: "Add parameter" }).click();
    await expect(page.getByRole("button", { name: "Add parameter" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "(p-1: (<Undefined>))" })).toBeAttached();

    await page.getByRole("textbox").click();
    await page.getByRole("textbox").press("Control+a");
    await page.getByRole("textbox").fill("test");
    await page.getByRole("textbox").press("Tab");

    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await expect(page.getByRole("columnheader", { name: "(test: (boolean))" })).toBeAttached();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await expect(page.getByRole("columnheader", { name: "(test: (boolean), p-2: (<Undefined>))" })).toBeAttached();
    await page.getByLabel("", { exact: true }).getByRole("button").nth(2).click();
    await expect(page.getByRole("columnheader", { name: "(p-2: (<Undefined>))" })).toBeAttached();

    await page.getByLabel("", { exact: true }).getByRole("button").nth(2).click();
    await expect(page.getByRole("columnheader", { name: "Edit parameters" })).toBeAttached();
  });
});

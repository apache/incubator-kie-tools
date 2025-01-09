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

test.describe("Create Boxed Function", () => {
  test.beforeEach(async ({ stories }) => {
    await stories.openBoxedFunction();
  });

  test("should render FEEL function expression correctly", async ({ bee, page }) => {
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(await bee.expression.asFunction().functionType.content.textContent()).toEqual("F");
    await expect(await bee.expression.asFunction().parameters.textContent()).toEqual("Edit parameters");
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(bee.getContainer()).toHaveScreenshot("boxed-feel-function.png");
  });

  test("should render Java function expression correctly", async ({ bee, page }) => {
    await bee.expression.asFunction().functionType.open();
    await bee.expression.asFunction().functionType.setType({ type: "Java" });
    await expect(bee.getContainer()).toHaveScreenshot("boxed-java-function.png");

    await expect(await bee.expression.asFunction().functionType.content.textContent()).toEqual("J");
    // the "onmouseenter" events triggers if the mouse was outside of the element before it appears on screen.
    await bee.expression.asFunction().functionType.content.hover();
    await page.getByText("Class name(string)").hover();
    await expect(page.getByRole("cell", { name: "Class name (string)" }).locator("svg")).toBeAttached();
    await page.getByText("Method signature(string)").hover();
    await expect(page.getByRole("cell", { name: "Method signature (string)" }).locator("svg")).toBeAttached();
    await expect(page.getByRole("row", { name: "1 Class name (string)" })).toBeAttached();
    await expect(page.getByRole("row", { name: "2 Method signature (string)" })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(6);
  });

  test("should render PMML function expression correctly", async ({ bee, page }) => {
    await bee.expression.asFunction().functionType.open();
    await bee.expression.asFunction().functionType.setType({ type: "PMML" });
    await expect(bee.getContainer()).toHaveScreenshot("boxed-pmml-function.png");

    await expect(page.getByRole("columnheader", { name: "P", exact: true })).toBeAttached();
    await expect(page.getByText("Document(string)")).toBeAttached();
    await expect(page.getByText("Model(string)")).toBeAttached();

    await expect(page.getByRole("row", { name: "2 Model (string) Options menu" }).locator("div").nth(3)).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("kie-tools--bee--pmml-document").click();
    await expect(page.getByLabel("document")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("kie-tools--bee--pmml-model").click();
    await expect(page.getByLabel("model")).toBeAttached();

    await page.getByLabel("document").click();
    await page.getByTestId("kie-tools--bee--pmml-mining pmml").click();
    await expect(page.getByLabel("mining pmml")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("kie-tools--bee--pmml-MiningModelSum").click();
    await expect(page.getByLabel("MiningModelSum")).toBeAttached();

    await page.getByLabel("mining pmml").click();
    await page.getByTestId("kie-tools--bee--pmml-regression pmml").click();
    await expect(page.getByLabel("regression pmml")).toBeAttached();
    await page.getByLabel("-- None selected --").click();
    await page.getByTestId("kie-tools--bee--pmml-RegressionLinear").click();
    await expect(page.getByLabel("RegressionLinear")).toBeAttached();

    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(6);
  });

  test.describe("Change function type", () => {
    test.beforeEach(async ({ bee }) => {
      await bee.expression.asFunction().functionType.open();
    });

    const functioTypes = [
      ["FEEL", "F"],
      ["Java", "J"],
      ["PMML", "P"],
    ];

    for (const [functionName, abbreviation] of functioTypes) {
      test(`should change function type to ${functionName}`, async ({ bee, page }) => {
        await page.getByRole("menuitem", { name: functionName }).hover();
        await expect(page.getByLabel(`${functionName}-help`)).toBeAttached();
        await bee.expression.asFunction().functionType.setType({ type: functionName as "FEEL" | "Java" | "PMML" });
        await expect(page.getByRole("columnheader", { name: abbreviation, exact: true })).toBeAttached();
      });
    }
  });

  test("should edit function parameters", async ({ bee }) => {
    await bee.expression.asFunction().addParameter({ name: "test", dataType: "boolean" });
    await bee.expression.asFunction().addParameter({});

    expect(await bee.expression.asFunction().parameters.textContent()).toEqual("(test: (boolean), p-2: (<Undefined>))");

    await bee.expression.asFunction().deleteParameter({ nth: 1 });
    await bee.expression.asFunction().deleteParameter({ nth: 0 });

    expect(await bee.expression.asFunction().parameters.textContent()).toEqual("Edit parameters");
  });
});

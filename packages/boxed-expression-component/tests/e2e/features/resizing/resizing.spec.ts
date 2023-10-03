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

test.describe("Resizing", () => {
  test.describe("Literal expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedLiteral();
    });

    test("should resize the header and reset to default width", async ({ page, resizing }) => {
      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });

      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 240);
      await resizing.reset(header);
      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 190);
    });

    test("should change literal decision name and resize to fit", async ({ page, resizing, browserName }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very big decision literal name");
      await page.keyboard.press("Enter");

      const header = page.getByRole("columnheader", {
        name: "A very very very big decision literal name (<Undefined>)",
      });
      const literal = page.getByRole("cell");

      expect(await header.boundingBox()).toHaveProperty("width", 190);
      expect(await literal.boundingBox()).toHaveProperty("width", 190);
      await resizing.reset(header);
      if (browserName === "webkit") {
        expect(await header.boundingBox()).toHaveProperty("width", 312);
        expect(await literal.boundingBox()).toHaveProperty("width", 312);
      } else {
        expect(await header.boundingBox()).toHaveProperty("width", 301);
        expect(await literal.boundingBox()).toHaveProperty("width", 301);
      }
    });
  });

  test.describe("Context expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedContext();
    });

    test("should resize header column", async ({ page, resizing }) => {
      test.skip(true, "https://github.com/kiegroup/kie-issues/issues/179");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/179",
      });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
    });

    test("should resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
    });

    test("should change decision name and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very very very very big decision name");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", {
        name: "A very very very very very very big decision name (<Undefined>)",
      });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 351);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 231);
    });

    test("should resize results column and reset", async ({ page, resizing }) => {
      const result = page.getByRole("cell", { name: "<result>" });
      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });

      await resizing.resizeCell(result, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await result.boundingBox()).toHaveProperty("width", 170);
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 170);
      await resizing.reset(result);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
    });

    test("should resize context entry cell and reset", async ({ page, resizing }) => {
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

      await resizing.resizeCell(firstEntry, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await result.boundingBox()).toHaveProperty("width", 170);
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 170);
      await resizing.reset(firstEntry);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
    });

    test("should change context entry name, resize to fit and reset to result size", async ({
      page,
      resizing,
      browserName,
    }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A context entry name");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "A context entry name (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(firstEntry);
      expect(await header.boundingBox()).toHaveProperty("width", 365);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 153);
      expect(await result.boundingBox()).toHaveProperty("width", 153);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(result);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
    });

    test("check resize on nested stories", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/180",
      });

      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "Context" }).click();
      await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).first().hover();
      await page.locator(".add-row-button").click();
      await page.getByText("Select expression").nth(2).click();
      await page.getByRole("menuitem", { name: "Context" }).click();

      const nestedEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).nth(2);
      await resizing.resizeCell(nestedEntry, { x: 0, y: 0 }, { x: 50, y: 0 });

      await page.getByText("Select expression").nth(2).click();
      await page.getByRole("menuitem", { name: "Literal" }).click();
      const nestedLiteralExpresison = page.getByRole("cell", { name: "=", exact: true });

      expect(await nestedEntry.boundingBox()).toHaveProperty("width", 170);
      expect(await nestedLiteralExpresison.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(nestedEntry);
      expect(await nestedEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await nestedLiteralExpresison.boundingBox()).toHaveProperty("width", 212);
    });
  });

  test.describe("Decision Table expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openDecisionTable();
    });

    test("should resize input column and add new columns", async ({ page, resizing }) => {
      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      await resizing.resizeCell(inputHeader, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 150);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await inputHeader.hover({ position: { x: 0, y: 0 } });
      await inputHeader.locator("svg").click();

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 150);
      expect(await page.getByRole("columnheader", { name: "input-2 (<Undefined>)" }).boundingBox()).toHaveProperty(
        "width",
        100
      );
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(inputHeader);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should change input column name and reset size", async ({ page, resizing, browserName }) => {
      await page.getByRole("columnheader", { name: "input-1 (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("Installment Calculation");
      await page.keyboard.press("Enter");

      const inputHeader = page.getByRole("columnheader", { name: "Installment Calculation (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(inputHeader);
      if (browserName === "webkit") {
        expect(await inputHeader.boundingBox()).toHaveProperty("width", 179);
      } else {
        expect(await inputHeader.boundingBox()).toHaveProperty("width", 173);
      }
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should resize output column and add new columns", async ({ page, resizing }) => {
      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      await resizing.resizeCell(outputHeader, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 150);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await outputHeader.hover({ position: { x: 0, y: 0 } });
      await outputHeader.locator("svg").click();
      const output1 = page.getByRole("columnheader", { name: "output-1 (<Undefined>)" });
      const output2 = page.getByRole("columnheader", { name: "output-2 (<Undefined>)" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 250);
      expect(await output1.boundingBox()).toHaveProperty("width", 150);
      expect(await output2.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(outputHeader);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 200);
      expect(await output1.boundingBox()).toHaveProperty("width", 100);
      expect(await output2.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should change decision name and reset to fit", async ({ page, resizing, browserName }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("Installment Calculation");
      await page.keyboard.press("Enter");

      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Installment Calculation (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(outputHeader);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      if (browserName === "webkit") {
        expect(await outputHeader.boundingBox()).toHaveProperty("width", 179);
      } else {
        expect(await outputHeader.boundingBox()).toHaveProperty("width", 173);
      }
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should add new output columns rename decision and resize to fit to proportionally distribute", async ({
      page,
      resizing,
      browserName,
    }) => {
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await outputHeader.hover({ position: { x: 0, y: 0 } });
      await outputHeader.locator("svg").click();

      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very very very big decision name");
      await page.keyboard.press("Enter");
      const header = page.getByRole("columnheader", {
        name: "A very very very very very big decision name (<Undefined>)",
      });
      const output1 = page.getByRole("columnheader", { name: "output-1 (<Undefined>)" });
      const output2 = page.getByRole("columnheader", { name: "output-2 (<Undefined>)" });
      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await header.boundingBox()).toHaveProperty("width", 200);
      expect(await output1.boundingBox()).toHaveProperty("width", 100);
      expect(await output2.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(header);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      if (browserName === "webkit") {
        expect(await header.boundingBox()).toHaveProperty("width", 326);
        expect(await output1.boundingBox()).toHaveProperty("width", 163);
        expect(await output2.boundingBox()).toHaveProperty("width", 163);
      } else {
        expect(await header.boundingBox()).toHaveProperty("width", 315);
        expect(await output1.boundingBox()).toHaveProperty("width", 157);
        expect(await output2.boundingBox()).toHaveProperty("width", 158);
      }
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should add new output columns and resize to fit", async ({ page, resizing, browserName }) => {
      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      await outputHeader.hover({ position: { x: 0, y: 0 } });
      await outputHeader.locator("svg").click();

      await page.getByRole("columnheader", { name: "output-1 (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("Installment Calculation");
      await page.keyboard.press("Enter");
      const output1 = page.getByRole("columnheader", { name: "Installment Calculation (<Undefined>)" });
      const output2 = page.getByRole("columnheader", { name: "output-2 (<Undefined>)" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 200);
      expect(await output1.boundingBox()).toHaveProperty("width", 100);
      expect(await output2.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(output1);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      if (browserName === "webkit") {
        expect(await outputHeader.boundingBox()).toHaveProperty("width", 279);
        expect(await output1.boundingBox()).toHaveProperty("width", 179);
      } else {
        expect(await outputHeader.boundingBox()).toHaveProperty("width", 273);
        expect(await output1.boundingBox()).toHaveProperty("width", 173);
      }
      expect(await output2.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
    });

    test("should resize annotation column and reset", async ({ page, resizing, browserName }) => {
      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "annotation-1" });

      await resizing.resizeCell(annotationsHeader, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 150);

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 150);
      await resizing.reset(annotationsHeader);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      if (browserName === "webkit") {
        expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 106);
      } else {
        expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 102);
      }
    });

    test("should change annotations column name and reset size", async ({ page, resizing, browserName }) => {
      await page.getByRole("columnheader", { name: "annotation-1" }).click();
      await page.keyboard.type("Relevant information");
      await page.keyboard.press("Enter");

      const inputHeader = page.getByRole("columnheader", { name: "input-1 (<Undefined>)" });
      const outputHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const annotationsHeader = page.getByRole("columnheader", { name: "Relevant information" });

      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(annotationsHeader);
      expect(await inputHeader.boundingBox()).toHaveProperty("width", 100);
      expect(await outputHeader.boundingBox()).toHaveProperty("width", 100);
      if (browserName === "webkit") {
        expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 161);
      } else {
        expect(await annotationsHeader.boundingBox()).toHaveProperty("width", 158);
      }
    });
  });

  test.describe("Relation expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openRelation();
    });

    test("should resize column and add new columns", async ({ page, resizing }) => {
      const columnsHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const column1 = page.getByRole("columnheader", { name: "column-1 (<Undefined>)" });

      await resizing.resizeCell(columnsHeader, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await columnsHeader.boundingBox()).toHaveProperty("width", 150);
      await column1.hover({ position: { x: 0, y: 0 } });
      await column1.locator("svg").click();
      const column2 = page.getByRole("columnheader", { name: "column-2 (<Undefined>)" });

      expect(await columnsHeader.boundingBox()).toHaveProperty("width", 250);
      expect(await column1.boundingBox()).toHaveProperty("width", 150);
      expect(await column2.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(columnsHeader);
      expect(await columnsHeader.boundingBox()).toHaveProperty("width", 200);
      expect(await column1.boundingBox()).toHaveProperty("width", 100);
    });

    test("should change decision name and reset to fit and proportionally distribute to columns", async ({
      page,
      resizing,
      browserName,
    }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very big decision name");
      await page.keyboard.press("Enter");

      const columnsHeader = page.getByRole("columnheader", {
        name: "A very very very big decision name (<Undefined>)",
      });
      const column1 = page.getByRole("columnheader", { name: "column-1 (<Undefined>)" });
      await column1.hover({ position: { x: 0, y: 0 } });
      await column1.locator("svg").click();
      const column2 = page.getByRole("columnheader", { name: "column-2 (<Undefined>)" });

      expect(await columnsHeader.boundingBox()).toHaveProperty("width", 200);
      expect(await column1.boundingBox()).toHaveProperty("width", 100);
      expect(await column2.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(columnsHeader);
      if (browserName === "webkit") {
        expect(await columnsHeader.boundingBox()).toHaveProperty("width", 256);
        expect(await column1.boundingBox()).toHaveProperty("width", 128);
        expect(await column2.boundingBox()).toHaveProperty("width", 128);
      } else {
        expect(await columnsHeader.boundingBox()).toHaveProperty("width", 248);
        expect(await column1.boundingBox()).toHaveProperty("width", 124);
        expect(await column2.boundingBox()).toHaveProperty("width", 124);
      }
    });

    test("should change column name and reset size", async ({ page, resizing, browserName }) => {
      await page.getByRole("columnheader", { name: "column-1 (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("Installment Calculation");
      await page.keyboard.press("Enter");

      const columnsHeader = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const column1 = page.getByRole("columnheader", { name: "Installment Calculation (<Undefined>)" });
      await column1.hover({ position: { x: 0, y: 0 } });
      await column1.locator("svg").click();
      const column2 = page.getByRole("columnheader", { name: "column-2 (<Undefined>)" });

      expect(await columnsHeader.boundingBox()).toHaveProperty("width", 200);
      expect(await column1.boundingBox()).toHaveProperty("width", 100);
      expect(await column2.boundingBox()).toHaveProperty("width", 100);
      await resizing.reset(column1);

      if (browserName === "webkit") {
        expect(await columnsHeader.boundingBox()).toHaveProperty("width", 279);
        expect(await column1.boundingBox()).toHaveProperty("width", 179);
      } else {
        expect(await columnsHeader.boundingBox()).toHaveProperty("width", 273);
        expect(await column1.boundingBox()).toHaveProperty("width", 173);
      }
      expect(await column2.boundingBox()).toHaveProperty("width", 100);
    });
  });

  test.describe("Function expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedFunction();
    });

    test("should resize header column", async ({ page, resizing }) => {
      test.skip(true, "https://github.com/kiegroup/kie-issues/issues/179");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/179",
      });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "Edit parameters" });

      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 262);
      expect(await params.boundingBox()).toHaveProperty("width", 262);
    });

    test("should resize the header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "Edit parameters" });
      const literal = page.getByRole("cell", { name: "=" });

      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 262);
      expect(await params.boundingBox()).toHaveProperty("width", 262);
      expect(await literal.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
    });

    test("should change the decision name column and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very big boxed function");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "A very very very big boxed function (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "Edit parameters" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 256);
      expect(await params.boundingBox()).toHaveProperty("width", 256);
      expect(await literal.boundingBox()).toHaveProperty("width", 256);
    });

    test("should create function parameters and resize to fit", async ({ page, resizing }) => {
      test.skip(true, "https://github.com/kiegroup/kie-issues/issues/535");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/535",
      });

      await page.getByText("Edit parameters").click();
      await page.getByRole("button", { name: "Add parameter" }).click();
      await page.getByRole("button", { name: "Add parameter" }).click();
      await page.getByRole("button", { name: "Add parameter" }).click();
      await page.keyboard.press("Escape");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "p-1" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(params);
      expect(await header.boundingBox()).toHaveProperty("width", 355);
      expect(await params.boundingBox()).toHaveProperty("width", 355);
      expect(await literal.boundingBox()).toHaveProperty("width", 355);
    });
  });

  test.describe("Invocation expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedInvocation();
    });

    test("should resize header column", async ({ page, resizing }) => {
      test.skip(true, "https://github.com/kiegroup/kie-issues/issues/179");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/179",
      });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await functionName.boundingBox()).toHaveProperty("width", 382);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
    });

    test("should resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });
      const literal = page.getByRole("cell", { name: "=" });

      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await functionName.boundingBox()).toHaveProperty("width", 382);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
    });

    test("should resize function name column and reset", async ({ page, resizing }) => {
      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });

      await resizing.resizeCell(params, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await functionName.boundingBox()).toHaveProperty("width", 382);
      expect(await params.boundingBox()).toHaveProperty("width", 170);
      await resizing.reset(params);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
    });

    test("should change decision name and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("columnheader", { name: "Expression Name" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very big boxed invocation decision name");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", {
        name: "A very very very big boxed invocation decision name (<Undefined>)",
      });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 364);
      expect(await functionName.boundingBox()).toHaveProperty("width", 364);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 244);
    });

    test("should change function name and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("columnheader", { name: "FUNCTION" }).click();
      await page.keyboard.type("A very very very very big function to invoke");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "A very very very very big function to invoke" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(functionName);
      expect(await header.boundingBox()).toHaveProperty("width", 391);
      expect(await functionName.boundingBox()).toHaveProperty("width", 391);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 271);
    });

    test("should change parameter name and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("cell", { name: "p-1 (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very big parameter");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "A very big parameter (<Undefined>)" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(params);
      expect(await header.boundingBox()).toHaveProperty("width", 365);
      expect(await functionName.boundingBox()).toHaveProperty("width", 365);
      expect(await params.boundingBox()).toHaveProperty("width", 153);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
    });
  });

  test.describe("List expression", () => {
    test.beforeEach(async ({ stories }) => {
      await stories.openBoxedList();
    });

    test("should resize header column", async ({ page, resizing }) => {
      test.skip(true, "https://github.com/kiegroup/kie-issues/issues/179");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/179",
      });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 212);
    });

    test("should resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();
      const literal = page.getByRole("cell", { name: "=" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 262);
      expect(await literal.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
    });

    test("should change list decision name and resize to fit", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");
      test.info().annotations.push({
        type: TestAnnotations.REGRESSION,
        description: "https://github.com/kiegroup/kie-issues/issues/438",
      });

      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
      await page.getByPlaceholder("Expression Name").fill("A very very very big decision list name");
      await page.keyboard.press("Enter");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "A very very very big decision list name (<Undefined>)" });
      const literal = page.getByRole("cell", { name: "=" });

      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await literal.boundingBox()).toHaveProperty("width", 212);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 283);
      expect(await literal.boundingBox()).toHaveProperty("width", 283);
    });
  });
});

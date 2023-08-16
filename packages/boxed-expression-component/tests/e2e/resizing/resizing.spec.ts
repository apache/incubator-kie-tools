import { test, expect } from "../fixtures/boxedExpression";

test.describe("Resizing", () => {
  test.describe("Literal expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openLiteralExpression();
    });

    test("resing the header and reset to default width", async ({ page, resizing }) => {
      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });

      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 240);
      await resizing.reset(header);
      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openContextExpression();
    });

    test("shouldn't resize header column", async ({ page, resizing }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
    });

    test("resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");

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

    test("resize results column and reset", async ({ page, resizing }) => {
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

    test("resize context entry cell and reset", async ({ page, resizing }) => {
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
  });

  test.describe("Decision Table expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openDecisionTableExpression();
    });

    test("resize input column and add new columns", async ({ page, resizing }) => {
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

    test("resize output column and add new columns", async ({ page, resizing }) => {
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
  });

  test.describe("Relation expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openRelationExpression();
    });

    test("resize column and add new columns", async ({ page, resizing }) => {
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
      expect(await column2.boundingBox()).toHaveProperty("width", 100);
    });
  });

  test.describe("Function expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openFunctionExpression();
    });

    test("shouldn't resize header column", async ({ page, resizing }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "Edit parameters" });

      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
    });

    test("resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const params = page.getByRole("columnheader", { name: "Edit parameters" });

      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 262);
      expect(await params.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      expect(await params.boundingBox()).toHaveProperty("width", 212);
    });
  });

  test.describe("Invocation expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openInvocationExpression();
    });

    test("shouldn't resize header column", async ({ page, resizing }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });

      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
    });

    test("resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const functionName = page.getByRole("columnheader", { name: "FUNCTION" });
      const params = page.getByRole("cell", { name: "p-1 (<Undefined>)" });

      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await functionName.boundingBox()).toHaveProperty("width", 382);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await functionName.boundingBox()).toHaveProperty("width", 332);
      expect(await params.boundingBox()).toHaveProperty("width", 120);
    });

    test("resize parameters column and reset", async ({ page, resizing }) => {
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
  });

  test.describe("List expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openListExpression();
    });

    test("shouldn't resize header column", async ({ page, resizing }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      expect(await header.boundingBox()).toHaveProperty("width", 212);
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 212);
    });

    test("resize header column and reset", async ({ page, resizing, browserName }) => {
      test.skip(browserName === "webkit", "https://github.com/kiegroup/kie-issues/issues/438");

      // Requires a nested expression to save the header width
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await resizing.resizeCell(header, { x: 0, y: 0 }, { x: 50, y: 0 });
      expect(await header.boundingBox()).toHaveProperty("width", 262);
      await resizing.reset(header);
      expect(await header.boundingBox()).toHaveProperty("width", 212);
    });
  });
});

import { test, expect } from "../fixtures/boxedExpression";
// import { Page } from "@playwright/test";

// test.describe.configure({ mode: "serial" });

// let page: Page;

// test.beforeAll(async ({ browser }) => {
//   page = await browser.newPage();
//   await page.goto("http://localhost:3015");
// });

// test.afterAll(async () => {
//   await page.close();
// });

test.describe("Resizing Expressions", () => {
  test.afterEach(() => {
    page.reload();
  });

  test.describe("Literal Expression", () => {
    test.beforeEach(async ({ expressionSelector, page }) => {
      await expressionSelector.literalExpression(page);
    });

    test("resing the header and reset to default width", async ({ expressionSelector, resizer, page }) => {
      await expressionSelector.getBee(page).getByRole("columnheader").hover();
      const handle = expressionSelector.getBee(page).getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await expressionSelector.getBee(page).getByRole("columnheader").boundingBox()).toHaveProperty(
        "width",
        240
      );
      await expressionSelector.getBee(page).getByRole("columnheader").hover();
      await resizer.reset(handle);
      expect(await expressionSelector.getBee(page).getByRole("columnheader").boundingBox()).toHaveProperty(
        "width",
        190
      );
    });

    test("resing the textbox and reset to default width", async ({ expressionSelector, resizer, page }) => {
      const target = expressionSelector.getBee(page).getByRole("cell");
      await target.hover();
      const handle = expressionSelector.getBee(page).getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await target.boundingBox()).toHaveProperty("width", 240);
      await expressionSelector.getBee(page).getByRole("cell").hover();
      await resizer.reset(handle);
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context Expression", () => {
    test.beforeEach(async ({ expressionSelector, page }) => {
      await expressionSelector.contextExpression(page);
    });

    // test.afterEach(() => {
    //   page.reload();
    // });

    test.skip("resize header column and reset", async ({ expressionSelector, resizer, page }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = expressionSelector
        .getBee(page)
        .getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await header.hover();
      const handle = expressionSelector
        .getBee(page)
        .getByRole("row", { name: "Expression Name (<Undefined>)" })
        .getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      const firstEntry = expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const secondEntry = expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" });
      const result = expressionSelector.getBee(page).getByRole("cell", { name: "<result>" });

      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await secondEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      await resizer.reset(handle);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await secondEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
    });

    test("resize results column and reset", async ({ expressionSelector, resizer, page }) => {
      await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).hover();
      const handle = expressionSelector.getBee(page).getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).boundingBox()
      ).toHaveProperty("width", 170);
      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 170);
      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 170);
      expect(
        await expressionSelector
          .getBee(page)
          .getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
          .boundingBox()
      ).toHaveProperty("width", 382);

      await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).hover();
      await resizer.reset(handle);
      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "<result>" }).boundingBox()
      ).toHaveProperty("width", 120);
      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 120);
      expect(
        await expressionSelector.getBee(page).getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 120);
      expect(
        await expressionSelector
          .getBee(page)
          .getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
          .boundingBox()
      ).toHaveProperty("width", 332);
    });
  });
});

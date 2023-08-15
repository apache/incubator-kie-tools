import { test, expect } from "../fixtures/boxedExpression";

test.describe("Resizing", () => {
  test.describe("Literal expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openLiteralExpression();
    });

    test("resing the header and reset to default width", async ({ page }) => {
      await page.getByRole("columnheader").hover();
      const handle = page.getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 240);
      await page.getByRole("columnheader").hover();
      await handle.dblclick();
      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 190);
    });

    test("resing the textbox and reset to default width", async ({ page }) => {
      const target = page.getByRole("cell");
      await target.hover();
      const handle = page.getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await target.boundingBox()).toHaveProperty("width", 240);
      await page.getByRole("cell").hover();
      await handle.dblclick();
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context expression", () => {
    test.beforeEach(async ({ expressions }) => {
      await expressions.openContextExpression();
    });

    test("resize header column and reset", async ({ page }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      // it is required to have a nested expression to work
      await page.getByText("Select expression").first().click();
      await page.getByRole("menuitem", { name: "FEEL Literal" }).click();

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await header.hover();
      const handle = page.getByRole("row", { name: "Expression Name (<Undefined>)" }).getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      await handle.dblclick();
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
    });

    test("resize results column and reset", async ({ page }) => {
      const result = page.getByRole("cell", { name: "<result>" });
      await result.hover();
      const handle = page.getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });

      expect(await result.boundingBox()).toHaveProperty("width", 170);
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 170);

      await page.getByRole("cell", { name: "<result>" }).hover();
      await handle.dblclick();
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
    });
  });
});

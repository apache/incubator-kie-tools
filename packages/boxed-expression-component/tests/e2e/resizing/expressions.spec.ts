import { test, expect } from "../fixtures/boxedExpression";

test.describe("Resizing Expressions", () => {
  test.describe("Literal Expression", () => {
    test.beforeEach(async ({ boxedExpressionEditor }) => {
      await boxedExpressionEditor.literalExpression();
    });

    test("resing the header and reset to default width", async ({ page, resizer }) => {
      await page.getByRole("columnheader").hover();
      const handle = page.getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 240);
      await page.getByRole("columnheader").hover();
      await resizer.reset(handle);
      expect(await page.getByRole("columnheader").boundingBox()).toHaveProperty("width", 190);
    });

    test("resing the textbox and reset to default width", async ({ page, resizer }) => {
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
      await resizer.reset(handle);
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context Expression", () => {
    test.beforeEach(async ({ boxedExpressionEditor }) => {
      await boxedExpressionEditor.contextExpression();
    });

    test.skip("resize header column and reset", async ({ page, resizer }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await header.hover();
      const handle = page.getByRole("row", { name: "Expression Name (<Undefined>)" }).getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      const firstEntry = page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const secondEntry = page.getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" });
      const result = page.getByRole("cell", { name: "<result>" });

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

    test("resize results column and reset", async ({ page, resizer }) => {
      await page.getByRole("cell", { name: "<result>" }).hover();
      const handle = page.getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await page.getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty("width", 170);
      expect(await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()).toHaveProperty(
        "width",
        170
      );
      expect(
        await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 382);

      await page.getByRole("cell", { name: "<result>" }).hover();
      await resizer.reset(handle);
      expect(await page.getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty("width", 120);
      expect(await page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()).toHaveProperty(
        "width",
        120
      );
      expect(
        await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 332);
    });
  });
});

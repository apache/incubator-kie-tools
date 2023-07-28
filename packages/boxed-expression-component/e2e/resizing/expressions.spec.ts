import { test, expect } from "../fixtures/boxedExpression";

test.describe("Resizing Expressions", () => {
  test.describe("Literal Expression", () => {
    test.beforeEach(async ({ expressionSelector }) => {
      await expressionSelector.literalExpression();
    });

    test("resing the header and reset", async ({ expressionSelector, resizer }) => {
      const target = expressionSelector.getBee().getByRole("columnheader");
      await target.hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await resizer.drag(handle, 50, 0);

      expect(await target.boundingBox()).toHaveProperty("width", 240);
      await resizer.reset(handle);
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });

    test("resing the textbox and reset", async ({ expressionSelector, resizer }) => {
      const target = expressionSelector.getBee().getByRole("cell");
      await target.hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await resizer.drag(handle, 50, 0);

      expect(await target.boundingBox()).toHaveProperty("width", 240);
      await resizer.reset(handle);
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context Expression", () => {
    test.beforeEach(async ({ expressionSelector }) => {
      await expressionSelector.contextExpression();
    });

    test.skip("resize header column and reset", async ({ expressionSelector, resizer }) => {
      test
        .info()
        .annotations.push({ type: "kie-issue", description: "it's not possible to resize without expressions!!" });

      const header = expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await header.hover();
      const handle = expressionSelector
        .getBee()
        .getByRole("row", { name: "Expression Name (<Undefined>)" })
        .getByTestId("resizer-handle");
      await resizer.drag(handle, 50, 0);

      const firstEntry = expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const secondEntry = expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" });
      const result = expressionSelector.getBee().getByRole("cell", { name: "<result>" });

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

    test("resize results column and reset", async ({ expressionSelector, resizer }) => {
      const result = expressionSelector.getBee().getByRole("cell", { name: "<result>" });
      await result.hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await resizer.drag(handle, 50, 0);

      const firstEntry = expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" });
      const secondEntry = expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" });
      const header = expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" });

      expect(await result.boundingBox()).toHaveProperty("width", 170);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 170);
      expect(await secondEntry.boundingBox()).toHaveProperty("width", 170);
      expect(await header.boundingBox()).toHaveProperty("width", 382);
      await resizer.reset(handle);
      expect(await result.boundingBox()).toHaveProperty("width", 120);
      expect(await firstEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await secondEntry.boundingBox()).toHaveProperty("width", 120);
      expect(await header.boundingBox()).toHaveProperty("width", 332);
    });
  });
});

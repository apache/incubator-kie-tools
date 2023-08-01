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
  test.describe("Literal Expression", () => {
    test.beforeEach(async ({ expressionSelector }) => {
      await expressionSelector.literalExpression();
    });

    test("resing the header and reset to default width", async ({ expressionSelector, resizer }) => {
      await expressionSelector.getBee().getByRole("columnheader").hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await expressionSelector.getBee().getByRole("columnheader").boundingBox()).toHaveProperty("width", 240);
      await expressionSelector.getBee().getByRole("columnheader").hover();
      await resizer.reset(handle);
      expect(await expressionSelector.getBee().getByRole("columnheader").boundingBox()).toHaveProperty("width", 190);
    });

    test("resing the textbox and reset to default width", async ({ expressionSelector, resizer }) => {
      const target = expressionSelector.getBee().getByRole("cell");
      await target.hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await target.boundingBox()).toHaveProperty("width", 240);
      await expressionSelector.getBee().getByRole("cell").hover();
      await resizer.reset(handle);
      expect(await target.boundingBox()).toHaveProperty("width", 190);
    });
  });

  test.describe("Context Expression", () => {
    test.beforeEach(async ({ expressionSelector }) => {
      await expressionSelector.contextExpression();
    });

    // test.afterEach(() => {
    //   page.reload();
    // });

    test.skip("resize header column and reset", async ({ expressionSelector, resizer }) => {
      test.info().annotations.push({ type: "kie-issue", description: "<link>" });

      const header = expressionSelector.getBee().getByRole("columnheader", { name: "Expression Name (<Undefined>)" });
      await header.hover();
      const handle = expressionSelector
        .getBee()
        .getByRole("row", { name: "Expression Name (<Undefined>)" })
        .getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

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
      await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).hover();
      const handle = expressionSelector.getBee().getByTestId("resizer-handle");
      await handle.dragTo(handle, {
        force: true,
        sourcePosition: { x: 0, y: 0 },
        targetPosition: { x: 50, y: 0 },
      });

      expect(await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
        "width",
        170
      );
      expect(
        await expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 170);
      expect(
        await expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 170);
      expect(
        await expressionSelector
          .getBee()
          .getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
          .boundingBox()
      ).toHaveProperty("width", 382);

      await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).hover();
      await resizer.reset(handle);
      expect(await expressionSelector.getBee().getByRole("cell", { name: "<result>" }).boundingBox()).toHaveProperty(
        "width",
        120
      );
      expect(
        await expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 120);
      expect(
        await expressionSelector.getBee().getByRole("cell", { name: "ContextEntry-2 (<Undefined>)" }).boundingBox()
      ).toHaveProperty("width", 120);
      expect(
        await expressionSelector
          .getBee()
          .getByRole("columnheader", { name: "Expression Name (<Undefined>)" })
          .boundingBox()
      ).toHaveProperty("width", 332);
    });
  });
});

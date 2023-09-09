import { test, expect } from "../../__fixtures__/boxedExpression";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Boxed Context context menu", () => {
  test.describe("context entry control", () => {
    test.beforeEach(async ({ expressions, page, monaco, boxedExpressionEditor }) => {
      await expressions.openBoxedContext();
      await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "ContextEntry-1 (<Undefined>)" }));
      await monaco.fill(page.getByTestId("monaco-container"), '"test"');
    });

    test("should't render selection context menu", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "CONTEXT ENTRY" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
    });

    test("shouldn't render context entry context menu", async ({ page }) => {
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click({ button: "right" });
      await expect(page.getByRole("heading", { name: "CONTEXT ENTRY" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");

      await page.getByTestId("monaco-container").click({ button: "right" });
      await expect(page.getByRole("heading", { name: "CONTEXT ENTRY" })).not.toBeAttached();
      await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
      await expect(page.getByRole("heading", { name: "COLUMNS" })).not.toBeAttached();
      await page.keyboard.press("Escape");
    });

    test("should open context entry context menu and insert context entry above", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(1)).toContainText("test");
    });

    test("should open context entry context menu and insert context entry below", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(0)).toContainText("test");
    });

    test("should open context entry context menu and insert multiples context entry above", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(4);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(3)).toContainText("test");
    });

    test("should open context entry context menu and insert multiples context entry below", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(0)).toContainText("test");
    });

    test("should open context entry context menu and delete row", async ({ page }) => {
      await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(1)).toContainText("test");
      await page.getByRole("cell", { name: "ContextEntry-2" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(1);
      await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(0)).toContainText("test");
    });
  });

  test("should reset insert multiples menu when opening another cell context menu", async ({
    expressions,
    page,
    monaco,
  }) => {
    test.skip(true, "https://github.com/kiegroup/kie-issues/issues/421");
    test.info().annotations.push({
      type: TestAnnotations.REGRESSION,
      description: "https://github.com/kiegroup/kie-issues/issues/421",
    });

    await expressions.openRelation();
    await monaco.fill(page.getByTestId("monaco-container"), '"test"');
    await page.getByTestId("monaco-container").click({ button: "right" });
    await page.getByRole("menuitem", { name: "Insert", exact: true }).first().click();
    await page.getByRole("cell", { name: "ContextEntry-1" }).click({ button: "right" });
    await expect(page.getByRole("heading", { name: "CONTEXT ENTRY" })).toBeAttached();
    await expect(page.getByRole("heading", { name: "SELECTION" })).toBeAttached();
  });

  test.describe("Hovering", () => {
    test.beforeEach(async ({ expressions, page, boxedExpressionEditor, monaco }) => {
      await expressions.openBoxedContext();
      await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "ContextEntry-1 (<Undefined>)" }));
      await monaco.fill(page.getByTestId("monaco-container"), '"test"');
    });

    test.describe("Add context entry", () => {
      test("should add context entry above by positioning mouse on the index cell upper section", async ({ page }) => {
        await page.getByRole("cell", { name: "ContextEntry-1" }).hover({ position: { x: 0, y: 0 } });
        await page.getByRole("cell", { name: "ContextEntry-1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
        await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(1)).toContainText("test");
      });

      test("should add context entry below by positioning mouse on the index cell lower section", async ({ page }) => {
        await page.getByRole("cell", { name: "ContextEntry-1" }).hover();
        await page.getByRole("cell", { name: "ContextEntry-1" }).locator("svg").click();
        await expect(page.getByRole("row", { name: "ContextEntry-" })).toHaveCount(2);
        await expect(page.getByRole("row", { name: "ContextEntry-" }).nth(0)).toContainText("test");
      });
    });
  });
});

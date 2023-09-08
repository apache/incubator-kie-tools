import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Boxed List", () => {
  test("should correctly populate boxed list with boxed literal", async ({
    expressions,
    boxedExpressionEditor,
    page,
  }) => {
    await expressions.openBoxedList();
    await boxedExpressionEditor.selectBoxedLiteral();

    await page.getByTestId("monaco-container").nth(0).click();
    await page.keyboard.type(`"test0"`);
    await page.keyboard.press("Enter");
    await expect(page.getByRole("row", { name: "1" })).toBeAttached();
    await expect(page.getByRole("row", { name: "1" })).toContainText("=");
    await expect(page.getByRole("row", { name: "1" })).toContainText("test0");

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-list-populated-literal.png");
  });

  test("should correctly populate boxed relation", async ({ expressions, boxedExpressionEditor, page }) => {
    await expressions.openBoxedList();
    await boxedExpressionEditor.selectRelation();

    await page.getByTestId("monaco-container").nth(0).click();
    await page.keyboard.type(`"test0"`);
    await page.keyboard.press("Enter");
    await expect(page.getByRole("row", { name: "1" })).toBeAttached();
    await expect(page.getByRole("row", { name: "1" })).toContainText("=");
    await expect(page.getByRole("row", { name: "1" })).toContainText("test0");
  });
});

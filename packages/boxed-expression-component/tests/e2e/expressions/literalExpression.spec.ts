import { test, expect } from "../fixtures/boxedExpression";

test.describe("Literal expression", () => {
  test("Check if expression rendered correctly", async ({ boxedExpressionEditor, standaloneExpression, page }) => {
    await standaloneExpression.openLiteralExpression();
    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell")).toHaveCount(1);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("literal-expression.png");
  });
});

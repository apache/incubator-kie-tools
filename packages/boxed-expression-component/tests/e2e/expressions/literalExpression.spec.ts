import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Literal expression", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, expressions, page }) => {
    await expressions.openLiteralExpression();
    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell")).toHaveCount(1);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("literal-expression.png");
  });
});

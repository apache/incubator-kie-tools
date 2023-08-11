import { test, expect } from "../fixtures/boxedExpression";

test.describe("Root Expression", () => {
  test("Check if the root expression renders correctly", async ({ boxedExpressionEditor, page }) => {
    await boxedExpressionEditor.goto();
    await expect(page.getByText("Expression Name (<Undefined>)")).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("root-expression.png");
  });
});

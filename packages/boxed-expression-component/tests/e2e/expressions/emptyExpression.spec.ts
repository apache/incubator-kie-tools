import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Empty Expression", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, page }) => {
    await boxedExpressionEditor.goto();
    await expect(page.getByText("Expression Name (<Undefined>)")).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("root-expression.png");
  });
});

import { test, expect } from "../fixtures/boxedExpression";

test.describe("Function Expression", () => {
  test("Check function expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openFunctionExpression();
    await expect(page.getByText("Expression Name (Function)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

import { test, expect } from "../fixtures/boxedExpression";

test.describe("Literal Expression", () => {
  test("Check literal expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openLiteralExpression();
    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

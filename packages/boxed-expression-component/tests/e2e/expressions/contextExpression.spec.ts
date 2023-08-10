import { test, expect } from "../fixtures/boxedExpression";

test.describe("Context Expression", () => {
  test("Check context expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openContextExpression();
    await expect(page.getByText("Expression Name (Context)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

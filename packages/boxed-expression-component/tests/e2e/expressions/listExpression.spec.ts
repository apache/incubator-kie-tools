import { test, expect } from "../fixtures/boxedExpression";

test.describe("List Expression", () => {
  test("Check list expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openListExpression();
    await expect(page.getByText("Expression Name (List)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

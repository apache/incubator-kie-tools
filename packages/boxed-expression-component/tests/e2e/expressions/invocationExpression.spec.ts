import { test, expect } from "../fixtures/boxedExpression";

test.describe("Invocation Expression", () => {
  test("Check invocation expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openInvocationExpression();
    await expect(page.getByText("Expression Name (Invocation)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

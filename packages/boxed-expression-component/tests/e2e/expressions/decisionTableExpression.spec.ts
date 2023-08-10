import { test, expect } from "../fixtures/boxedExpression";

test.describe("Decision Table Expression", () => {
  test("Check decision table expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openDecisionTableExpression();
    await expect(page.getByText("Expression Name (Decision Table)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

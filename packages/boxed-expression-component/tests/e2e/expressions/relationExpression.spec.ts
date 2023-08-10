import { test, expect } from "../fixtures/boxedExpression";

test.describe("Relation Expression", () => {
  test("Check relation expression screenshot", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
    await expect(page.getByText("Expression Name (Relation)")).toBeAttached();
    await expect(page).toHaveScreenshot();
  });
});

import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Boxed Invocation", () => {
  test("should correctly populate boxed invocation", async ({ expressions, page }) => {
    await expressions.openBoxedInvocation();
    await page.getByTestId("monaco-container").nth(0).click();
    await page.keyboard.type(`"test0"`);
    await page.keyboard.press("Enter");
    await expect(page.getByRole("cell", { name: `"test0"` })).toBeAttached();
  });
});

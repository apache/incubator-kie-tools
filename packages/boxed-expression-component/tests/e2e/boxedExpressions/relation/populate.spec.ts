import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Relation", () => {
  test("should correctly populate relation", async ({ expressions, boxedExpressionEditor, page }) => {
    await expressions.openRelation("bigger");

    // Populate Relation with test(n) values
    let test = 0;
    for (let i = 0; i < 3; i++) {
      await page.getByTestId("monaco-container").nth(test).click();
      for (let j = 0; j < 4; j++) {
        await page.keyboard.type(`"test${test}"`);
        await page.keyboard.press("Tab");
        test++;
      }
    }

    // Check if Relation has test(n) values on each cell
    for (let i = 0; i < 11; i++) {
      await expect(page.getByRole("cell", { name: `"test${i}"` })).toBeAttached();
    }

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-populated.png");
  });
});

import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Relation", () => {
  test("should correctly populate relation", async ({ boxedExpressionEditor, expressions, page }) => {
    await expressions.openRelation("bigger");
    await page.getByTestId("monaco-container").nth(5).click();

    for (let i = 0; i < 4; i++) {
      await page.keyboard.type(`"test${i}"`);
      await page.keyboard.press("Space");
      await page.keyboard.press("Tab");
    }

    // await expect(
    //   page
    //     .getByRole("row", { name: `${i}`, exact: true })
    //     .getByRole("cell")
    //     .nth(j)
    // ).toContainText(`test${i+j-2}`);
    // }
    // for (let i = 1; i < 2; i++) {
    //   for (let j = 1; j < 2; j++) {
    //     await expect(
    //       page
    //         .getByRole("row", { name: `${i}`, exact: true })
    //         .getByRole("cell")
    //         .nth(j)
    //     ).toContainText(`test${i+j-2}`);
    //   }
    // }
  });
});

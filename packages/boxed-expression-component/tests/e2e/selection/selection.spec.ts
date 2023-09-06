import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Cell selection", () => {
  test.beforeEach(async ({ expressions, browserName }) => {
    test.skip(
      browserName !== "chromium",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    await expressions.openRelationExpression();
    await expressions.createRelationExpression();
  });

  test("should select multiple cells and write on cells", async ({ page }) => {
    for (let i = 0; i < 9; i++) {
      await expect(page.getByRole("cell", { name: `test${i}` }).first()).toContainText(`test${i}`);
    }
  });

  test("should select multiple cells and copy/paste", async ({ page, clipboard }) => {
    await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(8));
    await clipboard.copy();
    await page.keyboard.press("Delete");

    for (let i = 1; i < 4; i++) {
      for (let j = 1; j < 4; j++) {
        await expect(
          page
            .getByRole("row", { name: `${i}`, exact: true })
            .getByRole("cell")
            .nth(j)
        ).toContainText("");
      }
    }

    await clipboard.paste();
    for (let i = 0; i < 9; i++) {
      await expect(page.getByRole("cell", { name: `test${i}` }).first()).toContainText(`test${i}`);
    }
  });

  test("should select multiple cells and cut/paste", async ({ page, clipboard }) => {
    await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(8));
    await clipboard.cut();

    for (let i = 1; i < 4; i++) {
      for (let j = 1; j < 4; j++) {
        await expect(
          page
            .getByRole("row", { name: `${i}`, exact: true })
            .getByRole("cell")
            .nth(j)
        ).toContainText("");
      }
    }

    await clipboard.paste();
    for (let i = 0; i < 9; i++) {
      await expect(page.getByRole("cell", { name: `test${i}` }).first()).toContainText(`test${i}`);
    }
  });
});

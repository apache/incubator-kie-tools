import { test, expect } from "../../__fixtures__/boxedExpression";

// TODO: CHECK
test.describe("Cell selection", () => {
  test.describe("Relation selection", () => {
    test.beforeEach(async ({ expressions, page }) => {
      await expressions.openRelationExpression();
      await page.getByTestId("monaco-container").click();
      await page.keyboard.type('"test"');
      await page.keyboard.press("Enter");
    });

    test.describe(() => {
      test.beforeEach(async ({ browserName }) => {
        test.skip(
          browserName !== "chromium",
          "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
        );
      });

      test("should open selection context menu and copy/paste row", async ({ page, clipboard }) => {
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Copy" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await page.getByTestId("monaco-container").click();
        await page.keyboard.press("Delete");
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await clipboard.paste();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });

      test("should open selection context menu and cut row", async ({ page, clipboard }) => {
        await page.getByTestId("monaco-container").click({ button: "right" });
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await page.getByRole("menuitem", { name: "Cut" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await page.getByTestId("monaco-container").click();
        await clipboard.paste();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });

      test("should open selection context menu and paste row", async ({ page, context }) => {
        await context.grantPermissions(["clipboard-read", "clipboard-write"]);
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Cut" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await page.getByTestId("monaco-container").click();
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Paste" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });
    });

    test("should open selection context menu and reset row", async ({ page }) => {
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await page.getByTestId("monaco-container").click({ button: "right" });
      await page.getByRole("menuitem", { name: "Reset" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
    });
  });

  test.beforeEach(async ({ expressions, browserName }) => {
    test.skip(
      browserName !== "chromium",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    await expressions.openRelationExpression();
    await expressions.createRelationExpression();
  });

  test("should select multiple cells and write on them", async ({ page }) => {
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

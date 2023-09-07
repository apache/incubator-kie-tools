import { describe } from "node:test";
import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Selection", () => {
  test.describe("Context menu", () => {
    test.beforeEach(async ({ expressions, page }) => {
      await expressions.openRelation();
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

  describe("Cell selection", () => {
    test("should select multiple cells and write on them", async ({ expressions, page }) => {
      await expressions.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).dragTo(page.getByTestId("monaco-container").nth(10));

      // Write on cells
      for (let i = 0; i < 4; i++) {
        await page.keyboard.type(`"test${i}"`);
        await page.keyboard.press("Space");
        await page.keyboard.press("Tab");
      }

      await expect(page.getByRole("cell", { name: "test0" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test1" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test2" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test3" })).toBeAttached();
    });

    test("should select multiple cells and write on more of them", async ({ expressions, page }) => {
      await expressions.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).dragTo(page.getByTestId("monaco-container").nth(10));

      // Write on cells
      for (let i = 0; i < 9; i++) {
        await page.keyboard.type(`"test${i}"`);
        await page.keyboard.press("Space");
        await page.keyboard.press("Tab");
      }

      await expect(page.getByRole("cell", { name: "test8" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test5" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test6" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "test7" })).toBeAttached();
    });

    test("should select multiple cells and copy/paste", async ({ expressions, page, clipboard, browserName }) => {
      test.skip(
        browserName !== "chromium",
        "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
      );

      await expressions.openRelation("people");
      await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(5));
      await clipboard.copy();
      await page.keyboard.press("Delete");

      // Check if cells are empty
      for (let i = 1; i < 3; i++) {
        for (let j = 1; j < 4; j++) {
          await expect(
            page
              .getByRole("row", { name: `${i}`, exact: true })
              .getByRole("cell")
              .nth(j)
          ).toContainText("");
        }
      }

      await expressions.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).click();
      await clipboard.paste();
      await expect(page.getByRole("cell", { name: "Luiz" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "30" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Brazil" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Tiago" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "29" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "USA" })).toBeAttached();
    });

    test("should select multiple cells and cut/paste", async ({ expressions, page, clipboard }) => {
      await expressions.openRelation("people");
      await page.getByTestId("monaco-container").nth(0).dragTo(page.getByTestId("monaco-container").nth(5));
      await clipboard.cut();

      // Check if cells are empty
      for (let i = 1; i < 3; i++) {
        for (let j = 1; j < 4; j++) {
          await expect(
            page
              .getByRole("row", { name: `${i}`, exact: true })
              .getByRole("cell")
              .nth(j)
          ).toContainText("");
        }
      }

      await expressions.openRelation("bigger");
      await page.getByTestId("monaco-container").nth(5).click();
      await clipboard.paste();
      await expect(page.getByRole("cell", { name: "Luiz" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "30" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Brazil" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "Tiago" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "29" })).toBeAttached();
      await expect(page.getByRole("cell", { name: "USA" })).toBeAttached();
    });
  });
});

import { test, expect } from "../../__fixtures__/base";

test.describe("Selection", () => {
  test.describe("Context menu", () => {
    test.beforeEach(async ({ stories, page, monaco }) => {
      await stories.openRelation();
      await monaco.fill({ monacoParentLocator: page, content: '"test"' });
    });

    test.describe(() => {
      test.beforeAll(({ browserName }) => {
        test.skip(
          browserName !== "chromium",
          "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
        );
      });

      test.beforeEach(async ({ clipboard, context, browserName }) => {
        clipboard.setup(context, browserName);
      });

      test("should use copy from selection context menu", async ({ page, clipboard }) => {
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Copy" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await page.getByTestId("monaco-container").click();
        await page.keyboard.press("Delete");
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await clipboard.paste();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });

      test("should use cut from selection context menu", async ({ page, clipboard }) => {
        await page.getByTestId("monaco-container").click({ button: "right" });
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await page.getByRole("menuitem", { name: "Cut" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await page.getByTestId("monaco-container").click();
        await clipboard.paste();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });

      test("should use copy and paste from selection context menu", async ({ page }) => {
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Copy" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
        await page.getByTestId("monaco-container").click();
        await page.keyboard.press("Delete");
        await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
        await page.getByTestId("monaco-container").click({ button: "right" });
        await page.getByRole("menuitem", { name: "Paste" }).click();
        await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      });

      test("should use cut and paste from selection context menu", async ({ page, context }) => {
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

    test("should use reset from selection context menu", async ({ page }) => {
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await page.getByTestId("monaco-container").click({ button: "right" });
      await page.getByRole("menuitem", { name: "Reset" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).not.toContainText("test");
    });
  });
});

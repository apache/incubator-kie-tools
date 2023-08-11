import { test, expect } from "../fixtures/boxedExpression";

test.describe("Cell context menu", () => {
  test.describe("Rows control", () => {
    test.beforeEach(async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
      await page.getByTestId("monaco-container").click();
      await page.keyboard.type('"test"');
      await page.keyboard.press("Enter");

      test
        .info()
        .annotations.push({ type: "kie-issue", description: "https://github.com/kiegroup/kie-issues/issues/421" });
      // Click on 1 to prevent kie-issues#421;
      await page.getByRole("cell", { name: "1" }).click();
    });

    test("Open row context menu and insert row above", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });

    test("Open row context menu and insert row below", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert below" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("Open row context menu and insert multiples rows above", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "4" })).toContainText("test");
    });

    test("Open row context menu and insert multiples rows below", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Below").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("row", { name: "2" })).toBeAttached();
    });

    test("Open row context menu and delete row", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Insert above" }).click();
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");

      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
    });

    test("Open row context menu and duplicate row", async ({ page }) => {
      await page.getByRole("cell", { name: "1" }).click({ button: "right" });
      await page.getByRole("menuitem", { name: "Duplicate" }).click();
      await expect(page.getByRole("row", { name: "1" }).nth(1)).toContainText("test");
      await expect(page.getByRole("row", { name: "2" })).toContainText("test");
    });
  });

  test.describe("Columns controls", () => {
    test.beforeEach(async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
      await page.getByTestId("monaco-container").click();
      await page.keyboard.type('"test"');
      await page.keyboard.press("Enter");

      test
        .info()
        .annotations.push({ type: "kie-issue", description: "https://github.com/kiegroup/kie-issues/issues/421" });
      // Click on 1 to prevent kie-issues#421;
      await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    });

    test("Open column context menu and insert column right", async ({ page }) => {
      await page.getByTestId("expression-container").getByText("Expression Name").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Insert right" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
    });

    test("Open column context menu and insert column left", async ({ page }) => {
      await page.getByTestId("expression-container").getByText("Expression Name").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Insert left" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");
    });

    test("Open column context menu and insert multiples columns on right", async ({ page }) => {
      await page.getByTestId("expression-container").getByText("Expression Name").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "plus" }).click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(5);
    });

    test("Open column context menu and insert multiples columns on left", async ({ page }) => {
      await page.getByTestId("expression-container").getByText("Expression Name").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Insert", exact: true }).click();
      await page.getByRole("button", { name: "minus" }).click();
      await page.getByLabel("Left").click();
      await page.getByRole("button", { name: "Insert" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(3);
    });

    test("Open column context menu and delete column", async ({ standaloneExpression, page }) => {
      await page.getByTestId("expression-container").getByText("Expression Name").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Insert left" }).click();
      await expect(page.getByRole("cell").nth(2)).toContainText("test");

      await page.getByTestId("expression-container").getByText("column-2").click({
        button: "right",
      });
      await page.getByRole("menuitem", { name: "Delete" }).click();
      await expect(page.getByRole("cell").nth(1)).toContainText("test");
      await expect(page.getByRole("cell")).toHaveCount(2);
    });
  });

  test.describe("Selection context menu", () => {
    // chromium / chrome only;
    test("Open selection context menu and copy row", async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
    });

    // chromium / chrome only;
    test("Open selection context menu and cut row", async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
    });

    // chromium / chrome only;
    test("Open selection context menu and paste row", async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
    });

    test("Open selection context menu and reset row", async ({ standaloneExpression, page }) => {
      await standaloneExpression.openRelationExpression();
    });
  });
});

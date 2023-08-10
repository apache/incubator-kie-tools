import { test, expect } from "../fixtures/boxedExpression";

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });

  test("set expression name and type", async ({ page }) => {
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").click();
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Expression");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByRole("option", { name: "context" }).click();
    await page.getByPlaceholder("Expression Name").press("Enter");

    await expect(page.getByText("My Expression (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "My Expression (context)" })).toBeAttached();
  });

  test("cancel edit expression name and type", async ({ page }) => {
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Expression");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.mouse.wheel(0, 100);
    await page.getByRole("option", { name: "date and time" }).click();
    await page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
  });
});

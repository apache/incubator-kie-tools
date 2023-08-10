import { test, expect } from "../fixtures/boxedExpression";

test.describe("Context Expression", () => {
  test.beforeEach(async ({ standaloneExpression }) => {
    await standaloneExpression.openContextExpression();
  });

  test("set expression name and type", async ({ page }) => {
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").click();
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Expression");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByText("BUILT-IN").hover();
    await page.mouse.wheel(0, 300);
    await page.getByRole("option", { name: "number" }).click();
    await page.getByPlaceholder("Expression Name").press("Enter");

    await expect(page.getByText("My Expression (Context)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "My Expression (number)" })).toBeAttached();
  });

  test("cancel expression name and type", async ({ page }) => {
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").click();
    await page.getByPlaceholder("Expression Name").press("Control+a");
    await page.getByPlaceholder("Expression Name").fill("My Expression");
    await page.getByRole("button", { name: "Options menu" }).click();
    await page.getByText("BUILT-IN").hover();
    await page.mouse.wheel(0, 300);
    await page.getByRole("option", { name: "number" }).click();
    await page.getByRole("button", { name: "Options menu" }).press("Escape");

    await expect(page.getByText("Expression Name (Context)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
  });
});

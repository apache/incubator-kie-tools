import { test, expect } from "../__fixtures__/setup";

test.describe("Boxed expression header menu popover", () => {
  test("should open context menu and change name and type", async ({ stories, page }) => {
    await stories.openBoxedLiteral();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expect(page.getByTestId("expression-container")).toBeAttached();

    await page.getByPlaceholder("Expression Name").fill("Test");
    await expect(page.getByPlaceholder("Expression Name")).toHaveValue("Test");

    await page.getByLabel("<Undefined>").click();
    await page.mouse.wheel(200, 0);
    await page.getByRole("option", { name: "years and months duration" }).click();
    await expect(page.getByLabel("years and months duration")).toBeAttached();
    await page.keyboard.press("Enter");

    await expect(page.getByText("Test (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Test (years and months duration)" })).toBeAttached();
  });

  test("should open context menu and cancel value change of name and type", async ({ stories, page }) => {
    await stories.openBoxedLiteral();

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await expect(page.getByTestId("expression-container")).toBeAttached();

    await page.getByPlaceholder("Expression Name").fill("Test");
    await expect(page.getByPlaceholder("Expression Name")).toHaveValue("Test");

    await page.getByLabel("<Undefined>").click();
    await page.mouse.wheel(200, 0);
    await page.getByRole("option", { name: "years and months duration" }).click();
    await expect(page.getByLabel("years and months duration")).toBeAttached();
    await page.keyboard.press("Escape");

    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
  });
});

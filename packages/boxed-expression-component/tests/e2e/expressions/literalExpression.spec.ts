import { test, expect } from "@playwright/test";

test.beforeAll(async ({ page }) => {
  await page.goto("http://localhost:3015/");
  await page.getByText("Select expression").click();
  await page.getByRole("menuitem", { name: "FEEL Literal" }).click();
});

test("Literal Expression", async ({ page }) => {
  await page.locator(".feel-input > div").click();
  await page.locator(".boxed-expression").press('"');
  await page.locator(".boxed-expression").press("t");
  await page.locator(".boxed-expression").press("e");
  await page.locator(".boxed-expression").press("s");
  await page.locator(".boxed-expression").press("t");
  await page.locator(".boxed-expression").press('"');
  await page.locator(".boxed-expression").press("Enter");

  expect(page.locator(".boxed-expression")).toMatchSnapshot();
  const json = page.locator(".updated-json");

  // type
  await expect(json).toContainText("Literal");

  // nested
  await expect(json).toContainText("false");

  // expression name
  await expect(json).toContainText("Expression Name");

  // value
  await expect(json).toContainText("test");
});

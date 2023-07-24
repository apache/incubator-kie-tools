import { test, expect } from "@playwright/test";

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:3015/");
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "FEEL Literal" }).click();
  });

  test("Literal Expression editing by select context", async ({ page }) => {
    await page.getByTestId("monaco-container").click();
    await page.getByTestId("monaco-container").type("data");
    await page.getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).press("Escape");
    await page.getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).press("Enter");

    const json = page.getByTestId("boxed-expression-json");
    await expect(json).toContainText("Literal");
    await expect(json).toContainText("false");
    await expect(json).toContainText("Expression Name");
    await expect(json).toContainText("data");
  });

  test("Literal Expression editing by double click", async ({ page }) => {
    await page.getByTestId("monaco-container").dblclick();
    await page.getByRole("textbox", { name: "Editor content" }).fill('"data"');
    await page.locator(".boxed-expression").press("Enter");

    const json = page.getByTestId("boxed-expression-json");
    await expect(json).toContainText("Literal");
    await expect(json).toContainText("false");
    await expect(json).toContainText("Expression Name");
    await expect(json).toContainText("data");
  });

  test("Literal Expression change expression name and type", async ({ page }) => {
    const literalExpression = page.getByTestId("literal-expression");

    await literalExpression.getByText("Expression Name").click();
    await literalExpression.getByPlaceholder("Expression Name").press("Control+a");
    await literalExpression.getByPlaceholder("Expression Name").fill("My Expression");
    await literalExpression.getByRole("button", { name: "Options menu" }).click();
    await literalExpression.getByPlaceholder("Choose...").fill("context");
    await literalExpression.getByRole("option", { name: "context" }).click();
    await literalExpression.getByRole("button", { name: "Options menu" }).press("Enter");

    const json = page.locator(".updated-json");
    await expect(json).toContainText("Literal");
    await expect(json).toContainText("context");
    await expect(json).toContainText("My Expression");
  });

  test("Literal Expression cancel expression name and type", async ({ page }) => {
    const literalExpression = page.getByTestId("literal-expression");

    await literalExpression.getByText("Expression Name").click();
    await literalExpression.getByPlaceholder("Expression Name").press("Control+a");
    await literalExpression.getByPlaceholder("Expression Name").fill("My Expression");
    await literalExpression.getByRole("button", { name: "Options menu" }).click();
    await literalExpression.getByPlaceholder("Choose...").fill("string");
    await literalExpression.getByRole("option", { name: "string" }).click();
    await literalExpression.press("Escape");

    const json = page.locator(".updated-json");
    await expect(json).toContainText("Literal");
    await expect(json).toContainText("<Undefined>");
    await expect(json).toContainText("Expression Name");
  });

  test.skip("Literal Expression resizing", async ({ page }) => {
    const literalExpression = page.getByTestId("literal-expression");

    const splitter = literalExpression.locator(".pf-c-drawer__splitter");
    const box = await literalExpression.boundingBox();

    await splitter.dragTo(splitter, { force: true, targetPosition: { x: box!.x + 50, y: box!.y } });

    const nexBox = await literalExpression.boundingBox();
    expect(nexBox?.x).toEqual(box!.x + 50);
    await page.goto("http://localhost:3015/");
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "FEEL Literal" }).click();
    await page.locator(".feel-input > div").click();
    await page.getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).fill("test");
    await page.getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).press("Escape");
    await page.getByRole("textbox", { name: "Editor content;Press Alt+F1 for Accessibility Options." }).press("Enter");
    await page
      .locator("div")
      .filter({ hasText: /^testtest$/ })
      .locator("div")
      .nth(1)
      .click();
    await page.getByText("testtest").press("Escape");
  });
});

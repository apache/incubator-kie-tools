import { test, expect } from "@playwright/test";

test.describe("Literal Expression", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:3015/");
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "FEEL Literal" }).click();
  });

  test("Literal Expression editing by select context", async ({ page }) => {
    await page.locator(".feel-input > div").click();
    await page.locator(".boxed-expression").type('"test"');
    await page.locator(".boxed-expression").press("Enter");
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

  test("Literal Expression editing double click", async ({ page }) => {
    await page.locator(".feel-input > div").click();
    await page.locator(".feel-input > div").dblclick();
    await page.getByRole("textbox", { name: "Editor content" }).fill('"test"');
    await page.locator(".boxed-expression").press("Enter");
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
    // type
    await expect(json).toContainText("Literal");

    // dataType
    await expect(json).toContainText("context");

    // expression name
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
    // type
    await expect(json).toContainText("Literal");

    // dataType
    await expect(json).toContainText("<Undefined>");

    // expression name
    await expect(json).toContainText("Expression Name");
  });

  test.skip("Literal Expression resizing", async ({ page }) => {
    const literalExpression = page.getByTestId("literal-expression");

    const splitter = literalExpression.locator(".pf-c-drawer__splitter");
    const box = await literalExpression.boundingBox();

    await splitter.dragTo(splitter, { force: true, targetPosition: { x: box!.x + 50, y: box!.y } });

    const nexBox = await literalExpression.boundingBox();
    expect(nexBox?.x).toEqual(box!.x + 50);
  });
});

import { test, expect } from "../fixtures/boxedExpression";

test.describe("Logic type selector", () => {
  test.beforeEach(async ({ boxedExpressionEditor }) => {
    await boxedExpressionEditor.goto();
  });

  test("Select literal expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Literal" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("literal-expression.png");
  });

  test("Select context expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Context" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("context-expression.png");
  });

  test("Select decision table expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Decision Table" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("decision-table-expression.png");
  });

  test("Select relation expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Relation" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-expression.png");
  });

  test("Select invocation expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Invocation" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("invocation-expression.png");
  });

  test("Select list expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("list-expression.png");
  });

  test("Shouldn't have function expression on root", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await expect(page.getByRole("menuitem", { name: "Function" })).not.toBeAttached();
  });

  test("Select a nested function expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();

    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Function" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("nested-function-expression.png");
  });
});

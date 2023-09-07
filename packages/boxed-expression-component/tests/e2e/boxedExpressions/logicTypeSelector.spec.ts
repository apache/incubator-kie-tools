import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Logic type selector", () => {
  test.beforeEach(async ({ boxedExpressionEditor }) => {
    await boxedExpressionEditor.goto();
  });

  test("should select literal expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Literal" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("literal-expression.png");
  });

  test("should select context expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Context" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("context-expression.png");
  });

  test("should select decision table expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Decision Table" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("decision-table-expression.png");
  });

  test("should select relation expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Relation" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-expression.png");
  });

  test("should select invocation expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Invocation" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("invocation-expression.png");
  });

  test("should select list expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("list-expression.png");
  });

  test("shouldn't have function expression on root", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await expect(page.getByRole("menuitem", { name: "Function" })).not.toBeAttached();
  });

  test("should select a nested function expression", async ({ page, boxedExpressionEditor }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();

    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Function" }).click();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("nested-function-expression.png");
  });
});

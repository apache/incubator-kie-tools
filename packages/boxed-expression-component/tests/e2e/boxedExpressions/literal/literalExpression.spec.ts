import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Create Boxed Literal", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, stories, page }) => {
    await stories.openBoxedLiteral();
    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell")).toHaveCount(1);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-literal.png");
  });
});

import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Relation expression", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, expressions, page }) => {
    await expressions.openRelationExpression();
    await expect(page.getByText("Expression Name (Relation)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "column-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-expression.png");
  });
});

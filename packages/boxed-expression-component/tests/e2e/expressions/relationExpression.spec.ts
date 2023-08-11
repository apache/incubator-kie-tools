import { test, expect } from "../fixtures/boxedExpression";

test.describe("Relation expression", () => {
  test("Check if expression rendered correctly", async ({ boxedExpressionEditor, standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
    await expect(page.getByText("Expression Name (Relation)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "column-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("relation-expression.png");
  });
});

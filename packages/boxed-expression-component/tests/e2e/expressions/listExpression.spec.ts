import { test, expect } from "../fixtures/boxedExpression";

test.describe("List expression", () => {
  test("Check if expression rendered correctly", async ({ boxedExpressionEditor, standaloneExpression, page }) => {
    await standaloneExpression.openListExpression();
    await expect(page.getByText("Expression Name (List)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("list-expression.png");
  });
});

import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Create Boxed Invocation", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, expressions, page }) => {
    await expressions.openBoxedInvocation();
    await expect(page.getByText("Expression Name (Invocation)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "FUNCTION" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "p-1" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-invocation.png");
  });
});

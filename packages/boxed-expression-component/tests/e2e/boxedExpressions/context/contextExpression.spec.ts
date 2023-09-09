import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Create Boxed Context", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, stories, page }) => {
    await stories.openBoxedContext();
    await expect(page.getByText("Expression Name (Context)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "<result>" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(2);
    await expect(page.getByRole("columnheader")).toHaveCount(1);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-context.png");
  });
});

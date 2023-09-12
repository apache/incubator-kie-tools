import { test, expect } from "../../__fixtures__/base";

test.describe("Create Boxed List", () => {
  test("should render expression correctly", async ({ boxedExpressionEditor, stories, page }) => {
    await stories.openBoxedList();
    await expect(page.getByText("Expression Name (List)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-list.png");
  });
});

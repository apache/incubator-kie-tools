import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Boxed Literal", () => {
  test("should correctly create a can drive boxed literal", async ({
    expressions,
    page,
    boxedExpressionEditor,
    resizing,
    monaco,
  }) => {
    await expressions.openBoxedLiteral();
    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Can drive?");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Enter");

    await monaco.fill(page.getByTestId("monaco-container").nth(0), "Age >= 18 then true else false");

    await resizing.resizeCell(
      page.getByRole("columnheader", { name: "Can drive? (boolean)" }),
      { x: 0, y: 0 },
      { x: 80, y: 0 }
    );

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-literal-can-drive.png");
  });
});

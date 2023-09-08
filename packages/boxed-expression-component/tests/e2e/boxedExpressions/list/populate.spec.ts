import { test, expect } from "../../__fixtures__/boxedExpression";

test.describe("Populate Boxed List", () => {
  test("should correctly create an age group boxed list", async ({
    expressions,
    boxedExpressionEditor,
    page,
    monaco,
  }) => {
    await expressions.openBoxedList();
    await page.getByRole("cell", { name: "1" }).hover();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();
    await page.getByRole("cell", { name: "1" }).locator("svg").click();

    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "1 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "2 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "3 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "4 " }));
    await boxedExpressionEditor.selectBoxedLiteral(page.getByRole("row", { name: "5 " }));

    await page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" }).click();
    await page.getByPlaceholder("Expression Name").fill("Age groups");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "number" }).click();
    await page.keyboard.press("Enter");

    await monaco.fill(page.getByTestId("monaco-container").nth(0), "<18");
    await monaco.fill(page.getByTestId("monaco-container").nth(1), "[18..30)");
    await monaco.fill(page.getByTestId("monaco-container").nth(2), "[30..50)");
    await monaco.fill(page.getByTestId("monaco-container").nth(3), "[50..70)");
    await monaco.fill(page.getByTestId("monaco-container").nth(4), ">=70");

    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("boxed-list-age-group.png");
  });
});

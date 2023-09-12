import { test, expect } from "../__fixtures__/base";

test.describe("Check can drive use case", () => {
  test("should render can drive expression correctly", async ({ useCases, boxedExpressionEditor }) => {
    await useCases.openCanDrive();
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("can-drive-expression.png");
  });
});

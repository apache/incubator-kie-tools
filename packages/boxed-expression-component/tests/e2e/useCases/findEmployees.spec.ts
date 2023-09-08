import { test, expect } from "../__fixtures__/boxedExpression";

test.describe("Check find employees use case", () => {
  test("should render employees expression correctly", async ({ useCases, boxedExpressionEditor }) => {
    await useCases.openFindEmployees("employees");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("employees-expression.png");
  });

  test("should render find by employees expression correctly", async ({ useCases, boxedExpressionEditor }) => {
    await useCases.openFindEmployees("find-by-employees");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("find-by-employees-expression.png");
  });

  test("should render find employees by knowledge expression correctly", async ({
    useCases,
    boxedExpressionEditor,
  }) => {
    await useCases.openFindEmployees("find-employees-by-knowledge");
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("find-employees-by-knowledge-expression.png");
  });
});

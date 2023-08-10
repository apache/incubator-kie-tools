import { test, expect } from "../fixtures/boxedExpression";

test.describe("Expression context menu", () => {
  test("Reset expression", async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });

  test("Copy expression", async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });

  test("Cut expression", async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });
});

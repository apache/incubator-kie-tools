import { test, expect } from "./fixtures/boxedExpression";

test.describe("Selection", () => {
  test("Select multiple cells", async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });

  test("Select multiple cells and delete", async ({ standaloneExpression }) => {
    await standaloneExpression.openLiteralExpression();
  });
});

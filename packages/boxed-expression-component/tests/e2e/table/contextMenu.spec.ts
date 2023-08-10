import { test, expect } from "../fixtures/boxedExpression";

test.describe("Table context menu", () => {
  test("Open table context menu and insert above", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });

  test("Open table context menu and insert below", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });

  test("Open table context menu and insert multiples rows", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });

  test("Open table context menu and insert right", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });

  test("Open table context menu and insert left", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });

  test("Open table context menu and insert multiples columns", async ({ standaloneExpression, page }) => {
    await standaloneExpression.openRelationExpression();
  });
});

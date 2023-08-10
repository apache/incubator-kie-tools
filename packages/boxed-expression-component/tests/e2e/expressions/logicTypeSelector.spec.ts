import { test, expect } from "../fixtures/boxedExpression";

test.describe("Logic type selector", () => {
  test.beforeEach(async ({ boxedExpressionEditor }) => {
    await boxedExpressionEditor.goto();
  });

  test("Select Literal Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Literal" }).click();

    await expect(page.getByText("Expression Name (Literal)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell")).toHaveCount(1);
  });

  test("Select Context Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Context" }).click();

    await expect(page.getByText("Expression Name (Context)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "ContextEntry-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "<result>" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(2);
    await expect(page.getByRole("columnheader")).toHaveCount(1);
    await expect(page.getByRole("cell")).toHaveCount(4);
  });

  test("Select Decision Table Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Decision Table" }).click();

    await expect(page.getByText("Expression Name (Decision Table)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "annotation-1" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(4);
    await expect(page.getByRole("cell")).toHaveCount(4);
  });

  test("Select Relation Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Relation" }).click();

    await expect(page.getByText("Expression Name (Relation)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "column-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(3);
    await expect(page.getByRole("cell")).toHaveCount(2);
  });

  test("Select Invocation Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Invocation" }).click();

    await expect(page.getByText("Expression Name (Invocation)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "FUNCTION" })).toBeAttached();
    await expect(page.getByRole("cell", { name: "p-1" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
  });

  test("Select List Expression", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();

    await expect(page.getByText("Expression Name (List)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "#", exact: true })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(2);
    await expect(page.getByRole("cell")).toHaveCount(2);
  });

  test("Shouldn't have Function Expression on root", async ({ page }) => {
    await page.getByText("Select expression").click();

    await expect(page.getByRole("menuitem", { name: "Function" })).not.toBeAttached();
  });

  test("Select Function Expression from List", async ({ page }) => {
    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "List" }).click();

    await page.getByText("Select expression").click();
    await page.getByRole("menuitem", { name: "Function" }).click();

    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "F", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Edit parameters" })).toBeAttached();
    await expect(page.getByText("Select expression")).toHaveCount(1);
    await expect(page.getByRole("columnheader")).toHaveCount(4);
    await expect(page.getByRole("cell")).toHaveCount(4);
  });
});

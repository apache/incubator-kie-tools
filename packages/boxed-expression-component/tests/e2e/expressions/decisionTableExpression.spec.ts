import { beforeEach, describe } from "node:test";
import { test, expect } from "../fixtures/boxedExpression";

test.describe("Decision table expression", () => {
  test.beforeEach(async ({ standaloneExpression }) => {
    await standaloneExpression.openDecisionTableExpression();
  });

  test("Check if expression rendered correctly", async ({ boxedExpressionEditor, page }) => {
    await expect(page.getByText("Expression Name (Decision Table)")).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "input-1 (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "Expression Name (<Undefined>)" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "annotation-1" })).toBeAttached();
    await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    await expect(page.getByRole("columnheader")).toHaveCount(4);
    await expect(page.getByRole("cell")).toHaveCount(4);
    await expect(boxedExpressionEditor.getContainer()).toHaveScreenshot("decision-table-expression.png");
  });

  test.describe("Hit policy", () => {
    test.beforeEach(async ({ page }) => {
      await page.getByRole("columnheader", { name: "U", exact: true }).click();
    });

    const hitPolicies = [
      ["UNIQUE", "U"],
      ["FIRST", "F"],
      ["PRIORITY", "P"],
      ["ANY", "A"],
      ["COLLECT", "C?"],
      ["RULE ORDER", "R"],
      ["OUTPUT ORDER", "O"],
    ];

    for (const [policy, abreviation] of hitPolicies) {
      test(`Change hit policy to ${policy}`, async ({ page }) => {
        await page.getByRole("menuitem", { name: policy }).hover();
        await expect(page.getByLabel(`${policy}-help`)).toBeAttached();
        await page.getByRole("menuitem", { name: policy }).click();
        await expect(page.getByRole("columnheader", { name: abreviation, exact: true })).toBeAttached();
      });
    }

    test("Change between hit policies", async ({ page }) => {
      await page.getByRole("menuitem", { name: "FIRST" }).click();
      await expect(page.getByRole("columnheader", { name: "F", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "PRIORITY" }).click();
      await expect(page.getByRole("columnheader", { name: "P", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "ANY" }).click();
      await expect(page.getByRole("columnheader", { name: "A", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "COLLECT" }).click();
      await expect(page.getByRole("columnheader", { name: "C?", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "RULE ORDER" }).click();
      await expect(page.getByRole("columnheader", { name: "R", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "OUTPUT ORDER" }).click();
      await expect(page.getByRole("columnheader", { name: "O", exact: true })).toBeAttached();

      await page.getByRole("menuitem", { name: "UNIQUE" }).click();
      await expect(page.getByRole("columnheader", { name: "U", exact: true })).toBeAttached();
    });

    test.describe("Change collect aggregator function", () => {
      const aggregatorFunction = [
        ["<None>", "?"],
        ["SUM", "+"],
        ["COUNT", "#"],
        ["MIN", "<"],
        ["MAX", ">"],
      ];

      for (const [aggregator, abreviation] of aggregatorFunction) {
        test(`Change aggregator to ${aggregator}`, async ({ page }) => {
          await page.getByRole("menuitem", { name: "COLLECT" }).click();
          await page.getByRole("menuitem", { name: aggregator }).hover();
          await expect(page.getByLabel(`${abreviation}-help`)).toBeAttached();
          await page.getByRole("menuitem", { name: aggregator }).click();
          await expect(page.getByRole("columnheader", { name: `C${abreviation}`, exact: true })).toBeAttached();
        });
      }
    });
  });
});

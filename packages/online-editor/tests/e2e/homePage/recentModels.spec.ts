import { test, expect } from "../fixtures/base";

test.describe("Recent models", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/");
    test.slow();
  });

  test("should open single file workspace", async ({ page, kieSandbox }) => {});

  test("should open a file from ephemeral workspace", async ({ page, kieSandbox }) => {});

  test("should open a file from git workspace", async ({ page, kieSandbox }) => {});

  test("should be possible to add a file on folder workspace", async ({ page, kieSandbox }) => {});
});

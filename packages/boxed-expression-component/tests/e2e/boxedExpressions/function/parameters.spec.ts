import { test, expect } from "../../__fixtures__/base";
import { TestAnnotations } from "@kie-tools/playwright-base/annotations";

test.describe("Boxed Function parameters", () => {
  test("should add a function parameter and change its name and type", async ({ stories, page }) => {
    await stories.openBoxedFunction();

    await page.getByRole("columnheader", { name: "Edit parameters" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter Name").fill("name");

    test
      .info()
      .annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/kiegroup/kie-issues/issues/540",
      });
    await page.keyboard.press("Tab");
    await page.getByLabel("<Undefined>").click();
    await page.getByRole("option", { name: "string" }).click();
    await page.keyboard.press("Escape");

    await expect(page.getByRole("columnheader", { name: "(name: (string))" })).toBeAttached();
  });

  test("should multiple function parameter and change its name and type", async ({ stories, page }) => {
    await stories.openBoxedFunction();

    await page.getByRole("columnheader", { name: "Edit parameters" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter Name").nth(0).fill("name");
    await page.getByPlaceholder("Parameter Name").nth(1).fill("age");
    await page.getByPlaceholder("Parameter Name").nth(2).fill("maried");

    test
      .info()
      .annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/kiegroup/kie-issues/issues/540",
      });
    await page.keyboard.press("Tab");
    await page.getByRole("button", { name: "options menu" }).nth(0).click();
    await page.getByRole("option", { name: "string" }).click();
    await page.getByRole("button", { name: "options menu" }).nth(1).click();
    await page.getByRole("option", { name: "number" }).click();
    await page.getByRole("button", { name: "options menu" }).nth(2).click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.keyboard.press("Escape");

    await expect(
      page.getByRole("columnheader", { name: "(name: (string), age: (number), maried: (boolean))" })
    ).toBeAttached();
  });

  test("should add function parameters and delete them", async ({ stories, page }) => {
    await stories.openBoxedFunction();

    await page.getByRole("columnheader", { name: "Edit parameters" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByRole("button", { name: "Add parameter" }).click();
    await page.getByPlaceholder("Parameter Name").nth(0).fill("name");
    await page.getByPlaceholder("Parameter Name").nth(1).fill("age");
    await page.getByPlaceholder("Parameter Name").nth(2).fill("maried");

    test
      .info()
      .annotations.push({
        type: TestAnnotations.WORKAROUND_DUE_TO,
        description: "https://github.com/kiegroup/kie-issues/issues/540",
      });
    await page.keyboard.press("Tab");
    await page.getByRole("button", { name: "options menu" }).nth(0).click();
    await page.getByRole("option", { name: "string" }).click();
    await page.getByRole("button", { name: "options menu" }).nth(1).click();
    await page.getByRole("option", { name: "number" }).click();
    await page.getByRole("button", { name: "options menu" }).nth(2).click();
    await page.getByRole("option", { name: "boolean" }).click();
    await page.getByRole("button", { name: "", exact: true }).nth(0);
    await page.keyboard.press("Escape");

    await expect(page.getByRole("columnheader", { name: "(age: (number), maried: (boolean))" })).toBeAttached();
  });
});

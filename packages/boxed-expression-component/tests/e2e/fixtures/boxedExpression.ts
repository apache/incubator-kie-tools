import { Locator, Page, test as base } from "@playwright/test";

type BoxedExpressionFixtures = {
  bee: BoxedExpression;
};

class BoxedExpression {
  constructor(public page: Page) {
    this.page = page;
  }

  public async selectLiteralExpression(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
    await this.page.getByRole("menuitem", { name: "FEEL Literal" }).click();
  }

  public async selectContextExpression(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
    await this.page.getByRole("menuitem", { name: "{} Context" }).click();
  }

  public getExpression() {
    return this.page.locator(".boxed-expression-provider");
  }
}

export const test = base.extend<BoxedExpressionFixtures>({
  page: async ({ baseURL, page }, use) => {
    await page.goto(baseURL ?? "");
    await use(page);
  },
  bee: async ({ page }, use) => {
    await use(new BoxedExpression(page));
  },
});

export { expect } from "@playwright/test";

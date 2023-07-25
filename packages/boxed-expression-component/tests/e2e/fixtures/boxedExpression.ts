import { Locator, Page, test as base } from "@playwright/test";

type BoxedExpressionFixtures = {
  bee: BoxedExpression;
};

class BoxedExpression {
  constructor(public page: Page) {
    this.page = page;
  }

  public async open() {
    await this.page.goto("http://localhost:3015/");
  }

  public async selectLiteralExpression(from = this.page) {
    await from.getByText("Select expression").click();
    await from.getByRole("menuitem", { name: "FEEL Literal" }).click();
  }

  public async selectContextExpression(from = this.page) {
    await from.getByText("Select expression").click();
    await from.getByRole("menuitem", { name: "{} Context" }).click();
  }

  public getExpression() {
    return this.page.getByTestId("expression-container");
  }

  public getJson() {
    return this.page.getByTestId("boxed-expression-json");
  }
}

export const test = base.extend<BoxedExpressionFixtures>({
  bee: async ({ page }, use) => {
    await use(new BoxedExpression(page));
  },
});

export { expect } from "@playwright/test";

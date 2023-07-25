import { Page, test as base } from "@playwright/test";
import { ExpressionDefinitionBase } from "@kie-tools/boxed-expression-component/dist/api";

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

  public async getJson<T extends ExpressionDefinitionBase>() {
    await this.page.getByTestId("boxed-expression-json").getByText("items").hover();
    await this.page
      .locator("span")
      .filter({ hasText: "items" })
      .getByTitle("Copy to clipboard")
      .getByRole("img")
      .click();
    const clipboard: string = await this.page.evaluate("navigator.clipboard.readText()");
    return JSON.parse(clipboard) as T;
  }
}

export const test = base.extend<BoxedExpressionFixtures>({
  bee: async ({ page }, use) => {
    await use(new BoxedExpression(page));
  },
});

export { expect } from "@playwright/test";

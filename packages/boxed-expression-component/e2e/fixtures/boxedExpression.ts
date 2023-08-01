import { Locator, Page, test as base } from "@playwright/test";

type BoxedExpressionFixtures = {
  expressionSelector: ExpressionSelector;
  resizer: Resizer;
};

class ExpressionSelector {
  constructor(public page: Page) {
    this.page = page;
  }

  public async select(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
  }

  public async literalExpression(from: Page | Locator = this.page) {
    this.select(from);
    await from.getByRole("menuitem", { name: "Literal" }).click();
  }

  public async contextExpression(from: Page | Locator = this.page) {
    this.select(from);
    await from.getByRole("menuitem", { name: "Context" }).click();
  }

  public getBee(page: Page = this.page) {
    return page.locator(".boxed-expression-provider");
  }
}

class Resizer {
  constructor(public page: Page) {
    this.page = page;
  }

  public async reset(handle: Locator) {
    return handle.dblclick();
  }
}

export const test = base.extend<BoxedExpressionFixtures>({
  // page: async ({ baseURL, page }, use) => {
  //   await page.goto(baseURL ?? "");
  //   await use(page);
  // },
  expressionSelector: async ({ page }, use) => {
    await use(new ExpressionSelector(page));
  },
  resizer: async ({ page }, use) => {
    await use(new Resizer(page));
  },
});

export { expect } from "@playwright/test";

import { Locator, Page, test as base } from "@playwright/test";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  standaloneExpression: StandaloneExpression;
  resizer: Resizer;
};

class StandaloneExpression {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public async openLiteralExpression() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--literal-expression` ?? "");
  }

  public async openContextExpression() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--context-expression` ?? "");
  }
}

class BoxedExpressionEditor {
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
  boxedExpressionEditor: async ({ page, baseURL }, use) => {
    await page.goto(`${baseURL}/iframe.html?id=expressions-boxedexpressioneditor--empty-expression` ?? "");
    await use(new BoxedExpressionEditor(page));
  },
  standaloneExpression: async ({ page, baseURL }, use) => {
    await use(new StandaloneExpression(page, baseURL));
  },
  resizer: async ({ page }, use) => {
    await use(new Resizer(page));
  },
});

export { expect } from "@playwright/test";

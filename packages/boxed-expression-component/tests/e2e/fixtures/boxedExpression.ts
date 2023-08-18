import { Locator, Page, test as base } from "@playwright/test";
import { Clipboard } from "./clipboard";
import { Expressions } from "./expression";
import { Resizing } from "./resizing";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  expressions: Expressions;
  clipboard: Clipboard;
  resizing: Resizing;
};

class BoxedExpressionEditor {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
  }

  public async select(from: Page | Locator = this.page) {
    await from.getByText("Select expression").click();
  }

  public async selectLiteralExpression(from: Page | Locator = this.page) {
    this.select(from);
    await from.getByRole("menuitem", { name: "Literal" }).click();
  }

  public async selectContextExpression(from: Page | Locator = this.page) {
    this.select(from);
    await from.getByRole("menuitem", { name: "Context" }).click();
  }

  public async goto() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-empty-expression--base` ?? "");
  }

  public getContainer() {
    return this.page.locator(".boxed-expression-provider");
  }
}

export const test = base.extend<BoxedExpressionFixtures>({
  boxedExpressionEditor: async ({ page, baseURL }, use) => {
    await use(new BoxedExpressionEditor(page, baseURL));
  },
  expressions: async ({ page, baseURL }, use) => {
    await use(new Expressions(page, baseURL));
  },
  clipboard: async ({ browserName, context, page }, use) => {
    const clipboard = new Clipboard(page);
    clipboard.setup(context, browserName);
    await use(clipboard);
  },
  resizing: async ({ page }, use) => {
    await use(new Resizing(page));
  },
});

export { expect } from "@playwright/test";

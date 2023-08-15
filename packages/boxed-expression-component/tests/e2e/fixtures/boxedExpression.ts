import { Locator, Page, test as base } from "@playwright/test";
import { Clipboard } from "./clipboard";
import { Expressions } from "./expression";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  expressions: Expressions;
  clipboard: Clipboard;
  sleep: (timeout: number) => Promise<true>;
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
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--root-expression` ?? "");
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
    if (browserName === "chromium") {
      await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    }
    await use(new Clipboard(page));
  },
});

export { expect } from "@playwright/test";

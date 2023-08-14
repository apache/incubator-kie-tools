import { Locator, Page, test as base } from "@playwright/test";
import { Clipboard } from "./clipboard";

type BoxedExpressionFixtures = {
  boxedExpressionEditor: BoxedExpressionEditor;
  standaloneExpression: StandaloneExpression;
  resizer: Resizer;
  clipboard: Clipboard;
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

  public async openDecisionTableExpression() {
    await this.page.goto(
      `${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--decision-table-expression` ?? ""
    );
  }

  public async openRelationExpression() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--relation-expression` ?? "");
  }

  public async openInvocationExpression() {
    await this.page.goto(
      `${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--invocation-expression` ?? ""
    );
  }

  public async openListExpression() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--list-expression` ?? "");
  }

  public async openFunctionExpression() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--function-expression` ?? "");
  }
}

class BoxedExpressionEditor {
  constructor(public page: Page, public baseURL?: string) {
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

  public async goto() {
    await this.page.goto(`${this.baseURL}/iframe.html?id=expressions-boxedexpressioneditor--root-expression` ?? "");
  }

  public getContainer() {
    return this.page.locator(".boxed-expression-provider");
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
    await use(new BoxedExpressionEditor(page, baseURL));
  },
  standaloneExpression: async ({ page, baseURL }, use) => {
    await use(new StandaloneExpression(page, baseURL));
  },
  resizer: async ({ page }, use) => {
    await use(new Resizer(page));
  },
  clipboard: async ({ browserName, context, page }, use) => {
    if (browserName === "chromium") {
      await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    }
    await use(new Clipboard(page));
  },
});

export { expect } from "@playwright/test";

import { Page } from "@playwright/test";

export class Expressions {
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

  public async createRelationExpression() {
    // create 3x3 relation
    await this.page.getByTestId("monaco-container").click({ button: "right" });
    await this.page.getByRole("menuitem", { name: "Insert", exact: true }).nth(0).click();
    await this.page.getByRole("button", { name: "Insert" }).click();
    await this.page.getByTestId("monaco-container").nth(0).click({ button: "right" });
    await this.page.getByRole("menuitem", { name: "Insert", exact: true }).nth(1).click();
    await this.page.getByLabel("Below").click();
    await this.page.getByRole("button", { name: "Insert" }).click();

    // populate
    await this.page.getByTestId("monaco-container").nth(0).dragTo(this.page.getByTestId("monaco-container").nth(8));
    for (let i = 0; i < 9; i++) {
      await this.page.keyboard.type(`"test${i}"`);
      await this.page.keyboard.press("Space");
      await this.page.keyboard.press("Tab");
      // required
      await new Promise<true>((res) => setTimeout(() => res(true), 50));
    }
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

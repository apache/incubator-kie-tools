import { Page } from "@playwright/test";

export class Selection {
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

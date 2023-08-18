import { Page } from "@playwright/test";

export class Expressions {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openLiteralExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-literal-expression--base")}` ?? "");
  }

  public async openContextExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-context-expression--base")}` ?? "");
  }

  public async openDecisionTableExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-decision-table-expression--base")}` ?? "");
  }

  public async openRelationExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-relation-expression--base")}` ?? "");
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
      await this.page.waitForTimeout(50);
    }
  }

  public async openInvocationExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-invocation-expression--base")}` ?? "");
  }

  public async openListExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-list-expression--base")}` ?? "");
  }

  public async openFunctionExpression() {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL("expressions-function-expression--base")}` ?? "");
  }
}

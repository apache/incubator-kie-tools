import { Page } from "@playwright/test";

type BoxedExpressionTypes = "base" | "nested";

export class Stories {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
    this.baseURL = baseURL;
  }

  public getIframeURL(iframeId: string) {
    return `iframe.html?id=${iframeId}&viewMode=story`;
  }

  public async openBoxedContext(type: BoxedExpressionTypes | "installment-calculation" | "customer" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-context--${type}`)}` ?? "");
  }

  public async openDecisionTable(type: BoxedExpressionTypes | "discount" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-decision-table--${type}`)}` ?? "");
  }

  public async openBoxedFunction(type: BoxedExpressionTypes | "installment-calculation" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-function--${type}`)}` ?? "");
  }

  public async openBoxedInvocation(type: BoxedExpressionTypes | "monthly-installment" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-invocation--${type}`)}` ?? "");
  }

  public async openBoxedList(type: BoxedExpressionTypes | "age-groups" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-list--${type}`)}` ?? "");
  }

  public async openBoxedLiteral(type: BoxedExpressionTypes | "can-drive" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-literal--${type}`)}` ?? "");
  }

  public async openRelation(type: BoxedExpressionTypes | "bigger" | "people" = "base") {
    await this.page.goto(`${this.baseURL}/${this.getIframeURL(`boxed-expressions-relation--${type}`)}` ?? "");
  }
}

import { Page, Locator } from "@playwright/test";

export class Monaco {
  constructor(public page: Page) {}

  public async fill(target: Locator, content: string) {
    await target.dblclick();
    await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(content);
    await this.page.keyboard.press("Home");
    await this.page.keyboard.press("Enter");
  }
}

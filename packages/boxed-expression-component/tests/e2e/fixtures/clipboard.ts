import { Locator, Page, test as base } from "@playwright/test";

export class Clipboard {
  constructor(public page: Page) {}

  public async copy() {
    const modifier = process.platform === "darwin" ? "Meta" : "Control";
    await this.page.keyboard.press(`${modifier}+KeyC`);
  }

  public async paste() {
    const modifier = process.platform === "darwin" ? "Meta" : "Control";
    await this.page.keyboard.press(`${modifier}+KeyV`);
  }
}

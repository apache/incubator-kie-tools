import { BrowserContext, Page } from "@playwright/test";

export class Clipboard {
  constructor(public page: Page) {}

  public async copy() {
    const modifier = process.platform === "darwin" ? "Meta" : "Control";
    await this.page.keyboard.press(`${modifier}+KeyC`);
  }

  public async cut() {
    const modifier = process.platform === "darwin" ? "Meta" : "Control";
    await this.page.keyboard.press(`${modifier}+KeyX`);
  }

  public async paste() {
    const modifier = process.platform === "darwin" ? "Meta" : "Control";
    await this.page.keyboard.press(`${modifier}+KeyV`);
  }

  public async setup(context: BrowserContext, browserName: string) {
    if (browserName === "chromium") {
      await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    }
  }

  public use() {}
}

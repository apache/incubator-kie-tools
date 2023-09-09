import { Page, Locator } from "@playwright/test";

export class Monaco {
  constructor(public page: Page) {}

  public async fill(args: { monacoParentLocator: Locator | Page; content: string; nth?: number }) {
    if (args.nth !== undefined) {
      await args.monacoParentLocator.getByTestId("monaco-container").nth(args.nth).dblclick();
    } else {
      await args.monacoParentLocator.getByTestId("monaco-container").dblclick();
    }
    // FEEL text input selector when the monaco editor is selected.
    await this.page.getByLabel("Editor content;Press Alt+F1 for Accessibility Options.").fill(args.content);
    await this.page.keyboard.press("Home");
    await this.page.keyboard.press("Enter");
  }
}

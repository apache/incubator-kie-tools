import { Locator, Page, test as base } from "@playwright/test";
import { Upload } from "./upload";

type BaseFixtures = {
  onlineEditor: OnlineEditor;
  upload: Upload;
};

class OnlineEditor {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
  }

  public getEditor() {
    return this.page.frameLocator("#kogito-iframe");
  }
}

export const test = base.extend<BaseFixtures>({
  onlineEditor: async ({ page, baseURL }, use) => {
    await use(new OnlineEditor(page, baseURL));
  },
  upload: async ({ page }, use) => {
    await use(new Upload(page));
  },
});

export { expect } from "@playwright/test";

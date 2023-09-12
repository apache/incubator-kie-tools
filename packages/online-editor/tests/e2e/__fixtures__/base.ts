import { Page, test as base } from "@playwright/test";
import { Upload } from "./upload";

type BaseFixtures = {
  kieSandbox: KieSandbox;
  upload: Upload;
};

class KieSandbox {
  constructor(public page: Page, public baseURL?: string) {
    this.page = page;
  }

  public getEditor() {
    return this.page.frameLocator("#kogito-iframe");
  }
}

export const test = base.extend<BaseFixtures>({
  kieSandbox: async ({ page, baseURL }, use) => {
    await use(new KieSandbox(page, baseURL));
  },
  upload: async ({ page }, use) => {
    await use(new Upload(page));
  },
});

export { expect } from "@playwright/test";

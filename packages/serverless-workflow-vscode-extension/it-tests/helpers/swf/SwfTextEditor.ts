import { sleep } from "@kie-tools/vscode-extension-common-test-helpers";
import { expect } from "chai";
import { TextEditor } from "vscode-extension-tester";

export default class SwfTextEditor extends TextEditor {
  constructor() {
    super();
  }

  public async selectFromContentAssist(value: string): Promise<void> {
    try {
      const contentAssist = await this.toggleContentAssist(true);
      const item = await contentAssist?.getItem(value);
      await sleep(1000);
      expect(await item?.getLabel()).contain(value);
      await item?.click();
    } catch (e) {
      throw new Error(
        `The ContentAssist menu is not available or it was not possible to select the element '${value}'!`
      );
    }
  }
}

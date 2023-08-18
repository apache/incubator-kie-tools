import { Page, Locator } from "@playwright/test";
import { readFileSync } from "fs";
import * as path from "path";

export class Upload {
  constructor(public page: Page) {}

  /**
   * Drags and drop a file that is present in the "files" folder
   * @param locator drag and drop zone
   * @param fileName the file name with its extension
   */
  public async dragAndDropFile(locator: Locator, fileName: string) {
    const fileType = fileName.split(".").slice(1).join(".");
    const filePath = path.join(__dirname, `../files/${fileName}`);

    const buffer = readFileSync(filePath).toString("base64");

    const dataTransfer = await this.page.evaluateHandle(
      async ({ bufferData, localFileName, localFileType }) => {
        const dt = new DataTransfer();

        const blobData = await fetch(bufferData).then((res) => res.blob());

        const file = new File([blobData], localFileName, { type: localFileType });
        dt.items.add(file);
        return dt;
      },
      {
        bufferData: `data:application/octet-stream;base64,${buffer}`,
        localFileName: fileName,
        localFileType: fileType,
      }
    );

    await locator.dispatchEvent("drop", { dataTransfer });
  }

  public async fileSelector(uploadLocator: Locator, fileName: string) {
    const filePath = path.join(__dirname, `../files/${fileName}`);

    const fileChooserPromise = this.page.waitForEvent("filechooser");
    await uploadLocator.click();
    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles(filePath);
  }
}

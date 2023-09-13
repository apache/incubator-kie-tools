/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

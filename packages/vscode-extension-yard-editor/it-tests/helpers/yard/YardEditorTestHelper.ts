/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { WebElement } from "selenium-webdriver";
import { By, WebView } from "vscode-extension-tester";

/**
 * Helper class to support work with yard editor webview.
 * Make sure you switch to the webview's frame before creating and instance via constructor
 */
export default class YardEditorTestHelper {
  constructor(private readonly webview: WebView) {}

  public async getYardTabElements(): Promise<WebElement[]> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElements(
      By.xpath(".//div[@aria-label='yard menu tabs']/ul/li/button/span")
    );
    await this.switchBack();
    return Promise.resolve(result);
  }

  private async switchToEditorFrame(): Promise<void> {
    const driver = this.webview.getDriver();
    await driver.switchTo().frame(await driver.findElement(By.className("webview ready")));
    await driver.switchTo().frame(await driver.findElement(By.id("active-frame")));
  }

  private async switchBack(): Promise<void> {
    await this.webview.getDriver().switchTo().frame(null);
  }
}

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import { By, WebElement, WebView } from "vscode-extension-tester";

/**
 * Helper class to easen work with Dashbuilder editor inside of a webview.
 */
export default class DashbuilderEditorTestHelper {
  constructor(private readonly webview: WebView) {}

  public async getDashbuilderRenderedContent(): Promise<WebElement> {
    const result = await this.webview.findWebElement(By.xpath("//*[@id='mainContainer']"));
    return Promise.resolve(result);
  }

  public async getEmptyContentView(): Promise<WebElement> {
    const result = await this.webview.findWebElement(By.xpath("//*[@id='emptyImport']"));
    return Promise.resolve(result);
  }

  /**
   * Helper method that needs to be called when Dashbuilder editor is being interacted with
   */
  public async switchToEditorFrame(): Promise<void> {
    const driver = this.webview.getDriver();
    await driver.switchTo().frame(await driver.findElement(By.className("webview ready")));
    await driver.switchTo().frame(await driver.findElement(By.id("active-frame")));
  }

  /**
   * Helper method to switch back from Dashbuilder editor to Text editor
   */
  public async switchBack(): Promise<void> {
    await this.webview.getDriver().switchTo().frame(null);
  }
}

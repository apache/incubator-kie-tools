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
 * Helper class to easen work with SWF editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class SwfEditorTestHelper {
  constructor(private readonly webview: WebView) {}

  public async getAllNodeIds(): Promise<string[]> {
    await this.switchToEditorFrame();
    const driver = this.webview.getDriver();
    const nodeIds = (await driver.executeScript("return window.frames.canvas.getNodeIds()")) as string[];
    await this.switchBack();
    return Promise.resolve(nodeIds);
  }

  public async getWorkbenchPanelViewElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='qe-static-workbench-panel-view']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getCanvasPanelElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='canvas-panel']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getAllNodesInMermaidDiagram(): Promise<WebElement[]> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElements(
      By.xpath(".//*[name()='svg']//*[name()='g' and (@class='node statediagram-state' or @class='node default')]")
    );
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getSvgElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//*[name()='svg']"));
    await this.switchBack();
    return Promise.resolve(result);
  }

  public async getMermaidDivElement(): Promise<WebElement> {
    await this.switchToEditorFrame();
    const result = await this.webview.findWebElement(By.xpath(".//div[@class='mermaid']"));
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

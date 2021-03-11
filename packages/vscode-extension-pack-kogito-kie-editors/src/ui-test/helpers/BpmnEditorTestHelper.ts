/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { assertWebElementIsDisplayedEnabled } from "./CommonAsserts";
import { expandedDocksBarE, h3ComponentWithText } from "./CommonLocators";

/**
 * Helper class to easen work with BPMN editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class BpmnEditorTestHelper {
  /**
   * WebView in whitch the editor Iframe is located.
   * Initialize in constructor.
   */
  private webview: WebView;

  constructor(webview: WebView) {
    this.webview = webview;
  }

  /**
   * Finds BPMN diagram properties element. Clicking it opens/closes properties panel.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram properties element.
   */
  public getDiagramProperties = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(
      By.className("docks-item-E-DiagramEditorPropertiesScreen")
    );
  };

  /**
   * Finds BPMN diagram explorer element. Clicking it opens/closes explorer panel.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram explorer element.
   */
  public getDiagramExplorer = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(
      By.className("docks-item-E-ProjectDiagramExplorerScreen")
    );
  };

  /**
   * Finds BPMN diagram palette element. Clicking it opens/closes the palette.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram palette element.
   */
  public getPalette = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(By.className("kie-palette"));
  };

  /**
   * Opens diagram properties panel
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public openDiagramProperties = async (): Promise<WebElement> => {
    const properties = await this.getDiagramProperties();
    await assertWebElementIsDisplayedEnabled(properties);
    await properties.click();
    const expandedPropertiesPanel = await this.webview.findWebElement(
      expandedDocksBarE()
    );
    await assertWebElementIsDisplayedEnabled(
      await properties.findElement(By.xpath(h3ComponentWithText("Properties")))
    );
    await assertWebElementIsDisplayedEnabled(expandedPropertiesPanel);
    return expandedPropertiesPanel;
  };

  /**
   * Opens diagram explorer panel.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public openDiagramExplorer = async (): Promise<WebElement> => {
    const explorer = await this.getDiagramExplorer();
    await assertWebElementIsDisplayedEnabled(explorer);
    await explorer.click();
    const expandedExplorerPanel = await this.webview.findWebElement(
      expandedDocksBarE()
    );
    await assertWebElementIsDisplayedEnabled(
      await explorer.findElement(
        By.xpath(h3ComponentWithText("Explore Diagram"))
      )
    );
    await assertWebElementIsDisplayedEnabled(expandedExplorerPanel);
    return expandedExplorerPanel;
  };
}

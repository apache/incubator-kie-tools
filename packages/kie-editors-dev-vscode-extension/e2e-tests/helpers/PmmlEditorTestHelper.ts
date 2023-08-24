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
import { assertWebElementIsDisplayedEnabled, EditorTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";

export class Modal {
  constructor(readonly webview: WebElement, private closeButton: WebElement) {}

  public close = () => {
    this.closeButton.click();
  };
}

/**
 * Helper class to ease work with PMML editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class PmmlEditorTestHelper extends EditorTestHelper {
  private dataDictionaryButton: WebElement;
  private miningSchemaButton: WebElement;
  private outputsButton: WebElement;

  /**
   *
   * @param webview WebView where the editor Iframe is located.
   */
  constructor(webview: WebView) {
    super(webview);
  }

  /**
   * Opens Data Dictionary modal.
   *
   * Verifies the button is displayed and enabled.
   *
   * @returns a promise resolving to WebElement of opened modal.
   */
  public openDataDictionary = async (): Promise<Modal> => {
    const button = await this.getDataDictionaryButton();
    await assertWebElementIsDisplayedEnabled(button);
    await button.click();

    return this.resolveModal("DataDictionaryModal");
  };

  /**
   * Finds button that open PMML Data Dictionary modal.
   *
   * @returns Promise<WebElement> promise that resolves to PMML Data Dictionary button.
   */
  private getDataDictionaryButton = async (): Promise<WebElement> => {
    this.dataDictionaryButton = await this.webview.findWebElement(By.xpath("//button[@data-title='DataDictionary']"));
    return this.dataDictionaryButton;
  };

  /**
   * Opens Mining Schema modal.
   *
   * Verifies the button is displayed and enabled.
   *
   * @returns a promise resolving to WebElement of opened modal.
   */
  public openMiningSchema = async (): Promise<Modal> => {
    const button = await this.getMiningSchemaButton();
    await assertWebElementIsDisplayedEnabled(button);
    await button.click();

    return this.resolveModal("MiningSchemaModal");
  };

  /**
   * Finds button that open PMML Mining Schema modal.
   *
   * @returns Promise<WebElement> promise that resolves to PMML Mining Schema button.
   */
  private getMiningSchemaButton = async (): Promise<WebElement> => {
    this.miningSchemaButton = await this.webview.findWebElement(By.xpath("//button[@data-title='MiningSchema']"));
    return this.miningSchemaButton;
  };

  /**
   * Opens Outputs modal.
   *
   * Verifies the button is displayed and enabled.
   *
   * @returns a promise resolving to WebElement of opened modal.
   */
  public openOutputs = async (): Promise<Modal> => {
    const button = await this.getOutputsButton();
    await assertWebElementIsDisplayedEnabled(button);
    await button.click();

    return this.resolveModal("OutputsModal");
  };

  /**
   * Finds button that open PMML Outputs modal.
   *
   * @returns Promise<WebElement> promise that resolves to PMML Outputs button.
   */
  private getOutputsButton = async (): Promise<WebElement> => {
    this.outputsButton = await this.webview.findWebElement(By.xpath("//button[@data-title='Outputs']"));
    return this.outputsButton;
  };

  private resolveModal = async (dataTitle: string): Promise<Modal> => {
    const body = await this.webview.findWebElement(By.xpath(`//div[@data-title='${dataTitle}']`));
    const close = await this.webview.findWebElement(By.xpath(`//button[@data-title='${dataTitle}Close']`));
    return Promise.resolve(new Modal(body, close));
  };
}

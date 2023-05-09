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
import {
  assertWebElementIsDisplayedEnabled,
  EditorTestHelper,
  sleep,
} from "@kie-tools/vscode-extension-common-test-helpers";
import { expandedDocksBarE, h3ComponentWithText } from "../CommonLocators";
import { assert } from "chai";
import PropertiesPanelHelper from "../bpmn/PropertiesPanelHelper";
import DiagramExplorerHelper from "../bpmn/DiagramExplorerHelper";

export enum PaletteCategories {
  START_EVENTS = "Start Events",
  INTERMEDIATE_EVENTS = "Intermediate Events",
  END_EVENTS = "End Events",
  ACTIVITIES = "Activities",
  SUBPROCESSES = "SubProcesses",
  GATEWAYS = "Gateways",
  CONTAINERS = "Containers",
  ARTIFACTS = "Artifacts",
  CUSTOM_TASKS = "Custom Tasks",
}

/**
 * Helper class to easen work with BPMN editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class BpmnEditorTestHelper extends EditorTestHelper {
  properties: WebElement;
  diagramExplorer: WebElement;
  palette: WebElement;

  /**
   *
   * @param webview WebView where the editor Iframe is located.
   */
  constructor(webview: WebView) {
    super(webview);
  }

  /**
   * Finds BPMN diagram properties element. Clicking it opens/closes properties panel.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram properties element.
   */
  public async getDiagramProperties(): Promise<WebElement> {
    this.properties = await this.webview.findWebElement(By.className("docks-item-E-DiagramEditorPropertiesScreen"));
    return this.properties;
  }

  /**
   * Finds BPMN diagram explorer element. Clicking it opens/closes explorer panel.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram explorer element.
   */
  public async getDiagramExplorer(): Promise<WebElement> {
    this.diagramExplorer = await this.webview.findWebElement(By.className("docks-item-E-ProjectDiagramExplorerScreen"));
    return this.diagramExplorer;
  }

  /**
   * Finds BPMN diagram palette element. Clicking it opens/closes the palette.
   *
   * @returns Promise<WebElement> promise that resolves to BPMN diagram palette element.
   */
  public async getPalette(): Promise<WebElement> {
    this.palette = await this.webview.findWebElement(By.className("kie-palette"));
    return this.palette;
  }

  /**
   * Opens diagram properties panel
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public async openDiagramProperties(): Promise<PropertiesPanelHelper> {
    const properties = await this.getDiagramProperties();
    await assertWebElementIsDisplayedEnabled(properties);
    await properties.click();
    const expandedPropertiesPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(await properties.findElement(By.xpath(h3ComponentWithText("Properties"))));
    await assertWebElementIsDisplayedEnabled(expandedPropertiesPanel);
    return new PropertiesPanelHelper(expandedPropertiesPanel);
  }

  /**
   * Opens diagram explorer panel.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public async openDiagramExplorer(): Promise<DiagramExplorerHelper> {
    const explorer = await this.getDiagramExplorer();
    await assertWebElementIsDisplayedEnabled(explorer);
    await explorer.click();
    const expandedExplorerPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(
      await explorer.findElement(By.xpath(h3ComponentWithText("Explore Diagram")))
    );
    await assertWebElementIsDisplayedEnabled(expandedExplorerPanel);
    return new DiagramExplorerHelper(expandedExplorerPanel);
  }

  /**
   * Opens the palette of BPMN editor with category specified in argument.
   * Categories are listed in {@see PaletteCategories}
   * Function will always check if there is expected number of categories before clicking the target one.
   *
   * @param categoryToOpen use one of PaletteCategories values
   * @returns root WebElement of openned palette flyout, can be used to query offered nodes and click them
   */
  public async openDiagramPalette(categoryToOpen: PaletteCategories): Promise<WebElement> {
    const palette = await this.getPalette();
    await assertWebElementIsDisplayedEnabled(palette);
    const paletteElements = await palette.findElements(By.xpath("//button[@data-field='categoryIcon' and @title]"));
    assert.lengthOf(paletteElements, 9); // there should always be 9 categories

    const categories = Object.values(PaletteCategories);
    let title: string;
    for (let i = 0; i < paletteElements.length; i++) {
      title = await paletteElements[i].getAttribute("title");
      assert.equal(
        title,
        categories[i],
        "Unexpected palette category on index: [" + i + "]. Expected [" + categories[i] + "], but got [" + title + "]."
      );
      if (title == categoryToOpen) {
        await paletteElements[i].click();
        this.palette = await palette.findElement(By.className("kie-palette-flyout"));
        return this.palette;
      }
    }

    throw Error("Palette category: [" + categoryToOpen + "] not found.");
  }

  public async scrollElementIntoView(element: WebElement): Promise<void> {
    const driver = element.getDriver();
    await sleep(500);
    await driver.executeScript("arguments[0].scrollIntoView({ behavior: 'auto', block: 'end'});", element);
  }
}

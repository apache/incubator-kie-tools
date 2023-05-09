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

import { expect } from "chai";
import { By, until, WebElement, WebView } from "vscode-extension-tester";
import { assertWebElementIsDisplayedEnabled, EditorTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import { expandedDocksBarE, h3ComponentWithText, tabWithTitle } from "../CommonLocators";
import { EditorTabs } from "./EditorTabs";
import ExpressionEditorHelper from "./ExpressionEditorHelper";
import PropertiesPanelHelper from "./PropertiesPanelHelper";
import DecisionNavigatorHelper from "./DecisionNavigatorHelper";

/**
 * Helper class to easen work with DMN editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class DmnEditorTestHelper extends EditorTestHelper {
  private decisionNavigator: WebElement;
  private diagramExplorer: WebElement;
  private properties: WebElement;

  /**
   *
   * @param webview WebView where the editor Iframe is located.
   */
  constructor(webview: WebView) {
    super(webview);
  }

  /**
   * Finds button that open DMN diagram explorer.
   *
   * @returns Promise<WebElement> promise that resolves to DMN diagram explorer button.
   */
  public async getDiagramExplorer(): Promise<WebElement> {
    this.diagramExplorer = await this.webview.findWebElement(By.xpath("//button[@data-title='Explore diagram']"));
    return this.diagramExplorer;
  }

  /**
   * Finds DMN diagram properties element.
   *
   * @returns Promise<WebElement> promise that resolves to DMN diagram properties element.
   */
  public async getDiagramProperties(): Promise<WebElement> {
    this.properties = await this.webview.findWebElement(By.className("docks-item-E-DiagramEditorPropertiesScreen"));
    return this.properties;
  }

  /**
   * Finds DMN decision navigator element.
   *
   * @returns Promise<WebElement> promise that resolves to DMN decision navigator element.
   */
  public async getDecisionNavigator(): Promise<WebElement> {
    this.decisionNavigator = await this.webview.findWebElement(
      By.className("docks-item-E-org.kie.dmn.decision.navigator")
    );
    return this.decisionNavigator;
  }

  public async getExpressionEditor(): Promise<ExpressionEditorHelper> {
    this.decisionNavigator = await this.webview.findWebElement(By.id("dmn-expression-editor-container"));
    return new ExpressionEditorHelper(this.decisionNavigator);
  }

  /**
   * Switch editor to other Tab
   * @param editorTab Tab to be swithced on
   */
  public async switchEditorTab(editorTab: EditorTabs): Promise<void> {
    const tabElement = await this.webview.findWebElement(tabWithTitle(editorTab));
    await assertWebElementIsDisplayedEnabled(tabElement);
    await tabElement.click();
  }

  /**
   * Using 'EditorTabs.IncludedModels' tab new model is included
   * @param modelFileName file name in the same direcotry that will be included
   */
  public async includeModel(modelFileName: string, modelAlias: string): Promise<void> {
    // Invoke Include Model pop-up
    const includeModelButton = await this.webview.findWebElement(By.xpath("//button[@data-field='include-model']"));
    await assertWebElementIsDisplayedEnabled(includeModelButton);
    await includeModelButton.click();

    const modelsDropDown: string = "//div[@data-i18n-prefix='KogitoKieAssetsDropdownView.']";

    // Display list of available models
    const selectorButton = await this.webview.findWebElement(
      By.xpath(modelsDropDown + "//button[@data-toggle='dropdown']")
    );
    await assertWebElementIsDisplayedEnabled(selectorButton);
    await selectorButton.click();

    // Select demanded model
    const modelOption = await this.webview
      .getDriver()
      .wait(
        until.elementLocated(By.xpath(modelsDropDown + `/label/div/div/ul/li[contains(., '${modelFileName}')]`)),
        5000,
        "Model options were not shown after 5 seconds"
      );
    await modelOption.click();

    // Provide alias for demanded model
    const modelAliasInput = await this.webview.findWebElement(By.xpath("//input[@data-field='model-name']"));
    await assertWebElementIsDisplayedEnabled(modelAliasInput);
    await modelAliasInput.sendKeys(modelAlias);

    // Confirm include
    const includeButton = await this.webview.findWebElement(By.xpath("//button[@data-field='include']"));
    await assertWebElementIsDisplayedEnabled(includeButton);
    await includeButton.click();
  }

  /**
   * Assert included model details on the 'EditorTabs.IncludedModels'
   * @param modelAlias model to be asserted
   * @param nodesCount asserted model expected nodes count
   */
  public async inspectIncludedModel(modelAlias: string, nodesCount: number): Promise<void> {
    // Wait until card with include details is shown
    const includeDetails = await this.webview
      .getDriver()
      .wait(
        until.elementLocated(
          By.xpath(`//div[@data-i18n-prefix='DMNCardComponentContentView.' and contains(., '${modelAlias}')]`)
        ),
        5000,
        "Include details was not shown after 5 seconds"
      );

    const includedNodes = await includeDetails.findElement(By.xpath("//span[@data-field='drg-elements-count']"));
    expect(await includedNodes.getText()).to.equal(`${nodesCount}`, "Included model nodes count was not as expected");
  }

  /**
   * Opens diagram properties.
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
   * Opens diagram explorer.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public async openDiagramExplorer(): Promise<WebElement> {
    const explorer = await this.getDiagramExplorer();
    await assertWebElementIsDisplayedEnabled(explorer);
    await explorer.click();
    const expandedExplorerPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(
      await explorer.findElement(By.xpath(h3ComponentWithText("Explore diagram")))
    );
    await assertWebElementIsDisplayedEnabled(expandedExplorerPanel);
    return expandedExplorerPanel;
  }

  /**
   * Opens decision navigator.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public async openDecisionNavigator(): Promise<DecisionNavigatorHelper> {
    const navigator = await this.getDecisionNavigator();
    await assertWebElementIsDisplayedEnabled(navigator);
    await navigator.click();
    const expandedNavigatorPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(
      await navigator.findElement(By.xpath(h3ComponentWithText("Decision Navigator")))
    );
    await assertWebElementIsDisplayedEnabled(expandedNavigatorPanel);
    return new DecisionNavigatorHelper(expandedNavigatorPanel);
  }
}

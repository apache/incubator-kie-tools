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

import { By, until, WebElement, WebView } from "vscode-extension-tester";
import { assertWebElementIsDisplayedEnabled, EditorTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import { expandedDocksBarE, h3ComponentWithText, spanComponentWithText } from "./CommonLocators";

/**
 * Helper class to easen work with SCESIM editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class ScesimEditorTestHelper extends EditorTestHelper {
  /**
   *
   * @param webview WebView where the editor Iframe is located.
   */
  constructor(webview: WebView) {
    super(webview);
  }

  /**
   * Finds button that open SCESIM editor settings.
   *
   * @returns Promise<WebElement> promise that resolves to SCESIM diagram settings button.
   */
  public getSettings = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(By.xpath("//button[@data-title='Settings']"));
  };

  /**
   * Finds DMN diagram properties element.
   *
   * @returns Promise<WebElement> promise that resolves to DMN diagram properties element.
   */
  public getTestTools = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(By.xpath("//button[@data-title='Test Tools']"));
  };

  /**
   * Finds DMN decision navigator element.
   *
   * @returns Promise<WebElement> promise that resolves to DMN decision navigator element.
   */
  public getScenarioCheatsheet = async (): Promise<WebElement> => {
    return await this.webview.findWebElement(By.className("docks-item-E-org.drools.scenariosimulation.CheatSheet"));
  };

  /**
   * Opens settings.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public openSettings = async (): Promise<WebElement> => {
    const settings = await this.getSettings();
    await assertWebElementIsDisplayedEnabled(settings);
    await settings.click();
    const expandedSettingsPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(await settings.findElement(By.xpath(h3ComponentWithText("Settings"))));
    await assertWebElementIsDisplayedEnabled(expandedSettingsPanel);
    return expandedSettingsPanel;
  };

  /**
   * Opens test tools.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public openTestTools = async (): Promise<WebElement> => {
    const testTools = await this.getTestTools();
    await assertWebElementIsDisplayedEnabled(testTools);
    await testTools.click();
    const expandedTestToolsPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(await testTools.findElement(By.xpath(h3ComponentWithText("Test Tools"))));
    await assertWebElementIsDisplayedEnabled(expandedTestToolsPanel);
    return expandedTestToolsPanel;
  };

  /**
   * Opens scenario cheatsheet.
   *
   * Verifies the button is displayed and enabled.
   * Verifies there is an expanded panel element displayed and enabled.
   * Verifies there is a <h3> element with proper text.
   *
   * @returns a promise resolving to WebElement of openned panel.
   */
  public openScenarioCheatsheet = async (): Promise<WebElement> => {
    const scenarioCheatsheet = await this.getScenarioCheatsheet();
    await assertWebElementIsDisplayedEnabled(scenarioCheatsheet);
    await scenarioCheatsheet.click();
    const expandedNavigatorPanel = await this.webview.findWebElement(expandedDocksBarE());
    await assertWebElementIsDisplayedEnabled(
      await scenarioCheatsheet.findElement(By.xpath(h3ComponentWithText("Scenario Cheatsheet")))
    );
    await assertWebElementIsDisplayedEnabled(expandedNavigatorPanel);
    return expandedNavigatorPanel;
  };

  /**
   * In case you open empty scesim file a landing page prompts you to specify a file you want to test.
   * This method sets specified dmn file to be tested.
   *
   * @param dmnFileName
   *
   */
  public specifyDmnOnLandingPage = async (dmnFileName: String): Promise<void> => {
    await (await this.webview.findWebElement(spanComponentWithText("DMN"))).click();

    // wait until available DMNs appear
    const availableDmns = await this.webview
      .getDriver()
      .wait(until.elementLocated(By.xpath("//div[@data-field='dmn-assets']")), 5000, "DMN Models not found");

    // expand available DMNs
    await (await availableDmns.findElement(By.xpath("//button[@title='Select']"))).click();
    const options = await this.webview
      .getDriver()
      .wait(
        until.elementLocated(By.xpath("//ul[@role='menu']")),
        5000,
        "DMN Models not not expanded after click on Select"
      );

    // select demanded DMN
    await (await options.findElement(By.xpath(`//li/a/span[text()='${dmnFileName}']`))).click();

    // finish landing page by click on Confirm button
    await (await this.webview.findWebElement(By.xpath("//button[@data-field='ok-button']"))).click();
    return;
  };
}

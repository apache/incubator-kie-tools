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
import { h3ComponentWithText } from "./CommonLocators";

/**
 * Helper class to easen work with SCESIM editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class ScesimEditorTestHelper {

    /**
     * WebView in whitch the editor Iframe is located.
     * Initialize in constructor.
     */
    private webview: WebView;

    constructor(webview: WebView) {
        this.webview = webview;
    }

    /**
     * Finds button that open SCESIM editor settings.
     * 
     * @returns Promise<WebElement> promise that resolves to SCESIM diagram settings button.
     */
    public getSettings = async (): Promise<WebElement> => {
        return await this.webview.findWebElement(By.xpath('//button[@data-title=\'Settings\']'));
    }

    /**
     * Finds DMN diagram properties element.
     * 
     * @returns Promise<WebElement> promise that resolves to DMN diagram properties element. 
     */
    public getTestTools = async (): Promise<WebElement> => {
        return await this.webview.findWebElement(By.xpath('//button[@data-title=\'Test Tools\']'));
    }

    /**
     * Finds DMN decision navigator element.
     * 
     * @returns Promise<WebElement> promise that resolves to DMN decision navigator element. 
     */
    public getScenarioCheatsheet = async (): Promise<WebElement> => {
        return await this.webview.findWebElement(By.className("qe-docks-item-E-org.drools.scenariosimulation.CheatSheet"));
    }

    /**
     * Opens settings.
     * 
     * Verifies the button is displayed and enabled.
     * Verifies there is an expanded panel element displayed and enabled.
     * Verifies there is a <h3> element with propert text.
     * 
     * @returns a promise resolving to WebElement of openned panel.
     */
    public openSettings = async (): Promise<WebElement> => {
        const settings = await this.getSettings();
        await assertWebElementIsDisplayedEnabled(settings)
        await settings.click();
        const expandedSettingsPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await settings.findElement(By.xpath(h3ComponentWithText('Settings'))));
        await assertWebElementIsDisplayedEnabled(expandedSettingsPanel);
        return expandedSettingsPanel;
    }

    /**
     * Opens test tools.
     * 
     * Verifies the button is displayed and enabled.
     * Verifies there is an expanded panel element displayed and enabled.
     * Verifies there is a <h3> element with propert text.
     * 
     * @returns a promise resolving to WebElement of openned panel.
     */
    public openTestTools = async (): Promise<WebElement> => {
        const testTools = await this.getTestTools();
        await assertWebElementIsDisplayedEnabled(testTools);
        await testTools.click();
        const expandedTestToolsPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await testTools.findElement(By.xpath(h3ComponentWithText('Test Tools'))));
        await assertWebElementIsDisplayedEnabled(expandedTestToolsPanel);
        return expandedTestToolsPanel;
    }

    /**
     * Opens scenario cheatsheet.
     * 
     * Verifies the button is displayed and enabled.
     * Verifies there is an expanded panel element displayed and enabled.
     * Verifies there is a <h3> element with propert text.
     * 
     * @returns a promise resolving to WebElement of openned panel.
     */
    public openScenarioCheatsheet = async (): Promise<WebElement> => {
        const scenarioCheatsheet = await this.getScenarioCheatsheet();
        await assertWebElementIsDisplayedEnabled(scenarioCheatsheet);
        await scenarioCheatsheet.click();
        const expandedNavigatorPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await scenarioCheatsheet.findElement(By.xpath(h3ComponentWithText('Scenario Cheatsheet'))));
        await assertWebElementIsDisplayedEnabled(expandedNavigatorPanel);
        return expandedNavigatorPanel;
    }
}
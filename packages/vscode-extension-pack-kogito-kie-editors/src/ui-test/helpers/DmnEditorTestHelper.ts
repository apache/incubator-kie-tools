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
 * Helper class to easen work with DMN editor inside of a webview.
 * Make sure you switch to the webview's frame before creating and instance
 * via contructor.
 */
export default class DmnEditorTestHelper {

    /**
     * WebView in whitch the editor Iframe is located.
     * Initialize in constructor.
     */
    private webview: WebView;
    
    private decisionNavigator: WebElement;
    private diagramExplorer: WebElement;
    private properties: WebElement;

    constructor(webview: WebView) {
        this.webview = webview;
    }

    /**
     * Finds button that open DMN diagram explorer.
     * 
     * @returns Promise<WebElement> promise that resolves to DMN diagram explorer button.
     */
    public getDiagramExplorer = async (): Promise<WebElement> => {
        this.diagramExplorer = await this.webview.findWebElement(By.xpath('//button[@data-title=\'Explore diagram\']'));
        return this.diagramExplorer;
    }

    /**
     * Finds DMN diagram properties element.
     * 
     * @returns Promise<WebElement> promise that resolves to DMN diagram properties element. 
     */
    public getDiagramProperties = async (): Promise<WebElement> => {
        this.properties = await this.webview.findWebElement(By.className('qe-docks-item-E-DiagramEditorPropertiesScreen'));
        return this.properties;
    }

    /**
     * Finds DMN decision navigator element.
     * 
     * @returns Promise<WebElement> promise that resolves to DMN decision navigator element. 
     */
    public getDecisionNavigator = async (): Promise<WebElement> => {
        this.decisionNavigator = await this.webview.findWebElement(By.className('qe-docks-item-E-org.kie.dmn.decision.navigator'));
        return this.decisionNavigator;
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
    public openDiagramProperties = async (): Promise<WebElement> => {
        const properties = await this.getDiagramProperties();
        await assertWebElementIsDisplayedEnabled(properties)
        await properties.click();
        const expandedPropertiesPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await properties.findElement(By.xpath(h3ComponentWithText('Properties'))));
        await assertWebElementIsDisplayedEnabled(expandedPropertiesPanel);
        return expandedPropertiesPanel;
    }

    /**
     * Opens diagram explorer.
     * 
     * Verifies the button is displayed and enabled.
     * Verifies there is an expanded panel element displayed and enabled.
     * Verifies there is a <h3> element with propert text.
     * 
     * @returns a promise resolving to WebElement of openned panel.
     */
    public openDiagramExplorer = async (): Promise<WebElement> => {
        const explorer = await this.getDiagramExplorer();
        await assertWebElementIsDisplayedEnabled(explorer);
        await explorer.click();
        const expandedExplorerPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await explorer.findElement(By.xpath(h3ComponentWithText('Explore diagram'))));
        await assertWebElementIsDisplayedEnabled(expandedExplorerPanel);
        return expandedExplorerPanel;
    }

    /**
     * Opens decision navigator.
     * 
     * Verifies the button is displayed and enabled.
     * Verifies there is an expanded panel element displayed and enabled.
     * Verifies there is a <h3> element with propert text.
     * 
     * @returns a promise resolving to WebElement of openned panel.
     */
    public openDecisionNavigator = async (): Promise<WebElement> => {
        const navigator = await this.getDecisionNavigator();
        await assertWebElementIsDisplayedEnabled(navigator);
        await navigator.click();
        const expandedNavigatorPanel = await this.webview.findWebElement(By.className('qe-docks-bar-expanded-E'))
        await assertWebElementIsDisplayedEnabled(await navigator.findElement(By.xpath(h3ComponentWithText('Decision Navigator'))));
        await assertWebElementIsDisplayedEnabled(expandedNavigatorPanel);
        return expandedNavigatorPanel;
    }
}
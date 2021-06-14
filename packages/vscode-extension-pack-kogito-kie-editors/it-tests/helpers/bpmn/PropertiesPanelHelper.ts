/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import { By, WebElement } from "vscode-extension-tester";
import {
  addGlobalVariableButton,
  addProcessVariableButton,
  globalVariableDataTypeInput,
  globalVariableNameInput,
  globalVariablesDiv,
  processVariableDataTypeInput,
  processVariableNameInput,
  propertiesSectionAnchor,
} from "./BpmnLocators";
import { labeledAnyElementInPropertiesPanel } from "../CommonLocators";
import { assert } from "chai";
import { sleep } from "../VSCodeTestHelper";

export enum PropertiesPanelSection {
  PROCESS = "Process",
  PROCESS_DATA = "Process Data",
  Advanced = "Advanced",
}

/**
 * Class for accessing expanded BPMN Properties panel
 */
export default class PropertiesPanelHelper {
  constructor(private readonly root: WebElement) {}

  public get rootElement() {
    return this.root;
  }

  /**
   * Expand desired section of property panel.
   *
   * @param sectionName
   */
  public async expandPropertySection(sectionToExpand: PropertiesPanelSection): Promise<PropertiesPanelHelper> {
    const sectionAnchor = await this.root.findElement(propertiesSectionAnchor(sectionToExpand));
    await this.scrollPropertyIntoView(sectionAnchor);
    await sectionAnchor.click();

    return this;
  }

  public async addProcessVariable(
    name: string,
    dataType: string,
    isNewCustomDataType?: boolean
  ): Promise<PropertiesPanelHelper> {
    await this.expandPropertySection(PropertiesPanelSection.PROCESS_DATA);

    const processVariableAddButton = await this.root.findElement(addProcessVariableButton());
    await this.scrollPropertyIntoView(processVariableAddButton);
    await processVariableAddButton.click();

    const processVariableNameInputField = await this.root.findElement(processVariableNameInput());
    await this.scrollPropertyIntoView(processVariableNameInputField);
    await processVariableNameInputField.sendKeys(name);

    const processVariableDataTypeInputField = await this.root.findElement(processVariableDataTypeInput());
    await processVariableDataTypeInputField.click();

    if (isNewCustomDataType) {
      const customOption = await this.root.findElement(By.xpath("//select/option[@value='Custom ...']"));
      await customOption.click();
      const customDataTypeInput = await this.root.findElement(By.xpath("//input[@data-field='customDataType']"));
      await customDataTypeInput.sendKeys(dataType);
    } else {
      const dataTypeOption = await this.root.findElement(By.xpath(`//select/option[@value='${dataType}']`));
      await dataTypeOption.click();
    }

    return this;
  }

  public async addGlobalVariable(
    name: string,
    dataType: string,
    isNewCustomDataType?: boolean
  ): Promise<PropertiesPanelHelper> {
    const globalVariablesWidget = await this.root.findElement(globalVariablesDiv());
    const addGlobalButton = await globalVariablesWidget.findElement(addGlobalVariableButton());
    await this.scrollPropertyIntoView(addGlobalButton);
    await addGlobalButton.click();

    const globalVariableNameInputField = await globalVariablesWidget.findElement(globalVariableNameInput());
    await this.scrollPropertyIntoView(globalVariableNameInputField);
    globalVariableNameInputField.sendKeys(name);

    const globalVariableDataTypeInputField = await globalVariablesWidget.findElement(globalVariableDataTypeInput());
    await globalVariableDataTypeInputField.click();

    if (isNewCustomDataType) {
      const customOption = await globalVariablesWidget.findElement(By.xpath(".//select/option[@value='Custom ...']"));
      await customOption.click();
      const customDataTypeInput = await globalVariablesWidget.findElement(
        By.xpath(".//input[@data-field='customDataType']")
      );
      await customDataTypeInput.sendKeys(dataType);
    } else {
      const dataTypeOption = await globalVariablesWidget.findElement(
        By.xpath(".//select/option[@value='" + dataType + "']")
      );
      await dataTypeOption.click();
    }

    return this;
  }

  /**
   * Scrolls desired property element into view.
   *
   * @param propertyElement element that si to be scrolled into view
   */
  public async scrollPropertyIntoView(propertyElement: WebElement): Promise<PropertiesPanelHelper> {
    const driver = propertyElement.getDriver();
    await driver.executeScript("arguments[0].scrollIntoView({ behavior: 'auto', block: 'end'});", propertyElement);
    await sleep(500);

    return this;
  }

  /**
   * Change a property to a provided value. The Original value is replaced by the new value completely.
   * Just visible properties are assumed. Accordion view hidden content can not be changed by this method.
   *
   * @param propertyName
   * @param propertyValue
   */
  public async changeProperty(propertyName: string, propertyValue: string): Promise<PropertiesPanelHelper> {
    const property = await this.getProperty(propertyName);
    await property.clear();
    await property.sendKeys(propertyValue);

    return this;
  }

  /**
   * Asserts that value of a property matches expectedValue provided as argument.
   *
   * @param propertyName
   * @param expectedValue
   */
  public async assertPropertyValue(propertyName: string, expectedValue: string, propertyType?: string): Promise<void> {
    const property = await this.getProperty(propertyName, propertyType);
    const actualValue = await property.getAttribute("value");
    assert.equal(
      actualValue,
      expectedValue,
      "Value of " +
        propertyName +
        " property did not match the expected value. Actual value is [" +
        actualValue +
        "]. Expected value is [" +
        expectedValue +
        "]"
    );
  }

  /**
   * Get a property from properties panel. Use its name to identify the property element.
   *
   * @param propertyName type of the html element the property uses, select, input, custom widgets etc.
   *                     this should be a locator relative to it's div#fieldContainer in properties.
   */
  public async getProperty(propertyName: string, propertyType?: string): Promise<WebElement> {
    const property = await this.root.findElement(labeledAnyElementInPropertiesPanel(propertyName, propertyType));
    return property;
  }
}

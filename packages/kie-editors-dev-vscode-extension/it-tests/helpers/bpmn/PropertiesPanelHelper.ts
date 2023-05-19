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
import { sleep } from "@kie-tools/vscode-extension-common-test-helpers";
import CorrelationModalHelper from "./CorrelationModalHelper";
import ProcessVariablesWidgetHelper from "./ProcessVariablesWidgetHelper";
import DataAssignmentsModalHelper from "./DataAssignmentsModalHelper";

export enum PropertiesPanelSection {
  COLLABORATION = "Collaboration",
  CORRELATION = "Correlation",
  PROCESS = "Process",
  PROCESS_DATA = "Process Data",
  IMPLEMENTATION_EXECUTION = "Implementation/Execution",
  ADVANCED = "Advanced",
  DATA_ASSIGNMENTS = "Data Assignments",
}

/**
 * Class for accessing expanded BPMN Properties panel
 */
export default class PropertiesPanelHelper {
  constructor(protected readonly root: WebElement) {}

  public get rootElement() {
    return this.root;
  }

  /**
   * Expand desired section of property panel.
   * When desired section is already open, collapse the section instead.
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
      const dataTypeOption = await this.root.findElement(By.xpath("//td/select/option[@value='" + dataType + "']"));
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
   * @param propertyType Type of property (select, textarea etc.).
   */
  public async changeProperty(
    propertyName: string,
    propertyValue: string,
    propertyType?: string
  ): Promise<PropertiesPanelHelper> {
    const property = await this.getProperty(propertyName, propertyType);
    if (propertyType == "select") {
      property.click();
      const propertyOption = await property.findElement(
        By.xpath(
          "//label[contains(.,'" +
            propertyName +
            "')]/following-sibling::div[@data-field='fieldContainer']/select/option[@value='" +
            propertyValue +
            "']"
        )
      );
      await propertyOption.click();
    } else {
      await property.clear();
      await property.sendKeys(propertyValue);
    }

    return this;
  }

  /**
   * Change a widgeted property to a provided value. The Original value is replaced by the new value completely.
   * Just visible properties are assumed. Accordion view hidden content can not be changed by this method.
   *
   * @param propertyName
   * @param propertyValue
   * @param propertyType Type of property (select, textarea etc.).
   */
  public async changeWidgetedProperty(
    propertyName: string,
    propertyValue: string,
    propertyType?: string
  ): Promise<PropertiesPanelHelper> {
    const property = await this.getProperty(propertyName, "/" + propertyType);
    if (propertyType == "select") {
      const propertyOption = await property.findElement(
        By.xpath(
          "//label[contains(.,'" +
            propertyName +
            "')]/following-sibling::div[@data-field='fieldContainer']//select/option[@value='" +
            propertyValue +
            "']"
        )
      );
      await propertyOption.click();
    } else {
      await property.clear();
      await property.sendKeys(propertyValue);
    }

    return this;
  }

  /**
   * Asserts that value of a property matches expectedValue provided as argument.
   *
   * @param propertyName
   * @param expectedValue
   * @param propertyType Type of property (select, textarea etc.).
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
   * Asserts that value of a widgeted property matches expectedValue provided as argument.
   *
   * @param propertyName
   * @param expectedValue
   * @param propertyType Type of property (select, textarea etc.).
   */
  public async assertWidgetedPropertyValue(
    propertyName: string,
    expectedValue: string,
    propertyType?: string
  ): Promise<void> {
    const property = await this.getProperty(propertyName, "/" + propertyType);
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

  /**
   * Get a correlation modal helper class that allows checking and asserting
   * Collaborations on top-level of process.
   *
   * @returns CorrelationModalHelper class that is initialized
   */
  public async getCollerationModalHelper(): Promise<CorrelationModalHelper> {
    await this.expandPropertySection(PropertiesPanelSection.COLLABORATION);
    const correllationsButton = await this.root.findElement(By.xpath("//button[@id='correlationsButton']"));
    await this.scrollPropertyIntoView(correllationsButton);
    await correllationsButton.click();
    const modalDialog = await this.root.findElement(By.xpath("//div[@class='modal-dialog']"));
    return new CorrelationModalHelper(modalDialog);
  }
  /**
   * Get a helper class that allows working with Process variables widget.
   * This method expands the Process Data section where this widget is located
   * initializes the helper and returns it.
   * View is also scrolled so that the widget is visible.
   *
   * @returns Initialized instance of ProcessVariableWidgetHelper
   */
  public async getProcessVariablesHelper(): Promise<ProcessVariablesWidgetHelper> {
    await this.expandPropertySection(PropertiesPanelSection.PROCESS_DATA);
    const processVariableWidget = await this.root.findElement(
      labeledAnyElementInPropertiesPanel("Process Variables", "div")
    );
    await this.scrollPropertyIntoView(processVariableWidget);
    return new ProcessVariablesWidgetHelper(processVariableWidget);
  }

  /**
   * Get a helper class that allows working with Data Assignments widget.
   * This method expands the Data Assignments section where this widget is located,
   * opens the data assignment modal, initializes the helper and returns it.
   * View is also scrolled so that the widget is visible.
   *
   * @returns Initialized instance of DataAssignmntModalHelper
   */
  public async getDataAssignmentsModalHelper(): Promise<DataAssignmentsModalHelper> {
    await this.expandPropertySection(PropertiesPanelSection.DATA_ASSIGNMENTS);
    const assignmentsButton = await this.root.findElement(By.xpath("//span[@class='input-group-btn']"));
    await this.scrollPropertyIntoView(assignmentsButton);
    await assignmentsButton.click();
    const modalDialog = await this.root.findElement(By.xpath("//div[@class='modal-dialog']"));
    return new DataAssignmentsModalHelper(modalDialog);
  }
}

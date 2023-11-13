/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { By } from "vscode-extension-tester";
import { anchorContainingSpanWithTextEqual } from "../CommonLocators";

/**
 * Creates a XPATH string that locates <a> element with specific <span> with text.
 *
 * @param itemName name displayed inside that a
 */
export const palletteItemAnchor = (itemName: string): By => {
  return anchorContainingSpanWithTextEqual(itemName);
};

/**
 * Creates a XPATH string that locates <a> element that contains
 * specific <span> with text.
 *
 * @param sectionName name of the section in properties
 */
export const propertiesSectionAnchor = (sectionName: string): By => {
  return anchorContainingSpanWithTextEqual(sectionName);
};

/**
 * Creates a XPATH locator for field containers of properties.
 *
 * @returns By locator for list of field containers that contain properties
 */
export const propertiesFieldContainers = (): By => {
  return By.xpath(`//div[@class='row']`);
};

/**
 * Creates a xpath locator for custom tasks name textarea located in
 * properties.
 */
export const customTaskNameTextArea = (): By => {
  return By.xpath("//textarea[contains(@name, 'general.name')]");
};

/**
 * Creates a xpath locator for custom tasks documentation textarea located
 * in properties.
 */
export const customTaskDocumentationTextArea = (): By => {
  return By.xpath("//textarea[contains(@name,'general.documentation')]");
};

/**
 * Creates a xpath locator for data assignments property input.
 * This input shows number of input and output assignments of selected task.
 */
export const assignmentsTextBoxInput = (): By => {
  return By.xpath("//input[@id='assignmentsTextBox']");
};

/**
 * Creates a xpath locator for process name property input.
 * This input is used to manipulate name of process.
 */
export const processNameInput = (): By => {
  return By.xpath("//input[contains(@name, 'diagramSet.name')]");
};

/**
 * Creates a xpath locator for process variable editor widget.
 * This div contains table for process variables manipulation.
 */
export const processVariablesDiv = (): By => {
  return By.xpath("//label[span/text() = 'Process Variables']/following-sibling::div[1]");
};

/**
 * Creates a xpath locator for button that adds process variable.
 * This button is used to add a row to table with process variables by clicking it.
 */
export const addProcessVariableButton = (): By => {
  return By.xpath("//button[@data-field='addVarButton']");
};

/**
 * Creates a xpath locator for process variable name input field.
 * This input is used to manipulate or set name of process variable.
 */
export const processVariableNameInput = (): By => {
  return By.xpath("//input[@data-field='name']");
};

/**
 * Creates a xpath locator for process variable data types select field.
 * This select is used to manipulate or set data type of process variable.
 */
export const processVariableDataTypeInput = (): By => {
  return By.xpath("//td/select[@data-field='dataType']");
};

/**
 * Creates a xpath locator for global variable div.
 * This div contains VariableEditorWidget that allow manipulation of global
 * variables in process.
 * NOTE: VariableEditorWidget is also used for process variables. Therefore
 * the asociated locators as `globalVariablesDiv()` used in context of this div.
 */
export const globalVariablesDiv = (): By => {
  return By.xpath("//label[span/text() = 'Global Variables']/following-sibling::div[1]");
};

/**
 * Creates a xpath locator for global variable name input.
 * This input is used to manipulate or set name of global variable.
 *
 * NOTE: Use in context of element returned using globalVariablesDiv locator.
 */
export const globalVariableNameInput = (): By => {
  return By.xpath(".//input[@data-field='name']");
};

/**
 * Creates a xpath locator for global variable data type select.
 * This select is used to manipulate or set data type of global variable.
 *
 * NOTE: Use in context of element returned using globalVariablesDiv locator.
 */
export const globalVariableDataTypeInput = (): By => {
  return By.xpath(".//select[@data-field='dataType']");
};

/**
 * Creates a xpath locator for global variable add button.
 * This button is used to add a new global variable.
 *
 * NOTE: Use in context of element returned using globalVariablesDiv locator.
 */
export const addGlobalVariableButton = (): By => {
  return By.xpath(".//button[@data-field='addVarButton']");
};

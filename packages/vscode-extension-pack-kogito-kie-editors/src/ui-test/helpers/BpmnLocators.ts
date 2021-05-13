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

import { By } from "vscode-extension-tester";

/*
 * Creates a XPATH string that locates <span> element with specific text.
 *
 * @param By locator for pallete item anchors
 */
export const palletteItemAnchor = (itemName: string): By => {
  return By.xpath(`//a[.//span[text() = \'${itemName}\']]`);
};

/**
 * Creates a XPATH locator for field containers of properties.
 *
 * @returns By locator for list of field containers that contain properties
 */
export const propertiesFieldContainers = (): By => {
  return By.xpath(`//div[@class=\'row\']`);
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

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

import { By } from "vscode-extension-tester";

/**
 * Creates a XPATH string that locates <a> element with specific text.
 *
 * @param text string to match
 */
export const aComponentWithText = (text: string): By => {
  return By.xpath(`//a[text() = '${text}']`);
};

/**
 * Creates a XPATH locator that locates <span> element with specific text.
 *
 * @param text string to match
 */
export const spanComponentWithText = (text: string): By => {
  return By.xpath(`//span[text() = '${text}']`);
};

/**
 * Creates a XPATH locator that locates <{inputElement}> element with specified label
 * in properties panel. Use this for special widgets and elements that ae not direct children
 * of fieldContainers.
 *
 * Looks for <input> if the inputElement is not provided or empty
 *
 * @param inputElement should be XPATH locator for element withing fieldContainer div, optional
 * @param label label of the input field
 */
export const labeledAnyElementInPropertiesPanel = (label: string, inputElement?: string): By => {
  if (inputElement && inputElement.length != 0) {
    return By.xpath(
      `//label[contains(.,\'${label}\')]/following-sibling::div[@data-field='fieldContainer']/${inputElement}`
    );
  } else {
    return By.xpath(`//label[contains(.,\'${label}\')]/following-sibling::div[@data-field='fieldContainer']/input`);
  }
};

/**
 * Creates a XPATH string that locates <h3> element with specific text.
 *
 * @param text string to match
 */
export const h3ComponentWithText = (text: string): string => {
  return `//h3[text() = '${text}']`;
};

/**
 * Creates a XPATH string that locates <h5> element with specific text.
 *
 * @param text string to match
 */
export const h5ComponentWithText = (text: string): By => {
  return By.xpath(`//h5[text() = '${text}']`);
};

/**
 * Creates a classname locator for 'expanded-docks-bar-E'
 */
export const expandedDocksBarE = (): By => {
  return By.className("expanded-docks-bar-E");
};

/** Creates a XPATH locator that locates an editor tab element.
 *
 * @param title title of the tab to match
 */
export const tabWithTitle = (title: string): By => {
  return By.xpath(`//ul/li[@data-ouia-component-type='editor-nav-tab' and @data-ouia-component-id='${title}']/a`);
};

/**
 * Creates a XPATH string that locates <a> element with specific <span> with text.
 *
 * @param itemName name displayed inside of span
 */
export const anchorContainingSpanWithTextEqual = (itemName: string): By => {
  return By.xpath(`//a[.//span[text() = '${itemName}']]`);
};

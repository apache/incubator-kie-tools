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

import { By } from "vscode-extension-tester";

/**
 * Creates a XPATH string that locates <a> element with specific text.
 *
 * @param text string to match
 */
export const aComponentWithText = (text: string): string => {
  return `//a[text() = \'${text}\']`;
};

/**
 * Creates a XPATH string that locates <span> element with specific text.
 *
 * @param text string to match
 */
export const spanComponentWithText = (text: string): string => {
  return `//span[text() = \'${text}\']`;
};

/**
 * Creates a XPATH string that locates <h3> element with specific text.
 *
 * @param text string to match
 */
export const h3ComponentWithText = (text: string): string => {
  return `//h3[text() = \'${text}\']`;
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
  return By.xpath(`//ul/li[@data-ouia-component-type='editor-nav-tab' and @data-ouia-component-id=\'${title}\']/a`);
};

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
 * Creates a locator for iframe with webview ready class.
 * The element is located in a webview.
 */
export const webViewReady = (): By => {
  return By.className("webview ready");
};

/**
 * Creates a locator for iframe with active-frame id.
 * The element is located in a webview.
 */
export const activeFrame = (): By => {
  return By.id("active-frame");
};

/**
 * Creates a locator for envelope app.
 * The div element is located in webview's active-frame.
 */
export const envelopeApp = (): By => {
  return By.id("envelope-app");
};

/**
 * Creates a locator for kogito loading spinner.
 * This is a common spinner used by envelope and will always be present.
 */
export const kogitoLoadingSpinner = (): By => {
  return By.className("kie-tools--loading-screen-spinner");
};

/**
 * Creates a locator for inputBox.
 */
export const inputBox = (): By => {
  return By.xpath('.//input[@type="text"]');
};

/**
 * Creates a locator for a folder with the corresponding name in the folder structure of the explorer.
 *
 * @param folderName the name of the folder for which the locator is to be created
 */
export const explorerFolder = (folderName: string): By => {
  return By.xpath(`//div[contains(@class, "monaco-list-row") and @aria-label="${folderName}"]`);
};

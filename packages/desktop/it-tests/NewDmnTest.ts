/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Application, SpectronClient } from "spectron";
import {initApp, waitLoading} from "./Tools";

let app: Application;
let client: SpectronClient;

beforeEach(async () => {
  app = await initApp();
  client = app.client;
});

afterEach(async () => {
  await app.stop();
});

test("new DMN from pages", async () => {
  // create new dmn
  const bpmnCard = await client.$("[data-ouia-component-id='new-blank-dmn-file-card']");
  await bpmnCard.click();

  // test empty dmn editor
  await testEmptyDmn();
});

test("new DMN from learn more", async () => {
  // open sidebar if it is closed
  const sidebarButton = await client.$("#nav-toggle");
  const sidebar = await client.$("#page-sidebar");
  if (!(await sidebar.getAttribute("class")).includes("expanded")) {
    await sidebarButton.click();
  }

  // open Learn more page
  const learnMoreNav = await client.$("[data-ouia-component-id='learn-more-nav']");
  await client.waitUntil(async () => await learnMoreNav.isDisplayed(), { timeout: 2000 });
  await learnMoreNav.click();

  // close sidebar if it is open
  if ((await sidebar.getAttribute("class")).includes("expanded")) {
    await sidebarButton.click();
  }

  // create new bpmn
  const newBpmnButton = await client.$("[data-ouia-component-id='create-dmn-button']");
  await newBpmnButton.click();

  // test empty dmn editor
  await testEmptyDmn();
});

async function testEmptyDmn() {
  // switch context to iframe
  const iframe = await client.$("#kogito-iframe");
  await client.switchToFrame(iframe);

  await waitLoading(client);

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check dmn name in properties
  const dmnName = await client.$("input[name$='.definitions.nameHolder']");
  expect(await dmnName.getValue()).toEqual("unsaved file");

  // open decision navigator
  const decisionNavigatorButton = await client.$("[data-ouia-component-id='collapsed-docks-bar-W']");
  await decisionNavigatorButton.click();

  // check node names
  const dmnNodes = await client.$$(
    "[data-i18n-prefix='DecisionNavigatorTreeView.'] > div > span[data-field='text-content']"
  );
  const nodeNames = await Promise.all(dmnNodes.map(async (i) => await i.getText()));
  expect(nodeNames).toEqual(["unsaved file"]);

  // open data types tab
  const dataTypesTab = await client.$("[data-ouia-component-id='Data Types'] > a");
  await dataTypesTab.click();

  // check no data types
  const noDataTypesTitle = await client.$("[data-i18n-key='NoCustomDataTitle']");
  expect(await noDataTypesTitle.getText()).toEqual("No custom data types have been defined.");

  // switch context back
  await client.switchToParentFrame();

  // check editor title name
  const title = await client.$("[data-testid='toolbar-title'] > h3");
  expect(await title.getText()).toEqual("unsaved file");

  // check save and close buttons
  const saveButton = await client.$("[data-ouia-component-id='save-button']");
  expect(await saveButton.isDisplayed()).toEqual(true);
  const closeButton = await client.$("[data-ouia-component-id='close-button']");
  expect(await closeButton.isDisplayed()).toEqual(true);

  // check header brand
  const headerBrand = await client.$(".pf-c-brand");
  expect(await headerBrand.getAttribute("alt")).toEqual("dmn kogito logo");
  expect(await headerBrand.getAttribute("src")).toContain("images/dmn_kogito_logo.svg");

  // close editor
  await closeButton.click();
  const newFileActions = await client.$("[data-ouia-component-id='new-file-gallery']");
  expect(await newFileActions.isDisplayed()).toEqual(true);
}

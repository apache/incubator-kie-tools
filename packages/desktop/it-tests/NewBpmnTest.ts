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
import { initApp } from "./Tools";

let app: Application;
let client: SpectronClient;

beforeEach(async () => {
  app = await initApp();
  client = app.client;
});

afterEach(async () => {
  await app.stop();
});

test("new BPMN from Files page", async () => {
  // create new bpmn
  const newBpmnButton = await client.$("[data-ouia-component-id='new-blank-bpmn-file-card']");
  await newBpmnButton.click();

  // test empty bpmn editor
  await testEmptyBpmn();
});

test("new BPMN from Learn more page", async () => {
  // open sidebar (if it is closed)
  const sidebarButton = await client.$("#nav-toggle");
  const sidebar = await client.$("#page-sidebar");
  if (!(await sidebar.getAttribute("class")).includes("expanded")) {
    await sidebarButton.click();
  }

  // open Learn more page
  const learnMoreNav = await client.$("[data-ouia-component-id='learn-more-nav']");
  await client.waitUntil(async () => await learnMoreNav.isDisplayed(), { timeout: 2000 });
  await learnMoreNav.click();

  // close sidebar (if it is open)
  if ((await sidebar.getAttribute("class")).includes("expanded")) {
    await sidebarButton.click();
  }

  // create new bpmn
  const newBpmnButton = await client.$("[data-ouia-component-id='create-bpmn-button']");
  await newBpmnButton.click();

  // test empty bpmn editor
  await testEmptyBpmn();
});

async function testEmptyBpmn() {
  // switch context to iframe
  const iframe = await client.$("#kogito-iframe");
  await client.switchToFrame(iframe);

  // wait until loading popup disappears
  const loadingDialog = await client.$("#loading-screen");
  await client.waitUntil(async () => await loadingDialog.isDisplayed(), { timeout: 5000 });
  await client.waitUntil(async () => !(await loadingDialog.isExisting()), { timeout: 30000 });

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check process name in properties
  const processName = await client.$("input[name$='.diagramSet.name']");
  expect(await processName.getValue()).toEqual("unsaved file");

  // open explorer diagram
  const explorerDiagramButton = await client.$("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']");
  await explorerDiagramButton.click();

  // check there is no node, just the process
  const nodes = await client.$$("[data-ouia-component-type='tree-item'] a");
  const nodeNames = await Promise.all(nodes.map(async (n) => await n.getText()));
  expect(nodeNames).toEqual(["unsaved file"]);

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
  expect(await headerBrand.getAttribute("alt")).toEqual("bpmn kogito logo");
  expect(await headerBrand.getAttribute("src")).toContain("images/bpmn_kogito_logo.svg");

  // close editor
  await closeButton.click();
  const newFileActions = await client.$("[data-ouia-component-id='new-file-gallery']");
  expect(await newFileActions.isDisplayed()).toEqual(true);
}

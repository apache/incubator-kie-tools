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
import { initApp, waitLoading } from "./Tools";

let app: Application;
let client: SpectronClient;

beforeEach(async () => {
  app = await initApp();
  client = app.client;
});

afterEach(async () => {
  await app.stop();
});

test("BPMN from source", async () => {
  // open BPMN from source
  const urlInput = await client.$("#url-text-input");
  await urlInput.setValue(
    "https://raw.githubusercontent.com/kiegroup/kie-tools/main/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.bpmn"
  );
  const openFromSourceButton = await client.$("[data-ouia-component-id='open-from-source-button']");
  await openFromSourceButton.click();

  // switch context to iframe
  const iframe = await client.$("#kogito-iframe");
  await client.switchToFrame(iframe);

  await waitLoading(client);

  // open explorer diagram
  const explorerDiagramButton = await client.$("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']");
  await explorerDiagramButton.click();

  // check process name
  const process = await client.$("[data-ouia-component-id='tree-item-myProcess'] > div");
  expect(await process.getText()).toEqual("myProcess");

  // check node names
  const nodes = await client.$$("[data-ouia-component-type='tree-item'] a");
  const nodeNames = await Promise.all(nodes.map(async (n) => await n.getText()));
  expect(nodeNames).toEqual(["myProcess", "MyStart", "MyTask", "MyEnd"]);

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check process name in properties
  const processName = await client.$("input[name$='.diagramSet.name']");
  expect(await processName.getValue()).toEqual("myProcess");

  // check script node name in properties
  await explorerDiagramButton.click();
  const taskNode = await client.$("[data-ouia-component-id='tree-item-MyTask']");
  await taskNode.click();
  await propertiesButton.click();
  const nodePropName = await client.$("textarea[name$='.general.name']");
  expect(await nodePropName.getValue()).toEqual("MyTask");

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
});

test("DMN from source", async () => {
  // open DMN from source
  const urlInput = await client.$("#url-text-input");
  await urlInput.setValue(
    "https://raw.githubusercontent.com/kiegroup/kie-tools/main/packages/chrome-extension-pack-kogito-kie-editors/it-tests/samples/test.dmn"
  );
  const openFromSourceButton = await client.$("[data-ouia-component-id='open-from-source-button']");
  await openFromSourceButton.click();

  // switch context to iframe
  const iframe = await client.$("#kogito-iframe");
  await client.switchToFrame(iframe);

  await waitLoading(client);

  // open decision navigator
  const decisionNavigatorButton = await client.$("[data-ouia-component-id='collapsed-docks-bar-W']");
  await decisionNavigatorButton.click();

  // check node names
  const dmnNodes = await client.$$(
    "[data-i18n-prefix='DecisionNavigatorTreeView.'] > div > span[data-field='text-content']"
  );
  const nodeNames = await Promise.all(dmnNodes.map(async (i) => await i.getText()));
  expect(nodeNames).toEqual(["myDmn", "MyDecision", "MyInputData", "MyModel", "Function"]);

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check dmn name in properties
  const dmnName = await client.$("input[name$='.definitions.nameHolder']");
  expect(await dmnName.getValue()).toEqual("myDmn");

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
});

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

test("BPMN example", async () => {
  // create new bpmn sample
  const bpmnCard = await client.$("[data-ouia-component-id='new-sample-bpmn-file-card']");
  await bpmnCard.click();

  // switch context to iframe
  const iframe = await client.$("#kogito-iframe");
  await client.switchToFrame(iframe);

  await waitLoading(client);

  // open explorer diagram
  const explorerDiagramButton = await client.$("[data-ouia-component-id='docks-item-ProjectDiagramExplorerScreen']");
  await explorerDiagramButton.click();

  // check process name
  const process = await client.$("[data-ouia-component-id='tree-item-Process travelers'] > div");
  expect(await process.getText()).toEqual("Process travelers");

  // check node names
  const nodes = await client.$$("[data-ouia-component-type='tree-item'] a");
  const nodeNames = await Promise.all(nodes.map(async (n) => await n.getText()));
  expect(nodeNames).toEqual([
    "Process travelers",
    "processedtraveler",
    "skipTraveler",
    "Processed Traveler?",
    "Process Traveler",
    "travelers",
    "Log Traveler",
    "Skip Traveler",
  ]);

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check process name in properties
  const processName = await client.$("input[name$='.diagramSet.name']");
  expect(await processName.getValue()).toEqual("Process travelers");

  // check script node name in properties
  await explorerDiagramButton.click();
  const scriptNode = await client.$("[data-ouia-component-id='tree-item-Log Traveler']");
  await scriptNode.click();
  await propertiesButton.click();
  const nodePropName = await client.$("textarea[name$='.general.name']");
  expect(await nodePropName.getValue()).toEqual("Log Traveler");

  // switch context back
  await client.switchToParentFrame();

  // check editor title name
  const title = await client.$("[data-testid='toolbar-title'] > h3");
  expect(await title.getText()).toEqual("sample.bpmn");

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

test("DMN example", async () => {
  // create new dmn sample
  const bpmnCard = await client.$("[data-ouia-component-id='new-sample-dmn-file-card']");
  await bpmnCard.click();

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
  expect(nodeNames).toEqual([
    "loan_pre_qualification",
    "Applicant Data",
    "Back End Ratio",
    "Context",
    "Credit Score Rating",
    "Decision Table",
    "Credit Score",
    "DTI",
    "Function",
    "Front End Ratio",
    "Context",
    "Lender Acceptable DTI",
    "Function",
    "Lender Acceptable PITI",
    "Function",
    "Loan Pre-Qualification",
    "Decision Table",
    "PITI",
    "Function",
    "Requested Product",
  ]);

  // open properties panel
  const propertiesButton = await client.$("[data-ouia-component-id='docks-item-DiagramEditorPropertiesScreen']");
  await propertiesButton.click();

  // check dmn name in properties
  const dmnName = await client.$("input[name$='.definitions.nameHolder']");
  expect(await dmnName.getValue()).toEqual("loan_pre_qualification");

  // open data types tab
  const dataTypesTab = await client.$("[data-ouia-component-id='Data Types'] > a");
  await dataTypesTab.click();

  // check data types
  const dataTypes = await client.$$(".kie-dnd-draggable:not(.hidden) .name-text");
  const dataTypeNames = await Promise.all(dataTypes.map(async (d) => await d.getText()));
  expect(dataTypeNames).toEqual([
    "Requested_Product",
    "Marital_Status",
    "Applicant_Data",
    "Post-Bureau_Risk_Category",
    "Pre-Bureau_Risk_Category",
    "Eligibility",
    "Strategy",
    "Bureau_Call_Type",
    "Product_Type",
    "Risk_Category",
    "Credit_Score_Rating",
    "Back_End_Ratio",
    "Front_End_Ratio",
    "Qualification",
    "Credit_Score",
    "Loan_Qualification",
  ]);

  // switch context back
  await client.switchToParentFrame();

  // check editor title name
  const title = await client.$("[data-testid='toolbar-title'] > h3");
  expect(await title.getText()).toEqual("sample.dmn");

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

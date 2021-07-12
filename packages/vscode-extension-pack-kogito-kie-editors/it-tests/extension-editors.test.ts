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

import { By, SideBarView, WebView } from "vscode-extension-tester";
import * as path from "path";
import { aComponentWithText, h5ComponentWithText, spanComponentWithText } from "./helpers/CommonLocators";
import { EditorTabs } from "./helpers/EditorTabs";
import { assertWebElementIsDisplayedEnabled, assertWebElementWithAtribute } from "./helpers/CommonAsserts";
import VSCodeTestHelper from "./helpers/VSCodeTestHelper";
import BpmnEditorTestHelper, { PaletteCategories } from "./helpers/BpmnEditorTestHelper";
import ScesimEditorTestHelper from "./helpers/ScesimEditorTestHelper";
import DmnEditorTestHelper from "./helpers/dmn/DmnEditorTestHelper";
import PmmlEditorTestHelper from "./helpers/PmmlEditorTestHelper";
import { assert } from "chai";
import {
  customTaskNameTextArea,
  customTaskDocumentationTextArea,
  palletteItemAnchor,
  assignmentsTextBoxInput,
  processNameInput,
} from "./helpers/BpmnLocators";
import DecisionNavigatorHelper from "./helpers/dmn/DecisionNavigatorHelper";

describe("Editors are loading properly", () => {
  const RESOURCES: string = path.resolve("it-tests-tmp", "resources");
  const DEMO_BPMN: string = "demo.bpmn";
  const DEMO_DMN: string = "demo.dmn";
  const DEMO_SCESIM: string = "demo.scesim";
  const DEMO_PMML: string = "demo.pmml";

  const REUSABLE_DMN: string = "reusable-model.dmn";
  const WID_BPMN: string = "process-wid.bpmn";

  let testHelper: VSCodeTestHelper;
  let webview: WebView;
  let folderView: SideBarView;

  before(async function () {
    this.timeout(60000);
    testHelper = new VSCodeTestHelper();
    folderView = await testHelper.openFolder(RESOURCES);
  });

  beforeEach(async function () {
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
    await webview.switchBack();
  });

  it("Opens demo.bpmn file in BPMN Editor and loads correct diagram", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_BPMN);
    await testHelper.switchWebviewToFrame(webview);
    const bpmnEditorTester = new BpmnEditorTestHelper(webview);

    const palette = await bpmnEditorTester.getPalette();
    await assertWebElementIsDisplayedEnabled(palette);

    await bpmnEditorTester.openDiagramProperties();

    const explorer = await bpmnEditorTester.openDiagramExplorer();
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("demo")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("Start")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("End")));

    await webview.switchBack();
  });

  it("Opens demo.dmn file in DMN Editor", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_DMN);
    await testHelper.switchWebviewToFrame(webview);
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    await dmnEditorTester.openDiagramProperties();
    await dmnEditorTester.openDiagramExplorer();
    await dmnEditorTester.openDecisionNavigator();

    await webview.switchBack();
  });

  it("Include reusable-model in DMN Editor", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_DMN);
    await testHelper.switchWebviewToFrame(webview);
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    await dmnEditorTester.switchEditorTab(EditorTabs.IncludedModels);
    await dmnEditorTester.includeModel(REUSABLE_DMN, "reusable-model");

    // Blocked by https://issues.redhat.com/browse/KOGITO-4261
    // await dmnEditorTester.inspectIncludedModel("reusable-model", 2)

    await dmnEditorTester.switchEditorTab(EditorTabs.Editor);

    await webview.switchBack();
  });

  it("Undo command in DMN Editor", async function () {
    this.timeout(40000);
    webview = await testHelper.openFileFromSidebar(DEMO_DMN);
    await testHelper.switchWebviewToFrame(webview);
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    const decisionNavigator = await dmnEditorTester.openDecisionNavigator();
    await decisionNavigator.selectDiagramNode("?DemoDecision1");

    const diagramProperties = await dmnEditorTester.openDiagramProperties();
    await diagramProperties.changeProperty("Name", "Updated Name 1");

    const navigatorPanel: DecisionNavigatorHelper = await dmnEditorTester.openDecisionNavigator();
    await navigatorPanel.assertDiagramNodeIsPresent("Updated Name 1");
    await navigatorPanel.assertDiagramNodeIsPresent("?DecisionFinal1");

    await webview.switchBack();

    // changeProperty() is implemented as clear() and sendKeys(), that is why we need two undo operations
    await testHelper.executeCommandFromPrompt("Undo");
    await testHelper.executeCommandFromPrompt("Undo");

    await testHelper.switchWebviewToFrame(webview);

    await navigatorPanel.assertDiagramNodeIsPresent("?DemoDecision1");
    await navigatorPanel.assertDiagramNodeIsPresent("?DecisionFinal1");

    await webview.switchBack();
  });

  it("Opens demo.scesim file in SCESIM Editor", async function () {
    this.timeout(20000);

    webview = await testHelper.openFileFromSidebar(DEMO_SCESIM);
    await testHelper.switchWebviewToFrame(webview);
    const scesimEditorTester = new ScesimEditorTestHelper(webview);

    await scesimEditorTester.openScenarioCheatsheet();
    await scesimEditorTester.openSettings();
    await scesimEditorTester.openTestTools();

    await webview.switchBack();
  });

  it("Opens demo.pmml file in PMML Editor", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_PMML);
    await testHelper.switchWebviewToFrame(webview);
    const pmmlEditorTester = new PmmlEditorTestHelper(webview);

    const dataDictionaryModel = await pmmlEditorTester.openDataDictionary();
    dataDictionaryModel.close();

    const miningSchemaModel = await pmmlEditorTester.openMiningSchema();
    miningSchemaModel.close();

    const outputsModal = await pmmlEditorTester.openOutputs();
    outputsModal.close();

    await webview.switchBack();
  });

  it("Opens process with work item definition properly", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(WID_BPMN, "src/main/java/org/kie/businessapp");
    await testHelper.switchWebviewToFrame(webview);
    const bpmnEditorTester = new BpmnEditorTestHelper(webview);

    const customTasksPaletteCategory = await bpmnEditorTester.openDiagramPalette(PaletteCategories.CUSTOM_TASKS);
    assertWebElementIsDisplayedEnabled(await customTasksPaletteCategory.findElement(h5ComponentWithText("Milestone")));
    assertWebElementIsDisplayedEnabled(
      await customTasksPaletteCategory.findElement(h5ComponentWithText("CustomTasks"))
    );
    assertWebElementIsDisplayedEnabled(
      await customTasksPaletteCategory.findElement(palletteItemAnchor("CreateCustomer"))
    );
    assertWebElementIsDisplayedEnabled(await customTasksPaletteCategory.findElement(palletteItemAnchor("Email")));

    const explorer = await bpmnEditorTester.openDiagramExplorer();
    const customTaskAnchor = await explorer.findElement(aComponentWithText("Create Customer Internal Service")); //store to click after checks.
    await assertWebElementIsDisplayedEnabled(customTaskAnchor);
    await assertWebElementIsDisplayedEnabled(
      await explorer.findElement(aComponentWithText("ProcessWithWorkItemDefinition"))
    );
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("Start")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("Email")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("End")));

    await customTaskAnchor.click();
    const propertiesPanel = await bpmnEditorTester.openDiagramProperties();
    const nameInput = await propertiesPanel.findElement(customTaskNameTextArea());
    assertWebElementIsDisplayedEnabled(nameInput);
    assertWebElementWithAtribute(nameInput, "value", "Create Customer Internal Service");

    const docInput = await propertiesPanel.findElement(customTaskDocumentationTextArea());
    assertWebElementIsDisplayedEnabled(docInput);
    assertWebElementWithAtribute(
      docInput,
      "value",
      "Calls internal service that creates the customer in database server."
    );

    const assignmentsTextBox = await propertiesPanel.findElement(assignmentsTextBoxInput());
    assert.isTrue(await assignmentsTextBox.isEnabled());
    assert.equal(await assignmentsTextBox.getAttribute("value"), "7 data inputs, 1 data output");
    assertWebElementWithAtribute(assignmentsTextBox, "value", "7 data inputs, 1 data output");

    await webview.switchBack();
  });

  it("Saves a change of process name in BPMN editor properly", async function () {
    this.timeout(60000);
    webview = await testHelper.openFileFromSidebar("SaveAssetTest.bpmn");
    await testHelper.switchWebviewToFrame(webview);
    let bpmnEditorTester = new BpmnEditorTestHelper(webview);

    let properties = await bpmnEditorTester.openDiagramProperties();
    let processNameInputField = await properties.findElement(processNameInput());
    assert.isTrue(await processNameInputField.isEnabled());
    const formerProcessId = await processNameInputField.getAttribute("value");
    assert.isDefined(formerProcessId);

    await processNameInputField.sendKeys("Renamed");
    await bpmnEditorTester.openDiagramExplorer();

    await webview.switchBack();

    await testHelper.executeCommandFromPrompt("File: Save");
    await testHelper.closeAllEditors();

    webview = await testHelper.openFileFromSidebar("SaveAssetTest.bpmn");
    await testHelper.switchWebviewToFrame(webview);
    bpmnEditorTester = new BpmnEditorTestHelper(webview);
    properties = await bpmnEditorTester.openDiagramProperties();
    processNameInputField = await properties.findElement(processNameInput());
    assert.isTrue(await processNameInputField.isEnabled());
    assert.equal(await processNameInputField.getAttribute("value"), formerProcessId + "Renamed");

    await webview.switchBack();
  });
});

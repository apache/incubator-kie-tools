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
import DmnEditorTestHelper from "./helpers/DmnEditorTestHelper";
import PmmlEditorTestHelper from "./helpers/PmmlEditorTestHelper";
import { assert } from "chai";
import {
  customTaskNameTextArea,
  customTaskDocumentationTextArea,
  palletteItemAnchor,
  assignmentsTextBoxInput,
} from "./helpers/BpmnLocators";

describe("Editors are loading properly", () => {
  const RESOURCES: string = path.resolve("src", "ui-test", "resources");
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
    await testHelper.closeAllEditors();
    folderView = await testHelper.openFolder(RESOURCES);
  });

  afterEach(async function () {
    this.timeout(15000);
    await testHelper.closeAllEditors();
  });

  it("Opens demo.bpmn file in BPMN Editor and loads correct diagram", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_BPMN);
    await webview.switchToFrame();
    const bpmnEditorTester = new BpmnEditorTestHelper(webview);

    const envelopApp = await webview.findWebElement(By.id("envelope-app"));
    await assertWebElementIsDisplayedEnabled(envelopApp);

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
    await webview.switchToFrame();
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    const envelopApp = await webview.findWebElement(By.id("envelope-app"));
    await assertWebElementIsDisplayedEnabled(envelopApp);

    await dmnEditorTester.openDiagramProperties();
    await dmnEditorTester.openDiagramExplorer();
    await dmnEditorTester.openDecisionNavigator();

    await webview.switchBack();
  });

  it("Include reusable-model in DMN Editor", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_DMN);
    await webview.switchToFrame();
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    const envelopApp = await webview.findWebElement(By.id("envelope-app"));
    await assertWebElementIsDisplayedEnabled(envelopApp);

    await dmnEditorTester.switchEditorTab(EditorTabs.IncludedModels);
    await dmnEditorTester.includeModel(REUSABLE_DMN, "reusable-model");

    // Blocked by https://issues.redhat.com/browse/KOGITO-4261
    // await dmnEditorTester.inspectIncludedModel("reusable-model", 2)

    await dmnEditorTester.switchEditorTab(EditorTabs.Editor);

    await webview.switchBack();
  });

  it("Opens demo.scesim file in SCESIM Editor", async function () {
    this.timeout(20000);

    webview = await testHelper.openFileFromSidebar(DEMO_SCESIM);
    await webview.switchToFrame();
    const scesimEditorTester = new ScesimEditorTestHelper(webview);

    const envelopApp = await webview.findWebElement(By.id("envelope-app"));
    await assertWebElementIsDisplayedEnabled(envelopApp);

    await scesimEditorTester.openScenarioCheatsheet();
    await scesimEditorTester.openSettings();
    await scesimEditorTester.openTestTools();

    await webview.switchBack();
  });

  it("Opens demo.pmml file in PMML Editor", async function () {
    this.timeout(20000);
    webview = await testHelper.openFileFromSidebar(DEMO_PMML);
    await webview.switchToFrame();
    const pmmlEditorTester = new PmmlEditorTestHelper(webview);

    const envelopApp = await webview.findWebElement(By.id("envelope-app"));
    await assertWebElementIsDisplayedEnabled(envelopApp);

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
    await webview.switchToFrame();
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
    let customTaskAnchor = await explorer.findElement(aComponentWithText("Create Customer Internal Service")); //store to click after checks.
    await assertWebElementIsDisplayedEnabled(customTaskAnchor);
    await assertWebElementIsDisplayedEnabled(
      await explorer.findElement(aComponentWithText("ProcessWithWorkItemDefinition"))
    );
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("Start")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("Email")));
    await assertWebElementIsDisplayedEnabled(await explorer.findElement(aComponentWithText("End")));

    await customTaskAnchor.click();
    const propertiesPanel = await bpmnEditorTester.openDiagramProperties();
    let nameInput = await propertiesPanel.findElement(customTaskNameTextArea());
    assertWebElementIsDisplayedEnabled(nameInput);
    assertWebElementWithAtribute(nameInput, "value", "Create Customer Internal Service");

    let docInput = await propertiesPanel.findElement(customTaskDocumentationTextArea());
    assertWebElementIsDisplayedEnabled(docInput);
    assertWebElementWithAtribute(
      docInput,
      "value",
      "Calls internal service that creates the customer in database server."
    );

    let assignmentsTextBox = await propertiesPanel.findElement(assignmentsTextBoxInput());
    assert.isTrue(await assignmentsTextBox.isEnabled());
    assert.equal(await assignmentsTextBox.getAttribute("value"), "7 data inputs, 1 data output");
    assertWebElementWithAtribute(assignmentsTextBox, "value", "7 data inputs, 1 data output");

    await webview.switchBack();
  });
});

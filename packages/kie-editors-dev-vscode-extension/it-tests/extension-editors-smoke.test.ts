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

import { SideBarView, WebView } from "vscode-extension-tester";
import * as path from "path";
import { assertWebElementIsDisplayedEnabled, VSCodeTestHelper } from "@kie-tools/vscode-extension-common-test-helpers";
import BpmnEditorTestHelper from "./helpers/bpmn/BpmnEditorTestHelper";
import ScesimEditorTestHelper from "./helpers/ScesimEditorTestHelper";
import DmnEditorTestHelper from "./helpers/dmn/DmnEditorTestHelper";
import PmmlEditorTestHelper from "./helpers/PmmlEditorTestHelper";

/**
 * Smoke tests, ensuring editors can open files.
 * Anything above this level should go to respectives editors test suite.
 */
describe("KIE Editors Integration Test Suite - Smoke tests", () => {
  const RESOURCES: string = path.resolve("it-tests-tmp", "resources");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");
  const DEMO_BPMN: string = "demo.bpmn";
  const DEMO_DMN: string = "demo.dmn";
  const DEMO_SCESIM: string = "demo.scesim";
  const DEMO_PMML: string = "demo.pmml";

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
    await testHelper.takeScreenshotOnTestFailure(this, DIST_IT_TESTS_FOLDER);
    await testHelper.closeAllEditors();
    await testHelper.closeAllNotifications();
    await webview.switchBack();
  });

  it("Opens demo.bpmn file in BPMN Editor and loads correct diagram", async function () {
    this.timeout(20000);
    const editorWebviews = await testHelper.openFileFromSidebar(DEMO_BPMN);
    webview = editorWebviews[0];
    const bpmnEditorTester = new BpmnEditorTestHelper(webview);
    await bpmnEditorTester.switchToEditorFrame();

    const palette = await bpmnEditorTester.getPalette();
    await assertWebElementIsDisplayedEnabled(palette);

    await bpmnEditorTester.openDiagramProperties();

    const explorer = await bpmnEditorTester.openDiagramExplorer();
    await explorer.assertDiagramNodeIsPresent("Start");
    await explorer.assertDiagramNodeIsPresent("End");

    await bpmnEditorTester.switchBack();
  });

  it("Opens demo.dmn file in DMN Editor", async function () {
    this.timeout(20000);
    const editorWebviews = await testHelper.openFileFromSidebar(DEMO_DMN);
    webview = editorWebviews[0];
    const dmnEditorTester = new DmnEditorTestHelper(webview);
    await dmnEditorTester.switchToEditorFrame();

    await dmnEditorTester.openDiagramProperties();
    await dmnEditorTester.openDiagramExplorer();
    await dmnEditorTester.openDecisionNavigator();

    await dmnEditorTester.switchBack();
  });

  it("Opens demo.scesim file in SCESIM Editor", async function () {
    this.timeout(20000);

    const editorWebviews = await testHelper.openFileFromSidebar(DEMO_SCESIM);
    webview = editorWebviews[0];
    const scesimEditorTester = new ScesimEditorTestHelper(webview);
    await scesimEditorTester.switchToEditorFrame();

    await scesimEditorTester.openScenarioCheatsheet();
    await scesimEditorTester.openSettings();
    await scesimEditorTester.openTestTools();

    await scesimEditorTester.switchBack();
  });

  it("Opens demo.pmml file in PMML Editor", async function () {
    this.timeout(20000);
    const editorWebviews = await testHelper.openFileFromSidebar(DEMO_PMML);
    webview = editorWebviews[0];
    const pmmlEditorTester = new PmmlEditorTestHelper(webview);
    await pmmlEditorTester.switchToEditorFrame();

    const dataDictionaryModel = await pmmlEditorTester.openDataDictionary();
    dataDictionaryModel.close();

    const miningSchemaModel = await pmmlEditorTester.openMiningSchema();
    miningSchemaModel.close();

    const outputsModal = await pmmlEditorTester.openOutputs();
    outputsModal.close();

    await pmmlEditorTester.switchBack();
  });
});

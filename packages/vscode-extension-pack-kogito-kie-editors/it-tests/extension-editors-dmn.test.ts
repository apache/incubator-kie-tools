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

require("./extension-editors-smoke.test");

import { SideBarView, WebView } from "vscode-extension-tester";
import * as path from "path";
import { EditorTabs } from "./helpers/dmn/EditorTabs";
import VSCodeTestHelper from "./helpers/VSCodeTestHelper";
import DmnEditorTestHelper from "./helpers/dmn/DmnEditorTestHelper";
import DecisionNavigatorHelper from "./helpers/dmn/DecisionNavigatorHelper";

/**
 * DMN editor vscode integration test suite, add any acceptance tests,
 * freature verificaition, bug reproducers here.
 *
 * For scenarios with other editor consider adding it to a specific
 * file for the integration e.g. "extensions-editors-dmn-bpmn.test.ts"
 */
describe("KIE Editors Integration Test Suite - DMN Editor", () => {
  const RESOURCES: string = path.resolve("it-tests-tmp", "resources");
  const DIST_IT_TESTS_FOLDER: string = path.resolve("dist-it-tests");
  const DEMO_DMN: string = "demo.dmn";
  const DEMO_EXPRESSION_DMN: string = "demo-expression.dmn";
  const REUSABLE_DMN: string = "reusable-model.dmn";

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

  it("Check new DMN Expression Editor", async function () {
    this.timeout(40000);
    webview = await testHelper.openFileFromSidebar(DEMO_EXPRESSION_DMN);
    await testHelper.switchWebviewToFrame(webview);
    const dmnEditorTester = new DmnEditorTestHelper(webview);

    const decisionNavigator = await dmnEditorTester.openDecisionNavigator();

    await decisionNavigator.selectNodeExpression("context demo", "Context");
    const contextEditor = await dmnEditorTester.getExpressionEditor();
    await contextEditor.activateBetaVersion();
    await contextEditor.assertExpressionDetails("context demo", "string");

    await decisionNavigator.selectNodeExpression("function demo", "Function");
    const functionEditor = await dmnEditorTester.getExpressionEditor();
    await functionEditor.activateBetaVersion();
    await functionEditor.assertExpressionDetails("function demo", "string");

    await decisionNavigator.selectNodeExpression("decision table demo", "Decision Table");
    const decisionTableEditor = await dmnEditorTester.getExpressionEditor();
    await decisionTableEditor.activateBetaVersion();
    await decisionTableEditor.assertExpressionDetails("decision table demo", "string");

    await webview.switchBack();
  });
});

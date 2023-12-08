/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import DecisionNavigator from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/dmn/DecisionNavigator";
import DmnEditor from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/dmn/DmnEditor";
import DmnSideBar from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/dmn/DmnSideBar";
import GitHubEditorPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-editor/GitHubEditorPage";
import GitHubListItem from "@kie-tools/chrome-extension-test-helper/dist/framework/github-file-list/GitHubListItem";
import GitHubListPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-file-list/GitHubListPage";
import Tools from "@kie-tools/chrome-extension-test-helper/dist/utils/Tools";
import DmnExpressionEditor from "@kie-tools/chrome-extension-test-helper/dist/framework/editor/dmn/DmnExpressionEditor";

const TEST_NAME = "DmnExpressionEditorTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const WEB_PAGE =
    "https://github.com/apache/incubator-kie-tools/tree/main/packages/chrome-extension-pack-kogito-kie-editors/e2e-tests/samples";
  const FILE_NAME = "test.dmn";
  const BKM_NODE_NAME = "MyModel";

  // check link to online editor in the list
  const gitHubListPage: GitHubListPage = await tools.openPage(GitHubListPage, WEB_PAGE);
  const gitHubFile: GitHubListItem = await gitHubListPage.getFile(FILE_NAME);

  // open DMN editor
  const editorPage: GitHubEditorPage = await gitHubFile.open();
  const dmnEditor: DmnEditor = await editorPage.getDmnEditor();

  await dmnEditor.enter();

  //check DMN nodes in navigator
  const sideBar: DmnSideBar = await dmnEditor.getSideBar();
  const decisionNavigator: DecisionNavigator = await sideBar.openDecisionNavigator();
  await decisionNavigator.selectNodeExpression(BKM_NODE_NAME);

  await dmnEditor.getExpressionEditor();

  await dmnEditor.leave();
});

afterEach(async () => {
  await tools.finishTest();
});

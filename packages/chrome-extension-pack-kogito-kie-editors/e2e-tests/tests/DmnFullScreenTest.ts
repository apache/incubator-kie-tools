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
import FullScreenPage from "@kie-tools/chrome-extension-test-helper/dist/framework/fullscreen-editor/FullScreenPage";
import GitHubEditorPage from "@kie-tools/chrome-extension-test-helper/dist/framework/github-editor/GitHubEditorPage";
import Tools from "@kie-tools/chrome-extension-test-helper/dist/utils/Tools";

const TEST_NAME = "DmnFullScreenTest";

let tools: Tools;

beforeEach(async () => {
  tools = await Tools.init(TEST_NAME);
});

test(TEST_NAME, async () => {
  const dmnUrl: string =
    "https://github.com/apache/incubator-kie-tools/" +
    "blob/main/packages/chrome-extension-pack-kogito-kie-editors/e2e-tests/samples/test.dmn";
  let dmnPage: GitHubEditorPage = await tools.openPage(GitHubEditorPage, dmnUrl);
  // open and check full screen editor
  const fullScreenPage: FullScreenPage = await dmnPage.fullScreen();
  const fullScreenEditor: DmnEditor = await fullScreenPage.getDmnEditor();
  await fullScreenEditor.enter();
  const fullScreenSideBar: DmnSideBar = await fullScreenEditor.getSideBar();
  const fullScreenExplorer: DecisionNavigator = await fullScreenSideBar.openDecisionNavigator();
  expect((await fullScreenExplorer.getNodeNames()).sort()).toEqual(
    ["MyDecision", "MyInputData", "MyModel", "Function"].sort()
  );
  await fullScreenEditor.leave();

  expect(await fullScreenPage.getExitFullScreenUrl()).toBe(dmnUrl + "#");

  await fullScreenPage.scrollToTop();
  dmnPage = await fullScreenPage.exitFullScreen();
  expect(await dmnPage.isEditorVisible()).toBe(true);
  expect(await dmnPage.isSourceVisible()).toBe(false);
});

afterEach(async () => {
  await tools.finishTest();
});
